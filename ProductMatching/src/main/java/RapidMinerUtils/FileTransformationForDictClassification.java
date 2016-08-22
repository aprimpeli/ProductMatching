package RapidMinerUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import BagOfWordsModel.PreprocessingConfiguration;
import CombinationUtils.CommonFeatures;
import DictionaryApproach.Dictionary;
import DictionaryApproach.DictionaryCreator;
import DictionaryApproach.ProductEntity;

public class FileTransformationForDictClassification {

	static String htmlParsingElements="marked_up_data";//all_html, html_tables, html_lists, html_tables_lists, marked_up_data, html_tables_lists_wrapper
	static String productCategory="tv";
	static String trainingOrTest="TESTING";
	static String commonWordsfile="resources/Rapidminer/dict_"+productCategory+"_"+htmlParsingElements+"_error_analysis.csv";
	static String scoresFiles="resources/Rapidminer/dict_error_analysis_scores_"+productCategory+"_"+htmlParsingElements+"exact.txt";
	
	static String dataPath="resources/2.ProfilingOfData/LabelledDataProfiling/";	
	static String catalog=dataPath+"ProductCatalog/"+productCategory+"Catalog.json";
	static String labelled=dataPath+"/CorrectedLabelledEntities/UnifiedGoldStandard_extra/"+productCategory+"s.txt";
	static double idfThresholdForcatalog=0.8;
	
	public static void main (String args[]) throws IOException{
		
		transformForLearningFeatures(commonWordsfile, scoresFiles);
	}
	public static void transformForLearningFeatures(String commonWordsfile, String scoresFiles) throws IOException{
		
		ArrayList<CommonFeatures> allPairs = new ArrayList<CommonFeatures>();
		HashMap<String,String> answers= FileTransformationForRapidminer.getRightAnswers(scoresFiles);
		BufferedReader reader = new BufferedReader(new FileReader(new File(commonWordsfile)));;
	    String line;
	    String node="";
	    String predicted="";
	    int matches;
	    while ((line = reader.readLine()) != null) {
	        if(line.startsWith("node")) {
	        	String lineparts[]=line.split(";");
	        	node=lineparts[0].trim();
	        	predicted=lineparts[1].trim();
	        	if(answers.get(node).toLowerCase().equals(predicted.toLowerCase())) matches=1;
	        	else matches=0;
	        	
	        	String [] commonWords = lineparts[2].trim().replace("[", "").replace("]", "").split(",");
	        	HashSet<String> common=new HashSet<String>();

	        	for (int c=0;c<commonWords.length;c++){
	        		common.add(commonWords[c].trim());
	        	}
	    	    	
	        	CommonFeatures pair= new CommonFeatures(node, predicted, matches, common);
	        	allPairs.add(pair);
	        }
	        if (line.startsWith("THRESHOLD")) break;
	        else continue;
	   }
		reader.close();
		
		Dictionary dictionary = new Dictionary();
		DictionaryCreator creator= new DictionaryCreator();
		PreprocessingConfiguration preprocessing = new PreprocessingConfiguration(true, true, true, htmlParsingElements,false,false,false);
		dictionary=creator.createDictionary(catalog, productCategory,preprocessing, labelled, idfThresholdForcatalog, false);
		HashSet<String> allUniqueFeatures= getUniqueFeatures(dictionary);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("resources/Rapidminer/outputForFeatureWeightsDICT_"+productCategory+htmlParsingElements+"_"+trainingOrTest+".csv")));
		String headerLine = "pair;matches";
		String allAttributes="";
		for (String word:allUniqueFeatures)
			allAttributes=allAttributes+";"+word;
		writer.append(headerLine+allAttributes);
		writer.newLine();
		
		String[] sepAttributes= allAttributes.split(";");

		for (CommonFeatures p:allPairs){
			String entityLine=p.getNode()+"-"+p.getPredicted()+";"+p.getMatches();
			String attributeLine="";
			for(int at=1; at<sepAttributes.length;at++){
				if(isItValueOfEntity(p.getPredicted(), sepAttributes[at], p.getCommonWords(), dictionary))
					attributeLine=attributeLine+";"+"1";
				else
					attributeLine=attributeLine+";"+"0";

			}
			
			writer.append(entityLine+attributeLine);
			writer.newLine();
			writer.flush();
		}
		writer.close();
	

	}

	private static boolean isItValueOfEntity(String predicted, String feature,
			HashSet<String> commonWords, Dictionary dictionary) {
		
		for(ProductEntity pr:dictionary.getProductEntities()){
			if(pr.getName().toLowerCase().trim().equals(predicted.trim().toLowerCase())) {
				
				HashMap<String, ArrayList<String>> lowercased =lowercase(pr.getFeatureValues());
				for(String w:lowercased.get(feature.toLowerCase())){
					if(commonWords.contains(w)) return true;
				}
			}
		}
		
		return false;
	}

	private static HashMap<String, ArrayList<String>> lowercase(
			HashMap<String, ArrayList<String>> featureValues) {
		
		HashMap<String, ArrayList<String>> lowercased= new HashMap<String, ArrayList<String>>();
		
		for(Map.Entry<String, ArrayList<String>> e:featureValues.entrySet()){
			ArrayList<String> lowerValues= new ArrayList<String>();
			for(String s:e.getValue()) lowerValues.add(s);
			lowercased.put(e.getKey().toLowerCase(), lowerValues);
		}
			
		
		return lowercased;
	}
	private static HashSet<String> getUniqueFeatures(Dictionary dictionary) {

		HashSet<String> uniqueFeatures= new HashSet<String>();
		
		for(ProductEntity pr:dictionary.getProductEntities()){
			
			for(Map.Entry<String, ArrayList<String>> f:pr.getFeatureValues().entrySet())
				uniqueFeatures.add(f.getKey());
		}
		return uniqueFeatures;
	}
	

}
