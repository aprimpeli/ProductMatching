package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Evaluation.ResultItem;

public class ErrorAnalysisLog {

	private HashMap<String, String> commonWords;
	private double threshold;
	public HashMap<String, String> getCommonWords() {
		return commonWords;
	}
	public void setCommonWords(HashMap<String, String> commonWords) {
		this.commonWords = commonWords;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public ArrayList<String> getFalsePositives() {
		return falsePositives;
	}
	public void setFalsePositives(ArrayList<String> falsePositives) {
		this.falsePositives = falsePositives;
	}
	public ArrayList<String> getFalseNegatives() {
		return falseNegatives;
	}
	public void setFalseNegatives(ArrayList<String> falseNegatives) {
		this.falseNegatives = falseNegatives;
	}
	public String getLogFile() {
		return logFile;
	}
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
	
	public void  printlogErrorAnalysis(String errorLogFile, ResultItem results) throws IOException{
		
		setLogFile(errorLogFile);
				
		BufferedWriter logger = new BufferedWriter(new FileWriter(new File(this.logFile)));
		logger.append("COMMON WORDS");
		logger.newLine();
		for(Map.Entry<String, String> common: this.commonWords.entrySet()){
			logger.append(common.getKey()+";"+common.getValue().toString());
			logger.newLine();
		}
		logger.append("THRESHOLD:"+results.getThreshold());
		logger.newLine();
		logger.append("FALSE NEGATIVES"+results.getFalseNegatives());
		logger.newLine();
		for (String fn:results.getFalseNegativesValues()){
			if (fn.equals("") || fn.equals(null)) continue;
			logger.append(fn);
			logger.newLine();
		}
		logger.append("FALSE POSITIVES"+results.getFalsePositives());
		logger.newLine();
		for (String fp:results.getFalsePositivesValues()){
			logger.append(fp);
			logger.newLine();
		}
		logger.close();
		
	}
	
	private ArrayList<String> falsePositives;
	private ArrayList<String> falseNegatives;
	private String logFile;
	
	
}
