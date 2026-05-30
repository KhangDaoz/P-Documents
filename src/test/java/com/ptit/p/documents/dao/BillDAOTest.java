package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Bill;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.User;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillDAOTest {

    @Spy
    @InjectMocks
    private BillDAO billDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        doReturn(mockConnection).when(billDAO).getConnection();
    }

    @Test
    void testCreateBill_Success() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(99); // Mocked generated ID

        Bill bill = new Bill();
        Borrowing borrowing = new Borrowing();
        borrowing.setId(10);
        bill.setBorrowing(borrowing);
        
        User user = new User();
        user.setId(2);

        // Act
        boolean result = billDAO.createBill(bill, user);

        // Assert
        assertTrue(result);
        assertTrue(bill.getId() == 99);
    }
}
