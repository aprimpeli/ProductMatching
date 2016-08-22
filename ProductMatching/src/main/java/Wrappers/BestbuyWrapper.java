package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class BestbuyWrapper {

	public static void main (String args[]) throws IOException{
		BestbuyWrapper w= new BestbuyWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/"
				+ "HTML_Pages/Unified_extra/phones/node184f6ea8a1ee2399a6acc6d31871fd.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.getElementById("sku-model");
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
		
		Element secondTable = doc.getElementById("features");
		allTablesContent.append(Jsoup.parse(secondTable.html()).text()+" ");
		
		return allTablesContent.toString();

	}
}
