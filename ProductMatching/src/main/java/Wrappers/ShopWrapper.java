package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class ShopWrapper {
	public static void main (String args[]) throws IOException{
		ShopWrapper w= new ShopWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/"
				+ "headphones/node1d701f12284b0f5f58498bffbca8ec.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div#details").first();
		if (null!=firstTable) allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
		else allTablesContent.append(" ");
		
		return allTablesContent.toString();

	}
}
