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
package org.ohdsi.usagi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.MapsToRelationship;
import org.ohdsi.usagi.SubsumesRelationship;

public class ConceptInformationDialog extends JDialog {

	private static final long	serialVersionUID	= -2112565437136224217L;
	private JTextArea			area;
	private JLabel				conceptNameLabel;
	private JButton				backButton;
	private JButton				forwardButton;
	private ConceptTableModel	parentConceptTableModel;
	private UsagiTable			parentsConceptTable;
	private ConceptTableModel	childrenConceptTableModel;
	private UsagiTable			childrenConceptTable;
	private ConceptTableModel	sourceConceptTableModel;
	private UsagiTable			sourceConceptTable;
	private List<Concept>		history				= new ArrayList<Concept>();
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
	}

	private Component createCenterPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder());
		panel.setLayout(new BorderLayout());
		JScrollPane infoPanel = createInfoPanel();
		JTabbedPane tabPanel = createTabPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPanel, tabPanel);
		panel.add(splitPane, BorderLayout.CENTER);
		return panel;
	}

	private JTabbedPane createTabPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Hierarchy", createHierarchyPanel());
		tabbedPane.addTab("Source concepts", createSourceConceptPanel());
		return tabbedPane;
	}

	private Component createSourceConceptPanel() {
		sourceConceptTableModel = new ConceptTableModel(false);
		sourceConceptTable = new UsagiTable(sourceConceptTableModel);
		sourceConceptTable.setPreferredScrollableViewportSize(new Dimension(500, 45));
		sourceConceptTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		sourceConceptTable.setRowSelectionAllowed(false);
		sourceConceptTable.hideColumn("Parents");
		sourceConceptTable.hideColumn("Children");
		sourceConceptTable.hideColumn("Valid start date");
		sourceConceptTable.hideColumn("Valid end date");
		sourceConceptTable.hideColumn("Invalid reason");

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
		parentsConceptTable = new UsagiTable(parentConceptTableModel);
		parentsConceptTable.setPreferredScrollableViewportSize(new Dimension(500, 45));
		parentsConceptTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		parentsConceptTable.setRowSelectionAllowed(true);
		parentsConceptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		parentsConceptTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!updating) {
					updating = true;
					int viewRow = parentsConceptTable.getSelectedRow();
					Global.conceptInfoAction.setEnabled(true);
					int modelRow = parentsConceptTable.convertRowIndexToModel(viewRow);
					Global.conceptInformationDialog.setConcept(parentConceptTableModel.getConcept(modelRow));
					updating = false;
				}
			}
		});
		parentsConceptTable.hideColumn("Parents");
		parentsConceptTable.hideColumn("Children");
		parentsConceptTable.hideColumn("Valid start date");
		parentsConceptTable.hideColumn("Valid end date");
		parentsConceptTable.hideColumn("Invalid reason");

		JScrollPane parentsPane = new JScrollPane(parentsConceptTable);
		parentsPane.setBorder(BorderFactory.createTitledBorder("Parent concepts"));
		parentsPane.setMinimumSize(new Dimension(500, 50));
		parentsPane.setPreferredSize(new Dimension(500, 50));
		c.gridy = 0;
		c.weighty = 0.4;
		panel.add(parentsPane, c);

		childrenConceptTableModel = new ConceptTableModel(false);
		childrenConceptTable = new UsagiTable(childrenConceptTableModel);
		childrenConceptTable.setPreferredScrollableViewportSize(new Dimension(500, 45));
		childrenConceptTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		childrenConceptTable.setRowSelectionAllowed(true);
		childrenConceptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		childrenConceptTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!updating) {
					updating = true;
					int viewRow = childrenConceptTable.getSelectedRow();
					Global.conceptInfoAction.setEnabled(true);
					int modelRow = childrenConceptTable.convertRowIndexToModel(viewRow);
					Global.conceptInformationDialog.setConcept(childrenConceptTableModel.getConcept(modelRow));
					updating = false;
				}
			}
		});
		childrenConceptTable.hideColumn("Parents");
		childrenConceptTable.hideColumn("Children");
		childrenConceptTable.hideColumn("Valid start date");
		childrenConceptTable.hideColumn("Valid end date");
		childrenConceptTable.hideColumn("Invalid reason");

		JScrollPane childrenPane = new JScrollPane(childrenConceptTable);
		childrenPane.setBorder(BorderFactory.createTitledBorder("Children concepts"));
		childrenPane.setMinimumSize(new Dimension(500, 50));
		childrenPane.setPreferredSize(new Dimension(500, 50));
		c.gridy = 1;
		c.weighty = 0.6;
		panel.add(childrenPane, c);
		return panel;
	}

	private JScrollPane createInfoPanel() {
		area = new JTextArea();
		area.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(area);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Concept information"));
		scrollPane.setPreferredSize(new Dimension(600, 200));
		scrollPane.setMinimumSize(new Dimension(200, 100));
		scrollPane.setAutoscrolls(true);
		return scrollPane;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		JButton replaceButton = new JButton("Replace concept");
		replaceButton.setToolTipText("Replace selected concept");
		replaceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Global.mappingDetailPanel.replaceConcepts(history.get(historyCursor));
				Global.frame.requestFocus();
			}

		});
		// replaceButton.setEnabled(false);
		buttonPanel.add(replaceButton);
		JButton addButton = new JButton("Add concept");
		addButton.setToolTipText("Add selected concept");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Global.mappingDetailPanel.addConcept(history.get(historyCursor));
				Global.frame.requestFocus();
			}

		});
		// addButton.setEnabled(false);
		buttonPanel.add(addButton);
		return buttonPanel;
	}

	private Component createHeaderPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder());
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		conceptNameLabel = new JLabel("No concept selected");
		panel.add(conceptNameLabel);
		panel.add(Box.createHorizontalGlue());
		backButton = new JButton("<");
		backButton.setToolTipText("Back to previous concept");
		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				goBack();
			}
		});
		backButton.setEnabled(false);
		panel.add(backButton);
		forwardButton = new JButton(">");
		forwardButton.setToolTipText("Forward to next concept");
		forwardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				goForward();
			}
		});
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

	public void setConcept(Concept concept) {
		if (historyCursor < 0 || history.get(historyCursor).conceptId != concept.conceptId) {
			if (historyCursor < history.size() - 1)
				history = history.subList(0, historyCursor + 1);
			historyCursor = history.size();
			history.add(concept);
			backButton.setEnabled(historyCursor > 0);
			forwardButton.setEnabled(false);
			showConcept(concept);
		}
	}

	private void showConcept(Concept concept) {
		String name = concept.conceptName;
		if (name.length() > 80)
			name = name.substring(0, 80) + "...";
		conceptNameLabel.setText(name + " (" + concept.conceptId + ")");

		StringBuilder conceptInfo = new StringBuilder();
		conceptInfo.append("Concept name: " + concept.conceptName + "\n");
		conceptInfo.append("Domain ID: " + concept.domainId + "\n");
		conceptInfo.append("Concept class ID: " + concept.conceptClassId + "\n");
		conceptInfo.append("Vocabulary ID: " + concept.vocabularyId + "\n");
		conceptInfo.append("Concept ID: " + concept.conceptId + "\n");
		conceptInfo.append("Concept code: " + concept.conceptCode + "\n");
		conceptInfo.append("Invalid reason: " + (concept.invalidReason == null ? "" : concept.invalidReason) + "\n");
		conceptInfo.append("Standard concept: " + concept.standardConcept + "\n");
		if (concept.additionalInformation != null)
			conceptInfo.append(concept.additionalInformation.replaceAll("\\\\n", "\n"));
		area.setText(conceptInfo.toString());

		List<Concept> parents = new ArrayList<Concept>();
		for (SubsumesRelationship relationship : Global.dbEngine.getSubsumesRelationshipsByChildConceptId(concept.conceptId))
			parents.add(Global.dbEngine.getConcept(relationship.parentConceptId));
		parentConceptTableModel.setConcepts(parents);

		List<Concept> children = new ArrayList<Concept>();
		for (SubsumesRelationship relationship : Global.dbEngine.getSubsumesRelationshipsByParentConceptId(concept.conceptId))
			children.add(Global.dbEngine.getConcept(relationship.childConceptId));
		childrenConceptTableModel.setConcepts(children);

		List<Concept> sourceConcepts = new ArrayList<Concept>();
		for (MapsToRelationship relationship : Global.dbEngine.getMapsToRelationshipsByConceptId2(concept.conceptId))
			sourceConcepts.add(Global.dbEngine.getConcept(relationship.conceptId1));
		sourceConceptTableModel.setConcepts(sourceConcepts);
	}
}
