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
package org.ohdsi.usagi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.table.TableRowSorter;

import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.MapsToRelationship;
import org.ohdsi.usagi.ParentChildRelationShip;

public class ConceptInformationDialog extends JFrame {

	private static final long	serialVersionUID	= -2112565437136224217L;
	private Concept				activeConcept;
	private JLabel				conceptNameLabel;
	private JButton				backButton;
	private JButton				forwardButton;
	private ConceptTableModel	parentConceptTableModel;
	private UsagiTable			parentsConceptTable;
	private ConceptTableModel	currentConceptTableModel;
	private ConceptTableModel	childrenConceptTableModel;
	private UsagiTable			childrenConceptTable;
	private ConceptTableModel	sourceConceptTableModel;
	private JTextArea			synonymArea;
	private List<Concept>		history				= new ArrayList<>();
	private int					historyCursor		= -1;
	private boolean				updating			= false;

	public ConceptInformationDialog() {
		setTitle("Concept information");
		setLayout(new BorderLayout());
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createCenterPanel(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		setSize(800, 600);
		setLocationRelativeTo(Global.frame);
		UsagiMain.loadIcons(this);
	}

	private Component createCenterPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder());
		panel.setLayout(new BorderLayout());
		JTabbedPane tabPanel = createTabPanel();
		panel.add(tabPanel, BorderLayout.CENTER);
		return panel;
	}

	private JTabbedPane createTabPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Hierarchy", createHierarchyPanel());
		tabbedPane.addTab("Source concepts", createSourceConceptPanel());
		tabbedPane.addTab("Synonyms", createSynonymsPanel());
		return tabbedPane;
	}

	private Component createSourceConceptPanel() {
		sourceConceptTableModel = new ConceptTableModel(false);
		UsagiTable sourceConceptTable = buildConceptTable(sourceConceptTableModel, false);

		JScrollPane sourcePane = new JScrollPane(sourceConceptTable);
		sourcePane.setBorder(BorderFactory.createTitledBorder("Source concepts"));
		sourcePane.setMinimumSize(new Dimension(500, 50));
		sourcePane.setPreferredSize(new Dimension(500, 50));
		return sourcePane;
	}

	private Component createHierarchyPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder());
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;

		parentConceptTableModel = new ConceptTableModel(false);
		parentsConceptTable = buildConceptTable(parentConceptTableModel, true);
		parentsConceptTable.getSelectionModel().addListSelectionListener(event -> {
			if (!updating) {
				updating = true;
				int viewRow = parentsConceptTable.getSelectedRow();
				Global.conceptInfoAction.setEnabled(true);
				int modelRow = parentsConceptTable.convertRowIndexToModel(viewRow);
				Global.conceptInformationDialog.setActiveConcept(parentConceptTableModel.getConcept(modelRow));
				updating = false;
			}
		});

		JScrollPane parentsPane = new JScrollPane(parentsConceptTable);
		parentsPane.setBorder(BorderFactory.createTitledBorder("Parent concepts"));
		parentsPane.setMinimumSize(new Dimension(500, 50));
		parentsPane.setPreferredSize(new Dimension(500, 50));
		c.gridy = 0;
		c.weighty = 0.3;
		panel.add(parentsPane, c);

		currentConceptTableModel = new ConceptTableModel(false);
		UsagiTable currentConceptTable = buildConceptTable(currentConceptTableModel, false);
		JScrollPane currentConceptPane = new JScrollPane(currentConceptTable);
		currentConceptPane.setBorder(BorderFactory.createTitledBorder("Current concept"));
		currentConceptPane.setMinimumSize(new Dimension(500, 20));
		currentConceptPane.setPreferredSize(new Dimension(500, 20));
		c.gridy = 1;
		c.weighty = 0.2;
		panel.add(currentConceptPane, c);

		childrenConceptTableModel = new ConceptTableModel(false);
		childrenConceptTable = buildConceptTable(childrenConceptTableModel, true);
		childrenConceptTable.getSelectionModel().addListSelectionListener(event -> {
			if (!updating) {
				updating = true;
				int viewRow = childrenConceptTable.getSelectedRow();
				Global.conceptInfoAction.setEnabled(true);
				int modelRow = childrenConceptTable.convertRowIndexToModel(viewRow);
				Global.conceptInformationDialog.setActiveConcept(childrenConceptTableModel.getConcept(modelRow));
				updating = false;
			}
		});

		JScrollPane childrenPane = new JScrollPane(childrenConceptTable);
		childrenPane.setBorder(BorderFactory.createTitledBorder("Children concepts"));
		childrenPane.setMinimumSize(new Dimension(500, 50));
		childrenPane.setPreferredSize(new Dimension(500, 50));
		c.gridy = 2;
		c.weighty = 0.5;
		panel.add(childrenPane, c);
		return panel;
	}

	private static UsagiTable buildConceptTable(ConceptTableModel tableModel, boolean rowSelectionAllowed) {
		UsagiTable result = new UsagiTable(tableModel);
		result.setRowSorter(new TableRowSorter<>(tableModel));
		result.setPreferredScrollableViewportSize(new Dimension(500, 45));
		result.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		result.hideColumn("Parents");
		result.hideColumn("Children");
		result.hideColumn("Valid start date");
		result.hideColumn("Valid end date");
		result.hideColumn("Invalid reason");

		result.setRowSelectionAllowed(rowSelectionAllowed);
		if (rowSelectionAllowed) {
			result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		return result;
	}

	private Component createSynonymsPanel() {
		synonymArea = new JTextArea();
		synonymArea.setEditable(false);
		JScrollPane synonymPane = new JScrollPane(synonymArea);
		synonymPane.setBorder(BorderFactory.createTitledBorder("Synonyms"));
		synonymPane.setMinimumSize(new Dimension(500, 50));
		synonymPane.setPreferredSize(new Dimension(500, 50));
		return synonymPane;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		JButton replaceButton = new JButton("Replace concept");
		replaceButton.setToolTipText("Replace selected concept");
		replaceButton.addActionListener(e -> {
			Global.mappingDetailPanel.replaceConcepts(history.get(historyCursor));
			Global.frame.requestFocus();
		});
		buttonPanel.add(replaceButton);
		JButton addButton = new JButton("Add concept");
		addButton.setToolTipText("Add selected concept");
		addButton.addActionListener(e -> {
			Global.mappingDetailPanel.addConcept(history.get(historyCursor));
			Global.frame.requestFocus();
		});
		buttonPanel.add(addButton);
		return buttonPanel;
	}

	private Component createHeaderPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder());
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		conceptNameLabel = new JLabel("No concept selected");
		panel.add(Box.createHorizontalGlue());
		panel.add(conceptNameLabel);
		backButton = new JButton("<");
		backButton.setToolTipText("Back to previous concept");
		backButton.addActionListener(arg0 -> goBack());
		backButton.setEnabled(false);
		panel.add(backButton);
		forwardButton = new JButton(">");
		forwardButton.setToolTipText("Forward to next concept");
		forwardButton.addActionListener(arg0 -> goForward());
		forwardButton.setEnabled(false);
		panel.add(forwardButton);
		return panel;
	}

	private void goBack() {
		historyCursor--;
		Concept concept = history.get(historyCursor);
		forwardButton.setEnabled(true);
		if (historyCursor == 0)
			backButton.setEnabled(false);
		showConcept(concept);
	}

	private void goForward() {
		historyCursor++;
		Concept concept = history.get(historyCursor);
		backButton.setEnabled(true);
		if (historyCursor == history.size() - 1)
			forwardButton.setEnabled(false);
		showConcept(concept);
	}

	public void setActiveConcept(Concept concept) {
		this.activeConcept = concept;

		// Keep track of active concept history
		if (historyCursor < 0 || history.get(historyCursor).conceptId != concept.conceptId) {
			if (historyCursor < history.size() - 1)
				history = history.subList(0, historyCursor + 1);
			historyCursor = history.size();
			history.add(concept);
			backButton.setEnabled(historyCursor > 0);
			forwardButton.setEnabled(false);
		}

		if (this.isVisible()) {
			showConcept(activeConcept);
		}
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		this.showConcept(activeConcept);
	}

	private void showConcept(Concept concept) {
		String name = concept.conceptName;
		if (name.length() > 80)
			name = name.substring(0, 80) + "...";
		conceptNameLabel.setText(name + " (" + concept.conceptId + ")");

		List<Concept> parents = new ArrayList<>();
		for (ParentChildRelationShip relationship : Global.dbEngine.getParentChildRelationshipsByChildConceptId(concept.conceptId))
			parents.add(Global.dbEngine.getConcept(relationship.parentConceptId));
		parentConceptTableModel.setConcepts(parents);

		currentConceptTableModel.setConcepts(Collections.singletonList(concept));

		List<Concept> children = new ArrayList<>();
		for (ParentChildRelationShip relationship : Global.dbEngine.getParentChildRelationshipsByParentConceptId(concept.conceptId))
			children.add(Global.dbEngine.getConcept(relationship.childConceptId));
		childrenConceptTableModel.setConcepts(children);

		List<Concept> sourceConcepts = new ArrayList<>();
		for (MapsToRelationship relationship : Global.dbEngine.getMapsToRelationshipsByConceptId2(concept.conceptId))
			sourceConcepts.add(Global.dbEngine.getConcept(relationship.conceptId1));
		sourceConceptTableModel.setConcepts(sourceConcepts);

		List<String> synonyms = Global.usagiSearchEngine.searchTermsByConceptId(concept.conceptId);
		StringBuilder text = new StringBuilder();
		for (String synonym : synonyms) {
			text.append(synonym + "\n");
		}
		synonymArea.setText(text.toString());
	}
}
