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

import org.ohdsi.usagi.Concept;
import org.ohdsi.usagi.ui.AboutDialog;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.usagi.ui.UsagiMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AthenaAction extends AbstractAction {

	private static final long serialVersionUID = -25905854723973L;
	private static final String ATHENA_URL = "https://athena.ohdsi.org/search-terms/terms/";
	private Concept selectedConcept;

	public AthenaAction() {
		putValue(Action.NAME, "Athena (web)");
		putValue(Action.SHORT_DESCRIPTION, "Link out to Athena web based concept browser, showing page of currently selected OMOP concept.");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI(ATHENA_URL + selectedConcept.conceptId));
		} catch (URISyntaxException | IOException ex) {

		}
	}

	public void setConcept(Concept concept) {
		selectedConcept = concept;
	}
}
