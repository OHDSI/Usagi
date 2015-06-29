/*******************************************************************************
 * Copyright 2014 Observational Health Data Sciences and Informatics
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

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ohdsi.usagi.UsagiSearchEngine;
import org.ohdsi.usagi.ui.actions.AboutAction;
import org.ohdsi.usagi.ui.actions.ApproveAction;
import org.ohdsi.usagi.ui.actions.ApproveAllAction;
import org.ohdsi.usagi.ui.actions.ClearAllAction;
import org.ohdsi.usagi.ui.actions.ConceptInformationAction;
import org.ohdsi.usagi.ui.actions.ExportSourceToConceptMapAction;
import org.ohdsi.usagi.ui.actions.ImportAction;
import org.ohdsi.usagi.ui.actions.OpenAction;
import org.ohdsi.usagi.ui.actions.RebuildIndexAction;
import org.ohdsi.usagi.ui.actions.SaveAction;
import org.ohdsi.usagi.ui.actions.SaveAsAction;

/**
 * The main application class
 */
public class UsagiMain implements ActionListener {

	private JFrame			frame;

	public static void main(String[] args) {
		new UsagiMain(args);
	}

	public UsagiMain(String[] args) {
		frame = new JFrame("Usagi");

		// Initialize global variables:
		Global.mapping = new Mapping();
		if (args.length != 0)
			Global.folder = args[0];
		else
			Global.folder = new File("").getAbsolutePath();

		Global.usagiSearchEngine = new UsagiSearchEngine(Global.folder);
		Global.conceptInformationDialog = new ConceptInformationDialog();
		Global.frame = frame;
		Global.openAction = new OpenAction();
		Global.importAction = new ImportAction();
		Global.exportAction = new ExportSourceToConceptMapAction();
		Global.saveAction = new SaveAction();
		Global.saveAsAction = new SaveAsAction();
		Global.approveAction = new ApproveAction();
		Global.conceptInfoAction = new ConceptInformationAction();
		Global.aboutAction = new AboutAction();
		Global.approveAllAction = new ApproveAllAction();
		Global.rebuildIndexAction = new RebuildIndexAction();

		Global.saveAction.setEnabled(false);
		Global.saveAsAction.setEnabled(false);
		Global.exportAction.setEnabled(false);
		Global.approveAction.setEnabled(false);
		Global.approveAllAction.setEnabled(false);
		Global.clearAllAction = new ClearAllAction();
		Global.clearAllAction.setEnabled(false);
		Global.conceptInfoAction.setEnabled(false);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setLayout(new BorderLayout());
		frame.setJMenuBar(new UsagiMenubar());

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		Global.mappingTablePanel = new MappingTablePanel();
		main.add(Global.mappingTablePanel, BorderLayout.CENTER);

		Global.mappingDetailPanel = new MappingDetailPanel();
		main.add(Global.mappingDetailPanel, BorderLayout.SOUTH);

		Global.mappingTablePanel.addCodeSelectedListener(Global.mappingDetailPanel);
		frame.add(main, BorderLayout.CENTER);

		Global.statusBar = new UsagiStatusBar();
		frame.add(Global.statusBar, BorderLayout.SOUTH);

		loadIcons(frame);
		frame.pack();
		frame.setVisible(true);

		if (!Global.usagiSearchEngine.mainIndexExists())
			Global.rebuildIndexAction.actionPerformed(null);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

	}

	private void loadIcons(JFrame f) {
		List<Image> icons = new ArrayList<Image>();
		icons.add(loadIcon("Usagi16.png", f));
		icons.add(loadIcon("Usagi32.png", f));
		icons.add(loadIcon("Usagi48.png", f));
		icons.add(loadIcon("Usagi64.png", f));
		icons.add(loadIcon("Usagi128.png", f));
		icons.add(loadIcon("Usagi256.png", f));
		f.setIconImages(icons);
	}

	private Image loadIcon(String name, JFrame f) {
		Image icon = Toolkit.getDefaultToolkit().getImage(UsagiMain.class.getResource(name));
		MediaTracker mediaTracker = new MediaTracker(f);
		mediaTracker.addImage(icon, 0);
		try {
			mediaTracker.waitForID(0);
			return icon;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

}
