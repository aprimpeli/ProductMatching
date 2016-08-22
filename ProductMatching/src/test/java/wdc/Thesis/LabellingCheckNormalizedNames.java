package wdc.Thesis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import BagOfWordsModel.DocPreprocessor;

public class LabellingCheckNormalizedNames {

	@Test
	public void checkNames() throws JSONException, IOException{
		
		Scanner sc = new Scanner(System.in);


		String file="";

		JSONArray labelled = new JSONArray(DocPreprocessor.fileToText(file));
		for(int i = 0 ; i < labelled.length() ; i++){
			JSONObject entity = labelled.getJSONObject(i);
			System.out.println(entity.getString("normalized_product_name"));
			System.out.println(entity.getString("product-name"));
			System.out.println(entity.getString("id_self"));
			if (sc.next().equals("y")) continue;
		}
		
	}
	
	@Test
	public void checkDistinctNames2() throws JSONException, IOException{
		
		String file="";
		Set<String> s = new HashSet<String>();
		JSONArray labelled = new JSONArray(DocPreprocessor.fileToText(file));
		for(int i = 0 ; i < labelled.length() ; i++){
			JSONObject entity = labelled.getJSONObject(i);
			s.add(entity.getString("id_warc"));
		}
		for (String y :s)
			System.out.println(y);
	}
	
	//@Test get the necessary nq file
	public void test() throws JSONException, IOException{
		
		String fileNQ="";
		
		String labelledFile="";
		
		Set<String> nodes = new HashSet<String>();
		
		JSONArray labelled = new JSONArray(DocPreprocessor.fileToText(labelledFile));
		for(int i = 0 ; i < labelled.length() ; i++){
			JSONObject entity = labelled.getJSONObject(i);
			nodes.add(entity.getString("id_self"));
		}
		for (String node :nodes){
			FileInputStream fis = new FileInputStream(fileNQ);
			 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		 
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.split("\\|\\|")[0].equals(node)){
					System.out.println(line);
					break;
				}
			}
		 
			br.close();
		}
	}
}
