package wdc.Thesis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONException;
import org.junit.Test;

import BagOfWordsModel.DocPreprocessor;
import BagOfWordsModel.ModelConfiguration;
import BagOfWordsModel.PreprocessingConfiguration;
import BagOfWordsModel.SimilarityCalculator;
import DictionaryApproach.Dictionary;
import DictionaryApproach.DictionaryCreator;


public class TestDictionaryApproach {
	static String labelled="";
	static double idfThresholdForcatalog=0.0;
	static int windowSize = 2;
	//PREPROCESSING
	static boolean stemming=false;
	static boolean stopWordRemoval=false;
	static boolean lowerCase=true;
	static boolean numericalHandling=true;
	static boolean tablesFiltering=true;
	static boolean modelNameHandling=true;
	static String htmlParsingElements="html_tables"; //all_html, html_tables, html_lists, html_tables_lists, marked_up_data, html_tables_lists_wrapper
	Map<Integer, List<String>> tokenizedInput;
	
	@Test
	public void runTest() throws JSONException, IOException{
		String catalog="";
		//String text="this is i phone6 in gold color it s a smartphone with 5Megapixel camera";
		String text = "ireland";
		DictionaryCreator dict = new DictionaryCreator();
		Dictionary dictionary = new Dictionary();
		PreprocessingConfiguration preprocessing = new PreprocessingConfiguration(stemming, stopWordRemoval, lowerCase, htmlParsingElements,numericalHandling,tablesFiltering,modelNameHandling);

		dictionary=dict.createDictionary(catalog, "phone",preprocessing, labelled, idfThresholdForcatalog,true);		
		
		tokenizedInput = new HashMap<Integer, List<String>> ();
		DocPreprocessor process= new DocPreprocessor();
		for (int i=1; i<10; i++){
			List<String> grams = process.textProcessing(null, text, i, false, preprocessing, labelled);
			tokenizedInput.put(i, grams);
		}
		
		DocPreprocessor processdoc = new DocPreprocessor();
		for(Map.Entry<String, Set<String>> dictEntry:dictionary.getDictionary().entrySet()){
			for(final String value:dictEntry.getValue()){
				
				if(value.length()<3) continue;
				
				int gramsOfValue=processdoc.getGramsOfValue(value, preprocessing);
				if (!tokenizedInput.containsKey(gramsOfValue+windowSize)){
					System.out.println("The input wont be tokenized for "+gramsOfValue+windowSize+" grams. That's too much and wont make sense. Next value.");
					continue;
				}
				HashMap<String, Double> initialCandidates = getTopCandidates(value, gramsOfValue+windowSize, preprocessing, 
						 0.6 , null, text, labelled, false, 3);
				
						
				HashMap<String, Double> finalCandidates = new HashMap<String, Double>();
		       //for the top candidates try to reduce the window size till you get the best scores meet the thresold
		        for (Entry<String, Double> candidate : initialCandidates.entrySet())
		        {
		        	String maxCandidate="";
		        	double maxScore=0.0;
		            for(int i=gramsOfValue+windowSize; i>=1; i--){ //reduce window size
		            	HashMap<String, Double> tempCandidates=getTopCandidates(value, i, preprocessing,  0.8, null, candidate.getKey(), labelled, false, 3);
		            	for (Entry<String,Double> c:tempCandidates.entrySet()){
		            		if (c.getValue()>maxScore) {
		            			maxScore=c.getValue();
		            			maxCandidate=c.getKey();
		            		}
		            		
		            	}
		            }
		            if (maxScore>0.4) finalCandidates.put(maxCandidate,maxScore);
	            	

		        }
			       
				for(Entry<String, Double> candidate : finalCandidates.entrySet())  {
					System.out.println(candidate+" is similar to the value:"+value+" and will be added the feature: "+dictEntry.getKey());
				}  
				
			}
		}
		

		
	}
	public HashMap<String,Double> getTopCandidates(final String valueToCompare, int gramsToTokenize, PreprocessingConfiguration preprocessing,  double simThreshold, String htmlPath, 
			String text, String labelledPath, boolean isHTML, int pruneLength) throws IOException{
		
		DocPreprocessor process = new DocPreprocessor();
		List<String> gramsOfCorpus = new ArrayList<String>();
		
		if(isHTML) gramsOfCorpus=tokenizedInput.get(gramsToTokenize); //precalculated tokenization no need to compute it every time for the main input
		else gramsOfCorpus = process.textProcessing(htmlPath, text, gramsToTokenize, isHTML, preprocessing, labelledPath);
    	
		Set<String> uniqueGrams = new HashSet<String>(gramsOfCorpus);
    	
    	ModelConfiguration model = new ModelConfiguration();
    	model.setOnTopLevenshtein(true);
    	HashMap<String,Double> topCandidates = new HashMap<String,Double>();
    	
    	List<String> gramsOfValue = process.textProcessing(null, valueToCompare, 1, false, preprocessing, labelledPath);
    	for(final String unique:uniqueGrams){
    		if (unique.length()<pruneLength) continue;
    		List<String> gramsOfUnique = process.textProcessing(null, unique, 1, false, preprocessing, labelledPath);
			double score = SimilarityCalculator.getMongeElkanSimilarity(gramsOfUnique, gramsOfValue, "jaroWrinkler");
			if(score>simThreshold) topCandidates.put(unique, score);
    	}
    	return topCandidates;
	}
}
