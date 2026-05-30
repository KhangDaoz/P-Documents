package com.ptit.p.documents;

import com.ptit.p.documents.view.LoginFrm;
import javax.swing.SwingUtilities;

public class PDocuments {

    public static void main(String[] args) {
        try {
            boolean success = com.formdev.flatlaf.FlatIntelliJLaf.setup();
            System.out.println("DEBUG: FlatLaf setup success? " + success);
            javax.swing.UIManager.put("PasswordField.showRevealButton", true);
            javax.swing.UIManager.put("JPasswordField.showRevealButton", true);
            System.out.println("DEBUG: Active LookAndFeel is -> " + javax.swing.UIManager.getLookAndFeel().getName());
        } catch (Exception ex) {
            System.err.println("FlatLaf Look and Feel setup failed: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> new LoginFrm().setVisible(true));
    }
}
