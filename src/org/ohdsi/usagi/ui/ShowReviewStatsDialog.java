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

		Map<CodeMapping.MappingStatus, Integer> mappingStats = new HashMap<>();
		for (CodeMapping codeMapping : codeMappings) {
			mappingStats.putIfAbsent(codeMapping.getMappingStatus(), 0);
			Integer c = mappingStats.get(codeMapping.getMappingStatus());
			mappingStats.put(codeMapping.getMappingStatus(), ++c);
		}

		mappingStats.forEach((key, value) -> {
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

		Map<CodeMapping.Equivalence, Integer> equivalenceStats = new HashMap<>();
		for (CodeMapping codeMapping : codeMappings) {
			equivalenceStats.putIfAbsent(codeMapping.getEquivalence(), 0);
			Integer c = equivalenceStats.get(codeMapping.getEquivalence());
			equivalenceStats.put(codeMapping.getEquivalence(), ++c);
		}

		equivalenceStats.forEach((key, value) -> {
			g.gridx = 0;
			g.gridy++;
			add(new JLabel(String.format("%s - %d", key, value)), g);
		});

		// Reviewer
		g.gridx = 0;
		g.gridy++;
		l = new JLabel("By reviewer:");
		l.setFont(headerFont);
		add(l, g);

		Map<String, Pair<Integer, Integer>> reviewerStats = new HashMap<>();
		for (CodeMapping codeMapping : codeMappings) {
			reviewerStats.putIfAbsent(codeMapping.getAssignedReviewer(), new Pair<>(0,0));
			Pair<Integer, Integer> c = reviewerStats.get(codeMapping.getAssignedReviewer());
			Integer nApproved = c.getItem1();
			if (codeMapping.getMappingStatus() == CodeMapping.MappingStatus.APPROVED) {
				c.setItem1(++nApproved);
			}
			Integer nTotal = c.getItem2();
			c.setItem2(++nTotal);
		}

		reviewerStats.forEach((key, value) -> {
			g.gridx = 0;
			g.gridy++;
			add(new JLabel(String.format("%s - %d/%d", key, value.getItem1(), value.getItem2())), g);
		});

		// Number of target mappings
		g.gridx = 0;
		g.gridy++;
		l = new JLabel("By number of targets:");
		l.setFont(headerFont);
		add(l, g);

		Map<Integer, Integer> nTargetsStats = new HashMap<>();
		for (CodeMapping codeMapping : codeMappings) {
			Integer nTargets = codeMapping.getTargetConcepts().size();
			nTargetsStats.putIfAbsent(nTargets, 0);
			Integer c = nTargetsStats.get(nTargets);
			nTargetsStats.put(nTargets, ++c);
		}

		nTargetsStats.forEach((key, value) -> {
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
