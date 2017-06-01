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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ohdsi.usagi.BerkeleyDbEngine;
import org.ohdsi.usagi.MapsToRelationship;
import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.UsagiSearchEngine;
import org.ohdsi.usagi.indexBuilding.IndexBuildCoordinator.BuildThread;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.utilities.files.Row;
import org.ohdsi.utilities.files.WriteTextFile;

import com.sleepycat.persist.EntityCursor;

/**
 * Builds the initial Lucene indes used by Usagi
 */
public class LuceneIndexBuilder {

	public void buildIndex(String vocabFolder, String loincFile, BuildThread buildThread) {
		buildThread.report("Adding concepts to search index");
		UsagiSearchEngine usagiSearchEngine = new UsagiSearchEngine(Global.folder);
		usagiSearchEngine.createNewMainIndex();
		Set<String> vocabularies = new HashSet<String>();
		Set<String> conceptClassIds = new HashSet<String>();
		Set<String> domainIds = new HashSet<String>();

		BerkeleyDbEngine dbEngine = new BerkeleyDbEngine(Global.folder);
		dbEngine.openForReading();
		int count = 0;
		EntityCursor<Concept> cursor = dbEngine.getConceptCursor();
		for (Concept concept : cursor) {
			if (concept.standardConcept.equals("S") || concept.standardConcept.equals("C")) {
				usagiSearchEngine.addTermToIndex(concept.conceptName, UsagiSearchEngine.CONCEPT_TERM, concept);
				vocabularies.add(concept.vocabularyId);
				conceptClassIds.add(concept.conceptClassId);
				domainIds.add(concept.domainId);
			} else {
				MapsToRelationship mapsToRelationship = dbEngine.getMapsToRelationship(concept.conceptId);
				if (mapsToRelationship != null) {
					Concept mappedToConcept = dbEngine.getConcept(mapsToRelationship.conceptId2);
					if (mappedToConcept == null)
						throw new RuntimeException("Error: cannot find concept with concept ID " + mapsToRelationship.conceptId2);
					if (!mappedToConcept.conceptName.toLowerCase().equals(concept.conceptName.toLowerCase()))
						usagiSearchEngine.addTermToIndex(concept.conceptName, UsagiSearchEngine.SOURCE_TERM, mappedToConcept);
				}
			}
			count++;
			if (count % 100000 == 0)
				System.out.println("Processed " + count + " concepts");
		}
		buildThread.report("Adding synonyms to search index");
		count = 0;
		for (Row row : new ReadAthenaFile(vocabFolder + "/CONCEPT_SYNONYM.csv")) {
			Concept concept = dbEngine.getConcept(row.getInt("concept_id"));
			if (concept == null) {
				System.err.println("Concept not found for concept ID " + row.getInt("concept_id"));
			} else {
				if ((concept.standardConcept.equals("S") || concept.standardConcept.equals("C"))
						&& !concept.conceptName.toLowerCase().equals(row.get("concept_synonym_name").toLowerCase())) {
					usagiSearchEngine.addTermToIndex(row.get("concept_synonym_name"), UsagiSearchEngine.CONCEPT_TERM, concept);
				} else {
					MapsToRelationship mapsToRelationship = dbEngine.getMapsToRelationship(concept.conceptId);
					if (mapsToRelationship != null) {
						Concept mappedToConcept = dbEngine.getConcept(mapsToRelationship.conceptId2);
						if (!mappedToConcept.conceptName.toLowerCase().equals(row.get("concept_synonym_name").toLowerCase()))
							usagiSearchEngine.addTermToIndex(row.get("concept_synonym_name"), UsagiSearchEngine.SOURCE_TERM, mappedToConcept);
					}
				}
			}
			count++;
			if (count % 100000 == 0)
				System.out.println("Processed " + count + " synonyms");
		}
		usagiSearchEngine.close();
		cursor.close();
		dbEngine.shutdown();
		saveSorted(vocabularies, Global.folder + "/VocabularyIds.txt");
		saveSorted(conceptClassIds, Global.folder + "/ConceptClassIds.txt");
		saveSorted(domainIds, Global.folder + "/DomainIds.txt");
	}

	private void saveSorted(Set<String> set, String fileName) {
		List<String> list = new ArrayList<String>(set);
		Collections.sort(list);
		WriteTextFile out = new WriteTextFile(fileName);
		for (String item : list)
			out.writeln(item);
		out.close();
	}

}
