

DROP DATABASE IF EXISTS p_documents;
CREATE DATABASE p_documents
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE p_documents;

CREATE TABLE tblUser (
    ID        INT(10)      NOT NULL AUTO_INCREMENT,
    username  VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    fullName  VARCHAR(255) NOT NULL,
    phone     VARCHAR(15)  NOT NULL,
    role      ENUM('admin','librarian','manager') NOT NULL DEFAULT 'librarian',
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    UNIQUE KEY uq_user_username (username)
) ENGINE=InnoDB;

CREATE TABLE tblStudent (
    ID        VARCHAR(20)  NOT NULL,
    fullName  VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    phone     VARCHAR(15)  NOT NULL,
    address   VARCHAR(255),
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE tblBook (
    ISBN        VARCHAR(20)   NOT NULL,
    title       VARCHAR(255)  NOT NULL,
    author      VARCHAR(255)  NOT NULL,
    genre       VARCHAR(255)  NOT NULL,
    publisher   VARCHAR(255)  NOT NULL,
    publishYear INT(10)       NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    description VARCHAR(255)  NOT NULL,
    createdAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ISBN)
) ENGINE=InnoDB;

CREATE TABLE tblBookItem (
    ID          INT(10) NOT NULL AUTO_INCREMENT,
    status      ENUM('good','damaged','lost') NOT NULL DEFAULT 'good',
    tblBookISBN VARCHAR(20) NOT NULL,
    createdAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_bookitem_book
        FOREIGN KEY (tblBookISBN) REFERENCES tblBook(ISBN)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE tblBorrowing (
    ID                  INT(10) NOT NULL AUTO_INCREMENT,
    expectedReceiveDate DATE,
    actualReceiveDate   DATE,
    note                VARCHAR(255),
    status              ENUM('borrowed','returned','overdue','pending','cancelled') NOT NULL DEFAULT 'pending',
    tblStudentID        VARCHAR(20) NOT NULL,
    tblUserID           INT(10) NOT NULL,
    createdAt           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_borrowing_student
        FOREIGN KEY (tblStudentID) REFERENCES tblStudent(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_borrowing_user
        FOREIGN KEY (tblUserID) REFERENCES tblUser(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE tblBorrowedBook (
    ID                 INT(10) NOT NULL AUTO_INCREMENT,
    expectedReturnDate DATE NOT NULL,
    actualReturnDate   DATE,
    status             ENUM('lost','damaged','good') NOT NULL DEFAULT 'good',
    note               VARCHAR(255),
    price              DECIMAL(10,2),
    tblBookItemID      INT(10) NOT NULL,
    tblBorrowingID     INT(10) NOT NULL,
    createdAt          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_borrowedbook_bookitem
        FOREIGN KEY (tblBookItemID) REFERENCES tblBookItem(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_borrowedbook_borrowing
        FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE tblFine (
    ID          INT(10)       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255)  NOT NULL,
    fineRate    DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    createdAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE tblBorrowedBookFine (
    ID                INT(10)       NOT NULL AUTO_INCREMENT,
    fineRate          DECIMAL(10,2) NOT NULL,
    tblBorrowedBookID INT(10)       NOT NULL,
    tblFineID         INT(10)       NOT NULL,
    createdAt         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_bbfine_borrowedbook
        FOREIGN KEY (tblBorrowedBookID) REFERENCES tblBorrowedBook(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_bbfine_fine
        FOREIGN KEY (tblFineID) REFERENCES tblFine(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE tblBill (
    ID             INT(10)      NOT NULL AUTO_INCREMENT,
    paymentDate    DATE         NOT NULL,
    note           VARCHAR(255),
    paymentType    VARCHAR(255),
    tblBorrowingID INT(10)      NOT NULL,
    tblUserID      INT(10)      NOT NULL,
    createdAt      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT fk_bill_borrowing
        FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_bill_user
        FOREIGN KEY (tblUserID) REFERENCES tblUser(ID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

INSERT INTO tblUser (username, password, fullName, phone, role) VALUES
('admin',      'admin123',   'Nguyễn Văn Admin', '0900000001', 'admin'),
('manager1',   'manager123', 'Lê Thị Quản Lý',   '0900000002', 'manager'),
('librarian1', 'lib123',     'Trần Thị Thư',     '0900000003', 'librarian');

INSERT INTO tblStudent (ID, fullName, email, phone, address) VALUES
('SV001', 'Đỗ Huy Hoàng',     'hoang@ptit.edu.vn', '0911111111', 'Hà Nội'),
('SV002', 'Nguyễn Minh Kiên', 'kien@ptit.edu.vn',  '0922222222', 'Hà Nội'),
('SV003', 'Vũ Minh Sáng',     'sang@ptit.edu.vn',  '0933333333', 'Đà Nẵng'),
('SV004', 'Lê Đức Hiếu',      'hieu@ptit.edu.vn',  '0944444444', 'TP.HCM'),
('SV005', 'Trần Đắc Mạnh',    'manh@ptit.edu.vn',  '0955555555', 'Hải Phòng');

INSERT INTO tblBook (ISBN, title, author, genre, publisher, publishYear, price, description) VALUES
('ISBN-CS-01', 'Nhập môn Công nghệ phần mềm',    'John Smith',           'Computer Science', 'MIT Press',       2022, 150000.00, 'Kiến thức nền tảng công nghệ phần mềm'),
('ISBN-CS-02', 'Cấu trúc dữ liệu và Giải thuật', 'Alice Johnson',        'Computer Science', 'Oxford',          2021, 180000.00, 'Cấu trúc dữ liệu với Java'),
('ISBN-CS-03', 'Hệ quản trị Cơ sở dữ liệu',      'Abraham Silberschatz', 'Computer Science', 'McGraw-Hill',     2020, 250000.00, 'Giáo trình cơ sở dữ liệu (chưa có bản sao)'),
('ISBN-CS-04', 'Sách kiểm thử Sửa/Xóa',          'Test Author',          'Computer Science', 'Test Publisher',  2024, 100000.00, 'Phục vụ test thêm/sửa/xóa'),
('ISBN-LIT-01','Giết con chim nhại',             'Harper Lee',           'Literature',       'J.B. Lippincott', 1960,  90000.00, 'Tác phẩm văn học kinh điển'),
('ISBN-SCI-01','Lược sử thời gian',              'Stephen Hawking',      'Science',          'Bantam Books',    1988, 120000.00, 'Vũ trụ học cho mọi người');

INSERT INTO tblBookItem (ID, status, tblBookISBN, createdAt, updatedAt) VALUES
( 1, 'good',    'ISBN-CS-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),
( 2, 'good',    'ISBN-CS-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),
( 3, 'damaged', 'ISBN-CS-01', '2025-12-01 00:00:00', '2026-02-10 00:00:00'),  
( 4, 'good',    'ISBN-CS-02', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),
( 5, 'good',    'ISBN-CS-02', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),
( 6, 'lost',    'ISBN-CS-02', '2025-12-01 00:00:00', '2026-02-20 00:00:00'),  
( 7, 'good',    'ISBN-CS-04', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),
( 8, 'good',    'ISBN-LIT-01','2025-12-01 00:00:00', '2025-12-01 00:00:00'),
( 9, 'damaged', 'ISBN-LIT-01','2025-12-01 00:00:00', '2026-03-15 00:00:00'),  
(10, 'good',    'ISBN-SCI-01','2025-12-01 00:00:00', '2025-12-01 00:00:00'),
(11, 'lost',    'ISBN-SCI-01','2025-12-01 00:00:00', '2026-04-08 00:00:00');  

INSERT INTO tblFine (ID, name, fineRate, description) VALUES
(1, 'Trả trễ',       5000.00, 'Phạt 5.000đ/ngày trả trễ'),
(2, 'Mất sách',    500000.00, 'Phạt mất sách bằng giá trị sách'),
(3, 'Hư hỏng sách', 50000.00, 'Phạt hư hỏng tuỳ mức độ');

INSERT INTO tblBorrowing (ID, tblStudentID, tblUserID, status, createdAt, updatedAt) VALUES
( 1, 'SV001', 3, 'returned', '2026-01-10 00:00:00', '2026-01-10 00:00:00'),  
( 2, 'SV002', 3, 'returned', '2026-01-20 00:00:00', '2026-01-20 00:00:00'),
( 3, 'SV003', 3, 'returned', '2026-02-05 00:00:00', '2026-02-05 00:00:00'),
( 4, 'SV004', 3, 'borrowed', '2026-02-25 00:00:00', '2026-02-25 00:00:00'),
( 5, 'SV005', 3, 'borrowed', '2026-03-10 00:00:00', '2026-03-10 00:00:00'),
( 6, 'SV001', 3, 'overdue',  '2026-03-22 00:00:00', '2026-03-22 00:00:00'),
( 7, 'SV002', 3, 'borrowed', '2026-04-05 00:00:00', '2026-04-05 00:00:00'),
( 8, 'SV003', 3, 'returned', '2026-04-20 00:00:00', '2026-04-20 00:00:00'),
( 9, 'SV004', 3, 'pending',  '2026-05-08 00:00:00', '2026-05-08 00:00:00'),
(10, 'SV005', 3, 'borrowed', '2026-05-10 00:00:00', '2026-05-10 00:00:00');  

INSERT INTO tblBorrowedBook (ID, tblBorrowingID, tblBookItemID, expectedReturnDate, actualReturnDate, status) VALUES

( 1,  1,  1, '2026-01-24', '2026-01-22', 'good'),
( 2,  2,  2, '2026-02-03', '2026-02-02', 'good'),
( 3,  3,  1, '2026-02-19', '2026-02-18', 'damaged'),  
( 4,  6,  2, '2026-04-05', NULL,         'good'),      
( 5, 10,  1, '2026-05-24', NULL,         'good'),      

( 6,  2,  4, '2026-02-03', '2026-02-02', 'good'),
( 7,  4,  5, '2026-03-11', NULL,         'good'),      
( 8,  8,  4, '2026-05-04', '2026-05-06', 'good'),      

( 9,  5,  8, '2026-03-24', NULL,         'good'),      
(10,  7,  8, '2026-04-19', NULL,         'good'),      

(11,  9, 10, '2026-05-22', NULL,         'good'),      

(12,  3,  7, '2026-02-19', '2026-02-18', 'good');

INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID) VALUES
(10000.00, 8, 1),   
(50000.00, 3, 3);   

INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID) VALUES
('2026-01-22', 'Trả đúng hạn',         'Tiền mặt', 1, 3),
('2026-02-02', 'Trả đúng hạn',         'Tiền mặt', 2, 3),
('2026-02-18', 'Có phí hư hỏng sách',  'Tiền mặt', 3, 3),
('2026-05-06', 'Trả trễ 2 ngày',       'Tiền mặt', 8, 3);

SELECT 'Khởi tạo p_documents hoàn tất!' AS message;
SHOW TABLES;
