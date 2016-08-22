package Utils;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import BagOfWordsModel.DocPreprocessor;

public class LabelledFileExtractions {

	public static NodeFromLabelled extractNodeFromLabelledFile (String htmlPath, String labelled) throws IOException{
		

		NodeFromLabelled node= new NodeFromLabelled();
		String splitChar;
		if(htmlPath.contains("\\")){
			splitChar= "\\\\";
		}
		else{
			splitChar= "/";

		}
		String htmlfrag[]= htmlPath.split(splitChar);

		String htmlName = htmlfrag[htmlfrag.length-1];
		String nodeID=htmlName.replace(".html", "");
		String title="";
		String description="";
						
		JSONArray labelledEntities = new JSONArray(DocPreprocessor.fileToText(labelled));
			
		for(int i = 0 ; i < labelledEntities.length() ; i++){
			
			JSONObject entity = labelledEntities.getJSONObject(i);
			String nodeIDLabelles= entity.getString("id_self");
			if(nodeID.equals(nodeIDLabelles)){
				title=entity.getString("product-name");
				description=entity.getString("product-description");
			}
		}
		  
		  if (nodeID.equals("")) {
			  System.out.println("Something went wrong. The node ID could not be retrieved from the page: "+htmlName);
		  }
		  node.setNodeID(nodeID);
		  node.setTitle(title);
		  node.setDescription(description);

		  return node;
	}
}
