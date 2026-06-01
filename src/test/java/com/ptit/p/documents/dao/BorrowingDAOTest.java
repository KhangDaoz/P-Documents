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

public class BorrowingDAOTest {

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

    // ---- MODULE DAT SACH ----

    // TT11: SV va sach ton tai, con ban sao — dat thanh cong
    @Test
    public void testAddBorrowingStandard() {
        Borrowing b = buildSampleBorrowing("ISBN-CS-01");
        boolean ok = bd.addBorrowing(b);
        Assert.assertTrue(ok);
        Assert.assertTrue(b.getId() > 0);
        deleteTestBorrowing(b.getId());
    }

    // TT12: Sach khong con ban sao kha dung — dat that bai
    @Test
    public void testAddBorrowingExceptionNoBookItem() {
        Borrowing b = buildSampleBorrowing("ISBN-CS-03"); // 0 BookItem trong seed
        boolean ok = bd.addBorrowing(b);
        Assert.assertFalse(ok);
    }

    // TT13: Sinh vien khong ton tai — dat that bai (FK violation)
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

    // TT16: Huy phieu dang pending — thanh cong
    @Test
    public void testCancelBorrowingStandard() {
        Borrowing b = buildSampleBorrowing("ISBN-CS-01");
        boolean addOk = bd.addBorrowing(b);
        Assert.assertTrue(addOk);
        try {
            boolean cancelOk = bd.cancelBorrowing(b.getId());
            Assert.assertTrue(cancelOk);

            // Sau huy: phieu nay khong con trong danh sach pending
            ArrayList<Borrowing> list = bd.searchBorrowing("SV001");
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
        Borrowing b = buildSampleBorrowing("ISBN-CS-01");
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
