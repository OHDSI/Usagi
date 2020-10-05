/*******************************************************************************
 * Copyright 2020 Observational Health Data Sciences and Informatics & The Hyve
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

import org.ohdsi.usagi.ui.Global;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class FlagAction extends AbstractAction {

	private static final long	serialVersionUID	= -395107404415936659L;

	public FlagAction() {
		setToFlag();
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.ALT_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Global.mappingDetailPanel.flag();
	}

	public void setToFlag() {
		putValue(Action.NAME, "Flag");
		putValue(Action.SHORT_DESCRIPTION, "Flag this source code for further review");
	}

	public void setToUnflag() {
		putValue(Action.NAME, "Unflag");
		putValue(Action.SHORT_DESCRIPTION, "Unflag this code");
	}
}
