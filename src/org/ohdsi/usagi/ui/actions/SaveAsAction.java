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
package org.ohdsi.usagi.ui.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.usagi.ui.Global;

public class SaveAsAction extends AbstractAction {

	private static final long	serialVersionUID	= -2219725738187965551L;

	public SaveAsAction() {
		putValue(Action.NAME, "Save As...");
		putValue(Action.SHORT_DESCRIPTION, "Save mapping file as");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser fileChooser = new JFileChooser(Global.folder);
		FileFilter csvFilter = new FileNameExtensionFilter("CSV files", "csv");
		fileChooser.setFileFilter(csvFilter);
		if (fileChooser.showSaveDialog(Global.frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".csv"))
				file = new File(file.getAbsolutePath() + ".csv");
			Global.frame.setTitle("Usagi - " + file.getName());
			Global.filename = file.getAbsolutePath();
			Global.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Global.mapping.saveToFile(Global.filename);
			Global.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

}
