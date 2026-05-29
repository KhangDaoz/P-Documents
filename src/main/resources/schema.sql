-- =====================================================================
-- Library Management System - Database Schema + Seed Data
-- Schema final: p_documents (theo message.txt)
-- Modul 1: Thống kê sách mượn nhiều
-- Modul 2: Báo cáo sách hư hỏng / thất lạc
-- =====================================================================

CREATE DATABASE IF NOT EXISTS p_documents
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE p_documents;

DROP TABLE IF EXISTS tblBorrowedBookFine;
DROP TABLE IF EXISTS tblBill;
DROP TABLE IF EXISTS tblFine;
DROP TABLE IF EXISTS tblBorrowedBook;
DROP TABLE IF EXISTS tblBorrowing;
DROP TABLE IF EXISTS tblBookItem;
DROP TABLE IF EXISTS tblBook;
DROP TABLE IF EXISTS tblStudent;
DROP TABLE IF EXISTS tblUser;

-- ---------------------------------------------------------------------
-- 1. tblStudent
-- ---------------------------------------------------------------------
CREATE TABLE tblStudent (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    fullName    VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    phone       VARCHAR(15)  NOT NULL,
    address     VARCHAR(255),
    createdAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. tblUser (nhân viên / thủ thư)
-- ---------------------------------------------------------------------
CREATE TABLE tblUser (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    username    VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    fullName    VARCHAR(255) NOT NULL,
    phone       VARCHAR(15)  NOT NULL,
    role        ENUM('admin', 'librarian', 'manager') NOT NULL DEFAULT 'librarian',
    createdAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. tblBook
-- ---------------------------------------------------------------------
CREATE TABLE tblBook (
    ISBN            VARCHAR(20)  NOT NULL,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(255) NOT NULL,
    genre           VARCHAR(255) NOT NULL,
    publisher       VARCHAR(255) NOT NULL,
    publishYear     INT(10)      NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    description     VARCHAR(255) NOT NULL,
    createdAt       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ISBN)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. tblBookItem (bản sao vật lý của từng cuốn sách)
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- 5. tblBorrowing (phiếu mượn sách)
-- ---------------------------------------------------------------------
CREATE TABLE tblBorrowing (
    ID                  INT(10)      NOT NULL AUTO_INCREMENT,
    expectedReceiveDate DATE,
    actualReceiveDate   DATE,
    note                VARCHAR(255),
    status              ENUM('borrowed', 'returned', 'overdue', 'pending', 'cancelled') NOT NULL DEFAULT 'pending',
    tblStudentID        INT(10)      NOT NULL,
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

-- ---------------------------------------------------------------------
-- 6. tblBorrowedBook (chi tiết từng cuốn trong phiếu mượn)
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- 7. tblFine (bảng loại phạt)
-- ---------------------------------------------------------------------
CREATE TABLE tblFine (
    ID          INT(10)      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    fineRate    DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    createdAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. tblBorrowedBookFine (phạt áp dụng cho từng lần mượn)
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- 9. tblBill (hoá đơn thanh toán)
-- ---------------------------------------------------------------------
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

-- =====================================================================
-- SEED DATA (sample data cho kiểm thử Modul 1 + Modul 2)
-- =====================================================================

-- ----- tblUser -----
INSERT INTO tblUser (username, password, fullName, phone, role) VALUES
('admin',      'admin',  'Nguyễn Văn Admin',  '0901234567', 'manager'),
('librarian1', '123456', 'Trần Thị Thư',      '0912345678', 'librarian');

-- ----- tblStudent -----
-- ID 1..6 tương ứng SV001..SV006 cũ
INSERT INTO tblStudent (fullName, email, phone, address) VALUES
('Nguyễn Văn An',  'nguyen.an@student.edu.vn',  '0923456789', 'Hà Nội'),
('Trần Thị Bình',  'tran.binh@student.edu.vn',  '0934567890', 'Hà Nội'),
('Lê Văn Cường',   'le.cuong@student.edu.vn',   '0945678901', 'Đà Nẵng'),
('Phạm Thị Dung',  'pham.dung@student.edu.vn',  '0956789012', 'TP.HCM'),
('Hoàng Văn Em',   'hoang.em@student.edu.vn',   '0967890123', 'Hải Phòng'),
('Vũ Minh Sáng',   'vu.sang@student.edu.vn',    '0978901234', 'Hà Nội');

-- ----- tblBook -----
-- ISBN dùng ID ngắn (B001..B007) để tương thích test case
INSERT INTO tblBook (ISBN, title, author, genre, publisher, publishYear, price, description) VALUES
('B001', 'Nhà Giả Kim',                    'Paulo Coelho',                'Tiểu thuyết',         'NXB Văn Học',  2020, 89000.00, 'Tiểu thuyết nổi tiếng thế giới'),
('B002', 'Đắc Nhân Tâm',                   'Dale Carnegie',               'Kỹ năng sống',        'NXB Tổng Hợp', 2019, 76000.00, 'Sách kỹ năng giao tiếp kinh điển'),
('B003', 'Tuổi Trẻ Đáng Giá Bao Nhiêu',    'Rosie Nguyễn',                'Kỹ năng sống',        'NXB Hội Nhà Văn', 2018, 65000.00, 'Sách dành cho giới trẻ'),
('B004', 'Sapiens',                          'Yuval Noah Harari',           'Lịch sử',             'NXB Tri Thức', 2021, 120000.00, 'Lược sử loài người'),
('B005', 'Clean Code',                       'Robert C. Martin',            'Công nghệ thông tin', 'Prentice Hall', 2008, 350000.00, 'Cẩm nang viết code sạch'),
('B006', 'Cây Cam Ngọt Của Tôi',            'José Mauro de Vasconcelos',   'Tiểu thuyết',         'NXB Hội Nhà Văn', 2020, 68000.00, 'Tiểu thuyết Brazil cảm động'),
('B007', 'Tôi Tài Giỏi, Bạn Cũng Thế',     'Adam Khoo',                   'Kỹ năng sống',        'NXB Phụ Nữ',  2017, 72000.00, 'Sách phát triển bản thân');

-- ----- tblBookItem -----
-- ID 1..14 tương ứng BC0001..BC0014 cũ
-- Các bản sách bình thường: status = 'good', createdAt/updatedAt mặc định
-- Các bản sách hư hỏng/thất lạc: status tương ứng, updatedAt = ngày phát hiện (cho Modul 2)
INSERT INTO tblBookItem (status, tblBookISBN, createdAt, updatedAt) VALUES
('good',    'B001', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=1  (BC0001)
('good',    'B001', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=2  (BC0002)
('damaged', 'B001', '2025-12-01 00:00:00', '2026-02-10 00:00:00'),  -- ID=3  (BC0003) Hư hỏng 10/02/2026
('good',    'B002', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=4  (BC0004)
('good',    'B002', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=5  (BC0005)
('lost',    'B002', '2025-12-01 00:00:00', '2026-02-20 00:00:00'),  -- ID=6  (BC0006) Thất lạc 20/02/2026
('good',    'B003', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=7  (BC0007)
('good',    'B003', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=8  (BC0008)
('good',    'B004', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=9  (BC0009)
('damaged', 'B004', '2025-12-01 00:00:00', '2026-03-15 00:00:00'),  -- ID=10 (BC0010) Hư hỏng 15/03/2026
('good',    'B005', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=11 (BC0011)
('good',    'B005', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=12 (BC0012)
('good',    'B006', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),  -- ID=13 (BC0013)
('lost',    'B007', '2025-12-01 00:00:00', '2026-04-08 00:00:00');  -- ID=14 (BC0014) Thất lạc 08/04/2026

-- ----- tblBorrowing -----
-- createdAt = borrow_date cũ (dùng cho Modul 1 lọc theo khoảng thời gian)
-- tblUserID = 1 (admin) cho tất cả
INSERT INTO tblBorrowing (ID, tblStudentID, tblUserID, status, createdAt, updatedAt) VALUES
( 1, 1, 1, 'borrowed', '2026-01-10 00:00:00', '2026-01-10 00:00:00'),
( 2, 2, 1, 'borrowed', '2026-01-15 00:00:00', '2026-01-15 00:00:00'),
( 3, 3, 1, 'borrowed', '2026-02-01 00:00:00', '2026-02-01 00:00:00'),
( 4, 4, 1, 'borrowed', '2026-02-12 00:00:00', '2026-02-12 00:00:00'),
( 5, 5, 1, 'borrowed', '2026-03-05 00:00:00', '2026-03-05 00:00:00'),
( 6, 1, 1, 'borrowed', '2026-03-20 00:00:00', '2026-03-20 00:00:00'),
( 7, 6, 1, 'borrowed', '2026-04-02 00:00:00', '2026-04-02 00:00:00'),
( 8, 2, 1, 'borrowed', '2026-04-15 00:00:00', '2026-04-15 00:00:00'),
( 9, 3, 1, 'borrowed', '2026-04-28 00:00:00', '2026-04-28 00:00:00'),
(10, 4, 1, 'borrowed', '2026-05-10 00:00:00', '2026-05-10 00:00:00');

-- ----- tblBorrowedBook -----
-- Nhà Giả Kim (B001): bookItem 1,2 → 7 lượt
INSERT INTO tblBorrowedBook (tblBorrowingID, tblBookItemID, expectedReturnDate, actualReturnDate, status) VALUES
( 1,  1, '2026-01-24', '2026-01-22', 'good'),     -- ID=1
( 2,  2, '2026-01-29', '2026-01-30', 'good'),     -- ID=2
( 3,  1, '2026-02-15', '2026-02-14', 'good'),     -- ID=3
( 4,  2, '2026-02-26', NULL,         'good'),     -- ID=4  Đang mượn
( 5,  1, '2026-03-19', '2026-03-18', 'good'),     -- ID=5
( 6,  2, '2026-04-03', '2026-04-05', 'good'),     -- ID=6  Trả trễ
( 7,  1, '2026-04-16', NULL,         'good'),     -- ID=7  Đang mượn
-- Đắc Nhân Tâm (B002): bookItem 4,5 → 3 lượt
( 2,  4, '2026-01-29', '2026-01-28', 'good'),     -- ID=8
( 5,  5, '2026-03-19', '2026-03-20', 'good'),     -- ID=9
( 8,  4, '2026-04-29', NULL,         'good'),     -- ID=10 Đang mượn
-- Tuổi Trẻ Đáng Giá Bao Nhiêu (B003): bookItem 7,8 → 2 lượt
( 3,  7, '2026-02-15', '2026-02-15', 'good'),     -- ID=11
( 9,  8, '2026-05-12', NULL,         'good'),     -- ID=12 Đang mượn
-- Sapiens (B004): bookItem 9 → 2 lượt
( 4,  9, '2026-02-26', '2026-02-28', 'good'),     -- ID=13
(10,  9, '2026-05-24', NULL,         'good'),     -- ID=14 Đang mượn
-- Clean Code (B005): bookItem 11,12 → 2 lượt
( 6, 11, '2026-04-03', '2026-04-02', 'good'),     -- ID=15
( 7, 12, '2026-04-16', NULL,         'good'),     -- ID=16 Đang mượn
-- Cây Cam Ngọt Của Tôi (B006): bookItem 13 → 1 lượt
( 8, 13, '2026-04-29', '2026-04-30', 'good');     -- ID=17

-- ----- tblFine -----
INSERT INTO tblFine (name, fineRate, description) VALUES
('Trả trễ',       5000.00, 'Phạt 5.000đ/ngày trả trễ'),
('Mất sách',   500000.00, 'Phạt mất sách bằng giá trị sách'),
('Hư hỏng sách', 50000.00, 'Phạt hư hỏng tuỳ mức độ');

-- =====================================================================
-- SANITY CHECKS
-- =====================================================================
SELECT 'Setup p_documents hoàn tất!' AS message;
SHOW TABLES;

-- Kiểm tra lượt mượn B001 = 7 (Modul 1)
SELECT b.ISBN, b.title, COUNT(bb.ID) AS borrow_count
FROM tblBook b
JOIN tblBookItem bi ON bi.tblBookISBN = b.ISBN
JOIN tblBorrowedBook bb ON bb.tblBookItemID = bi.ID
GROUP BY b.ISBN, b.title
ORDER BY borrow_count DESC;

-- Kiểm tra sách hư hỏng/thất lạc = 4 bản (Modul 2)
SELECT bi.ID, b.title, bi.status, bi.updatedAt
FROM tblBookItem bi
JOIN tblBook b ON b.ISBN = bi.tblBookISBN
WHERE bi.status IN ('damaged', 'lost')
ORDER BY bi.updatedAt;
