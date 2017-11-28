/*******************************************************************************
 * Copyright 2017 Observational Health Data Sciences and Informatics
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
package org.ohdsi.usagi.indexBuilding;

import java.util.HashMap;
import java.util.Map;

import org.ohdsi.usagi.BerkeleyDbEngine;
import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.MapsToRelationship;
import org.ohdsi.usagi.ParentChildRelationShip;
import org.ohdsi.usagi.indexBuilding.IndexBuildCoordinator.BuildThread;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.utilities.collections.IntHashSet;
import org.ohdsi.utilities.files.ReadCSVFileWithHeader;
import org.ohdsi.utilities.files.Row;

public class BerkeleyDbBuilder {
	private BerkeleyDbEngine	dbEngine;
	private BuildThread			buildThread;

	public void buildIndex(String vocabFolder, String loincFileName, BuildThread buildThread) {
		this.buildThread = buildThread;
		dbEngine = new BerkeleyDbEngine(Global.folder);
		dbEngine.createDatabase();
		IntHashSet validConceptIds = loadValidConceptIds(vocabFolder + "/CONCEPT.csv");
		loadAncestors(vocabFolder + "/CONCEPT_ANCESTOR.csv", validConceptIds);
		loadRelationships(vocabFolder + "/CONCEPT_RELATIONSHIP.csv", validConceptIds);
		loadConcepts(vocabFolder + "/CONCEPT.csv", loincFileName);
		dbEngine.shutdown();
	}

	private IntHashSet loadValidConceptIds(String conceptFileName) {
		IntHashSet validConceptIds = new IntHashSet();
		for (Row row : new ReadAthenaFile(conceptFileName)) 
			if (row.get("invalid_reason") == null)
				validConceptIds.add(row.getInt("concept_id"));
		return validConceptIds;
	}

	private void loadRelationships(String conceptRelationshipFileName, IntHashSet validConceptIds) {
		buildThread.report("Loading relationship information");
		int count = 0;
		for (Row row : new ReadAthenaFile(conceptRelationshipFileName)) {
			if (row.get("relationship_id").equals("Maps to") && row.get("invalid_reason") == null && !row.get("concept_id_1").equals(row.get("concept_id_2"))
					&& validConceptIds.contains(row.getInt("concept_id_1")) && validConceptIds.contains(row.getInt("concept_id_2"))) {
				MapsToRelationship mapsToRelationship = new MapsToRelationship(row);
				dbEngine.put(mapsToRelationship);
			}
			count++;
			if (count % 100000 == 0)
				System.out.println("Processed " + count + " relationships");
		}
	}

	private void loadAncestors(String conceptAncestorFileName, IntHashSet validConceptIds) {
		buildThread.report("Loading parent-child information");
		int count = 0;
		for (Row row : new ReadAthenaFile(conceptAncestorFileName)) {
			if (row.get("min_levels_of_separation").equals("1") && !row.get("ancestor_concept_id").equals(row.get("descendant_concept_id"))
					&& validConceptIds.contains(row.getInt("ancestor_concept_id")) && validConceptIds.contains(row.getInt("descendant_concept_id"))) {
				ParentChildRelationShip parentChildRelationship = new ParentChildRelationShip(row);
				dbEngine.put(parentChildRelationship);
			}
			count++;
			if (count % 100000 == 0)
				System.out.println("Processed " + count + " relationships");
		}
	}

	private void loadConcepts(String conceptFileName, String loincFileName) {
		Map<String, String> loincToInfo = null;
		if (loincFileName != null) {
			buildThread.report("Loading LOINC additional information");
			loincToInfo = loadLoincInfo(loincFileName);
		}
		buildThread.report("Loading concept information");
		int count = 0;
		for (Row row : new ReadAthenaFile(conceptFileName)) {
			Concept concept = new Concept(row);
			if (concept.invalidReason == null) {
				if (loincToInfo != null) {
					String info = loincToInfo.get(concept.conceptCode);
					if (info != null)
						concept.additionalInformation = info;
				}
				concept.parentCount = dbEngine.getParentChildRelationshipsByChildConceptId(concept.conceptId).size();
				concept.childCount = dbEngine.getParentChildRelationshipsByParentConceptId(concept.conceptId).size();
				dbEngine.put(concept);
				count++;
				if (count % 100000 == 0)
					System.out.println("Loaded " + count + " concepts");
			}
		}
	}

	private Map<String, String> loadLoincInfo(String loincFile) {
		Map<String, String> loincToInfo = new HashMap<String, String>();
		for (Row row : new ReadCSVFileWithHeader(loincFile)) {
			StringBuilder info = new StringBuilder();
			info.append("LOINC concept information\n\n");
			info.append("Component: ");
			info.append(row.get("COMPONENT"));
			info.append("\n");
			info.append("Property: ");
			info.append(row.get("PROPERTY"));
			info.append("\n");
			info.append("Time aspect: ");
			info.append(row.get("TIME_ASPCT"));
			info.append("\n");
			info.append("System: ");
			info.append(row.get("SYSTEM"));
			info.append("\n");
			info.append("Scale type: ");
			info.append(row.get("SCALE_TYP"));
			info.append("\n");
			info.append("Method type: ");
			info.append(row.get("METHOD_TYP"));
			info.append("\n");
			info.append("Definition description: ");
			info.append(row.get("DefinitionDescription"));
			info.append("\n");
			info.append("Formula: ");
			info.append(row.get("FORMULA"));
			info.append("\n");
			info.append("Example units: ");
			info.append(row.get("EXAMPLE_UCUM_UNITS"));
			info.append("\n");
			loincToInfo.put(row.getCells().get(0), info.toString());
		}
		return loincToInfo;
	}
}
