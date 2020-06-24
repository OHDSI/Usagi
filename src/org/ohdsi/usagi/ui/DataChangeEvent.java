package org.ohdsi.usagi.ui;

public enum DataChangeEvent {

    APPROVE_EVENT (true, false, false),
    SIMPLE_UPDATE_EVENT (false, false, false),
    MULTI_UPDATE_EVENT (false, false, true),
    RESTRUCTURE_EVENT (false, true, false);

    public boolean	approved;
    public boolean	structureChange;
    public boolean	multiUpdate;

    DataChangeEvent(boolean approved, boolean structureChange, boolean multiUpdate) {
        this.approved = approved;
        this.structureChange = structureChange;
        this.multiUpdate = multiUpdate;
    }
}
