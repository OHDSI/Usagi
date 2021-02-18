package org.ohdsi.usagi.ui;

import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.MappingTarget;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class TargetConceptTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -4978479688021056281L;

    private final String[] columnNames = {"Concept ID", "Concept name", "Domain", "Concept class", "Vocabulary", "Concept code",
            "Valid start date", "Valid end date", "Invalid reason", "Standard concept", "Parents", "Children",
            "Creation Provenance"};
    private List<MappingTarget> targetConcepts = new ArrayList<>();

    public TargetConceptTableModel() {
    }

    public MappingTarget getMappingTarget(int row) {
        return targetConcepts.get(row);
    }

    public Concept getConcept(int row) {
        return getMappingTarget(row).getConcept();
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
        MappingTarget mappingTarget = targetConcepts.get(row);
        Concept targetConcept = mappingTarget.getConcept();
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
            case 12:
                if (mappingTarget.getCreatedTime() != 0L) {
                    return String.format("%s (%tF)", mappingTarget.getCreatedBy(), mappingTarget.getCreatedTime());
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
