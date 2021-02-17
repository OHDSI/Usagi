/*******************************************************************************
 * Copyright 2020 Observational Health Data Sciences and Informatics & The Hyve
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
package org.ohdsi.usagi;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class for holding information on a single source code and its mapping
 *
 * @author MSCHUEMI
 */
public class CodeMapping {
    public enum MappingStatus {
        // Includes IGNORED for backwards compatibility
        APPROVED, UNCHECKED, AUTO_MAPPED, AUTO_MAPPED_TO_1, INVALID_TARGET, FLAGGED, IGNORED
    };

    public enum Equivalence {
        EQUAL, EQUIVALENT, WIDER, NARROWER, INEXACT, UNMATCHED, UNREVIEWED
    };

    public SourceCode sourceCode;
    public double matchScore;
    public MappingStatus mappingStatus;
    public Equivalence equivalence;
    public List<MappingTarget> targetConcepts = new ArrayList<>(1);
    public String comment;
    public String statusSetBy;
    public long statusSetOn;
    public String assignedReviewer;

    public CodeMapping(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

    public void setStatus(MappingStatus mappingStatus, String author) {
        this.mappingStatus = mappingStatus;
        this.statusSetOn = System.currentTimeMillis();
        this.statusSetBy = author;
    }

    public void setUnchecked() {
        this.mappingStatus = MappingStatus.UNCHECKED;
        this.statusSetOn = 0;
        this.statusSetBy = "";
    }

    public void approve(String approvedBy, Equivalence equivalence) {
        setStatus(MappingStatus.APPROVED, approvedBy);
        this.equivalence = equivalence;
    }

    public void approve(String approvedBy) {
        approve(approvedBy, Equivalence.EQUAL);
    }
}
