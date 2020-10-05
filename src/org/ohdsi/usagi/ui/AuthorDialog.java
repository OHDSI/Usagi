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

public class AuthorDialog extends JDialog {

	private static final long	serialVersionUID	= 8239922540117895957L;

	public AuthorDialog() {
		setTitle("Author");
		setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.ipadx = 5;
		g.ipady = 5;

		g.gridx = 0;
		g.gridy = 0;
		add(new JLabel("Author:"), g);

		g.gridx = 1;
		g.gridy = 0;
		JTextFieldLimit authorField = new JTextFieldLimit(20);
		authorField.setToolTipText("Please enter your name");
		authorField.setPreferredSize(new Dimension(100, 10));
		add(authorField, g);

		g.gridx = 0;
		g.gridy = 2;
		g.gridwidth = 2;
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		JButton saveButton = new JButton("Save");
		saveButton.setToolTipText("Save your name");
		saveButton.addActionListener(event -> {
			Global.author = authorField.getText();
			setVisible(false);
		});
		buttonPanel.add(saveButton);
		add(buttonPanel, g);

		pack();
		setModal(true);
		setLocationRelativeTo(Global.frame);
	}
}
