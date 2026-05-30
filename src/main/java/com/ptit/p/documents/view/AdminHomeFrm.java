package com.ptit.p.documents.view;

import com.ptit.p.documents.model.User;
import javax.swing.JFrame;

public class AdminHomeFrm extends JFrame {
    public AdminHomeFrm(User user) {
        setTitle("Admin Home - " + user.getFullName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}