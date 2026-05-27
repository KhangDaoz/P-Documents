package com.ptit.p.documents.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Xác minh quan hệ BorrowedBook -> Borrowing -> Student (spec §1.b bước 27-31)
 * và BorrowedBook -> BookItem.
 */
class BorrowedBookTest {

    @Test
    void borrowedBook_chains_to_student_and_bookItem() {
        BookItem item = new BookItem("BC0001", "Đang mượn");
        Student sv = new Student("SV001", "Nguyễn Văn An");
        Borrowing br = new Borrowing(1, sv, LocalDate.of(2026, 1, 10));
        BorrowedBook bb = new BorrowedBook(br, item,
                LocalDate.of(2026, 1, 24), null, "Đang mượn");

        assertEquals("SV001", bb.getBorrowing().getStudent().getStudentCode());
        assertEquals("Nguyễn Văn An", bb.getBorrowing().getStudent().getFullName());
        assertEquals("BC0001", bb.getBookItem().getId());
        assertEquals("Đang mượn", bb.getStatus());
        assertNull(bb.getReturnDate());
    }
}
