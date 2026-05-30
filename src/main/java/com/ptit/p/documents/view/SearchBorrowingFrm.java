package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowingDAO;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SearchBorrowingFrm extends JFrame {
    private User currentUser;
    private SearchMode mode;
    private BorrowingDAO borrowingDAO;

    private JTextField txtStudentId;
    private JTextField txtStudentName;
    private JButton btnSearch;
    private JButton btnClear;
    private JTable tblResult;
    private DefaultTableModel tbmResult;
    private JButton btnSelect;
    private JButton btnBack;

    private List<Borrowing> searchResults;

    // Inline Detail Panel components
    private JSplitPane mainSplitPane;
    private JPanel pnlBorrowingDetail;
    private JLabel lblDetailName;
    private JLabel lblDetailId;
    private JTable tblBorrowedBooks;
    private DefaultTableModel tbmBorrowedBooks;
    private JButton btnAddFine;
    private JButton btnContinue;
    private JButton btnDetailBack;

    private Borrowing selectedBorrowing;
    private BorrowedBook selectedBorrowedBook;

    public SearchBorrowingFrm(User user, SearchMode mode) {
        this.currentUser = user;
        this.mode = mode;
        this.borrowingDAO = new BorrowingDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Tìm kiếm phiếu mượn");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // NORTH: pnlSearch
        JPanel pnlSearch = new JPanel(new GridBagLayout());
        pnlSearch.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        pnlSearch.add(new JLabel("Mã sinh viên:"), gbc);
        txtStudentId = new JTextField(12);
        gbc.gridx = 1;
        pnlSearch.add(txtStudentId, gbc);

        gbc.gridx = 2;
        pnlSearch.add(new JLabel("Tên sinh viên:"), gbc);
        txtStudentName = new JTextField(15);
        gbc.gridx = 3;
        pnlSearch.add(txtStudentName, gbc);

        btnSearch = new JButton("Tìm kiếm");
        gbc.gridx = 4;
        pnlSearch.add(btnSearch, gbc);

        btnClear = new JButton("Xóa");
        gbc.gridx = 5;
        pnlSearch.add(btnClear, gbc);

        add(pnlSearch, BorderLayout.NORTH);

        // CENTER: tblResult inside JSplitPane
        String[] columns = {"#", "Mã PM", "Tên sinh viên", "Ngày mượn", "Ngày hẹn nhận", "Trạng thái"};
        tbmResult = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblResult = new JTable(tbmResult);
        tblResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrResult = new JScrollPane(tblResult);

        // SOUTH: pnlActions
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSelect = new JButton(mode == SearchMode.RETURN_BOOK ? "Chọn phiếu trả" : "Chọn phiếu mượn");
        btnBack = new JButton("Quay lại");
        pnlActions.add(btnSelect);
        pnlActions.add(btnBack);

        // Wrapper for TOP part
        JPanel pnlTopWrap = new JPanel(new BorderLayout());
        pnlTopWrap.add(scrResult, BorderLayout.CENTER);
        pnlTopWrap.add(pnlActions, BorderLayout.SOUTH);

        // Details Panel (Bottom part for SplitPane)
        pnlBorrowingDetail = new JPanel(new BorderLayout());
        pnlBorrowingDetail.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu mượn"));
        pnlBorrowingDetail.setVisible(false);

        JPanel pnlDetailInfo = new JPanel(new GridLayout(2, 2, 10, 10));
        lblDetailName = new JLabel("Họ tên: ");
        lblDetailId = new JLabel("Mã SV: ");
        pnlDetailInfo.add(lblDetailName);
        pnlDetailInfo.add(lblDetailId);
        pnlBorrowingDetail.add(pnlDetailInfo, BorderLayout.NORTH);

        String[] bookCols = {"#", "Mã sách", "Tên sách", "Hạn trả", "Trạng thái", "Lỗi phạt"};
        tbmBorrowedBooks = new DefaultTableModel(bookCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBorrowedBooks = new JTable(tbmBorrowedBooks);
        pnlBorrowingDetail.add(new JScrollPane(tblBorrowedBooks), BorderLayout.CENTER);

        JPanel pnlDetailActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAddFine = new JButton("Thêm lỗi phạt");
        btnContinue = new JButton("Tiếp tục ->");
        btnDetailBack = new JButton("Quay lại");
        pnlDetailActions.add(btnAddFine);
        pnlDetailActions.add(btnContinue);
        pnlDetailActions.add(btnDetailBack);
        pnlBorrowingDetail.add(pnlDetailActions, BorderLayout.SOUTH);

        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlTopWrap, pnlBorrowingDetail);
        mainSplitPane.setDividerLocation(300);
        mainSplitPane.setResizeWeight(0.6);
        add(mainSplitPane, BorderLayout.CENTER);

        // Event Listeners
        btnSearch.addActionListener(e -> searchAction());
        btnClear.addActionListener(e -> clearAction());
        btnBack.addActionListener(e -> dispose());
        btnSelect.addActionListener(e -> selectAction());
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    selectAction();
                }
            }
        });

        btnAddFine.addActionListener(e -> addFineAction());
        btnDetailBack.addActionListener(e -> {
            pnlBorrowingDetail.setVisible(false);
            mainSplitPane.setDividerLocation(1.0); // Hide bottom
        });
        btnContinue.addActionListener(e -> {
            if (selectedBorrowing == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new ReturnConfirmFrm(currentUser, selectedBorrowing).setVisible(true);
            // this.dispose();
        });
    }

    private void searchAction() {
        System.out.println("Search action");
        String studentId = txtStudentId.getText().trim();
        String studentName = txtStudentName.getText().trim();

        if (mode == SearchMode.CONFIRM_BORROW) {
            searchResults = borrowingDAO.searchBorrowing(studentId, studentName, "pending");
        } else {
            searchResults = borrowingDAO.searchBorrowing(studentId, studentName, "borrowed", "overdue");
        }

        tbmResult.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int idx = 1;
        for (Borrowing b : searchResults) {
            String bDate = b.getBorrowDate() != null ? b.getBorrowDate().format(dtf) : "";
            String eDate = b.getExpectedReceiveDate() != null ? b.getExpectedReceiveDate().format(dtf) : "";
            tbmResult.addRow(new Object[]{
                idx++,
                b.getId(),
                b.getStudent() != null ? b.getStudent().getFullName() : "",
                bDate,
                eDate,
                b.getStatus()
            });
            System.out.println("Loaded borrowing's ID: " + b.getId());
            System.out.println("Books in borrowing: " + b.toString());
        }
    }

    private void clearAction() {
        txtStudentId.setText("");
        txtStudentName.setText("");
        tbmResult.setRowCount(0);
    }

    private void selectAction() {
        int selectedRow = tblResult.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedBorrowing = searchResults.get(selectedRow);

        if (mode == SearchMode.CONFIRM_BORROW) {
            new ConfirmBorrowingFrm(currentUser, selectedBorrowing).setVisible(true);
            // this.dispose();
        } else {
            // INLINE DETAIL for RETURN_BOOK
            showInlineDetail();
        }
    }

    private void showInlineDetail() {
        // Load books if needed (DAO logic needed to be called maybe? borrowing.getBooks() could be empty)
        // if (selectedBorrowing.getBooks() == null || selectedBorrowing.getBooks().isEmpty()) {
        //     selectedBorrowing.setBooks(borrowingDAO.loadBorrowedBooks(selectedBorrowing.getId()));

        lblDetailName.setText("Họ tên: " + selectedBorrowing.getStudent().getFullName());
        lblDetailId.setText("Mã SV: " + selectedBorrowing.getStudent().getId());
        
        ((TitledBorder)pnlBorrowingDetail.getBorder()).setTitle("Chi tiết phiếu mượn #" + selectedBorrowing.getId());

        tbmBorrowedBooks.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int idx = 1;
        for (BorrowedBook bb : selectedBorrowing.getBooks()) {
            String expDate = bb.getExpectedReturnDate() != null ? bb.getExpectedReturnDate().format(dtf) : "";
            // Simplified name fallback since BookItem currently lacks a Book reference in model
            String bookName = "Sách ID: " + (bb.getBookItem() != null ? bb.getBookItem().getId() : "N/A");
            int bookItemId = bb.getBookItem() != null ? bb.getBookItem().getId() : -1;
            
            // Summarize fines
            long fineCount = 0;
            if (bb.getBorrowedBookFines() != null) {
                fineCount = bb.getBorrowedBookFines().size();
            }
            String finesSum = fineCount > 0 ? fineCount + " lỗi" : "Không có";

            tbmBorrowedBooks.addRow(new Object[]{
                idx++,
                bookItemId,
                bookName,
                expDate,
                bb.getStatus(),
                finesSum
            });
        }

        pnlBorrowingDetail.setVisible(true);
        mainSplitPane.setDividerLocation(300);
    }

    private void addFineAction() {
        int selectedRow = tblBorrowedBooks.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một quyển sách để thêm lỗi phạt", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedBorrowedBook = selectedBorrowing.getBooks().get(selectedRow);

        new AddFineDlg(this, selectedBorrowedBook).setVisible(true);
        selectedBorrowing.updateBorrowedBook(selectedBorrowedBook);
        // After dialog closes, refresh the inline panel to show updated fines
        showInlineDetail(); 
    }
}
