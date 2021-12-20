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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ForkJoinPool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.CodeMapping.MappingStatus;
import org.ohdsi.usagi.MappingTarget;
import org.ohdsi.usagi.SourceCode;
import org.ohdsi.usagi.UsagiSearchEngine.ScoredConcept;
import org.ohdsi.utilities.ReadXlsxFile;
import org.ohdsi.utilities.StringUtilities;
import org.ohdsi.utilities.collections.Pair;
import org.ohdsi.utilities.files.ReadCSVFile;

import static org.ohdsi.usagi.ui.DataChangeEvent.*;

public class ImportDialog extends JDialog {

	private static final long		serialVersionUID		= 8119661833870381094L;
	private static String			CONCEPT_IDS				= "Auto concept ID column";
	private static String			ATC						= "ATC column";
	private List<String>			columnNames				= new ArrayList<String>();
	private String[]				comboBoxOptions;
	private List<List<String>>		data					= new ArrayList<List<String>>();
	private FilterPanel				filterPanel;
	private JPanel					columnMappingPanel;
	private JScrollPane				columnMappingScrollPane;
	private JComboBox<String>		conceptIdsOrAtc;
	private JComboBox<String>		sourceCodeColumn;
	private JComboBox<String>		sourceNameColumn;
	private JComboBox<String>		sourceFrequencyColumn;
	private JComboBox<String>		autoConceptIdColumn;
	private List<JComboBox<String>>	additionalInfoColumns	= new ArrayList<JComboBox<String>>();
	private int						gridY;

	public ImportDialog(String filename) {
		setTitle("Import codes from " + new File(filename).getName());
		setLayout(new BorderLayout());

		try {
			loadData(filename);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error loading file", JOptionPane.ERROR_MESSAGE);
			return;
		}
		add(createTablePanel(), BorderLayout.CENTER);
		add(createOptionsPanel(), BorderLayout.SOUTH);
		setModal(true);
		setSize(1200, 600);
		setLocationRelativeTo(Global.frame);
		setVisible(true);
	}

	private void loadData(String filename) {
		Iterator<List<String>> iterator;
		if (filename.toLowerCase().endsWith(".xlsx"))
			iterator = new ReadXlsxFile(filename).iterator();
		else
			iterator = new ReadCSVFile(filename).iterator();
		if (!iterator.hasNext())
			throw new RuntimeException("File contains no data");
		columnNames = iterator.next();
		Set<String> uniqueNames = new HashSet<>();
		for (String columnName : columnNames) {
			if (columnName.isEmpty()) {
				continue;
			}
			if (!uniqueNames.add(columnName)) {
				throw new RuntimeException("Found duplicate column name '" + columnName + "', duplicates are not allowed.");
			}
		}
		comboBoxOptions = new String[columnNames.size() + 1];
		comboBoxOptions[0] = "";
		for (int i = 0; i < columnNames.size(); i++)
			comboBoxOptions[i + 1] = columnNames.get(i);

		while (iterator.hasNext()) {
			List<String> row = iterator.next();
			for (int i = row.size(); i < columnNames.size(); i++)
				row.add("");
			data.add(row);
		}
	}

	private Component createTablePanel() {
		TableModelWrapper tableModel = new TableModelWrapper();
		JTable table = new JTable(tableModel);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setDefaultRenderer(String.class, new UsagiCellRenderer());
		JScrollPane scrollPane = new JScrollPane(table);
		return scrollPane;
	}

	private Component createOptionsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		panel.add(createColumnMappingPanel(), c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		filterPanel = new FilterPanel();
		panel.add(filterPanel, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		panel.add(createButtonsPanel(), c);

		return panel;
	}

	private Component createButtonsPanel() {
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

		JButton importButton = new JButton("Import");
		importButton.setBackground(new Color(151, 220, 141));
		importButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				importData();
			}
		});
		panel.add(importButton);
		return panel;
	}

	private Component createColumnMappingPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Column mapping"));
		panel.setLayout(new BorderLayout());

		columnMappingPanel = new JPanel();
		columnMappingPanel.setLayout(new GridBagLayout());

		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.fill = GridBagConstraints.BOTH;
		cLabel.gridx = 0;
		cLabel.gridy = 1;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.weightx = 1;

		GridBagConstraints cBox = new GridBagConstraints();
		cBox.fill = GridBagConstraints.BOTH;
		cBox.gridx = 1;
		cBox.gridy = 1;
		cBox.anchor = GridBagConstraints.EAST;
		cBox.weightx = 0.1;

		columnMappingPanel.add(new JLabel("Source code column"), cLabel);
		sourceCodeColumn = new JComboBox<String>(comboBoxOptions);
		sourceCodeColumn.setToolTipText("The column containing the source code");
		columnMappingPanel.add(sourceCodeColumn, cBox);

		cLabel.gridy++;
		cBox.gridy++;
		columnMappingPanel.add(new JLabel("Source name column"), cLabel);
		sourceNameColumn = new JComboBox<>(comboBoxOptions);
		sourceNameColumn.setToolTipText("The column containing the name or description of the source code, which will be used for matching");
		columnMappingPanel.add(sourceNameColumn, cBox);

		cLabel.gridy++;
		cBox.gridy++;
		columnMappingPanel.add(new JLabel("Source frequency column"), cLabel);
		sourceFrequencyColumn = new JComboBox<>(comboBoxOptions);
		sourceFrequencyColumn.setToolTipText("The column containing the frequency of the code in the source database");
		columnMappingPanel.add(sourceFrequencyColumn, cBox);

		cLabel.gridy++;
		cBox.gridy++;
		conceptIdsOrAtc = new JComboBox<>(new String[] { CONCEPT_IDS, ATC });
		columnMappingPanel.add(conceptIdsOrAtc, cLabel);
		autoConceptIdColumn = new JComboBox<>(comboBoxOptions);
		autoConceptIdColumn.setToolTipText("The column containing a (semicolon-delimited) list of concept IDs to which the search will be restricted");
		columnMappingPanel.add(autoConceptIdColumn, cBox);
		autoConceptIdColumn.addActionListener(e -> {
			if (!autoConceptIdColumn.getSelectedItem().toString().equals("")) {
				filterPanel.setFilterByAuto();
			}
		});

		gridY = cLabel.gridy + 1;
		addExtraColumnMapping();

		columnMappingScrollPane = new JScrollPane(columnMappingPanel);
		columnMappingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		columnMappingScrollPane.setBorder(BorderFactory.createEmptyBorder());
		panel.add(columnMappingScrollPane, BorderLayout.CENTER);
		return panel;
	}

	private void addExtraColumnMapping() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = gridY;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		columnMappingPanel.add(new JLabel("Additional info column"), c);
		c.gridx = 1;
		c.gridy = gridY;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0.1;
		JComboBox<String> additionalInfoColumn = new JComboBox<String>(comboBoxOptions);
		additionalInfoColumn.setToolTipText("A column containing additional information");
		columnMappingPanel.add(additionalInfoColumn, c);
		additionalInfoColumns.add(additionalInfoColumn);
		additionalInfoColumn.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!((JComboBox<String>) arg0.getSource()).getSelectedItem().toString().equals(""))
					addExtraColumnMapping();

			}
		});
		gridY++;
		columnMappingPanel.doLayout();
		if (columnMappingScrollPane != null)
			columnMappingScrollPane.doLayout();
	}

	private void importData() {
		try {
			if (sourceNameColumn.getSelectedItem().toString().equals("")) {
				JOptionPane.showMessageDialog(this, "Must select a source name column", "Cannot complete import", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (filterPanel.getFilterByAuto() && autoConceptIdColumn.getSelectedItem().toString().equals("")) {
				JOptionPane.showMessageDialog(this,
						"Must select an auto concept ID column / ATC column when filtering by automatically selected concept IDs / ATC code",
						"Cannot complete import", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (filterPanel.getFilterByAuto() && conceptIdsOrAtc.getSelectedItem().toString().equals(ATC)) {
				boolean atcLoaded = false;
				for (String line : Global.vocabularyIds)
					if (line.equals("ATC")) {
						atcLoaded = true;
						break;
					}
				if (!atcLoaded) {
					JOptionPane.showMessageDialog(this,
							"Filtering by ATC codes is selected, but the vocabulary does not contain ATC concept. Please reload the vocabulary from Athena.",
							"ATC vocabulary missing",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			List<SourceCode> sourceCodes = createSourceCodes();

			JDialog dialog = new JDialog(this, "Progress Dialog", false);

			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createRaisedBevelBorder());
			panel.setLayout(new BorderLayout());
			panel.add(BorderLayout.NORTH, new JLabel("Importing codes..."));
			JProgressBar progressBar = new JProgressBar(0, 100);
			panel.add(BorderLayout.CENTER, progressBar);
			dialog.add(panel);

			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setSize(300, 75);
			dialog.setLocationRelativeTo(this);
			dialog.setUndecorated(true);
			dialog.setModal(true);

			ImportCodesThread thread = new ImportCodesThread(sourceCodes, progressBar, dialog);
			thread.start();
			dialog.setVisible(true);
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Global.filename = null;
			Global.mapping.fireDataChanged(RESTRUCTURE_EVENT);
			setVisible(false);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(Global.frame, StringUtilities.wordWrap(e.toString(), 80), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private List<SourceCode> createSourceCodes() {
		int sourceCodeIndex = columnNames.indexOf(sourceCodeColumn.getSelectedItem().toString());
		int sourceNameIndex = columnNames.indexOf(sourceNameColumn.getSelectedItem().toString());
		int sourceFrequencyIndex = columnNames.indexOf(sourceFrequencyColumn.getSelectedItem().toString());
		int sourceAutoIndex = columnNames.indexOf(autoConceptIdColumn.getSelectedItem().toString());
		List<Integer> additionalInfoIndexes = new ArrayList<>();
		for (JComboBox<String> additionalInfoColumn : additionalInfoColumns) {
			int index = columnNames.indexOf(additionalInfoColumn.getSelectedItem().toString());
			if (index != -1)
				additionalInfoIndexes.add(index);
		}
		List<SourceCode> sourceCodes = new ArrayList<SourceCode>();
		for (List<String> row : data) {
			SourceCode sourceCode = new SourceCode();
			if (sourceCodeIndex == -1)
				sourceCode.sourceCode = "";
			else
				sourceCode.sourceCode = row.get(sourceCodeIndex);
			sourceCode.sourceName = row.get(sourceNameIndex);
			if (sourceCodeIndex != -1)
				sourceCode.sourceCode = row.get(sourceCodeIndex);
			if (sourceFrequencyIndex != -1)
				sourceCode.sourceFrequency = Integer.parseInt(row.get(sourceFrequencyIndex));
			else
				sourceCode.sourceFrequency = -1;
			if (sourceAutoIndex != -1)
				if (conceptIdsOrAtc.getSelectedItem().toString().equals(CONCEPT_IDS)) {
					for (String conceptId : row.get(sourceAutoIndex).split(";"))
						if (!conceptId.equals(""))
							sourceCode.sourceAutoAssignedConceptIds.add(Integer.parseInt(conceptId));
				} else {
					Set<Integer> conceptIds = Global.dbEngine.getRxNormConceptIds(row.get(sourceAutoIndex));
					sourceCode.sourceAutoAssignedConceptIds.addAll(conceptIds);
				}
			for (int additionalInfoIndex : additionalInfoIndexes)
				sourceCode.sourceAdditionalInfo.add(new Pair<String, String>(columnNames.get(additionalInfoIndex), row.get(additionalInfoIndex)));
			sourceCodes.add(sourceCode);
		}
		return sourceCodes;
	}

	private class ImportCodesThread extends Thread {
		private JProgressBar		progressBar;
		private List<SourceCode>	sourceCodes;
		private JDialog				dialog;

		public ImportCodesThread(List<SourceCode> sourceCodes, JProgressBar progressBar, JDialog dialog) {
			this.sourceCodes = sourceCodes;
			this.progressBar = progressBar;
			this.dialog = dialog;
		}

		public void run() {
			try {
				Global.usagiSearchEngine.createDerivedIndex(sourceCodes, null);

				boolean filterStandard = filterPanel.getFilterStandard();
				Vector<String> filterConceptClasses = null;
				if (filterPanel.getFilterByConceptClasses())
					filterConceptClasses = filterPanel.getConceptClass();
				Vector<String> filterVocabularies = null;
				if (filterPanel.getFilterByVocabularies())
					filterVocabularies = filterPanel.getVocabulary();
				Vector<String> filterDomains = null;
				if (filterPanel.getFilterByDomains())
					filterDomains = filterPanel.getDomain();
				boolean includeSourceConcepts = filterPanel.getIncludeSourceTerms();
				final Vector<String> filterConceptClassesFinal = filterConceptClasses;
				final Vector<String> filterVocabulariesFinal = filterVocabularies;
				final Vector<String> filterDomainsFinal = filterDomains;

				Global.mapping.clear();

				List<CodeMapping> globalMappingList = Collections.synchronizedList(Global.mapping);
				Integer threadCount = Runtime.getRuntime().availableProcessors();
				if (threadCount <= 0) {
					threadCount = 1;
				}

				// Note: Lucene's and BerkeleyDB's search objects are thread safe, so do not need to be recreated for each thread.
				ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
				forkJoinPool.submit(() -> sourceCodes.parallelStream().forEach(sourceCode -> {
					Set<Integer> filterConceptIds = null;
					if (filterPanel.getFilterByAuto())
						filterConceptIds = sourceCode.sourceAutoAssignedConceptIds;
					try {
						CodeMapping codeMapping = new CodeMapping(sourceCode);
						List<ScoredConcept> concepts = Global.usagiSearchEngine.search(sourceCode.sourceName, true, filterConceptIds, filterDomainsFinal,
								filterConceptClassesFinal, filterVocabulariesFinal, filterStandard, includeSourceConcepts);
						if (concepts.size() > 0) {
							codeMapping.getTargetConcepts().add(new MappingTarget(concepts.get(0).concept, "<auto>"));
							codeMapping.setMatchScore(concepts.get(0).matchScore);
						} else {
							codeMapping.setMatchScore(0);
						}
						codeMapping.setComment("");
						codeMapping.setMappingStatus(MappingStatus.UNCHECKED);
						if (sourceCode.sourceAutoAssignedConceptIds.size() == 1 && concepts.size() > 0) {
							codeMapping.setMappingStatus(MappingStatus.AUTO_MAPPED_TO_1);
						} else if (sourceCode.sourceAutoAssignedConceptIds.size() > 1 && concepts.size() > 0) {
							codeMapping.setMappingStatus(MappingStatus.AUTO_MAPPED);
						}
						codeMapping.setEquivalence(CodeMapping.Equivalence.UNREVIEWED);
						synchronized (globalMappingList) {
							globalMappingList.add(codeMapping);
							progressBar.setValue(Math.round(100 * globalMappingList.size() / sourceCodes.size()));
						}
					} catch (Exception e) {
						System.out.println(e.toString());
					}
				})).get();
				forkJoinPool.shutdown();
				dialog.setVisible(false);
				Global.applyPreviousMappingAction.setEnabled(true);
				Global.saveAction.setEnabled(true);
				Global.saveAsAction.setEnabled(true);
				Global.exportAction.setEnabled(true);
				Global.exportForReviewAction.setEnabled(true);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(Global.frame, StringUtilities.wordWrap(e.getMessage(), 80), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class TableModelWrapper implements TableModel {

		@Override
		public void addTableModelListener(TableModelListener l) {
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return columnNames.size();
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames.get(columnIndex);
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data.get(rowIndex).get(columnIndex);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}
	}
}
