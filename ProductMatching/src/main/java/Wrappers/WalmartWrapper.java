package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class WalmartWrapper {
	public static void main (String args[]) throws IOException{
		WalmartWrapper w= new WalmartWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/"
				+ "phones/node1229217732928ddc23f59b16124547ec.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div[class=about-item-complete js-slide-panel-content hide-content display-block-m]").first();
		if (null!=firstTable) allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
		else allTablesContent.append(" ");
	
		
		return allTablesContent.toString();

	}
}
