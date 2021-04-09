/*******************************************************************************
 * Copyright 2019 Observational Health Data Sciences and Informatics
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
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.ReadCodeMappingsFromFile;
import org.ohdsi.usagi.SourceCode;
import org.ohdsi.usagi.WriteCodeMappingsToFile;
import org.ohdsi.utilities.collections.Pair;

import static org.ohdsi.usagi.ui.DataChangeEvent.*;

public class Mapping extends ArrayList<CodeMapping> {
	private static final long			serialVersionUID	= -8560539820505747600L;
	private List<DataChangeListener>	listeners			= new ArrayList<>();

	public void loadFromFile(String filename) {
		clear();
		int nInvalidTargets = 0;
		try {
			for (CodeMapping codeMapping : new ReadCodeMappingsFromFile(filename)) {
				add(codeMapping);
				if (codeMapping.getMappingStatus() == CodeMapping.MappingStatus.INVALID_TARGET) {
					nInvalidTargets += 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(
					Global.frame,
					"Invalid File Format: '" + e.getMessage() + "'",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
		}

		if (nInvalidTargets > 0) {
			JOptionPane.showMessageDialog(
					null,
					nInvalidTargets + " illegal target concepts found. The corresponding source codes are marked in red.",
					"Illegal target concepts",
					JOptionPane.WARNING_MESSAGE
			);
		}
		fireDataChanged(RESTRUCTURE_EVENT);
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
		return this.stream()
				.map(CodeMapping::getSourceCode)
				.collect(Collectors.toList());
	}

	public List<String> getAdditionalColumnNames() {
		CodeMapping codeMapping = Global.mapping.get(0);
		return codeMapping.getSourceCode().sourceAdditionalInfo.stream()
				.map(Pair::getItem1)
				.collect(Collectors.toList());
	}
}
