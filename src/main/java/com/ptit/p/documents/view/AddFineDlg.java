package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.dao.FineDAO;
import com.ptit.p.documents.dao.BookItemDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.BorrowedBookFine;
import com.ptit.p.documents.model.Fine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AddFineDlg extends JDialog implements ActionListener {
    private final BorrowedBook borrowedBook;
    private final FineDAO fineDAO;

    private JComboBox<Fine> cmbFineType;
    private JTextField txtFineRate;
    private JTextArea txtNote;

    public AddFineDlg(JFrame parent, BorrowedBook borrowedBook) {
        super(parent, "Thêm lỗi phạt", true);
        this.borrowedBook = borrowedBook;
        this.fineDAO = new FineDAO();

        initComponents();
        // loadFineTypes();
        List<Fine> fines = fineDAO.findAll();
        DefaultComboBoxModel<Fine> model = new DefaultComboBoxModel<>();
        for (Fine fine : fines) {
            model.addElement(fine);
        }
        cmbFineType.setModel(model);
        if (model.getSize() > 0) {
            cmbFineType.setSelectedIndex(0);
            Fine fine = (Fine) cmbFineType.getSelectedItem();
            txtFineRate.setText(fine != null ? String.valueOf(fine.getFineRate()) : ""); 
        }
    }

    private void initComponents() {
        System.out.println("AddFine intialized");
        int bookItemId = borrowedBook.getBookItem() != null ? borrowedBook.getBookItem().getId() : -1;
        String bookTitle;
        if (borrowedBook.getBookItem() == null) {
            bookTitle = "Không xác định";
        } else {
            BookDAO bookDAO = new BookDAO();
            Book book = bookDAO.findByID(new BookItemDAO().getBookISBN(borrowedBook.getBookItem().getId()));
            bookTitle = book != null ? book.getTitle() : "Không xác định";
        }

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


        cmbFineType.setActionCommand("fineType");
        cmbFineType.addActionListener(this);

        // Xử lý sự kiện khi nhấn nút "Thêm"
        btnAdd.setActionCommand("add");
        btnAdd.addActionListener(this);
        btnCancel.setActionCommand("cancel");
        btnCancel.addActionListener(this);

    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt){
        String command = evt.getActionCommand();
        if (command == null) {
            return;
        }

        switch (command) {
            case "fineType": {
                Fine fine = (Fine) cmbFineType.getSelectedItem();
                txtFineRate.setText(fine != null ? String.valueOf(fine.getFineRate()) : "");
                break;
            }
            case "add": {
                Fine fine = (Fine) cmbFineType.getSelectedItem();
                if (fine == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn loại lỗi phạt", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                BorrowedBookFine borrowedBookFine = new BorrowedBookFine();
                borrowedBookFine.setFine(fine);
                borrowedBookFine.setFineRate(fine.getFineRate());
                borrowedBookFine.setTotalFine(fine.getFineRate());

                borrowedBook.addBorrowedBookFine(borrowedBookFine);

                String note = txtNote.getText().trim();
                if (!note.isEmpty()) {
                    borrowedBook.setNote(note);
                }
                
                System.out.println("Đã thêm lỗi phạt: " + borrowedBookFine.getFine().getName() + " into " + borrowedBook.getBookItem().getId());
                JOptionPane.showMessageDialog(this, "Đã thêm lỗi phạt", "Thành công", JOptionPane.INFORMATION_MESSAGE);

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

