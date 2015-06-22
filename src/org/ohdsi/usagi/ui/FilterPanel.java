/*******************************************************************************
 * Copyright 2014 Observational Health Data Sciences and Informatics
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.ohdsi.utilities.files.ReadTextFile;

public class FilterPanel extends JPanel {

	private static final long			serialVersionUID	= 1378433878412231259L;
	private JCheckBox					filterByAutoCheckBox;
	private JCheckBox					filterInvalidCheckBox;
	private JCheckBox					filterByConceptClassCheckBox;
	private JCheckBox					filterByVocabularyCheckBox;
	private JCheckBox					filterByDomainCheckBox;
	private JComboBox<String>			filterConceptClassComboBox;
	private JComboBox<String>			filterVocabularyComboBox;
	private JComboBox<String>			filterDomainComboBox;
	private List<FilterChangeListener>	listeners			= new ArrayList<FilterChangeListener>();

	public FilterPanel() {
		setBorder(BorderFactory.createTitledBorder("Filters"));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.1;
		c.gridwidth = 2;
		filterByAutoCheckBox = new JCheckBox("Filter by automatically select concepts", false);
		filterByAutoCheckBox.setToolTipText("Limit the search to those concept IDs specified in the input file");
		filterByAutoCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				notifyListeners();
			}

		});
		add(filterByAutoCheckBox, c);

		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.1;
		c.gridwidth = 2;
		filterInvalidCheckBox = new JCheckBox("Filter invalid concepts", false);
		filterInvalidCheckBox.setToolTipText("Limit the search to only valid concepts");
		filterInvalidCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				notifyListeners();
			}
		});
		add(filterInvalidCheckBox, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.1;
		c.gridwidth = 1;
		filterByConceptClassCheckBox = new JCheckBox("Filter by concept class:", false);
		filterByConceptClassCheckBox.setToolTipText("Limit the search to concepts of this class");
		filterByConceptClassCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				notifyListeners();

			}
		});
		add(filterByConceptClassCheckBox, c);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 1;
		filterConceptClassComboBox = new JComboBox<String>(loadVectorFromFile(Global.folder + "/ConceptClassIds.txt"));
		filterConceptClassComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (filterByConceptClassCheckBox.isSelected())
					notifyListeners();
			}
		});
		add(filterConceptClassComboBox, c);

		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0.1;
		c.gridwidth = 1;
		filterByVocabularyCheckBox = new JCheckBox("Filter by vocabulary:", false);
		filterByVocabularyCheckBox.setToolTipText("Limit the search to concepts of this vocabulary");
		filterByVocabularyCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				notifyListeners();
			}
		});
		add(filterByVocabularyCheckBox, c);

		c.gridx = 3;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 1;
		filterVocabularyComboBox = new JComboBox<String>(loadVectorFromFile(Global.folder + "/VocabularyIds.txt"));
		filterVocabularyComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (filterByVocabularyCheckBox.isSelected())
					notifyListeners();
			}
		});
		add(filterVocabularyComboBox, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.1;
		c.gridwidth = 1;
		filterByDomainCheckBox = new JCheckBox("Filter by domain:", false);
		filterByDomainCheckBox.setToolTipText("Limit the search to concepts of this domain");
		filterByDomainCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				notifyListeners();
			}
		});
		add(filterByDomainCheckBox, c);

		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 1;
		filterDomainComboBox = new JComboBox<String>(loadVectorFromFile(Global.folder + "/DomainIds.txt"));
		filterDomainComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (filterByDomainCheckBox.isSelected())
					notifyListeners();
			}
		});
		add(filterDomainComboBox, c);

	}

	private Vector<String> loadVectorFromFile(String fileName) {
		if (new File(fileName).exists()) {
			Vector<String> vector = new Vector<String>();
			for (String line : new ReadTextFile(fileName))
				vector.add(line);
			return vector;
		} else
			return new Vector<String>();
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

	public boolean getFilterInvalid() {
		return filterInvalidCheckBox.isSelected();
	}

	public boolean getFilterByConceptClass() {
		return filterByConceptClassCheckBox.isSelected();
	}

	public boolean getFilterByVocabulary() {
		return filterByVocabularyCheckBox.isSelected();
	}

	public boolean getFilterByDomain() {
		return filterByDomainCheckBox.isSelected();
	}

	public String getConceptClass() {
		return filterConceptClassComboBox.getSelectedItem().toString();
	}

	public String getVocabulary() {
		return filterVocabularyComboBox.getSelectedItem().toString();
	}

	public String getDomain() {
		return filterDomainComboBox.getSelectedItem().toString();
	}
}
