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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class FilterPanel extends JPanel {

	private static final long serialVersionUID = 1378433878412231259L;
	private JCheckBox filterByAutoCheckBox;
	private JCheckBox filterStandardCheckBox;
	private JCheckBox filterByConceptClassCheckBox;
	private JCheckBox filterByVocabularyCheckBox;
	private JCheckBox filterByDomainCheckBox;
	private JCheckBox includeSourceTermsCheckbox;
	private CheckedComboBox filterConceptClassComboBox;
	private CheckedComboBox filterVocabularyComboBox;
	private CheckedComboBox filterDomainComboBox;
	private List<FilterChangeListener> listeners = new ArrayList<>();

	public FilterPanel() {
		setBorder(BorderFactory.createTitledBorder("Filters"));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.1;
		c.gridwidth = 2;
		filterByAutoCheckBox = new JCheckBox("Filter by user selected concepts / ATC code", false);
		filterByAutoCheckBox.setToolTipText("Limit the search to those concept IDs specified in the input file");
		filterByAutoCheckBox.addActionListener(actionEvent -> notifyListeners());
		add(filterByAutoCheckBox, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.1;
		c.gridwidth = 2;
		filterStandardCheckBox = new JCheckBox("Filter standard concepts", false);
		filterStandardCheckBox.setToolTipText("Limit the search to only standard concepts");
		filterStandardCheckBox.addActionListener(actionEvent -> notifyListeners());
		filterStandardCheckBox.setSelected(true);
		add(filterStandardCheckBox, c);

		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.1;
		c.gridwidth = 1;
		filterByConceptClassCheckBox = new JCheckBox("Filter by concept class:", false);
		filterByConceptClassCheckBox.setToolTipText("Limit the search to concepts of this class");
		filterByConceptClassCheckBox.addActionListener(actionEvent -> notifyListeners());
		add(filterByConceptClassCheckBox, c);

		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 1;
		filterConceptClassComboBox = new CheckedComboBox(Global.conceptClassIds);
		filterConceptClassComboBox.addActionListener(actionEvent -> {
			if (filterByConceptClassCheckBox.isSelected())
				notifyListeners();
		});
		add(filterConceptClassComboBox, c);

		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0.1;
		c.gridwidth = 1;
		filterByVocabularyCheckBox = new JCheckBox("Filter by vocabulary:", false);
		filterByVocabularyCheckBox.setToolTipText("Limit the search to concepts of this vocabulary");
		filterByVocabularyCheckBox.addActionListener(actionEvent -> notifyListeners());
		add(filterByVocabularyCheckBox, c);

		c.gridx = 3;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 1;
		filterVocabularyComboBox = new CheckedComboBox(Global.vocabularyIds);
		filterVocabularyComboBox.addActionListener(actionEvent -> {
			if (filterByVocabularyCheckBox.isSelected())
				notifyListeners();
		});
		add(filterVocabularyComboBox, c);

		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0.1;
		c.gridwidth = 1;
		filterByDomainCheckBox = new JCheckBox("Filter by domain:", false);
		filterByDomainCheckBox.setToolTipText("Limit the search to concepts of this domain");
		filterByDomainCheckBox.addActionListener(actionEvent -> notifyListeners());
		add(filterByDomainCheckBox, c);

		c.gridx = 3;
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 1;
		filterDomainComboBox = new CheckedComboBox(Global.domainIds);
		filterDomainComboBox.addActionListener(actionEvent -> {
			if (filterByDomainCheckBox.isSelected())
				notifyListeners();
		});
		add(filterDomainComboBox, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 2;
		includeSourceTermsCheckbox = new JCheckBox("Include source terms", true);
		includeSourceTermsCheckbox.setToolTipText("Include names of source concepts to be used to find standard concepts they map to");
		includeSourceTermsCheckbox.addActionListener(actionEvent -> notifyListeners());
		add(includeSourceTermsCheckbox, c);

	}

	private void notifyListeners() {
		for (FilterChangeListener listener : listeners)
			listener.filterChanged();
	}

	public void addListener(FilterChangeListener listener) {
		listeners.add(listener);
	}

	public boolean getFilterByAuto() {
		return filterByAutoCheckBox.isSelected();
	}

	public void setFilterByAuto() {
		filterByAutoCheckBox.setSelected(true);
	}

	public boolean getFilterStandard() {
		return filterStandardCheckBox.isSelected();
	}

	public boolean getFilterByConceptClasses() {
		return filterByConceptClassCheckBox.isSelected();
	}

	public boolean getFilterByVocabularies() {
		return filterByVocabularyCheckBox.isSelected();
	}

	public boolean getFilterByDomains() {
		return filterByDomainCheckBox.isSelected();
	}

	public boolean getIncludeSourceTerms() {
		return includeSourceTermsCheckbox.isSelected();
	}

	public Vector<String> getConceptClass() {
		return filterConceptClassComboBox.getSelectedItems();
	}

	public Vector<String> getVocabulary() {
		return filterVocabularyComboBox.getSelectedItems();
	}

	public Vector<String> getDomain() {
		return filterDomainComboBox.getSelectedItems();
	}
}
