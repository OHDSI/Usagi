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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.CodeMapping.MappingStatus;
import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.MappingTarget;
import org.ohdsi.utilities.files.Row;
import org.ohdsi.utilities.files.WriteCSVFileWithHeader;

public class ExportSourceToConceptMapDialog extends JDialog {

	private JTextField			sourceVocabularyIdField;
	private static final long	serialVersionUID	= -6846083121849826818L;
	private boolean exportUnapproved = false;

	public ExportSourceToConceptMapDialog() {
		setTitle("Export to source_to_concept_map");
		setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.ipadx = 5;
		g.ipady = 5;

		g.gridx = 0;
		g.gridy = 0;
		add(new JLabel("Source vocabulary id:"), g);

		g.gridx = 1;
		g.gridy = 0;
		sourceVocabularyIdField = new JTextFieldLimit(20);
		sourceVocabularyIdField.setToolTipText("Pick an alphanumeric string to identify the source vocabulary (max 20 characters)");
		sourceVocabularyIdField.setPreferredSize(new Dimension(100, 10));
		add(sourceVocabularyIdField, g);

		g.gridx = 0;
		g.gridy = 2;
		g.gridwidth = 2;
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Cancel the export");
		cancelButton.addActionListener(event -> setVisible(false));
		buttonPanel.add(cancelButton);
		JButton exportButton = new JButton("Export");
		exportButton.setToolTipText("Select the filename and export using these settings");
		exportButton.addActionListener(event -> export());
		buttonPanel.add(exportButton);
		add(buttonPanel, g);

		pack();
		setModal(true);
		setLocationRelativeTo(Global.frame);
	}

	public void setExportUnapproved(boolean exportUnapproved) {
		this.exportUnapproved = exportUnapproved;
	}

	private void export() {
		JFileChooser fileChooser = new JFileChooser(Global.folder);
		FileFilter csvFilter = new FileNameExtensionFilter("CSV files", "csv");
		fileChooser.setFileFilter(csvFilter);
		fileChooser.setDialogTitle("Export");
		if (fileChooser.showSaveDialog(Global.frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			Global.folder = file.getParentFile().getAbsolutePath();
			if (!file.getName().toLowerCase().endsWith(".csv"))
				file = new File(file.getAbsolutePath() + ".csv");

			writeToCsvFile(file.getAbsolutePath());
			setVisible(false);
		}
	}

	private void writeToCsvFile(String filename) {
		WriteCSVFileWithHeader out = new WriteCSVFileWithHeader(filename);
		for (CodeMapping mapping : Global.mapping)
			if (exportUnapproved || mapping.getMappingStatus() == MappingStatus.APPROVED) {
				List<Concept> targetConcepts;
				if (mapping.getTargetConcepts().size() == 0) {
					targetConcepts = new ArrayList<Concept>(1);
					targetConcepts.add(Concept.EMPTY_CONCEPT);
				} else {
					targetConcepts = mapping.getTargetConcepts().stream().map(MappingTarget::getConcept).collect(Collectors.toList());
				}

				for (Concept targetConcept : targetConcepts) {
					Row row = new Row();
					row.add("source_code", mapping.getSourceCode().sourceCode);
					row.add("source_concept_id", "0");
					row.add("source_vocabulary_id", sourceVocabularyIdField.getText());
					row.add("source_code_description", mapping.getSourceCode().sourceName);
					row.add("target_concept_id", targetConcept.conceptId);
					row.add("target_vocabulary_id", targetConcept.conceptId == 0 ? "None" : targetConcept.vocabularyId );
					row.add("valid_start_date", "1970-01-01");
					row.add("valid_end_date", "2099-12-31");
					row.add("invalid_reason", "");
					out.write(row);
				}
			}
		out.close();
	}
}
