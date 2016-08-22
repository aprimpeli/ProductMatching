package wdc.Thesis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import BagOfWordsModel.DocPreprocessor;
import BagOfWordsModel.ModelConfiguration;
import BagOfWordsModel.PreprocessingConfiguration;
import BagOfWordsModel.SimilarityCalculator;

public class SimilarityComputationTest {

	//FILEPATHS
		static String productCategory="phone"; //tv, phone, headphone
		static String catalog="";
		static String htmlFolder="";
		static String labelled="";
		static String nqFileMap="";
		
		//PREPROCESSING
		static boolean stemming=true;
		static boolean stopWordRemoval=true;
		static boolean lowerCase=true;
		static boolean numericalHandling=true;
		static boolean tablesFiltering=true;
		static boolean modelNameHandling=true;
		static String htmlParsingElements="";
		
		//SIMILARITY CONFIGURATION
		//cosine or simple(exact matching) or jaccard or simple with frequency threshold
		static String similarityType="jaccard";
		//simple(average frequency) or tfidf applied only for cosine similarity
		static String typeOfWeighting="tfidf";
		//any possible number of n-grams is possible
		static int grams=1;
		//applied only for simple with frequency threshold similarity - otherwise they make no sense
		static double maxFreq=0.03;
		static double minFreq=0;
		//can be applies on top of everyone of the previous similarity types in order to calculate common words
		static boolean onTopLevenshtein=false;
		static double levenshteinThreshold=0.8;

		public void runSimilarityTest() throws IOException{
		List<String> doc1 = new ArrayList<String>(){{
			add("apple");
			add("apple");
			add("cake");
			add("cut");
			add("slice");
			add("recipe");
			add("recipe");
			
		}};			
		List<String> doc2 = new ArrayList<String>(){{
			add("slice");
			add("slice");
			add("recipe");
			add("recipe");
			add("meat");
			add("meat");
			add("meat");
			add("bean");
			add("cook");
			add("buy");

		}};		
		List<String> doc3 = new ArrayList<String>(){{
			add("apple");
			add("cake");
			add("cake");
			add("slice");
			add("buy");
			add("buy");
			add("buy");
			
		}};	
	
		List<String> query1 = new ArrayList<String>(){{
			add("apple");
			add("cake");
			add("recipe");
			
		}};	
		
		List<String> query2 = new ArrayList<String>(){{
			add("apple");
			add("cut");
			add("cake");
			
		}};	
	
		HashMap<String, List<String>> docCorpus = new HashMap<String,List<String>>();
		docCorpus.put("doc1",doc1);
		docCorpus.put("doc2",doc2);
		docCorpus.put("doc3",doc3);
		
		HashMap<String, List<String>> queryCorpus = new HashMap<String,List<String>>();
		queryCorpus.put("query1",query1);
		queryCorpus.put("query2",query2);
		
		ModelConfiguration modelConfig= new ModelConfiguration("BagOfWordsModel",productCategory, catalog,
				 htmlFolder,  labelled,  
				 similarityType, typeOfWeighting, grams,
				 maxFreq,  minFreq,  onTopLevenshtein,
				 levenshteinThreshold,false,"");
		
		PreprocessingConfiguration preprocessing = new PreprocessingConfiguration(stemming, stopWordRemoval, lowerCase,htmlParsingElements,numericalHandling,tablesFiltering,modelNameHandling);

		SimilarityCalculator calculate = new SimilarityCalculator(modelConfig, preprocessing, queryCorpus, docCorpus , null);

		for(Map.Entry<String,List<String>> query: queryCorpus.entrySet()){
			calculate.getPredictedAnswers(query.getValue(),null,null,false);
		}
	}

	@Test
	public void MongeElkanTest() throws IOException{
		String a="5mp";
		String b="5megapixel";
		
		DocPreprocessor process = new DocPreprocessor();
		PreprocessingConfiguration config = new PreprocessingConfiguration(false, false, true, "",numericalHandling,tablesFiltering,modelNameHandling);
		List<String> gramsa = process.textProcessing(null, a, 1, false, config, "");
		List<String> gramsb = process.textProcessing(null, b, 1, false, config, "");

		System.out.println(SimilarityCalculator.getMongeElkanSimilarity(gramsa, gramsb, "default"));
	}
}
