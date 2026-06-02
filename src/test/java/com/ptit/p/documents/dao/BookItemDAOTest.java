package com.ptit.p.documents.dao;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BookItem;

public class BookItemDAOTest {
  BookItemDAO bid= new BookItemDAO();
  BookDAO bd = new BookDAO();

  @Test
  public void testAddBookItem_ExistingISBN_Success() {
      Book book = new Book("ISBN-TEST-03", "Test Book Item", "Test Author", "Test Genre",
              "Test Publisher", 2024, 9.99, "This is a test book item.", 5);
      bd.addBook(book);
      BookItem item = new BookItem(5001, "good", "ISBN-TEST-03");
      boolean result = bid.addBookItem(item);
      Assert.assertTrue(result);

      bid.deleteBookItem("ISBN-TEST-03");
      bd.deleteBook("ISBN-TEST-03");
  }

  @Test
  public void testAddBookItem_NotExistingISBN_Failure() {
      BookItem item = new BookItem(5002, "good", "ISBN-TEST-04");
      boolean result = bid.addBookItem(item);
      Assert.assertFalse(result);
  }

  @Test
  public void testDeleteBookItem_ExistingISBN_Success() {
      Book book = new Book("ISBN-TEST-05", "Test Book Item Delete", "Test Author", "Test Genre",
              "Test Publisher", 2024, 9.99, "This is a test book item for deletion.", 5);
      bd.addBook(book);
      BookItem item = new BookItem(5003, "good", "ISBN-TEST-05");
      bid.addBookItem(item);

      boolean result = bid.deleteBookItem("ISBN-TEST-05");
      Assert.assertTrue(result);

      bd.deleteBook("ISBN-TEST-05");
  }

  @Test
  public void testDeleteBookItem_NotExistingISBN_Failure() {
      boolean result = bid.deleteBookItem("ISBN-TEST-06");
      Assert.assertFalse(result);
  }
}
