package com.library.model;

/**
 * Thực thể thống kê sách mượn nhiều - kế thừa Book (spec §1.a "Tầng thực thể").
 * Bổ sung thuộc tính borrowCount để hiển thị cột "Lượt mượn" (spec §1.b bước 20).
 */
public class BorrowingStat extends Book {
    private int borrowCount;

    public BorrowingStat() {
        super();
    }

    public BorrowingStat(String bookId, String title, String author, String category, int borrowCount) {
        super(bookId, title, author, category);
        this.borrowCount = borrowCount;
    }

    public int  getBorrowCount()       { return borrowCount; }
    public void setBorrowCount(int v)  { this.borrowCount = v; }
}
