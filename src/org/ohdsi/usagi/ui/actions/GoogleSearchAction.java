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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GoogleSearchAction extends AbstractAction {

	private static final long serialVersionUID = -934859464521233L;
	private static final String GOOGLE_Q_URL = "https://www.google.com/search?q=";
	private String sourceTerm;

	public GoogleSearchAction() {
		putValue(Action.NAME, "Google (web)");
		putValue(Action.SHORT_DESCRIPTION, "Search source term on Google");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.ALT_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI(GOOGLE_Q_URL + sourceTerm));
		} catch (URISyntaxException | IOException ex) {

		}
	}

	public void setSourceTerm(String sourceTerm) {
		this.sourceTerm = sourceTerm;
	}
}
