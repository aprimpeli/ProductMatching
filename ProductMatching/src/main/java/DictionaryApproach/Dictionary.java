package DictionaryApproach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Dictionary {

	private HashMap<String, Set<String>> dictionary;
	private ArrayList<ProductEntity> productEntities;
	public HashMap<String, Set<String>> getDictionary() {
		return dictionary;
	}
	public void setDictionary(HashMap<String, Set<String>> dictionary) {
		this.dictionary = dictionary;
	}
	public ArrayList<ProductEntity> getProductEntities() {
		return productEntities;
	}
	public void setProductEntities(ArrayList<ProductEntity> productEntities) {
		this.productEntities = productEntities;
	}
	
	
	
}
