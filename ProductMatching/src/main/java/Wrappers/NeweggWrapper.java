package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class NeweggWrapper {
	public static void main (String args[]) throws IOException{
		NeweggWrapper w= new NeweggWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/"
				+ "headphones/nodec6b3fdd4f0ac126a096cd23e55cec14.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("div#Details_Content").first();
		if (null!=firstTable) allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
		else allTablesContent.append(" ");
		
		return allTablesContent.toString();

	}
}
