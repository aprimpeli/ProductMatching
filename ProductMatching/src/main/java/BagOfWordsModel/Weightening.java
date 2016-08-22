package BagOfWordsModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Weightening {
	HashMap<String, Double> optimalWeights;
	
	public  Weightening(){};
	
	public  Weightening(String optimalWeightsFile) throws IOException{
		optimalWeights = readWeightsFromFile(optimalWeightsFile);
	};
	
	public HashMap<String,Double> getSimpleWeighting(List<String> wordsOfVector){
		HashMap<String, Double> gramWeights = new HashMap<String,Double>();
		int totalGrams= wordsOfVector.size();
		HashMap<String, Integer> frequencyOfWords = getFrequencyOfWords(wordsOfVector);
		for(Map.Entry<String,Integer> gramFrequency: frequencyOfWords.entrySet()){
			gramWeights.put(gramFrequency.getKey(), ((double)gramFrequency.getValue()/(double) totalGrams));
		}
		
		return gramWeights;
	}
	
	public HashMap<String, Double> getIDFWeighting(List<List<String>> vectorsOfCorpus){
		HashMap<String,Double> IDFWeights = new HashMap<String,Double>();
		Set<String> uniqueGrams = new HashSet<String>();
		for(List<String> vector: vectorsOfCorpus){
			for(String gram:vector)
				uniqueGrams.add(gram);
		}
		for (String uniqueGram:uniqueGrams){			
			int appearanceOfTokeninCorpus=0;
			for(List<String> vector: vectorsOfCorpus)
				if(vector.contains(uniqueGram)) appearanceOfTokeninCorpus++;
			IDFWeights.put(uniqueGram, Math.log10((double)vectorsOfCorpus.size()/(double)appearanceOfTokeninCorpus));
		}
		
		return IDFWeights;
	}
	
	public HashMap<String, Double> getTfIdfWeighting (List<String> wordsOfVector, HashMap<String,Double> IDFWeights){
		
		HashMap<String, Double> tfidfWeights = new HashMap<String,Double>();
		HashMap<String, Integer> frequencyOfWords = getFrequencyOfWords(wordsOfVector);
		double tfidf=0;
		for(Map.Entry<String,Integer> gramFrequency: frequencyOfWords.entrySet()){	
			if(null==IDFWeights.get(gramFrequency.getKey())){
				System.out.println("The idf weight for:"+gramFrequency.getKey()+"could not be retrieved. If you are in inexact matching this is ok.");
			}
			else  tfidf = (double) gramFrequency.getValue() * IDFWeights.get(gramFrequency.getKey());
			tfidfWeights.put(gramFrequency.getKey(), tfidf);
		}
		return tfidfWeights;
	}
	
	public HashMap<String,Double> getTfIdfWeighting_(List<String> wordsOfVector, List<List<String>> vectorsOfCorpus){
		//get tf
		HashMap<String, Integer> frequencyOfWords = getFrequencyOfWords(wordsOfVector);
		int totalDocuments = vectorsOfCorpus.size();
		HashMap<String,Double> gramWeights= new HashMap<String,Double>();
		for(Map.Entry<String,Integer> gramFrequency: frequencyOfWords.entrySet()){
			int appearanceOfTokeninCorpus=0;
			String token = gramFrequency.getKey();
			//search for the token in the other documents
			for(List<String> gramsOfDoc : vectorsOfCorpus){
				if(gramsOfDoc.contains(token)) appearanceOfTokeninCorpus++;
			}
			double idf = Math.log10((double)totalDocuments /(double) appearanceOfTokeninCorpus);
			gramWeights.put(token, (double)gramFrequency.getValue()*idf);
		}
		return gramWeights;
		
	}

	public HashMap<String,Double> getBooleanWeightning(List<String> wordsOfVector){
		HashMap<String,Double> weights = new HashMap<String,Double>();
		for (String gram: wordsOfVector)
			weights.put(gram, 1.0);
		return weights;
	}
	public HashMap<String, Integer> getFrequencyOfWords(List<String> wordsOfVector){
	
		HashMap<String, Integer> frequencyOfWords = new HashMap<String,Integer>();
		Set<String> unique = new HashSet<String>(wordsOfVector);
		for (String key : unique) {
			frequencyOfWords.put(key, Collections.frequency(wordsOfVector, key));
		}
		return frequencyOfWords;
	}

	public HashMap<String, Double> getOptimalWeighting(
		List<String> itemVector) throws IOException {
		HashMap<String,Double> itemWeights = new HashMap<String,Double>();

		for(String word:  itemVector){
			if(optimalWeights.containsKey(word)) itemWeights.put(word, optimalWeights.get(word));
			else itemWeights.put(word, 0.0);
		}
		return itemWeights;
	}

	private HashMap<String, Double> readWeightsFromFile(
			String featureWeightsFile) throws IOException {
		HashMap<String, Double> allWeights= new HashMap<String,Double>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(featureWeightsFile)));
		String line="";
		while((line = reader.readLine()) != null){
			String attribute=line.split(";")[0];
			String score=line.split(";")[1].replace(",", ".");
			double weight= Double.parseDouble(score);
			allWeights.put(attribute.replaceAll("\"", ""), weight);
		}
		reader.close();
		return allWeights;
	}
	
	
}
