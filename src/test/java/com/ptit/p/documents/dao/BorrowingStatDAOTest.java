package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BorrowingStat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho BorrowingStatDAO áp dụng kỹ thuật Phân vùng tương đương (EP) 
 * và Phân tích giá trị biên (BVA).
 * Lưu ý: Yêu cầu CSDL MySQL localhost:3306/p_documents đã chạy file schema.sql.
 */
class BorrowingStatDAOTest {

    private BorrowingStatDAO dao;

    @BeforeEach
    void setUp() {
        dao = new BorrowingStatDAO();
    }

    @Test
    void test_EP_D1_EP_N2_ValidRangeAndTopN() {
        // EP-D1: Khoảng thời gian có dữ liệu (01/01/2026 -> 31/05/2026)
        // EP-N2: Top N hợp lệ lớn (N >= tổng số sách) -> 10
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 10);
        
        assertFalse(result.isEmpty(), "Kết quả không được rỗng (EP-D1)");
        assertEquals(6, result.size(), "Có 6 đầu sách có lượt mượn trong CSDL");
        assertEquals("B001", result.get(0).getIsbn(), "Sách B001 (Nhà Giả Kim) mượn nhiều nhất");
        assertEquals(7, result.get(0).getBorrowCount());
    }

    @Test
    void test_BVA_D2_ToDateIsFirstBorrowingDate() {
        // BVA-D2: Đến ngày = đúng ngày lượt mượn đầu tiên (10/01/2026)
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 10);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 10);
        
        assertEquals(1, result.size(), "Chỉ có lượt mượn trong ngày 10/01/2026");
        assertEquals("B001", result.get(0).getIsbn());
        assertEquals(1, result.get(0).getBorrowCount());
    }

    @Test
    void test_BVA_D4_FromDateIsAfterLastBorrowingDate() {
        // BVA-D4: Từ ngày = 1 ngày sau lượt mượn cuối cùng (11/05/2026) -> Bảng rỗng
        LocalDate from = LocalDate.of(2026, 5, 11);
        LocalDate to = LocalDate.of(2026, 12, 31);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 10);
        
        assertTrue(result.isEmpty(), "Phải trả về rỗng do nằm sau ngày mượn cuối cùng");
    }

    @Test
    void test_BVA_N2_TopNIs1() {
        // BVA-N2: Top N = 1 (biên dưới)
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 1);
        
        assertEquals(1, result.size(), "Chỉ trả về 1 đầu sách");
        assertEquals("B001", result.get(0).getIsbn());
    }

    @Test
    void test_BVA_N6_TopNIsGreaterThanTotalBooks() {
        // BVA-N6: Top N = 7 (vượt tổng số 6 đầu sách có lượt mượn)
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 7);
        
        assertEquals(6, result.size(), "Vẫn chỉ trả về 6 đầu sách, không lỗi");
    }

    @Test
    void test_EP_D3_FromDateGreaterThanToDate() {
        // EP-D3: Từ ngày > Đến ngày (31/05/2026 -> 01/01/2026)
        LocalDate from = LocalDate.of(2026, 5, 31);
        LocalDate to = LocalDate.of(2026, 1, 1);
        
        List<BorrowingStat> result = dao.getTopBorrowedBooks(from, to, 10);
        
        assertTrue(result.isEmpty(), "SQL sẽ không tìm thấy dữ liệu nếu khoảng thời gian ngược");
    }
}
