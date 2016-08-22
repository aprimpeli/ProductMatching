package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class TomtopWrapper {
	public static void main (String args[]) throws IOException{
		TomtopWrapper w= new TomtopWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/"
				+ "phones/node83f8e462117b54abb673d66a9290fdb5.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div[class=description block]").first();
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
	
		
		return allTablesContent.toString();

	}
}
