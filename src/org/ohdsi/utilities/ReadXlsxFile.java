/*******************************************************************************
 * Copyright 2016 Observational Health Data Sciences and Informatics
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
package org.ohdsi.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadXlsxFile implements Iterable<List<String>> {
	private InputStream	inputstream;

	public ReadXlsxFile(String filename) {
		try {
			inputstream = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ReadXlsxFile(InputStream inputstream) {
		this.inputstream = inputstream;
	}

	@Override
	public Iterator<List<String>> iterator() {
		return new RowIterator();
	}

	public class RowIterator implements Iterator<List<String>> {

		private Iterator<org.apache.poi.ss.usermodel.Row>	iterator;
		private DecimalFormat								myFormatter	= new DecimalFormat("###############.################");

		public RowIterator() {
			try {
				XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
				XSSFSheet sheet = workbook.getSheetAt(0);
				iterator = sheet.iterator();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public List<String> next() {
			List<String> cells = new ArrayList<String>();
			for (Cell cell : iterator.next()) {
				String text;
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
					text = myFormatter.format(cell.getNumericCellValue());
				else
					text = cell.toString();
				cells.add(text);
			}
			return cells;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Remove not supported");
		}

	}
}
