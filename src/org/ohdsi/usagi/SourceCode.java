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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ohdsi.utilities.StringUtilities;
import org.ohdsi.utilities.collections.Pair;
import org.ohdsi.utilities.files.Row;

/**
 * Data structure for containing information about a source code
 */
public class SourceCode {

	public String						sourceCode;
	public String						sourceName;
	public String						sourceValueCode;
	public String						sourceValueName;
	public String						sourceUnitName;
	public int							sourceFrequency;
	public Set<Integer>					sourceAutoAssignedConceptIds	= new HashSet<Integer>();
	public List<Pair<String, String>>	sourceAdditionalInfo			= new ArrayList<Pair<String, String>>();

	private final static String			ADDITIONAL_INFO_PREFIX			= "ADD_INFO:";

	public Row toRow() {
		Row row = new Row();
		row.add("sourceCode", sourceCode);
		row.add("sourceName", sourceName);
		row.add("sourceValueCode", sourceValueCode);
		row.add("sourceValueName", sourceValueName);
		row.add("sourceUnitName", sourceUnitName);
		row.add("sourceFrequency", sourceFrequency);
		row.add("sourceAutoAssignedConceptIds", StringUtilities.join(sourceAutoAssignedConceptIds, ";"));
		for (Pair<String, String> pair : sourceAdditionalInfo) {
			row.add(ADDITIONAL_INFO_PREFIX + pair.getItem1(), pair.getItem2());
		}
		return row;
	}

	public SourceCode() {
	}

	public SourceCode(Row row) {
		sourceCode = row.get("sourceCode");
		sourceName = row.get("sourceName");
		if (row.getFieldNames().contains("sourceValueCode")) {
			// Assume that if source value code exists, then other new fields as well
			sourceValueCode = row.get("sourceValueCode");
			sourceValueName = row.get("sourceValueName");
			sourceUnitName = row.get("sourceUnitName");
		}
		sourceFrequency = row.getInt("sourceFrequency");
		sourceAutoAssignedConceptIds = parse(row.get("sourceAutoAssignedConceptIds"));
		for (String field : row.getFieldNames())
			if (field.startsWith(ADDITIONAL_INFO_PREFIX)) {
				String name = field.substring(ADDITIONAL_INFO_PREFIX.length(), field.length());
				sourceAdditionalInfo.add(new Pair<String, String>(name, row.get(field)));
			}
	}

	private Set<Integer> parse(String string) {
		if (string.length() == 0)
			return Collections.emptySet();
		else {
			Set<Integer> conceptIds = new HashSet<Integer>();
			for (String cid : string.split(";"))
				conceptIds.add(Integer.parseInt(cid));
			return conceptIds;
		}
	}
}
