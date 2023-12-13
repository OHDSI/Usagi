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

import org.ohdsi.usagi.ui.Global;
import org.ohdsi.usagi.Equivalence;

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
    }

    private SourceCode sourceCode;
    private double matchScore;
    private MappingStatus mappingStatus;
    private Equivalence equivalence;
    private List<MappingTarget> targetConcepts = new ArrayList<>(1);
    private String comment;
    private String statusSetBy;
    private long statusSetOn;
    private String assignedReviewer;

    public CodeMapping(SourceCode sourceCode) {
        this.setSourceCode(sourceCode);
    }

    public void approve(Equivalence equivalence) {
        setStatus(MappingStatus.APPROVED);
        this.setEquivalence(equivalence);
    }

    public void flag(Equivalence equivalence) {
        setStatus(MappingStatus.FLAGGED);
        this.setEquivalence(equivalence);
    }

    public void setStatus(MappingStatus mappingStatus) {
        this.setMappingStatus(mappingStatus);
        this.setStatusSetOn(System.currentTimeMillis());
        this.setStatusSetBy(Global.author);
    }

    public void setUnchecked() {
        this.setMappingStatus(MappingStatus.UNCHECKED);
        this.setEquivalence(Equivalence.UNREVIEWED);
        this.setStatusSetOn(0);
        this.setStatusSetBy("");
    }

    public SourceCode getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public MappingStatus getMappingStatus() {
        return mappingStatus;
    }

    public void setMappingStatus(MappingStatus mappingStatus) {
        this.mappingStatus = mappingStatus;
    }

    public Equivalence getEquivalence() {
        return equivalence;
    }

    public void setEquivalence(Equivalence equivalence) {
        this.equivalence = equivalence;
    }

    public List<MappingTarget> getTargetConcepts() {
        return targetConcepts;
    }

    public void setTargetConcepts(List<MappingTarget> targetConcepts) {
        this.targetConcepts = targetConcepts;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatusSetBy() {
        return statusSetBy;
    }

    public void setStatusSetBy(String statusSetBy) {
        this.statusSetBy = statusSetBy;
    }

    public long getStatusSetOn() {
        return statusSetOn;
    }

    public void setStatusSetOn(long statusSetOn) {
        this.statusSetOn = statusSetOn;
    }

    public String getAssignedReviewer() {
        return assignedReviewer;
    }

    public void setAssignedReviewer(String assignedReviewer) {
        this.assignedReviewer = assignedReviewer;
    }
}
