package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import com.ptit.p.documents.model.Book;

public class BookDaoTest {
    private static final String EXISTING_ISBN = "978-604-1-01234-5";
    BookDAO bd = new BookDAO();

    @Test
    public void testSearchBookException(){
        String key = "xxxxxxxxxx";
        List<Book> listBook = bd.searchBook(key);
        Assert.assertNotNull(listBook);
        Assert.assertEquals(0, listBook.size());
    }

    @Test
    public void testSearchBookStandard(){
        String key = "Java";
        List<Book> listBook = bd.searchBook(key);
        Assert.assertNotNull(listBook);
        for(int i=0; i<listBook.size(); i++){
            boolean match = listBook.get(i).getTitle().toLowerCase().contains(key.toLowerCase()) || 
                            listBook.get(i).getAuthor().toLowerCase().contains(key.toLowerCase()) ||
                            listBook.get(i).getISBN().toLowerCase().contains(key.toLowerCase());
            Assert.assertTrue(match);
        }
    }

    @Test
    public void testUpdateBook(){
        Connection con = bd.getConnection();
        if(con == null) {
            Assert.fail("Không thể kết nối CSDL");
            return;
        }
        
        String newTitle = "Java Core Updated";
        double newPrice = 250000;
        String key = EXISTING_ISBN;
        
        try{
            con.setAutoCommit(false);
            List<Book> lb = bd.searchBook(key);
            if(lb.size() > 0) {
                lb.get(0).setTitle(newTitle);
                lb.get(0).setPrice(newPrice);
                bd.updateBook(lb.get(0));
                
                //test the new updated row
                lb.clear();
                lb = bd.searchBook(key);
                Assert.assertEquals(newTitle, lb.get(0).getTitle());
                Assert.assertEquals(newPrice, lb.get(0).getPrice(), 0.000001);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.rollback();
                con.setAutoCommit(true);
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testUpdateBookException() {
        Connection con = bd.getConnection();
        if(con == null) return;
        Book book = new Book();
        book.setISBN("ISBN_NOT_EXIST");
        book.setTitle("Sách Cập Nhật Ảo");
        try {
            con.setAutoCommit(false);
            boolean result = bd.updateBook(book);
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
    public void testAddBookStandard() {
        Connection con = bd.getConnection();
        if(con == null) return;
        Book book = new Book();
        book.setISBN("ISBN999");
        book.setTitle("Sách Mới Thêm");
        book.setAuthor("Tác Giả A");
        book.setGenre("Khoa Học");
        book.setPublisher("Nhà Xuất Bản A");
        book.setPublishYear(2024);
        book.setPrice(100000);
        book.setDescription("Mô tả sách mới thêm");
        book.setAvailableCopies(10);
        book.setTotalCopies(10);
        try {
            con.setAutoCommit(false);
            boolean result = bd.addBook(book);
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
    public void testAddBookException() {
        Connection con = bd.getConnection();
        if(con == null) return;
        Book book = new Book();
        book.setISBN(EXISTING_ISBN); // Đã tồn tại trong seed data
        book.setTitle("Sách Trùng ISBN");
        try {
            con.setAutoCommit(false);
            boolean result = bd.addBook(book);
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
    public void testDeleteBookStandard() {
        Connection con = bd.getConnection();
        if(con == null) return;
        String isbn = EXISTING_ISBN;
        try {
            con.setAutoCommit(false);
            boolean result = bd.deleteBook(isbn);
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
    public void testDeleteBookException() {
        Connection con = bd.getConnection();
        if(con == null) return;
        String isbn = "ISBN_NOT_EXIST";
        try {
            con.setAutoCommit(false);
            boolean result = bd.deleteBook(isbn);
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
