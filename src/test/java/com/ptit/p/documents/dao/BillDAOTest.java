package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Bill;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.time.LocalDate;

public class BillDAOTest {

    BillDAO billDAO = new BillDAO();

    @Test
    public void testCreateBill_Success() {
        Connection con = billDAO.getCon();
        try {
            con.setAutoCommit(false);

            Bill bill = new Bill();
            Borrowing borrowing = new Borrowing();
            borrowing.setId(1); // Borrowing ID=1 tồn tại trong seed data
            bill.setBorrowing(borrowing);
            bill.setPaymentDate(LocalDate.now());
            bill.setNote("Test bill");
            bill.setPaymentType("Tiền mặt");

            User user = new User();
            user.setId(3); // User ID=3 (librarian1) tồn tại trong seed data

            boolean result = billDAO.createBill(bill, user);
            Assert.assertTrue(result);
            Assert.assertTrue(bill.getId() > 0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { con.rollback(); con.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
