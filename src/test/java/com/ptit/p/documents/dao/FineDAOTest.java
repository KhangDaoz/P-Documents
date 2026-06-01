package com.ptit.p.documents.dao;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FineDAOTest {

    @Spy
    @InjectMocks
    private FineDAO fineDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        doReturn(mockConnection).when(fineDAO).getCon();
    }

    @Test
    void testFindAll_ReturnsList() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false); // 1 result, then stop
        
        org.mockito.Mockito.lenient().when(mockResultSet.getInt(anyString())).thenReturn(1);
        org.mockito.Mockito.lenient().when(mockResultSet.getString(anyString())).thenReturn("Trả trễ");
        org.mockito.Mockito.lenient().when(mockResultSet.getDouble(anyString())).thenReturn(5000.0);

        // Act
        List<Fine> result = fineDAO.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Trả trễ", result.get(0).getName());
        assertEquals(5000.0, result.get(0).getFineRate());
    }
}
