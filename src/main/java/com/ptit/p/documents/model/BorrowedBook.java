package com.ptit.p.documents.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Đại diện cho một cuốn sách cụ thể trong một phiếu mượn.
 * Tương ứng với bảng tblBorrowedBook trong CSDL.
 *
 * Các giá trị status (ENUM trong DB):
 *   "good"    — sách được trả trong trạng thái tốt
 *   "damaged" — sách bị hư hỏng khi trả
 *   "lost"    — sách bị mất
 *
 * Lưu ý:
 *   - bookItem : bản sao vật lý được gán (auto-tìm bởi BorrowingDAO khi thêm mới).
 *   - book     : tham chiếu tiện ích đến đầu sách — không lưu trực tiếp vào DB,
 *                dùng để truyền thông tin ISBN khi tạo phiếu mượn mới.
 */
public class BorrowedBook {
    private int        id;
    private Date       expectedReturnDate;  // Ngày dự kiến trả
    private Date       actualReturnDate;    // Ngày trả thực tế
    private String     status;             // 'good' | 'damaged' | 'lost'
    private String     note;
    private BigDecimal price;
    private BookItem   bookItem;           // Bản sao vật lý được mượn (PK từ tblBookItem)
    private Book       book;              // Đầu sách — tham chiếu tiện ích, không phải cột DB

    public BorrowedBook() {}

    /**
     * Constructor dùng khi thủ thư vừa chọn sách từ kết quả tìm kiếm.
     * bookItem sẽ được auto-gán bởi BorrowingDAO.addBorrowing().
     *
     * @param book               Đầu sách sinh viên muốn mượn
     * @param expectedReturnDate Ngày dự kiến trả sách
     * @param price              Giá tiền mượn sách (lấy từ tblBook)
     */
    public BorrowedBook(Book book, Date expectedReturnDate, BigDecimal price) {
        this.book               = book;
        this.expectedReturnDate = expectedReturnDate;
        this.price              = price;
        this.status             = "good";
    }

    // -------- Getters & Setters --------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public Date getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(Date actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BookItem getBookItem() {
        return bookItem;
    }

    public void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
