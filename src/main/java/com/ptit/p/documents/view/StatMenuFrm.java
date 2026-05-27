package com.ptit.p.documents.view;

import javax.swing.*;
import java.awt.*;

/**
 * Menu các loại thống kê (spec §1.a, §2.a).
 * - "Thống kê sách mượn nhiều"          -> BorrowingStatFrm (Modul 1)
 * - "Báo cáo sách hư hỏng / thất lạc"   -> StockStatFrm     (Modul 2)
 */
public class StatMenuFrm extends JFrame {

    private final ManagerHomeFrm parent;

    public StatMenuFrm(ManagerHomeFrm parent) {
        this.parent = parent;

        setTitle("StatMenuFrm - Menu báo cáo thống kê");
        setSize(520, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("CHỌN LOẠI BÁO CÁO", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        header.setBorder(BorderFactory.createEmptyBorder(18, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;

        JButton btnBorrowing = new JButton("Thống kê sách mượn nhiều");
        btnBorrowing.setPreferredSize(new Dimension(280, 44));
        g.gridy = 0; center.add(btnBorrowing, g);

        JButton btnStock = new JButton("Báo cáo sách hư hỏng / thất lạc");
        btnStock.setPreferredSize(new Dimension(280, 44));
        g.gridy = 1; center.add(btnStock, g);

        JButton btnBack = new JButton("Trở về");
        btnBack.setPreferredSize(new Dimension(280, 36));
        g.gridy = 2; center.add(btnBack, g);

        add(center, BorderLayout.CENTER);

        // spec §1.b bước 7-8
        btnBorrowing.addActionListener(e -> new BorrowingStatFrm(this).setVisible(true));
        // spec §2.b bước 7-8
        btnStock.addActionListener(e -> new StockStatFrm(this).setVisible(true));
        btnBack.addActionListener(e -> {
            dispose();
            if (parent != null) parent.setVisible(true);
        });
    }
}
