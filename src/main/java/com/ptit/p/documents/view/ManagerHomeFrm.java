package com.ptit.p.documents.view;

import javax.swing.*;
import java.awt.*;

/**
 * Giao diện chính của nhân viên quản lý (spec §1.a).
 * Có nút "Báo cáo thống kê" mở StatMenuFrm (spec §1.b bước 1-3).
 */
public class ManagerHomeFrm extends JFrame {

    public ManagerHomeFrm() {
        setTitle("ManagerHomeFrm - Trang chủ nhân viên quản lý");
        setSize(560, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 20f));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;

        JButton btnStat = new JButton("Báo cáo thống kê");
        btnStat.setPreferredSize(new Dimension(220, 44));
        g.gridy = 0; center.add(btnStat, g);

        JButton btnExit = new JButton("Thoát");
        btnExit.setPreferredSize(new Dimension(220, 44));
        g.gridy = 1; center.add(btnExit, g);

        add(center, BorderLayout.CENTER);

        // spec §1.b bước 2-3: actionPerformed -> mở StatMenuFrm
        btnStat.addActionListener(e -> {
            new StatMenuFrm(this).setVisible(true);
            setVisible(false);
        });
        btnExit.addActionListener(e -> System.exit(0));
    }
}
