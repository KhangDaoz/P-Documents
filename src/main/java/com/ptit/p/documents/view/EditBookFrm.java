package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditBookFrm extends JFrame implements ActionListener {
    private JLabel lblISBN;
    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JTextField txtGenre;
    private JTextField txtPublisher;
    private JTextField txtPublishYear;
    private JTextField txtPrice;
    private JTextField txtDescription;
    private JLabel lblCopies;
    private JButton btnSave;
    private JButton btnReset;
    private JButton btnBack;
    private User currentUser;
    private Book currentBook;

    public EditBookFrm(User user, Book book) {
        this.currentUser = user;
        this.currentBook = book;
        initComponents();
        
        lblISBN.setText(currentBook.getISBN());
        txtTitle.setText(currentBook.getTitle());
        txtAuthor.setText(currentBook.getAuthor());
        txtGenre.setText(currentBook.getGenre());
        txtPublisher.setText(currentBook.getPublisher());
        txtPublishYear.setText(String.valueOf(currentBook.getPublishYear()));
        txtPrice.setText(String.valueOf(currentBook.getPrice()));
        txtDescription.setText(currentBook.getDescription());
        lblCopies.setText(String.valueOf(currentBook.getTotalCopies()));
    }

    private void initComponents() {
        setTitle("Sửa thông tin sách");
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

        
        JLabel lblFormTitle = new JLabel("SỬA THÔNG TIN SÁCH", SwingConstants.CENTER);
        lblFormTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblFormTitle.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblFormTitle, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("ISBN:"), gbc);
        lblISBN = new JLabel();
        lblISBN.setFont(new Font("Arial", Font.BOLD, 13));
        lblISBN.setForeground(Color.BLACK);
        gbc.gridx = 1;
        mainPanel.add(lblISBN, gbc);

        
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
        mainPanel.add(new JLabel("Thể loại:"), gbc);
        txtGenre = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtGenre, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Nhà xuất bản:"), gbc);
        txtPublisher = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtPublisher, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Năm xuất bản:"), gbc);
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
        mainPanel.add(new JLabel("Mô tả:"), gbc);
        txtDescription = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(txtDescription, gbc);

        
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Số lượng:"), gbc);
        lblCopies = new JLabel();
        lblCopies.setFont(new Font("Arial", Font.BOLD, 13));
        lblCopies.setForeground(Color.BLACK);
        gbc.gridx = 1;
        mainPanel.add(lblCopies, gbc);

        
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
            
            
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            String genre = txtGenre.getText().trim();
            String publisher = txtPublisher.getText().trim();
            String publishYearStr = txtPublishYear.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String description = txtDescription.getText().trim();

            if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || publisher.isEmpty()
                    || publishYearStr.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ các trường bắt buộc (*): tên sách, tác giả, thể loại, nhà xuất bản, năm xuất bản, giá bìa, mô tả!",
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

            
            int publishYear;
            try {
                publishYear = Integer.parseInt(publishYearStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Năm xuất bản phải là số nguyên hợp lệ!",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            currentBook.setTitle(title);
            currentBook.setAuthor(author);
            currentBook.setGenre(genre);
            currentBook.setPublisher(publisher);
            currentBook.setPublishYear(publishYear);
            currentBook.setPrice(price);
            currentBook.setDescription(description);

            
            BookDAO bookDAO = new BookDAO();
            boolean success = bookDAO.updateBook(currentBook);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thông tin sách thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                new ManagerHomeFrm(currentUser).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thất bại! Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnReset) {
            
            lblISBN.setText(currentBook.getISBN());
            txtTitle.setText(currentBook.getTitle());
            txtAuthor.setText(currentBook.getAuthor());
            txtGenre.setText(currentBook.getGenre());
            txtPublisher.setText(currentBook.getPublisher());
            txtPublishYear.setText(String.valueOf(currentBook.getPublishYear()));
            txtPrice.setText(String.valueOf(currentBook.getPrice()));
            txtDescription.setText(currentBook.getDescription());
            lblCopies.setText(String.valueOf(currentBook.getTotalCopies()));
        } else if (e.getSource() == btnBack) {
            new BookManageFrm(currentUser).setVisible(true);
            this.dispose();
        }
    }
    
}
