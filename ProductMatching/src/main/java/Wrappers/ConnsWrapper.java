package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import BagOfWordsModel.DocPreprocessor;

public class ConnsWrapper {
	public static void main (String args[]) throws IOException{
		ConnsWrapper w= new ConnsWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/"
				+ "tvs/node30a2be3afe2094bc95c9b875fd909987.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("table#product-attribute-specs-table").first();
		if (null!=firstTable) allTablesContent.append(Jsoup.parse(firstTable.html()).text()+" ");
		else allTablesContent.append(" ");
	
		
		return allTablesContent.toString();

	}
}
