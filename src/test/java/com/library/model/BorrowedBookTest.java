package com.library.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Xác minh quan hệ thành phần BorrowedBook -> Borrowing -> Student / BookItem -> Book
 * mô tả ở spec §1.b bước 27-31.
 */
class BorrowedBookTest {

    @Test
    void borrowedBook_chains_to_student_and_book() {
        Book book = new Book("B001", "Nhà Giả Kim", "Paulo Coelho", "Tiểu thuyết");
        BookItem item = new BookItem("BC0001", "Đang mượn", book);
        Student sv = new Student("SV001", "Nguyễn Văn An");
        Borrowing br = new Borrowing(1, sv, LocalDate.of(2026, 1, 10));
        BorrowedBook bb = new BorrowedBook(br, item,
                LocalDate.of(2026, 1, 24), null, "Đang mượn");

        assertEquals("SV001", bb.getBorrowing().getStudent().getStudentCode());
        assertEquals("Nguyễn Văn An", bb.getBorrowing().getStudent().getFullName());
        assertEquals("Nhà Giả Kim", bb.getBookItem().getBook().getTitle());
        assertEquals("BC0001", bb.getBookItem().getBarcode());
        assertEquals("Đang mượn", bb.getStatus());
        assertNull(bb.getReturnDate());
    }
}
