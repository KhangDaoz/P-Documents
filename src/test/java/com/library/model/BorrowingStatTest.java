package com.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Xác minh điểm thiết kế then chốt từ spec §1.a:
 * "BorrowingStat (kế thừa Book)".
 */
class BorrowingStatTest {

    @Test
    void borrowingStat_extends_Book() {
        BorrowingStat s = new BorrowingStat("B001", "Nhà Giả Kim", "Paulo Coelho", "Tiểu thuyết", 42);
        assertTrue(s instanceof Book, "BorrowingStat phải kế thừa Book (spec §1.a)");
        assertEquals("B001", s.getBookId());
        assertEquals("Nhà Giả Kim", s.getTitle());
        assertEquals("Paulo Coelho", s.getAuthor());
        assertEquals("Tiểu thuyết", s.getCategory());
        assertEquals(42, s.getBorrowCount());
    }

    @Test
    void setters_work() {
        BorrowingStat s = new BorrowingStat();
        s.setBookId("B002");
        s.setTitle("Đắc Nhân Tâm");
        s.setBorrowCount(7);
        assertEquals("B002", s.getBookId());
        assertEquals("Đắc Nhân Tâm", s.getTitle());
        assertEquals(7, s.getBorrowCount());
    }
}
