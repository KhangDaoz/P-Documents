package com.ptit.p.documents.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Đại diện cho một phiếu mượn (borrowing slip) trong hệ thống thư viện.
 * Tương ứng với bảng tblBorrowing trong CSDL.
 *
 * Vòng đời trạng thái (status) — theo ENUM trong DB:
 *   "pending"   → Chờ nhận sách (trạng thái khởi tạo)
 *   "borrowed"  → Đang mượn (đã nhận sách)
 *   "returned"  → Đã trả
 *   "overdue"   → Quá hạn
 *   "cancelled" → Đã hủy
 *
 * Lưu ý: borrowDate không có trong DB mới — thay bằng createdAt (tự động).
 * Sử dụng getCreatedAt() nếu cần hiển thị ngày tạo phiếu.
 */
public class Borrowing {
    private int                    id;
    private Date                   expectedReceiveDate;  // Ngày dự kiến đến nhận sách
    private Date                   actualReceiveDate;    // Ngày thực tế nhận sách
    private String                 note;                // Ghi chú (nullable)
    private String                 status;             // Trạng thái phiếu mượn
    private Date                   createdAt;          // Ngày tạo phiếu (ánh xạ từ DB)
    private ArrayList<BorrowedBook> books;              // Danh sách sách trong phiếu
    private Student                student;             // Sinh viên mượn sách
    private User                   user;               // Thủ thư tạo phiếu

    public Borrowing() {
        this.books = new ArrayList<>();
    }

    /**
     * Constructor dùng khi thủ thư vừa xác nhận thông tin để tạo phiếu mới.
     *
     * @param student             Sinh viên đã xác nhận
     * @param user                Thủ thư đang thao tác
     * @param expectedReceiveDate Ngày sinh viên dự kiến đến nhận sách
     * @param note                Ghi chú thêm (có thể null)
     */
    public Borrowing(Student student, User user, Date expectedReceiveDate, String note) {
        this.student             = student;
        this.user               = user;
        this.expectedReceiveDate = expectedReceiveDate;
        this.note               = note;
        this.status             = "pending";
        this.books              = new ArrayList<>();
    }

    /**
     * Constructor tương thích với code cũ (không có note).
     *
     * @param student             Sinh viên đã xác nhận
     * @param user                Thủ thư đang thao tác
     * @param borrowDate          Bỏ qua — DB mới dùng createdAt tự động (truyền null cũng được)
     * @param expectedReceiveDate Ngày sinh viên dự kiến đến nhận sách
     */
    public Borrowing(Student student, User user, Date borrowDate, Date expectedReceiveDate) {
        this.student             = student;
        this.user               = user;
        this.expectedReceiveDate = expectedReceiveDate;
        this.status             = "pending";
        this.books              = new ArrayList<>();
    }

    // -------- Getters & Setters --------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @deprecated Không còn cột borrowDate trong DB mới. Dùng getCreatedAt() thay thế.
     * Giữ lại để tương thích với code View cũ.
     */
    @Deprecated
    public Date getBorrowDate() {
        return createdAt;
    }

    /**
     * @deprecated Không còn cột borrowDate trong DB mới. Dùng setCreatedAt() thay thế.
     */
    @Deprecated
    public void setBorrowDate(Date borrowDate) {
        this.createdAt = borrowDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpectedReceiveDate() {
        return expectedReceiveDate;
    }

    public void setExpectedReceiveDate(Date expectedReceiveDate) {
        this.expectedReceiveDate = expectedReceiveDate;
    }

    public Date getActualReceiveDate() {
        return actualReceiveDate;
    }

    public void setActualReceiveDate(Date actualReceiveDate) {
        this.actualReceiveDate = actualReceiveDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<BorrowedBook> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<BorrowedBook> books) {
        this.books = books;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
