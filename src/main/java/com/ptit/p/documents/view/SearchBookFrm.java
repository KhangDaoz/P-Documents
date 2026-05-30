package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SearchBookFrm extends JFrame implements ActionListener {
    private JTextField txtKeyword;
    private JButton btnSearch;
    private JButton btnBack;
    private JTable tblResults;
    private DefaultTableModel tableModel;
    private User currentUser;
    private String mode; // "edit" hoặc "delete"
    private List<Book> searchResults;

    public SearchBookFrm(User user, String mode) {
        this.currentUser = user;
        this.mode = mode;
        initComponents();
    }

    private void initComponents() {
        String titleText = mode.equals("edit") ? "Tìm sách để sửa" : "Tìm sách để xóa";
        setTitle(titleText);
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel lblTitle = new JLabel(titleText.toUpperCase(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLACK);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.add(new JLabel("Từ khóa (tên sách/tác giả/ISBN):"));
        txtKeyword = new JTextField(25);
        searchPanel.add(txtKeyword);
        btnSearch = new JButton("Tìm");
        btnSearch.setBackground(UIManager.getColor("Button.background"));
        btnSearch.setForeground(Color.BLACK);
        btnSearch.setPreferredSize(new Dimension(80, 30));
        searchPanel.add(btnSearch);
        btnBack = new JButton("Quay lại");
        btnBack.setPreferredSize(new Dimension(100, 30));
        searchPanel.add(btnBack);

        // Đặt search panel ở trên bảng
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Thay đổi: đặt title và search vào topPanel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ISBN", "Tên sách", "Tác giả", "Thể loại", "Nhà xuất bản",
                "Năm XB", "Giá bìa", "Mô tả", "Số lượng"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblResults = new JTable(tableModel);
        tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblResults.setRowHeight(25);
        tblResults.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblResults.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(tblResults);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Hint label
        String hintText = mode.equals("edit")
                ? "Click vào một dòng sách để chỉnh sửa thông tin"
                : "Click vào một dòng sách để xóa";
        JLabel lblHint = new JLabel(hintText, SwingConstants.CENTER);
        lblHint.setFont(new Font("Arial", Font.ITALIC, 12));
        lblHint.setForeground(Color.BLACK);
        mainPanel.add(lblHint, BorderLayout.SOUTH);

        // Event handlers
        btnSearch.addActionListener(this);
        btnBack.addActionListener(this);

        tblResults.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    ActionEvent ae = new ActionEvent(tblResults, ActionEvent.ACTION_PERFORMED, "tableClick");
                    actionPerformed(ae);
                }
            }
        });

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            // performSearch() inlined
            String keyword = txtKeyword.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập từ khóa tìm kiếm!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BookDAO bookDAO = new BookDAO();
            searchResults = bookDAO.searchBook(keyword);

            // Xóa dữ liệu cũ
            tableModel.setRowCount(0);

            if (searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy sách nào với từ khóa: " + keyword,
                        "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Hiển thị kết quả
            for (Book book : searchResults) {
                Object[] rowData = {
                        book.getISBN(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getPublisher(),
                        book.getPublishYear(),
                        book.getPrice(),
                        book.getDescription(),
                        book.getTotalCopies()
                };
                tableModel.addRow(rowData);
            }
        } else if (e.getSource() == btnBack) {
            new BookManageFrm(currentUser).setVisible(true);
            this.dispose();
        } else if (e.getSource() == tblResults) {
            // handleBookSelection() inlined for table click
            int selectedRow = tblResults.getSelectedRow();
            if (selectedRow >= 0 && searchResults != null && selectedRow < searchResults.size()) {
                Book selectedBook = searchResults.get(selectedRow);
                if (mode.equals("edit")) {
                    new EditBookFrm(currentUser, selectedBook).setVisible(true);
                    this.dispose();
                } else if (mode.equals("delete")) {
                    // Kiểm tra tình trạng mượn
                    BookDAO bookDAO = new BookDAO();
                    boolean isBeingBorrowed = bookDAO.checkBookStatus(selectedBook.getISBN(), false);

                    if (isBeingBorrowed) {
                        JOptionPane.showMessageDialog(this,
                                "Sách đang có phiếu mượn ở trạng thái \"Đang mượn\" hoặc \"Chờ nhận sách\"!\n"
                                        + "Không thể xóa sách này.",
                                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        // Quay về trang chủ quản lý
                        new ManagerHomeFrm(currentUser).setVisible(true);
                        this.dispose();
                    } else {
                        new DeleteBookFrm(currentUser, selectedBook).setVisible(true);
                        this.dispose();
                    }
                }
            }
        }
}
}
