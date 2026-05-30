package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.dao.FineDAO;
import com.ptit.p.documents.dao.BorrowedBookDAO;
import com.ptit.p.documents.dao.BookItemDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.BorrowedBookFine;
import com.ptit.p.documents.model.Fine;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AddFineDlg extends JDialog {
    private final BorrowedBook borrowedBook;
    private final FineDAO fineDAO;
    private final BorrowedBookDAO borrowedBookDAO;

    private JComboBox<Fine> cmbFineType;
    private JTextField txtFineRate;
    private JTextArea txtNote;

    public AddFineDlg(JFrame parent, BorrowedBook borrowedBook) {
        super(parent, "Thêm lỗi phạt", true);
        this.borrowedBook = borrowedBook;
        this.fineDAO = new FineDAO();
        this.borrowedBookDAO = new BorrowedBookDAO();

        initComponents();
        loadFineTypes();
    }

    private void initComponents() {
        System.out.println("AddFine intialized");
        int bookItemId = borrowedBook.getBookItem() != null ? borrowedBook.getBookItem().getId() : -1;
        String bookTitle = resolveBookTitle();

        setTitle("Thêm lỗi phạt - Sách #" + bookItemId);
        setSize(500, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JLabel lblBookInfo = new JLabel("Sách: " + bookTitle + " (Mã: " + bookItemId + ")");
        JPanel pnlBookInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBookInfo.add(lblBookInfo);
        add(pnlBookInfo, BorderLayout.NORTH);

        JPanel pnlFineForm = new JPanel(new GridBagLayout());
        pnlFineForm.setBorder(BorderFactory.createTitledBorder("Chọn lỗi phạt"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblFineType = new JLabel("Loại lỗi:");
        cmbFineType = new JComboBox<>();
        cmbFineType.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Fine) {
                    setText(((Fine) value).getName());
                }
                return this;
            }
        });

        JLabel lblFineRate = new JLabel("Tỷ lệ phạt:");
        txtFineRate = new JTextField(15);
        txtFineRate.setEditable(false);

        JLabel lblNote = new JLabel("Ghi chú chi tiết:");
        txtNote = new JTextArea(3, 30);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        JScrollPane scrNote = new JScrollPane(txtNote);

        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlFineForm.add(lblFineType, gbc);
        gbc.gridx = 1;
        pnlFineForm.add(cmbFineType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlFineForm.add(lblFineRate, gbc);
        gbc.gridx = 1;
        pnlFineForm.add(txtFineRate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        pnlFineForm.add(lblNote, gbc);
        gbc.gridx = 1;
        pnlFineForm.add(scrNote, gbc);

        add(pnlFineForm, BorderLayout.CENTER);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm");
        JButton btnCancel = new JButton("Hủy");
        pnlActions.add(btnAdd);
        pnlActions.add(btnCancel);
        add(pnlActions, BorderLayout.SOUTH);

        cmbFineType.addActionListener(e -> updateFineRate());
        btnAdd.addActionListener(e -> addFineAction());
        btnCancel.addActionListener(e -> dispose());
    }

    private String resolveBookTitle() {
        if (borrowedBook.getBookItem() == null) {
            return "Không xác định";
        }
        BookDAO bookDAO = new BookDAO();
        Book book = bookDAO.findByID(new BookItemDAO().getBookISBN(borrowedBook.getBookItem().getId()));
        return book != null ? book.getTitle() : "Không xác định";
    }

    private void loadFineTypes() {
        List<Fine> fines = fineDAO.findAll();
        DefaultComboBoxModel<Fine> model = new DefaultComboBoxModel<>();
        for (Fine fine : fines) {
            model.addElement(fine);
        }
        cmbFineType.setModel(model);
        if (model.getSize() > 0) {
            cmbFineType.setSelectedIndex(0);
            updateFineRate();
        }
    }

    private void updateFineRate() {
        Fine fine = (Fine) cmbFineType.getSelectedItem();
        txtFineRate.setText(fine != null ? String.valueOf(fine.getFineRate()) : "");
    }

    private void addFineAction() {
        Fine fine = (Fine) cmbFineType.getSelectedItem();
        if (fine == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại lỗi phạt", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BorrowedBookFine borrowedBookFine = new BorrowedBookFine();
        borrowedBookFine.setFine(fine);
        borrowedBookFine.setFineRate(fine.getFineRate());
        borrowedBookFine.setTotalFine(fine.getFineRate());


        // Add BorrowedBookFine to BorrowedBook object in memory
        borrowedBook.addBorrowedBookFine(borrowedBookFine);

        String note = txtNote.getText().trim();
        if (!note.isEmpty()) {
            borrowedBook.setNote(note);
        }
        
        System.out.println("Đã thêm lỗi phạt: " + borrowedBookFine.getFine().getName() + " into " + borrowedBook.getBookItem().getId());
        JOptionPane.showMessageDialog(this, "Đã thêm lỗi phạt", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        
        if (borrowedBookDAO.setBorrowedBookFine(borrowedBookFine, borrowedBook)) {
            JOptionPane.showMessageDialog(this, "Đã thêm lỗi phạt", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể lưu lỗi phạt", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
