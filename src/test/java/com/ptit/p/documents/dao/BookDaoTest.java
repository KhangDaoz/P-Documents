package com.ptit.p.documents.dao;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ptit.p.documents.model.Book;

public class BookDaoTest {

    @BeforeClass
    public static void initDb() {
    }

    BookDAO bd = new BookDAO();

    @Test
    public void testAddBook_NewISBN_Success() {
        Book book = new Book("ISBN-TEST-01", "Test Book", "Test Author", "Test Genre",
                "Test Publisher", 2024, 9.99, "This is a test book.", 5);
        boolean result = bd.addBook(book);
        Assert.assertTrue(result);

        bd.deleteBook("ISBN-TEST-01");
    }

    @Test
    public void testAddBook_DuplicateISBN_Failure() {
        Book book1 = new Book("ISBN-TEST-02", "Test Book 1", "Test Author", "Test Genre",
                "Test Publisher", 2024, 9.99, "This is a test book.", 5);
        Book book2 = new Book("ISBN-TEST-02", "Test Book 2", "Test Author", "Test Genre",
                "Test Publisher", 2024, 9.99, "This is another test book.", 5);

        boolean result1 = bd.addBook(book1);
        boolean result2 = bd.addBook(book2);

        Assert.assertTrue(result1);
        Assert.assertFalse(result2);

        bd.deleteBook("ISBN-TEST-02");
    }
    
    @Test
    public void testSearchBookException1() {
        ArrayList<Book> list = bd.searchBook("xxxxxxxxxx", "", "", "");
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    
    @Test
    public void testSearchBookStandard1() {
        String key = "Computer Science";
        ArrayList<Book> list = bd.searchBook("", "", key, "");
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() >= 1);
        for (int i = 0; i < list.size(); i++) {
            Assert.assertTrue(
                list.get(i).getGenre().toLowerCase().contains(key.toLowerCase())
            );
        }
    }

    
    @Test
    public void testSearchBookStandard2() {
        String isbn = "ISBN-CS-01";
        ArrayList<Book> list = bd.searchBook("", "", "", isbn);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(isbn, list.get(0).getIsbn());
    }

    
    @Test
    public void testSearchBookAllEmpty() {
        ArrayList<Book> list = bd.searchBook("", "", "", "");
        Assert.assertNotNull(list);
        Assert.assertEquals(6, list.size());
    }

    
    @Test
    public void testSearchBookAvailableCopies() {
        String isbn = "ISBN-CS-01"; 
        ArrayList<Book> list = bd.searchBook("", "", "", isbn);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        
        int avail = list.get(0).getAvailableCopies();
        Assert.assertTrue(avail >= 0);
        Assert.assertTrue(avail <= 3);
    }

    
    @Test
    public void testSearchBookNoCopies() {
        String isbn = "ISBN-CS-03"; 
        ArrayList<Book> list = bd.searchBook("", "", "", isbn);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(0, list.get(0).getAvailableCopies());
    }

    @Test
    public void testUpdateBook_Success() {
        Book book = new Book("ISBN-TEST-05", "Test Book Update", "Test Author", "Test Genre",
                "Test Publisher", 2024, 9.99, "This is a test book for update.", 5);
        bd.addBook(book);

        book.setTitle("Updated Test Book");
        boolean result = bd.updateBook(book);
        Assert.assertTrue(result);

        Book updatedBook = bd.findByID("ISBN-TEST-05");
        Assert.assertNotNull(updatedBook);
        Assert.assertEquals("Updated Test Book", updatedBook.getTitle());

        bd.deleteBook("ISBN-TEST-05");
    }

    @Test
    public void testUpdateBook_NotExistingISBN_Failure() {
        Book book = new Book("ISBN-TEST-06", "Non-Existing Book", "Test Author", "Test Genre",
                "Test Publisher", 2024, 9.99, "This book does not exist.", 5);
        boolean result = bd.updateBook(book);
        Assert.assertFalse(result);
    }

    @Test
    public void testCheckBookStatus_Success() {
        String isbn = "ISBN-CS-01"; 
        boolean result = bd.checkBookStatus(isbn, false);
        Assert.assertTrue(result);
    }

    @Test
    public void testCheckBookStatus_NoAvailableCopies() {
        String isbn = "ISBN-CS-03"; 
        boolean result = bd.checkBookStatus(isbn, false);
        Assert.assertFalse(result);
    }

    @Test
    public void testDeleteBook_Success() {
        Book book = new Book("ISBN-TEST-07", "Test Book Delete", "Test Author", "Test Genre",
                "Test Publisher", 2024, 9.99, "This is a test book for delete.", 5);
        bd.addBook(book);

        boolean result = bd.deleteBook("ISBN-TEST-07");
        Assert.assertTrue(result);

        Book deletedBook = bd.findByID("ISBN-TEST-07");
        Assert.assertNull(deletedBook);
    }

    @Test
    public void testDeleteBook_NotExistingISBN_Failure() {
        boolean result = bd.deleteBook("ISBN-TEST-08");
        Assert.assertFalse(result);
    }
}
