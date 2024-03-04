package org.ohdsi.usagi;

import java.util.ArrayList;
import java.util.List;

public enum Equivalence {
    EQUAL("exactMatch"), 
    EQUIVALENT("closeMatch"), 
    WIDER("broadMatch"), 
    NARROWER("narrowMatch"), 
    INEXACT("relatedMatch"), 
    UNMATCHED("unmatched"), 
    UNREVIEWED("unreviewed");
    
    private String displayName;
    
    private Equivalence(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public static Equivalence fromDisplayName(String displayName) {
        for (Equivalence equivalence : Equivalence.values()) {
            if (equivalence.getDisplayName().equals(displayName)) {
                return equivalence;
            }
        }
        return null;
    }
    
    public static List<String> getAllDisplayNames() {
        List<String> names = new ArrayList<>();
        for (Equivalence equivalence : Equivalence.values()) {
            names.add(equivalence.getDisplayName());
        }
        return names;
    }
   

    public static void main(String[] args) {
        System.out.println(Equivalence.getAllDisplayNames());
        String spam = Equivalence.getAllDisplayNames().get(3);
        System.out.println(Equivalence.fromDisplayName(spam));
    }
}