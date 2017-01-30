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

import org.ohdsi.usagi.ui.AboutDialog;
import org.ohdsi.usagi.ui.Global;

public class AboutAction extends AbstractAction {

	private static final long	serialVersionUID	= -6399524936473823131L;

	public AboutAction() {
		putValue(Action.NAME, "About Usagi");
		putValue(Action.SHORT_DESCRIPTION, "About Usagi");
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		AboutDialog dialog = new AboutDialog();
		dialog.setLocationRelativeTo(Global.frame);
		dialog.setVisible(true);
	}
}
