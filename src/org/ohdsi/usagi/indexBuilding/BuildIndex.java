/*******************************************************************************
 * Copyright 2015 Observational Health Data Sciences and Informatics
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.ohdsi.usagi.TargetConcept;
import org.ohdsi.usagi.UsagiSearchEngine;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.utilities.StringUtilities;
import org.ohdsi.utilities.files.FileSorter;
import org.ohdsi.utilities.files.MultiRowIterator;
import org.ohdsi.utilities.files.WriteTextFile;
import org.ohdsi.utilities.files.MultiRowIterator.MultiRowSet;
import org.ohdsi.utilities.files.ReadCSVFileWithHeader;
import org.ohdsi.utilities.files.Row;

/**
 * Builds the initial Lucene indes used by Usagi
 */
public class BuildIndex {

	public static int	MAX_WARN_COUNT	= 10;

	public static void main(String[] args) {
		Global.folder = "C:/home/Software/Usagi/";
		BuildIndex buildIndex = new BuildIndex();
		buildIndex.buildIndex("S:/Data/OMOP Standard Vocabulary V5/Vocabulary_5.0_20150717_v4", "S:/Data/LOINC/loinc.csv");
	}

	public void buildIndex(String vocabFolder, String loincFile) {
		JDialog dialog = null;
		JLabel label = null;
		if (Global.frame != null) {
			dialog = new JDialog(Global.frame, "Progress Dialog", false);

			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createRaisedBevelBorder());

			JPanel sub = new JPanel();
			sub.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
			sub.setLayout(new BoxLayout(sub, BoxLayout.Y_AXIS));

			sub.add(new JLabel("Building index. This will take a while...."));

			label = new JLabel("Starting");
			sub.add(label);
			panel.add(sub);
			dialog.add(panel);

			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setSize(300, 75);
			dialog.setLocationRelativeTo(Global.frame);
			dialog.setUndecorated(true);
			dialog.setModal(true);
		}
		BuildThread thread = new BuildThread(vocabFolder, loincFile, label, dialog);
		thread.start();
		if (dialog != null)
			dialog.setVisible(true);
		try {
			thread.join();
			JOptionPane.showMessageDialog(Global.frame, "Please restart Usagi to use the new index");
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class BuildThread extends Thread {

		private JDialog	dialog;
		private JLabel	label;
		private String	vocabFolder;
		private String	loincFile;

		public BuildThread(String vocabFolder, String loincFile, JLabel label, JDialog dialog) {
			this.vocabFolder = vocabFolder;
			this.loincFile = loincFile;
			this.label = label;
			this.dialog = dialog;
		}

		private void report(String message) {
			if (label != null)
				label.setText(message);
		}

		public void run() {
			// Load LOINC information into memory if user wants to include it in the index:
			try {
			Map<String, String> loincToInfo = null;
			if (loincFile != null) {
				report("Loading LOINC additional information");
				loincToInfo = loadLoincInfo(loincFile);
			}
			report("Sorting vocabulary files");
			FileSorter.delimiter = '\t';
			FileSorter.sort(vocabFolder + "/CONCEPT.csv", new String[] { "CONCEPT_ID" }, new boolean[] { true });
			FileSorter.sort(vocabFolder + "/CONCEPT_SYNONYM.csv", new String[] { "CONCEPT_ID" }, new boolean[] { true });

			report("Adding concepts to index");
			UsagiSearchEngine usagiSearchEngine = new UsagiSearchEngine(Global.folder);
			usagiSearchEngine.createNewMainIndex();

			Iterator<Row> conceptIterator = new ReadCSVFileWithHeader(vocabFolder + "/CONCEPT.csv", '\t').iterator();
			Iterator<Row> conceptSynIterator = new ReadCSVFileWithHeader(vocabFolder + "/CONCEPT_SYNONYM.csv", '\t').iterator();
			@SuppressWarnings("unchecked")
			MultiRowIterator iterator = new MultiRowIterator("CONCEPT_ID", true, new String[] { "concept", "concept_synonym" }, new Iterator[] {
					conceptIterator, conceptSynIterator });
			Set<String> vocabularies = new HashSet<String>();
			Set<String> conceptClassIds = new HashSet<String>();
			Set<String> domainIds = new HashSet<String>();
			int warnCount = 0;
			while (iterator.hasNext()) {
				MultiRowSet multiRowSet = iterator.next();
				if (multiRowSet.get("concept").size() == 0) {
					if (warnCount < MAX_WARN_COUNT) {
						warnCount++;
						System.out.println("No concept found for concept ID " + multiRowSet.linkingId);
					}
				} else {

					Row conceptRow = multiRowSet.get("concept").get(0);
					if (conceptRow.getCells().size() > 2) // Extra check to catch badly formatted rows (which are in a vocab we don't care about)
						if (conceptRow.get("STANDARD_CONCEPT").equals("S")) {
							vocabularies.add(conceptRow.get("VOCABULARY_ID"));
							conceptClassIds.add(conceptRow.get("CONCEPT_CLASS_ID"));
							domainIds.add(conceptRow.get("DOMAIN_ID"));
							List<Row> synonymRows = multiRowSet.get("concept_synonym");

							// Adding concept name as synonym:
							Row tempRow = new Row();
							tempRow.add("CONCEPT_SYNONYM_NAME", conceptRow.get("CONCEPT_NAME"));
							synonymRows.add(tempRow);

							for (Row synonymRow : synonymRows) {
								TargetConcept concept = new TargetConcept();
								concept.term = synonymRow.get("CONCEPT_SYNONYM_NAME");
								concept.conceptClass = conceptRow.get("CONCEPT_CLASS_ID");
								concept.conceptCode = conceptRow.get("CONCEPT_CODE");
								concept.conceptId = conceptRow.getInt("CONCEPT_ID");
								concept.conceptName = conceptRow.get("CONCEPT_NAME");
								for (String domain : conceptRow.get("DOMAIN_ID").split("/")) {
									if (domain.equals("Obs"))
										domain = "Observation";
									if (domain.equals("Meas"))
										domain = "Measurement";
									concept.domains.add(domain);
								}
								concept.invalidReason = conceptRow.get("INVALID_REASON");
								concept.validEndDate = conceptRow.get("VALID_END_DATE");
								concept.validStartDate = conceptRow.get("VALID_START_DATE");
								concept.vocabulary = conceptRow.get("VOCABULARY_ID");
								if (loincToInfo != null && concept.vocabulary.equals("LOINC")) {
									String info = loincToInfo.get(concept.conceptCode);
									if (info != null)
										concept.additionalInformation = info;
								}
								if (concept.additionalInformation == null)
									concept.additionalInformation = "";
								usagiSearchEngine.addConceptToIndex(concept);
							}
						}
				}
			}
			usagiSearchEngine.close();
			saveSorted(vocabularies, Global.folder + "/VocabularyIds.txt");
			saveSorted(conceptClassIds, Global.folder + "/ConceptClassIds.txt");
			saveSorted(domainIds, Global.folder + "/DomainIds.txt");
			if (dialog != null)
				dialog.setVisible(false);
			} catch (Exception e){
				if (Global.frame != null)
				  JOptionPane.showMessageDialog(Global.frame, StringUtilities.wordWrap(e.getLocalizedMessage(), 80), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				if (dialog != null)
					dialog.setVisible(false);
			}
		}

		private void saveSorted(Set<String> set, String fileName) {
			List<String> list = new ArrayList<String>(set);
			Collections.sort(list);
			WriteTextFile out = new WriteTextFile(fileName);
			for (String item : list)
				out.writeln(item);
			out.close();
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
				info.append("Comments: ");
				info.append(row.get("COMMENTS"));
				info.append("\n");
				info.append("Formula: ");
				info.append(row.get("FORMULA"));
				info.append("\n");
				info.append("Example units: ");
				info.append(row.get("EXAMPLE_UNITS"));
				info.append("\n");
				loincToInfo.put(row.get("LOINC_NUM"), info.toString());
			}
			return loincToInfo;
		}
	}
}
