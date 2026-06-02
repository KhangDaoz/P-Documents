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

    // @Test
    // public void testFindByBorrowingId_ReturnsList() {
    //     // Borrowing ID=1 có borrowed book ID=1 trong seed data
    //     List<BorrowedBook> result = borrowedBookDAO.findByBorrowingId(1);
    //     Assert.assertNotNull(result);
    //     Assert.assertEquals(1, result.size());
    //     Assert.assertEquals(1, result.get(0).getId());
    // }

    
}
