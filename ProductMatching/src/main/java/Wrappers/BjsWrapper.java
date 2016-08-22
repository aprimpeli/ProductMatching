package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class BjsWrapper {

	public static void main (String args[]) throws IOException{
		BjsWrapper w= new BjsWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/"
				+ "HTML_Pages/Unified_extra/phones/node89f753a473d0f8126f23b65ff87af0cd.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div#tab-2").first();
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
	
		
		return allTablesContent.toString();

	}
}
