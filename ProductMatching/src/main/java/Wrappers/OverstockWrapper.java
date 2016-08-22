package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import BagOfWordsModel.DocPreprocessor;

public class OverstockWrapper {
	public static void main (String args[]) throws IOException{
		OverstockWrapper w= new OverstockWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/"
				+ "phones/node2fc4f4364f9b6eef132b1ac2ffb09184.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
				
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.select("table[class=table table-dotted table-extended table-header translation-table]").first();
		Element content = firstTable.select("tbody").first();
		if(null!=content){
			Elements items = content.select("tr");
			for(Element item:items){
				Elements values= item.select("td");
				if(values.size()!= 2) continue;
				allTablesContent.append(values.get(0).text()+" ");
				allTablesContent.append(values.get(1).text()+" ");
			}	
			Element secondTable = doc.select("table[class=table table-dotted table-header]").first();
			Elements seconditems = secondTable.select("tr");
			if(null!=content){
				for(Element item:seconditems){
					Elements values= item.select("td");
					if(values.size()!= 2) continue;
					allTablesContent.append(values.get(0).text()+" ");				
					allTablesContent.append(values.get(1).text()+" ");				
				}
				
			} 
		}
	
		
		return allTablesContent.toString();

	}
}
