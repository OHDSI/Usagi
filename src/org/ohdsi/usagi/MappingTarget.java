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

import java.util.Objects;

/**
 * Class for holding information about a single (target) concept in the Vocabulary
 */
public class MappingTarget{
	public enum Type {
		MAPS_TO, MAPS_TO_VALUE, MAPS_TO_UNIT;

		public static Type valueOfCompat(String value) {
			// For backwards compatibility with old types
			switch (value) {
				case "EVENT": return MAPS_TO;
				case "VALUE": return MAPS_TO_VALUE;
				case "UNIT": return MAPS_TO_UNIT;
				default: return valueOf(value);
			}
		}
	}

	private final Concept concept;
	private final String createdBy;
	private final long createdTime;
	private Type mappingType;

	public MappingTarget() {
		this.concept = Concept.createEmptyConcept();
		this.mappingType = Type.MAPS_TO;
		this.createdBy = "";
		this.createdTime = 0;
	}

	public MappingTarget(Concept concept, String createdBy) {
		this(concept, Type.MAPS_TO, createdBy);
	}

	public MappingTarget(Concept concept, Type mappingType, String createdBy) {
		this.concept = concept;
		this.mappingType = mappingType;
		this.createdBy = createdBy;
		this.createdTime = System.currentTimeMillis();
	}

	public MappingTarget(Concept concept, Type mappingType, String createdBy, long createdTime) {
		this.concept = concept;
		this.mappingType = mappingType;
		this.createdBy = createdBy;
		this.createdTime = createdTime;
	}

	public Concept getConcept() {
		return concept;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public Type getMappingType() {
		return mappingType;
	}

	public void setMappingType(Type mappingType) {
		this.mappingType = mappingType;
	}

	@Override
	public boolean equals(Object o) {
		// Only compares target concept and mappingType.
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MappingTarget that = (MappingTarget) o;
		return Objects.equals(concept, that.concept) && mappingType == that.mappingType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(concept, mappingType);
	}
}
