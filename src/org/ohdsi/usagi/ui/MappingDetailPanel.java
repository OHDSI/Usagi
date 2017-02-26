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
package org.ohdsi.usagi.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.CodeMapping.MappingStatus;
import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.UsagiSearchEngine.ScoredConcept;

public class MappingDetailPanel extends JPanel implements CodeSelectedListener, FilterChangeListener {

	private static final long		serialVersionUID	= 2127318722005512776L;
	private UsagiTable				sourceCodeTable;
	private SourceCodeTableModel	sourceCodeTableModel;
	private UsagiTable				targetConceptTable;
	private TargetConceptTableModel	targetConceptTableModel;
	private UsagiTable				searchTable;
	private TargetConceptTableModel	searchTableModel;
	private JButton					approveButton;
	private JButton					removeButton;
	private JButton					replaceButton;
	private JButton					addButton;
	private JRadioButton			autoQueryButton;
	private JRadioButton			manualQueryButton;
	private JTextField				manualQueryField;
	private CodeMapping				codeMapping;
	private FilterPanel				filterPanel;
	private Timer					timer;

	public MappingDetailPanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(createSourceCodePanel());
		add(createTargetConceptsPanel());
		add(createSearchPanel());
		add(createApprovePanel());
	}

	private Component createSearchPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Search"));
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0.1;
		panel.add(createQueryPanel(), c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.1;
		c.weighty = 0.1;
		filterPanel = new FilterPanel();
		filterPanel.addListener(this);
		panel.add(filterPanel, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		panel.add(createSearchResultsPanel(), c);
		return panel;
	}

	private Component createQueryPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Query"));
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.1;
		c.gridwidth = 2;

		autoQueryButton = new JRadioButton("Use source term as query", true);
		autoQueryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				doSearch();
			}
		});
		panel.add(autoQueryButton, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.1;
		c.gridwidth = 1;
		manualQueryButton = new JRadioButton("Query:", false);
		manualQueryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				doSearch();
			}
		});
		panel.add(manualQueryButton, c);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(autoQueryButton);
		buttonGroup.add(manualQueryButton);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 1;
		manualQueryField = new JTextField("");
		// manualQueryField.setPreferredSize(new Dimension(200, 5));
		manualQueryField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				manualQueryButton.setSelected(true);
				doSearch();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				manualQueryButton.setSelected(true);
				doSearch();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				manualQueryButton.setSelected(true);
				doSearch();
			}
		});
		panel.add(manualQueryField, c);
		return panel;
	}

	private Component createSearchResultsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Results"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		searchTableModel = new TargetConceptTableModel(true);
		searchTable = new UsagiTable(searchTableModel);
		searchTable.setPreferredScrollableViewportSize(new Dimension(100, 100));
		searchTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		searchTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				int viewRow = searchTable.getSelectedRow();
				if (viewRow == -1) {
					addButton.setEnabled(false);
					replaceButton.setEnabled(false);
				} else {
					addButton.setEnabled(true);
					replaceButton.setEnabled(true);
					Global.conceptInfoAction.setEnabled(true);
					int modelRow = searchTable.convertRowIndexToModel(viewRow);
					Global.conceptInformationDialog.setConcept(searchTableModel.getTargetConcept(modelRow));
				}
			}

		});
		// searchTable.hideColumn("Synonym");
		searchTable.hideColumn("Valid start date");
		searchTable.hideColumn("Valid end date");
		searchTable.hideColumn("Invalid reason");
		panel.add(new JScrollPane(searchTable));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		replaceButton = new JButton("Replace concept");
		replaceButton.setToolTipText("Replace selected concept");
		replaceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				replace();
			}

		});
		replaceButton.setEnabled(false);
		buttonPanel.add(replaceButton);
		addButton = new JButton("Add concept");
		addButton.setToolTipText("Add selected concept");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add();
			}

		});
		addButton.setEnabled(false);
		buttonPanel.add(addButton);
		panel.add(buttonPanel);

		return panel;
	}

	private Component createApprovePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());

		approveButton = new JButton(Global.approveAction);
		approveButton.setBackground(new Color(151, 220, 141));
		panel.add(approveButton);
		return panel;
	}

	private JPanel createSourceCodePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Source code"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		sourceCodeTableModel = new SourceCodeTableModel();
		sourceCodeTable = new UsagiTable(sourceCodeTableModel);
		sourceCodeTable.setPreferredScrollableViewportSize(new Dimension(500, 35));
		sourceCodeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		sourceCodeTable.setRowSelectionAllowed(false);
		sourceCodeTable.setCellSelectionEnabled(false);
		JScrollPane pane = new JScrollPane(sourceCodeTable);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setMinimumSize(new Dimension(500, 40));
		pane.setPreferredSize(new Dimension(500, 40));
		panel.add(pane);
		return panel;
	}

	private JPanel createTargetConceptsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Target concepts"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		targetConceptTableModel = new TargetConceptTableModel(false);
		targetConceptTable = new UsagiTable(targetConceptTableModel);
		targetConceptTable.setPreferredScrollableViewportSize(new Dimension(500, 45));
		targetConceptTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		targetConceptTable.setRowSelectionAllowed(true);
		targetConceptTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				int viewRow = targetConceptTable.getSelectedRow();
				if (viewRow == -1) {
					removeButton.setEnabled(false);
				} else {
					removeButton.setEnabled(true);
					Global.conceptInfoAction.setEnabled(true);
					int modelRow = targetConceptTable.convertRowIndexToModel(viewRow);
					Global.conceptInformationDialog.setConcept(targetConceptTableModel.getTargetConcept(modelRow));
				}
			}

		});
		targetConceptTable.hideColumn("Valid start date");
		targetConceptTable.hideColumn("Valid end date");
		targetConceptTable.hideColumn("Invalid reason");

		JScrollPane pane = new JScrollPane(targetConceptTable);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setMinimumSize(new Dimension(500, 50));
		pane.setPreferredSize(new Dimension(500, 50));
		panel.add(pane);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		removeButton = new JButton("Remove concept");
		removeButton.setToolTipText("Add selected concept");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove();
			}

		});
		removeButton.setEnabled(false);
		buttonPanel.add(removeButton);
		panel.add(buttonPanel);
		return panel;
	}

	@Override
	public void codeSelected(CodeMapping codeMapping) {
		this.codeMapping = codeMapping;
		setApproveButton();
		sourceCodeTableModel.setMapping(codeMapping);
		targetConceptTableModel.setTargetConcepts(codeMapping.targetConcepts);
		doSearch();
	}

	public void approve() {
		if (codeMapping.mappingStatus != CodeMapping.MappingStatus.APPROVED) {
			codeMapping.mappingStatus = CodeMapping.MappingStatus.APPROVED;
			Global.mapping.fireDataChanged(DataChangeListener.APPROVE_EVENT);
		} else {
			codeMapping.mappingStatus = CodeMapping.MappingStatus.UNCHECKED;
			Global.mapping.fireDataChanged(DataChangeListener.SIMPLE_UPDATE_EVENT);
			setApproveButton();
		}
	}

	private void setApproveButton() {
		if (codeMapping.mappingStatus == MappingStatus.APPROVED) {
			Global.approveAction.putValue(Action.NAME, "Unapprove");
			Global.approveAction.putValue(Action.SHORT_DESCRIPTION, "Unapprove this mapping");
			approveButton.setBackground(new Color(220, 151, 141));
		} else {
			Global.approveAction.putValue(Action.NAME, "Approve");
			Global.approveAction.putValue(Action.SHORT_DESCRIPTION, "Approve this mapping");
			approveButton.setBackground(new Color(151, 220, 141));
		}
	}

	private void add() {
		for (int viewRow : searchTable.getSelectedRows())
			if (viewRow != -1) {
				int modelRow = searchTable.convertRowIndexToModel(viewRow);
				codeMapping.targetConcepts.add(searchTableModel.getTargetConcept(modelRow));
			}
		targetConceptTableModel.fireTableDataChanged();
		Global.mapping.fireDataChanged(DataChangeListener.SIMPLE_UPDATE_EVENT);
	}

	private void replace() {
		codeMapping.targetConcepts.clear();
		add();
	}

	private void remove() {
		List<Integer> rows = new ArrayList<Integer>();
		for (int row : targetConceptTable.getSelectedRows())
			rows.add(targetConceptTable.convertRowIndexToModel(row));

		Collections.sort(rows, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});
		for (int row : rows)
			codeMapping.targetConcepts.remove(row);

		targetConceptTableModel.fireTableDataChanged();
		Global.mapping.fireDataChanged(DataChangeListener.SIMPLE_UPDATE_EVENT);
	}

	private class SearchTask extends TimerTask {

		@Override
		public void run() {
			Set<Integer> filterConceptIds = null;
			if (filterPanel.getFilterByAuto())
				filterConceptIds = codeMapping.sourceCode.sourceAutoAssignedConceptIds;

			boolean filterStandard = filterPanel.getFilterStandard();
			String filterConceptClass = null;
			if (filterPanel.getFilterByConceptClass())
				filterConceptClass = filterPanel.getConceptClass();
			String filterVocabulary = null;
			if (filterPanel.getFilterByVocabulary())
				filterVocabulary = filterPanel.getVocabulary();
			String filterDomain = null;
			if (filterPanel.getFilterByDomain())
				filterDomain = filterPanel.getDomain();
			String query = manualQueryField.getText();
			if (autoQueryButton.isSelected())
				query = codeMapping.sourceCode.sourceName;
			boolean includeSourceConcepts = filterPanel.getIncludeSourceTerms();
		

			if (Global.usagiSearchEngine.isOpenForSearching()) {
				List<ScoredConcept> searchResults = Global.usagiSearchEngine.search(query, true, filterConceptIds, filterDomain, filterConceptClass,
						filterVocabulary, filterStandard, includeSourceConcepts);

				searchTableModel.setScoredConcepts(searchResults);
				searchTable.scrollRectToVisible(new Rectangle(searchTable.getCellRect(0, 0, true)));
			}
			Global.statusBar.setSearching(false);
		}
	}

	public void doSearch() {
		 Global.statusBar.setSearching(true);
		 if (timer != null)
		 timer.cancel();
		 timer = new Timer();
		 timer.schedule(new SearchTask(), 500);
	}

	class SourceCodeTableModel extends AbstractTableModel {
		private static final long	serialVersionUID	= 169286268154988911L;

		private String[]			defaultColumnNames	= { "Source code", "Source term", "Frequency" };
		private String[]			columnNames			= defaultColumnNames;
		private CodeMapping			codeMapping;
		private int					addInfoColCount		= 0;
		private int					ADD_INFO_START_COL	= 3;

		public int getColumnCount() {
			return columnNames.length;
		}

		public void setMapping(CodeMapping codeMapping) {
			this.codeMapping = codeMapping;

			columnNames = defaultColumnNames;
			addInfoColCount = codeMapping.sourceCode.sourceAdditionalInfo.size();
			columnNames = new String[defaultColumnNames.length + addInfoColCount];
			for (int i = 0; i < ADD_INFO_START_COL; i++)
				columnNames[i] = defaultColumnNames[i];

			for (int i = 0; i < addInfoColCount; i++)
				columnNames[i + ADD_INFO_START_COL] = codeMapping.sourceCode.sourceAdditionalInfo.get(i).getItem1();

			fireTableStructureChanged();
		}

		public int getRowCount() {
			return 1;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			if (codeMapping == null)
				return "";
			if (col >= ADD_INFO_START_COL) {
				return codeMapping.sourceCode.sourceAdditionalInfo.get(col - ADD_INFO_START_COL).getItem2();
			} else {
				switch (col) {
					case 0:
						return codeMapping.sourceCode.sourceCode;
					case 1:
						return codeMapping.sourceCode.sourceName;
					case 2:
						return codeMapping.sourceCode.sourceFrequency;
					default:
						return "";
				}
			}

		}

		public Class<?> getColumnClass(int col) {
			if (col >= ADD_INFO_START_COL) {
				return String.class;
			} else {
				switch (col) {
					case 0:
						return String.class;
					case 1:
						return String.class;
					case 2:
						return Integer.class;
					default:
						return String.class;
				}
			}
		}

		public boolean isCellEditable(int row, int col) {
			return true;
		}

		public void setValueAt(Object value, int row, int col) {

		}
	}

	class TargetConceptTableModel extends AbstractTableModel {
		private static final long	serialVersionUID	= 169286268154988911L;

		private String				scoreColumnName		= "Score";
		private String				termColumnName		= "Term";
		private String[]			columnNames			= { "Concept ID", "Concept name", "Domain", "Concept class", "Vocabulary", "Concept code",
																"Valid start date", "Valid end date", "Invalid reason", "Standard concept", "Parents", "Children" };
		private List<Concept>		targetConcepts		= new ArrayList<Concept>();
		private boolean				hasScoreColumn;
		private List<String>		terms				= new ArrayList<String>();
		private Double[]			scoreColumn;

		public TargetConceptTableModel(boolean scoreColumn) {
			this.hasScoreColumn = scoreColumn;
		}

		public Concept getTargetConcept(int row) {
			return targetConcepts.get(row);
		}

		public int getColumnCount() {
			if (hasScoreColumn)
				return columnNames.length + 2;
			else
				return columnNames.length;
		}

		public void setTargetConcepts(List<Concept> targetConcepts) {
			this.targetConcepts = targetConcepts;
			fireTableDataChanged();
		}

		public void setScoredConcepts(List<ScoredConcept> scoredConcepts) {
			targetConcepts = new ArrayList<Concept>(scoredConcepts.size());
			terms = new ArrayList<String>();
			scoreColumn = new Double[scoredConcepts.size()];
			for (int i = 0; i < scoredConcepts.size(); i++) {
				targetConcepts.add(scoredConcepts.get(i).concept);
				scoreColumn[i] = (double) scoredConcepts.get(i).matchScore;
				terms.add(scoredConcepts.get(i).term);
			}
			fireTableDataChanged();
		}

		public int getRowCount() {
			return targetConcepts.size();
		}

		public String getColumnName(int col) {
			if (hasScoreColumn) {
				if (col == 0)
					return scoreColumnName;
				else if (col == 1)
					return termColumnName;
				else
					return columnNames[col - 2];
			} else
				return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			if (row > targetConcepts.size())
				return "";
			if (hasScoreColumn) {
				if (col == 0)
					return scoreColumn[row];
				if (col == 1)
					return terms.get(row);
				col -= 2;
			}
			Concept targetConcept = targetConcepts.get(row);
			switch (col) {
				case 0:
					return targetConcept.conceptId;
				case 1:
					return targetConcept.conceptName;
				case 2:
					return targetConcept.domainId;
				case 3:
					return targetConcept.conceptClassId;
				case 4:
					return targetConcept.vocabularyId;
				case 5:
					return targetConcept.conceptCode;
				case 6:
					return targetConcept.validStartDate;
				case 7:
					return targetConcept.validEndDate;
				case 8:
					return targetConcept.invalidReason == null ? "" : targetConcept.invalidReason;
				case 9:
					return targetConcept.standardConcept;
				case 10:
					return targetConcept.parentCount;
				case 11:
					return targetConcept.childCount;
				default:
					return "";
			}
		}

		public Class<?> getColumnClass(int col) {
			if (hasScoreColumn) {
				if (col == 0)
					return Double.class;
				if (col == 1)
					return String.class;
				col -= 2;
			}
			switch (col) {
				case 1:
					return Integer.class;
				case 10:
					return Integer.class;
				case 11:
					return Integer.class;
				default:
					return String.class;
			}
		}

		public boolean isCellEditable(int row, int col) {
			return true;
		}

		public void setValueAt(Object value, int row, int col) {

		}
	}

	@Override
	public void filterChanged() {
		doSearch();
	}

}
