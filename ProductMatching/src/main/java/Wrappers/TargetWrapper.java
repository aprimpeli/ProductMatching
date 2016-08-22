package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class TargetWrapper {
	public static void main (String args[]) throws IOException{
		TargetWrapper w= new TargetWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/"
				+ "phones/node8dd5e71059c4296f696e29ebfbb0e9.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div#specifications").first();
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
	
		
		return allTablesContent.toString();

	}
}
