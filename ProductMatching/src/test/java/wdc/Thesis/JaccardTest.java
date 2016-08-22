package wdc.Thesis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import BagOfWordsModel.ModelConfiguration;
import BagOfWordsModel.SimilarityCalculator;

public class JaccardTest {

	@Test
	public void runTest(){
		List<String> doc1 = new ArrayList<String>(){{
			add("studio live monitoring impedance 38ohm");		
		}};	
		List<String> doc2 = new ArrayList<String>(){{
			add("20 ohm");

		}};	
		ModelConfiguration config = new ModelConfiguration();
		config.setLevenshteinThreshold(0.1);
		config.setOnTopLevenshtein(true);
		SimilarityCalculator test = new SimilarityCalculator(config);
		double score = test.jaccardSimilarity(doc1, doc2);
		System.out.println(score);
	}
}
