package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class AlibabaWrapper {
	
	public static void main (String args[]) throws IOException{
		AlibabaWrapper w= new AlibabaWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/"
				+ "HTML_Pages/Unified_extra/phones/noded1df2fda3631e7ad09cde2bc6e5c298.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
		
		
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.getElementById("J-product-detail");
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
		return allTablesContent.toString();

	}
		
}
