package DictionaryApproach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;








import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Utils.ProductCatalogs;
import BagOfWordsModel.DocPreprocessor;
import BagOfWordsModel.PreprocessingConfiguration;
import BagOfWordsModel.Weightening;

public class DictionaryCreator {
	

	

	public Dictionary createDictionary(String pathToCatalog, String productCategory, PreprocessingConfiguration preprocessing, String labelledPath, double idfWeightThreshold, boolean idfFiltering) 
			throws JSONException, IOException{
		
		Dictionary CompleteDictionary = new Dictionary();
		
		HashMap<String,Set<String>> dictionary = new HashMap<String,Set<String>>();
		ArrayList<ProductEntity> allCatalogProductEntities = new ArrayList<ProductEntity>();
		
		ArrayList<String> properties = Utils.ProductCatalogs.getPropertiesFromFile(productCategory);
		
	    JSONObject catalog = new JSONObject(DocPreprocessor.fileToText(pathToCatalog));
		JSONArray catalogEntities = catalog.getJSONArray(Utils.ProductCatalogs.getHeadItem(productCategory));
		
		ArrayList<String> unimportantValues = new ArrayList<String>();
		

		if(idfFiltering) unimportantValues = getIDFsForCatalogValues(pathToCatalog, productCategory,  preprocessing, idfWeightThreshold);
		System.out.println("Unimportant values:"+unimportantValues);
		
		for(int i = 0 ; i < catalogEntities.length() ; i++){
			ProductEntity product = new ProductEntity();
			product.setName(catalogEntities.getJSONObject(i).getString("Product Name"));
			HashMap<String, ArrayList<String>> featureValues = new HashMap<String, ArrayList<String>>();
			for (String property: properties){
				featureValues.put(property, new ArrayList<String>());
				
				String value= catalogEntities.getJSONObject(i).getString(property);
				//preprocess the value
				InputPreprocessor process = new InputPreprocessor();
				if (value.equals("")) continue;
				else {
					if (null==dictionary.get(property)) dictionary.put(property, new HashSet<String>());					
					if (value.contains("|")){
						ArrayList<String> values = new ArrayList<String>(Arrays.asList(value.split("\\s*\\|\\s*")));
						Set<String> uniqueValues = new HashSet<String>(values);
						Set<String> newValues = new HashSet<String>();
						for(String v:uniqueValues){
							String processedValue;
						     processedValue = process.textProcessing(null, v, false, preprocessing, labelledPath);

							if(unimportantValues.contains(processedValue)) continue;
							newValues.add(processedValue);
						}
						dictionary.get(property).addAll(newValues);
						featureValues.get(property).addAll(newValues);
					}
					else {
						String processedValue;
						if(property.equals("Product Name") && (productCategory.equals("tv")|| productCategory.equals("headphone"))) {
							PreprocessingConfiguration modelNamePreprocessing = new PreprocessingConfiguration(preprocessing.isStemming(), preprocessing.isStopWordRemoval(), preprocessing.isLowerCase(),
									preprocessing.getHtmlParsingType(), false, preprocessing.isTablesFiltering(), preprocessing.isModelNameHandling());
							processedValue = process.textProcessing(null, value, false, modelNamePreprocessing, labelledPath);

						}
						else  {
							processedValue = process.textProcessing(null, value, false, preprocessing, labelledPath);
						}
						if(unimportantValues.contains(processedValue)) continue;
						dictionary.get(property).add(processedValue);
						featureValues.get(property).add(processedValue);
											
					}
				}
			}
			product.setFeatureValues(featureValues);
			allCatalogProductEntities.add(product);
		}
		CompleteDictionary.setDictionary(dictionary);
		CompleteDictionary.setProductEntities(allCatalogProductEntities);
		//printDictionary(dictionary);
		return CompleteDictionary;


	}
	
	private ArrayList<String> getIDFsForCatalogValues(
			String pathToCatalog, String productCategory,
			PreprocessingConfiguration preprocessing, double idfThreshold) throws IOException {

		HashMap<String, List<String>> catalogTokens= ProductCatalogs.getCatalogTokens(productCategory, pathToCatalog, 1, preprocessing);
		ArrayList<List<String>> CatalogEntitiesAsList = new ArrayList<List<String>>();
		for(Map.Entry<String,List<String>> v:catalogTokens.entrySet() ) CatalogEntitiesAsList.add(v.getValue());
		
		Weightening weights = new Weightening();
		HashMap<String,Double> weightsOfTerms = weights.getIDFWeighting(CatalogEntitiesAsList);
		
		ArrayList<String> unimportant = new ArrayList<String>();
		for(Map.Entry<String,Double> term:weightsOfTerms.entrySet()){
			if(term.getValue()>idfThreshold) unimportant.add(term.getKey());
		}
		return unimportant;
	}

	public void printDictionary(HashMap<String,Set<String> >dictionary){
		for(Map.Entry<String, Set<String>> entry:dictionary.entrySet()){
			String prop=entry.getKey();
			String values="";
			for(String v:entry.getValue())
				values+=v+"--";
			System.out.println(prop+":"+values);
		}
		
	}
}
