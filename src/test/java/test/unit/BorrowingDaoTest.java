package test.unit;

import com.ptit.p.documents.dao.BorrowingDAO;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BorrowingDaoTest {

    BorrowingDAO bd = new BorrowingDAO();

    // Xoa du lieu test de tranh anh huong den DB sau khi test
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

    // Helper: tao phieu muon mau voi ISBN cho truoc
    private Borrowing buildSampleBorrowing(String isbn) {
        User user  = new User(1, "admin", "Nguyen Van Admin", "admin");
        Student sv = new Student("SV220001", "Le Van An", "le@ptit.edu.vn", "0923456789", "Ha Noi");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date receive = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 14);
        Date returnDt = cal.getTime();

        Book book = new Book();
        book.setIsbn(isbn);

        BorrowedBook bb = new BorrowedBook(book, returnDt, BigDecimal.valueOf(85000));

        Borrowing b = new Borrowing(sv, user, new Date(), receive);
        b.getBooks().add(bb);
        return b;
    }

    // ---- MODULE DAT SACH ----

    // TT11: SV va sach ton tai, con ban sao — dat thanh cong
    @Test
    public void testAddBorrowingStandard() {
        Borrowing b = buildSampleBorrowing("978-604-1-01234-5");
        boolean ok = bd.addBorrowing(b);
        Assert.assertTrue(ok);
        Assert.assertTrue(b.getId() > 0);
        deleteTestBorrowing(b.getId());
    }

    // TT12: Sach khong con ban sao kha dung — dat that bai
    @Test
    public void testAddBorrowingExceptionNoBookItem() {
        Borrowing b = buildSampleBorrowing("978-604-1-09012-3"); // 0 BookItem trong seed
        boolean ok = bd.addBorrowing(b);
        Assert.assertFalse(ok);
    }

    // TT13: Sinh vien khong ton tai — dat that bai (FK violation)
    @Test
    public void testAddBorrowingExceptionInvalidStudent() {
        User user    = new User(1, "admin", "Nguyen Van Admin", "admin");
        Student svKo = new Student("SV999999", "Khong Ton Tai", "", "", "");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date receive = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 14);

        Book book = new Book();
        book.setIsbn("978-604-1-01234-5");
        BorrowedBook bb = new BorrowedBook(book, cal.getTime(), BigDecimal.valueOf(85000));

        Borrowing b = new Borrowing(svKo, user, new Date(), receive);
        b.getBooks().add(bb);

        boolean ok = bd.addBorrowing(b);
        Assert.assertFalse(ok);
    }

    // ---- MODULE HUY DAT SACH ----

    // TT14: Khong co phieu pending cua tu khoa nay
    @Test
    public void testSearchBorrowingException1() {
        ArrayList<Borrowing> list = bd.searchBorrowing("XXXXXXXXXX");
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    // TT15: Co phieu pending — setup trong test, xoa sau khi test
    @Test
    public void testSearchBorrowingStandard() {
        Borrowing b = buildSampleBorrowing("978-604-1-01234-5");
        boolean addOk = bd.addBorrowing(b);
        Assert.assertTrue(addOk);
        try {
            ArrayList<Borrowing> list = bd.searchBorrowing("SV220001");
            Assert.assertNotNull(list);
            Assert.assertTrue(list.size() >= 1);
            Assert.assertEquals("pending", list.get(0).getStatus());
        } finally {
            deleteTestBorrowing(b.getId());
        }
    }

    // TT16: Huy phieu dang pending — thanh cong
    @Test
    public void testCancelBorrowingStandard() {
        Borrowing b = buildSampleBorrowing("978-604-1-01234-5");
        boolean addOk = bd.addBorrowing(b);
        Assert.assertTrue(addOk);
        try {
            boolean cancelOk = bd.cancelBorrowing(b.getId());
            Assert.assertTrue(cancelOk);

            // Sau huy: phieu nay khong con trong danh sach pending
            ArrayList<Borrowing> list = bd.searchBorrowing("SV220001");
            for (Borrowing item : list) {
                Assert.assertNotEquals(b.getId(), item.getId());
            }
        } finally {
            deleteTestBorrowing(b.getId());
        }
    }

    // TT17: Huy phieu khong ton tai — that bai
    @Test
    public void testCancelBorrowingExceptionNotExist() {
        boolean ok = bd.cancelBorrowing(999999);
        Assert.assertFalse(ok);
    }

    // TT18: Huy phieu khong o trang thai pending — that bai (huy lan 2)
    @Test
    public void testCancelBorrowingExceptionNotPending() {
        Borrowing b = buildSampleBorrowing("978-604-1-01234-5");
        boolean addOk = bd.addBorrowing(b);
        Assert.assertTrue(addOk);
        try {
            boolean cancel1 = bd.cancelBorrowing(b.getId());
            Assert.assertTrue(cancel1);

            boolean cancel2 = bd.cancelBorrowing(b.getId()); // phieu da la 'cancelled'
            Assert.assertFalse(cancel2);
        } finally {
            deleteTestBorrowing(b.getId());
        }
    }
}
