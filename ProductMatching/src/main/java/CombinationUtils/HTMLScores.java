package CombinationUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class HTMLScores {
	
	private String id;
	private HashMap<String, Double> scoresTocatalog;
	private ArrayList<String> rightAnswers;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HashMap<String, Double> getScoresTocatalog() {
		return scoresTocatalog;
	}

	public void setScoresTocatalog(HashMap<String, Double> scoresTocatalog) {
		this.scoresTocatalog = scoresTocatalog;
	}

	public ArrayList<String> getRightAnswers() {
		return rightAnswers;
	}

	public void setRightAnswers(ArrayList<String> rightAnswers) {
		this.rightAnswers = rightAnswers;
	}
}
