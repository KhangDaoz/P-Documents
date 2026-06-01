

package com.ptit.p.documents;

import com.ptit.p.documents.dao.DAO;
import com.ptit.p.documents.view.LoginFrm;

import javax.swing.*;
import java.awt.Color;

public class PDocuments {

    public static void main(String[] args) {
        
        try {
            boolean success = com.formdev.flatlaf.FlatIntelliJLaf.setup();
            System.out.println("DEBUG: FlatLaf setup success? " + success);
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("Viewport.background", Color.WHITE);
            UIManager.put("ScrollPane.background", Color.WHITE);
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("TableHeader.background", Color.WHITE);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("FormattedTextField.background", Color.WHITE);
            UIManager.put("PasswordField.background", Color.WHITE);
            UIManager.put("TextArea.background", Color.WHITE);
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("Button.background", Color.WHITE);
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("MenuBar.background", Color.WHITE);
            UIManager.put("ToolBar.background", Color.WHITE);
            UIManager.put("PasswordField.showRevealButton", true);
            UIManager.put("JPasswordField.showRevealButton", true);
            System.out.println("DEBUG: Active LookAndFeel is -> " + UIManager.getLookAndFeel().getName());
        } catch (Exception ex) {
            System.err.println("FlatLaf Look and Feel setup failed: " + ex.getMessage());
        }

        
        
        try {
            new DAO();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null,
                    "Không thể kết nối database p_documents!\n"
                    + "Vui lòng kiểm tra:\n"
                    + "  • MySQL đang chạy trên localhost:3306\n"
                    + "  • Đã chạy src/main/resources/schema.sql để tạo CSDL\n"
                    + "  • Username/password của root đã được thử (1812 và 123456)",
                    "Lỗi kết nối Database",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        
        SwingUtilities.invokeLater(() -> {
            new LoginFrm().setVisible(true);
        });
    }
}
