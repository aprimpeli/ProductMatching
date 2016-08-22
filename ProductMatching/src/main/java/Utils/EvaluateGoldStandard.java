package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class EvaluateGoldStandard {

	static String scoresFile="resources/labelingEvaluation/error_analysis_scores_tv_simple.txt";
	static String negativesFile="resources/labelingEvaluation/negatives.txt";
	
	public static void main (String args[]){
		EvaluateGoldStandard gs = new EvaluateGoldStandard();
		ArrayList<String> negatives = gs.loadNegatives();
		LinkedHashMap<String,Double> answerScores = gs.getRightAnswerWithMaxScore(negatives);
		for(Map.Entry<String, Double> a: answerScores.entrySet()){
			System.out.println(a.getKey()+"--"+a.getValue());
		}
			
		
	}
	
	private LinkedHashMap<String,Double> getRightAnswerWithMaxScore(ArrayList<String> negatives) {
		
		BufferedReader reader = null;
		LinkedHashMap<String,Double> answers= new LinkedHashMap<String,Double>();
		try {
		    File file = new File(scoresFile);
		    reader = new BufferedReader(new FileReader(file));

		    String line;
		    String answer=null;
		    double maxScore=-1;
		    String nodeID=null;
		    while ((line = reader.readLine()) != null) {
		    	if(line.startsWith("node")){
		    		if(null!=nodeID){
		    			if(negatives.contains(answer))
			    			answers.put(nodeID, maxScore);
		    		}
		    		nodeID=line.trim();
		    		answer=null;
		    		maxScore=-1;
		    		
		    	}
		        if(line.startsWith("Right Answer:")){	        	
		        	answer=line.split(":")[1].trim();
		        }
		        
		        if(line.startsWith("Predicted Answer:")){
		        	String score=line.split("---")[1].trim();
		        	double scoreValue=Double.parseDouble(score);
		        	if(scoreValue>maxScore) maxScore=scoreValue;
		        }
		        
		    }
		    reader.close();
		} catch (Exception e){
			
		}
		return answers;
	
	}

	public ArrayList<String> loadNegatives(){
		
		BufferedReader reader = null;
		ArrayList<String> negatives = new ArrayList<String>();
		try {
		    File file = new File(negativesFile);
		    reader = new BufferedReader(new FileReader(file));

		    String line;
		    while ((line = reader.readLine()) != null) {
		        negatives.add(line.trim());
		    }
		    reader.close();
		} catch (Exception e){
			
		}
		return negatives;
	}
}
