package com.ptit.p.documents.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Xác minh chuỗi StockStat -> Book -> BookItem (spec §2.b bước 15-18).
 */
class StockStatTest {

    @Test
    void stockStat_reaches_bookItem_via_book() {
        Book book = new Book("B007", "Tôi Tài Giỏi, Bạn Cũng Thế", "Adam Khoo", "Kỹ năng sống");
        book.addItem(new BookItem("BC0014", "Thất lạc"));
        StockStat s = new StockStat(book, "Thất lạc", LocalDate.of(2026, 4, 8));

        assertEquals("Tôi Tài Giỏi, Bạn Cũng Thế", s.getBook().getTitle());
        assertEquals(1, s.getBook().getItems().size());
        assertEquals("BC0014", s.getBook().getItems().get(0).getId());
        assertEquals("Thất lạc", s.getBook().getItems().get(0).getStatus());
        assertEquals("Thất lạc", s.getReason());
        assertEquals(LocalDate.of(2026, 4, 8), s.getReportedDate());
    }
}
