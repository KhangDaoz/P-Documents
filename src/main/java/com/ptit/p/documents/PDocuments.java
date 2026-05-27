package com.ptit.p.documents;

import com.ptit.p.documents.view.LoginFrm;
import javax.swing.SwingUtilities;

public class PDocuments {

    public static void main(String[] args) {
        // Run Kien's Admin User Management GUI entry point
        try {
            com.formdev.flatlaf.FlatIntelliJLaf.setup();
        } catch (Exception ex) {
            System.err.println("FlatLaf Look and Feel setup failed: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrm().setVisible(true);
        });
    }
}


