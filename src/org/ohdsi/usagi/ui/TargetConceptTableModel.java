package org.ohdsi.usagi.ui;

import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.MappingTarget;
import org.ohdsi.usagi.UsagiSearchEngine.ScoredConcept;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class TargetConceptTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -4978479688021056281L;

    private final String termColumnName = "Term";
    private final String[] columnNames = {"Concept ID", "Concept name", "Domain", "Concept class", "Vocabulary", "Concept code",
            "Valid start date", "Valid end date", "Invalid reason", "Standard concept", "Parents", "Children", "Mapping Type",
            "Creation Provenance"};
    private List<MappingTarget> targetConcepts = new ArrayList<>();

    public TargetConceptTableModel() {
    }

    public Concept getConcept(int row) {
        return targetConcepts.get(row).concept;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public void setConcepts(List<MappingTarget> mappingTargets) {
        this.targetConcepts = mappingTargets;
        fireTableDataChanged();
    }

    public int getRowCount() {
        return targetConcepts.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        if (row > targetConcepts.size()) {
            return "";
        }
        MappingTarget targetConcept = targetConcepts.get(row);
        switch (col) {
            case 0:
                return targetConcept.concept.conceptId;
            case 1:
                return targetConcept.concept.conceptName;
            case 2:
                return targetConcept.concept.domainId;
            case 3:
                return targetConcept.concept.conceptClassId;
            case 4:
                return targetConcept.concept.vocabularyId;
            case 5:
                return targetConcept.concept.conceptCode;
            case 6:
                return targetConcept.concept.validStartDate;
            case 7:
                return targetConcept.concept.validEndDate;
            case 8:
                return targetConcept.concept.invalidReason;
            case 9:
                return targetConcept.concept.standardConcept;
            case 10:
                return targetConcept.concept.parentCount;
            case 11:
                return targetConcept.concept.childCount;
            case 12:
                return targetConcept.mappingType;
            case 13:
                if (targetConcept.createdOn != 0L) {
                    return String.format("%s (%tF)", targetConcept.createdBy, targetConcept.createdOn);
                }
            default:
                return "";
        }
    }

    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 1:
            case 10:
            case 11:
                return Integer.class;
            default:
                return String.class;
        }
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void setValueAt(Object value, int row, int col) {

    }
}
