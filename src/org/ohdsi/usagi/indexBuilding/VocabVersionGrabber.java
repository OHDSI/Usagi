package org.ohdsi.usagi.indexBuilding;

import org.ohdsi.usagi.ui.Global;
import org.ohdsi.utilities.files.Row;
import org.ohdsi.utilities.files.WriteTextFile;

public class VocabVersionGrabber {
	public void grabVersion(String vocabFolder) {
		for (Row row : new ReadAthenaFile(vocabFolder + "/VOCABULARY.csv")) {
			if (row.get("vocabulary_name").contains("Standardized Vocabularies")) {
				WriteTextFile out = new WriteTextFile(Global.folder + "/vocabularyVersion.txt");
				out.writeln(row.get("vocabulary_version"));
				out.close();
			}
		}
	}
}
