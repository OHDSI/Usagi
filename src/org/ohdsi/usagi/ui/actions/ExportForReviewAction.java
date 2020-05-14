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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.CodeMapping.MappingStatus;
import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.utilities.files.Row;
import org.ohdsi.utilities.files.WriteCSVFileWithHeader;

public class ExportForReviewAction extends AbstractAction {

	private static final long	serialVersionUID	= -1846753187468184738L;

	public ExportForReviewAction() {
		putValue(Action.NAME, "Export for review");
		putValue(Action.SHORT_DESCRIPTION, "Export mapping to a human readable format for reviewing");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		boolean exportUnapproved = UsagiDialogs.askExportUnapprovedMappings();

		boolean hasApprovedMappings = false;
		for (CodeMapping mapping : Global.mapping) {
			if (mapping.mappingStatus == MappingStatus.APPROVED) {
				hasApprovedMappings = true;
				break;
			}
		}

		if (!exportUnapproved && !hasApprovedMappings) {
			UsagiDialogs.warningNothingToExport();
			return;
		}

		JFileChooser fileChooser = new JFileChooser(Global.folder);
		FileFilter csvFilter = new FileNameExtensionFilter("CSV files", "csv");
		fileChooser.setFileFilter(csvFilter);
		if (fileChooser.showSaveDialog(Global.frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			Global.folder = file.getParentFile().getAbsolutePath();
			if (!file.getName().toLowerCase().endsWith(".csv"))
				file = new File(file.getAbsolutePath() + ".csv");
			Global.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			WriteCSVFileWithHeader out = new WriteCSVFileWithHeader(file.getAbsolutePath());
			for (CodeMapping mapping : Global.mapping)
				if (exportUnapproved || mapping.mappingStatus == MappingStatus.APPROVED) {
					List<Concept> targetConcepts;
					if (mapping.targetConcepts.size() == 0) {
						targetConcepts = new ArrayList<Concept>(1);
						targetConcepts.add(Concept.EMPTY_CONCEPT);
					} else
						targetConcepts = mapping.targetConcepts;

					for (Concept targetConcept : targetConcepts) {
						Row row = mapping.sourceCode.toRow();
						row.add("matchScore", mapping.matchScore);
						if (exportUnapproved) row.add("mappingStatus", mapping.mappingStatus.toString());
						row.add("targetConceptId", targetConcept.conceptId);
						row.add("targetConceptName", targetConcept.conceptName);
						row.add("targetVocabularyId", targetConcept.vocabularyId);
						row.add("targetDomainId", targetConcept.domainId);
						row.add("targetStandardConcept", targetConcept.standardConcept);
						row.add("targetChildCount", targetConcept.childCount);
						row.add("targetParentCount", targetConcept.parentCount);
						row.add("targetConceptClassId", targetConcept.conceptClassId);
						row.add("targetConceptCode", targetConcept.conceptCode);
						row.add("targetValidStartDate", targetConcept.validStartDate);
						row.add("targetValidEndDate", targetConcept.validEndDate);
						row.add("targetInvalidReason", targetConcept.invalidReason);
						out.write(row);
					}
				}
			out.close();
			Global.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

}
