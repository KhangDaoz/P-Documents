package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.BorrowedBookFine;
import com.ptit.p.documents.model.Fine;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class BorrowedBookDAOTest {

    BorrowedBookDAO borrowedBookDAO = new BorrowedBookDAO();

    @Test
    public void testFindByBorrowingId_ReturnsList() {
        // Borrowing ID=1 có borrowed book ID=1 trong seed data
        List<BorrowedBook> result = borrowedBookDAO.findByBorrowingId(1);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(1, result.get(0).getId());
    }

    @Test
    public void testSetBorrowedBookFine_Success() {
        Connection con = borrowedBookDAO.getCon();
        try {
            con.setAutoCommit(false);

            BorrowedBookFine fine = new BorrowedBookFine();
            fine.setFineRate(5000);
            Fine fType = new Fine();
            fType.setId(1); // Fine ID=1 (Trả trễ) tồn tại trong seed data
            fine.setFine(fType);

            BorrowedBook bb = new BorrowedBook();
            bb.setId(1); // BorrowedBook ID=1 tồn tại trong seed data

            boolean result = borrowedBookDAO.setBorrowedBookFine(fine, bb);
            Assert.assertTrue(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { con.rollback(); con.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
