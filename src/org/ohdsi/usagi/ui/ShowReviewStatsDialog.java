/*******************************************************************************
 * Copyright 2021 Observational Health Data Sciences and Informatics & The Hyve
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

import org.ohdsi.usagi.CodeMapping;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShowReviewStatsDialog extends JDialog {

	private static final long	serialVersionUID	= -4646761336953654777L;

	private static final Font HEADER_FONT = new Font("Arial", Font.BOLD,12);

	public ShowReviewStatsDialog() {
		// If selection of multiple codes made, then use that to calculate statistics
		java.util.List<CodeMapping> selectedCodeMappings = Global.mappingTablePanel.getSelectedCodeMappings();
		List<CodeMapping> codeMappings;
		if (selectedCodeMappings.size() > 1) {
			codeMappings = selectedCodeMappings;
		} else {
			codeMappings = Global.mapping;
		}

		setTitle("Review statistics");
		setLayout(new GridBagLayout());

		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.ipadx = 10;
		g.ipady = 10;

		g.gridx = 0;
		g.gridy = 0;
		addLabel(g, String.format("Number of (selected) source codes: %d", codeMappings.size()));

		// Mapping status
		addHeaderLabel(g, "By mapping status:");

		Map<CodeMapping.MappingStatus, Long> countByMappingStatus = codeMappings.stream()
				.collect(Collectors.groupingBy(CodeMapping::getMappingStatus, Collectors.counting()));

		countByMappingStatus.forEach((key, value) -> addLabel(g, String.format("%s - %d", key, value)));

		// Equivalence status
		addHeaderLabel(g,"By equivalence status:");

		Map<CodeMapping.Equivalence, Long> countByEquivalence = codeMappings.stream()
				.collect(Collectors.groupingBy(CodeMapping::getEquivalence, Collectors.counting()));

		countByEquivalence.forEach((key, value) -> addLabel(g, String.format("%s - %d", key, value)));

		// Reviewer
		addHeaderLabel(g,"By assigned reviewer:");

		Map<String, Long> countByAssignedReviewer = codeMappings.stream()
				.collect(Collectors.groupingBy(CodeMapping::getAssignedReviewer, Collectors.counting()));

		Map<String, Long> countApprovedByAssignedReviewer = codeMappings.stream()
				.filter(x -> x.getMappingStatus().equals(CodeMapping.MappingStatus.APPROVED))
				.collect(Collectors.groupingBy(CodeMapping::getAssignedReviewer, Collectors.counting()));

		countByAssignedReviewer.forEach((key, total) -> {
			long nApproved = countApprovedByAssignedReviewer.getOrDefault(key, 0L);
			addLabel(g, String.format("%s - %d/%d", key, nApproved, total));
		});

		// Number of target mappings
		addHeaderLabel(g,"By number of target concepts:");

		Map<Integer, Long> countByNumberOfTargetConcepts = codeMappings.stream()
				.collect(Collectors.groupingBy(x -> x.getTargetConcepts().size(), Collectors.counting()));

		countByNumberOfTargetConcepts.forEach((key, value) -> addLabel(g, String.format("%d - %d", key, value)));

		g.gridx = 0;
		g.gridy++;
		g.gridwidth = 2;

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		JButton okButton = new JButton("Ok");
		okButton.addActionListener(arg0 -> close());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalGlue());

		add(buttonPanel, g);

		setModal(true);
		setResizable(false);
		pack();

	}

	private JLabel addLabel(GridBagConstraints g, String message) {
		g.gridx = 0;
		g.gridy++;
		JLabel label = new JLabel(message);
		add(label, g);
		return label;
	}

	private JLabel addHeaderLabel(GridBagConstraints g, String message) {
		JLabel label = addLabel(g, message);
		label.setFont(HEADER_FONT);
		return label;
	}

	private void close() {
		setVisible(false);
	}
}
