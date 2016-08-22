package wdc.Thesis;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.simmetrics.metrics.JaroWinkler;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.metrics.SmithWatermanGotoh;
import org.simmetrics.metrics.StringMetrics;

import BagOfWordsModel.SimilarityCalculator;

public class MongeElkanTest {

	
	@Test
	public void testMongeElkanMethod() throws IOException{
		
		String measure="jaroWrinkler"; // default, levenshtein, jaroWrinkler
		ArrayList<String> a = new ArrayList<String>();
		ArrayList<String> b = new ArrayList<String>();

		a.add("i phone6");
		b.add("i-phone6");

		
		System.out.println("MONGEELKAN-LEV:"+SimilarityCalculator.getMongeElkanSimilarity(a, b, "levenshtein"));
		System.out.println("MONGEELKAN-JARO:"+SimilarityCalculator.getMongeElkanSimilarity(a, b, "jaroWrinkler"));
		System.out.println("MONGEELKAN-SMITH:"+SimilarityCalculator.getMongeElkanSimilarity(a, b, "default"));
		Levenshtein lev =  new Levenshtein();
		JaroWinkler jar = new JaroWinkler();
		SmithWatermanGotoh smith = new SmithWatermanGotoh();
		
		
//		System.out.println("LEVENSHTEIN:"+lev.compare("iphone", "i"));
//		System.out.println("JARO:"+jar.compare("iphone", "i"));
//		System.out.println("SMITH:"+smith.compare("iphone", "i"));

	}
}
