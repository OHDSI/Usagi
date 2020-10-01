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

/**
 * Class for holding information about a single (target) concept in the Vocabulary
 */
public class MappingTarget{
	public enum MappingType {
		REGULAR, VALUE, UNIT // Maybe OPERATOR, TYPE, etc.
	};
	public Concept concept;
	public MappingType mappingType;

	public MappingTarget(Concept concept, MappingType mappingType) {
		this.concept = concept;
		this.mappingType = mappingType;
	}

	public MappingTarget(Concept concept) {
		this(concept, MappingType.REGULAR);
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public MappingType getMappingType() {
		return mappingType;
	}

	public void setMappingType(MappingType mappingType) {
		this.mappingType = mappingType;
	}
}
