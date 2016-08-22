package DictionaryApproach;

import java.io.IOException;
import java.io.Reader;



import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import BagOfWordsModel.DocPreprocessor;
import BagOfWordsModel.PreprocessingConfiguration;

public class InputPreprocessor {

	public String textProcessing(String filepath,  String text, boolean isHTML, PreprocessingConfiguration preprocessing, String labelledPath) throws IOException{
		DocPreprocessor process= new DocPreprocessor();
		String extractedText=process.getText(isHTML, text, filepath, preprocessing, labelledPath);
		String processedText="";
		
		if(null==extractedText) return null;
		Reader corpus= process.StringToReaderConverter(extractedText);

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

			processedText+=token+" ";	
		}	
		result.close();
		return processedText.trim();
	}
}
