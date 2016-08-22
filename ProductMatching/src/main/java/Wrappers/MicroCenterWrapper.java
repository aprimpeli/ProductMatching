package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class MicroCenterWrapper {

	public static void main (String args[]) throws IOException{
		MicroCenterWrapper w= new MicroCenterWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/"
				+ "HTML_Pages/Unified_extra/phones/node36aabfe23c73e25d2b7b62abc7e126a.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div#detail-list").first();
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
	
		Element secondTable = doc.select("article#tab-specs").first();
		allTablesContent.append(Jsoup.parse(secondTable.html()).text()+" ");
		
		return allTablesContent.toString();

	}

}
