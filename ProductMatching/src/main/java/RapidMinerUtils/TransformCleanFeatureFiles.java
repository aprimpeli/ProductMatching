package RapidMinerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

public class TransformCleanFeatureFiles {

	public static void main (String args[]) throws IOException{
		
		String file="phones.txt.tsv";
		TransformCleanFeatureFiles tra = new TransformCleanFeatureFiles();
		tra.transformFile(file);
	}
	
	public void getUniqueProperties(String filePath) throws FileNotFoundException{
		
		ArrayList<String> uniqueProperties = new ArrayList<String>();
		ArrayList<CleanFeaturePair> entities = new ArrayList<CleanFeaturePair>();
		TsvParserSettings settings = new TsvParserSettings();
		TsvParser parser = new TsvParser(settings);

		// parses all rows in one go.
		List<String[]> allRows = parser.parseAll(new FileReader(filePath));
		for(String[] row:allRows){
			String id=row[0]+"-"+row[1];
			HashMap<String,String> featureValuePairs= new HashMap<String,String>();
			
			for(int i=4;i<row.length;i++){
				
				//property
				if(i%2==0){
					if(!uniqueProperties.contains(row[i])) uniqueProperties.add(row[i]);
					System.out.println("Property:"+row[i]);
					System.out.println("Value:"+row[i+1]);
					featureValuePairs.put(row[i], row[i+1]);
					
				}				
			}
			CleanFeaturePair f = new CleanFeaturePair();
			f.setId(id);
			f.setFeatureValuePairs(featureValuePairs);
		}
		
	}

	public void transformFile(String filePath) throws IOException {
		
		ArrayList<String> uniqueProperties = new ArrayList<String>();
		ArrayList<CleanFeaturePair> entities = new ArrayList<CleanFeaturePair>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
		
		String line="";
		while ((line = reader.readLine()) != null) {
	        String row[] = line.split("\\t");
	        
	        String id=row[0]+"-"+row[1];
			HashMap<String,String> featureValuePairs= new HashMap<String,String>();
			System.out.println(line);
			for(int i=4;i<row.length;i++){
				
				//property
				if(i%2==0){
					if(!uniqueProperties.contains(row[i])) uniqueProperties.add(row[i]);
					System.out.println("Property:"+row[i]);
					System.out.println("Value:"+row[i+1]);
					featureValuePairs.put(row[i], row[i+1]);
					
				}				
			}
			CleanFeaturePair f = new CleanFeaturePair();
			f.setId(id);
			f.setFeatureValuePairs(featureValuePairs);
			entities.add(f);
	    }
		reader.close();
	}

}
