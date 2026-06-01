package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BorrowingStat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BorrowingStatDAOTest {

    private BorrowingStatDAO dao;

    @BeforeEach
    void setUp() {
        dao = new BorrowingStatDAO();
    }

    @Test
    void test_EP_D1_EP_N2_ValidRangeAndTopN() {
        
        
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 10);
        
        assertFalse(result.isEmpty(), "Kết quả không được rỗng (EP-D1)");
        assertEquals(5, result.size(), "Có 5 đầu sách có lượt mượn trong CSDL");
        assertEquals("ISBN-CS-01", result.get(0).getIsbn(), "Sách ISBN-CS-01 mượn nhiều nhất");
        assertEquals(5, result.get(0).getBorrowCount());
    }

    @Test
    void test_BVA_D2_ToDateIsFirstBorrowingDate() {
        
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 10);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 10);
        
        assertEquals(1, result.size(), "Chỉ có lượt mượn trong ngày 10/01/2026");
        assertEquals("ISBN-CS-01", result.get(0).getIsbn());
        assertEquals(1, result.get(0).getBorrowCount());
    }

    @Test
    void test_BVA_D4_FromDateIsAfterLastBorrowingDate() {
        
        LocalDate from = LocalDate.of(2026, 5, 11);
        LocalDate to = LocalDate.of(2026, 12, 31);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 10);
        
        assertTrue(result.isEmpty(), "Phải trả về rỗng do nằm sau ngày mượn cuối cùng");
    }

    @Test
    void test_BVA_N2_TopNIs1() {
        
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 1);
        
        assertEquals(1, result.size(), "Chỉ trả về 1 đầu sách");
        assertEquals("ISBN-CS-01", result.get(0).getIsbn());
    }

    @Test
    void test_BVA_N6_TopNIsGreaterThanTotalBooks() {
        
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 7);
        
        assertEquals(5, result.size(), "Vẫn chỉ trả về 5 đầu sách, không lỗi");
    }

    @Test
    void test_EP_D3_FromDateGreaterThanToDate() {
        
        LocalDate from = LocalDate.of(2026, 5, 31);
        LocalDate to = LocalDate.of(2026, 1, 1);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 10);
        
        assertTrue(result.isEmpty(), "SQL sẽ không tìm thấy dữ liệu nếu khoảng thời gian ngược");
    }
}
