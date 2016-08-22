package ErrorAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReformFiles {

	String fpFile;
	String fnFile;
	
	public static void main (String args[]) throws IOException{
		ReformFiles r= new ReformFiles();
		r.reformFalsePositivesAnalysis();
	}
	
	public void reformFalsePositivesAnalysis() throws IOException{
		
		BufferedReader reader = null;
	    File file = new File(fpFile);
	    reader = new BufferedReader(new FileReader(file));
	    
	    File newfile = new File("\\REFORMED.csv");
		FileWriter fw = new FileWriter(newfile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

	    String line;
	    System.out.println("START");
	    while ((line = reader.readLine()) != null) {
	    	String productcategory=line.split(";")[0];
	    	String inputType=line.split(";")[1];
	    	String nodeID=line.split(";")[2].split("-")[0];
	    	String []falses=line.split(";")[3].replace("[","").replace("]","").split(",");
	    	for (int i=0;i<falses.length;i++){
	    		bw.append(productcategory.trim()+";"+inputType.trim()+";"+nodeID.trim()+";"+falses[i].trim());
	    		bw.newLine();
	    	}
	    }
	    bw.flush();
	    bw.close();
	    reader.close();
	}
}
