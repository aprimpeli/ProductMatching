package Wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import BagOfWordsModel.DocPreprocessor;

public class EbayWrapper {
	public static void main (String args[]) throws IOException{
		EbayWrapper w= new EbayWrapper();
		System.out.println(w.getTableListsContentWithWrapperText("2.ProfilingOfData/LabelledDataProfiling/"
				+ "HTML_Pages/Unified_extra/phones/node31b8212baab41182355763ea91d635dd.html"));
	}
	
	public String getTableListsContentWithWrapperText(String file) throws IOException{
			
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		Element firstTable = doc.getElementsByClass("itemAttr").first();
		if(null!=firstTable){
			Elements items = firstTable.select("tr");
			for(Element item:items){
				Elements values= item.select("td");
				if(values.size()!= 4) continue;
				//get only the values not the feature names
				allTablesContent.append(values.get(0).text()+" ");
				allTablesContent.append(values.get(1).text()+" ");
				allTablesContent.append(values.get(2).text()+" ");
				allTablesContent.append(values.get(3).text()+" ");						
			}				
		} 					
		//detailed table
		Element secondTable = doc.getElementsByClass("prodDetailSec").first();
		if (null!=secondTable){
			Elements detaileditems = secondTable.select("tr");
			for(Element item:detaileditems){
				Elements values = item.select("td");
				if(values.size()!=2) continue;
				else {
					if (values.get(1).text().isEmpty() || values.get(1).text()==null ) continue;
					allTablesContent.append(values.get(0).text()+" ");						
					allTablesContent.append(values.get(1).text()+" ");	

				}
			}
		}				
		return allTablesContent.toString();
	}
}
