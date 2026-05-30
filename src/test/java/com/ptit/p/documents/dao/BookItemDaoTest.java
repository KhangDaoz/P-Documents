package com.ptit.p.documents.dao;

import java.sql.Connection;
import org.junit.Assert;
import org.junit.Test;
import com.ptit.p.documents.model.BookItem;

public class BookItemDaoTest {
    private static final String EXISTING_ISBN = "978-604-1-01234-5";
    BookItemDAO bid = new BookItemDAO();

    @Test
    public void testAddBookItemStandard(){
        Connection con = bid.getConnection();
        if(con == null) {
            Assert.fail("Không thể kết nối CSDL");
            return;
        }
        
        BookItem item = new BookItem();
        item.setBookISBN(EXISTING_ISBN); // Dùng ISBN có sẵn trong seed data
        item.setStatus("good");
        
        try {
            con.setAutoCommit(false);
            boolean result = bid.addBookItem(item);
            Assert.assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Ném ra ngoại lệ khi thêm BookItem");
        } finally {
            try {
                con.rollback(); // Rollback để không lưu vào CSDL thật
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testAddBookItemException(){
        Connection con = bid.getConnection();
        if(con == null) return;
        
        BookItem item = new BookItem();
        item.setBookISBN("ISBN_NOT_EXIST"); // Mã sách không có thật
        item.setStatus("good");
        
        try {
            con.setAutoCommit(false);
            boolean result = bid.addBookItem(item);
            // Sẽ trả về false vì vi phạm khóa ngoại (Foreign key)
            Assert.assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testDeleteBookItemStandard() {
        Connection con = bid.getConnection();
        if(con == null) return;
        String isbn = EXISTING_ISBN;
        try {
            con.setAutoCommit(false);
            boolean result = bid.deleteBookItem(isbn);
            Assert.assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testDeleteBookItemException() {
        Connection con = bid.getConnection();
        if(con == null) return;
        String isbn = "ISBN_NOT_EXIST";
        try {
            con.setAutoCommit(false);
            boolean result = bid.deleteBookItem(isbn);
            Assert.assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
