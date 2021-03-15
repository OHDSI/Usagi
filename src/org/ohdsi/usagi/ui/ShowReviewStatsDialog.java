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
import org.ohdsi.utilities.collections.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShowReviewStatsDialog extends JDialog {

	private static final long	serialVersionUID	= 2028328868610404663L;

	public ShowReviewStatsDialog() {
		Font headerFont = new Font("Arial", Font.BOLD,12);

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
		g.ipadx = 50;
		g.ipady = 10;

		g.gridx = 0;
		g.gridy = 0;
		add(new JLabel(String.format("Number of (selected) source codes: %d", codeMappings.size())), g);

		// Mapping status
		g.gridx = 0;
		g.gridy++;
		JLabel l = new JLabel("By mapping status:");
		l.setFont(headerFont);
		add(l, g);

		Map<CodeMapping.MappingStatus, Long> countByMappingStatus = codeMappings.stream()
				.collect(Collectors.groupingBy(CodeMapping::getMappingStatus, Collectors.counting()));

		countByMappingStatus.forEach((key, value) -> {
			g.gridx = 0;
			g.gridy++;
			add(new JLabel(String.format("%s - %d", key, value)), g);
		});

		// Equivalence status
		g.gridx = 0;
		g.gridy++;
		l = new JLabel("By equivalence status:");
		l.setFont(headerFont);
		add(l, g);

		Map<CodeMapping.Equivalence, Long> countByEquivalence = codeMappings.stream()
				.collect(Collectors.groupingBy(CodeMapping::getEquivalence, Collectors.counting()));

		countByEquivalence.forEach((key, value) -> {
			g.gridx = 0;
			g.gridy++;
			add(new JLabel(String.format("%s - %d", key, value)), g);
		});

		// Reviewer
		g.gridx = 0;
		g.gridy++;
		l = new JLabel("By assigned reviewer:");
		l.setFont(headerFont);
		add(l, g);

		Map<String, Long> countByAssignedReviewer = codeMappings.stream()
				.collect(Collectors.groupingBy(CodeMapping::getAssignedReviewer, Collectors.counting()));

		Map<String, Long> countApprovedByAssignedReviewer = codeMappings.stream()
				.filter(x -> x.getMappingStatus().equals(CodeMapping.MappingStatus.APPROVED))
				.collect(Collectors.groupingBy(CodeMapping::getAssignedReviewer, Collectors.counting()));

		countByAssignedReviewer.forEach((key, total) -> {
			g.gridx = 0;
			g.gridy++;
			long nApproved = countApprovedByAssignedReviewer.getOrDefault(key, 0L);
			add(new JLabel(String.format("%s - %d/%d", key, nApproved, total)), g);
		});

		// Number of target mappings
		g.gridx = 0;
		g.gridy++;
		l = new JLabel("By number of target concepts:");
		l.setFont(headerFont);
		add(l, g);

		Map<Integer, Long> countByNumberOfTargetConcepts = codeMappings.stream()
				.collect(Collectors.groupingBy(x -> x.getTargetConcepts().size(), Collectors.counting()));

		countByNumberOfTargetConcepts.forEach((key, value) -> {
			g.gridx = 0;
			g.gridy++;
			add(new JLabel(String.format("%d - %d", key, value)), g);
		});

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

	private void close() {
		setVisible(false);
	}
}
