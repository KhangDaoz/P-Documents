package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BillDAO;
import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.dao.BookItemDAO;
import com.ptit.p.documents.dao.BorrowedBookDAO;
import com.ptit.p.documents.dao.BorrowingDAO;
import com.ptit.p.documents.dao.FineDAO;
import com.ptit.p.documents.model.Bill;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.BorrowedBookFine;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Fine;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.ZoneId;

public class ReturnConfirmFrm extends JFrame {
    private final User currentUser;
    private final Borrowing borrowing;

    private final BorrowingDAO borrowingDAO;
    private final BorrowedBookDAO borrowedBookDAO;
    private final BookItemDAO bookItemDAO;
    private final BillDAO billDAO;
    private final BookDAO bookDAO;
    private final FineDAO fineDAO;

    private JTextField txtStudentId;
    private JTextField txtStudentName;
    private JTextField txtEmail;
    private JTextField txtPhone;

    private JTable tblReturnBooks;
    private DefaultTableModel tbmReturnBooks;

    private JTextField txtBorrowDate;
    private JTextField txtReturnDate;
    private JTextField txtOverdueDays;
    private JTextField txtOverdueFine;
    private JTextField txtDamageFine;
    private JTextField txtTotalAmount;
    private JComboBox<String> cmbPaymentType;
    private JTextField txtNote;

    private Bill bill;
    private int totalOverdueDays;
    private double totalOverdueFine;
    private double totalDamageFine;
    private final Map<Integer, Double> borrowedBookFineTotals = new HashMap<>();

    private JPanel buildStudentInfoPanel() {
        // Student info header panel with read-only fields.
        JPanel pnlStudentInfo = new JPanel(new GridBagLayout());
        pnlStudentInfo.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblId = new JLabel("Mã SV:");
        txtStudentId = createReadOnlyField(15);
        JLabel lblName = new JLabel("Họ tên:");
        txtStudentName = createReadOnlyField(20);
        JLabel lblEmail = new JLabel("Email:");
        txtEmail = createReadOnlyField(20);
        JLabel lblPhone = new JLabel("SĐT:");
        txtPhone = createReadOnlyField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlStudentInfo.add(lblId, gbc);
        gbc.gridx = 1;
        pnlStudentInfo.add(txtStudentId, gbc);
        gbc.gridx = 2;
        pnlStudentInfo.add(lblName, gbc);
        gbc.gridx = 3;
        pnlStudentInfo.add(txtStudentName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlStudentInfo.add(lblEmail, gbc);
        gbc.gridx = 1;
        pnlStudentInfo.add(txtEmail, gbc);
        gbc.gridx = 2;
        pnlStudentInfo.add(lblPhone, gbc);
        gbc.gridx = 3;
        pnlStudentInfo.add(txtPhone, gbc);

        return pnlStudentInfo;
    }

    private JPanel buildBookDetailPanel() {
        // Table panel listing returned books and fine details.
        JPanel pnlBookDetail = new JPanel(new BorderLayout());
        pnlBookDetail.setBorder(BorderFactory.createTitledBorder("Chi tiết sách trả"));

        String[] columns = { "#", "Mã sách", "Tên sách", "Hạn trả", "Trạng thái", "Lỗi phạt", "Tiền phạt", "Ghi chú" };
        tbmReturnBooks = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblReturnBooks = new JTable(tbmReturnBooks);
        pnlBookDetail.add(new JScrollPane(tblReturnBooks), BorderLayout.CENTER);

        return pnlBookDetail;
    }

    private JPanel buildBillSummaryPanel() {
        // Bill summary panel with totals and payment inputs.
        JPanel pnlBillSummary = new JPanel(new GridBagLayout());
        pnlBillSummary.setBorder(BorderFactory.createTitledBorder("Tóm tắt hóa đơn"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblBorrowDate = new JLabel("Ngày mượn:");
        txtBorrowDate = createReadOnlyField(12);
        JLabel lblReturnDate = new JLabel("Ngày trả thực tế:");
        txtReturnDate = createReadOnlyField(12);
        JLabel lblOverdueDays = new JLabel("Số ngày quá hạn:");
        txtOverdueDays = createReadOnlyField(12);
        JLabel lblOverdueFine = new JLabel("Tiền phạt quá hạn:");
        txtOverdueFine = createReadOnlyField(12);
        JLabel lblDamageFine = new JLabel("Phí bồi thường:");
        txtDamageFine = createReadOnlyField(12);
        JLabel lblTotalAmount = new JLabel("TỔNG TIỀN:");
        txtTotalAmount = createReadOnlyField(12);
        txtTotalAmount.setFont(txtTotalAmount.getFont().deriveFont(Font.BOLD));
        txtTotalAmount.setForeground(Color.RED);

        JLabel lblPaymentType = new JLabel("Hình thức thanh toán:");
        cmbPaymentType = new JComboBox<>(new String[] { "Tiền mặt", "Chuyển khoản" });
        JLabel lblNote = new JLabel("Ghi chú:");
        txtNote = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlBillSummary.add(lblBorrowDate, gbc);
        gbc.gridx = 1;
        pnlBillSummary.add(txtBorrowDate, gbc);

        gbc.gridx = 2;
        pnlBillSummary.add(lblReturnDate, gbc);
        gbc.gridx = 3;
        pnlBillSummary.add(txtReturnDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlBillSummary.add(lblOverdueDays, gbc);
        gbc.gridx = 1;
        pnlBillSummary.add(txtOverdueDays, gbc);

        gbc.gridx = 2;
        pnlBillSummary.add(lblOverdueFine, gbc);
        gbc.gridx = 3;
        pnlBillSummary.add(txtOverdueFine, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        pnlBillSummary.add(lblDamageFine, gbc);
        gbc.gridx = 1;
        pnlBillSummary.add(txtDamageFine, gbc);

        gbc.gridx = 2;
        pnlBillSummary.add(lblTotalAmount, gbc);
        gbc.gridx = 3;
        pnlBillSummary.add(txtTotalAmount, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        pnlBillSummary.add(lblPaymentType, gbc);
        gbc.gridx = 1;
        pnlBillSummary.add(cmbPaymentType, gbc);

        gbc.gridx = 2;
        pnlBillSummary.add(lblNote, gbc);
        gbc.gridx = 3;
        pnlBillSummary.add(txtNote, gbc);

        return pnlBillSummary;
    }

    private JPanel buildActionsPanel() {
        // Action buttons for saving or canceling the return process.
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSaveBill = new JButton("Lưu hóa đơn & Xác nhận trả");
        JButton btnCancel = new JButton("Hủy");

        btnSaveBill.addActionListener(e -> saveBillAction());
        btnCancel.addActionListener(e -> dispose());

        pnlActions.add(btnSaveBill);
        pnlActions.add(btnCancel);

        return pnlActions;
    }

    public ReturnConfirmFrm(User currentUser, Borrowing borrowing) {
        this.currentUser = currentUser;
        this.borrowing = borrowing;
        this.borrowingDAO = new BorrowingDAO();
        this.borrowedBookDAO = new BorrowedBookDAO();
        this.bookItemDAO = new BookItemDAO();
        this.billDAO = new BillDAO();
        this.bookDAO = new BookDAO();
        this.fineDAO = new FineDAO();

        initComponents();
        loadBorrowingData();
    }

    private void initComponents() {
        System.out.println("ReturnConfirmFrm intialized");
        // Build and arrange all UI sections for the return confirmation screen.
        setTitle("Xác nhận trả sách - Phiếu #" + borrowing.getId());
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlStudentInfo = buildStudentInfoPanel();
        JPanel pnlBookDetail = buildBookDetailPanel();
        JPanel pnlBillSummary = buildBillSummaryPanel();
        JPanel pnlActions = buildActionsPanel();

        add(pnlStudentInfo, BorderLayout.NORTH);
        add(pnlBookDetail, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        pnlBottom.add(pnlBillSummary, BorderLayout.CENTER);
        pnlBottom.add(pnlActions, BorderLayout.SOUTH);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private JTextField createReadOnlyField(int columns) {
        // Helper to create a disabled text field used for display-only data.
        JTextField field = new JTextField(columns);
        field.setEditable(false);
        return field;
    }

    private void loadBorrowingData() {
        // Load borrowing details and refresh UI tables and totals.
        // if (borrowing.getBooks() == null || borrowing.getBooks().isEmpty()) {
        // borrowing.setBooks(borrowingDAO.loadBorrowedBooks(borrowing.getId()));
        // }

        if (borrowing.getStudent() != null) {
            txtStudentId.setText(borrowing.getStudent().getStudentId());
            txtStudentName.setText(borrowing.getStudent().getFullName());
            txtEmail.setText(borrowing.getStudent().getEmail());
            txtPhone.setText(borrowing.getStudent().getPhone());
        }

        populateBookTable();
        calculateSummary();
    }

    private void populateBookTable() {
        // Populate the return books table and compute per-book fines.
        tbmReturnBooks.setRowCount(0);
        borrowedBookFineTotals.clear();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat moneyFormat = new DecimalFormat("#,##0");
        LocalDate today = LocalDate.now();
        double overdueRatePerDay = resolveOverdueRate();

        totalOverdueDays = 0;
        totalOverdueFine = 0.0;
        totalDamageFine = 0.0;

        int idx = 1;
        for (BorrowedBook bb : borrowing.getBooks()) {
            String expDate = bb.getExpectedReturnDate() != null ? bb.getExpectedReturnDate().format(dtf) : "";
            String status = bb.getStatus() != null ? bb.getStatus() : "good";

            String title = resolveBookTitle(bb);
            int bookItemId = bb.getBookItem() != null ? bb.getBookItem().getId() : -1;

            double damageFine = sumDamageFine(bb);
            double overdueFine = 0.0;
            long overdueDays = 0;
            if (bb.getExpectedReturnDate() != null) {
                overdueDays = ChronoUnit.DAYS.between(bb.getExpectedReturnDate(), today);
                if (overdueDays > 0) {
                    overdueFine = overdueDays * overdueRatePerDay;
                } else {
                    overdueDays = 0;
                }
            }
            totalOverdueDays += (int) overdueDays;
            totalOverdueFine += overdueFine;
            totalDamageFine += damageFine;
            
            double totalBookFine = overdueFine + damageFine;
            borrowedBookFineTotals.put(bb.getId(), totalBookFine);

            String fineSummary = buildFineSummary(bb);
            String note = bb.getNote() != null ? bb.getNote() : "";

            tbmReturnBooks.addRow(new Object[] {
                    idx++,
                    bookItemId,
                    title,
                    expDate,
                    status,
                    fineSummary,
                    moneyFormat.format(totalBookFine),
                    note
            });
        }
    }

    private void calculateSummary() {
        // Compute and display the bill summary totals.
        bill = billDAO.calculateFine(borrowing);
        if (bill == null) {
            bill = new Bill();
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat moneyFormat = new DecimalFormat("#,##0");
        LocalDate today = LocalDate.now();

        txtBorrowDate.setText(borrowing.getCreatedAt().format(dtf));
        txtReturnDate.setText(today.format(dtf));

        double totalAmount = totalOverdueFine + totalDamageFine;

        bill.setOverdueDay(totalOverdueDays);
        bill.setFine(totalAmount);
        bill.setAmount(totalAmount);

        txtOverdueDays.setText(String.valueOf(totalOverdueDays));
        txtOverdueFine.setText(moneyFormat.format(totalOverdueFine));
        txtDamageFine.setText(moneyFormat.format(totalDamageFine));
        txtTotalAmount.setText(moneyFormat.format(totalAmount));
    }

    private void saveBillAction() {
        // Persist return updates and create the final bill after confirmation.
        if (!borrowing.getStatus().equals("borrowed")) {
            JOptionPane.showMessageDialog(this, "Phiếu mượn không ở trạng thái chờ trả", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận trả sách và lưu hóa đơn?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        LocalDate today = LocalDate.now();

        for (BorrowedBook bb : borrowing.getBooks()) {
            bb.setActualReturnDate(today);
            if (bb.getStatus() == null || bb.getStatus().isBlank()) {
                bb.setStatus("good");
            }

            Double totalFine = borrowedBookFineTotals.get(bb.getId());
            if (totalFine != null) {
                bb.setPrice(totalFine);
            }

            if (!borrowedBookDAO.updateReturnStatus(bb)) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật sách trả", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (bb.getBookItem() != null) {
                String itemStatus = mapBookItemStatus(bb.getStatus());
                if (!bookItemDAO.updateStatus(bb.getBookItem().getId(), itemStatus)) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái sách", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        if (!borrowingDAO.updateBorrowing(borrowing.getId(), today, "returned")) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật phiếu mượn", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        bill.setBorrowing(borrowing);
        bill.setPaymentDate(today);
        bill.setPaymentType((String) cmbPaymentType.getSelectedItem());
        bill.setNote(txtNote.getText().trim());
        bill.setAmount(totalOverdueFine + totalDamageFine);
        bill.setFine(totalOverdueFine + totalDamageFine);
        bill.setOverdueDay(totalOverdueDays);

        if (!billDAO.createBill(bill, currentUser)) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu hóa đơn", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Trả sách thành công! Hóa đơn đã được lưu.", "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private String resolveBookTitle(BorrowedBook bb) {
        // Resolve book title from its ISBN for display purposes.
        if (bb.getBookItem() == null) {
            return "Không xác định";
        }
        Book book = bookDAO.findByID(new BookItemDAO().getBookISBN(bb.getBookItem().getId()));
        return book != null ? book.getTitle() : "Không xác định";
    }

    private String buildFineSummary(BorrowedBook bb) {
        // Add overdue fine
        FineDAO fineDAO = new FineDAO();
        List<Fine> fines = fineDAO.findAll();
        Fine overdueFine = null;
        for (Fine f : fines){
            if (f.getName().equals("Overdue") || f.getName().equals("Trả trễ")){
                overdueFine = f;
                break;
            }
        }
        BorrowedBookFine overdueFineBBF = new BorrowedBookFine();
        overdueFineBBF.setFine(overdueFine);
        overdueFineBBF.setFineRate(overdueFine.getFineRate());
        overdueFineBBF.setTotalFine(overdueFine.getFineRate() * totalOverdueDays);
        bb.addBorrowedBookFine(overdueFineBBF);


        // Build a comma-separated summary of fine names for a borrowed book.
        if (bb.getBorrowedBookFines() == null || bb.getBorrowedBookFines().isEmpty()) {
            return "Không có";
        }


        StringBuilder summary = new StringBuilder();
        for (BorrowedBookFine fine : bb.getBorrowedBookFines()) {
            if (fine.getFine() != null) {
                if (summary.length() > 0) {
                    summary.append(", ");
                }
                summary.append(fine.getFine().getName());
            }
        }
        return summary.length() > 0 ? summary.toString() : "Không có";
    }

    private double sumDamageFine(BorrowedBook bb) {
        // Sum damage-related fine rates for a borrowed book.
        double total = 0.0;
        if (bb.getBorrowedBookFines() != null) {
            for (BorrowedBookFine fine : bb.getBorrowedBookFines()) {
                total += fine.getFineRate();
            }
        }
        return total;
    }

    private double resolveOverdueRate() {
        // Find the overdue fine rate from the configured fine list.
        List<Fine> fines = fineDAO.findAll();
        for (Fine fine : fines) {
            if (fine.getName() != null && isOverdueFineName(fine.getName())) {
                return fine.getFineRate();
            }
        }
        return 5000.0;
    }

    private boolean isOverdueFineName(String name) {
        // Heuristic match for fine names related to overdue penalties.
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        normalized = normalized.toLowerCase();
        return normalized.contains("tra tre") || normalized.contains("qua han") || normalized.contains("overdue");
    }

    private String mapBookItemStatus(String status) {
        // Map borrowed book status to a book item status.
        if (status == null) {
            return "good";
        }
        switch (status.toLowerCase()) {
            case "lost":
                return "lost";
            case "damaged":
                return "damaged";
            default:
                return "good";
        }
    }
}
