package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.UserDAO;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SearchUserFrm extends JFrame implements ActionListener {
    public enum Mode {
        EDIT, DELETE
    }

    private final Mode mode;
    private final JTextField txtKey;
    private final JButton btnSearch;
    private final JButton btnAction; // Nút "Sửa" hoặc "Xoá" tuỳ thuộc vào Mode
    private final JButton btnBack;
    private final JTable tblUser;
    private final DefaultTableModel tableModel;
    private final UserDAO userDAO;
    private List<User> searchResults;
    private static final Icon EDIT_ICON = new EditIcon();
    private static final Icon DELETE_ICON = new DeleteIcon();

    public SearchUserFrm(Mode mode) {
        super("SearchUserFrm");
        this.mode = mode;
        this.userDAO = new UserDAO();
        this.searchResults = new ArrayList<>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);

        // pnlMain is the content pane with BorderLayout
        JPanel pnlMain = new JPanel(new BorderLayout(0, 15));
        pnlMain.setBackground(new Color(220, 224, 230));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setContentPane(pnlMain);

        // Top panel for search fields
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlTop.setBackground(new Color(220, 224, 230));

        txtKey = new JTextField("Enter keyword to search...");
        txtKey.setPreferredSize(new Dimension(350, 35));
        txtKey.setBackground(Color.WHITE);
        txtKey.setForeground(Color.GRAY);
        txtKey.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtKey.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 160, 175), 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        txtKey.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtKey.getText().equals("Enter keyword to search...")) {
                    txtKey.setText("");
                    txtKey.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtKey.getText().isEmpty()) {
                    txtKey.setText("Enter keyword to search...");
                    txtKey.setForeground(Color.GRAY);
                }
            }
        });
        txtKey.addActionListener(this);
        pnlTop.add(txtKey);

        btnSearch = new JButton("Search");
        btnSearch.setPreferredSize(new Dimension(100, 35));
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setForeground(Color.BLACK);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnSearch.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnSearch.setBackground(Color.WHITE);
            }
        });
        btnSearch.addActionListener(this);
        pnlTop.add(btnSearch);

        pnlMain.add(pnlTop, BorderLayout.NORTH);

        // Center panel for table and label
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setBackground(new Color(220, 224, 230));

        JLabel lblList = new JLabel("User List", JLabel.LEFT);
        lblList.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblList.setForeground(new Color(50, 60, 70));
        pnlCenter.add(lblList, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] columnNames = {"ID", "Full Name", "Username", "Password", "Phone", "Role", "", ""};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 6 || column == 7) {
                    return Icon.class;
                }
                return super.getColumnClass(column);
            }
        };
        tblUser = new JTable(tableModel);
        tblUser.setBackground(Color.WHITE);
        tblUser.setForeground(Color.BLACK);
        tblUser.setGridColor(new Color(220, 220, 220));
        tblUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUser.setRowHeight(30);
        tblUser.getTableHeader().setBackground(Color.WHITE);
        tblUser.getTableHeader().setForeground(Color.BLACK);
        tblUser.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblUser.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Căn giữa header bảng
        ((DefaultTableCellRenderer)tblUser.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Căn giữa các ô trong bảng (trừ 2 cột biểu tượng)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblUser.getColumnCount() - 2; i++) {
            tblUser.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Click trực tiếp vào icon Bút chì hoặc Thùng rác trên hàng để Sửa/Xoá
        tblUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblUser.rowAtPoint(evt.getPoint());
                int col = tblUser.columnAtPoint(evt.getPoint());
                if (row >= 0 && row < searchResults.size()) {
                    User selectedUser = searchResults.get(row);
                    if (col == 6) { // Cột sửa (✎)
                        dispose();
                        EditUserFrm editFrm = new EditUserFrm(selectedUser);
                        editFrm.setVisible(true);
                    } else if (col == 7) { // Cột xoá (🗑)
                        dispose();
                        ConfirmDeleteUserFrm confirmDeleteFrm = new ConfirmDeleteUserFrm(selectedUser);
                        confirmDeleteFrm.setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblUser);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175), 1));
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        pnlMain.add(pnlCenter, BorderLayout.CENTER);

        // Thiết lập kích thước cột
        tblUser.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        tblUser.getColumnModel().getColumn(1).setPreferredWidth(170); // Full Name
        tblUser.getColumnModel().getColumn(2).setPreferredWidth(100); // Username
        tblUser.getColumnModel().getColumn(3).setPreferredWidth(100); // Password
        tblUser.getColumnModel().getColumn(4).setPreferredWidth(110); // Phone
        tblUser.getColumnModel().getColumn(5).setPreferredWidth(90);  // Role
        tblUser.getColumnModel().getColumn(6).setPreferredWidth(35);  // Sửa ✎
        tblUser.getColumnModel().getColumn(7).setPreferredWidth(35);  // Xoá 🗑

        // Bottom panel for back button
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBottom.setBackground(new Color(220, 224, 230));

        btnBack = new JButton("Back");
        btnBack.setPreferredSize(new Dimension(100, 35));
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(Color.BLACK);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnBack.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnBack.setBackground(Color.WHITE);
            }
        });
        btnBack.addActionListener(this);
        pnlBottom.add(btnBack);

        pnlMain.add(pnlBottom, BorderLayout.SOUTH);

        // Dummy btnAction to satisfy variable references without display
        btnAction = new JButton();

        // Load danh sách người dùng ban đầu
        performSearch();
    }

    private void performSearch() {
        String keyword = txtKey.getText().trim();
        if (keyword.equals("Enter keyword to search...")) {
            keyword = "";
        }
        // Gọi phương thức searchUser() của lớp UserDAO
        searchResults = userDAO.searchUser(keyword);

        // Xóa dữ liệu cũ trên bảng
        tableModel.setRowCount(0);

        // Hiển thị danh sách kết quả lên bảng
        for (User u : searchResults) {
            String roleText = "Librarian";
            if ("admin".equalsIgnoreCase(u.getRole())) {
                roleText = "Admin";
            } else if ("manager".equalsIgnoreCase(u.getRole())) {
                roleText = "Manager";
            } else if ("staff".equalsIgnoreCase(u.getRole())) {
                roleText = "Staff";
            }

            Object[] row = {
                "NV" + String.format("%03d", u.getId()),
                u.getFullName(),
                u.getUsername(),
                "********",
                u.getPhone(),
                roleText,
                EDIT_ICON,
                DELETE_ICON
            };
            tableModel.addRow(row);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch || e.getSource() == txtKey) {
            performSearch();
        } else if (e.getSource() == btnBack) {
            this.dispose();
            UserManageFrm manageFrm = new UserManageFrm();
            manageFrm.setVisible(true);
        }
    }

    private static class EditIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw a modern, beautiful pencil icon
            g2.translate(x + 8, y + 8);
            g2.rotate(Math.toRadians(-45));
            
            // Draw pencil tip (wood color)
            g2.setColor(new Color(222, 184, 135)); // BurlyWood
            int[] px = {-3, 3, 0};
            int[] py = {3, 3, 7};
            g2.fillPolygon(px, py, 3);
            
            // Draw pencil graphite lead tip
            g2.setColor(new Color(60, 60, 60)); // Dark grey
            int[] lx = {-1, 1, 0};
            int[] ly = {5, 5, 7};
            g2.fillPolygon(lx, ly, 3);
            
            // Draw pencil body (blue/indigo)
            g2.setColor(new Color(70, 130, 180)); // SteelBlue
            g2.fillRect(-3, -7, 6, 10);
            
            // Draw silver band
            g2.setColor(new Color(192, 192, 192)); // Silver
            g2.fillRect(-3, -9, 6, 2);
            
            // Draw pink eraser on top
            g2.setColor(new Color(255, 182, 193)); // LightPink
            g2.fillRect(-3, -11, 6, 2);
            
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }

    private static class DeleteIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw a modern, beautiful trash bin icon
            g2.setColor(new Color(220, 53, 69)); // Crimson red
            
            // Lid handle
            g2.fillRect(x + 6, y + 1, 4, 2);
            // Lid plate
            g2.fillRect(x + 2, y + 3, 12, 2);
            
            // Container body
            int[] bx = {x + 4, x + 12, x + 11, x + 5};
            int[] by = {y + 5, y + 5, y + 14, y + 14};
            g2.fillPolygon(bx, by, 4);
            
            // Vertical stripes inside bin (white cuts)
            g2.setColor(Color.WHITE);
            g2.fillRect(x + 6, y + 7, 1, 5);
            g2.fillRect(x + 8, y + 7, 1, 5);
            g2.fillRect(x + 10, y + 7, 1, 5);
            
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}
