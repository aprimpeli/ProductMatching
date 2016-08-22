package CombinationUtils;

import java.util.HashSet;

public class CommonFeatures {

	private String node;
	private String predicted;
	private int matches;
	private HashSet<String> commonWords;
	
	public CommonFeatures(String node, String predicted, int matches, HashSet<String> commonWords){
		this.node=node;
		this.predicted=predicted;
		this.matches=matches;
		this.commonWords=commonWords;
	}
	
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getPredicted() {
		return predicted;
	}
	public void setPredicted(String predicted) {
		this.predicted = predicted;
	}
	public int getMatches() {
		return matches;
	}
	public void setMatches(int matches) {
		this.matches = matches;
	}
	public HashSet<String> getCommonWords() {
		return commonWords;
	}
	public void setCommonWords(HashSet<String> commonWords) {
		this.commonWords = commonWords;
	}
	
	
}
