package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;

/**
 * Giao diện chon sach cho phieu muon — Buoc 1 cua module Dat Sach.
 */
public class SearchBorrowFrm extends JFrame implements ActionListener {

    private User      u;
    private JTextField txtBookName;
    private JTextField txtAuthor;
    private JTextField txtGenre;
    private JTextField txtISBN;
    private JButton    btnSearch;
    private JTable     tblListBook;
    private JLabel     lblStatus;

    private JTable     tblCart;
    private JButton    btnRemoveFromCart;
    private JButton    btnNext;
    private JLabel     lblCartCount;

    private DefaultTableModel tableModel;
    private DefaultTableModel cartModel;
    private ArrayList<Book>   searchResults = new ArrayList<>();
    private Borrowing         currentBorrowing;

    public SearchBorrowFrm(User u) {
        this.u = u;
        LocalDate today = LocalDate.now();
        LocalDate receiveDate = today.plusDays(2);
        currentBorrowing = new Borrowing(null, u, today, receiveDate);
        initComponents();
    }

    private void initComponents() {
        setTitle("Dat sach — Buoc 1: Chon sach");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(4, 4));

        // ---- Panel tìm kiếm ----
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tim kiem sach"));

        searchPanel.add(new JLabel("Ten sach:"));
        txtBookName = new JTextField(12);
        searchPanel.add(txtBookName);

        searchPanel.add(new JLabel("Tac gia:"));
        txtAuthor = new JTextField(10);
        searchPanel.add(txtAuthor);

        searchPanel.add(new JLabel("The loai:"));
        txtGenre = new JTextField(8);
        searchPanel.add(txtGenre);

        searchPanel.add(new JLabel("ISBN:"));
        txtISBN = new JTextField(8);
        searchPanel.add(txtISBN);

        btnSearch = new JButton("Tim kiem");
        btnSearch.addActionListener(this);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);

        // ---- Bảng kết quả tìm kiếm ----
        String[] searchCols = {"ISBN", "Ten sach", "Tac gia", "The loai", "Con lai"};
        tableModel = new DefaultTableModel(searchCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblListBook = new JTable(tableModel);
        tblListBook.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        lblStatus = new JLabel("Nhap tu khoa va nhan Tim kiem. Double-click de them vao phieu.");
        tblListBook.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) addBookToCart();
            }
        });

        JPanel resultPanel = new JPanel(new BorderLayout(2, 2));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Ket qua tim kiem"));
        resultPanel.add(new JScrollPane(tblListBook), BorderLayout.CENTER);
        resultPanel.add(lblStatus, BorderLayout.SOUTH);

        // ---- Bảng giỏ sách ----
        String[] cartCols = {"STT", "ISBN", "Ten sach", "Tac gia", "Gia (VND)"};
        cartModel = new DefaultTableModel(cartCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCart = new JTable(cartModel);
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        lblCartCount = new JLabel("Gio sach: 0 cuon");
        btnRemoveFromCart = new JButton("Xoa sach khoi gio");
        btnRemoveFromCart.addActionListener(this);

        JPanel cartTopBar = new JPanel(new BorderLayout());
        cartTopBar.add(lblCartCount, BorderLayout.WEST);
        cartTopBar.add(btnRemoveFromCart, BorderLayout.EAST);

        JPanel cartPanel = new JPanel(new BorderLayout(2, 2));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Gio sach da chon"));
        cartPanel.add(cartTopBar, BorderLayout.NORTH);
        cartPanel.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        // ---- SplitPane ----
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultPanel, cartPanel);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        // ---- Footer ----
        btnNext = new JButton("Tiep theo: Chon sinh vien");
        btnNext.setEnabled(false);
        btnNext.addActionListener(this);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(btnNext);
        add(footerPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            doSearch();
        } else if (e.getSource() == btnRemoveFromCart) {
            removeFromCart();
        } else if (e.getSource() == btnNext) {
            proceedToStudentSearch();
        }
    }

    private void doSearch() {
        BookDAO dao = new BookDAO();
        searchResults = dao.searchBook(
                txtBookName.getText().trim(),
                txtAuthor.getText().trim(),
                txtGenre.getText().trim(),
                txtISBN.getText().trim());

        tableModel.setRowCount(0);
        for (Book b : searchResults) {
            tableModel.addRow(new Object[]{
                    b.getIsbn(), b.getTitle(), b.getAuthor(),
                    b.getGenre(), b.getAvailableCopies()
            });
        }
        lblStatus.setText(searchResults.isEmpty()
                ? "Khong tim thay sach phu hop."
                : "Tim thay " + searchResults.size() + " sach. Double-click de them vao phieu.");
    }

    private void addBookToCart() {
        int row = tblListBook.getSelectedRow();
        if (row < 0 || row >= searchResults.size()) return;

        Book selected = searchResults.get(row);

        if (selected.getAvailableCopies() == 0) {
            JOptionPane.showMessageDialog(this,
                    "\"" + selected.getTitle() + "\" hien khong con ban sao kha dung.",
                    "Het sach", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (BorrowedBook bb : currentBorrowing.getBooks()) {
            if (bb.getBook() != null && bb.getBook().getIsbn().equals(selected.getIsbn())) {
                JOptionPane.showMessageDialog(this,
                        "\"" + selected.getTitle() + "\" da co trong phieu muon roi!",
                        "Trung sach", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        LocalDate returnDate = LocalDate.now().plusDays(14);
        BorrowedBook bb = new BorrowedBook(selected, returnDate, selected.getPrice());
        currentBorrowing.getBooks().add(bb);

        int stt = currentBorrowing.getBooks().size();
        cartModel.addRow(new Object[]{
                stt, selected.getIsbn(), selected.getTitle(),
                selected.getAuthor(), String.format("%,.0f", selected.getPrice())
        });
        updateCartStatus();
    }

    private void removeFromCart() {
        int row = tblCart.getSelectedRow();
        if (row < 0 || row >= currentBorrowing.getBooks().size()) {
            JOptionPane.showMessageDialog(this,
                    "Vui long chon mot dong sach trong gio de xoa.",
                    "Chua chon sach", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xoa \"" + currentBorrowing.getBooks().get(row).getBook().getTitle() + "\" khoi phieu?",
                "Xac nhan xoa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        currentBorrowing.getBooks().remove(row);
        rebuildCartTable();
    }

    private void rebuildCartTable() {
        cartModel.setRowCount(0);
        int stt = 1;
        for (BorrowedBook bb : currentBorrowing.getBooks()) {
            Book bk = bb.getBook();
            cartModel.addRow(new Object[]{
                    stt++,
                    bk != null ? bk.getIsbn()   : "",
                    bk != null ? bk.getTitle()  : "",
                    bk != null ? bk.getAuthor() : "",
                    bk != null ? String.format("%,.0f", bk.getPrice()) : ""
            });
        }
        updateCartStatus();
    }

    private void updateCartStatus() {
        int count = currentBorrowing.getBooks().size();
        lblCartCount.setText("Gio sach: " + count + " cuon");
        btnNext.setEnabled(count > 0);
    }

    private void proceedToStudentSearch() {
        if (currentBorrowing.getBooks().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui long them it nhat 1 cuon sach vao phieu muon.",
                    "Gio sach trong", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new SearchStudentFrm(currentBorrowing).setVisible(true);
        this.dispose();
    }
}
