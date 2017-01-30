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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.ohdsi.usagi.CodeMapping.MappingStatus;

public class UsagiTable extends JTable {

	private static final long	serialVersionUID	= -8047438531290463795L;
	private List<TableColumn>	hiddenColumns		= new ArrayList<TableColumn>();
	private List<String>		hiddenColumnNames	= new ArrayList<String>();

	public UsagiTable(TableModel tableModel) {
		super(tableModel);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setDefaultRenderer(String.class, new UsagiCellRenderer());
		setDefaultRenderer(Double.class, new UsagiCellRenderer());
		setDefaultRenderer(Integer.class, new UsagiCellRenderer());
		setDefaultRenderer(MappingStatus.class, new UsagiCellRenderer());
		setFillsViewportHeight(true);

		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					showDialog(e);
				}

			}
		});

	}

	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (e.getFirstRow() == TableModelEvent.HEADER_ROW && hiddenColumnNames != null) {
			List<String> temp = new ArrayList<String>(hiddenColumnNames);
			hiddenColumnNames.clear();
			hiddenColumns.clear();
			for (String columnName : temp)
				hideColumn(columnName);
		}
	}

	public void hideColumn(String columnName) {
		for (int i = 0; i < getColumnCount(); i++)
			if (getColumnName(i).equals(columnName)) {
				TableColumn column = getColumnModel().getColumn(i);
				getColumnModel().removeColumn(column);
				hiddenColumns.add(column);
				hiddenColumnNames.add(columnName);
				break;
			}
	}

	private void showDialog(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		for (int i = 0; i < getColumnCount(); i++) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(getColumnName(i));
			item.setSelected(true);
			item.addActionListener(new ColumnSelectActionListener(getColumnName(i), true));
			menu.add(item);
		}
		for (String column : hiddenColumnNames) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(column);
			item.setSelected(false);
			item.addActionListener(new ColumnSelectActionListener(column, false));
			menu.add(item);
		}

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	class ColumnSelectActionListener implements ActionListener {
		private String	columnName;
		private boolean	isVisible;

		public ColumnSelectActionListener(String columnName, boolean isVisible) {
			this.columnName = columnName;
			this.isVisible = isVisible;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (isVisible) {
				for (int i = 0; i < getColumnCount(); i++)
					if (getColumnName(i).equals(columnName)) {
						TableColumn column = getColumnModel().getColumn(i);
						getColumnModel().removeColumn(column);
						hiddenColumns.add(column);
						hiddenColumnNames.add(columnName);
						break;
					}
			} else {
				int index = hiddenColumnNames.indexOf(columnName);
				if (index != -1) {
					hiddenColumnNames.remove(columnName);
					getColumnModel().addColumn(hiddenColumns.remove(index));
				}
			}
		}
	}

}
