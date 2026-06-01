package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Student;
import com.ptit.p.documents.model.User;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.mockito.ArgumentMatchers;
public class BorrowingDAOTest {

    BorrowingDAO bd = new BorrowingDAO();

    
    private void deleteTestBorrowing(int borrowingId) {
        if (borrowingId <= 0) return;
        try {
            Connection con = bd.getCon();
            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM tblBorrowedBook WHERE tblBorrowingID = ?");
            ps.setInt(1, borrowingId);
            ps.executeUpdate();
            ps = con.prepareStatement("DELETE FROM tblBorrowing WHERE ID = ?");
            ps.setInt(1, borrowingId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private Borrowing buildSampleBorrowing(String isbn) {
        User user  = new User(3, "librarian1", "Tran Thi Thu", "librarian");
        Student sv = new Student("SV001", "Do Huy Hoang", "hoang@ptit.edu.vn", "0911111111", "Hanoi");

        LocalDate receive  = LocalDate.now().plusDays(2);
        LocalDate returnDt = LocalDate.now().plusDays(16);

        Book book = new Book();
        book.setIsbn(isbn);
        BorrowedBook bb = new BorrowedBook(book, returnDt, BigDecimal.valueOf(150000));

        Borrowing b = new Borrowing(sv, user, LocalDate.now(), receive);
        b.getBooks().add(bb);
        return b;
    }

    

    
    @Test
    public void testAddBorrowingStandard() {
        Borrowing b = buildSampleBorrowing("ISBN-CS-01");
        boolean ok = bd.addBorrowing(b);
        Assert.assertTrue(ok);
        Assert.assertTrue(b.getId() > 0);
        deleteTestBorrowing(b.getId());
    }

    
    @Test
    public void testAddBorrowingExceptionNoBookItem() {
        Borrowing b = buildSampleBorrowing("ISBN-CS-03"); 
        boolean ok = bd.addBorrowing(b);
        Assert.assertFalse(ok);
    }

    
    @Test
    public void testAddBorrowingExceptionInvalidStudent() {
        User user    = new User(3, "librarian1", "Tran Thi Thu", "librarian");
        Student svKo = new Student("SV999999", "Khong Ton Tai", "", "", "");

        LocalDate receive  = LocalDate.now().plusDays(2);
        LocalDate returnDt = LocalDate.now().plusDays(16);

        Book book = new Book();
        book.setIsbn("ISBN-CS-01");
        BorrowedBook bb = new BorrowedBook(book, returnDt, BigDecimal.valueOf(150000));

        Borrowing b = new Borrowing(svKo, user, LocalDate.now(), receive);
        b.getBooks().add(bb);

        boolean ok = bd.addBorrowing(b);
        Assert.assertFalse(ok);
    }

    

    
    @Test
    public void testSearchBorrowingException1() {
        ArrayList<Borrowing> list = bd.searchBorrowing("XXXXXXXXXX");
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    
    @Test
    public void testSearchBorrowingStandard() {
        Borrowing b = buildSampleBorrowing("ISBN-CS-01");
        boolean addOk = bd.addBorrowing(b);
        Assert.assertTrue(addOk);
        try {
            ArrayList<Borrowing> list = bd.searchBorrowing("SV001");
            Assert.assertNotNull(list);
            Assert.assertTrue(list.size() >= 1);
            Assert.assertEquals("pending", list.get(0).getStatus());
        } finally {
            deleteTestBorrowing(b.getId());
        }
    }

    
    @Test
    public void testCancelBorrowingStandard() {
        Borrowing b = buildSampleBorrowing("ISBN-CS-01");
        boolean addOk = bd.addBorrowing(b);
        Assert.assertTrue(addOk);
        try {
            boolean cancelOk = bd.cancelBorrowing(b.getId());
            Assert.assertTrue(cancelOk);

            
            ArrayList<Borrowing> list = bd.searchBorrowing("SV001");
            for (Borrowing item : list) {
                Assert.assertNotEquals(b.getId(), item.getId());
            }
        } finally {
            deleteTestBorrowing(b.getId());
        }
    }

    
    @Test
    public void testCancelBorrowingExceptionNotExist() {
        boolean ok = bd.cancelBorrowing(999999);
        Assert.assertFalse(ok);
    }

    
    @Test
    public void testCancelBorrowingExceptionNotPending() {
        Borrowing b = buildSampleBorrowing("ISBN-CS-01");
        boolean addOk = bd.addBorrowing(b);
        Assert.assertTrue(addOk);
        try {
            boolean cancel1 = bd.cancelBorrowing(b.getId());
            Assert.assertTrue(cancel1);

            boolean cancel2 = bd.cancelBorrowing(b.getId()); 
            Assert.assertFalse(cancel2);
        } finally {
            deleteTestBorrowing(b.getId());
        }
    }

    // --- Bổ sung Unit Test với Mockito theo thiết kế ---

    @Spy
    @InjectMocks
    private BorrowingDAO borrowingDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStatement;
    @Mock private ResultSet mockResultSet;

    @Before
    public void setUpMock() {
        MockitoAnnotations.openMocks(this);
        // Giả lập kết nối trả về mockConnection
        Mockito.doReturn(mockConnection).when(borrowingDAO).getCon();
    }

    @Test
    public void testSearchBorrowing_Success() throws SQLException {
        // Arrange
        Mockito.when(mockConnection.prepareStatement(ArgumentMatchers.anyString())).thenReturn(mockStatement);
        Mockito.when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true, false); // Trả về 1 bản ghi
        Mockito.when(mockResultSet.getInt("ID")).thenReturn(1);
        Mockito.when(mockResultSet.getString("status")).thenReturn("pending");
        // Giả lập thêm dữ liệu mock ResultSet (tùy thuộc mapRow)

        // Act
        List<Borrowing> results = borrowingDAO.searchBorrowing("B21DCCN001", "", "pending");

        // Assert
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("pending", results.get(0).getStatus());
    }

    @Test
    public void testUpdateBorrowing_Success() throws SQLException {
        // Arrange
        Mockito.when(mockConnection.prepareStatement(ArgumentMatchers.anyString())).thenReturn(mockStatement);
        Mockito.when(mockStatement.executeUpdate()).thenReturn(1); // Giả lập 1 dòng bị ảnh hưởng (cập nhật thành công)

        // Act
        boolean result = borrowingDAO.updateBorrowing(1, LocalDate.now(), "borrowed");

        // Assert
        Assert.assertTrue(result);
    }
}
