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
package org.ohdsi.usagi.ui;

import java.util.ArrayList;
import java.util.List;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.ReadCodeMappingsFromFile;
import org.ohdsi.usagi.SourceCode;
import org.ohdsi.usagi.WriteCodeMappingsToFile;
import org.ohdsi.usagi.ui.DataChangeListener.DataChangeEvent;

public class Mapping extends ArrayList<CodeMapping> {
	private static final long			serialVersionUID	= -8560539820505747600L;
	private List<DataChangeListener>	listeners			= new ArrayList<DataChangeListener>();

	public void loadFromFile(String filename) {
		clear();
		for (CodeMapping codeMapping : new ReadCodeMappingsFromFile(filename))
			add(codeMapping);
		fireDataChanged(DataChangeListener.RESTRUCTURE_EVENT);
	}

	public Mapping() {
		super();
	}

	public void addListener(DataChangeListener listener) {
		listeners.add(listener);
	}

	public void fireDataChanged(DataChangeEvent event) {
		for (DataChangeListener listener : listeners)
			listener.dataChanged(event);
	}

	public void saveToFile(String filename) {
		WriteCodeMappingsToFile out = new WriteCodeMappingsToFile(filename);
		for (CodeMapping codeMapping : this)
			out.write(codeMapping);
		out.close();
	}

	public List<SourceCode> getSourceCodes() {
		List<SourceCode> sourceCodes = new ArrayList<SourceCode>(size());
		for (CodeMapping codeMapping : this)
			sourceCodes.add(codeMapping.sourceCode);
		return sourceCodes;
	}
}
