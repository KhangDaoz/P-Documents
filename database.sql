-- ============================================================
--  HỆ THỐNG QUẢN LÝ THƯ VIỆN - BẢN CHUẨN (COPY & PASTE LÀ CHẠY)
--  Tạo database mới: p_documents
-- ============================================================

DROP DATABASE IF EXISTS p_documents;               -- Xóa database cũ nếu có (để làm sạch)
CREATE DATABASE p_documents
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE p_documents;

-- ============================================================
-- 1. tblStudent - Sinh viên mượn sách
-- ============================================================
CREATE TABLE IF NOT EXISTS tblStudent (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    fullName    VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    phone       VARCHAR(15)  NOT NULL,
    address     VARCHAR(255),
    createdAt   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ============================================================
-- 2. tblUser - Nhân viên thư viện
-- ============================================================
CREATE TABLE IF NOT EXISTS tblUser (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    username    VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    fullName    VARCHAR(255) NOT NULL,
    phone       VARCHAR(15)  NOT NULL,
    role        ENUM('admin', 'librarian', 'manager') NOT NULL DEFAULT 'librarian',
    createdAt   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ============================================================
-- 3. tblBook - Thông tin đầu sách
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBook (
    ISBN            VARCHAR(20)  NOT NULL,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(255) NOT NULL,
    genre           VARCHAR(255) NOT NULL,
    publisher       VARCHAR(255) NOT NULL,
    publishYear     INT(10)      NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    description     VARCHAR(255) NOT NULL,
    createdAt       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ISBN)
) ENGINE=InnoDB;

-- ============================================================
-- 4. tblBookItem - Bản sao sách (cuốn sách cụ thể)
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBookItem (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    status      ENUM('good', 'damaged', 'lost') NOT NULL DEFAULT 'good',
    tblBookISBN VARCHAR(20)  NOT NULL,
    createdAt   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_bookitem_book
        FOREIGN KEY (tblBookISBN) REFERENCES tblBook(ISBN)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 5. tblBorrowing - Phiếu mượn sách
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBorrowing (
    ID                  INT(10)      NOT NULL AUTO_INCREMENT,
    expectedReceiveDate DATE,
    actualReceiveDate   DATE,
    note                VARCHAR(255),
    status              ENUM('borrowed', 'returned', 'overdue', 'pending', 'cancelled') NOT NULL DEFAULT 'pending',
    tblStudentID        INT(10)      NOT NULL,
    tblUserID           INT(10)      NOT NULL,
    createdAt           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_borrowing_student
        FOREIGN KEY (tblStudentID) REFERENCES tblStudent(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_borrowing_user
        FOREIGN KEY (tblUserID) REFERENCES tblUser(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 6. tblBorrowedBook - Chi tiết từng cuốn trong phiếu mượn
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBorrowedBook (
    ID                  INT(10)      NOT NULL AUTO_INCREMENT,
    expectedReturnDate  DATE         NOT NULL,
    actualReturnDate    DATE,
    status              ENUM('lost', 'damaged', 'good') NOT NULL DEFAULT 'good',
    note                VARCHAR(255),
    price               DECIMAL(10,2),
    tblBookItemID       INT(10)      NOT NULL,
    tblBorrowingID      INT(10)      NOT NULL,
    createdAt           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_borrowedbook_bookitem
        FOREIGN KEY (tblBookItemID) REFERENCES tblBookItem(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_borrowedbook_borrowing
        FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 7. tblFine - Loại phạt
-- ============================================================
CREATE TABLE IF NOT EXISTS tblFine (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    fineRate    DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    createdAt   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ============================================================
-- 8. tblBorrowedBookFine - Phạt cho từng cuốn sách mượn
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBorrowedBookFine (
    ID                INT(10)       NOT NULL AUTO_INCREMENT,
    fineRate          DECIMAL(10,2) NOT NULL,
    tblBorrowedBookID INT(10)       NOT NULL,
    tblFineID         INT(10)       NOT NULL,
    createdAt         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_bbfine_borrowedbook
        FOREIGN KEY (tblBorrowedBookID) REFERENCES tblBorrowedBook(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_bbfine_fine
        FOREIGN KEY (tblFineID) REFERENCES tblFine(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 9. tblBill - Hóa đơn thanh toán
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBill (
    ID              INT(10)       NOT NULL AUTO_INCREMENT,
    paymentDate     DATE          NOT NULL,
    note            VARCHAR(255),
    paymentType     VARCHAR(255),
    tblBorrowingID  INT(10)       NOT NULL,
    tblUserID       INT(10)       NOT NULL,
    createdAt       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_bill_borrowing
        FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_bill_user
        FOREIGN KEY (tblUserID) REFERENCES tblUser(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- DỮ LIỆU MẪU (SEED DATA)
-- ============================================================

-- Nhân viên (mật khẩu để đơn giản - khi dùng thật phải hash)
INSERT INTO tblUser (username, password, fullName, phone, role) VALUES
('admin',     '123456', 'Nguyễn Văn Admin',  '0901234567', 'admin'),
('thangnt',   't123',   'Nguyễn Tiến Thắng', '0987654321', 'manager'),
('lanpt',     'l234',   'Phạm Thị Lan',      '0977777777', 'librarian'),
('minhbq',    'm123',   'Bùi Quang Minh',    '0944444444', 'librarian'),
('truongnv',  't456',   'Nguyễn Văn Trường', '0912345678', 'librarian'),
('dungnt',    'd789',   'Nguyễn Tuấn Dũng',  '0909090909', 'librarian'),
('namlh',     'n012',   'Lê Hoàng Nam',      '0988888888', 'librarian'),
('tuanva',    't567',   'Vũ Anh Tuấn',       '0966666666', 'librarian'),
('huongnt',   'h890',   'Ngô Thu Hương',     '0955555555', 'librarian'),
('quangdt',   'q234',   'Đặng Tuấn Quang',   '0933333333', 'librarian');

-- Sinh viên
INSERT INTO tblStudent (fullName, email, phone, address) VALUES
('Lê Văn An',     'levan.an@student.edu.vn',   '0923456789', 'Hà Nội'),
('Phạm Thị Bình', 'pham.binh@student.edu.vn', '0934567890', 'Hà Nội');

-- Đầu sách
INSERT INTO tblBook (ISBN, title, author, genre, publisher, publishYear, price, description) VALUES
('978-604-1-01234-5', 'Lập Trình C Căn Bản',   'Nguyễn Phú Quý',   'Technology', 'NXB Thông Tin', 2020, 85000.00, 'Sách nhập môn lập trình C'),
('978-604-1-05678-9', 'Cơ Sở Dữ Liệu',        'Trần Minh Tùng',   'Technology', 'NXB ĐHQG',      2019, 95000.00, 'Giáo trình cơ sở dữ liệu'),
('978-604-1-09012-3', 'Giải Tích Toán Học',   'Lê Đình Phương',   'Mathematics', 'NXB Giáo Dục',  2021, 75000.00, 'Tài liệu giải tích toán học');

-- Bản sao sách
INSERT INTO tblBookItem (status, tblBookISBN) VALUES
('good', '978-604-1-01234-5'),
('good', '978-604-1-01234-5'),
('good', '978-604-1-01234-5'),
('good', '978-604-1-05678-9'),
('good', '978-604-1-05678-9');

-- Loại phạt
INSERT INTO tblFine (name, fineRate, description) VALUES
('Trả trễ',       5000.00, 'Phạt 5.000đ/ngày trả trễ'),
('Mất sách',   500000.00, 'Phạt mất sách bằng giá trị sách'),
('Hư hỏng sách', 50000.00, 'Phạt hư hỏng tuỳ mức độ');

-- ============================================================
-- KIỂM TRA KẾT QUẢ
-- ============================================================
SELECT '✅ Tạo database và bảng thành công!' AS message;
SHOW TABLES;
