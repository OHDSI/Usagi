package org.ohdsi.usagi.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.UsagiSearchEngine.ScoredConcept;

class ConceptTableModel extends AbstractTableModel {
	private static final long	serialVersionUID	= 169286268154988911L;

	private String				scoreColumnName		= "Score";
	private String				termColumnName		= "Term";
	private String[]			columnNames			= { "Concept ID", "Concept name", "Domain", "Concept class", "Vocabulary", "Concept code",
															"Valid start date", "Valid end date", "Invalid reason", "Standard concept", "Parents", "Children" };
	private List<Concept>		targetConcepts		= new ArrayList<Concept>();
	private boolean				hasScoreColumn;
	private List<String>		terms				= new ArrayList<String>();
	private Double[]			scoreColumn;

	public ConceptTableModel(boolean scoreColumn) {
		this.hasScoreColumn = scoreColumn;
	}

	public Concept getConcept(int row) {
		return targetConcepts.get(row);
	}

	public int getColumnCount() {
		if (hasScoreColumn)
			return columnNames.length + 2;
		else
			return columnNames.length;
	}

	public void setConcepts(List<Concept> targetConcepts) {
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
				return targetConcept.invalidReason;
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
