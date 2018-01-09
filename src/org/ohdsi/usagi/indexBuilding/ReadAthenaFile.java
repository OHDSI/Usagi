package org.ohdsi.usagi.indexBuilding;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ohdsi.utilities.files.Row;

public class ReadAthenaFile implements Iterable<Row> {
	public String				filename;
	protected BufferedReader	bufferedReader;
	public boolean				EOF	= false;

	public ReadAthenaFile(String filename) {
		this.filename = filename;
		try {
			FileInputStream inputStream = new FileInputStream(filename);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.err.println("Computer does not support ISO-8859-1 encoding");
			e.printStackTrace();
		}
	}

	public Iterator<Row> getIterator() {
		return iterator();
	}

	private class AthenaFileIterator implements Iterator<Row> {
		private List<String>				buffer;
		private HashMap<String, Integer>	fieldName2ColumnIndex;

		public AthenaFileIterator() {
			readNext();
			fieldName2ColumnIndex = new HashMap<String, Integer>();
			for (int i = 0; i < buffer.size(); i++)
				fieldName2ColumnIndex.put(buffer.get(i).toLowerCase(), i);
			if (!EOF)
				readNext();
		}

		private void readNext() {
			try {
				String line = bufferedReader.readLine();
				if (line == null) {
					EOF = true;
					bufferedReader.close();
				} else {
					buffer = Arrays.asList(line.split("\t"));
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public boolean hasNext() {
			return !EOF;
		}

		public Row next() {
			Row result = new Row(buffer, fieldName2ColumnIndex);
			readNext();
			return result;
		}

		public void remove() {
			// not implemented
		}

	}

	public Iterator<Row> iterator() {
		return new AthenaFileIterator();
	}
}
