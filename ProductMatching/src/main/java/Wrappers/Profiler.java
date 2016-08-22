package Wrappers;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;

import Utils.HTMLFragmentsExtractor;

public class Profiler {

	static String productCategory= "tv";
	static String dataPath="2.ProfilingOfData/LabelledDataProfiling/";
	static String htmlFolder=dataPath+"HTML_Pages/Unified_extra/"+productCategory+"s";
	static String labelled=dataPath+"/CorrectedLabelledEntities/UnifiedGoldStandard_extra/"+productCategory+"s.txt";

	
	public static void main(String args[]) throws JSONException, IOException{
		
		File folder = new File(htmlFolder);
		File[] listOfFiles = folder.listFiles();
		HTMLFragmentsExtractor extr = new HTMLFragmentsExtractor(labelled);
		int count=0;
	    for (int i = 0; i < listOfFiles.length; i++) {
	    	String content=extr.getTablesListsContentFromWrappers(listOfFiles[i].getPath(), labelled);
	    	
	    	if(null==content || content.equals("") || content.length()==0 || content.equals(" ")) continue;
	    	else count++;
	    }
	    System.out.println(count);
	    System.out.println("Contain at least one specification table or list:"+((double)count/500.0));
	}
}
