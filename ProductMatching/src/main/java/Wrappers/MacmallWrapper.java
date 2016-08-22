package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class MacmallWrapper {
	
	public static void main (String args[]) throws IOException{
		MacmallWrapper w= new MacmallWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/tvs"
				+ "/node2a50a66d0cfe2f9c641097c83892ee.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div[class=mfr-num]").first();
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
	
		Element secondTable = doc.select("div[class=part-num]").first();
		allTablesContent.append(Jsoup.parse(secondTable.html()).text()+" ");
		
		return allTablesContent.toString();

	}
}
