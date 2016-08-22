package Utils;


import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import Wrappers.AlibabaWrapper;
import Wrappers.AliexpressWrapper;
import Wrappers.BestbuyWrapper;
import Wrappers.BjsWrapper;
import Wrappers.ConnsWrapper;
import Wrappers.DhgateWrapper;
import Wrappers.EbayWrapper;
import Wrappers.FlipKartWrapper;
import Wrappers.MacmallWrapper;
import Wrappers.MicroCenterWrapper;
import Wrappers.NeweggWrapper;
import Wrappers.OverstockWrapper;
import Wrappers.SamsclubWrapper;
import Wrappers.SearsoutletWrapper;
import Wrappers.ShopWrapper;
import Wrappers.SonyWrapper;
import Wrappers.TargetWrapper;
import Wrappers.TechspotWrapper;
import Wrappers.TescoWrapper;
import Wrappers.TomtopWrapper;
import Wrappers.WalmartWrapper;


import BagOfWordsModel.DocPreprocessor;

public class HTMLFragmentsExtractor {
	static String labelledEntitiesPath;
	
	public HTMLFragmentsExtractor(String labelPath){
		labelledEntitiesPath=labelPath;
	}
			
	public static void main(String args[]) throws IOException{
		HTMLFragmentsExtractor extr= new HTMLFragmentsExtractor("");
		
		File folder = new File("resources/2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/"
				+ "tvs");
		File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	    	System.out.println(listOfFiles[i].getName());
	    	System.out.println(extr.getTablesListsContentFromWrappers(listOfFiles[i].getPath(), labelledEntitiesPath));
	    }
		    
		
	}

	public String getListText(String file) throws IOException {
		
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allListContent = new StringBuilder();
	   
		
		for (Element list : doc.select("ul,ol,dl")) {
			//exclude nested elements
			if(isNestedElement(list, false,true)) continue;
			
		    String list_text=Jsoup.parse(list.html()).text(); 
		    allListContent.append(list_text);
		}
		return allListContent.toString();
	}
	
	public String getTableText(String file) throws IOException {
		
		String contentOfFile = DocPreprocessor.fileToText(file);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
		StringBuilder allTablesContent= new StringBuilder();
		
		for (Element table : doc.select("table")) {
			//exclude nested elements
			if(isNestedElement(table, false,true)) continue;
			
		    String table_text=Jsoup.parse(table.html()).text(); 

		    allTablesContent.append(table_text);
			//System.out.println(table_text);		     
		}
		//System.out.println(allTablesContent.toString());
		return allTablesContent.toString();
	}

	public boolean isNestedElement(Element el, boolean isTable, boolean isList){
				
		if((isTable && el.children().select("table").size()>1) || (isList && el.children().select("table").size()>0)) return true;
		if((isList && el.children().select("ul,ol,dl").size()>1) || (isTable && el.children().select("ul,ol,dl").size()>0)) return true;

		return false;

	}
	

	public String getTablesListsContentFromWrappers(String file, String labelledEntitiesPath) throws JSONException, IOException{
		String pld= HTMLPages.getPLDFromHTMLPath(labelledEntitiesPath, file);
		if(pld.contains("alibaba")){
			AlibabaWrapper w= new AlibabaWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("aliexpress")){
			AliexpressWrapper w= new AliexpressWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("bestbuy")){
			BestbuyWrapper w= new BestbuyWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("bjs")){
			BjsWrapper w= new BjsWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("conns")){
			ConnsWrapper w= new ConnsWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("dhgate")){
			DhgateWrapper w= new DhgateWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("ebay")){
			EbayWrapper w= new EbayWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("flipkart")){
			FlipKartWrapper w= new FlipKartWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("macmal")){
			MacmallWrapper w= new MacmallWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("microcenter")){
			MicroCenterWrapper w= new MicroCenterWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("newegg")){
			NeweggWrapper w= new NeweggWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("overstock")){
			OverstockWrapper w= new OverstockWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("samsclub")){
			SamsclubWrapper w= new SamsclubWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("searsoutlet")){
			SearsoutletWrapper w= new SearsoutletWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("shop")){
			ShopWrapper w= new ShopWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("sony")){
			SonyWrapper w= new SonyWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("target")){
			TargetWrapper w= new TargetWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("techspot")){
			TechspotWrapper w= new TechspotWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("tesco")){
			TescoWrapper w= new TescoWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("tomtop")){
			TomtopWrapper w= new TomtopWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else if(pld.contains("walmart")){
			WalmartWrapper w= new WalmartWrapper();
			return w.getTableListsContentWithWrapperText(file);
		}
		else {
			System.out.println("PLD:"+pld+" is unknown.");
			System.exit(0);
			return null;
		}
	}
	
	public String getFilteredTableText(String filepath) throws IOException {
		
		String contentOfFile = DocPreprocessor.fileToText(filepath);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");
		StringBuilder allTablesContent= new StringBuilder();

		//Petar's code
		main_loop: for (Element table : doc.getElementsByTag("table")) {

			// remove tables inside forms
			for (Element p : table.parents()) {
				if (p.tagName().equals("form")) {
					continue main_loop;
				}
			}

			// remove table with sub-tables
			Elements subTables = table.getElementsByTag("table");
			subTables.remove(table);
			if (subTables.size() > 0) {
				continue;
			}
			
			// remove table with sub-lists
			Elements subLists = table.select("ul,ol,dl");
			subLists.remove(table);
			if (subLists.size()>0) {
				continue;
			}

			// remove table with option elements
			if (table.select("option").size()>0) {
				continue;
			}
					
			//if survived all the above
			String table_text=Jsoup.parse(table.html()).text(); 
		    allTablesContent.append(table_text+" ");
			
		}
		return allTablesContent.toString();
	}

	public String getFilteredListText(String filepath) throws IOException{
		String contentOfFile = DocPreprocessor.fileToText(filepath);
		Document doc = Jsoup.parse(contentOfFile, "UTF-8");
		StringBuilder allListsContent= new StringBuilder();

		//Petar's code
		for (Element list : doc.getElementsByTag("ul")) {

			// remove list inside forms
			for (Element p : list.parents()) {
				if (p.tagName().equals("form")) {
					continue;
				}
			}

			// remove list with sub-tables
			Elements subTables = list.getElementsByTag("table");
			subTables.remove(list);
			if (subTables.size() > 0) {
				continue;
			}
			
			// remove list with sub-lists
			Elements subLists = list.select("ul,ol,dl");
			subLists.remove(list);
			if (subLists.size()>0) {
				continue;
			}

			// remove table with option elements
			if (list.select("option").size()>0) {
				continue;
			}
			
//			// there should not be a big number of urls
//			Elements links = list.select("a[href]");
//			if(links.size()>=list.getElementsByTag("li").size()) {
//				System.out.println(filepath);
//				System.out.println("Links:"+links.size());
//				System.out.println("Li:"+list.getElementsByTag("li").size());
//				continue;
//			}
			
			//if all li elements have an href child elements diregard
			Elements liEl = list.getElementsByTag("li");
			boolean allHref=true;
			for(Element li:liEl){
				if(li.select("a[href]").size()==0){
					allHref=false;
					break;
				}
			}
			if(allHref) continue;
			
			//if survived all the above
			String list_text=Jsoup.parse(list.html()).text(); 
			allListsContent.append(list_text+" ");
			
		}
		return allListsContent.toString();
	}

//	public String getTableWithWrapperText(String file) throws IOException{
//		
//		String pld= HTMLPages.getPLDFromHTMLPath(labelledEntitiesPath, file);
//		
//		String contentOfFile = DocPreprocessor.fileToText(file);
//		Document doc = Jsoup.parse(contentOfFile, "UTF-8");	
//		StringBuilder allTablesContent= new StringBuilder();
//		
//		if(pld.contains("ebay")){
//			Element firstTable = doc.getElementsByClass("itemAttr").first();
//			if(null!=firstTable){
//				Elements items = firstTable.select("tr");
//				for(Element item:items){
//					Elements values= item.select("td");
//					if(values.size()!= 4) continue;
//					//get only the values not the feature names
//					allTablesContent.append(values.get(1).text()+" ");
//					allTablesContent.append(values.get(3).text()+" ");						
//				}				
//			} 					
//			//detailed table
//			Element secondTable = doc.getElementsByClass("prodDetailSec").first();
//			if (null!=secondTable){
//				Elements detaileditems = secondTable.select("tr");
//				for(Element item:detaileditems){
//					Elements values = item.select("td");
//					if(values.size()!=2) continue;
//					else {
//						if (values.get(1).text().isEmpty() || values.get(1).text()==null ) continue;
//						allTablesContent.append(values.get(1).text()+" ");						
//					}
//				}
//			}						
//		}// end of ebay wrapper
//		else if (pld.contains("overstock")){
//			Element firstTable = doc.select("table[class=table table-dotted table-extended table-header translation-table]").first();
//			Element content = firstTable.select("tbody").first();
//			if(null!=content){
//				Elements items = content.select("tr");
//				for(Element item:items){
//					Elements values= item.select("td");
//					if(values.size()!= 2) continue;
//					allTablesContent.append(values.get(1).text()+" ");
//				}	
//				Element secondTable = doc.select("table[class=table table-dotted table-header]").first();
//				Elements seconditems = secondTable.select("tr");
//				if(null!=content){
//					for(Element item:seconditems){
//						Elements values= item.select("td");
//						if(values.size()!= 2) continue;		
//						allTablesContent.append(values.get(1).text()+" ");				
//					}
//					
//				} 
//			}
//		} 	//end of overstock parser	
//		else if (pld.contains("tesco")){
//			Element table = doc.select("div[id=product-spec-section-content]").first();
//			if(null!=table){
//
//				Elements labelCells = table.select("div[class=product-spec-cell product-spec-label]");
//				Elements labelValues = table.select("div[class=product-spec-cell product-spec-value]");
//				if (labelCells.size()!= labelValues.size()) System.out.println("The labels size and the value size do not match. Please check the Tesco wrapper.");
//				for(int i=0; i<labelCells.size(); i++){
//					String value= labelValues.get(i).text();
//					allTablesContent.append(value+" ");										
//				}					
//			}
//		}//end of tesco wrapper
//			
//		else if (pld.contains("alibaba")){
//
//			Elements labelCells = doc.select("td[class=name J-name]");
//			Elements labelValues = doc.select("td[class=value J-value]");
//			if (labelCells.size()!= labelValues.size()) System.out.println("The labels size and the value size do not match. Please check the Alibaba wrapper.");
//			for(int i=0; i<labelCells.size(); i++){
//				String value= labelValues.get(i).text();
//				allTablesContent.append(value+" ");
//				
//			}	
//			Elements secondlabelValues = doc.select("table[class=aliDataTable]").select("td");
//			for(int i=0; i<secondlabelValues.size(); i=i+2){
//				String value= secondlabelValues.get(i+1).text();
//				allTablesContent.append(value+" ");
//
//			}	
//		}
//		else {
//			System.out.println("No wrapper was defined for the pld:"+pld);
//			return null;
//			}
//		return allTablesContent.toString();
//	}



}

