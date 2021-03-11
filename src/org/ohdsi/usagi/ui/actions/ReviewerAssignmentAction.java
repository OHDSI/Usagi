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

import org.ohdsi.usagi.ui.Global;
import org.ohdsi.usagi.ui.ReviewerAssignmentDialog;
import org.ohdsi.usagi.ui.ShowStatsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ReviewerAssignmentAction extends AbstractAction {

	private static final long	serialVersionUID	= -6399524936473823131L;

	public ReviewerAssignmentAction() {
		putValue(Action.NAME, "Assign Reviewers");
		putValue(Action.SHORT_DESCRIPTION, "Assign reviewer to the mappings");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		ReviewerAssignmentDialog dialog = new ReviewerAssignmentDialog();
		dialog.setLocationRelativeTo(Global.frame);
		dialog.setVisible(true);
	}

}
