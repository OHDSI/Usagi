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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.ohdsi.usagi.ui.Global;

public class ConceptInformationAction extends AbstractAction {

	private static final long	serialVersionUID	= -1846753187468184738L;

	public ConceptInformationAction() {
		putValue(Action.NAME, "Concept information");
		putValue(Action.SHORT_DESCRIPTION, "Show additional concept information");
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Global.conceptInformationDialog.setVisible(true);
	}

}
