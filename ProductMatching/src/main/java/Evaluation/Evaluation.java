package Evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class Evaluation {
	ResultItem currentResults;
	BufferedWriter logEvaluation;
	
	public Evaluation(String simType) throws IOException {
		currentResults=new ResultItem();
		logEvaluation= new BufferedWriter(new FileWriter("resources/logEvaluation.txt"));
		logEvaluation.append(simType);
		logEvaluation.newLine();
	}
	
	
	public ResultItem getResultsOptimizingF1(List<EvaluationItem>  allLinks ) throws IOException{
		ResultItem bestResult= new ResultItem();
		
		for(double a=0; a<=1; a=a+0.001){
			currentResults= new ResultItem();
			//double threshold = defineThresholdWitha(allLinks,a);
			double threshold=a;
			if(Double.isNaN(threshold)) {
				System.out.println("The threshold could not be defined. The program will unsuccessfully exit.");
				System.exit(0);
			}
			currentResults.setA(a);
			currentResults.setThreshold(threshold);
			for(EvaluationItem item:allLinks){
				calculateResults(threshold, item);
			}
			logEvaluation.append(a+";"+currentResults.getF1());
			logEvaluation.newLine();
			//System.out.println("THRESHOLD:"+threshold+" F1:"+currentResults.getF1());
			if (currentResults.getF1()>bestResult.getF1()){
				bestResult=currentResults;
			}
			//if f1 is the same take the result with the best precision
			else if (currentResults.getF1()==bestResult.getF1()){
				if(bestResult.getPrecision()<currentResults.getPrecision()){
					bestResult=currentResults;
				}
			}
		}
		logEvaluation.flush();
		logEvaluation.close();
		return bestResult;
	}

 	private double defineThresholdWitha(List<EvaluationItem> allLinks,
			double a) {
 		//calculate the mean(average)
 		double sum=0;
 		int numberOfAllLinks=0;
 		for(EvaluationItem item:allLinks){
 			for(Map.Entry<String, Double> entry:item.getPredictedAnswers().entrySet()){
 				
 				sum+=entry.getValue();
 				numberOfAllLinks++;
 			}
 		}
		double mean= sum/(double)numberOfAllLinks;
		//calculate the standard deviation
		double sdev = 0;
		for(EvaluationItem item:allLinks){
			for(Map.Entry<String, Double> entry:item.getPredictedAnswers().entrySet()){
				sdev+= Math.pow(entry.getValue() - mean, 2);
			}
		}
		
		sdev=sdev/(double)numberOfAllLinks;

		//define threshold
		double threshold= mean+a*sdev;
		
		return threshold;
	}
	
 	public ResultItem getResultsWithMedianThreshold(List<EvaluationItem>  allLinks) throws IOException{
		
 		double threshold=defineThresholdasMedian(allLinks);
 		currentResults.setThreshold(threshold);
 		for(EvaluationItem item:allLinks){			
 			calculateResults(threshold, item);
 		}	
		return currentResults;
	}

 	public ResultItem getResultsWithAverageThreshold(List<EvaluationItem>  allLinks) throws IOException{
		
 		double threshold=defineThresholdAsAverage(allLinks);
 		currentResults.setThreshold(threshold);

 		for(EvaluationItem item:allLinks){			
 			calculateResults(threshold, item);
 		}	
		return currentResults;
	}

	private double defineThresholdasMedian(List<EvaluationItem> allLinks) {
				
		double [] similarityScores = new double[allLinks.size()*allLinks.get(0).getPredictedAnswers().size()];
		int i=0;
		for (EvaluationItem item:allLinks){
			for(Map.Entry<String, Double> entry:item.getPredictedAnswers().entrySet()){
				similarityScores[i]=entry.getValue();
				i++;
			}
		}
		
		
		Arrays.sort(similarityScores);
		double median;
		if (similarityScores.length % 2 == 0)
		    median = ((double)similarityScores[similarityScores.length/2] + (double)similarityScores[similarityScores.length/2 - 1])/2;
		else
		    median = (double) similarityScores[similarityScores.length/2];
		
		return 1.5*median;

	}

	private double defineThresholdAsAverage(List<EvaluationItem> allLinks){
		
		double sum=0;
 		int numberOfAllLinks=0;
 		for(EvaluationItem item:allLinks){
 			for(Map.Entry<String, Double> entry:item.getPredictedAnswers().entrySet()){
 				sum+=entry.getValue();
 				numberOfAllLinks++;
 			}
 		}
		
		double mean= sum/(double)numberOfAllLinks;
		
		return mean;
	}
	
	private void calculateResults(double threshold,EvaluationItem item) throws IOException{
		
		ArrayList<String> positives = new ArrayList<String>();
		ArrayList<String> negatives = new ArrayList<String>();
		
		for(Map.Entry<String, Double> entry:item.getPredictedAnswers().entrySet()){
			if(entry.getValue()>threshold) positives.add(entry.getKey());
			else negatives.add(entry.getKey());
		}
	
		List<String> truePositives = new ArrayList<String>(positives);
		truePositives.retainAll(item.getRightAnswers());
		List<String> falsePositives = new ArrayList<String> (positives);
		falsePositives.removeAll(item.getRightAnswers());
		List<String> trueNegatives = new ArrayList<String>(negatives);
		trueNegatives.removeAll(item.getRightAnswers());
		List<String> falseNegatives = new ArrayList<String>(negatives);
		falseNegatives.retainAll(item.getRightAnswers());
		
		
		currentResults.setTruePositives(currentResults.getTruePositives()+truePositives.size());
		currentResults.setFalsePositives(currentResults.getFalsePositives()+falsePositives.size());
		currentResults.setTrueNegatives(currentResults.getTrueNegatives()+trueNegatives.size());
		currentResults.setFalseNegatives(currentResults.getFalseNegatives()+falseNegatives.size());
		
		if (null==currentResults.getFalsePositivesValues()) currentResults.setFalsePositivesValues(new ArrayList<String>());
		if (null==currentResults.getFalseNegativesValues()) currentResults.setFalseNegativesValues(new ArrayList<String>());
		if (null==currentResults.getTruePositivesValues()) currentResults.setTruePositivesValues(new ArrayList<String>());
		if (null==currentResults.getTrueNegativesValues()) currentResults.setTrueNegativesValues(new ArrayList<String>());

		if (falsePositives.size()!=0)
			currentResults.getFalsePositivesValues().add(item.getPath()+"-"+item.getRightAnswers().toString()+";"+falsePositives.toString());
		if (falseNegatives.size()!=0)
			currentResults.getFalseNegativesValues().add(item.getPath()+"-"+item.getRightAnswers().toString()+";"+falseNegatives.toString());
		if(truePositives.size()!=0)		
			currentResults.getTruePositivesValues().add(item.getPath()+"-"+item.getRightAnswers().toString()+";"+truePositives.toString());
		if(trueNegatives.size()!=0)		
			currentResults.getTrueNegativesValues().add(item.getPath()+"-"+item.getRightAnswers().toString()+";"+trueNegatives.toString());

	}
}
