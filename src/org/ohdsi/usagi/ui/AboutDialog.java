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
package org.ohdsi.usagi.ui;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class AboutDialog extends JDialog {

	private static final long	serialVersionUID	= 2028328868610404663L;
	private JEditorPane			text;

	public AboutDialog() {
		setTitle("About Usagi");
		setLayout(new GridBagLayout());

		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.ipadx = 10;
		g.ipady = 10;

		g.gridx = 0;
		g.gridy = 0;

		Image icon = Toolkit.getDefaultToolkit().getImage(UsagiMain.class.getResource("Usagi64.png"));
		add(new JLabel(new ImageIcon(icon)), g);

		g.gridx = 1;
		g.gridy = 0;

		text = new JEditorPane(
				"text/html",
				"Usagi was developed by <a href=\"http://ohdsi.org\">Observational Health Data Sciences and Informatics</a> (OHDSI).<br/><br/>For help, please review the <a href =\"http://www.ohdsi.org/web/wiki/doku.php?id=documentation:software:usagi\">Usagi Wiki</a>.");

		text.setEditable(false);
		text.setOpaque(false);

		text.addHyperlinkListener(new HyperlinkListener() {

			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
					try {
						// jep.setPage(hle.getURL());
						try {
							Desktop desktop = Desktop.getDesktop();
							desktop.browse(new URI(hle.getURL().toString()));
						} catch (URISyntaxException ex) {

						}
					} catch (IOException ex) {

					}

				}
			}
		});
		add(text, g);

		g.gridx = 0;
		g.gridy = 1;
		g.gridwidth = 2;

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();

			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalGlue());

		add(buttonPanel, g);

		setModal(true);
		setResizable(false);
		pack();

	}

	private void close() {
		setVisible(false);
	}
}
