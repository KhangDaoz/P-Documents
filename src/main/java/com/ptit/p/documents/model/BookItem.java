package com.ptit.p.documents.model;

/**
 * Đại diện cho một bản sao vật lý của một đầu sách trong thư viện.
 * Mỗi Book (đầu sách) có thể có nhiều BookItem (bản sao vật lý).
 * Tương ứng với bảng tblBookItem trong CSDL.
 *
 * Các giá trị status (ENUM trong DB):
 *   "good"    — bản sao đang ở trạng thái tốt (có thể được mượn)
 *   "damaged" — bản sao bị hư hỏng
 *   "lost"    — bản sao bị mất
 *
 * Lưu ý: trạng thái "khả dụng để mượn" được xác định bởi status='good'
 * VÀ không đang nằm trong phiếu mượn active (pending/borrowed).
 * Xem BookDAO để biết cách tính availableCopies.
 */
public class BookItem {
    private int    id;
    private String status;       // 'good' | 'damaged' | 'lost'
    private String bookISBN;  // ISBN của đầu sách (tham chiếu tiện ích)

    public BookItem() {}

    public BookItem(int id, String status) {
        this.id     = id;
        this.status = status;
    }

    public BookItem(int id, String status, String bookISBN) {
        this.id = id;
        this.status = status;
        this.bookISBN = bookISBN;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTblBookISBN() {
        return bookISBN;
    }

    public void setTblBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    @Override
    public String toString() {
        return "BookItem{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", bookISBN='" + bookISBN + '\'' +
                '}';
    }
}
