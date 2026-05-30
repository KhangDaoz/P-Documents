-- ============================================================
--  Script khởi tạo CSDL cho hệ thống Quản lý Thư viện PTIT
--  Chạy file này trong MySQL Workbench hoặc dùng lệnh:
--    mysql -u root -p < library_db.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS library_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE library_db;

-- ============================================================
--  1. Bảng đầu sách
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBook (
    ISBN            VARCHAR(255) PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(255),
    genre           VARCHAR(255),
    publisher       VARCHAR(255),
    publishYear     INT,
    price           FLOAT,
    description     VARCHAR(255),
    availableCopies INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  2. Bảng bản sao vật lý của sách
--     status: 'available' | 'reserved' | 'borrowed' | 'lost'
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBookItem (
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    status      VARCHAR(255) NOT NULL DEFAULT 'available',
    tblBookISBN VARCHAR(255) NOT NULL,
    FOREIGN KEY (tblBookISBN) REFERENCES tblBook(ISBN)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  3. Bảng sinh viên
-- ============================================================
CREATE TABLE IF NOT EXISTS tblStudent (
    ID       VARCHAR(255) PRIMARY KEY,   -- Mã sinh viên, ví dụ: SV220134
    fullName VARCHAR(255),
    email    VARCHAR(255),
    phone    VARCHAR(255),
    address  VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  4. Bảng nhân viên thư viện (thủ thư / admin)
-- ============================================================
CREATE TABLE IF NOT EXISTS tblUser (
    ID       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    fullName VARCHAR(255),
    phone    VARCHAR(255),
    role     VARCHAR(255) DEFAULT 'librarian'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  5. Bảng phiếu mượn
--     status: 'Chờ nhận sách' | 'Đang mượn' | 'Đã trả' | 'Đã hủy'
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBorrowing (
    ID                   INT AUTO_INCREMENT PRIMARY KEY,
    borrowDate           DATETIME,
    expectedReceiveDate  DATETIME,
    actualReceiveDate    DATETIME,
    status               VARCHAR(255) NOT NULL DEFAULT 'Chờ nhận sách',
    tblStudentID         VARCHAR(255) NOT NULL,
    tblUserID            INT NOT NULL,
    FOREIGN KEY (tblStudentID) REFERENCES tblStudent(ID),
    FOREIGN KEY (tblUserID)    REFERENCES tblUser(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  6. Bảng sách trong phiếu mượn
-- ============================================================
CREATE TABLE IF NOT EXISTS tblBorrowedBook (
    ID                  INT AUTO_INCREMENT PRIMARY KEY,
    expectedReturnDate  DATETIME,
    actualReturnDate    DATETIME,
    status              VARCHAR(255) DEFAULT 'Chờ nhận sách',
    note                VARCHAR(255),
    price               FLOAT,
    tblBookItemID       INT NOT NULL,
    tblBorrowingID      INT NOT NULL,
    FOREIGN KEY (tblBookItemID)  REFERENCES tblBookItem(ID),
    FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  DỮ LIỆU MẪU (dùng để test)
-- ============================================================

-- Đầu sách mẫu (khớp với kịch bản trong đề)
INSERT INTO tblBook VALUES ('BK001', 'Lập trình Java căn bản', 'Nguyễn Văn X', 'CNTT', 'NXB PTIT',    2020, 120000, 'Sách học Java từ cơ bản', 3);
INSERT INTO tblBook VALUES ('BK045', 'Java nâng cao',           'Nguyễn Văn X', 'CNTT', 'NXB PTIT',    2021, 150000, 'Java nâng cao và design pattern', 1);
INSERT INTO tblBook VALUES ('BK210', 'Cấu trúc dữ liệu với Java','Trần Hữu Y',  'CNTT', 'NXB ĐHQG',   2019, 100000, 'CTDL và giải thuật với Java', 0);
INSERT INTO tblBook VALUES ('BK312', 'Kỹ nghệ phần mềm',        'Lê Thị Z',     'CNTT', 'NXB Bách khoa', 2022, 90000, 'Quy trình phát triển phần mềm', 2);

-- Bản sao vật lý (BookItem) — 3 bản BK001, 1 bản BK045, 2 bản BK312
INSERT INTO tblBookItem (status, tblBookISBN) VALUES ('available', 'BK001');
INSERT INTO tblBookItem (status, tblBookISBN) VALUES ('available', 'BK001');
INSERT INTO tblBookItem (status, tblBookISBN) VALUES ('available', 'BK001');
INSERT INTO tblBookItem (status, tblBookISBN) VALUES ('available', 'BK045');
INSERT INTO tblBookItem (status, tblBookISBN) VALUES ('available', 'BK312');
INSERT INTO tblBookItem (status, tblBookISBN) VALUES ('available', 'BK312');
-- BK210 availableCopies = 0, không có BookItem trạng thái 'available'

-- Tài khoản thủ thư mẫu
INSERT INTO tblUser (username, password, fullName, phone, role)
VALUES ('librarian01', '123456', 'Thủ thư A', '0900000001', 'librarian');

-- Sinh viên mẫu (khớp với kịch bản trong đề)
INSERT INTO tblStudent VALUES ('SV220134',  'Nguyễn Minh B',    'b.nguyen22@stu.edu.vn', '0988123456', 'Hà Nội');
INSERT INTO tblStudent VALUES ('SV220134A', 'Nguyễn Minh Bình', 'b.binh22@stu.edu.vn',   '0977666555', 'Hà Nội');
INSERT INTO tblStudent VALUES ('SV220200',  'Trần Văn C',       'c.tran22@stu.edu.vn',   '0912345678', 'Hà Nội');
