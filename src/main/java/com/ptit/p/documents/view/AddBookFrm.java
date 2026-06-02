package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.dao.BookItemDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddBookFrm extends JFrame implements ActionListener {
    private JTextField txtISBN;
    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JTextField txtGenre;
    private JTextField txtPublisher;
    private JTextField txtPublishYear;
    private JTextField txtPrice;
    private JTextField txtDescription;
    private JTextField txtCopies;
    private JButton btnSave;
    private JButton btnReset;
    private JButton btnBack;
    private User currentUser;
    private Book b;

    public AddBookFrm(User user) {
        this(user, null);
    }

    public AddBookFrm(User user, Book b) {
        this.currentUser = user;
        this.b = b;
        initComponents();
        if (this.b != null) {
            txtISBN.setText(b.getISBN() != null ? b.getISBN() : "");
            txtTitle.setText(b.getTitle() != null ? b.getTitle() : "");
            txtAuthor.setText(b.getAuthor() != null ? b.getAuthor() : "");
            txtGenre.setText(b.getGenre() != null ? b.getGenre() : "");
            txtPublisher.setText(b.getPublisher() != null ? b.getPublisher() : "");
            txtPublishYear.setText(b.getPublishYear() > 0 ? String.valueOf(b.getPublishYear()) : "");
            txtPrice.setText(b.getPrice() > 0 ? String.valueOf(b.getPrice()) : "");
            txtDescription.setText(b.getDescription() != null ? b.getDescription() : "");
            txtCopies.setText(String.valueOf(b.getAvailableCopies()));
        }
    }

    private void initComponents() {
        setTitle("Thêm sách mới");
        setSize(550, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        
        JLabel lblTitle = new JLabel("THÊM SÁCH MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("ISBN (*):"), gbc);
        txtISBN = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtISBN, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Tên sách (*):"), gbc);
        txtTitle = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtTitle, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Tác giả (*):"), gbc);
        txtAuthor = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtAuthor, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Thể loại (*):"), gbc);
        txtGenre = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtGenre, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Nhà xuất bản (*):"), gbc);
        txtPublisher = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtPublisher, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Năm xuất bản (*):"), gbc);
        txtPublishYear = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtPublishYear, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Giá bìa (*):"), gbc);
        txtPrice = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtPrice, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Mô tả (*):"), gbc);
        txtDescription = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtDescription, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Số lượng:"), gbc);
        txtCopies = new JTextField(25);
        txtCopies.setText("0");
        gbc.gridx = 1;
        mainPanel.add(txtCopies, gbc);

        
        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnSave = new JButton("Lưu");
        btnReset = new JButton("Nhập lại");
        btnBack = new JButton("Quay lại");
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnReset.setPreferredSize(new Dimension(100, 35));
        btnBack.setPreferredSize(new Dimension(100, 35));
        btnSave.setBackground(UIManager.getColor("Button.background"));
        btnSave.setForeground(Color.BLACK);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnBack);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        btnSave.addActionListener(this);
        btnReset.addActionListener(this);
        btnBack.addActionListener(this);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSave) {
            
            String isbn = txtISBN.getText().trim();
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            String genre = txtGenre.getText().trim();
            String publisher = txtPublisher.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String copiesStr = txtCopies.getText().trim();
            String publishYearStr = txtPublishYear.getText().trim();
            String description = txtDescription.getText().trim();

            if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || genre.isEmpty()
                    || publisher.isEmpty() || priceStr.isEmpty() || publishYearStr.isEmpty()
                    || description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ các trường bắt buộc (*): ISBN, tên sách, tác giả, thể loại, nhà xuất bản, năm xuất bản, giá bìa, mô tả!",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Giá bìa phải là số dương!",
                            "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Giá bìa phải là số hợp lệ!",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            int copies;
            try {
                copies = Integer.parseInt(copiesStr);
                if (copies < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Số lượng bản copy không được âm!",
                            "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng bản copy phải là số nguyên hợp lệ!",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            int publishYear;
            try {
                publishYear = Integer.parseInt(publishYearStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Năm xuất bản phải là số nguyên hợp lệ!",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            Book book = new Book();
            book.setISBN(isbn);
            book.setTitle(title);
            book.setAuthor(author);
            book.setGenre(genre);
            book.setPublisher(publisher);
            book.setPublishYear(publishYear);
            book.setPrice(price);
            book.setDescription(description);
            book.setAvailableCopies(copies);
            book.setTotalCopies(copies);

            
            BookDAO bookDAO = new BookDAO();
            boolean success = bookDAO.addBook(book);

            if (!success) {
                JOptionPane.showMessageDialog(this,
                        "ISBN đã tồn tại trong hệ thống! Vui lòng kiểm tra lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            
            if (copies > 0) {
                BookItemDAO bookItemDAO = new BookItemDAO();
                for (int i = 0; i < copies; i++) {
                    BookItem item = new BookItem();
                    item.setStatus("good");
                    item.setBookISBN(isbn);
                    bookItemDAO.addBookItem(item);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Thêm sách thành công!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            
            new ManagerHomeFrm(currentUser).setVisible(true);
            this.dispose();

        } else if (e.getSource() == btnReset) {
            txtISBN.setText("");
            txtTitle.setText("");
            txtAuthor.setText("");
            txtGenre.setText("");
            txtPublisher.setText("");
            txtPublishYear.setText("");
            txtPrice.setText("");
            txtDescription.setText("");
            txtCopies.setText("0");
            txtISBN.requestFocus();

        } else if (e.getSource() == btnBack) {
            new BookManageFrm(currentUser).setVisible(true);
            this.dispose();
        }
    }

    
    

}
