package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowingDAO;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.User;
import com.ptit.p.documents.view.AcceptBorrowingFrm;
import com.ptit.p.documents.view.SearchMode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;

public class SearchBorrowingFrm extends JFrame implements ActionListener {
    private User currentUser;
    private SearchMode mode;
    private BorrowingDAO borrowingDAO;

    private JTextField txtStudentId;
    private JTextField txtStudentName;
    private JButton btnSearch;
    private JButton btnClear;
    private JTable tblResult;
    private DefaultTableModel tbmResult;
    // private JButton btnSelect;
    private JButton btnBack;

    private List<Borrowing> searchResults;

    
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
        String title = "Tìm kiếm phiếu mượn";
        if (mode == SearchMode.CONFIRM_BORROW) title = "Xử lý nhận sách";
        if (mode == SearchMode.RETURN_BOOK) title = "Xử lý trả sách";
        if (mode == SearchMode.CANCEL_BORROW) title = "Hủy đặt sách";
        setTitle(title);
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
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

        
        String[] columns = {"#", "Mã PM", "Tên sinh viên", "Ngày tạo", "Ngày hẹn nhận", "Trạng thái"};
        tbmResult = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblResult = new JTable(tbmResult);
        tblResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrResult = new JScrollPane(tblResult);

        
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        String btnSelectText = "Chọn phiếu";
        if (mode == SearchMode.RETURN_BOOK) btnSelectText = "Chọn phiếu trả";
        if (mode == SearchMode.CANCEL_BORROW) btnSelectText = "Chọn phiếu hủy";
        // btnSelect = new JButton(btnSelectText);
        
        btnBack = new JButton("Quay lại");
        // pnlActions.add(btnSelect);
        pnlActions.add(btnBack);

        
        JPanel pnlTopWrap = new JPanel(new BorderLayout());
        pnlTopWrap.add(scrResult, BorderLayout.CENTER);
        pnlTopWrap.add(pnlActions, BorderLayout.SOUTH);

        
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

        // Xử lý sự kiện tìm kiếm
        btnSearch.setActionCommand("search");
        btnSearch.addActionListener(this);

        // Xử lý sự kiện xóa
        btnClear.setActionCommand("clear");
        btnClear.addActionListener(this);

        btnBack.setActionCommand("back");
        btnBack.addActionListener(this);
        
        // btnSelect.addActionListener(e -> selectAction());
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    
                    
                    int selectedRow = tblResult.getSelectedRow();
                    if (selectedRow < 0) {
                        JOptionPane.showMessageDialog(SearchBorrowingFrm.this,
                                "Vui lòng chọn một phiếu mượn", "Thông báo",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    selectedBorrowing = searchResults.get(selectedRow);

                    if (mode == SearchMode.CONFIRM_BORROW) {
                        new AcceptBorrowingFrm(currentUser, selectedBorrowing).setVisible(true);
                    } else if (mode == SearchMode.CANCEL_BORROW) {
                        new ConfirmCancelFrm(selectedBorrowing, currentUser).setVisible(true);
                        SearchBorrowingFrm.this.dispose();
                    } else {
                        lblDetailName.setText("Họ tên: " + selectedBorrowing.getStudent().getFullName());
                        lblDetailId.setText("Mã SV: " + selectedBorrowing.getStudent().getStudentId());

                        ((TitledBorder) pnlBorrowingDetail.getBorder())
                                .setTitle("Chi tiết phiếu mượn #" + selectedBorrowing.getId());

                        tbmBorrowedBooks.setRowCount(0);
                        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                        int idx = 1;
                        for (BorrowedBook bb : selectedBorrowing.getBooks()) {
                            String expDate = bb.getExpectedReturnDate() != null ?
                                    bb.getExpectedReturnDate().format(sdf) : "";
                            String bookName = "Sách ID: " + (bb.getBookItem() != null ?
                                    bb.getBookItem().getId() : "N/A");
                            int bookItemId = bb.getBookItem() != null ? bb.getBookItem().getId() : -1;

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
                }
            }
        });

        btnAddFine.setActionCommand("addFine");
        btnAddFine.addActionListener(this);

        btnDetailBack.setActionCommand("detailBack");
        btnDetailBack.addActionListener(this);

        btnContinue.setActionCommand("continue");
        btnContinue.addActionListener(this);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        String command = evt.getActionCommand();
        if (command == null) {
            return;
        }

        switch (command) {
            case "search": {
                String studentId = txtStudentId.getText().trim();
                String studentName = txtStudentName.getText().trim();

                if (mode == SearchMode.CONFIRM_BORROW || mode == SearchMode.CANCEL_BORROW) {
                    searchResults = borrowingDAO.searchBorrowing(studentId, studentName, "pending");
                } else {
                    searchResults = borrowingDAO.searchBorrowing(studentId, studentName, "borrowed", "overdue");
                }

                tbmResult.setRowCount(0);
                DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                int idx = 1;

                if(searchResults.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                else
                {
                    for (Borrowing b : searchResults) {
                        String bDate = b.getCreatedAt() != null ? b.getCreatedAt().format(sdf) : "";
                        String eDate = b.getExpectedReceiveDate() != null ? b.getExpectedReceiveDate().format(sdf) : "";
                        tbmResult.addRow(new Object[]{
                            idx++,
                            b.getId(),
                            b.getStudent() != null ? b.getStudent().getFullName() : "",
                            bDate,
                            eDate,
                            b.getStatus()
                        });
                    }
                }
                break;
            }
            case "clear": {
                txtStudentId.setText("");
                txtStudentName.setText("");
                tbmResult.setRowCount(0);
                break;
            }
            case "back": {
                dispose();
                break;
            }
            case "addFine": {
                int selectedRow = tblBorrowedBooks.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(SearchBorrowingFrm.this,
                            "Vui lòng chọn một quyển sách để thêm lỗi phạt", "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                selectedBorrowedBook = selectedBorrowing.getBooks().get(selectedRow);

                new AddFineDlg(SearchBorrowingFrm.this, selectedBorrowedBook).setVisible(true);
                selectedBorrowing.updateBorrowedBook(selectedBorrowedBook);

                lblDetailName.setText("Họ tên: " + selectedBorrowing.getStudent().getFullName());
                lblDetailId.setText("Mã SV: " + selectedBorrowing.getStudent().getStudentId());

                ((TitledBorder) pnlBorrowingDetail.getBorder())
                        .setTitle("Chi tiết phiếu mượn #" + selectedBorrowing.getId());

                tbmBorrowedBooks.setRowCount(0);
                DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                int idx = 1;
                for (BorrowedBook bb : selectedBorrowing.getBooks()) {
                    String expDate = bb.getExpectedReturnDate() != null ?
                            bb.getExpectedReturnDate().format(sdf) : "";
                    String bookName = "Sách ID: " + (bb.getBookItem() != null ?
                            bb.getBookItem().getId() : "N/A");
                    int bookItemId = bb.getBookItem() != null ? bb.getBookItem().getId() : -1;

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
                break;
            }
            case "detailBack": {
                pnlBorrowingDetail.setVisible(false);
                mainSplitPane.setDividerLocation(1.0); 
                break;
            }
            case "continue": {
                if (selectedBorrowing == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                new ReturnConfirmFrm(currentUser, selectedBorrowing).setVisible(true);
                this.dispose();
                break;
            }
            default:
                break;
        }
    }

}
