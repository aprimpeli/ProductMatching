package RapidMinerUtils;

import java.util.HashMap;

public class CleanFeaturePair {
	
	private String id;
	private HashMap<String,String> FeatureValuePairs;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public HashMap<String, String> getFeatureValuePairs() {
		return FeatureValuePairs;
	}
	public void setFeatureValuePairs(HashMap<String, String> featureValuePairs) {
		FeatureValuePairs = featureValuePairs;
	}
	
	
}
