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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ohdsi.usagi.BerkeleyDbEngine.BerkeleyDbStats;
import org.ohdsi.utilities.files.ReadTextFile;

public class ShowStatsDialog extends JDialog {

	private static final long	serialVersionUID	= 2028328868610404663L;

	public ShowStatsDialog() {
		String versionFileName = Global.folder + "/vocabularyVersion.txt";
		String version = "Unknown";
		if (new File(versionFileName).exists()) {
			for (String line : new ReadTextFile(versionFileName))
			  version = line;
		} 
		int termCount = Global.usagiSearchEngine.getTermCount();
		BerkeleyDbStats berkeleyDbStats = Global.dbEngine.getStats();
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		
		setTitle("Index statistics");
		setLayout(new GridBagLayout());

		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.ipadx = 10;
		g.ipady = 10;

		g.gridx = 0;
		g.gridy = 0;
		add(new JLabel("Vocabulary version:"), g);
		
		g.gridx = 1;
		g.gridy = 0;
		add(new JLabel(version), g);

		g.gridx = 0;
		g.gridy = 1;
		add(new JLabel("Searchable terms:"), g);
		
		g.gridx = 1;
		g.gridy = 1;
		add(new JLabel(numberFormat.format(termCount)), g);

		g.gridx = 0;
		g.gridy = 2;
		add(new JLabel("Concepts:"), g);
		
		g.gridx = 1;
		g.gridy = 2;
		add(new JLabel(numberFormat.format(berkeleyDbStats.conceptCount)), g);

		g.gridx = 0;
		g.gridy = 3;
		add(new JLabel("Source to concept relationships:"), g);
		
		g.gridx = 1;
		g.gridy = 3;
		add(new JLabel(numberFormat.format(berkeleyDbStats.mapsToRelationshipCount)), g);

		g.gridx = 0;
		g.gridy = 4;
		add(new JLabel("Parent-child relationships:"), g);
		
		g.gridx = 1;
		g.gridy = 4;
		add(new JLabel(numberFormat.format(berkeleyDbStats.parentChildCount)), g);

		g.gridx = 0;
		g.gridy = 5;
		g.gridwidth = 2;

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();

			}
		});
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
