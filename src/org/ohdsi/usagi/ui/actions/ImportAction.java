/*******************************************************************************
 * Copyright 2018 Observational Health Data Sciences and Informatics
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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.usagi.ui.Global;
import org.ohdsi.usagi.ui.ImportDialog;

public class ImportAction extends AbstractAction {

	private static final long	serialVersionUID	= 3420357922150237898L;

	public ImportAction() {
		putValue(Action.NAME, "Import codes");
		putValue(Action.SHORT_DESCRIPTION, "Import a file containing codes");
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser fileChooser = new JFileChooser(Global.folder);
		FileFilter csvFilter = new FileNameExtensionFilter("CSV files", "csv", "txt");
		fileChooser.addChoosableFileFilter(csvFilter);
		FileFilter xlsxFilter = new FileNameExtensionFilter("Microsoft Excell files", "xlsx");
		fileChooser.addChoosableFileFilter(xlsxFilter);
		FileFilter allFilter = new FileNameExtensionFilter("CSV files or Microsoft Excell files", "csv", "txt", "xlsx");
		fileChooser.addChoosableFileFilter(allFilter);
		fileChooser.setFileFilter(allFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(Global.frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			new ImportDialog(file.getAbsolutePath());
		}
	}
}
