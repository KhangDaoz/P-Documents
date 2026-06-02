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
import java.awt.event.ActionListener;
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

public class ReturnConfirmFrm extends JFrame implements ActionListener {
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

 

    public ReturnConfirmFrm(User currentUser, Borrowing borrowing) {
        this.currentUser = currentUser;
        this.borrowing = borrowing;
        this.borrowingDAO = new BorrowingDAO();
        this.borrowedBookDAO = new BorrowedBookDAO();
        this.bookItemDAO = new BookItemDAO();
        this.billDAO = new BillDAO();
        this.bookDAO = new BookDAO();
        this.fineDAO = new FineDAO();

        // initComponents();

        System.out.println("ReturnConfirmFrm intialized");
        
        setTitle("Xác nhận trả sách - Phiếu #" + borrowing.getId());
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlStudentInfo = new JPanel(new GridBagLayout());
        pnlStudentInfo.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));
        GridBagConstraints gbcSI = new GridBagConstraints();
        gbcSI.insets = new Insets(5, 5, 5, 5);
        gbcSI.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblId = new JLabel("Mã SV:");
        txtStudentId = new JTextField(15);
        txtStudentId.setEditable(false);
        JLabel lblName = new JLabel("Họ tên:");
        txtStudentName = new JTextField(20);
        txtStudentName.setEditable(false);
        JLabel lblEmail = new JLabel("Email:");
        txtEmail = new JTextField(20);
        txtEmail.setEditable(false);
        JLabel lblPhone = new JLabel("SĐT:");
        txtPhone = new JTextField(15);
        txtPhone.setEditable(false);

        gbcSI.gridx = 0;
        gbcSI.gridy = 0;
        pnlStudentInfo.add(lblId, gbcSI);
        gbcSI.gridx = 1;
        pnlStudentInfo.add(txtStudentId, gbcSI);
        gbcSI.gridx = 2;
        pnlStudentInfo.add(lblName, gbcSI);
        gbcSI.gridx = 3;
        pnlStudentInfo.add(txtStudentName, gbcSI);

        gbcSI.gridx = 0;
        gbcSI.gridy = 1;
        pnlStudentInfo.add(lblEmail, gbcSI);
        gbcSI.gridx = 1;
        pnlStudentInfo.add(txtEmail, gbcSI);
        gbcSI.gridx = 2;
        pnlStudentInfo.add(lblPhone, gbcSI);
        gbcSI.gridx = 3;
        pnlStudentInfo.add(txtPhone, gbcSI);

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

        JPanel pnlBillSummary = new JPanel(new GridBagLayout());
        pnlBillSummary.setBorder(BorderFactory.createTitledBorder("Tóm tắt hóa đơn"));
        GridBagConstraints gbcBS = new GridBagConstraints();
        gbcBS.insets = new Insets(5, 5, 5, 5);
        gbcBS.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblBorrowDate = new JLabel("Ngày mượn:");
        txtBorrowDate = new JTextField(12);
        txtBorrowDate.setEditable(false);
        JLabel lblReturnDate = new JLabel("Ngày trả thực tế:");
        txtReturnDate = new JTextField(12);
        txtReturnDate.setEditable(false);
        JLabel lblOverdueDays = new JLabel("Số ngày quá hạn:");
        txtOverdueDays = new JTextField(12);
        txtOverdueDays.setEditable(false);
        JLabel lblOverdueFine = new JLabel("Tiền phạt quá hạn:");
        txtOverdueFine = new JTextField(12);
        txtOverdueFine.setEditable(false);
        JLabel lblDamageFine = new JLabel("Phí bồi thường:");
        txtDamageFine = new JTextField(12);
        txtDamageFine.setEditable(false);
        JLabel lblTotalAmount = new JLabel("TỔNG TIỀN:");
        txtTotalAmount = new JTextField(12);
        txtTotalAmount.setEditable(false);
        txtTotalAmount.setFont(txtTotalAmount.getFont().deriveFont(Font.BOLD));
        txtTotalAmount.setForeground(Color.RED);

        JLabel lblPaymentType = new JLabel("Hình thức thanh toán:");
        cmbPaymentType = new JComboBox<>(new String[] { "Tiền mặt", "Chuyển khoản" });
        JLabel lblNote = new JLabel("Ghi chú:");
        txtNote = new JTextField(20);

        gbcBS.gridx = 0;
        gbcBS.gridy = 0;
        pnlBillSummary.add(lblBorrowDate, gbcBS);
        gbcBS.gridx = 1;
        pnlBillSummary.add(txtBorrowDate, gbcBS);

        gbcBS.gridx = 2;
        pnlBillSummary.add(lblReturnDate, gbcBS);
        gbcBS.gridx = 3;
        pnlBillSummary.add(txtReturnDate, gbcBS);

        gbcBS.gridx = 0;
        gbcBS.gridy = 1;
        pnlBillSummary.add(lblOverdueDays, gbcBS);
        gbcBS.gridx = 1;
        pnlBillSummary.add(txtOverdueDays, gbcBS);

        gbcBS.gridx = 2;
        pnlBillSummary.add(lblOverdueFine, gbcBS);
        gbcBS.gridx = 3;
        pnlBillSummary.add(txtOverdueFine, gbcBS);

        gbcBS.gridx = 0;
        gbcBS.gridy = 2;
        pnlBillSummary.add(lblDamageFine, gbcBS);
        gbcBS.gridx = 1;
        pnlBillSummary.add(txtDamageFine, gbcBS);

        gbcBS.gridx = 2;
        pnlBillSummary.add(lblTotalAmount, gbcBS);
        gbcBS.gridx = 3;
        pnlBillSummary.add(txtTotalAmount, gbcBS);

        gbcBS.gridx = 0;
        gbcBS.gridy = 3;
        pnlBillSummary.add(lblPaymentType, gbcBS);
        gbcBS.gridx = 1;
        pnlBillSummary.add(cmbPaymentType, gbcBS);

        gbcBS.gridx = 2;
        pnlBillSummary.add(lblNote, gbcBS);
        gbcBS.gridx = 3;
        pnlBillSummary.add(txtNote, gbcBS);


        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSaveBill = new JButton("Lưu hóa đơn");
        JButton btnCancel = new JButton("Hủy");

        btnSaveBill.setActionCommand("saveBill");
        btnSaveBill.addActionListener(this);
        btnCancel.setActionCommand("cancel");
        btnCancel.addActionListener(this);

        pnlActions.add(btnSaveBill);
        pnlActions.add(btnCancel);

        add(pnlStudentInfo, BorderLayout.NORTH);
        add(pnlBookDetail, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        pnlBottom.add(pnlBillSummary, BorderLayout.CENTER);
        pnlBottom.add(pnlActions, BorderLayout.SOUTH);
        add(pnlBottom, BorderLayout.SOUTH);


        // loadBorrowingData();

        // Tải dữ liệu phiếu mượn và hiển thị thông tin sinh viên
        if (borrowing.getStudent() != null) {
            txtStudentId.setText(borrowing.getStudent().getStudentId());
            txtStudentName.setText(borrowing.getStudent().getFullName());
            txtEmail.setText(borrowing.getStudent().getEmail());
            txtPhone.setText(borrowing.getStudent().getPhone());
        }


 
        tbmReturnBooks.setRowCount(0);
        borrowedBookFineTotals.clear();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat moneyFormat = new DecimalFormat("#,##0");
        LocalDate today = LocalDate.now();
        double overdueRatePerDay = 0.0;
        List<Fine> fines = fineDAO.findAll();
        for (Fine fine : fines) {
            if (fine.getName() != null){
                
                String name = fine.getName();
                String normalized = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                normalized = normalized.toLowerCase();    
                if (normalized.contains("tra tre") || normalized.contains("qua han") || normalized.contains("overdue")) {
                overdueRatePerDay = fine.getFineRate();
                }
            }
            else {
                overdueRatePerDay = 5000.0;
            }
        }

        totalOverdueDays = 0;
        totalOverdueFine = 0.0;
        totalDamageFine = 0.0;

        int idx = 1;
        for (BorrowedBook bb : borrowing.getBooks()) {
            String expDate = bb.getExpectedReturnDate() != null ? bb.getExpectedReturnDate().format(dtf) : "";
            String status = bb.getStatus() != null ? bb.getStatus() : "good";

            String title = "";
            if (bb.getBookItem() == null) {
                title = "Không xác định";
            }
            Book book = bookDAO.findByID(new BookItemDAO().getBookISBN(bb.getBookItem().getId()));
            title = (book != null ? book.getTitle() : "Không xác định");


            int bookItemId = bb.getBookItem() != null ? bb.getBookItem().getId() : -1;
            double total = 0.0;
            if (bb.getBorrowedBookFines() != null) {
                for (BorrowedBookFine fine : bb.getBorrowedBookFines()) {
                    total += fine.getFineRate();
                }
            }
            double damageFine = total;
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


            FineDAO fineDAO = new FineDAO();
            List<Fine> sumFines = fineDAO.findAll();
            Fine sumOverdueFine = null;
            for (Fine f : sumFines){
                if (f.getName().equals("Overdue") || f.getName().equals("Trả trễ")){
                    sumOverdueFine = f;
                    break;
                }
            }
            BorrowedBookFine overdueFineBBF = new BorrowedBookFine();
            overdueFineBBF.setFine(sumOverdueFine);
            overdueFineBBF.setFineRate(sumOverdueFine.getFineRate());
            overdueFineBBF.setTotalFine(sumOverdueFine.getFineRate() * totalOverdueDays);
            bb.addBorrowedBookFine(overdueFineBBF);

            String fineSummary;
            if (bb.getBorrowedBookFines() == null || bb.getBorrowedBookFines().isEmpty()) {
                fineSummary = "Không có";
            }
            else
            {  
                StringBuilder summary = new StringBuilder();
                for (BorrowedBookFine fine : bb.getBorrowedBookFines()) {
                    if (fine.getFine() != null) {
                        if (summary.length() > 0) {
                            summary.append(", ");
                        }
                        summary.append(fine.getFine().getName());
                    }
                }
                fineSummary = summary.length() > 0 ? summary.toString() : "Không có";
            }

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



        // Tính toán và hiển thị tóm tắt hóa đơn
        bill = billDAO.calculateFine(borrowing);
        if (bill == null) {
            bill = new Bill();
        }

        DateTimeFormatter dtfOfSum = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat moneyFormatOfSum = new DecimalFormat("#,##0");
        LocalDate todayOfSum = LocalDate.now();

        txtBorrowDate.setText(borrowing.getCreatedAt().format(dtfOfSum));
        txtReturnDate.setText(todayOfSum.format(dtfOfSum));

        double totalAmount = totalOverdueFine + totalDamageFine;

        bill.setOverdueDay(totalOverdueDays);
        bill.setFine(totalAmount);
        bill.setAmount(totalAmount);

        txtOverdueDays.setText(String.valueOf(totalOverdueDays));
        txtOverdueFine.setText(moneyFormatOfSum.format(totalOverdueFine));
        txtDamageFine.setText(moneyFormatOfSum.format(totalDamageFine));
        txtTotalAmount.setText(moneyFormatOfSum.format(totalAmount));        




    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt){
        String command = evt.getActionCommand();
        if (command == null) {
            return;
        }

        switch (command) {
            case "saveBill": {
                // saveBillAction();

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
                        String bbStatus = bb.getStatus();
                        String itemStatus;
                        
                        if (bbStatus == null) {
                            itemStatus = "good";
                        } else {
                            switch (bbStatus.toLowerCase()) {
                                case "lost":
                                    itemStatus = "lost";
                                    break;
                                case "damaged":
                                    itemStatus = "damaged";
                                    break;
                                default:
                                    itemStatus = "good";
                            }
                        }


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
                break;
            }
            case "cancel": {
                dispose();
                break;
            }
            default:
                break;
        }
    }

}

