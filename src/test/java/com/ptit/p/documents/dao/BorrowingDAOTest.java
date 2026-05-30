package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Borrowing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BorrowingDAOTest {

    private BorrowingDAO borrowingDAO;

    @BeforeEach
    void setUp() {
        borrowingDAO = new BorrowingDAO();
    }

    @Test
    void testSearchBorrowingReturnsList() {
        // Since we may not know the exact state of the database, we just check that 
        // the method executes without throwing exceptions and returns a List.
        List<Borrowing> result = borrowingDAO.searchBorrowing("99999", "non_existent_book");
        
        assertNotNull(result, "The result list should not be null, even if empty.");
        assertTrue(result.isEmpty(), "The result list should be empty for a non-existent search key.");
    }
}
