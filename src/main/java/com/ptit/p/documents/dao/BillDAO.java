package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Bill;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.BorrowedBookFine;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Fine;
import java.text.Normalizer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.ptit.p.documents.model.User;
import com.ptit.p.documents.model.Borrowing;

public class BillDAO extends DAO {

    public boolean createBill(Bill bill, User user) {
        String sql = "INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setDate(1, bill.getPaymentDate() != null ? java.sql.Date.valueOf(bill.getPaymentDate()) : null);
            statement.setString(2, bill.getNote());
            statement.setString(3, bill.getPaymentType());
            statement.setInt(4, bill.getBorrowing().getId());
            statement.setInt(5, user.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (java.sql.ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bill.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Saving bill failed, no ID obtained.");
                }
            }
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Bill calculateFine(Borrowing borrowing) {
        if (borrowing == null) {
            return null;
        }

        Bill bill = new Bill();
        bill.setBorrowing(borrowing);
        bill.setPaymentDate(LocalDate.now());

        double overdueRatePerDay = resolveOverdueRate();
        int totalOverdueDays = 0;
        double totalFine = 0.0;

        List<BorrowedBook> borrowedBooks = borrowing.getBorrowedBooks();
        if (borrowedBooks != null) {
            for (BorrowedBook bb : borrowedBooks) {
                // Calculate overdue fine
                if (bb.getExpectedReturnDate() != null) {
                    long days = ChronoUnit.DAYS.between(bb.getExpectedReturnDate(), LocalDate.now());
                    if (days > 0) {
                        totalOverdueDays += (int) days;
                        totalFine += days * overdueRatePerDay;
                    }
                }
                // Add damage fines
                if (bb.getBorrowedBookFines() != null) {
                    for (BorrowedBookFine fine : bb.getBorrowedBookFines()) {
                        totalFine += fine.getFineRate();
                    }
                }
            }
        }

        bill.setOverdueDay(totalOverdueDays);
        bill.setFine(totalFine);
        bill.setAmount(totalFine);
        return bill;
    }

    private double resolveOverdueRate() {
        FineDAO fineDAO = new FineDAO();
        List<Fine> fines = fineDAO.findAll();
        for (Fine fine : fines) {
            if (fine.getName() != null && isOverdueFineName(fine.getName())) {
                return fine.getFineRate();
            }
        }
        return 5000.0;
    }

    private boolean isOverdueFineName(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        normalized = normalized.toLowerCase();
        return normalized.contains("tra tre") || normalized.contains("qua han") || normalized.contains("overdue");
    }
    // public Bill findById(int id, Borrowing borrowing) {
    // String sql = "SELECT * FROM tblBill WHERE id = ?";
    // try (Connection connection = getConnection();
    // PreparedStatement statement = connection.prepareStatement(sql)) {

    // statement.setInt(1, id);
    // java.sql.ResultSet resultSet = statement.executeQuery();
    // if (resultSet.next()) {
    // Bill bill = new Bill();
    // bill.setId(resultSet.getInt("id"));
    // bill.setPaymentDate(resultSet.getDate("paymentDate").toLocalDate());
    // bill.setNote(resultSet.getString("note"));
    // bill.setPaymentType(resultSet.getString("paymentType"));
    // bill.setBorrowing(borrowing);
    // return bill;
    // }
    // } catch (SQLException ex) {
    // ex.printStackTrace();
    // }
    // return null;
    // }
}
