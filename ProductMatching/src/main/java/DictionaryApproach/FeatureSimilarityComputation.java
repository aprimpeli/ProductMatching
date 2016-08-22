package DictionaryApproach;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import BagOfWordsModel.ModelConfiguration;
import BagOfWordsModel.SimilarityCalculator;

public class FeatureSimilarityComputation {


	 public FeatureSimilarityComputation() {
		
	}
	
	public HashMap<String,Double> getPredictedAnswersinDictionaryApproach
	(HashMap<String,ArrayList<String>> featureValuesOfPage, Dictionary dictionary, ModelConfiguration model, String htmlPath, SimilarityCalculator calculate) throws IOException{
		
		HashMap<String, Double> predictedAnswers = new HashMap<String, Double>();
		
		for (ProductEntity product:dictionary.getProductEntities()){
			System.out.println("Compared with:"+product.getName());
			double score=0.0;
			if(featureValuesOfPage.size()==0) predictedAnswers.put(product.getName().toLowerCase(), 0.0);
			
			else {
				for(Map.Entry<String,ArrayList<String>> featureValue:featureValuesOfPage.entrySet()){
					List<String> valuesOfPage = featureValue.getValue();
					List<String> valuesOfcatalog= product.getFeatureValues().get(featureValue.getKey());
					double currentScore=0.0;
					List<String> tokenizedValuesOfPage = new ArrayList<String>();
					List<String> tokenizedValuesOfCatalog= new ArrayList<String>();
					for (String s:valuesOfPage) tokenizedValuesOfPage.addAll(Arrays.asList(s.split(" ")));
					for (String s:valuesOfcatalog) tokenizedValuesOfCatalog.addAll(Arrays.asList(s.split(" ")));
					//System.out.println("Page:"+tokenizedValuesOfPage.toString());
					//System.out.println("Tokenized Catalog:"+tokenizedValuesOfCatalog.toString());
					if(model.getSimilarityType().equals("cosine"))
						currentScore=calculate.cosineSimilarity(tokenizedValuesOfCatalog, tokenizedValuesOfPage);
					else if(model.getSimilarityType().equals("simple"))							
						currentScore=calculate.simpleContainmentSimilarity(tokenizedValuesOfCatalog, tokenizedValuesOfPage);	
					else {
						System.out.println("Not defined similarity type:"+model.getSimilarityType());
						System.exit(0);
					}
				
					//currentScore=calculateNonExact.getMongeElkanSimilarity(valuesOfcatalog, valuesOfPage, model.getEditDistanceType());
					System.out.println(featureValue.getKey()+"--"+currentScore);				
									
					if(!Double.isNaN(currentScore))	score+=currentScore;
					else {
						System.out.println("Score between "+product.getName()+" and "+htmlPath+ " could not be defined");
					}
					//System.out.println("Feature:"+featureValue.getKey()+"--"+currentScore);
				}
				predictedAnswers.put(product.getName().toLowerCase(), score/(double)product.getFeatureValues().size());
			}
			
		}
		
		return predictedAnswers;
	}
	
	
}
