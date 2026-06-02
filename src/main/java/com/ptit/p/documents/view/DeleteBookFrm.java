package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.dao.BookItemDAOTest;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteBookFrm extends JFrame implements ActionListener {
    private JButton btnConfirmDelete;
    private JButton btnCancel;
    private User currentUser;
    private Book currentBook;

    public DeleteBookFrm(User user, Book book) {
        this.currentUser = user;
        this.currentBook = book;
        initComponents();
    }

    private void initComponents() {
        setTitle("Xác nhận xóa sách");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        
        JLabel lblTitle = new JLabel("XÁC NHẬN XÓA SÁCH", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        
        JLabel lblWarning = new JLabel("Bạn có chắc muốn xóa sách sau đây?", SwingConstants.CENTER);
        lblWarning.setFont(new Font("Arial", Font.ITALIC, 13));
        lblWarning.setForeground(Color.BLACK);
        gbc.gridy = 1;
        mainPanel.add(lblWarning, gbc);

        
        gbc.gridy = 2;
        

        gbc.gridwidth = 1;
        int row = 3;

        
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lblIsbnLabel = new JLabel("ISBN:");
        lblIsbnLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblIsbnLabel, gbc);

        gbc.gridx = 1;
        JLabel lblIsbnValue = new JLabel(currentBook.getISBN() != null ? currentBook.getISBN() : "");
        lblIsbnValue.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(lblIsbnValue, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lblTitleLabel = new JLabel("Tên sách:");
        lblTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblTitleLabel, gbc);

        gbc.gridx = 1;
        JLabel lblTitleValue = new JLabel(currentBook.getTitle() != null ? currentBook.getTitle() : "");
        lblTitleValue.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(lblTitleValue, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lblAuthorLabel = new JLabel("Tác giả:");
        lblAuthorLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblAuthorLabel, gbc);

        gbc.gridx = 1;
        JLabel lblAuthorValue = new JLabel(currentBook.getAuthor() != null ? currentBook.getAuthor() : "");
        lblAuthorValue.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(lblAuthorValue, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lblGenreLabel = new JLabel("Thể loại:");
        lblGenreLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblGenreLabel, gbc);

        gbc.gridx = 1;
        JLabel lblGenreValue = new JLabel(currentBook.getGenre() != null ? currentBook.getGenre() : "");
        lblGenreValue.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(lblGenreValue, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lblPublisherLabel = new JLabel("Nhà xuất bản:");
        lblPublisherLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblPublisherLabel, gbc);

        gbc.gridx = 1;
        JLabel lblPublisherValue = new JLabel(currentBook.getPublisher() != null ? currentBook.getPublisher() : "");
        lblPublisherValue.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(lblPublisherValue, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lblPublishYearLabel = new JLabel("Năm xuất bản:");
        lblPublishYearLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblPublishYearLabel, gbc);

        gbc.gridx = 1;
        JLabel lblPublishYearValue = new JLabel(String.valueOf(currentBook.getPublishYear()));
        lblPublishYearValue.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(lblPublishYearValue, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lblPriceLabel = new JLabel("Giá bìa:");
        lblPriceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblPriceLabel, gbc);

        gbc.gridx = 1;
        JLabel lblPriceValue = new JLabel(String.valueOf(currentBook.getPrice()));
        lblPriceValue.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(lblPriceValue, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lblCopiesLabel = new JLabel("Số lượng:");
        lblCopiesLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblCopiesLabel, gbc);

        gbc.gridx = 1;
        JLabel lblCopiesValue = new JLabel(String.valueOf(currentBook.getTotalCopies()));
        lblCopiesValue.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(lblCopiesValue, gbc);
        row++;

        
        gbc.gridy = row;
        gbc.gridwidth = 2;
        

        
        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnConfirmDelete = new JButton("Xác nhận xóa");
        btnCancel = new JButton("Hủy");
        btnConfirmDelete.setPreferredSize(new Dimension(140, 35));
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnConfirmDelete.setBackground(UIManager.getColor("Button.background"));
        btnConfirmDelete.setForeground(Color.BLACK);
        btnConfirmDelete.setFont(new Font("Arial", Font.PLAIN, 14));
        buttonPanel.add(btnConfirmDelete);
        buttonPanel.add(btnCancel);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        btnConfirmDelete.addActionListener(this);
        btnCancel.addActionListener(this);

        add(mainPanel);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirmDelete) {
            String isbn = currentBook.getISBN();

            BookDAO bookDAO = new BookDAO();

            if (bookDAO.checkBookStatus(isbn, true)) {
                JOptionPane.showMessageDialog(this,
                        "Sách đã có lịch sử mượn nên không thể xóa cứng theo ràng buộc cơ sở dữ liệu.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            BookItemDAOTest bookItemDAO = new BookItemDAOTest();
            bookItemDAO.deleteBookItem(isbn);

            
            boolean success = bookDAO.deleteBook(isbn);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Xóa sách thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                new ManagerHomeFrm(currentUser).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa sách thất bại! Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnCancel) {
            new ManagerHomeFrm(currentUser).setVisible(true);
            this.dispose();
        }
    }
}
