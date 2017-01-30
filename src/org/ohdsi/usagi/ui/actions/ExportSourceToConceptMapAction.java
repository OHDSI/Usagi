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
package org.ohdsi.usagi.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.ohdsi.usagi.CodeMapping;
import org.ohdsi.usagi.CodeMapping.MappingStatus;
import org.ohdsi.usagi.ui.ExportSourceToConceptMapDialog;
import org.ohdsi.usagi.ui.Global;

public class ExportSourceToConceptMapAction extends AbstractAction {

	private static final long	serialVersionUID	= -1846753187468184738L;

	public ExportSourceToConceptMapAction() {
		putValue(Action.NAME, "Export source_to_concept_map");
		putValue(Action.SHORT_DESCRIPTION, "Export mapping to source_to_concept_map");
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		boolean hasApprovedMappings = false;
		for (CodeMapping mapping : Global.mapping)
			if (mapping.mappingStatus == MappingStatus.APPROVED) {
				hasApprovedMappings = true;
				break;
			}
		if (hasApprovedMappings) {
			ExportSourceToConceptMapDialog dialog = new ExportSourceToConceptMapDialog();
			dialog.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(Global.frame, "There are no approved mappings, so nothing to export", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

}
