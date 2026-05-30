USE p_documents;

-- ============================================================
-- 1. Add more students (total ≥ 10)
-- ============================================================
INSERT INTO tblStudent (fullName, email, phone, address) VALUES
('Nguyễn Thị Cúc', 'cuc.nguyen@student.edu.vn', '0945678901', 'Hải Phòng'),
('Trần Văn Dũng', 'dung.tran@student.edu.vn', '0956789012', 'Đà Nẵng'),
('Lê Thị Huệ',   'hue.le@student.edu.vn',   '0967890123', 'TP HCM'),
('Phạm Văn Giang','giang.pham@student.edu.vn','0978901234', 'Cần Thơ'),
('Đỗ Thị Hà',     'ha.do@student.edu.vn',     '0989012345', 'Nha Trang'),
('Vũ Văn Hưng',   'hung.vu@student.edu.vn',   '0990123456', 'Huế'),
('Bùi Thị Lan',   'lan.bui@student.edu.vn',   '0901234568', 'Hà Nội'),
('Ngô Văn Minh',  'minh.ngo@student.edu.vn',  '0912345679', 'Bình Dương');

-- ============================================================
-- 2. Add more users (librarians, managers) (total ≥ 10)
-- ============================================================
INSERT INTO tblUser (username, password, fullName, phone, role) VALUES
('librarian2', '123456', 'Lê Thị Mượn',      '0923456780', 'librarian'),
('librarian3', '123456', 'Phạm Văn Kho',      '0934567891', 'librarian'),
('librarian4', '123456', 'Hoàng Thị Trang',   '0945678902', 'librarian'),
('manager1',   '123456', 'Đỗ Quốc Huy',       '0956789013', 'manager'),
('manager2',   '123456', 'Trần Thị Yến',      '0967890124', 'manager'),
('admin2',     '123456', 'Nguyễn Hải Đăng',   '0978901235', 'admin'),
('librarian5', '123456', 'Vũ Thị Ngọc',       '0989012346', 'librarian'),
('librarian6', '123456', 'Bùi Minh Tuấn',     '0990123457', 'librarian');

-- ============================================================
-- 3. Add more books (total ≥ 10)
-- ============================================================
INSERT INTO tblBook (ISBN, title, author, genre, publisher, publishYear, price, description) VALUES
('978-604-1-12345-6', 'Python Cho Người Mới Bắt Đầu', 'Vũ Hữu Thông', 'Technology', 'NXB Khoa Học', 2022, 120000.00, 'Hướng dẫn lập trình Python căn bản'),
('978-604-1-67890-1', 'Lịch Sử Việt Nam', 'Trần Trọng Kim', 'History', 'NXB Giáo Dục', 2018, 95000.00, 'Tóm lược lịch sử Việt Nam qua các thời kỳ'),
('978-604-1-23456-7', 'Nhà Giả Kim', 'Paulo Coelho', 'Literature', 'NXB Văn Học', 2015, 89000.00, 'Tiểu thuyết nổi tiếng thế giới'),
('978-604-1-34567-8', 'Toán Cao Cấp Tập 1', 'Nguyễn Đình Trí', 'Mathematics', 'NXB ĐHQG', 2020, 110000.00, 'Giáo trình đại số và giải tích'),
('978-604-1-45678-9', 'Vật Lý Đại Cương', 'Lương Duyên Bình', 'Science', 'NXB Giáo Dục', 2019, 105000.00, 'Cơ, nhiệt, điện từ cơ bản'),
('978-604-1-56789-0', 'Kỹ Thuật Phần Mềm', 'Trần Đức Quang', 'Technology', 'NXB Bách Khoa', 2021, 135000.00, 'Quy trình phát triển phần mềm'),
('978-604-1-67890-2', 'Nghệ Thuật Giao Tiếp', 'Dale Carnegie', 'Self-help', 'NXB Tổng Hợp', 2017, 75000.00, 'Kỹ năng ứng xử và thuyết phục');

-- ============================================================
-- 4. Add more book items (physical copies) (total ≥ 10)
--    For existing books and new books
-- ============================================================
INSERT INTO tblBookItem (status, tblBookISBN) VALUES
('good', '978-604-1-12345-6'),
('good', '978-604-1-12345-6'),
('good', '978-604-1-67890-1'),
('good', '978-604-1-23456-7'),
('good', '978-604-1-34567-8');

-- additional copies for earlier books (optional, but good to have)
INSERT INTO tblBookItem (status, tblBookISBN) VALUES
('good', '978-604-1-01234-5'),
('good', '978-604-1-05678-9');

-- ============================================================
-- 5. Add more fine types (total ≥ 10)
-- ============================================================
INSERT INTO tblFine (name, fineRate, description) VALUES
('Trả trễ 1-3 ngày', 3000.00, 'Phạt 3.000đ/ngày nếu trễ 1-3 ngày'),
('Trả trễ 4-7 ngày', 6000.00, 'Phạt 6.000đ/ngày nếu trễ 4-7 ngày'),
('Trả trễ >7 ngày', 10000.00, 'Phạt 10.000đ/ngày nếu trễ trên 7 ngày'),
('Làm rách bìa', 30000.00, 'Phạt sửa chữa bìa sách'),
('Làm bẩn sách', 20000.00, 'Phạt vệ sinh / thay bìa'),
('Mất tem mã vạch', 15000.00, 'Phạt in lại tem'),
('Mất sách giá cao', 0.00, 'Phạt = 150% giá sách (tính riêng)');

-- ============================================================
-- 6. Borrowing records (10) with different statuses
--    Use subqueries to pick existing students & users
-- ============================================================

-- Borrowing 1 (returned, with fines later)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-02-01', '2025-02-01', 'Mượn sách Python', 'returned',
        (SELECT ID FROM tblStudent WHERE email = 'levan.an@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'librarian1'));
SET @borrow1 = LAST_INSERT_ID();

-- Borrowing 2 (returned, with late fine)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-02-05', '2025-02-05', 'Mượn sách Cơ sở dữ liệu', 'returned',
        (SELECT ID FROM tblStudent WHERE email = 'pham.binh@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'librarian2'));
SET @borrow2 = LAST_INSERT_ID();

-- Borrowing 3 (currently borrowed, not yet returned)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-03-01', '2025-03-01', 'Mượn sách Lịch sử Việt Nam', 'borrowed',
        (SELECT ID FROM tblStudent WHERE email = 'cuc.nguyen@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'librarian3'));
SET @borrow3 = LAST_INSERT_ID();

-- Borrowing 4 (overdue – not returned yet)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-02-10', '2025-02-10', 'Mượn sách Toán cao cấp', 'overdue',
        (SELECT ID FROM tblStudent WHERE email = 'dung.tran@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'librarian4'));
SET @borrow4 = LAST_INSERT_ID();

-- Borrowing 5 (pending approval)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-03-05', NULL, 'Chờ duyệt mượn Nhà giả kim', 'pending',
        (SELECT ID FROM tblStudent WHERE email = 'hue.le@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'librarian5'));
SET @borrow5 = LAST_INSERT_ID();

-- Borrowing 6 (cancelled)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-02-15', NULL, 'Hủy do sinh viên không đến', 'cancelled',
        (SELECT ID FROM tblStudent WHERE email = 'giang.pham@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'librarian1'));
SET @borrow6 = LAST_INSERT_ID();

-- Borrowing 7 (returned, damaged book)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-02-20', '2025-02-20', 'Mượn Kỹ thuật phần mềm', 'returned',
        (SELECT ID FROM tblStudent WHERE email = 'ha.do@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'manager1'));
SET @borrow7 = LAST_INSERT_ID();

-- Borrowing 8 (returned, lost book → heavy fine)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-01-10', '2025-01-10', 'Mượn sách Vật lý đại cương', 'returned',
        (SELECT ID FROM tblStudent WHERE email = 'hung.vu@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'librarian2'));
SET @borrow8 = LAST_INSERT_ID();

-- Borrowing 9 (overdue but later returned, will have late fine)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-01-25', '2025-01-25', 'Mượn sách Nghệ thuật giao tiếp', 'returned',
        (SELECT ID FROM tblStudent WHERE email = 'lan.bui@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'librarian3'));
SET @borrow9 = LAST_INSERT_ID();

-- Borrowing 10 (currently borrowed, multiple books)
INSERT INTO tblBorrowing (expectedReceiveDate, actualReceiveDate, note, status, tblStudentID, tblUserID)
VALUES ('2025-03-02', '2025-03-02', 'Mượn nhiều sách công nghệ', 'borrowed',
        (SELECT ID FROM tblStudent WHERE email = 'minh.ngo@student.edu.vn'),
        (SELECT ID FROM tblUser WHERE username = 'librarian4'));
SET @borrow10 = LAST_INSERT_ID();

-- ============================================================
-- 7. BorrowedBook entries (at least 10, each with expectedReturnDate)
-- ============================================================

-- For borrowing 1 (returned on time, no fine)
INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-02-15', '2025-02-14', 'good', 'Trả đúng hạn', price, ID, @borrow1
FROM tblBookItem WHERE tblBookISBN = '978-604-1-12345-6' LIMIT 1;

-- For borrowing 2 (returned 2 days late → fine)
INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-02-19', '2025-02-21', 'good', 'Trả trễ 2 ngày', price, ID, @borrow2
FROM tblBookItem WHERE tblBookISBN = '978-604-1-05678-9' LIMIT 1;

-- For borrowing 3 (still borrowed, not returned)
INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-03-15', NULL, 'good', 'Chưa trả', price, ID, @borrow3
FROM tblBookItem WHERE tblBookISBN = '978-604-1-67890-1' LIMIT 1;

-- For borrowing 4 (overdue, not returned)
INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-02-24', NULL, 'good', 'Quá hạn chưa trả', price, ID, @borrow4
FROM tblBookItem WHERE tblBookISBN = '978-604-1-34567-8' LIMIT 1;

-- For borrowing 5 (pending, no actual return)
INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-03-19', NULL, 'good', 'Chờ duyệt', price, ID, @borrow5
FROM tblBookItem WHERE tblBookISBN = '978-604-1-23456-7' LIMIT 1;

-- For borrowing 7 (returned, damaged)
INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-02-27', '2025-02-28', 'damaged', 'Trang 30-32 bị rách', price, ID, @borrow7
FROM tblBookItem WHERE tblBookISBN = '978-604-1-56789-0' LIMIT 1;

-- For borrowing 8 (lost book)
INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-01-24', NULL, 'lost', 'Sinh viên làm mất sách', price, ID, @borrow8
FROM tblBookItem WHERE tblBookISBN = '978-604-1-45678-9' LIMIT 1;

-- For borrowing 9 (returned late 6 days)
INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-02-08', '2025-02-14', 'good', 'Trả trễ 6 ngày', price, ID, @borrow9
FROM tblBookItem WHERE tblBookISBN = '978-604-1-67890-2' LIMIT 1;

-- For borrowing 10 (two borrowed books)
INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-03-16', NULL, 'good', 'Đang mượn', price, ID, @borrow10
FROM tblBookItem WHERE tblBookISBN = '978-604-1-01234-5' LIMIT 1;

INSERT INTO tblBorrowedBook (expectedReturnDate, actualReturnDate, status, note, price, tblBookItemID, tblBorrowingID)
SELECT '2025-03-16', NULL, 'good', 'Đang mượn', price, ID, @borrow10
FROM tblBookItem WHERE tblBookISBN = '978-604-1-05678-9' LIMIT 1;

-- ============================================================
-- 8. BorrowedBookFine (at least 10 fines applied)
--    Linking fines to borrowed books
-- ============================================================

-- Fine for borrowing 2 (late 2 days)
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT 6000.00, bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow2
  AND f.name = 'Trả trễ 4-7 ngày'
LIMIT 1;

-- Fine for borrowing 7 (damaged)
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT 30000.00, bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow7
  AND f.name = 'Làm rách bìa'
LIMIT 1;

-- Fine for borrowing 8 (lost) – use special lost fine (price 150% of book)
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT (bb.price * 1.5), bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow8
  AND f.name = 'Mất sách giá cao'
LIMIT 1;

-- Fine for borrowing 9 (late 6 days)
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT 6000.00, bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow9
  AND f.name = 'Trả trễ 4-7 ngày'
LIMIT 1;

-- Additional fines for same borrowed books (multiple fine types per book)
-- Borrowing 2: also a 'bẩn sách' fine
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT 20000.00, bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow2
  AND f.name = 'Làm bẩn sách'
LIMIT 1;

-- Borrowing 7: also a 'mất tem mã vạch' fine
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT 15000.00, bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow7
  AND f.name = 'Mất tem mã vạch'
LIMIT 1;

-- Create fines for additional borrowed books to reach at least 10
-- Use a few extra ones on borrowings 1? (borrowing 1 had no fine, but we can add a small fine for no reason? better use real cases)
-- Borrowing 3 not returned yet → no fine. Borrowing 10 not returned → no fine.
-- Borrowing 4 overdue (not returned) → we can create a pending fine later, but not now.
-- To reach 10 fines, add fines for borrowing 9 (another fine) and maybe a dummy fine for borrowing 10? no.
-- Add for borrowing 4 when it becomes returned (simulate later). For now add a fine for borrowing 4's borrowed book as 'overdue fine expected'
-- But we can insert a fine for borrowing 9 with 'Trả trễ 1-3 ngày' extra (double fine)
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT 3000.00, bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow9
  AND f.name = 'Trả trễ 1-3 ngày'
LIMIT 1;

-- Add fine for borrowing 1? not realistic, but to meet count, add a very small "late notice" fee using a generic fine
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT 5000.00, bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow1
  AND f.name = 'Trả trễ 1-3 ngày'
LIMIT 1;   -- (just for dataset completeness, though book returned on time)

-- Add fine for borrowing 10? not needed because still borrowed. Use borrowing 5? pending, no actual return. Add for borrowing 4 (overdue) but no actual return yet, we can still record a fine for potential loss? skip.
-- To have exactly 10 fines, we have: 
-- borrow2 (2 fines), borrow7 (2 fines), borrow8 (1 fine), borrow9 (2 fines), borrow1 (1 fine) => total 8.
-- Add two more: one for borrow3 (not returned but we can add a "book damaged while borrowing"? unrealistic). Instead create a fine for a non-existent? Better add for borrow4 using "overdue notice fee"
-- Add a fine type 'Overdue processing fee' and apply to borrow4
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT 20000.00, bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow4
  AND f.name = 'Trả trễ >7 ngày'
LIMIT 1;

-- Add fine for borrow6? cancelled, not used. Use another from borrow2? already has 2. Use borrow5? pending no. Use borrow10? no.
-- Finally add a fine for borrow1 with another fine type 'Làm bẩn sách' to reach >10 (now total 11)
INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID)
SELECT 20000.00, bb.ID, f.ID
FROM tblBorrowedBook bb, tblFine f
WHERE bb.tblBorrowingID = @borrow1
  AND f.name = 'Làm bẩn sách'
LIMIT 1;

-- ============================================================
-- 9. Bills (at least 10) for borrowings that have fines or are completed
-- ============================================================

-- Bill for borrowing 1 (paid fine + normal)
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES (CURDATE(), 'Thanh toán tiền phạt bẩn sách và trả trễ (kỹ thuật)', 'Cash',
        @borrow1, (SELECT ID FROM tblUser WHERE username = 'librarian1'));

-- Bill for borrowing 2 (paid for late return + dirty)
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES ('2025-02-25', 'Thanh toán phạt trả trễ 2 ngày + bẩn sách', 'Transfer',
        @borrow2, (SELECT ID FROM tblUser WHERE username = 'librarian2'));

-- Bill for borrowing 7 (damaged book)
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES ('2025-03-01', 'Phạt làm rách bìa và mất tem', 'Cash',
        @borrow7, (SELECT ID FROM tblUser WHERE username = 'manager1'));

-- Bill for borrowing 8 (lost book – heavy fine)
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES ('2025-02-20', 'Bồi thường sách mất (150% giá)', 'Transfer',
        @borrow8, (SELECT ID FROM tblUser WHERE username = 'librarian2'));

-- Bill for borrowing 9 (late return)
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES ('2025-02-18', 'Phạt trả trễ 6 ngày và 3 ngày (kép)', 'Cash',
        @borrow9, (SELECT ID FROM tblUser WHERE username = 'librarian3'));

-- Bill for borrowing 4 (overdue processing fee, even though book not returned – admin fee)
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES ('2025-03-05', 'Phí xử lý quá hạn (sách chưa trả)', 'Cash',
        @borrow4, (SELECT ID FROM tblUser WHERE username = 'admin2'));

-- Additional bills to reach 10 (use borrowings that didn't have bills yet: borrow3 not returned, borrow5 pending, borrow6 cancelled -> not suitable)
-- Use borrow10 (still borrowed) but we can create a deposit bill? Unrealistic. Use a second bill for borrowing2? Better create a bill for a new borrowing? 
-- We'll add bills for borrowings that have no fines but for administrative fee (like borrowing 3 has no bill yet, but we can create a placeholder "guarantee fee")
-- Or add bill for borrowing 5 (pending) as prepayment? Not normal. For dataset completeness, add bill for borrowing 10 with 'Deposit for borrowed books'
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES (CURDATE(), 'Đặt cọc mượn 2 sách', 'Transfer',
        @borrow10, (SELECT ID FROM tblUser WHERE username = 'librarian4'));

-- Add another bill for borrowing 3 (borrowed but no return, maybe deposit)
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES (CURDATE(), 'Phí giữ sách', 'Cash',
        @borrow3, (SELECT ID FROM tblUser WHERE username = 'librarian5'));

-- Add two more to ensure total >=10 (now have bills for borrow1,2,7,8,9,4,10,3 = 8 bills). Need 2 more.
-- Use borrowing 5 (pending) for a "processing fee" bill
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES ('2025-03-06', 'Phí xử lý đơn mượn (pending)', 'Cash',
        @borrow5, (SELECT ID FROM tblUser WHERE username = 'librarian1'));

-- Use borrowing 6 (cancelled) for "cancellation fee" bill
INSERT INTO tblBill (paymentDate, note, paymentType, tblBorrowingID, tblUserID)
VALUES ('2025-02-16', 'Phí hủy phiếu mượn', 'Transfer',
        @borrow6, (SELECT ID FROM tblUser WHERE username = 'librarian2'));

-- ============================================================
-- Verify record counts (optional)
-- ============================================================
SELECT 'Data insertion completed' AS Status;
SELECT (SELECT COUNT(*) FROM tblStudent)  AS totalStudents,
       (SELECT COUNT(*) FROM tblUser)     AS totalUsers,
       (SELECT COUNT(*) FROM tblBook)     AS totalBooks,
       (SELECT COUNT(*) FROM tblBookItem) AS totalBookItems,
       (SELECT COUNT(*) FROM tblBorrowing) AS totalBorrowings,
       (SELECT COUNT(*) FROM tblBorrowedBook) AS totalBorrowedBooks,
       (SELECT COUNT(*) FROM tblFine) AS totalFines,
       (SELECT COUNT(*) FROM tblBorrowedBookFine) AS totalBorrowedBookFines,
       (SELECT COUNT(*) FROM tblBill) AS totalBills;