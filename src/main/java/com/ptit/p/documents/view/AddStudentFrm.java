package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.StudentDAO;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddStudentFrm extends JFrame implements ActionListener {

    private Borrowing b;
    private JTextField txtStudentId;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JButton btnAdd;
    private JButton btnCancel;

    public AddStudentFrm(Borrowing b) {
        this.b = b;
        initComponents();
    }

    private void initComponents() {
        setTitle("Thêm sinh viên mới");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(6, 6));

        
        JPanel form = new JPanel(new GridLayout(5, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));

        form.add(new JLabel("Mã sinh viên *:"));
        txtStudentId = new JTextField();
        form.add(txtStudentId);

        form.add(new JLabel("Họ tên *:"));
        txtFullName = new JTextField();
        form.add(txtFullName);

        form.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        form.add(txtEmail);

        form.add(new JLabel("Số điện thoại:"));
        txtPhone = new JTextField();
        form.add(txtPhone);

        form.add(new JLabel("Địa chỉ:"));
        txtAddress = new JTextField();
        form.add(txtAddress);

        add(form, BorderLayout.CENTER);

        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnAdd = new JButton("Thêm");
        btnCancel = new JButton("Hủy");
        btnAdd.addActionListener(this);
        btnCancel.addActionListener(this);
        btnPanel.add(btnCancel);
        btnPanel.add(btnAdd);
        add(btnPanel, BorderLayout.SOUTH);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            if (txtStudentId.getText().trim().isEmpty() || txtFullName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Mã sinh viên và Họ tên không được để trống.",
                        "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Student s = new Student(
                    txtStudentId.getText().trim(),
                    txtFullName.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim(),
                    txtAddress.getText().trim());

            StudentDAO dao = new StudentDAO();
            boolean ok = dao.addStudent(s);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Thêm sinh viên thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                b.setStudent(s);
                new ConfirmBorrowingFrm(b).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm thất bại! Mã sinh viên \"" + s.getStudentId() + "\" có thể đã tồn tại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnCancel) {
            this.dispose();
        }
    }
}
