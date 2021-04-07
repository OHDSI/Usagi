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
package org.ohdsi.usagi;

import org.ohdsi.utilities.files.Row;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * Class for holding information about a single (target) concept in the Vocabulary
 */
@Entity
public class Concept {
	public static Concept	EMPTY_CONCEPT	= createEmptyConcept();

	@PrimaryKey
	public int				conceptId;
	public String			conceptName;
	public String			domainId;
	public String			vocabularyId;
	public String			conceptClassId;
	public String			standardConcept;
	public String			conceptCode;
	public String			validStartDate;
	public String			validEndDate;
	public String			invalidReason;
	public int				parentCount;
	public int				childCount;

	public String			additionalInformation;

	public Concept(Row row) {
		conceptId = row.getInt("concept_id");
		conceptName = row.get("concept_name");
		domainId = row.get("domain_id");
		vocabularyId = row.get("vocabulary_id");
		conceptClassId = row.get("concept_class_id");
		standardConcept = row.get("standard_concept");
		conceptCode = row.get("concept_code");
		validStartDate = row.get("valid_start_date");
		validEndDate = row.get("valid_end_date");
		invalidReason = row.get("invalid_reason");
		additionalInformation = "";
	}

	public static Concept createEmptyConcept() {
		Concept concept = new Concept();
		concept.conceptId = 0;
		concept.conceptName = "Unmapped";
		concept.conceptClassId = "";
		concept.vocabularyId = "";
		concept.conceptCode = "";
		concept.domainId = "";
		concept.validStartDate = "";
		concept.validEndDate = "";
		concept.invalidReason = "";
		concept.standardConcept = "";
		concept.additionalInformation = "";
		concept.parentCount = 0;
		concept.childCount = 0;
		return concept;
	}

	public Concept() {
	}

	@Override
	public boolean equals(Object o) {
		// Only compare conceptId
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Concept concept = (Concept) o;
		return conceptId == concept.conceptId;
	}
}
