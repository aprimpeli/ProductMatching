package wdc.Thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;

public class CorrectNormalizedNames {
	
	@Test
	public void correct() throws IOException{
		String file="";
		FileInputStream fis = new FileInputStream(file);
		Scanner sc = new Scanner(System.in);

		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		ArrayList<String> correctLines = new ArrayList<String>();
		String line = null;
		int numberLine=0;
		FileWriter fw = new FileWriter("resources/correctedLabelledEntities.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		
		while ((line = br.readLine()) != null) {
			numberLine++;
			System.out.println(numberLine);
			if(!line.contains("normalized")) continue;
			String existName=line.split("normalized_product_name\":\"")[1].split("\"")[0];
			String title = line.split("product-name\":")[1];
			System.out.println(title);
			System.out.println(existName);
			
			String read= sc.nextLine();
			if (read.equals("y")){
				correctLines.add(line);
				bw.append(line+",");
				bw.newLine();
				continue;
			} 
			else if(read.equals("n")) continue;
			else {
				String newline= line.replace("\"normalized_product_name\":\""+existName+"\"", "\"normalized_product_name\":\""+read+"\"");
				correctLines.add(newline);
				bw.append(newline+",");
				bw.newLine();
				//ySystem.out.println(newline);
			}

			
			
		}
	
		bw.close();
		br.close();
		sc.close();
	}
}
