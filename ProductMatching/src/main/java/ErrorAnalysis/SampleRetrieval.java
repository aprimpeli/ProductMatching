package ErrorAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import BagOfWordsModel.DocPreprocessor;

/**
 * @author Anna
 *To be combined with a Rapidminer process for getting stratified sample
 */
public class SampleRetrieval {
	
	static String nodeIDsFile="resources\\4.ErrorAnalysis\\RapidMiner\\data\\tvs_sample_ids.csv";
	static String sampleFile="resources\\4.ErrorAnalysis\\RapidMiner\\data\\sampleTVLabelled.json";
	static String dataPath="resources/2.ProfilingOfData/LabelledDataProfiling/";

	static String originFile=dataPath+"/CorrectedLabelledEntities/UnifiedGoldStandard_extra/tvs.txt";
	
	public static void main (String args[]) throws JSONException, IOException{
		
		BufferedReader readNodes = new BufferedReader(new FileReader(new File(nodeIDsFile)));
		Set<String> sampleNodes = new HashSet<String>();
		String node="";
		while( (node = readNodes.readLine()) != null){
			sampleNodes.add(node.replace("\"",""));
		}
		readNodes.close();
		
		//parse the original set and if the node is included in the set of sample nodes write the json object
		JSONArray labelledEntities = new JSONArray(DocPreprocessor.fileToText(originFile));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sampleFile), "UTF-8"));

		for(int i = 0 ; i < labelledEntities.length() ; i++){
			JSONObject entity = labelledEntities.getJSONObject(i);
			String nodeIDLabelled= entity.getString("id_self");
			if(sampleNodes.contains(nodeIDLabelled)){
				bw.append(entity.toString());
				bw.newLine();	
			}
		}
		

		bw.flush();
		bw.close();
	}
}
