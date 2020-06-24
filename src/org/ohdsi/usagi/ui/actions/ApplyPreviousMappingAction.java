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
package org.ohdsi.usagi.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.usagi.ui.Mapping;

import static org.ohdsi.usagi.ui.DataChangeEvent.*;

public class ApplyPreviousMappingAction extends AbstractAction {

	private static final long serialVersionUID = 3420357922150237898L;

	public ApplyPreviousMappingAction() {
		putValue(Action.NAME, "Apply previous mapping");
		putValue(Action.SHORT_DESCRIPTION, "Apply previous mapping to current code set");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser fileChooser = new JFileChooser(Global.folder);
		FileFilter csvFilter = new FileNameExtensionFilter("CSV files", "csv");
		fileChooser.setFileFilter(csvFilter);
		if (fileChooser.showOpenDialog(Global.frame) == JFileChooser.APPROVE_OPTION) {
			int mappingsApplied = 0;
			int mappingsAdded = 0;

			// Existing code lookup
			Map<String, CodeMapping> codeToMapping = new HashMap<>();
			for (CodeMapping codeMapping: Global.mapping) {
				codeToMapping.put(codeMapping.sourceCode.sourceCode, codeMapping);
			}

			// Open mapping file to be applied
			File file = fileChooser.getSelectedFile();
			Mapping mappingToBeApplied = new Mapping();
			mappingToBeApplied.loadFromFile(file.getAbsolutePath());

			// Apply mapping. Add mappings not currently present
			for (CodeMapping codeMappingToBeApplied : mappingToBeApplied) {
				CodeMapping existingMapping = codeToMapping.get(codeMappingToBeApplied.sourceCode.sourceCode);
				if (existingMapping != null) {
					existingMapping.sourceCode.sourceName = codeMappingToBeApplied.sourceCode.sourceName;
					existingMapping.targetConcepts = codeMappingToBeApplied.targetConcepts;
					existingMapping.mappingStatus = codeMappingToBeApplied.mappingStatus;
					existingMapping.comment = codeMappingToBeApplied.comment;
					mappingsApplied++;
				} else {
					Global.mapping.add(codeMappingToBeApplied);
					mappingsAdded++;
				}
			}

			String message = "The applied mapping contained " + mappingToBeApplied.size() + " mappings of which " + mappingsApplied
					+ " were applied to the current mapping and " + mappingsAdded + " were newly added.";
			Global.mappingTablePanel.updateUI();
			Global.mappingDetailPanel.updateUI();
			Global.mapping.fireDataChanged(APPROVE_EVENT); // To update the footer
			if (mappingsAdded > 0) {
				Global.usagiSearchEngine.close();
				Global.usagiSearchEngine.createDerivedIndex(Global.mapping.getSourceCodes(), Global.frame);
				Global.mappingDetailPanel.doSearch();
			}
			JOptionPane.showMessageDialog(Global.frame, message);
		}
	}
}
