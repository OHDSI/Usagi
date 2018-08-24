package org.ohdsi.usagi.indexBuilding;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.ohdsi.usagi.ErrorReport;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.utilities.StringUtilities;

/**
 * Calls the builders for the Berkeley DB and the Lucene index, and create a dialog for the user
 */
public class IndexBuildCoordinator {

	public static void main(String[] args) {
		Global.folder = "c:/data/usagi/";
		String vocabFolder = "C:\\Data\\OMOP Standard Vocabulary V5\\Vocabulary-20180823";
//		String loincFolder = "c:/Data/LOINC/loinc.csv";
		String loincFolder = null;
		IndexBuildCoordinator buildIndex = new IndexBuildCoordinator();
		buildIndex.buildIndexes(vocabFolder, loincFolder);
	}

	public void buildIndexes(String vocabFolder, String loincFile) {
		JDialog dialog = null;
		JLabel label = null;
		if (Global.frame != null) {
			dialog = new JDialog(Global.frame, "Progress Dialog", false);

			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createRaisedBevelBorder());

			JPanel sub = new JPanel();
			sub.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
			sub.setLayout(new BoxLayout(sub, BoxLayout.Y_AXIS));

			sub.add(new JLabel("Building index. This will take a while...."));

			label = new JLabel("Starting");
			sub.add(label);
			panel.add(sub);
			dialog.add(panel);

			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setSize(300, 75);
			dialog.setLocationRelativeTo(Global.frame);
			dialog.setUndecorated(true);
			dialog.setModal(true);
		}
		BuildThread thread = new BuildThread(vocabFolder, loincFile, label, dialog);
		thread.start();
		if (dialog != null)
			dialog.setVisible(true);
		try {
			thread.join();
			JOptionPane.showMessageDialog(Global.frame, "Please restart Usagi to use the new index");
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public class BuildThread extends Thread {

		private JDialog	dialog;
		private JLabel	label;
		private String	vocabFolder;
		private String	loincFile;

		public BuildThread(String vocabFolder, String loincFile, JLabel label, JDialog dialog) {
			this.vocabFolder = vocabFolder;
			this.loincFile = loincFile;
			this.label = label;
			this.dialog = dialog;
		}

		public void report(String message) {
			if (label != null)
				label.setText(message);
			else
				System.out.println("Message: " + message);
		}

		public void run() {
			try {
				VocabVersionGrabber vocabVersionGrabber = new VocabVersionGrabber();
				vocabVersionGrabber.grabVersion(vocabFolder);
				
				BerkeleyDbBuilder berkeleyDbBuilder = new BerkeleyDbBuilder();
				berkeleyDbBuilder.buildIndex(vocabFolder, loincFile, this);

				LuceneIndexBuilder luceneIndexBuilder = new LuceneIndexBuilder();
				luceneIndexBuilder.buildIndex(vocabFolder, loincFile, this);
				sleep(2000);

				System.out.println("Finished building indexes");
				if (dialog != null)
					dialog.setVisible(false);
			} catch (Exception e) {
				handleError(e);
				if (dialog != null)
					dialog.setVisible(false);
			}
		}

		private void handleError(Exception e) {
			System.err.println("Error: " + e.getMessage());
			String errorReportFilename = ErrorReport.generate(Global.folder, e);
			String message = "Error: " + e.getLocalizedMessage();
			message += "\nAn error report has been generated:\n" + errorReportFilename;
			System.out.println(message);
			if (Global.frame != null)
				JOptionPane.showMessageDialog(Global.frame, StringUtilities.wordWrap(message, 80), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
