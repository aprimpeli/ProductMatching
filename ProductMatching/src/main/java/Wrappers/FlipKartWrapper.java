package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class FlipKartWrapper {

	public static void main (String args[]) throws IOException{
		FlipKartWrapper w= new FlipKartWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/"
				+ "HTML_Pages/Unified_extra/phones/node60c5ddf8a78a6a2e379eb950b210fec4.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div[class=productSpecs specSection]").first();
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
	
		
		return allTablesContent.toString();

	}
}
