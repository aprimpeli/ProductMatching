package Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import BagOfWordsModel.DocPreprocessor;
import BagOfWordsModel.PreprocessingConfiguration;
import DictionaryApproach.Dictionary;
import DictionaryApproach.ProductEntity;

public class ProductCatalogs {

	static BufferedWriter logProcessing;
	
	
	static {
	    try{
			logProcessing = new BufferedWriter(new FileWriter(new File("resources/CatalogWords_errorAnalysis.csv")));
	    } catch (IOException e){
	        e.printStackTrace();
	    }
	}
	
	public static void main (String args[]){
		try{
			DocPreprocessor process = new DocPreprocessor();
			ProductCatalogs test = new ProductCatalogs();
			String filepath="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\3.MatchingModels\\testInput\\catalog\\TVCatalog.json";
			PreprocessingConfiguration preprocess = new PreprocessingConfiguration(false, true, true,"",true,true,true);
			HashMap<String, List<String>> tokens = test.getCatalogTokens("tv", filepath, 1, preprocess);
			for (Map.Entry<String,List<String> > entry: tokens.entrySet()){
				process.printList(entry.getValue());
			}
		}
		catch (Exception e){
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	public static HashMap<String, List<String>> getCatalogTokens(String productCategory, String filePath, int grams, PreprocessingConfiguration preprocessing) throws JSONException, IOException{

		DocPreprocessor processText = new DocPreprocessor();
		HashMap<String, List<String>> catalogProducts = new HashMap<String,List<String>>();
		String headItem=getHeadItem(productCategory);
		ArrayList<String> properties = new ArrayList<String>();
		
		properties = getPropertiesFromFile(productCategory);

	    JSONObject catalog = new JSONObject(DocPreprocessor.fileToText(filePath));

		JSONArray array = catalog.getJSONArray(headItem);
		for(int i = 0 ; i < array.length() ; i++){
			ArrayList<String> entityValues = new ArrayList<String>();
			ArrayList<String> preprocessedValue = new ArrayList<String>();
			for (String property: properties){
				
				String value= array.getJSONObject(i).getString(property);
				if (value.equals("")) continue;
				preprocessedValue.add(value);
				List<String> tokenizedValue = processText.textProcessing(null, value, grams, false, preprocessing,null);
				//check for product model names
				if(preprocessing.isModelNameHandling()){
					tokenizedValue=getTokensWithModelNameHandling(tokenizedValue,preprocessing);					
				}
				entityValues.addAll(tokenizedValue);

			}
			logProcessing.append("Preprocessed;"+array.getJSONObject(i).getString("Product Name")+";"+preprocessedValue.toString());
			logProcessing.newLine();
			logProcessing.append("Processed;"+array.getJSONObject(i).getString("Product Name")+";"+entityValues.toString());
			logProcessing.newLine();
			logProcessing.flush();

			//System.out.println(array.getJSONObject(i).getString("Product Name")+"---"+entityValues.toString());
			catalogProducts.put(array.getJSONObject(i).getString("Product Name"), entityValues);
		}
		
		return catalogProducts;
	}

	static ArrayList<String> getTokensWithModelNameHandling(List<String> tokenizedValue, PreprocessingConfiguration preprocessing) {
		
		ArrayList<String> tokensToReturn=new ArrayList<String>();
		String processedValue="";
		for(int t=0;t<tokenizedValue.size();t++) processedValue=processedValue+" "+tokenizedValue.get(t);
		ArrayList<String> modelNamesFound= new ArrayList<String>();
		for(String modelName:preprocessing.getProductNames()){
			if(processedValue.contains(modelName)) modelNamesFound.add(modelName);
		}
		//put the model names and the rest of the tokens
		for(String fmodel:modelNamesFound){
			int countMatches= StringUtils.countMatches(processedValue, fmodel);
			
			for (int matches=1; matches<=countMatches;matches++){
				tokensToReturn.add(fmodel);}
		}
		String[] restOfTokens=processedValue.split(" ");
		for(int i=0;i<restOfTokens.length;i++) {
			if(!(restOfTokens[i].equals("") ||restOfTokens[i].equals(" ") ))tokensToReturn.add(restOfTokens[i]);
		}
		
		return tokensToReturn;
		
	}

	public static ArrayList<String> getPropertiesFromFile(String productCategory){
		String filename="";
		ArrayList<String> properties = new ArrayList<String>();
		if(productCategory.equals("phone")){
			filename="resources/Properties/phone.txt";
		}
		else if (productCategory.equals("headphone")){
			filename="resources/Properties/headphone.txt";
		}
		else if (productCategory.equals("tv"))		{
			filename="resources/Properties/tv.txt";
		}			
		else {
			System.out.println("Cannot retrieve properties for "+productCategory+". The catalogs stored can handle the following product categories: phones, headphone and tv ");			
			System.exit(0);
		}

		 try
		 {
			 FileInputStream in = new FileInputStream(filename);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String strLine;
		 
			  while((strLine = br.readLine())!= null)
			  {
				  properties.add(strLine);
			  }
			  br.close();

			  return properties;
		 }
		 catch(Exception e){
			 System.out.println(e);
			 return null;
		 }

	}
	
	public static String getHeadItem(String productCategory){
		if(productCategory.equals("phone")){
			return "phones";
		}
		else if (productCategory.equals("headphone")){
			 return "headphones";
		}
		else if (productCategory.equals("tv"))		{
			return "tvs";
		}			
		else {
			System.out.println("Cannot retrieve properties for "+productCategory+". The catalogs stored can handle the following product categories: phone, headphone and tv ");			
			System.exit(0);
			return null;
		}
	}

	
	public static HashMap<String, List<String>> getCatalogTokensDictionaryApproach(
			Dictionary dictionary) throws IOException {
		
		HashMap<String,List<String>> catalogTokens = new HashMap<String,List<String>>();
		List<ProductEntity> products = dictionary.getProductEntities();
		for (ProductEntity product:products){
			catalogTokens.put(product.getName(), new ArrayList<String>());
			for (Map.Entry<String,ArrayList<String>> values:product.getFeatureValues().entrySet())
				catalogTokens.get(product.getName()).addAll(values.getValue());
			

			logProcessing.append("Processed;"+product.getName()+";"+catalogTokens.get(product.getName()).toString());
			logProcessing.newLine();
			logProcessing.flush();
		}
		
		return catalogTokens;
	}

	public static ArrayList<String> getProductNamesFromcatalog(String productCategory, String filePath, PreprocessingConfiguration preprocessing) throws JSONException, IOException{
		
		DocPreprocessor processText = new DocPreprocessor();
		String headItem=getHeadItem(productCategory);
		String productNameProperty="Product Name";
		ArrayList<String> productNames=new ArrayList<String>();
	    JSONObject catalog = new JSONObject(DocPreprocessor.fileToText(filePath));

		JSONArray array = catalog.getJSONArray(headItem);
		
		for(int i = 0 ; i < array.length() ; i++){
			ArrayList<String> preprocessedValue = new ArrayList<String>();
				
			String value= array.getJSONObject(i).getString(productNameProperty);
			preprocessedValue.add(value);
			List<String> tokenizedValue = processText.textProcessing(null, value, 1, false, preprocessing,null);
			
			//do not tokenize the value
			String productName="";
			for(int t=0;t<tokenizedValue.size();t++) productName= productName+" "+tokenizedValue.get(t);
			
			productNames.add(productName.trim());
		}
		
		return productNames;
		
	}
		
		
}

