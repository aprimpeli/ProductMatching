package RapidMinerUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import CombinationUtils.CommonFeatures;

public class FileTransformationForRapidminer {
	
	static String productCat="phone;headphone;tv";
	static String input="marked_up_data"; //all_html, html_tables, html_lists, html_tables_lists, marked_up_data, html_tables_lists_wrapper
	
	static String fileScoresBow="resources/Rapidminer/bow_error_analysis_scores_"+productCat+"_"+input+"cosine.txt";
	static String fileCommonWords="resources/Rapidminer/bow_"+productCat+"_"+input+"_error_analysis.csv";
	
	public static void main (String args[]) throws IOException{
//		String fileBow="resources/Rapidminer/bow_error_analysis_scores_headphone_html_tables_listscosine.txt";
//		String fileDict="resources/Rapidminer/dict_error_analysis_scores_headphone_html_tables_listsexact.txt";
		
//		transformForLearningMethodWeights(fileBow,"bow");
//		transformForLearningMethodWeights(fileDict,"dictionary");
		
		

		
		transformForLearningFeatures();
		
	}
	
	/**
	 * @param file
	 * takes the log file with the nodes and the similarity scores for every predicted answer 
	 * and transforms to a file for learning the weights using Rapidminer
	 * @throws IOException 
	 */
	public static void transformForLearningMethodWeights(String file, String methodName) throws IOException{ 

		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));;
		BufferedWriter writer = new BufferedWriter (new FileWriter(new File("resources/Rapidminer/"+methodName+".csv")));
		
	    String line;
	    String node="";
	    String right="";
	    String predicted="";
	    int match;
	    Double simScore;
	    while ((line = reader.readLine()) != null) {
	        if(line.startsWith("node")||line.startsWith("NEW")) node=line.trim();
	        if(line.startsWith("Right")) right=line.split(":")[1].trim();
	        if(line.startsWith("Predicted")){
	        	predicted=line.split(":")[1].split("---")[0].trim();
	        	simScore=  Double.parseDouble(line.split(":")[1].split("---")[1].trim());
	        	if(predicted.equals(right)) match=1;
	        	else match=0;
	        	writer.append(node+"-"+predicted+";"+new DecimalFormat("##.#####").format(simScore)+";"+match);
	        	writer.newLine();
	        	writer.flush();
	        }
	   }
	   reader.close();
	   writer.close();
			
	}

	public static HashMap<String,String> getRightAnswers(String file) throws IOException{
		
		HashMap <String,String> answers = new HashMap<String,String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));;
		
	    String line;
	    String node="";
	    String right="";
	    while ((line = reader.readLine()) != null) {
	        if(line.startsWith("node")) node=line.trim();
	        if(line.startsWith("Right")) right=line.split(":")[1].trim();
	        answers.put(node, right);
	   }
	   reader.close();
	   return answers;
	}
	
	public static void transformForLearningFeatures() throws IOException{
		
		ArrayList<CommonFeatures> allPairs = new ArrayList<CommonFeatures>();
		HashMap<String,String> answers= new HashMap<String,String>();				
		Set<String> allUniqueWords= new HashSet<String>();
		
		String categories[]= productCat.split(";");
		for(int cat=0;cat<categories.length;cat++){
			
	
			if(categories.length>1){		
				fileScoresBow="resources/Rapidminer/bow_error_analysis_scores_"+categories[cat]+"_"+input+"cosine.txt";
				fileCommonWords="resources/Rapidminer/bow_"+categories[cat]+"_"+input+"_error_analysis.csv";

			}
			answers.putAll(getRightAnswers(fileScoresBow));
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileCommonWords)));
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
			    		allUniqueWords.add(commonWords[c].trim());
			    	}
				    	
			    	CommonFeatures pair= new CommonFeatures(node, predicted, matches, common);
			    	allPairs.add(pair);
			    }
			    if (line.startsWith("THRESHOLD")) break;
			    else continue;
			   }
				reader.close();
		}
		
	   
		
		//write the csv
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("resources/Rapidminer/outputForFeatureWeights"+productCat+"_"+input+".csv")));
		String headerLine = "pair_id;matches";
		String allAttributes="";
		for (String word:allUniqueWords)
			allAttributes=allAttributes+";"+word;
		writer.append(headerLine+allAttributes);
		writer.newLine();
		
		String[] sepAttributes= allAttributes.split(";");

		for (CommonFeatures p:allPairs){
			String entityLine=p.getNode()+"-"+p.getPredicted()+";"+p.getMatches();
			String attributeLine="";
			for(int at=1; at<sepAttributes.length;at++){
				if(p.getCommonWords().contains(sepAttributes[at]))
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
	
	
}
