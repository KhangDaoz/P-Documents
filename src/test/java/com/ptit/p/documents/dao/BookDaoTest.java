package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

public class BookDaoTest {

    @BeforeClass
    public static void initDb() {
    }

    BookDAO bd = new BookDAO();

    
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
}
