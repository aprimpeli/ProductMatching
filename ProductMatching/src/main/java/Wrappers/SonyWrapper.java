package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class SonyWrapper {
	public static void main (String args[]) throws IOException{
		SonyWrapper w= new SonyWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/"
				+ "HTML_Pages/Unified_extra/tvs/node8af8e085933f61b8fb8325a659b37240.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("section#specifications").first();
		if (null!=firstTable) allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
		else allTablesContent.append(" ");
	
		return allTablesContent.toString();

	}
}
