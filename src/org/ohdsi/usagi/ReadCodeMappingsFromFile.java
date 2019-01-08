/*******************************************************************************
 * Copyright 2018 Observational Health Data Sciences and Informatics
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
package org.ohdsi.usagi;

import java.util.Iterator;

import org.ohdsi.usagi.CodeMapping.MappingStatus;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.utilities.files.ReadCSVFileWithHeader;
import org.ohdsi.utilities.files.Row;

public class ReadCodeMappingsFromFile implements Iterable<CodeMapping> {
	private String filename;

	public ReadCodeMappingsFromFile(String filename) {
		this.filename = filename;
	}

	@Override
	public Iterator<CodeMapping> iterator() {
		return new RowIterator();
	}

	public class RowIterator implements Iterator<CodeMapping> {

		private Iterator<Row>	iterator;
		private CodeMapping		buffer;
		private Row				row;

		public RowIterator() {
			iterator = new ReadCSVFileWithHeader(filename).iterator();
			if (iterator.hasNext()) {
				row = iterator.next();
				readNext();
			} else
				buffer = null;
		}

		private void readNext() {
			if (row == null)
				buffer = null;
			else {
				buffer = new CodeMapping(new SourceCode(row));
				buffer.matchScore = row.getDouble("matchScore");
				buffer.mappingStatus = MappingStatus.valueOf(row.get("mappingStatus"));
				try {
					buffer.comment = row.get("comment");
				} catch (Exception e) {
					buffer.comment = "";
				}
				while (row != null && new SourceCode(row).sourceCode.equals(buffer.sourceCode.sourceCode)
						&& new SourceCode(row).sourceName.equals(buffer.sourceCode.sourceName)) {
					if (row.getInt("conceptId") != 0) {
						Concept concept = Global.dbEngine.getConcept(row.getInt("conceptId"));
						if (concept == null) {
							buffer.mappingStatus = CodeMapping.MappingStatus.INVALID_TARGET;
						} else {
							buffer.targetConcepts.add(concept);
						}
					}
					if (iterator.hasNext())
						row = iterator.next();
					else
						row = null;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return buffer != null;
		}

		@Override
		public CodeMapping next() {
			CodeMapping next = buffer;
			readNext();
			return next;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Remove not supported");
		}

	}

}
