package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class TescoWrapper {
	public static void main (String args[]) throws IOException{
		TescoWrapper w= new TescoWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/"
				+ "phones/node3f8f9382abf2a9f20b414d575d6fb1.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("ul[class=features]").first();
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
	
		Element secondTable = doc.select("div#product-spec-container").first();
		allTablesContent.append(Jsoup.parse(secondTable.html()).text()+" ");
		
		return allTablesContent.toString();

	}
}
