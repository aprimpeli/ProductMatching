package DictionaryApproach;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

import org.json.JSONException;

import UnitConversion.Test;
import Utils.ErrorAnalysisLog;
import Utils.HTMLPages;
import Utils.ProductCatalogs;
import Utils.TestDataRetriever;
import BagOfWordsModel.DocPreprocessor;
import BagOfWordsModel.ModelConfiguration;
import BagOfWordsModel.PreprocessingConfiguration;
import BagOfWordsModel.SimilarityCalculator;
import Evaluation.Evaluation;
import Evaluation.EvaluationItem;
import Evaluation.ReportGenarator;
import Evaluation.ResultItem;

public class DictionaryInitializer {

	//configure
	static String similarityComputation="bow";
	static String productCategory="phone"; //tv, phone, headphone
	static String mode="normal"; // define the mode (wrapper/normal). In the wrapper mode only the 4 plds for which a wrapper exists are considered (ebay, tesco, alibaba, overstock)
	static String dataPath="2.ProfilingOfData/LabelledDataProfiling/";
	//static String experimentsPath="C:/Users/Johannes/Google Drive/Master_Thesis/3.MatchingModels/ExperimentsResults/Dictionary/"+mode+"/"+productCategory+"/";
	static String experimentsPath="3.MatchingModels/ExperimentsResults";
	
	//FILEPATHS
	static String mainPath="resources/";
	static String modelType="DictionaryApproach";
	static String catalog=dataPath+"ProductCatalog/"+productCategory+"Catalog.json";
	static String htmlFolder=dataPath+"HTML_Pages/Unified_extra/"+productCategory+"s_test";
	static String labelled=dataPath+"/CorrectedLabelledEntities/UnifiedGoldStandard_extra/"+productCategory+"s.txt";
	static String currentExperimentPath; //allHTMLContent,MarkedUpContent,TablesandListsContent
	static String logFile="resources/log/logEvaluationItemsDictionary";

	//PREPROCESSING
	static boolean stemming=true;
	static boolean stopWordRemoval=true;
	static boolean lowerCase=true;
	static String htmlParsingElements="marked_up_data"; //all_html, html_tables, html_lists, html_tables_lists, marked_up_data, html_tables_lists_wrapper
	static double idfThresholdForcatalog=0.8;
	static boolean idfFiltering =false;
	//advanced
	static boolean numericalHandling=true;
	static boolean tablesListsFiltering=false;
	static boolean modelNameHandling=false;
	
	//advanced matching
	static boolean optimalFeatureWeighting=false;
	static String weightsFile="resources/learnedWeights/learnedWeights_markup.csv";	

	static String errorLogFile="resources/errorAnalysis/dictionary_"+htmlParsingElements+"_"+productCategory+"_error_analysis.csv";

	//get results only on training or only on test data - for enhanced methods
	static boolean onlyOnTestTrainingData=true; 
	static String TestOrTraining = "Test";
	static String TestTrainingDataPath="resources/TrainingTestData/"+productCategory+"s"+TestOrTraining+".csv";
	//String evaluation type definition
	static String evaluationType="optimizingF1"; //average, median, optimizingF1

	HashMap<String, HashMap<Integer, List<String>>> tokenizedInput;
	HashMap<String, ArrayList<String>> rightAnswersIndex;
	Dictionary dictionary;
	final static ErrorAnalysisLog error_logger=new ErrorAnalysisLog();
	PreprocessingConfiguration preprocessing;

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
			errorLogFile="resources/errorAnalysis/dictionary_"+htmlParsingElements+"_"+productCategory+"_error_analysis.csv";
		}
		String[] allHtmlParsingElements=htmlParsingElements.split(";");
		
		for (int i=0; i<allHtmlParsingElements.length;i++){
		
			DictionaryInitializer run =new DictionaryInitializer();
			htmlParsingElements=allHtmlParsingElements[i];
			System.out.println(htmlParsingElements);
			currentExperimentPath=experimentsPath+allHtmlParsingElements[i]+".csv";
			logFile="resources/log/dict_error_analysis_scores_"+productCategory+"_"+allHtmlParsingElements[i];
			errorLogFile="resources/errorAnalysis/dict_"+productCategory+"_"+allHtmlParsingElements[i]+"_error_analysis.csv";

			

			run.runMultipleInitializer();
		}
	}
	
	private void getDictionary() throws JSONException, IOException{
		dictionary = new Dictionary();
		DictionaryCreator creator= new DictionaryCreator();
		preprocessing = new PreprocessingConfiguration(stemming, stopWordRemoval, lowerCase, htmlParsingElements,numericalHandling,tablesListsFiltering,modelNameHandling);
		dictionary=creator.createDictionary(catalog, productCategory,preprocessing, labelled, idfThresholdForcatalog, idfFiltering);
	}
	private HashMap<String,List<String>> getFeatureTagging(ModelConfiguration modelConfig) throws IOException{
		
		File folderHTML = new File(htmlFolder);
		File[] listOfHTML = folderHTML.listFiles();
		HashMap<String,List<String>> tokensOfAllHTML=new HashMap<String,List<String>>();
		
		BufferedWriter logProcessing = new BufferedWriter(new FileWriter(new File("resources/HTMLPages_dictionary_errorAnalysis_"+htmlParsingElements+".csv")));
		System.out.println("Begin Feature Tagging");
		
		for (int i = 0; i < listOfHTML.length; i++) {
			List<String> allTaggedTokens= new ArrayList<String>();

	    	if(mode.equals("wrapper") ){
				String pld = HTMLPages.getPLDFromHTMLPath(labelled, listOfHTML[i].getPath());
				if (pld.contains("ebay")||pld.contains("tesco")||pld.contains("alibaba")||pld.contains("overstock")) continue;
	    	}
			//do the tagging step
	    	FeatureTagger tag;
	    	if(modelConfig.getDictsimType().equals("exact")) tag= new FeatureTagger();
	    	else tag = new FeatureTagger(tokenizedInput.get(listOfHTML[i].getName()));	
	    	
			FeatureTaggerResult ftResult = tag.setFeatureTagging(listOfHTML[i].getPath(),dictionary.getDictionary(),preprocessing, modelConfig);
			HashMap<String, ArrayList<String>> tagged = ftResult.getTaggedWords();
			HashMap<String, ArrayList<String>> reversed = tag.reverseTaggedWords(tagged);
			for(Map.Entry<String, ArrayList<String>> r: reversed.entrySet()){
				for(String value:r.getValue()){
					//add it as many times as it appeared in the corpus
					for(int fr=0;fr<ftResult.getTaggedWordFrequency().get(value);fr++)
						allTaggedTokens.add(value);
				}
				
				
			}
			
			if (reversed.size()==0) {
				System.out.println("No tagging could be done for the page:"+listOfHTML[i].getPath());
			}
			tokensOfAllHTML.put(listOfHTML[i].getName(), allTaggedTokens);

			logProcessing.append("Processed;"+listOfHTML[i].getName()+";"+allTaggedTokens);
			logProcessing.newLine();
			logProcessing.flush();
			
			
			
		}

		logProcessing.close();
		return tokensOfAllHTML;
	}
	
	private void getRightAnswers() throws JSONException, IOException {
		File folderHTML = new File(htmlFolder);
		File[] listOfHTML = folderHTML.listFiles();
		rightAnswersIndex = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < listOfHTML.length; i++) {
			SimilarityCalculator calculate = new SimilarityCalculator();
	    	ArrayList<String> answers= calculate.getRightAnswer(labelled, listOfHTML[i].getName());
	    	if (answers.size()==0) {
	    		System.out.println("no answer defined:"+listOfHTML[i]);
	    		continue;
	    	}
	    	rightAnswersIndex.put(listOfHTML[i].getName(),answers);
		}
		
	}

	private void initializeTokenizer() throws IOException {
		File folderHTML = new File(htmlFolder);
		File[] listOfHTML = folderHTML.listFiles();
		tokenizedInput= new HashMap<String, HashMap<Integer, List<String>>>();
		for (int i = 0; i < listOfHTML.length; i++) {
			//if you are in wrapper mode do not consider all pages but only the ones that could be potentially parsed by the implemented wrappers
			String pld = HTMLPages.getPLDFromHTMLPath(labelled, listOfHTML[i].getPath());
	    	if(mode.equals("wrapper") && !(pld.contains("ebay")||pld.contains("tesco")||pld.contains("alibaba")||pld.contains("overstock")) ) continue;
			
	    	PreprocessingConfiguration preprocessing = new PreprocessingConfiguration(stemming, stopWordRemoval, lowerCase, htmlParsingElements,numericalHandling,tablesListsFiltering,modelNameHandling);
			HashMap<Integer,List<String>> pagetokenizedInput= new HashMap<Integer,List<String>>();
			DocPreprocessor process= new DocPreprocessor();
			for (int j=1; j<=5; j++){
				List<String> grams = process.textProcessing(listOfHTML[i].getPath(), null, j, true, preprocessing,labelled);
				pagetokenizedInput.put(j, grams);
			}
			tokenizedInput.put(listOfHTML[i].getName(), pagetokenizedInput);
		}
	}

	private void runMultipleInitializer() throws JSONException, IOException{
		
		LinkedHashMap<ModelConfiguration, ResultItem> allResults = new LinkedHashMap<ModelConfiguration,ResultItem>();
		Queue<ModelConfiguration> allmodels = defineExperiments();
		
		for (ModelConfiguration m:allmodels){
			if(m.getDictsimType().equals("non-exact")){
				System.out.println("Tokenize Input");
				initializeTokenizer();
				break;
			}
		}
		
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
		
				PreprocessingConfiguration preprocessing = new PreprocessingConfiguration(stemming, stopWordRemoval, lowerCase, htmlParsingElements,numericalHandling,tablesListsFiltering,modelNameHandling);

				System.out.println("Start Running the Models");
				int modelProgress =1;
		
			
				System.out.println("Model "+modelProgress+" out of"+allmodels.size() );
				modelProgress++;
			
			
				System.out.println("Get Right Answers");
				getRightAnswers();
		
				//create the dictionary
				System.out.println("Create Dictionary");
				getDictionary();
				
				System.out.println("---START---");
				System.out.println("The dictionary approach will be executed for the product category "+ productCategory);
				System.out.println("The chosen similarity method is "+modelConfig.getDictsimType());
				System.out.println("The edit distance measure to be implemented for non exact matching is Levenshtein: "+modelConfig.isOnTopLevenshtein());
				
				File folderHTML = new File(htmlFolder);
				File[] listOfHTML = folderHTML.listFiles();

						
				HashMap<String, List<String>> tokensOfAllHTML = getFeatureTagging(modelConfig);
				HashMap<String,List<String>> tokensOfAllCatalogEntities = ProductCatalogs.getCatalogTokensDictionaryApproach(dictionary);
				SimilarityCalculator calculate = new SimilarityCalculator(modelConfig,preprocessing, tokensOfAllHTML, tokensOfAllCatalogEntities,error_logger);

			
				for (int i = 0; i < listOfHTML.length; i++) {
	
					double progress=(((double)(i+1)/(double)listOfHTML.length))*100;
					if((int)progress % 10==0) System.out.println("Current Progress:"+(int)progress+"%");
					//if you are in wrapper mode do not consider all pages but only the ones that could be potentially parsed by the implemented wrappers
					String pld = HTMLPages.getPLDFromHTMLPath(labelled, listOfHTML[i].getPath());
			    	if(mode.equals("wrapper") && !(pld.contains("ebay")||pld.contains("tesco")||pld.contains("alibaba")||pld.contains("overstock")) ) continue;
							    	
			    	//get all the matches with the equivalent scores	
					HashMap<String, Double> predictedAnswersForPage = new HashMap<String,Double>();
					
					//tag.printTagged(reversed);
					if(similarityComputation=="bow"){
						if(onlyOnTestTrainingData && !testData.containsKey(listOfHTML[i].getName())) continue;
				    	predictedAnswersForPage = calculate.getPredictedAnswers(tokensOfAllHTML.get(listOfHTML[i].getName()), listOfHTML[i].getName() ,testData.get(listOfHTML[i].getName()), onlyOnTestTrainingData);
			    	}
	
					
			    	EvaluationItem toBeEvaluated= new EvaluationItem();
			    	toBeEvaluated.setPath(listOfHTML[i].getName());
			    	toBeEvaluated.setPredictedAnswers(predictedAnswersForPage);
			    	toBeEvaluated.setRightAnswers(rightAnswersIndex.get(listOfHTML[i].getName()));
			    	toBeEvaluated.setProductCategory(productCategory);
			    	ItemstoBeEvaluated.add(toBeEvaluated);
	
			    }
				System.out.println("Items to be Evaluated:"+ItemstoBeEvaluated.size());
			}
			System.out.println("Items to be Evaluated:"+ItemstoBeEvaluated.size());

			//write some log to check the predictions
			BufferedWriter logger = new BufferedWriter(new FileWriter(new File(logFile+modelConfig.getDictsimType()+".txt")));
			writeLog(logger, ItemstoBeEvaluated);
			logger.close();
			
			Evaluation evaluate = new Evaluation(modelConfig.getDictsimType());
			//average, median, optimizingF1
			if (evaluationType.equals("average")) results=evaluate.getResultsWithAverageThreshold(ItemstoBeEvaluated);
			else if (evaluationType.equals("median")) results= evaluate.getResultsWithMedianThreshold(ItemstoBeEvaluated); //*1.5
			else if (evaluationType.equals("optimizingF1")) results=evaluate.getResultsOptimizingF1(ItemstoBeEvaluated);
			else System.out.println("Wrong input for evaluation type. I can only handle average, median and optimizingF1");
				
		    System.out.println("---RESULTS---");
		    System.out.println("Precision: "+results.getPrecision());
		    System.out.println("Recall: "+results.getRecall());
		    System.out.println("F1: "+results.getF1());
		    System.out.println("Threshold: "+results.getThreshold());
			System.out.println("---END---");
			System.out.println("False Negatives:"+results.getFalseNegatives());
			System.out.println("False Positives:"+results.getFalsePositives());
			System.out.println("True Positives:"+results.getTruePositives());

		
			allResults.put(modelConfig, results);
			error_logger.printlogErrorAnalysis(errorLogFile, results);

		}
		ReportGenarator report = new ReportGenarator();
		report.generateReportDictionaryApproach(allResults, currentExperimentPath);
		
	}
	
	private List<String> getTokenizedTaggedWords(HashMap<String, ArrayList<String>> reversed) {
		List<String> words=new ArrayList<String>();
		for(Map.Entry<String,ArrayList<String>> tagged:reversed.entrySet()){
			for(String s:tagged.getValue())
				words.addAll(Arrays.asList(s.split(" ")));
		}
		return words;
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
		
		//simType, windowsize, labelledpath, finalSimThreshold, editdistancetype, prunelength, similarityType, weighting, grams, html,category,catalog
		models.add(new ModelConfiguration("exact", 0,labelled,  0, false, 0, "cosine", "tfidf", 1 , htmlFolder, productCategory, catalog, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration("non-exact", 3,labelled,  0.7, true, 3,"cosine", "tfidf", 1 , htmlFolder, productCategory, catalog, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration("non-exact", 2,labelled,  0.6, true, 3,"cosine", "tfidf", 1 , htmlFolder, productCategory, catalog, optimalFeatureWeighting, weightsFile));
		models.add(new ModelConfiguration("non-exact", 2,labelled,  0.85, true, 4,"cosine", "tfidf", 1 , htmlFolder, productCategory, catalog, optimalFeatureWeighting, weightsFile));

		return models;
	}
	
}
