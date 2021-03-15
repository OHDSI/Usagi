/*******************************************************************************
 * Copyright 2021 Observational Health Data Sciences and Informatics & The Hyve
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
import org.ohdsi.usagi.ui.ShowReviewStatsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ShowReviewStatsAction extends AbstractAction {

	private static final long serialVersionUID = -5823000156280268511L;

	public ShowReviewStatsAction() {
		putValue(Action.NAME, "Show code review statistics");
		putValue(Action.SHORT_DESCRIPTION, "Show review stats");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		ShowReviewStatsDialog dialog = new ShowReviewStatsDialog();
		dialog.setLocationRelativeTo(Global.frame);
		dialog.setVisible(true);
	}
}
