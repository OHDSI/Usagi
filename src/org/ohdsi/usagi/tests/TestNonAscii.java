package org.ohdsi.usagi.tests;

import org.ohdsi.utilities.files.ReadTextFile;
import org.ohdsi.utilities.files.WriteTextFile;

public class TestNonAscii {
	
	public static String sourceFile = "c:/temp/nonascii.csv";
	public static String targetFile = "c:/temp/nonascii2.csv";

	public static void main(String[] args) {
		WriteTextFile out = new WriteTextFile(targetFile);
		for (String line : new ReadTextFile(sourceFile)){
			System.out.println(line);
			out.writeln(line);
		}
		out.close();
//		WriteCSVFile out = new WriteCSVFile(targetFile);
//		for (List<String> row : new ReadCSVFile(sourceFile))
//			out.write(row);
//		out.close();
	}

}
