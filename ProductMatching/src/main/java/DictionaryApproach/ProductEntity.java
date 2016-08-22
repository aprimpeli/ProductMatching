package DictionaryApproach;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductEntity {

	private String name;
	private HashMap<String, ArrayList<String>> featureValues;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashMap<String, ArrayList<String>> getFeatureValues() {
		return featureValues;
	}
	public void setFeatureValues(HashMap<String, ArrayList<String>> featureValues) {
		this.featureValues = featureValues;
	}
}
