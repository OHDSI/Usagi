/*******************************************************************************
 * Copyright 2014 Observational Health Data Sciences and Informatics
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

import java.util.HashSet;
import java.util.Set;

import org.ohdsi.usagi.CodeMapping.MappingStatus;
import org.ohdsi.utilities.StringUtilities;
import org.ohdsi.utilities.files.Row;

/**
 * Class for holding information about a single (target) concept in the Vocabulary
 */
public class TargetConcept {
	public MappingStatus		mappingStatus;
	public String				term;
	public int					conceptId;
	public String				conceptName;
	public String				conceptClass;
	public String				vocabulary;
	public String				conceptCode;
	public Set<String>			domains			= new HashSet<String>(1);
	public String				validStartDate;
	public String				validEndDate;
	public String				invalidReason;
	public String				additionalInformation;
	public static TargetConcept	EMPTY_CONCEPT	= createEmptyConcept();

	public TargetConcept(Row row) {
		row.upperCaseFieldNames();
		if (row.getFieldNames().contains("CONCEPT_ID")) {
			term = row.get("TERM");
			conceptId = row.getInt("CONCEPT_ID");
			conceptName = row.get("CONCEPT_NAME");
			conceptClass = row.get("CONCEPT_CLASS");
			vocabulary = row.get("VOCABULARY");
			conceptCode = row.get("CONCEPT_CODE");
			validStartDate = row.get("VALID_START_DATE");
			validEndDate = row.get("VALID_END_DATE");
			invalidReason = row.get("INVALID_REASON");
			for (String domain : row.get("DOMAINS").split("/"))
				domains.add(domain);
			additionalInformation = row.get("ADDITIONAL_INFORMATION");
		} else {
			term = row.get("TERM");
			conceptId = row.getInt("CONCEPTID");
			conceptName = row.get("CONCEPTNAME");
			conceptClass = row.get("CONCEPTCLASS");
			vocabulary = row.get("VOCABULARY");
			conceptCode = row.get("CONCEPTCODE");
			validStartDate = row.get("VALIDSTARTDATE");
			validEndDate = row.get("VALIDENDDATE");
			invalidReason = row.get("INVALIDREASON");
			for (String domain : row.get("DOMAINS").split("/"))
				domains.add(domain);
			additionalInformation = row.get("ADDITIONALINFORMATION");
		}
	}

	private static TargetConcept createEmptyConcept() {
		TargetConcept concept = new TargetConcept();
		concept.term = "";
		concept.conceptId = 0;
		concept.conceptName = "";
		concept.conceptClass = "";
		concept.vocabulary = "";
		concept.conceptCode = "";
		concept.validStartDate = "";
		concept.validEndDate = "";
		concept.invalidReason = "";
		concept.additionalInformation = "This is an empty concept";
		return concept;
	}

	public TargetConcept() {
	}

	public TargetConcept(TargetConcept concept) {
		term = concept.term;
		conceptId = concept.conceptId;
		conceptName = concept.conceptName;
		conceptClass = concept.conceptClass;
		vocabulary = concept.vocabulary;
		conceptCode = concept.conceptCode;
		validStartDate = concept.validStartDate;
		validEndDate = concept.validEndDate;
		invalidReason = concept.invalidReason;
		domains.addAll(concept.domains);
		additionalInformation = concept.additionalInformation;
	}

	public Row toRow() {
		Row row = new Row();
		row.add("term", term);
		row.add("conceptId", conceptId);
		row.add("conceptName", conceptName);
		row.add("conceptClass", conceptClass);
		row.add("vocabulary", vocabulary);
		row.add("conceptCode", conceptCode);
		row.add("validStartDate", validStartDate);
		row.add("validEndDate", validEndDate);
		row.add("invalidReason", invalidReason);
		row.add("domains", StringUtilities.join(domains, "/"));
		row.add("additionalInformation", additionalInformation);
		return row;
	}

}
