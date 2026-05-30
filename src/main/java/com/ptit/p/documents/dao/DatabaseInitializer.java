package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Khởi tạo database p_documents khi ứng dụng chạy lần đầu.
 *
 * Kết nối không chỉ định database (chỉ đến MySQL server) để tạo
 * database nếu chưa có, sau đó tạo các bảng theo schema p_documents.
 *
 * Gọi DatabaseInitializer.init() ở đầu main() trước khi mở bất kỳ DAO nào.
 */
public class DatabaseInitializer {

    // ---- Kết nối đến MySQL SERVER (không chỉ định DB) ----
    private static final String SERVER_URL =
            "jdbc:mysql://localhost:3306"
            + "?useSSL=false"
            + "&serverTimezone=UTC"
            + "&characterEncoding=UTF-8"
            + "&allowPublicKeyRetrieval=true";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "1812";

    /**
     * Tạo database và tất cả bảng nếu chưa tồn tại.
     * An toàn để gọi nhiều lần — dùng IF NOT EXISTS.
     *
     * @return true nếu thành công, false nếu có lỗi
     */
    public static boolean init() {
        return init(false);
    }

    public static boolean init(boolean dropFirst) {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try {
                con = DriverManager.getConnection(SERVER_URL, DB_USER, "1812");
            } catch (SQLException ex) {
                System.out.println("[DatabaseInitializer] Kết nối với mật khẩu '1812' thất bại. Đang thử mật khẩu '123456'...");
                con = DriverManager.getConnection(SERVER_URL, DB_USER, "123456");
            }
            Statement st = con.createStatement();

            if (dropFirst) {
                st.executeUpdate("DROP DATABASE IF EXISTS p_documents");
                System.out.println("[DatabaseInitializer] Đã xóa database cũ p_documents.");
            }

            // 1. Tạo database
            st.executeUpdate(
                "CREATE DATABASE IF NOT EXISTS p_documents"
                + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
            );
            st.executeUpdate("USE p_documents");

            // 2. tblStudent
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tblStudent ("
                + "  ID        VARCHAR(20)  NOT NULL,"
                + "  fullName  VARCHAR(255) NOT NULL,"
                + "  email     VARCHAR(255) NOT NULL,"
                + "  phone     VARCHAR(15)  NOT NULL,"
                + "  address   VARCHAR(255),"
                + "  createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (ID)"
                + ") ENGINE=InnoDB"
            );

            // 3. tblUser
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tblUser ("
                + "  ID        INT(10)      NOT NULL AUTO_INCREMENT,"
                + "  username  VARCHAR(255) NOT NULL,"
                + "  password  VARCHAR(255) NOT NULL,"
                + "  fullName  VARCHAR(255) NOT NULL,"
                + "  phone     VARCHAR(15)  NOT NULL,"
                + "  role      ENUM('admin','librarian','manager') NOT NULL DEFAULT 'librarian',"
                + "  createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (ID)"
                + ") ENGINE=InnoDB"
            );

            // 4. tblBook
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tblBook ("
                + "  ISBN        VARCHAR(20)  NOT NULL,"
                + "  title       VARCHAR(255) NOT NULL,"
                + "  author      VARCHAR(255) NOT NULL,"
                + "  genre       VARCHAR(255) NOT NULL,"
                + "  publisher   VARCHAR(255) NOT NULL,"
                + "  publishYear INT(10)      NOT NULL,"
                + "  price       DECIMAL(10,2) NOT NULL,"
                + "  description VARCHAR(255) NOT NULL,"
                + "  createdAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  updatedAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (ISBN)"
                + ") ENGINE=InnoDB"
            );

            // 5. tblBookItem
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tblBookItem ("
                + "  ID          INT(10) NOT NULL AUTO_INCREMENT,"
                + "  status      ENUM('good','damaged','lost') NOT NULL DEFAULT 'good',"
                + "  tblBookISBN VARCHAR(20) NOT NULL,"
                + "  createdAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  updatedAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (ID),"
                + "  CONSTRAINT fk_bookitem_book"
                + "    FOREIGN KEY (tblBookISBN) REFERENCES tblBook(ISBN)"
                + "    ON UPDATE CASCADE ON DELETE RESTRICT"
                + ") ENGINE=InnoDB"
            );

            // 6. tblBorrowing
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tblBorrowing ("
                + "  ID                  INT(10) NOT NULL AUTO_INCREMENT,"
                + "  expectedReceiveDate DATE,"
                + "  actualReceiveDate   DATE,"
                + "  note                VARCHAR(255),"
                + "  status              ENUM('borrowed','returned','overdue','pending','cancelled') NOT NULL DEFAULT 'pending',"
                + "  tblStudentID        VARCHAR(20) NOT NULL,"
                + "  tblUserID           INT(10) NOT NULL,"
                + "  createdAt           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  updatedAt           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (ID),"
                + "  CONSTRAINT fk_borrowing_student"
                + "    FOREIGN KEY (tblStudentID) REFERENCES tblStudent(ID)"
                + "    ON UPDATE CASCADE ON DELETE RESTRICT,"
                + "  CONSTRAINT fk_borrowing_user"
                + "    FOREIGN KEY (tblUserID) REFERENCES tblUser(ID)"
                + "    ON UPDATE CASCADE ON DELETE RESTRICT"
                + ") ENGINE=InnoDB"
            );

            // 7. tblBorrowedBook
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tblBorrowedBook ("
                + "  ID                 INT(10) NOT NULL AUTO_INCREMENT,"
                + "  expectedReturnDate DATE NOT NULL,"
                + "  actualReturnDate   DATE,"
                + "  status             ENUM('lost','damaged','good') NOT NULL DEFAULT 'good',"
                + "  note               VARCHAR(255),"
                + "  price              DECIMAL(10,2),"
                + "  tblBookItemID      INT(10) NOT NULL,"
                + "  tblBorrowingID     INT(10) NOT NULL,"
                + "  createdAt          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  updatedAt          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (ID),"
                + "  CONSTRAINT fk_borrowedbook_bookitem"
                + "    FOREIGN KEY (tblBookItemID) REFERENCES tblBookItem(ID)"
                + "    ON UPDATE CASCADE ON DELETE RESTRICT,"
                + "  CONSTRAINT fk_borrowedbook_borrowing"
                + "    FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID)"
                + "    ON UPDATE CASCADE ON DELETE RESTRICT"
                + ") ENGINE=InnoDB"
            );

            // 8. tblFine
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tblFine ("
                + "  ID          INT(10) NOT NULL AUTO_INCREMENT,"
                + "  name        VARCHAR(255) NOT NULL,"
                + "  fineRate    DECIMAL(10,2) NOT NULL,"
                + "  description VARCHAR(255),"
                + "  createdAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  updatedAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (ID)"
                + ") ENGINE=InnoDB"
            );

            // 9. tblBorrowedBookFine
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tblBorrowedBookFine ("
                + "  ID                INT(10)       NOT NULL AUTO_INCREMENT,"
                + "  fineRate          DECIMAL(10,2) NOT NULL,"
                + "  tblBorrowedBookID INT(10)       NOT NULL,"
                + "  tblFineID         INT(10)       NOT NULL,"
                + "  createdAt         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  updatedAt         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (ID),"
                + "  CONSTRAINT fk_bbfine_borrowedbook"
                + "    FOREIGN KEY (tblBorrowedBookID) REFERENCES tblBorrowedBook(ID)"
                + "    ON UPDATE CASCADE ON DELETE RESTRICT,"
                + "  CONSTRAINT fk_bbfine_fine"
                + "    FOREIGN KEY (tblFineID) REFERENCES tblFine(ID)"
                + "    ON UPDATE CASCADE ON DELETE RESTRICT"
                + ") ENGINE=InnoDB"
            );

            // 10. tblBill
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tblBill ("
                + "  ID             INT(10)      NOT NULL AUTO_INCREMENT,"
                + "  paymentDate    DATE         NOT NULL,"
                + "  note           VARCHAR(255),"
                + "  paymentType    VARCHAR(255),"
                + "  tblBorrowingID INT(10)      NOT NULL,"
                + "  tblUserID      INT(10)      NOT NULL,"
                + "  createdAt      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  updatedAt      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (ID),"
                + "  CONSTRAINT fk_bill_borrowing"
                + "    FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID)"
                + "    ON UPDATE CASCADE ON DELETE RESTRICT,"
                + "  CONSTRAINT fk_bill_user"
                + "    FOREIGN KEY (tblUserID) REFERENCES tblUser(ID)"
                + "    ON UPDATE CASCADE ON DELETE RESTRICT"
                + ") ENGINE=InnoDB"
            );

            // ---- Seed data mẫu (chỉ insert nếu bảng rỗng) ----
            insertSeedDataIfEmpty(st);

            st.close();
            con.close();
            System.out.println("[DatabaseInitializer] Database p_documents sẵn sàng.");
            return true;

        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Lỗi khởi tạo database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void insertSeedDataIfEmpty(Statement st) throws Exception {
        java.sql.ResultSet rs;

        rs = st.executeQuery("SELECT COUNT(*) FROM tblStudent");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblStudent (ID, fullName, email, phone, address) VALUES"
                + " ('SV220001', 'Le Van An',    'levan.an@student.edu.vn',   '0923456789', 'Ha Noi'),"
                + " ('SV220002', 'Pham Thi Binh','pham.binh@student.edu.vn',  '0934567890', 'Ha Noi'),"
                // Seed data from sang branch
                + " ('1', 'Nguyễn Văn An',  'nguyen.an@student.edu.vn',  '0923456789', 'Hà Nội'),"
                + " ('2', 'Trần Thị Bình',  'tran.binh@student.edu.vn',  '0934567890', 'Hà Nội'),"
                + " ('3', 'Lê Văn Cường',   'le.cuong@student.edu.vn',   '0945678901', 'Đà Nẵng'),"
                + " ('4', 'Phạm Thị Dung',  'pham.dung@student.edu.vn',  '0956789012', 'TP.HCM'),"
                + " ('5', 'Hoàng Văn Em',   'hoang.em@student.edu.vn',   '0967890123', 'Hải Phòng'),"
                + " ('6', 'Vũ Minh Sáng',   'vu.sang@student.edu.vn',    '0978901234', 'Hà Nội')"
            );
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM tblUser");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblUser (username, password, fullName, phone, role) VALUES"
                + " ('admin',      '123456', 'Nguyen Van Admin', '0901234567', 'admin'),"
                + " ('manager',    '123456', 'Nguyen Van Manager', '0901234567', 'manager'),"
                + " ('librarian1', '123456', 'Tran Thi Thu',     '0912345678', 'librarian')"
            );
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM tblBook");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblBook (ISBN, title, author, genre, publisher, publishYear, price, description) VALUES"
                + " ('978-604-1-01234-5', 'Lap Trinh C Can Ban', 'Nguyen Phu Quy', 'Technology',  'NXB Thong Tin', 2020, 85000.00, 'Sach nhap mon lap trinh C'),"
                + " ('978-604-1-05678-9', 'Co So Du Lieu',        'Tran Minh Tung', 'Technology',  'NXB DHQG',      2019, 95000.00, 'Giao trinh co so du lieu'),"
                + " ('978-604-1-09012-3', 'Giai Tich Toan Hoc',   'Le Dinh Phuong', 'Mathematics', 'NXB Giao Duc',  2021, 75000.00, 'Tai lieu giai tich toan hoc'),"
                // Seed data from sang branch
                + " ('B001', 'Nhà Giả Kim',                    'Paulo Coelho',                'Tiểu thuyết',         'NXB Văn Học',  2020, 89000.00, 'Tiểu thuyết nổi tiếng thế giới'),"
                + " ('B002', 'Đắc Nhân Tâm',                   'Dale Carnegie',               'Kỹ năng sống',        'NXB Tổng Hợp', 2019, 76000.00, 'Sách kỹ năng giao tiếp kinh điển'),"
                + " ('B003', 'Tuổi Trẻ Đáng Giá Bao Nhiêu',    'Rosie Nguyễn',                'Kỹ năng sống',        'NXB Hội Nhà Văn', 2018, 65000.00, 'Sách dành cho giới trẻ'),"
                + " ('B004', 'Sapiens',                          'Yuval Noah Harari',           'Lịch sử',             'NXB Tri Thức', 2021, 120000.00, 'Lược sử loài người'),"
                + " ('B005', 'Clean Code',                       'Robert C. Martin',            'Công nghệ thông tin', 'Prentice Hall', 2008, 350000.00, 'Cẩm nang viết code sạch'),"
                + " ('B006', 'Cây Cam Ngọt Của Tôi',            'José Mauro de Vasconcelos',   'Tiểu thuyết',         'NXB Hội Nhà Văn', 2020, 68000.00, 'Tiểu thuyết Brazil cảm động'),"
                + " ('B007', 'Tôi Tài Giỏi, Bạn Cũng Thế',     'Adam Khoo',                   'Kỹ năng sống',        'NXB Phụ Nữ',  2017, 72000.00, 'Sách phát triển bản thân')"
            );
            st.executeUpdate(
                "INSERT INTO tblBookItem (ID, status, tblBookISBN, createdAt, updatedAt) VALUES"
                // Seed data from sang branch (IDs 1 to 14)
                + " (1,  'good',    'B001', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (2,  'good',    'B001', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (3,  'damaged', 'B001', '2025-12-01 00:00:00', '2026-02-10 00:00:00'),"
                + " (4,  'good',    'B002', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (5,  'good',    'B002', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (6,  'lost',    'B002', '2025-12-01 00:00:00', '2026-02-20 00:00:00'),"
                + " (7,  'good',    'B003', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (8,  'good',    'B003', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (9,  'good',    'B004', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (10, 'damaged', 'B004', '2025-12-01 00:00:00', '2026-03-15 00:00:00'),"
                + " (11, 'good',    'B005', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (12, 'good',    'B005', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (13, 'good',    'B006', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (14, 'lost',    'B007', '2025-12-01 00:00:00', '2026-04-08 00:00:00'),"
                // Seed data from huy branch (IDs 15 to 19)
                + " (15, 'good', '978-604-1-01234-5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),"
                + " (16, 'good', '978-604-1-01234-5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),"
                + " (17, 'good', '978-604-1-01234-5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),"
                + " (18, 'good', '978-604-1-05678-9', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),"
                + " (19, 'good', '978-604-1-05678-9', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
            );
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM tblFine");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblFine (name, fineRate, description) VALUES"
                + " ('Tra tre',       5000.00,  'Phat 5.000d/ngay tra tre'),"
                + " ('Mat sach',    500000.00,  'Phat mat sach bang gia tri sach'),"
                + " ('Hu hong sach', 50000.00,  'Phat hu hong tuy muc do')"
            );
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM tblBorrowing");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblBorrowing (ID, tblStudentID, tblUserID, status, createdAt, updatedAt) VALUES"
                + " ( 1, '1', 1, 'borrowed', '2026-01-10 00:00:00', '2026-01-10 00:00:00'),"
                + " ( 2, '2', 1, 'borrowed', '2026-01-15 00:00:00', '2026-01-15 00:00:00'),"
                + " ( 3, '3', 1, 'borrowed', '2026-02-01 00:00:00', '2026-02-01 00:00:00'),"
                + " ( 4, '4', 1, 'borrowed', '2026-02-12 00:00:00', '2026-02-12 00:00:00'),"
                + " ( 5, '5', 1, 'borrowed', '2026-03-05 00:00:00', '2026-03-05 00:00:00'),"
                + " ( 6, '1', 1, 'borrowed', '2026-03-20 00:00:00', '2026-03-20 00:00:00'),"
                + " ( 7, '6', 1, 'borrowed', '2026-04-02 00:00:00', '2026-04-02 00:00:00'),"
                + " ( 8, '2', 1, 'borrowed', '2026-04-15 00:00:00', '2026-04-15 00:00:00'),"
                + " ( 9, '3', 1, 'borrowed', '2026-04-28 00:00:00', '2026-04-28 00:00:00'),"
                + " (10, '4', 1, 'borrowed', '2026-05-10 00:00:00', '2026-05-10 00:00:00')"
            );
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM tblBorrowedBook");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblBorrowedBook (tblBorrowingID, tblBookItemID, expectedReturnDate, actualReturnDate, status) VALUES"
                + " ( 1,  1, '2026-01-24', '2026-01-22', 'good'),"
                + " ( 2,  2, '2026-01-29', '2026-01-30', 'good'),"
                + " ( 3,  1, '2026-02-15', '2026-02-14', 'good'),"
                + " ( 4,  2, '2026-02-26', NULL,         'good'),"
                + " ( 5,  1, '2026-03-19', '2026-03-18', 'good'),"
                + " ( 6,  2, '2026-04-03', '2026-04-05', 'good'),"
                + " ( 7,  1, '2026-04-16', NULL,         'good'),"
                + " ( 2,  4, '2026-01-29', '2026-01-28', 'good'),"
                + " ( 5,  5, '2026-03-19', '2026-03-20', 'good'),"
                + " ( 8,  4, '2026-04-29', NULL,         'good'),"
                + " ( 3,  7, '2026-02-15', '2026-02-15', 'good'),"
                + " ( 9,  8, '2026-05-12', NULL,         'good'),"
                + " ( 4,  9, '2026-02-26', '2026-02-28', 'good'),"
                + " (10,  9, '2026-05-24', NULL,         'good'),"
                + " ( 6, 11, '2026-04-03', '2026-04-02', 'good'),"
                + " ( 7, 12, '2026-04-16', NULL,         'good'),"
                + " ( 8, 13, '2026-04-29', '2026-04-30', 'good')"
            );
        }
    }
}
