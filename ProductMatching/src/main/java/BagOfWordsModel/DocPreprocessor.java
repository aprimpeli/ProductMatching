package BagOfWordsModel;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;

import UnitConversion.ColumnTypeGuesser;
import UnitConversion.SubUnit;
import UnitConversion.ColumnTypeGuesser.ColumnDataType;
import Utils.HTMLFragmentsExtractor;
import Utils.LabelledFileExtractions;
import Utils.NodeFromLabelled;


public class DocPreprocessor {
	
	Pattern unitConversionPattern;
	
	public DocPreprocessor(){
		unitConversionPattern= Pattern.compile("(?:\\.*+\\d)++ *+[^ \\d]++");

	}
	
	/**
	 * @throws IOException
	 * Checks if it is a HTML page and removes the html tags to get the text
	 * Uses Lucene library for removal of stopwords, tokenization, stemming and normalization to lower case
	 * Returns the list of the preprocessed words
	 */
	public List<String> textProcessing (String filepath, String text, int grams, boolean isHTML,  PreprocessingConfiguration preprocessing, String labelledPath) throws IOException{
		
		String extractedText=getText(isHTML, text, filepath, preprocessing, labelledPath);
		if(null==extractedText) return null;
		Reader corpus= StringToReaderConverter(extractedText);
		
		List<String> processedWords = new ArrayList<String>();
		TokenStream result=null;
		
		final Tokenizer source = new StandardTokenizer(Version.LUCENE_36, corpus);
		result = new StandardFilter(Version.LUCENE_36, source);
					
		if (preprocessing.isLowerCase())
			result = new LowerCaseFilter(Version.LUCENE_36, result);
		if (preprocessing.isStopWordRemoval())
		     result = new StopFilter(Version.LUCENE_36, result, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		if (preprocessing.isStemming())
		     result = new PorterStemFilter(result);
		
		
		while(result.incrementToken()){
			String token=((CharTermAttribute)result.getAttribute(CharTermAttribute.class)).toString();		
			processedWords.add(token);	
		}	

		if(grams>1){
			StringBuilder buildText = new StringBuilder();
			for (String word:processedWords){
				buildText.append(word+" ");
			}
			
			processedWords.clear();
			corpus.reset();
			corpus = StringToReaderConverter(buildText.toString());
			result.reset();
			result = new StandardTokenizer(Version.LUCENE_36, corpus);
			result = new ShingleFilter(result, grams,grams);	
			while(result.incrementToken()){
				String token=((CharTermAttribute)result.getAttribute(CharTermAttribute.class)).toString();
				token.trim();
				if(!token.contains(" ")) continue;
				processedWords.add(token);	
			}
		}
		

		return processedWords;
    }
	
	public String getText(boolean isHTML, String text, String filepath,
			PreprocessingConfiguration preprocessing, String labelledpath) throws IOException {

		if (null==text && null!=filepath)
			text = fileToText(filepath);
		
		if (isHTML) {
			if(preprocessing.getHtmlParsingType().equals("all_html"))
				text= Jsoup.parse(text).text();
			else if(preprocessing.getHtmlParsingType().equals("html_tables")){
				HTMLFragmentsExtractor utils = new HTMLFragmentsExtractor(labelledpath);
				if(!preprocessing.isTablesFiltering()){
					text=utils.getTableText(filepath);
				}
				else{
					text=utils.getFilteredTableText(filepath);
				}
			}
			else if(preprocessing.getHtmlParsingType().equals("html_lists")){
				HTMLFragmentsExtractor utils = new HTMLFragmentsExtractor(labelledpath);
				if(!preprocessing.isTablesFiltering()){
					text=utils.getListText(filepath);
				}
				else{
					text=utils.getFilteredListText(filepath);
				}				
			}
			
			else if(preprocessing.getHtmlParsingType().equals("html_tables_lists")){
				HTMLFragmentsExtractor utils = new HTMLFragmentsExtractor(labelledpath);
				StringBuilder allContent = new StringBuilder();
				String tabletext="";
				String listText="";

				if(!preprocessing.isTablesFiltering()){
					listText=utils.getListText(filepath);
					tabletext=utils.getTableText(filepath);
					allContent.append(listText).append(tabletext);

				}
				else{
					String TablesListstext=utils.getTablesListsContentFromWrappers(filepath,labelledpath);
					allContent.append(TablesListstext);

				}
				
				text = allContent.toString();
			}
			
			else if (preprocessing.getHtmlParsingType().equals("marked_up_data")){
				 NodeFromLabelled node =LabelledFileExtractions.extractNodeFromLabelledFile(filepath, labelledpath);
				 StringBuilder allContent = new StringBuilder();

				 allContent.append(node.getTitle());
				 allContent.append(node.getDescription());
				 text = allContent.toString();
			}
		}
		
		if (preprocessing.isUnitConversion()) return handleNumericalValues(text,isHTML);
		else return text;
	}
	
	private String handleNumericalValues(String text,boolean isHTML) {
		
		text=text.toLowerCase().replaceAll("[()]","");
		
		Matcher extractedValues= unitConversionPattern.matcher(text);
		List<String> valuesToBeReplaced = new ArrayList<String>();
		List<String> newValues = new ArrayList<String>();
		ColumnTypeGuesser g = new ColumnTypeGuesser();

		while(extractedValues.find()){
			String tobeConverted=extractedValues.group(0);
			
			//take out the 2g, 3g, 4g so that they are not mixed with weights
			if (tobeConverted.replaceAll(" ", "").equals("2g") || tobeConverted.replaceAll(" ", "").equals("3g") 
					|| tobeConverted.replaceAll(" ", "").equals("4g"))	continue;
							       
	        SubUnit subUnit = new SubUnit();
	        
	        if(!g.guessTypeForValue(tobeConverted, null, true, subUnit).equals(ColumnDataType.unit)) continue;
	        	        
	        String normalizedValue=subUnit.getNewValue();
	        valuesToBeReplaced.add(tobeConverted);
	        
	        newValues.add(normalizedValue+subUnit.getBaseUnit().getMainUnit().getName());
												
		}
		
		for (int i=0;i<valuesToBeReplaced.size();i++){
			if(isHTML)
				text = text.replace(" "+valuesToBeReplaced.get(i)+" "," "+newValues.get(i)+" ");
			else 
				text = text.replace(valuesToBeReplaced.get(i)," "+newValues.get(i)+" ");
		}
				
		return text;
		
	}
	public Reader StringToReaderConverter(String text){
		
		StringReader wordReader = new StringReader(text);
		return wordReader;

	}
	
	
	/**
	 * @param filepath
	 * @return
	 * @throws IOException
	 * Gets the path of a file and transforms it to a string variableS
	 */
	public static String fileToText (String filepath) throws IOException{
		File f = new File(filepath);
		if (!f.exists()) {
		    System.out.println("The file could not be found: "+filepath);
		    System.exit(0);
		}
		byte[] encoded = Files.readAllBytes(Paths.get(filepath));
		  return new String(encoded, StandardCharsets.UTF_8);	
	}
	
	public void printList (List<String> list){
		System.out.println("SIZE of word list:"+list.size());
		for (String l:list) System.out.println(l);
	}
	
	public int getGramsOfValue(String value, PreprocessingConfiguration preprocessing) throws IOException {
		
		Reader corpus= StringToReaderConverter(value);
		
		List<String> processedWords = new ArrayList<String>();
		TokenStream result=null;
		
		final Tokenizer source = new StandardTokenizer(Version.LUCENE_36, corpus);
		result = new StandardFilter(Version.LUCENE_36, source);
					
		if (preprocessing.isLowerCase())
			result = new LowerCaseFilter(Version.LUCENE_36, result);
		if (preprocessing.isStopWordRemoval())
		     result = new StopFilter(Version.LUCENE_36, result, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		if (preprocessing.isStemming())
		     result = new PorterStemFilter(result);
		
		
		while(result.incrementToken()){
			String token=((CharTermAttribute)result.getAttribute(CharTermAttribute.class)).toString();		
			processedWords.add(token);	
		}	
		result.close();
		return processedWords.size();
	}
	
		
	
}
