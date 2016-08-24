package BagOfWordsModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.json.JSONException;
import org.simmetrics.metrics.NeedlemanWunch;

import Evaluation.Evaluation;
import Evaluation.EvaluationItem;
import Evaluation.ReportGenarator;
import Evaluation.ResultItem;
import Utils.ErrorAnalysisLog;
import Utils.HTMLPages;
import Utils.ProductCatalogs;
import Utils.TestDataRetriever;

public class BoWInitializer {

	//configure
	static String productCategory="phone"; //tv, phone, headphone
	static String mode="normal"; // define the mode (wrapper/normal)
	static String dataPath="2.ProfilingOfData\\LabelledDataProfiling\\";
	static String experimentsPath="3.MatchingModels\\ExperimentsResults\\";
	
	//do not configure but keep the same file structure
	static String modelType="BagOfWordsModel";
	static String catalog=dataPath+"ProductCatalog/"+productCategory+"Catalog.json";
	static String htmlFolder=dataPath+"HTML_Pages/Unified_extra/"+productCategory+"s";
	static String labelled=dataPath+"/CorrectedLabelledEntities/UnifiedGoldStandard_extra/"+productCategory+"s.txt";
	static String currentExperimentPath; //allHTMLContent,MarkedUpContent,TablesandListsContent
	static String logFile="";

	//PREPROCESSING
	//basic preprocessing methods
	static boolean stemming=true;
	static boolean stopWordRemoval=true;
	static boolean lowerCase=true;
	
	//advanced methods
	static boolean numericalHandling=false;
	static boolean tableslistsFiltering=false;
	static boolean modelNameHandling=false;
	
	
	static String htmlParsingElements="marked_up_data"; //all_html, html_tables, html_lists, html_tables_lists, marked_up_data, html_tables_lists_wrapper

	static String errorLogFile="resources/errorAnalysis/"+productCategory+"_"+htmlParsingElements+"_error_analysis.csv";

	//advanced matching methods
	static boolean optimalFeatureWeighting=false;
	static String weightsFile="resources/learnedWeights/learnedWeights_markup.csv";	

	//get results only on training or only on test data
	static boolean onlyOnTestTrainingData=false; 
	static String TestOrTraining = "Test";
	static String TestTrainingDataPath="resources/TrainingTestData/"+productCategory+"s"+TestOrTraining+".csv";

	//String evaluation type definition
	static String evaluationType="optimizingF1"; //average, median, optimizingF1
	
	final static ErrorAnalysisLog error_logger=new ErrorAnalysisLog();


	public static void main (String args[]) throws Exception{
		if(args.length == 5){
			productCategory=args[0];
			mode=args[1];
			dataPath=args[2];
			experimentsPath=args[3];
			htmlParsingElements=args[4];
			catalog=dataPath+"/ProductCatalog/"+productCategory+"Catalog.json";
			htmlFolder=dataPath+"/HTML_Pages/Unified_extra/"+productCategory+"s";
			labelled=dataPath+"/CorrectedLabelledEntities/UnifiedGoldStandard_extra/"+productCategory+"s.txt";
		}
		String[] allHtmlParsingElements=htmlParsingElements.split(";");
		
		for (int i=0; i<allHtmlParsingElements.length;i++){
			htmlParsingElements=allHtmlParsingElements[i];
			logFile="resources/log/error_analysis_scores_"+productCategory+"_"+allHtmlParsingElements[i];
			currentExperimentPath=experimentsPath+allHtmlParsingElements[i]+"test.csv";
			errorLogFile="resources/errorAnalysis/"+productCategory+"_"+allHtmlParsingElements[i]+"_error_analysis.csv";
			runMultipleInitializer();
		}
				
	}
	private static void runMultipleInitializer() throws JSONException, IOException{
		LinkedHashMap<ModelConfiguration, ResultItem> allResults = new LinkedHashMap<ModelConfiguration,ResultItem>();
		Queue<ModelConfiguration> allmodels = defineExperiments();

		for(ModelConfiguration modelConfig:allmodels){
					
			ResultItem results= new ResultItem();
			List<EvaluationItem> ItemstoBeEvaluated = new ArrayList<EvaluationItem>();
			
			String categories[]= productCategory.split(";");
			for(int cat=0;cat<categories.length;cat++){
				if(categories.length>1){
					productCategory=categories[cat];
					catalog=dataPath+"/ProductCatalog/"+productCategory+"Catalog.json";
					htmlFolder=dataPath+"/HTML_Pages/Unified_extra/"+productCategory+"s";
					labelled=dataPath+"/CorrectedLabelledEntities/UnifiedGoldStandard_extra/"+productCategory+"s.txt";
					TestTrainingDataPath="resources/TrainingTestData/"+productCategory+"s"+TestOrTraining+".csv";

					modelConfig.setCatalog(catalog);
					modelConfig.setHtmlFolder(htmlFolder);
					modelConfig.setLabelled(labelled);
					modelConfig.setProductCategory(productCategory);
				}
				HashMap<String, ArrayList<String>> testData = new HashMap<String, ArrayList<String>>();
				if(onlyOnTestTrainingData)	testData=	TestDataRetriever.getTestData(TestTrainingDataPath);
				
				PreprocessingConfiguration preprocessing = new PreprocessingConfiguration(stemming, stopWordRemoval, lowerCase, htmlParsingElements,numericalHandling,tableslistsFiltering,modelNameHandling);
				
				System.out.println("---START---");
				System.out.println("The bag of words model will be executed for the product category "+ productCategory);
				System.out.println("The chosen similarity method is "+modelConfig.getSimilarityType());
				System.out.println("The similarity measure will include Levenshtein distance on top:"+modelConfig.isOnTopLevenshtein()+" with threshold:"+modelConfig.getLevenshteinThreshold());
				System.out.println("The chosen type of weighting (not available for simple similarity method) is "+modelConfig.getTypeOfWeighting());
				System.out.println("The bag of words model will be implemented on the basis of "+modelConfig.getGrams()+" grams");
				
				ArrayList<String> productNames=new ArrayList<String>();
				if(modelNameHandling) productNames=ProductCatalogs.getProductNamesFromcatalog(modelConfig.getProductCategory(), modelConfig.getCatalog(), preprocessing);
				preprocessing.setProductNames(productNames);
				
				HashMap<String,List<String>> tokensOfAllHTML = HTMLPages.getHTMLToken(modelConfig, preprocessing, mode);
				
				if(null==tokensOfAllHTML) {
					System.out.println("Something went wrong. Check");
					System.exit(0);
				}
			
				HashMap<String,List<String>> tokensOfAllCatalogEntities = ProductCatalogs.getCatalogTokens(modelConfig.getProductCategory(), modelConfig.getCatalog(), modelConfig.getGrams(), preprocessing);
				SimilarityCalculator calculate = new SimilarityCalculator(modelConfig,preprocessing, tokensOfAllHTML, tokensOfAllCatalogEntities,error_logger);
				File folderHTML = new File(modelConfig.getHtmlFolder());
				File[] listOfHTML = folderHTML.listFiles();
				
								
				for (int i = 0; i < listOfHTML.length; i++) {
					//if you are in wrapper mode do not consider all pages but only the ones that could be potentially parsed by the implemented wrappers
					if(mode.equals("wrapper")){
						String pld = HTMLPages.getPLDFromHTMLPath(labelled, listOfHTML[i].getPath());
				    	if(!(pld.contains("ebay")||pld.contains("tesco")||pld.contains("alibaba")||pld.contains("overstock")) ) continue;
					}
					

			    	HashMap<String, Double> predictedAnswersForPage = new HashMap<String,Double>();
			    	ArrayList<String> rightAnswers= calculate.getRightAnswer(modelConfig.getLabelled(), listOfHTML[i].getName());
			    	if (rightAnswers.size()==0) {
			    		System.out.println("no answer defined:"+listOfHTML[i]);
			    		continue;
			    	}
			    	//get all the matches with the equivalent scores
			    	if(onlyOnTestTrainingData && !testData.containsKey(listOfHTML[i].getName())) continue;
			    	predictedAnswersForPage = calculate.getPredictedAnswers(tokensOfAllHTML.get(listOfHTML[i].getName()), listOfHTML[i].getName() ,testData.get(listOfHTML[i].getName()), onlyOnTestTrainingData);
			    	
			    	EvaluationItem toBeEvaluated= new EvaluationItem();
			    	toBeEvaluated.setPath(listOfHTML[i].getName());
			    	toBeEvaluated.setPredictedAnswers(predictedAnswersForPage);
			    	toBeEvaluated.setRightAnswers(rightAnswers);
			    	toBeEvaluated.setProductCategory(modelConfig.getProductCategory());
			    	ItemstoBeEvaluated.add(toBeEvaluated);

			    }
				System.out.println("Items to be Evaluated:"+ItemstoBeEvaluated.size());

			}
			
			System.out.println("Items to be Evaluated:"+ItemstoBeEvaluated.size());
			
			//write some log to check the predictions
			BufferedWriter logger = new BufferedWriter(new FileWriter(new File(logFile+modelConfig.getSimilarityType()+".txt")));
			writeLog(logger, ItemstoBeEvaluated);
			logger.close();
			
			Evaluation evaluate = new Evaluation(modelConfig.getSimilarityType());
			//average, median, optimizingF1
			if (evaluationType.equals("average")) results=evaluate.getResultsWithAverageThreshold(ItemstoBeEvaluated);
			else if (evaluationType.equals("median")) results= evaluate.getResultsWithMedianThreshold(ItemstoBeEvaluated); //*1.5
			else if (evaluationType.equals("optimizingF1")) results=evaluate.getResultsOptimizingF1(ItemstoBeEvaluated);
			else System.out.println("Wrong input for evaluation type. I can only handle average, median and optimizingF1");
					
			
		    System.out.println("---RESULTS---");
		    System.out.println("Precision: "+results.getPrecision());
		    System.out.println("Recall: "+results.getRecall());
		    System.out.println("F1: "+results.getF1());
			System.out.println("---END---");
			System.out.println("False Negatives:"+results.getFalseNegatives());
			System.out.println("False Positives:"+results.getFalsePositives());
			System.out.println("True Positives:"+results.getTruePositives());
		//	System.out.println("True Positives:"+results.getTruePositivesValues());
						
							
			allResults.put(modelConfig, results);
			error_logger.printlogErrorAnalysis(errorLogFile, results);

		}
		ReportGenarator report = new ReportGenarator();
		report.generateReport(allResults, currentExperimentPath);
	}


	private static void getStatsofHTMLCorpus(
			HashMap<String, List<String>> tokensOfAllHTML) {
		List<String> totalTokens=new ArrayList<String>();
		HashSet<String> uniqueTokens=new HashSet<String>();
		HashMap<Integer,Integer> distributionOfGramsSize= new HashMap<Integer,Integer>();
		
		for(Map.Entry<String, List<String>> t:tokensOfAllHTML.entrySet()){
			totalTokens.addAll(t.getValue());
			uniqueTokens.addAll(t.getValue());
		}
		
		for(String token:totalTokens){
			Integer gramsOfSize = distributionOfGramsSize.get(token.length());
			if(null==gramsOfSize){
				gramsOfSize=0;
			}
			distributionOfGramsSize.put(token.length(), gramsOfSize+1);
		}
		System.out.println("Total unigrams = "+totalTokens.size());
		System.out.println("Average unigrams = "+ (double)totalTokens.size()/(double)tokensOfAllHTML.size());

		System.out.println("Unique unigrams = "+uniqueTokens.size());
		System.out.println("Average unique unigrams = "+ (double)uniqueTokens.size()/(double)tokensOfAllHTML.size());
		
		System.out.println("Distribution of grams sizes:");
		for(Map.Entry<Integer, Integer> d:distributionOfGramsSize.entrySet())
			System.out.println(d.getKey()+";"+d.getValue());

		
	}
	private static void writeLog(BufferedWriter logger,
			List<EvaluationItem> itemstoBeEvaluated) throws IOException {
		for(EvaluationItem ev:itemstoBeEvaluated){
			logger.append(ev.getPath());
			logger.newLine();
			for (String answer:ev.getRightAnswers()){
				logger.append("Right Answer:"+answer);
				logger.newLine();
			}
			for(Entry<String, Double> pr:ev.getPredictedAnswers().entrySet()){
				logger.append("Predicted Answer:"+pr.getKey()+"---"+pr.getValue());
				logger.newLine();

			}
		}
		
	}
	private static Queue<ModelConfiguration> defineExperiments() {
		Queue<ModelConfiguration> models = new LinkedList<ModelConfiguration>();
		
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled, 
				 "simple", "n/a", 1,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled, 
				 "simple", "n/a", 2,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "simple", "n/a", 3,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "simple", "n/a", 2,0,  0,  true, 0.9, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "simple", "n/a", 2,0,  0,  true, 0.95, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "simple with frequency threshold", "n/a", 1,0.08, 0, false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "simple with frequency threshold", "n/a", 1,0.06, 0, false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "simple with frequency threshold", "n/a", 2,0.05,0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "simple with frequency threshold", "n/a", 2,0.04, 0, false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "jaccard", "n/a", 2,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "jaccard", "n/a", 3,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "jaccard", "n/a", 4,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "cosine", "simple", 1,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "cosine", "simple", 2,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "cosine", "simple", 3,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "cosine", "simple", 2,0,  0,  true, 0.6, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "cosine", "tfidf", 1,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "cosine", "tfidf", 2,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "cosine", "tfidf", 3,0,  0,  false, 0, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "cosine", "tfidf", 2,0,  0,  true, 0.6, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration
				(modelType,productCategory, catalog,htmlFolder,  labelled,  
				 "cosine", "tfidf", 2,0,  0,  true, 0.8, optimalFeatureWeighting, weightsFile));
		
		

		return models;
	}
	
}
