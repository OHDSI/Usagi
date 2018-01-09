/*******************************************************************************
 * Copyright 2018 Observational Health Data Sciences and Informatics
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
package org.ohdsi.usagi.dataImport;

import java.util.ArrayList;
import java.util.List;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.CodeMapping.MappingStatus;
import org.ohdsi.usagi.SourceCode;
import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.UsagiSearchEngine;
import org.ohdsi.usagi.UsagiSearchEngine.ScoredConcept;
import org.ohdsi.usagi.WriteCodeMappingsToFile;
import org.ohdsi.utilities.collections.Pair;
import org.ohdsi.utilities.files.ReadCSVFileWithHeader;
import org.ohdsi.utilities.files.Row;

/**
 * Use this class to programmatically import data into the Usagi format
 * 
 * @author MSCHUEMI
 * 
 */
public class ImportData {

	public static String		SOURCE_CODE_TYPE_STRING	= "S";
	public static String		CONCEPT_TYPE_STRING		= "C";

	private UsagiSearchEngine	usagiSearchEngine;

	public void process(ImportSettings settings) {
		usagiSearchEngine = new UsagiSearchEngine(settings.usagiFolder);
		List<SourceCode> sourceCodes = new ArrayList<SourceCode>();
		for (Row row : new ReadCSVFileWithHeader(settings.sourceFile))
			sourceCodes.add(convertToSourceCode(row, settings));

		usagiSearchEngine.createDerivedIndex(sourceCodes, null);

		createInitialMapping(sourceCodes, settings);

	}

	private SourceCode convertToSourceCode(Row row, ImportSettings settings) {
		SourceCode sourceCode = new SourceCode();
		sourceCode.sourceCode = row.get(settings.sourceCodeColumn);
		sourceCode.sourceName = row.get(settings.sourceNameColumn);
		if (settings.sourceFrequencyColumn != null)
			sourceCode.sourceFrequency = row.getInt(settings.sourceFrequencyColumn);
		if (settings.autoConceptIdsColumn != null)
			if (!row.get(settings.autoConceptIdsColumn).equals(""))
				for (String conceptId : row.get(settings.autoConceptIdsColumn).split(";"))
					sourceCode.sourceAutoAssignedConceptIds.add(Integer.parseInt(conceptId));
		for (String additionalInfoColumn : settings.additionalInfoColumns)
			sourceCode.sourceAdditionalInfo.add(new Pair<String, String>(additionalInfoColumn, row.get(additionalInfoColumn)));
		return sourceCode;
	}

	private void createInitialMapping(List<SourceCode> sourceCodes, ImportSettings settings) {
		WriteCodeMappingsToFile out = new WriteCodeMappingsToFile(settings.mappingFile);
		for (SourceCode sourceCode : sourceCodes) {
			CodeMapping codeMapping = new CodeMapping(sourceCode);

			List<ScoredConcept> concepts = usagiSearchEngine.search(sourceCode.sourceName, true, sourceCode.sourceAutoAssignedConceptIds,
					settings.filterDomain, settings.filterConceptClass, settings.filterVocabulary, settings.filterStandard, settings.includeSourceTerms);
			if (concepts.size() > 0) {
				codeMapping.targetConcepts.add(concepts.get(0).concept);
				codeMapping.matchScore = concepts.get(0).matchScore;
			} else {
				codeMapping.targetConcepts.add(Concept.EMPTY_CONCEPT);
				codeMapping.matchScore = 0;
			}
			codeMapping.mappingStatus = MappingStatus.UNCHECKED;
			if (sourceCode.sourceAutoAssignedConceptIds.size() == 1 && concepts.size() > 0) {
				codeMapping.mappingStatus = MappingStatus.AUTO_MAPPED_TO_1;
			} else if (sourceCode.sourceAutoAssignedConceptIds.size() > 1 && concepts.size() > 0) {
				codeMapping.mappingStatus = MappingStatus.AUTO_MAPPED;
			}
			out.write(codeMapping);
		}
		out.close();
	}

	public static class ImportSettings {
		/**
		 * The root folder of Usagi. This is needed to locate the index
		 */
		public String		usagiFolder				= "s:/data/Usagi";

		/**
		 * The full path to the csv file containing the source code information
		 */
		public String		sourceFile				= "";

		/**
		 * The full path to where the output csv file will be written
		 */
		public String		mappingFile				= "";

		/**
		 * The domain to which the search should be restricted. Set to null if not restricting by domain
		 */
		public String		filterDomain			= null;

		/**
		 * The concept class to which the search should be restricted. Set to null if not restricting by concept class
		 */
		public String		filterConceptClass		= null;

		/**
		 * The vocabulary to which the search should be restricted. Set to null if not restricting by vocabulary
		 */
		public String		filterVocabulary		= null;

		/**
		 * Specify whether the search should be restricted to standard concepts only. If not, classification concepts will
		 * also be allowed.
		 */
		public boolean		filterStandard			= true;

		/**
		 * The name of the column containing the source codes
		 */
		public String		sourceCodeColumn;

		/**
		 * The name of the column containing the source code names / descriptions
		 */
		public String		sourceNameColumn;

		/**
		 * The name of the column containing the source code frequency
		 */
		public String		sourceFrequencyColumn;

		/**
		 * The name of the column containing the automatically assigned concept IDs
		 */
		public String		autoConceptIdsColumn;

		/**
		 * The names of the columns containing additional information about the source codes that should be displayed in Usagi
		 */
		public List<String>	additionalInfoColumns	= new ArrayList<String>();
		
		/**
		 * Include names of source concepts that map to standard concepts in the search?
		 */
		public boolean includeSourceTerms = true;
	}

}
