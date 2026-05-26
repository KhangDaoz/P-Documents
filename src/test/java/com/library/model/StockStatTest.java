package com.library.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Xác minh quan hệ StockStat -> BookItem -> Book (spec §2.b bước 14-18).
 */
class StockStatTest {

    @Test
    void stockStat_wraps_bookItem_and_reason() {
        Book book = new Book("B007", "Tôi Tài Giỏi, Bạn Cũng Thế", "Adam Khoo", "Kỹ năng sống");
        BookItem item = new BookItem("BC0014", "Thất lạc", book);
        StockStat s = new StockStat(item, "Thất lạc", LocalDate.of(2026, 4, 8));

        assertEquals("BC0014", s.getBookItem().getBarcode());
        assertEquals("Tôi Tài Giỏi, Bạn Cũng Thế", s.getBookItem().getBook().getTitle());
        assertEquals("Thất lạc", s.getReason());
        assertEquals(LocalDate.of(2026, 4, 8), s.getReportedDate());
    }
}
