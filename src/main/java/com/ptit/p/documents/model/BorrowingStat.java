package com.ptit.p.documents.model;

/**
 * Thực thể thống kê sách mượn nhiều - kế thừa Book.
 * Bổ sung thuộc tính borrowCount để hiển thị cột "Lượt mượn".
 */
public class BorrowingStat extends Book {
    private int borrowCount;

    public BorrowingStat() {
        super();
    }

    public BorrowingStat(String isbn, String title, String author, String genre, int borrowCount) {
        super(isbn, title, author, genre);
        this.borrowCount = borrowCount;
    }

    public int  getBorrowCount()       { return borrowCount; }
    public void setBorrowCount(int v)  { this.borrowCount = v; }
}
