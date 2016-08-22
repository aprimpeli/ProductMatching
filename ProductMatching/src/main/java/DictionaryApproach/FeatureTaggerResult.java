package DictionaryApproach;

import java.util.ArrayList;
import java.util.HashMap;

public class FeatureTaggerResult {

	private HashMap<String, ArrayList<String>>  taggedWords;
	private HashMap<String, Integer>  taggedWordFrequency;
	private HashMap<String, ArrayList<String>> nonExactMatchedWords;
	
	public HashMap<String, Integer> getTaggedWordFrequency() {
		return taggedWordFrequency;
	}
	public void setTaggedWordFrequency(HashMap<String, Integer> taggedWordFrequency) {
		this.taggedWordFrequency = taggedWordFrequency;
	}
	public HashMap<String, ArrayList<String>> getTaggedWords() {
		return taggedWords;
	}
	public void setTaggedWords(HashMap<String, ArrayList<String>> taggedWords) {
		this.taggedWords = taggedWords;
	}
	public HashMap<String, ArrayList<String>> getNonExactMatchedWords() {
		return nonExactMatchedWords;
	}
	public void setNonExactMatchedWords(HashMap<String, ArrayList<String>> nonExactMatchedWords) {
		this.nonExactMatchedWords = nonExactMatchedWords;
	}
	
}
