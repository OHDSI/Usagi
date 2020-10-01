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
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.ohdsi.usagi.CodeMapping.MappingStatus;

public class UsagiCellRenderer extends DefaultTableCellRenderer {

	public static int					MAX_TOOLTIP_WIDTH_IN_CHARS	= 150;
	private static final long			serialVersionUID			= -4732586716304918837L;
	private static final Color			evenColor					= Color.white;
	private static final Color			oddColor					= new Color(0.95f, 0.95f, 1.00f);
	private static final Color			checkedColor				= new Color(0.75f, 1.00f, 0.75f);
	private static final Color			checkedOddColor				= new Color(0.85f, 1.00f, 0.85f);
	private static final Color			errorColor					= new Color(1.00f, 0.75f, 0.75f);
	private static final Color			errorOddColor				= new Color(1.00f, 0.85f, 0.85f);
	private static final DecimalFormat	doubleFormatter				= new DecimalFormat("###,###,###,##0.00");

	@Override
	public void setValue(Object aValue) {
		if (aValue != null)
			setToolTipText("<html>" + wordWrap(aValue.toString(), MAX_TOOLTIP_WIDTH_IN_CHARS) + "</html>");
		super.setValue(aValue);
	}

	public static String wordWrap(String text, int lineLength) {
		text = text.trim();
		if (text.length() < lineLength)
			return text;
		if (text.substring(0, lineLength).contains("\n"))
			return text.substring(0, text.indexOf("\n")).trim() + "<br><br>" + wordWrap(text.substring(text.indexOf("\n") + 1), lineLength);
		int place = Math.max(Math.max(text.lastIndexOf(" ", lineLength), text.lastIndexOf("\t", lineLength)), text.lastIndexOf("-", lineLength));
		if (place == -1)
			return text;
		else
			return text.substring(0, place).trim() + "<br>" + wordWrap(text.substring(place), lineLength);
	}

	@Override
	public Component getTableCellRendererComponent(JTable aTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		// if (value instanceof Integer)
		// value = intFormatter.format(value);
		if (value instanceof Double)
			value = doubleFormatter.format(value);
		if (value instanceof MappingStatus) {
			if (value == MappingStatus.APPROVED)
				value = "Approved";
			else if (value == MappingStatus.AUTO_MAPPED)
				value = "Automapped";
			else if (value == MappingStatus.AUTO_MAPPED_TO_1)
				value = "Auto mapped to 1";
			else if (value == MappingStatus.UNCHECKED)
				value = "Unchecked";
			else if (value == MappingStatus.INVALID_TARGET)
				value = "Invalid target";
			else if (value == MappingStatus.IGNORED)
				value = "Ignored";
		}
		Component component = super.getTableCellRendererComponent(aTable, value, isSelected, hasFocus, row, column);

		if (!isSelected) {
			int modelRow = aTable.convertRowIndexToModel(row);
			component.setForeground(Color.black);
			if (aTable.getModel().getValueAt(modelRow, 0) == MappingStatus.APPROVED) {
				if (row % 2 == 1)
					component.setBackground(checkedColor);
				else
					component.setBackground(checkedOddColor);
			} else if (aTable.getModel().getValueAt(modelRow, 0) == MappingStatus.INVALID_TARGET) {
				if (row % 2 == 1)
					component.setBackground(errorColor);
				else
					component.setBackground(errorOddColor);
			} else {
				if (row % 2 == 1) {
					component.setBackground(oddColor);
				} else {
					component.setBackground(evenColor);
				}
			}
			if (aTable.getModel().getValueAt(modelRow, 0) == MappingStatus.IGNORED) {
				component.setForeground(Color.gray);
			}
		}
		return component;
	}
}
