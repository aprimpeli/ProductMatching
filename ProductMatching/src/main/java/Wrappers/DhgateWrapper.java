package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class DhgateWrapper {

	public static void main (String args[]) throws IOException{
		DhgateWrapper w= new DhgateWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/"
				+ "HTML_Pages/Unified_extra/phones/node3d4ecf68541752f15092d7d01485675c.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div#itemDescription").select("div[class=description]").first();
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
	
		
		return allTablesContent.toString();

	}
}
