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
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.CodeMapping.MappingStatus;
import org.ohdsi.usagi.Concept;

import static org.ohdsi.usagi.ui.DataChangeEvent.*;

public class MappingTablePanel extends JPanel implements DataChangeListener {
	private static final long					serialVersionUID	= -5862314086097240860L;
	private UsagiTable							table;
	private CodeMapTableModel					tableModel;
	private List<CodeSelectedListener>			listeners			= new ArrayList<>();
	private boolean								ignoreSelection		= false;

	public MappingTablePanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		tableModel = new CodeMapTableModel();
		table = new UsagiTable(tableModel);
		table.setRowSorter(new TableRowSorter<>(tableModel));
		table.setPreferredScrollableViewportSize(new Dimension(1200, 200));
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		table.getSelectionModel().addListSelectionListener(event -> {
			if (!ignoreSelection) {
				int primaryViewRow = table.getSelectedRow();
				if (primaryViewRow != -1) {
					int primaryModelRow = table.convertRowIndexToModel(primaryViewRow);
					for (CodeSelectedListener listener : listeners) {
						listener.codeSelected(tableModel.getCodeMapping(primaryModelRow));
						listener.clearCodeMultiSelected();
					}

					Global.googleSearchAction.setEnabled(true);
					Global.googleSearchAction.setSourceTerm(tableModel.getCodeMapping(primaryModelRow).sourceCode.sourceName);

					Global.approveAction.setEnabled(true);
					Global.approveSelectedAction.setEnabled(true);
					Global.ignoreAction.setEnabled(true);
					Global.ignoreSelectedAction.setEnabled(true);
					Global.clearSelectedAction.setEnabled(true);
					if (tableModel.getCodeMapping(primaryModelRow).targetConcepts.size() > 0) {
						Concept firstConcept = tableModel.getCodeMapping(primaryModelRow).targetConcepts.get(0);
						Global.conceptInfoAction.setEnabled(true);
						Global.conceptInformationDialog.setConcept(firstConcept);
						Global.athenaAction.setEnabled(true);
						Global.athenaAction.setConcept(firstConcept);
					}

					// Store all other co-selected rows
					for (int viewRow : table.getSelectedRows()) {
						if (viewRow != -1 && viewRow != primaryViewRow) {
							int modelRow = table.convertRowIndexToModel(viewRow);
							for (CodeSelectedListener listener : listeners) {
								listener.addCodeMultiSelected(tableModel.getCodeMapping(modelRow));
							}
						}
					}
				} else {
					Global.approveSelectedAction.setEnabled(false);
					Global.approveAction.setEnabled(false);
					Global.ignoreAction.setEnabled(false);
					Global.ignoreSelectedAction.setEnabled(false);
					Global.clearSelectedAction.setEnabled(false);
				}
			}
		});

		// Hide some less-informative columns:
		table.hideColumn("Valid start date");
		table.hideColumn("Valid end date");
		table.hideColumn("Invalid reason");

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);

		Global.mapping.addListener(this);
	}

	class CodeMapTableModel extends AbstractTableModel {
		private static final long	serialVersionUID	= 169286268154988911L;

		private String[]			defaultColumnNames	= { "Status", "Source code", "Source term", "Frequency", "Match score", "Concept ID", "Concept name",
				"Domain", "Concept class", "Vocabulary", "Concept code", "Valid start date", "Valid end date", "Invalid reason", "Standard concept", "Parents",
				"Children", "Comment" };
		private String[]			columnNames			= defaultColumnNames;
		private int					addInfoColCount		= 0;
		private int					ADD_INFO_START_COL	= 4;

		public int getColumnCount() {
			return columnNames.length;
		}

		public CodeMapping getCodeMapping(int modelRow) {
			return Global.mapping.get(modelRow);
		}

		public void restructure() {
			columnNames = defaultColumnNames;
			addInfoColCount = 0;
			if (Global.mapping.size() != 0) {
				CodeMapping codeMapping = Global.mapping.get(0);
				addInfoColCount = codeMapping.sourceCode.sourceAdditionalInfo.size();
				columnNames = new String[defaultColumnNames.length + addInfoColCount];
				for (int i = 0; i < ADD_INFO_START_COL; i++)
					columnNames[i] = defaultColumnNames[i];

				for (int i = 0; i < addInfoColCount; i++)
					columnNames[i + ADD_INFO_START_COL] = codeMapping.sourceCode.sourceAdditionalInfo.get(i).getItem1();

				for (int i = ADD_INFO_START_COL; i < defaultColumnNames.length; i++)
					columnNames[i + addInfoColCount] = defaultColumnNames[i];
			}
			fireTableStructureChanged();
			table.setRowSelectionInterval(0, 0);
		}

		public int getRowCount() {
			return Global.mapping.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			CodeMapping codeMapping = Global.mapping.get(row);

			if (col >= ADD_INFO_START_COL && col < ADD_INFO_START_COL + addInfoColCount) {
				return codeMapping.sourceCode.sourceAdditionalInfo.get(col - ADD_INFO_START_COL).getItem2();
			} else {
				if (col >= ADD_INFO_START_COL) {
					col = col - addInfoColCount;
				}
				Concept targetConcept;
				if (codeMapping.targetConcepts.size() > 0)
					targetConcept = codeMapping.targetConcepts.get(0);
				else
					targetConcept = Concept.EMPTY_CONCEPT;
				switch (col) {
					case 0:
						return codeMapping.mappingStatus;
					case 1:
						return codeMapping.sourceCode.sourceCode;
					case 2:
						return codeMapping.sourceCode.sourceName;
					case 3:
						return codeMapping.sourceCode.sourceFrequency == -1 ? "" : codeMapping.sourceCode.sourceFrequency;
					case 4:
						return codeMapping.matchScore;
					case 5:
						return targetConcept.conceptId;
					case 6:
						return targetConcept.conceptName;
					case 7:
						return targetConcept.domainId;
					case 8:
						return targetConcept.conceptClassId;
					case 9:
						return targetConcept.vocabularyId;
					case 10:
						return targetConcept.conceptCode;
					case 11:
						return targetConcept.validStartDate;
					case 12:
						return targetConcept.validEndDate;
					case 13:
						return targetConcept.invalidReason;
					case 14:
						return targetConcept.standardConcept;
					case 15:
						return targetConcept.parentCount;
					case 16:
						return targetConcept.childCount;
					case 17:
						return codeMapping.comment;
					default:
						return "";
				}
			}
		}

		public Class<?> getColumnClass(int col) {
			if (col >= ADD_INFO_START_COL && col < ADD_INFO_START_COL + addInfoColCount) {
				return String.class;
			} else {
				if (col >= ADD_INFO_START_COL) {
					col = col - addInfoColCount;
				}
				switch (col) {
					case 0:
						return MappingStatus.class;
					case 3:
						return Integer.class;
					case 4:
						return Double.class;
					case 5:
						return Integer.class;
					case 7:
						return Integer.class;
					case 15:
						return Integer.class;
					case 16:
						return Integer.class;
					default:
						return String.class;
				}
			}
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public void setValueAt(Object value, int row, int col) {

		}
	}

	public void addCodeSelectedListener(CodeSelectedListener listener) {
		listeners.add(listener);
	}

	@Override
	public void dataChanged(DataChangeEvent event) {
		if (event.approved) {
			int row = table.getSelectedRow();
			ignoreSelection = true;
			tableModel.fireTableDataChanged();
			ignoreSelection = false;
			if (row < table.getRowCount() - 1) {
				table.setRowSelectionInterval(row + 1, row + 1);
				table.scrollRectToVisible(new Rectangle(table.getCellRect(row + 1, row + 1, true)));
			}
		} else if (event.structureChange) {
			tableModel.restructure();
			table.setRowSelectionInterval(0, 0);
		} else if (event.multiUpdate) {
			tableModel.fireTableDataChanged();
		} else {
			tableModel.fireTableRowsUpdated(table.getSelectedRow(), table.getSelectedRow());
		}

		// Multi selection is lost
		for (CodeSelectedListener listener : listeners) {
			listener.clearCodeMultiSelected();
		}
	}

	private void fireUpdateEventAll(DataChangeEvent event) {
		Global.mapping.fireDataChanged(event);
		int viewRow = table.getSelectedRow();
		if (viewRow != -1) {
			int modelRow = table.convertRowIndexToModel(viewRow);
			for (CodeSelectedListener listener : listeners) {
				listener.codeSelected(tableModel.getCodeMapping(modelRow));
			}
		}
	}

	public void approveSelected() {
		for (int viewRow : table.getSelectedRows()) {
			int modelRow = table.convertRowIndexToModel(viewRow);
			if (tableModel.getCodeMapping(modelRow).mappingStatus != MappingStatus.IGNORED) {
				tableModel.getCodeMapping(modelRow).mappingStatus = MappingStatus.APPROVED;
			}
		}
		fireUpdateEventAll(APPROVE_EVENT);
	}

	public void ignoreSelected() {
		for (int viewRow : table.getSelectedRows()) {
			int modelRow = table.convertRowIndexToModel(viewRow);
			if (tableModel.getCodeMapping(modelRow).mappingStatus != MappingStatus.APPROVED) {
				tableModel.getCodeMapping(modelRow).mappingStatus = MappingStatus.IGNORED;
				Global.mappingTablePanel.clearSelected();
			}
		}
		fireUpdateEventAll(APPROVE_EVENT);
	}

	public void clearSelected() {
		for (int viewRow : table.getSelectedRows()) {
			int modelRow = table.convertRowIndexToModel(viewRow);
			tableModel.getCodeMapping(modelRow).targetConcepts.clear();
		}
		fireUpdateEventAll(SIMPLE_UPDATE_EVENT);
	}
}
