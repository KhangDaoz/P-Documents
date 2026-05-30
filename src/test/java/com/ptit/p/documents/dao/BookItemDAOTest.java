package com.ptit.p.documents.dao;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookItemDAOTest {

    @Spy
    @InjectMocks
    private BookItemDAO bookItemDAO; // System Under Test

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        // Isolate from actual database
        doReturn(mockConnection).when(bookItemDAO).getConnection();
    }

    @Test
    void testUpdateStatus_Success() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1); // Simulate 1 row updated

        // Act
        boolean result = bookItemDAO.updateStatus(1, "borrowed");

        // Assert
        assertTrue(result);
    }

    @Test
    void testGetBookISBN_Success() throws SQLException {
        // Arrange
        String expectedISBN = "978-123456789";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("tblBookISBN")).thenReturn(expectedISBN);

        // Act
        String actualISBN = bookItemDAO.getBookISBN(1);

        // Assert
        assertEquals(expectedISBN, actualISBN);
    }
}
