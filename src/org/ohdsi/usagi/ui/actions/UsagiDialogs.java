package org.ohdsi.usagi.ui.actions;

import org.ohdsi.usagi.ui.Global;

import javax.swing.*;

public class UsagiDialogs {
    public static boolean askExportUnapprovedMappings() {
        String[] options = {"Only approved","Approved and Unapproved"};
        int PromptResult = JOptionPane.showOptionDialog(
                Global.frame,
                "Do you want to export both approved and unapproved mappings?",
                "Export for review",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        return PromptResult == 1;
    }

    public static void warningNothingToExport() {
        JOptionPane.showMessageDialog(
                Global.frame,
                "There are no approved mappings, so nothing to export",
                "Nothing to export",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static boolean askBeforeExit() {
        String[] objButtons = {"Yes","No"};
        int PromptResult = JOptionPane.showOptionDialog(
                Global.frame,
                "Do you want to exit?\nPlease make sure that any work is saved",
                "Usagi",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, objButtons, objButtons[1]
        );
        return PromptResult == JOptionPane.YES_OPTION;
    }

}
