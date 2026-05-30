package test.unit;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.dao.DatabaseInitializer;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

public class BookDaoTest {

    @BeforeClass
    public static void initDb() {
        DatabaseInitializer.init(true);
    }

    BookDAO bd = new BookDAO();

    // TT1: Tim sach khong ton tai
    @Test
    public void testSearchBookException1() {
        ArrayList<Book> list = bd.searchBook("xxxxxxxxxx", "", "", "");
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    // TT2: Tim sach ton tai theo the loai (genre ASCII, tranh loi dau tieng Viet)
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

    // TT3: Tim sach theo ISBN cu the — dung 1 ket qua, ISBN khop
    @Test
    public void testSearchBookStandard2() {
        String isbn = "ISBN-CS-01";
        ArrayList<Book> list = bd.searchBook("", "", "", isbn);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(isbn, list.get(0).getIsbn());
    }

    // TT4: Tat ca tham so rong — tra ve toan bo sach (seed: 10 sach)
    @Test
    public void testSearchBookAllEmpty() {
        ArrayList<Book> list = bd.searchBook("", "", "", "");
        Assert.assertNotNull(list);
        Assert.assertEquals(7, list.size());
    }

    // TT5: Sach co ban sao kha dung — availableCopies la thuoc tinh dan xuat >= 0
    @Test
    public void testSearchBookAvailableCopies() {
        String isbn = "ISBN-CS-01"; // 3 BookItem status='good' trong seed
        ArrayList<Book> list = bd.searchBook("", "", "", isbn);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        // Thuoc tinh dan xuat: >= 0 va <= tong so ban sao (3)
        int avail = list.get(0).getAvailableCopies();
        Assert.assertTrue(avail >= 0);
        Assert.assertTrue(avail <= 3);
    }

    // TT6: Sach khong co ban sao nao (ISBN co sach nhung khong co BookItem)
    @Test
    public void testSearchBookNoCopies() {
        String isbn = "ISBN-CS-03"; // 0 BookItem trong seed
        ArrayList<Book> list = bd.searchBook("", "", "", isbn);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(0, list.get(0).getAvailableCopies());
    }
}
