package Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TestDataRetriever {
	
	public static void main (String args[]) throws IOException{
		
		
		
		String TestOrTraining="Training";
		String productCategory="phone";
		String file="resources\\6.AdditionalMethod\\TrainingTestData\\"+productCategory+"s"+TestOrTraining+".csv";
		HashMap<String,ArrayList<String>> testData=getTestData( file);
		System.out.println(testData.size());
		
		
	}

	public static HashMap<String, ArrayList<String>> getTestData(String filePath) throws IOException{
		
		HashMap<String,ArrayList<String>> testData= new HashMap<String,ArrayList<String>>();
		BufferedReader reader  = new BufferedReader(new FileReader(new File(filePath)));
		
		String line="";
		while((line = reader.readLine()) != null){
			line=line.replaceAll("\"", "");
			if (!line.startsWith("node")) continue;
			String html = line.split(";")[0].split("-")[0];
			String catalogEntry = line.split(";")[0].split("-")[1];
			
			ArrayList<String> existingEntries= new ArrayList<String>();
			
			existingEntries=testData.get(html);
			if(null==existingEntries) existingEntries=new ArrayList<String>();
			existingEntries.add(catalogEntry);
			testData.put(html, existingEntries);

			
		}
		reader.close();
		return testData;
	}
}
