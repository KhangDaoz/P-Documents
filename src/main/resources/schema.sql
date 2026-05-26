-- =====================================================================
-- Library Management System - Database Schema + Seed Data
-- Modul 1: Thống kê sách mượn nhiều
-- Modul 2: Báo cáo sách hư hỏng / thất lạc
-- =====================================================================

CREATE DATABASE IF NOT EXISTS library_management
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_management;

DROP TABLE IF EXISTS stock_reports;
DROP TABLE IF EXISTS borrowed_books;
DROP TABLE IF EXISTS borrowings;
DROP TABLE IF EXISTS book_items;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- Người dùng (stub login - admin/admin)
-- ---------------------------------------------------------------------
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(20) NOT NULL
);

INSERT INTO users VALUES ('admin', 'admin', 'MANAGER');

-- ---------------------------------------------------------------------
-- Tầng thực thể: Book, BookItem, Student, Borrowing, BorrowedBook
-- ---------------------------------------------------------------------
CREATE TABLE books (
    book_id  VARCHAR(20) PRIMARY KEY,
    title    VARCHAR(255) NOT NULL,
    author   VARCHAR(255),
    category VARCHAR(100)
);

CREATE TABLE book_items (
    barcode VARCHAR(50) PRIMARY KEY,
    book_id VARCHAR(20) NOT NULL,
    status  VARCHAR(30) NOT NULL,    -- 'Sẵn sàng', 'Đang mượn', 'Hư hỏng', 'Thất lạc'
    CONSTRAINT fk_item_book FOREIGN KEY (book_id) REFERENCES books(book_id)
);

CREATE TABLE students (
    student_code VARCHAR(20) PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL
);

CREATE TABLE borrowings (
    borrow_id    INT AUTO_INCREMENT PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL,
    borrow_date  DATE NOT NULL,
    CONSTRAINT fk_borrow_student FOREIGN KEY (student_code) REFERENCES students(student_code)
);

CREATE TABLE borrowed_books (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    borrow_id   INT NOT NULL,
    barcode     VARCHAR(50) NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE NULL,
    status      VARCHAR(30) NOT NULL,    -- 'Đang mượn', 'Đã trả', 'Quá hạn'
    CONSTRAINT fk_bb_borrow FOREIGN KEY (borrow_id) REFERENCES borrowings(borrow_id),
    CONSTRAINT fk_bb_item   FOREIGN KEY (barcode)  REFERENCES book_items(barcode)
);

-- ---------------------------------------------------------------------
-- Modul 2: bản ghi sách hư hỏng / thất lạc
-- ---------------------------------------------------------------------
CREATE TABLE stock_reports (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    barcode       VARCHAR(50) NOT NULL,
    reason        VARCHAR(20) NOT NULL,    -- 'Hư hỏng' hoặc 'Thất lạc'
    reported_date DATE NOT NULL,
    CONSTRAINT fk_sr_item FOREIGN KEY (barcode) REFERENCES book_items(barcode)
);

-- =====================================================================
-- SEED DATA
-- =====================================================================

-- Đầu sách (tối thiểu phải có "Nhà Giả Kim" - spec §1.b bước 21)
INSERT INTO books VALUES
('B001', 'Nhà Giả Kim',       'Paulo Coelho',         'Tiểu thuyết'),
('B002', 'Đắc Nhân Tâm',      'Dale Carnegie',        'Kỹ năng sống'),
('B003', 'Tuổi Trẻ Đáng Giá Bao Nhiêu', 'Rosie Nguyễn', 'Kỹ năng sống'),
('B004', 'Sapiens',           'Yuval Noah Harari',    'Lịch sử'),
('B005', 'Clean Code',        'Robert C. Martin',     'Công nghệ thông tin'),
('B006', 'Cây Cam Ngọt Của Tôi', 'José Mauro de Vasconcelos', 'Tiểu thuyết'),
('B007', 'Tôi Tài Giỏi, Bạn Cũng Thế', 'Adam Khoo',   'Kỹ năng sống');

-- Bản sách (mỗi đầu sách có vài bản, mã vạch riêng)
INSERT INTO book_items VALUES
('BC0001', 'B001', 'Đang mượn'),
('BC0002', 'B001', 'Sẵn sàng'),
('BC0003', 'B001', 'Hư hỏng'),
('BC0004', 'B002', 'Sẵn sàng'),
('BC0005', 'B002', 'Đang mượn'),
('BC0006', 'B002', 'Thất lạc'),
('BC0007', 'B003', 'Sẵn sàng'),
('BC0008', 'B003', 'Đang mượn'),
('BC0009', 'B004', 'Sẵn sàng'),
('BC0010', 'B004', 'Hư hỏng'),
('BC0011', 'B005', 'Sẵn sàng'),
('BC0012', 'B005', 'Đang mượn'),
('BC0013', 'B006', 'Sẵn sàng'),
('BC0014', 'B007', 'Thất lạc');

-- Sinh viên
INSERT INTO students VALUES
('SV001', 'Nguyễn Văn An'),
('SV002', 'Trần Thị Bình'),
('SV003', 'Lê Văn Cường'),
('SV004', 'Phạm Thị Dung'),
('SV005', 'Hoàng Văn Em'),
('SV006', 'Vũ Minh Sáng');

-- Lượt mượn (header)
INSERT INTO borrowings (borrow_id, student_code, borrow_date) VALUES
(1, 'SV001', '2026-01-10'),
(2, 'SV002', '2026-01-15'),
(3, 'SV003', '2026-02-01'),
(4, 'SV004', '2026-02-12'),
(5, 'SV005', '2026-03-05'),
(6, 'SV001', '2026-03-20'),
(7, 'SV006', '2026-04-02'),
(8, 'SV002', '2026-04-15'),
(9, 'SV003', '2026-04-28'),
(10,'SV004', '2026-05-10');

-- Chi tiết mượn (Nhà Giả Kim - B001 - phải có nhiều lượt nhất)
INSERT INTO borrowed_books (borrow_id, barcode, due_date, return_date, status) VALUES
(1, 'BC0001', '2026-01-24', '2026-01-22', 'Đã trả'),
(2, 'BC0002', '2026-01-29', '2026-01-30', 'Đã trả'),
(3, 'BC0001', '2026-02-15', '2026-02-14', 'Đã trả'),
(4, 'BC0002', '2026-02-26', NULL,         'Đang mượn'),
(5, 'BC0001', '2026-03-19', '2026-03-18', 'Đã trả'),
(6, 'BC0002', '2026-04-03', '2026-04-05', 'Quá hạn'),
(7, 'BC0001', '2026-04-16', NULL,         'Đang mượn'),
-- Đắc Nhân Tâm
(2, 'BC0004', '2026-01-29', '2026-01-28', 'Đã trả'),
(5, 'BC0005', '2026-03-19', '2026-03-20', 'Đã trả'),
(8, 'BC0004', '2026-04-29', NULL,         'Đang mượn'),
-- Tuổi Trẻ Đáng Giá Bao Nhiêu
(3, 'BC0007', '2026-02-15', '2026-02-15', 'Đã trả'),
(9, 'BC0008', '2026-05-12', NULL,         'Đang mượn'),
-- Sapiens
(4, 'BC0009', '2026-02-26', '2026-02-28', 'Đã trả'),
(10,'BC0009', '2026-05-24', NULL,         'Đang mượn'),
-- Clean Code
(6, 'BC0011', '2026-04-03', '2026-04-02', 'Đã trả'),
(7, 'BC0012', '2026-04-16', NULL,         'Đang mượn'),
-- Cây Cam Ngọt Của Tôi
(8, 'BC0013', '2026-04-29', '2026-04-30', 'Đã trả');

-- Báo cáo hư hỏng / thất lạc (Modul 2)
INSERT INTO stock_reports (barcode, reason, reported_date) VALUES
('BC0003', 'Hư hỏng', '2026-02-10'),
('BC0006', 'Thất lạc', '2026-02-20'),
('BC0010', 'Hư hỏng', '2026-03-15'),
('BC0014', 'Thất lạc', '2026-04-08');
