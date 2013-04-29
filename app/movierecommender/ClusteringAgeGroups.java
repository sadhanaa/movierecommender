package movierecommender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/* 
 *  Order for execution of these functions is
 *  1) udata()
 *  2) ageGenderClassification
 *  
 * */

public class ClusteringAgeGroups {

	
	private static long userCount=0;
	
	public static void main(String[] args) {
		try {
			Long l=System.currentTimeMillis();
			uDataFile("a","b");
			ageGenderClassification("abc","abc");
			System.out.println("Time spent -> "+(System.currentTimeMillis()-l)/1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	

	/*
	 *  This function classifies the input file
	 *  You need to pass the input file and the destination directory for the output file 
	 *  Different files would be created with respective to the data 
	 *  ( Ex. E:/DataMining/DataParser/src/DataSets/clusterM28-38.csv )
	 *  
	 */
	
	public static void ageGenderClassification(String inf,String opf) throws IOException{
		// TODO Auto-generated method stub\ 
		BufferedReader reader = null,fullFileReader=null;
		
		// Remove this when you are using the inf and opf variables
		
		String inputFile="G:\\sadhana\\workspace\\movierecommender\\app\\movierecommender\\u.user";
		//String inputFile=inf;
		String outputFile="G:\\sadhana\\workspace\\movierecommender\\app\\movierecommender\\datasets";
		//String outputFile=opf;
		
		try {
			reader = new BufferedReader(new FileReader(inputFile));
			
			//outputFile
			String line = null;
			String lineSplit[]=new String[5];
			while ((line = reader.readLine()) != null) {
				userCount++;
				line=line.replaceAll("\\|",",");
				//System.out.println(line);
				lineSplit=line.split(",");
				String ageRange="7699";
				int age=Integer.parseInt(lineSplit[1]);
		//		System.out.println(age);
				if (0 <= age && age <= 12)
					ageRange="0012";
				else if (13 <= age && age <= 17)
					ageRange="1317";
				else if (18 <= age && age <= 27)
					ageRange="1827";
				else if (28 <= age && age <= 38)
					ageRange="2838";
				else if (39 <= age && age <= 55)	
					ageRange="3955";
				else if (56 <= age && age <= 75)
					ageRange="5675";
				else
					ageRange="7699";
				String fileName=outputFile+"cluster"+lineSplit[2]+ageRange+".csv";
				String data=lineSplit[0];
				
				findNAppend(fileName,data);
			}
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			
		}	
	}
	
	
	/*
	 * This functions finds the match between the userif from the age-gender file (u.user)   
	 * and Joins him with the main user file (u.data)
	 * fullFile file is the hard coded path for the csv which is created from uData function 
	 * 
	 */
	public static void findNAppend(String fileName, String data) throws IOException{
		PrintWriter writer = null;
		String fullFile="G:\\sadhana\\workspace\\movierecommender\\app\\movierecommender\\u.csv";
		BufferedReader br= new BufferedReader(new FileReader(fullFile));
	//	System.out.println(data);
	//	System.out.println(fileName);
		String str=br.readLine();
		while (str!=null){
		//	System.out.println(str.split(",")[0].trim());
			if (Integer.parseInt(str.split(",")[0])==Integer.parseInt(data)){
				
		//		System.out.println("Hello ==>"+str.split(",")[0].trim());
				writer=new PrintWriter(new BufferedWriter(new FileWriter(fileName,true)));
				writer.println(str);
				writer.flush();
			}
			str=br.readLine();
		}
	}
	
	
	/*
	 * This function converts the u.data file which contains the user id - item -id - rating - timestamp  
	 * to csv file.
	 * If you pass the parameters using inf and opf 
	 * inf = full path of the original u.data file  
	 * opf = full path of the csv files created by this functions 
	 */
	
	public static void uDataFile(String inf,String opf) throws IOException{
	// TODO Auto-generated method stub\ 
	BufferedReader reader = null;
	PrintWriter writer = null;
	// Remove this when you are using the inf and opf variables 
	String inputFile="G:\\sadhana\\workspace\\movierecommender\\app\\movierecommender\\u.data";
	//String inputFile=inf;
	String outputFile="G:\\sadhana\\workspace\\movierecommender\\app\\movierecommender\\datasets\\u.csv";
	//String outputFile=opf;
	
	try {
		reader = new BufferedReader(new FileReader(inputFile));
		writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile,true)));
		int i=0;
		String line = null;
		String lineSplit[]=new String[4];
		while ((line = reader.readLine()) != null) {
			line=line.replaceAll("\\s",",");
			lineSplit=line.split(",");
			//if (i<9){
			//System.out.print(lineSplit[0]+",");
			//System.out.print(lineSplit[1]+",");
			//System.out.println(lineSplit[2]);
			//}
			writer.write(lineSplit[0]);
			writer.write(",");
			writer.write(lineSplit[1]);
			writer.write(",");
			writer.write(lineSplit[2]);
			writer.write("\n");
		}
	}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			reader.close();
			writer.close();
		}
	}
}

