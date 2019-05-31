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

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.CodeMapping.MappingStatus;

public class UsagiStatusBar extends JPanel implements DataChangeListener {

	private static final long	serialVersionUID	= 4406343348570974587L;
	private JLabel				countLabel;
	private JLabel				percentLabel;
	private JLabel				searchLabel;
	private DecimalFormat		percentFormatter	= new DecimalFormat("##0.0");

	public UsagiStatusBar() {
		super();
		setBorder(BorderFactory.createEmptyBorder());
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel description = new JLabel("Approved / total:");
		description.setForeground(Color.gray);
		add(description);
		add(Box.createHorizontalStrut(5));
		countLabel = new JLabel("0/0");
		countLabel.setForeground(Color.black);
		add(countLabel);
		add(Box.createHorizontalStrut(15));
		percentLabel = new JLabel("0%");
		percentLabel.setForeground(Color.black);
		add(percentLabel);
		description = new JLabel(" of total frequency");
		description.setForeground(Color.gray);
		add(description);
		add(Box.createHorizontalGlue());
		searchLabel = new JLabel("Searching...");
		searchLabel.setVisible(false);
		add(searchLabel);
		add(Box.createHorizontalGlue());
		JLabel versionLabel = new JLabel("Vocabulary version: " + Global.vocabularyVersion);
		add(versionLabel);
		Global.mapping.addListener(this);
	}

	public void setSearching(boolean value) {
		searchLabel.setVisible(value);
	}

	private void update() {
		int approved = 0;
		long totalFreq = 0;
		long approvedFreq = 0;
		for (CodeMapping codeMapping : Global.mapping) {
			if (codeMapping.mappingStatus == MappingStatus.APPROVED) {
				approved++;
				if (codeMapping.sourceCode.sourceFrequency == -1)
					approvedFreq++;
				else
					approvedFreq += codeMapping.sourceCode.sourceFrequency;
			}
			if (codeMapping.sourceCode.sourceFrequency == -1)
				totalFreq++;
			else
				totalFreq += codeMapping.sourceCode.sourceFrequency;
		}
		countLabel.setText(approved + " / " + Global.mapping.size());
		countLabel.setToolTipText(approved + " of the " + Global.mapping.size() + " source codes now has an approved mapping");
		String percent = percentFormatter.format(100 * approvedFreq / (double) totalFreq) + "%";
		percentLabel.setText(percent);
		percentLabel.setToolTipText(percent + " of all entries in the source data now has an approved mapping");

	}

	@Override
	public void dataChanged(DataChangeEvent event) {
		update();
	}
}
