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
package org.ohdsi.usagi.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.usagi.indexBuilding.BuildIndex;

public class RebuildIndexDialog extends JDialog {

	private static final long	serialVersionUID	= 2560460762547493210L;
	private JTextField			vocabFolderField;
	private JTextField			loincFileField;
	private JCheckBox			loincCheckBox;
	private JButton				loincPickButton;

	public RebuildIndexDialog() {
		setTitle("Rebuild index");
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		add(createVocabFolderPanel());
		add(createLoincFilePanel());
		add(createButtonsPanel());

		pack();
		setModal(true);
		setLocationRelativeTo(Global.frame);
	}

	private void buildIndex() {
		String vocabFolder = vocabFolderField.getText();
		String loincFile = loincFileField.getText();
		if (!(new File(vocabFolder + "/concept.csv").exists())) {
			JOptionPane.showMessageDialog(this, "Vocabulary file concept.csv not found", "Cannot build index", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!(new File(vocabFolder + "/concept_synonym.csv").exists())) {
			JOptionPane.showMessageDialog(this, "Vocabulary file concept_synonym.csv not found", "Cannot build index", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (loincCheckBox.isSelected() && !(new File(loincFile).exists())) {
			JOptionPane.showMessageDialog(this, "LOINC file loinc.csv not found", "Cannot build index", JOptionPane.ERROR_MESSAGE);
			return;
		}
		setVisible(false);
		Global.usagiSearchEngine.close();
		BuildIndex buildIndex = new BuildIndex();
		buildIndex.buildIndex(vocabFolder, loincCheckBox.isSelected() ? loincFile : null);
	}

	private JPanel createVocabFolderPanel() {
		JPanel folderPanel = new JPanel();
		folderPanel.setLayout(new BoxLayout(folderPanel, BoxLayout.X_AXIS));
		folderPanel.setBorder(BorderFactory.createTitledBorder("Vocabulary location"));
		vocabFolderField = new JTextField();
		vocabFolderField.setText(Global.folder);
		vocabFolderField.setToolTipText("The folder where the Vocabulary csv files can be found");
		folderPanel.add(vocabFolderField);
		JButton pickButton = new JButton("Pick folder");
		pickButton.setToolTipText("Pick the location of the Vocabulary csv files");
		folderPanel.add(pickButton);
		pickButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pickVocabFolder();
			}
		});
		return folderPanel;
	}

	private JPanel createLoincFilePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("LOINC location"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.X_AXIS));
		loincCheckBox = new JCheckBox("Add additional LOINC information to index");
		loincCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxClicked();

			}
		});
		checkboxPanel.add(loincCheckBox);
		checkboxPanel.add(Box.createHorizontalGlue());
		panel.add(checkboxPanel);

		JPanel filePanel = new JPanel();
		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
		loincFileField = new JTextField();
		loincFileField.setText((new File(Global.folder + "/loinc.csv").getAbsolutePath()));
		loincFileField.setToolTipText("The location of the LOINC csv file");
		loincFileField.setEnabled(false);
		filePanel.add(loincFileField);
		loincPickButton = new JButton("Pick file");
		loincPickButton.setToolTipText("Pick the location of the LOINC csv file");
		loincPickButton.setEnabled(false);
		filePanel.add(loincPickButton);
		loincPickButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pickLoincFile();
			}
		});
		panel.add(filePanel);
		return panel;
	}

	private void checkBoxClicked() {
		loincFileField.setEnabled(loincCheckBox.isSelected());
		loincPickButton.setEnabled(loincCheckBox.isSelected());
	}

	private JPanel createButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		panel.add(cancelButton);

		JButton importButton = new JButton("Build index");
		importButton.setBackground(new Color(151, 220, 141));
		importButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				buildIndex();
			}
		});
		panel.add(importButton);
		return panel;
	}

	private void pickVocabFolder() {
		JFileChooser fileChooser = new JFileChooser(new File(vocabFolderField.getText()));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showDialog(this, "Select folder");
		if (returnVal == JFileChooser.APPROVE_OPTION)
			vocabFolderField.setText(fileChooser.getSelectedFile().getAbsolutePath());
	}

	private void pickLoincFile() {
		JFileChooser fileChooser = new JFileChooser(new File(loincFileField.getText()).getParent());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter loincFilter = new FileNameExtensionFilter("CSV files", "csv");
		fileChooser.setFileFilter(loincFilter);
		int returnVal = fileChooser.showDialog(this, "Select file");
		if (returnVal == JFileChooser.APPROVE_OPTION)
			loincFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
	}
}
