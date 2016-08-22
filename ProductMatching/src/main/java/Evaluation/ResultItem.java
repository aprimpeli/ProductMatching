package Evaluation;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultItem {

	private int truePositives;
	private int trueNegatives;
	private int falsePositives;
	private int falseNegatives;
	private ArrayList<String> truePositivesValues;
	private HashMap<String, Integer> truePositivesCounts;
	
	public HashMap<String, Integer> getUniqueCounts(ArrayList<String> list){
		HashMap<String, Integer> map = new HashMap<String,Integer>();
		for(String l:list){
			String s=l.split(";")[1];
			Integer counter=map.get(s);
			if(null==counter) counter=0;
			map.put(s, ++counter);
		}
		return map;
	}
	
	public HashMap<String, Integer> getTruePositivesCounts() {		
		return getUniqueCounts(this.truePositivesValues);
	}
	
	public HashMap<String, Integer> getTrueNegativesCounts() {
		
		return getUniqueCounts(this.trueNegativesValues);
	}

	public HashMap<String, Integer> getFalsePositivesCounts() {
		return getUniqueCounts(this.falsePositivesValues);
	}


	public HashMap<String, Integer> getFalseNegativesCounts() {
		return getUniqueCounts(this.falseNegativesValues);
	}

	
	public ArrayList<String> getTruePositivesValues() {
		return truePositivesValues;
	}

	public void setTruePositivesValues(ArrayList<String> truePositivesValues) {
		this.truePositivesValues = truePositivesValues;
	}

	public ArrayList<String> getTrueNegativesValues() {
		return trueNegativesValues;
	}

	public void setTrueNegativesValues(ArrayList<String> trueNegativesValues) {
		this.trueNegativesValues = trueNegativesValues;
	}

	public ArrayList<String> getFalsePositivesValues() {
		return falsePositivesValues;
	}

	public void setFalsePositivesValues(ArrayList<String> falsePositivesValues) {
		this.falsePositivesValues = falsePositivesValues;
	}

	public ArrayList<String> getFalseNegativesValues() {
		return falseNegativesValues;
	}

	public void setFalseNegativesValues(ArrayList<String> falseNegativesValues) {
		this.falseNegativesValues = falseNegativesValues;
	}

	private  ArrayList<String> trueNegativesValues;
	private  ArrayList<String> falsePositivesValues;
	private  ArrayList<String> falseNegativesValues;	
	private double threshold;
	private double a;
	private double avgCommonGrams;
	
	public ResultItem(){
		this.a=-1;
	}
	
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public double getA() {
		return a;
	}
	public void setA(double a) {
		this.a = a;
	}
	public int getTruePositives() {
		return truePositives;
	}
	public void setTruePositives(int truePositives) {
		this.truePositives = truePositives;
	}
	public int getTrueNegatives() {
		return trueNegatives;
	}
	public void setTrueNegatives(int trueNegatives) {
		this.trueNegatives = trueNegatives;
	}
	public int getFalsePositives() {
		return falsePositives;
	}
	public void setFalsePositives(int falsePositives) {
		this.falsePositives = falsePositives;
	}
	public int getFalseNegatives() {
		return falseNegatives;
	}
	public void setFalseNegatives(int falseNegatives) {
		this.falseNegatives = falseNegatives;
	}
	
	public double getPrecision(){
		double precision=(double) truePositives/((double) truePositives+(double)falsePositives);
		if(Double.isNaN(precision)) return 0;
		else return precision;
	}
	public double getRecall(){
		double recall=(double) truePositives/((double) truePositives+(double)falseNegatives);
		if(Double.isNaN(recall)) return 0;
		else return recall;

	}
	public double getF1(){
		
		double p=(double) truePositives/((double) truePositives+(double)falsePositives);
		double r=(double) truePositives/((double) truePositives+(double)falseNegatives);
		double f1=((2.0*p*r)/(p+r));
		if(Double.isNaN(f1)) return 0;
		else return f1;
	}

	public double getAvgCommonGrams() {
		return avgCommonGrams;
	}

	public void setAvgCommonGrams(double avgCommonGrams) {
		this.avgCommonGrams = avgCommonGrams;
	}



	
}
