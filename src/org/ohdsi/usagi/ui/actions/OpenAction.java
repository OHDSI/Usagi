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

public class OpenAction extends AbstractAction {

	private static final long	serialVersionUID	= 3420357922150237898L;

	public OpenAction() {
		putValue(Action.NAME, "Open");
		putValue(Action.SHORT_DESCRIPTION, "Open mapping file");
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser fileChooser = new JFileChooser(Global.folder);
		FileFilter csvFilter = new FileNameExtensionFilter("CSV files", "csv");
		fileChooser.setFileFilter(csvFilter);
		if (fileChooser.showOpenDialog(Global.frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			Global.frame.setTitle("Usagi - " + file.getName());
			Global.filename = file.getAbsolutePath();
			Global.mapping.loadFromFile(Global.filename);
			Global.usagiSearchEngine.close();
			Global.usagiSearchEngine.createDerivedIndex(Global.mapping.getSourceCodes(), Global.frame);
			Global.mappingDetailPanel.doSearch();
			Global.applyPreviousMappingAction.setEnabled(true);
			Global.saveAction.setEnabled(true);
			Global.saveAsAction.setEnabled(true);
			Global.exportAction.setEnabled(true);
			Global.exportForReviewAction.setEnabled(true);
		}
	}

}
