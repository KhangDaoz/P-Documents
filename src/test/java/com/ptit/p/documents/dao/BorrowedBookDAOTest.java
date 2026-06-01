package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.BorrowedBookFine;
import com.ptit.p.documents.model.Fine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowedBookDAOTest {

    @Spy
    @InjectMocks
    private BorrowedBookDAO borrowedBookDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        doReturn(mockConnection).when(borrowedBookDAO).getCon();
    }

    @Test
    void testFindByBorrowingId_ReturnsList() throws SQLException {
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false); 
        
        
        org.mockito.Mockito.lenient().when(mockResultSet.getInt(anyString())).thenReturn(1);
        org.mockito.Mockito.lenient().when(mockResultSet.getString(anyString())).thenReturn("borrowed");
        org.mockito.Mockito.lenient().when(mockResultSet.getDouble(anyString())).thenReturn(0.0);
        org.mockito.Mockito.lenient().when(mockResultSet.getDate(anyString())).thenReturn(new java.sql.Date(System.currentTimeMillis()));

        
        List<BorrowedBook> result = borrowedBookDAO.findByBorrowingId(10);

        
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void testSetBorrowedBookFine_Success() throws SQLException {
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        BorrowedBookFine fine = new BorrowedBookFine();
        fine.setFineRate(5000);
        Fine fType = new Fine();
        fType.setId(1);
        fine.setFine(fType);
        
        BorrowedBook bb = new BorrowedBook();
        bb.setId(10);

        
        boolean result = borrowedBookDAO.setBorrowedBookFine(fine, bb);

        
        assertTrue(result);
    }
}
