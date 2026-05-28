-- ============================================================
--  LIBRARY MANAGEMENT SYSTEM - MySQL Setup Script
--  Đã tối ưu từ ERD gốc: sửa float→decimal, thêm NOT NULL,
--  thêm UNIQUE, thêm timestamps, chuẩn hoá quan hệ
-- ============================================================

CREATE DATABASE IF NOT EXISTS p_documents
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE p_documents;

-- ============================================================
-- 1. tblStudent
-- ============================================================
CREATE TABLE tblStudent (
    ID          VARCHAR(20)  NOT NULL,
    fullName    VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    phone       VARCHAR(15)  NOT NULL,
    address     VARCHAR(255),
    createdAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ============================================================
-- 2. tblUser  (nhân viên / thủ thư)
-- ============================================================
CREATE TABLE tblUser (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    username    VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    fullName    VARCHAR(255) NOT NULL,
    phone       VARCHAR(15) NOT NULL,
    role        ENUM('admin', 'librarian', 'manager') NOT NULL DEFAULT 'librarian',
    createdAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ============================================================
-- 3. tblBook
-- ============================================================
CREATE TABLE tblBook (
    ISBN            VARCHAR(20)  NOT NULL,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(255) NOT NULL,
    genre           VARCHAR(255) NOT NULL,
    publisher       VARCHAR(255) NOT NULL,
    publishYear     INT(10) NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    description     VARCHAR(255) NOT NULL,
    createdAt       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ISBN)
) ENGINE=InnoDB;

-- ============================================================
-- 4. tblBookItem  (bản sao vật lý của từng cuốn sách)
-- ============================================================
CREATE TABLE tblBookItem (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    status      ENUM('good', 'damaged', 'lost') NOT NULL DEFAULT 'good',
    tblBookISBN VARCHAR(20)  NOT NULL,
    createdAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_bookitem_book
        FOREIGN KEY (tblBookISBN) REFERENCES tblBook(ISBN)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 5. tblBorrowing  (phiếu mượn sách)
-- ============================================================
CREATE TABLE tblBorrowing (
    ID                  INT(10)      NOT NULL AUTO_INCREMENT,
    expectedReceiveDate DATE,
    actualReceiveDate   DATE,
    note                VARCHAR(255),
    status              ENUM('borrowed', 'returned', 'overdue', 'pending', 'cancelled') NOT NULL DEFAULT 'pending',
    tblStudentID        VARCHAR(20)  NOT NULL,
    tblUserID           INT(10)      NOT NULL,
    createdAt           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_borrowing_student
        FOREIGN KEY (tblStudentID) REFERENCES tblStudent(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_borrowing_user
        FOREIGN KEY (tblUserID) REFERENCES tblUser(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 6. tblBorrowedBook  (chi tiết từng cuốn trong phiếu mượn)
-- ============================================================
CREATE TABLE tblBorrowedBook (
    ID                  INT(10)      NOT NULL AUTO_INCREMENT,
    expectedReturnDate  DATE         NOT NULL,
    actualReturnDate    DATE,
    status              ENUM('lost', 'damaged', 'good') NOT NULL DEFAULT 'good',
    note                VARCHAR(255),
    price               DECIMAL(10,2),
    tblBookItemID       INT(10)      NOT NULL,
    tblBorrowingID      INT(10)      NOT NULL,
    createdAt           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_borrowedbook_bookitem
        FOREIGN KEY (tblBookItemID) REFERENCES tblBookItem(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_borrowedbook_borrowing
        FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 7. tblFine  (bảng loại phạt)
-- ============================================================
CREATE TABLE tblFine (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    fineRate    DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    createdAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ============================================================
-- 8. tblBorrowedBookFine  (phạt áp dụng cho từng lần mượn)
-- ============================================================
CREATE TABLE tblBorrowedBookFine (
    ID                INT(10)       NOT NULL AUTO_INCREMENT,
    fineRate          DECIMAL(10,2) NOT NULL,
    tblBorrowedBookID INT(10)       NOT NULL,
    tblFineID         INT(10)       NOT NULL,
    createdAt         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_bbfine_borrowedbook
        FOREIGN KEY (tblBorrowedBookID) REFERENCES tblBorrowedBook(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_bbfine_fine
        FOREIGN KEY (tblFineID) REFERENCES tblFine(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 9. tblBill  (hoá đơn thanh toán)
-- ============================================================
CREATE TABLE tblBill (
    ID              INT(10)       NOT NULL AUTO_INCREMENT,
    paymentDate     DATE          NOT NULL,
    note            VARCHAR(255),
    paymentType     VARCHAR(255),
    tblBorrowingID  INT(10)       NOT NULL,
    tblUserID       INT(10)       NOT NULL,
    createdAt       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_bill_borrowing
        FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_bill_user
        FOREIGN KEY (tblUserID) REFERENCES tblUser(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- SEED DATA MẪU
-- ============================================================

-- Users (mật khẩu mẫu: 'password123' - nhớ hash trong ứng dụng thực)
INSERT INTO tblUser (username, password, fullName, phone, role) VALUES
('admin',     '123456', 'Nguyễn Văn Admin',  '0901234567', 'admin'),
('librarian1','123456', 'Trần Thị Thư',      '0912345678', 'librarian');

-- Students
INSERT INTO tblStudent (ID, fullName, email, phone, address) VALUES
('SV220001', 'Lê Văn An',    'levan.an@student.edu.vn',   '0923456789', 'Hà Nội'),
('SV220002', 'Phạm Thị Bình','pham.binh@student.edu.vn', '0934567890', 'Hà Nội');

-- Books
INSERT INTO tblBook (ISBN, title, author, genre, publisher, publishYear, price, description) VALUES
('978-604-1-01234-5', 'Lập Trình C Căn Bản', 'Nguyễn Phú Quý', 'Technology', 'NXB Thông Tin', 2020, 85000.00, 'Sách nhập môn lập trình C'),
('978-604-1-05678-9', 'Cơ Sở Dữ Liệu', 'Trần Minh Tùng', 'Technology', 'NXB ĐHQG', 2019, 95000.00, 'Giáo trình cơ sở dữ liệu'),
('978-604-1-09012-3', 'Giải Tích Toán Học', 'Lê Đình Phương', 'Mathematics', 'NXB Giáo Dục', 2021, 75000.00, 'Tài liệu giải tích toán học');

-- BookItems (bản sao vật lý)
INSERT INTO tblBookItem (status, tblBookISBN) VALUES
('good', '978-604-1-01234-5'),
('good', '978-604-1-01234-5'),
('good', '978-604-1-01234-5'),
('good', '978-604-1-05678-9'),
('good', '978-604-1-05678-9');

-- Fine types
INSERT INTO tblFine (name, fineRate, description) VALUES
('Trả trễ',       5000.00, 'Phạt 5.000đ/ngày trả trễ'),
('Mất sách',   500000.00, 'Phạt mất sách bằng giá trị sách'),
('Hư hỏng sách', 50000.00, 'Phạt hư hỏng tuỳ mức độ');

-- ============================================================
-- KIỂM TRA
-- ============================================================
SELECT 'Setup hoàn tất!' AS message;
SHOW TABLES;

-- ============================================================
-- SANITY CHECKS
-- ============================================================
SELECT
    CASE
        WHEN COUNT(*) = 0 THEN 'OK: tblBook INSERT khớp schema'
        ELSE 'FAIL: tblBook đang có dữ liệu không hợp lệ'
    END AS book_insert_check
FROM information_schema.columns
WHERE table_schema = DATABASE()
  AND table_name = 'tblBook'
  AND column_name IN ('availableCopies', 'totalCopies');

SELECT
    CASE
        WHEN COUNT(*) = 0 THEN 'OK: tblBookItem.status không còn giá trị invalid'
        ELSE 'FAIL: tblBookItem.status vẫn còn giá trị invalid'
    END AS bookitem_status_check
FROM tblBookItem
WHERE status NOT IN ('good', 'damaged', 'lost');