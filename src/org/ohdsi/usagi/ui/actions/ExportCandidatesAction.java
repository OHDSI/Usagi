package org.ohdsi.usagi.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Set; // Added for Set<Integer>
import java.util.Vector; // Added for Vector<String>
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.SourceCode;
import org.ohdsi.usagi.UsagiSearchEngine.ScoredConcept;
import org.ohdsi.usagi.ui.FilterPanel; // Added import for FilterPanel
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.utilities.files.Row;
import org.ohdsi.utilities.files.WriteCSVFileWithHeader;

public class ExportCandidatesAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    public ExportCandidatesAction() {
        putValue(Action.NAME, "Export candidates");
        putValue(Action.SHORT_DESCRIPTION, "Export all source codes with their candidates and scores using specified filters");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* This function configure filters for exporting candidates and writes the
         * exported data to a CSV file.
         */
        if (Global.mapping == null || Global.mapping.isEmpty()) {
            JOptionPane.showMessageDialog(Global.frame, "No mappings to export.", "Export Candidates", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 1. Create and show FilterPanel for user to specify filters
        FilterPanel exportFilterPanel = new FilterPanel();

        int filterConfigResult = JOptionPane.showConfirmDialog(Global.frame, exportFilterPanel,
                "Configure Export Filters", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (filterConfigResult != JOptionPane.OK_OPTION) {
            return; // User cancelled filter configuration
        }

        // 2. Get File Path using JFileChooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Candidates to CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        String suggestedFileName = "usagi_candidates_export.csv";
        fileChooser.setSelectedFile(new File(suggestedFileName));

        if (fileChooser.showSaveDialog(Global.frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getParentFile(), file.getName() + ".csv");
            }

            if (file.exists()) {
                int response = JOptionPane.showConfirmDialog(Global.frame,
                        "The file " + file.getName() + " already exists. Do you want to overwrite it?",
                        "Confirm Overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            WriteCSVFileWithHeader writer = null;
            try {
                writer = new WriteCSVFileWithHeader(file.getAbsolutePath());

                // 3. Write data using filters from exportFilterPanel
                for (CodeMapping codeMappingEntry : Global.mapping) {
                    SourceCode sourceCode = codeMappingEntry.getSourceCode();

                    // Get filter settings from the configured exportFilterPanel
                    Set<Integer> conceptIdsForFilter = null;
                    if (exportFilterPanel.getFilterByAuto()) {
                        conceptIdsForFilter = sourceCode.sourceAutoAssignedConceptIds;
                    }
                    boolean standardFilter = exportFilterPanel.getFilterStandard();
                    Vector<String> conceptClassesFilter = null;
                    if (exportFilterPanel.getFilterByConceptClasses()) {
                        conceptClassesFilter = exportFilterPanel.getConceptClass();
                    }
                    Vector<String> vocabulariesFilter = null;
                    if (exportFilterPanel.getFilterByVocabularies()) {
                        vocabulariesFilter = exportFilterPanel.getVocabulary();
                    }
                    Vector<String> domainsFilter = null;
                    if (exportFilterPanel.getFilterByDomains()) {
                        domainsFilter = exportFilterPanel.getDomain();
                    }
                    boolean includeSourceFilter = exportFilterPanel.getIncludeSourceTerms();
                    boolean useMlt = true; // Default for search, can also be made a filter option if needed

                    List<ScoredConcept> candidates = Global.usagiSearchEngine.search(
                            sourceCode.sourceName,
                            useMlt,
                            conceptIdsForFilter,
                            domainsFilter,
                            conceptClassesFilter,
                            vocabulariesFilter,
                            standardFilter,
                            includeSourceFilter
                    ); 
                    if (candidates == null || candidates.isEmpty()) {
                        Row row = new Row();
                        row.add("source_code", sourceCode.sourceCode);
                        row.add("source_name", sourceCode.sourceName);
                        // Add other source code fields as needed
                        row.add("target_concept_id", "");
                        row.add("target_domain_id", "");
                        row.add("match_score", "");
                        writer.write(row);
                    } else {
                        for (ScoredConcept candidate : candidates) {
                            Row row = new Row();
                            row.add("source_code", sourceCode.sourceCode);
                            row.add("source_name", sourceCode.sourceName);
                            // Add other source code fields
                            row.add("target_concept_id", String.valueOf(candidate.concept.conceptId));
                            row.add("target_domain_id", candidate.concept.domainId);
                            row.add("match_score", String.valueOf(candidate.matchScore));
                            writer.write(row);
                        }
                    }
                }
                JOptionPane.showMessageDialog(Global.frame, "Export completed successfully to " + file.getAbsolutePath(), "Export Candidates", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Global.frame, "Error during export: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
