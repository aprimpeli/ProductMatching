package RapidMinerUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import BagOfWordsModel.PreprocessingConfiguration;
import BagOfWordsModel.SimilarityCalculator;
import Utils.HTMLPages;
import Utils.ProductCatalogs;

public class GetContentOfAllItems {

	//PREPROCESSING
	static boolean stemming=true;
	static boolean stopWordRemoval=true;
	static boolean lowerCase=true;
	static boolean numericalHandling=true;
	static boolean tablesFiltering=true;
	static boolean modelNameHandling=true;
		
	static String htmlFolder="";
	static String productCategory="";
	static String productCatalog="";
	static int grams=0;
	
	public static void main(String[] args) throws JSONException, IOException {
		
		PreprocessingConfiguration preprocessing = new PreprocessingConfiguration(stemming, stopWordRemoval, lowerCase,"",numericalHandling,tablesFiltering,modelNameHandling);
		// TODO Auto-generated method stub
		HashMap<String,List<String>> tokensOfAllHTML = HTMLPages.getHTMLToken(null,null,"");
		HashMap<String,List<String>> tokensOfAllCatalogEntities = ProductCatalogs.getCatalogTokens(productCategory, productCatalog, grams, preprocessing);
	
		
	}

}
