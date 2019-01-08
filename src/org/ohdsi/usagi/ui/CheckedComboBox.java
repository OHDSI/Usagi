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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.accessibility.Accessible;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.plaf.basic.ComboPopup;

/**
 *  Based on https://java-swing-tips.blogspot.com/2016/07/select-multiple-jcheckbox-in-jcombobox.html
 */
public class CheckedComboBox extends JComboBox<CheckableItem> {
	private static final long			serialVersionUID	= -4241435331838290108L;
	private boolean						keepOpen;
	private transient ActionListener	listener;

	public CheckedComboBox(String[] items) {
		super();
		CheckableItem[] checkableItems = new CheckableItem[items.length];
		for (int i = 0; i < items.length; i++)
			checkableItems[i] = new CheckableItem(items[i], false);
		setModel(new DefaultComboBoxModel<CheckableItem>(checkableItems));
	}

	public CheckedComboBox(Vector<String> items) {
		super();
		CheckableItem[] checkableItems = new CheckableItem[items.size()];
		for (int i = 0; i < items.size(); i++)
			checkableItems[i] = new CheckableItem(items.get(i), false);
		setModel(new DefaultComboBoxModel<CheckableItem>(checkableItems));
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 20);
	}

	public Vector<String> getSelectedItems() {
		ComboBoxModel<CheckableItem> model = getModel();
		Vector<String> selectedItems = new Vector<String>();
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i).selected)
				selectedItems.add(model.getElementAt(i).text);
		}
		return selectedItems;
	}

	@Override
	public void updateUI() {
		setRenderer(null);
		removeActionListener(listener);
		super.updateUI();
		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
					updateItem(getSelectedIndex());
					keepOpen = true;
				}
			}
		};
		setRenderer(new CheckBoxCellRenderer());
		addActionListener(listener);
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "checkbox-select");
		getActionMap().put("checkbox-select", new AbstractAction() {
			private static final long serialVersionUID = -5556579312742384472L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Accessible a = getAccessibleContext().getAccessibleChild(0);
				if (a instanceof ComboPopup) {
					ComboPopup pop = (ComboPopup) a;
					updateItem(pop.getList().getSelectedIndex());
				}
			}
		});
	}

	private void updateItem(int index) {
		if (isPopupVisible()) {
			CheckableItem item = getItemAt(index);
			item.selected ^= true;
			setSelectedIndex(-1);
			setSelectedItem(item);
		}
	}

	@Override
	public void setPopupVisible(boolean v) {
		if (keepOpen) {
			keepOpen = false;
		} else {
			super.setPopupVisible(v);
		}
	}

	private class CheckBoxCellRenderer implements ListCellRenderer<CheckableItem> {
		private final JLabel	label	= new JLabel(" ");
		private final JCheckBox	check	= new JCheckBox(" ");

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Component getListCellRendererComponent(JList list, CheckableItem value, int index, boolean isSelected, boolean cellHasFocus) {
			if (index < 0) {
				label.setText(getCheckedItemString(list.getModel()));
				return label;
			} else {
				check.setText(Objects.toString(value, ""));
				check.setSelected(value.selected);
				if (isSelected) {
					check.setBackground(list.getSelectionBackground());
					check.setForeground(list.getSelectionForeground());
				} else {
					check.setBackground(list.getBackground());
					check.setForeground(list.getForeground());
				}
				return check;
			}
		}

		private String getCheckedItemString(ListModel<CheckableItem> model) {
			List<String> sl = new ArrayList<>();
			for (int i = 0; i < model.getSize(); i++) {
				Object o = model.getElementAt(i);
				if (o instanceof CheckableItem && ((CheckableItem) o).selected) {
					sl.add(o.toString());
				}
			}
			return sl.stream().sorted().collect(Collectors.joining(", "));
		}
	}
}

class CheckableItem {
	public final String	text;
	public boolean		selected;

	protected CheckableItem(String text, boolean selected) {
		this.text = text;
		this.selected = selected;
	}

	@Override
	public String toString() {
		return text;
	}
}
