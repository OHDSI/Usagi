/*******************************************************************************
 * Copyright 2017 Observational Health Data Sciences and Informatics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohdsi.utilities.files;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

public class WriteCSVFile {

	private char	delimiter	= ',';
	private String	charSet		= "ISO-8859-1";

	public WriteCSVFile(String filename, boolean append) {
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(filename, append);
			bufferedWrite = new BufferedWriter(new OutputStreamWriter(stream, charSet), 10000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public WriteCSVFile(String filename, String charSet, boolean append) {
		this.charSet = charSet;
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(filename, append);
			bufferedWrite = new BufferedWriter(new OutputStreamWriter(stream, charSet), 10000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public WriteCSVFile(String filename, String charSet) {
		this(filename, charSet, false);
	}
	
	public WriteCSVFile(String filename) {
		this(filename, false);
	}

	public WriteCSVFile(String filename, char delimiter) {
		this(filename);
		this.delimiter = delimiter;
	}

	public WriteCSVFile(String filename, char delimiter, String charSet) {
		this(filename, charSet);
		this.delimiter = delimiter;
	}

	public void write(List<String> string) {
		try {
			bufferedWrite.write(columns2line(string));
			bufferedWrite.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String columns2line(List<String> columns) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> iterator = columns.iterator();
		while (iterator.hasNext()) {
			String column = iterator.next();
			boolean hasQuotes = column.contains("\"");
			column = column.replaceAll("\\\\", "\\\\\\\\");
			if (hasQuotes)
				column = column.replaceAll("\"", "\\\\\"");
			column = column.replaceAll("\r", "");
			column = column.replaceAll("\n", "\\\\n");
			if (hasQuotes || column.contains(Character.toString(delimiter)))
				column = "\"" + column + "\"";
			sb.append(column);
			if (iterator.hasNext())
				sb.append(delimiter);
		}
		return sb.toString();
	}

	public void flush() {
		try {
			bufferedWrite.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			bufferedWrite.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedWriter	bufferedWrite;
}
