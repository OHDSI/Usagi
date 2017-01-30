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

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.ohdsi.usagi.TargetConcept;

public class ConceptInformationDialog extends JDialog {

	private static final long	serialVersionUID	= -2112565437136224217L;
	private JTextArea			area;

	public ConceptInformationDialog() {
		setTitle("Concept information");
		setLayout(new BorderLayout());
		area = new JTextArea();
		area.setEditable(false);
		add(new JScrollPane(area), BorderLayout.CENTER);
		setSize(600, 600);
		setLocationRelativeTo(Global.frame);
	}

	public void setConcept(TargetConcept concept) {
		setTitle("Information about concept '" + concept.conceptName + "' (" + concept.conceptId + ")");
		area.setText(concept.additionalInformation.replaceAll("\\\\n", "\n"));
	}
}
