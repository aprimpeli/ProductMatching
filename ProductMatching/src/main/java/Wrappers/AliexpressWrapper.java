package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class AliexpressWrapper {
	
	public static void main (String args[]) throws IOException{
		AliexpressWrapper w= new AliexpressWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/"
				+ "HTML_Pages/Unified_extra/phones/node5871edb130b8678d7533fd7b76f.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
		
		
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.getElementById("product-desc");
		allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
		
		
		return allTablesContent.toString();

	}
}
