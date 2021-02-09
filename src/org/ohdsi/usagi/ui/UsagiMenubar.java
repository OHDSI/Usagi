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

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class UsagiMenubar extends JMenuBar {
	private static final long	serialVersionUID	= 2177508154012319525L;

	public UsagiMenubar() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		add(fileMenu);

		fileMenu.add(Global.openAction);
		fileMenu.add(Global.importAction);
		fileMenu.add(Global.applyPreviousMappingAction);
		fileMenu.add(Global.exportAction);
		fileMenu.add(Global.exportForReviewAction);
		fileMenu.add(Global.saveAction);
		fileMenu.add(Global.saveAsAction);
		fileMenu.add(Global.exitAction);

		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		add(editMenu);

		editMenu.add(Global.approveAction);
		editMenu.add(Global.approveSelectedAction);
		editMenu.add(Global.ignoreAction);
		editMenu.add(Global.ignoreSelectedAction);
		editMenu.add(Global.clearSelectedAction);
		editMenu.add(Global.reviewerAssignmentAction);

		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		add(viewMenu);

		viewMenu.add(Global.conceptInfoAction);
		viewMenu.add(Global.athenaAction);
		viewMenu.add(Global.googleSearchAction);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		add(helpMenu);

		helpMenu.add(Global.rebuildIndexAction);
		helpMenu.add(Global.showStatsAction);
		helpMenu.add(Global.aboutAction);

	}
}
