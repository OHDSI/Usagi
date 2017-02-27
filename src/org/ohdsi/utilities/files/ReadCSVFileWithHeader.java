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

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ReadCSVFileWithHeader implements Iterable<Row> {

	private CSVParser parser;

	public ReadCSVFileWithHeader(String filename) {
		this(filename, CSVFormat.DEFAULT);
	}

	public ReadCSVFileWithHeader(String filename, CSVFormat format) {
		try {
			parser = new CSVParser(new FileReader(filename), format);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Iterator<Row> iterator() {
		return new RowIterator();
	}

	public class RowIterator implements Iterator<Row> {

		private Iterator<CSVRecord>		iterator;
		private Map<String, Integer>	fieldName2ColumnIndex;

		public RowIterator() {
			iterator = parser.iterator();
			CSVRecord record = iterator.next();
			fieldName2ColumnIndex = new HashMap<String, Integer>();
			for (String header : record)
				fieldName2ColumnIndex.put(header, fieldName2ColumnIndex.size());
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Row next() {
			CSVRecord record = iterator.next();
			List<String> row = new ArrayList<String>(record.size());
			for (int i = 0; i < record.size(); i++)
				row.add(record.get(i));
			return new Row(row, fieldName2ColumnIndex);
		}

		@Override
		public void remove() {
			throw new RuntimeException("Remove not supported");
		}
	}
}
