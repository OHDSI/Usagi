/*******************************************************************************
 * Copyright 2020 Observational Health Data Sciences and Informatics & The Hyve
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

import javax.swing.*;
import java.awt.*;

public class ReviewerAssignmentDialog extends JDialog {

	private static final long	serialVersionUID	= 4349740743952812807L;

	public ReviewerAssignmentDialog() {
		setTitle("Reviewer");
		setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.ipadx = 10;
		g.ipady = 10;

		g.gridx = 0;
		g.gridy = 0;
		add(new JLabel("Reviewers:"), g);

		g.gridx = 1;
		g.gridy = 0;
		JTextFieldLimit reviewersField = new JTextFieldLimit(20);
		reviewersField.setToolTipText("Please enter the reviewers as comma separated list");
		reviewersField.setPreferredSize(new Dimension(300, 10));
		reviewersField.setText("A,B,C,D");
		add(reviewersField, g);

		g.gridx = 0;
		g.gridy = 2;
		g.gridwidth = 2;
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		JButton saveButton = new JButton("Assign");
		saveButton.setToolTipText("Assign reviewers");
		saveButton.addActionListener(event -> {
			Global.mappingTablePanel.assignReviewers(
					reviewersField.getText().split(",")
			);
			setVisible(false);
		});
		buttonPanel.add(saveButton);
		add(buttonPanel, g);

		pack();
		setModal(true);
		setLocationRelativeTo(Global.frame);
	}
}
