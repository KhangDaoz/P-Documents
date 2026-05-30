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
                + " ('SV001', 'Do Huy Hoang',     'hoang@ptit.edu.vn', '0911111111', 'Hanoi'),"
                + " ('SV002', 'Nguyen Minh Kien',  'kien@ptit.edu.vn',  '0922222222', 'Hanoi'),"
                + " ('SV003', 'Vu Minh Sang',      'sang@ptit.edu.vn',  '0933333333', 'Hanoi'),"
                + " ('SV004', 'Le Duc Hieu',       'hieu@ptit.edu.vn',  '0944444444', 'Hanoi'),"
                + " ('SV005', 'Tran Dac Manh',     'manh@ptit.edu.vn',  '0955555555', 'Hanoi')"
            );
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM tblUser");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblUser (username, password, fullName, phone, role) VALUES"
                + " ('admin',      '123456', 'Nguyễn Văn Admin',  '0901234567', 'admin'),"
                + " ('manager',    '123456', 'Nguyễn Văn Manager', '0901234567', 'manager'),"
                + " ('librarian1', '123456', 'Trần Thị Thư',       '0912345678', 'librarian')"
            );
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM tblBook");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblBook (ISBN, title, author, genre, publisher, publishYear, price, description) VALUES"
                + " ('ISBN-CS-01', 'Introduction to Software Engineering', 'John Smith',            'Computer Science', 'MIT Press',       2022, 150000.00, 'Essential SE concepts'),"
                + " ('ISBN-CS-02', 'Data Structures and Algorithms',       'Alice Johnson',          'Computer Science', 'Oxford',          2021, 180000.00, 'Data structures in Java'),"
                + " ('ISBN-CS-03', 'Database Systems Concepts',            'Abraham Silberschatz',   'Computer Science', 'McGraw-Hill',      2020, 250000.00, 'Comprehensive database guide'),"
                + " ('ISBN-CS-04', 'Test Delete Book',                      'Test Author',            'Computer Science', 'Test Publisher',    2024, 100000.00, 'For testing delete'),"
                + " ('ISBN-LIT-01', 'To Kill a Mockingbird',                'Harper Lee',             'Literature',       'J.B. Lippincott',   1960,  90000.00, 'Classic literature'),"
                + " ('ISBN-LIT-02', 'The Great Gatsby',                     'F. Scott Fitzgerald',    'Literature',       'Scribner',          1925,  80000.00, 'American classic'),"
                + " ('ISBN-SCI-01', 'A Brief History of Time',             'Stephen Hawking',        'Science',          'Bantam Books',      1988, 120000.00, 'Cosmology for everyone')"
            );
            st.executeUpdate(
                "INSERT INTO tblBookItem (ID, status, tblBookISBN, createdAt, updatedAt) VALUES"
                + " (1,  'good',    'ISBN-CS-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (2,  'good',    'ISBN-CS-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (3,  'damaged', 'ISBN-CS-01', '2025-12-01 00:00:00', '2026-02-10 00:00:00'),"
                + " (4,  'good',    'ISBN-CS-02', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (5,  'good',    'ISBN-CS-02', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (6,  'lost',    'ISBN-CS-02', '2025-12-01 00:00:00', '2026-02-20 00:00:00'),"
                + " (7,  'good',    'ISBN-LIT-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (8,  'good',    'ISBN-LIT-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (9,  'good',    'ISBN-LIT-02', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (10, 'damaged', 'ISBN-LIT-02', '2025-12-01 00:00:00', '2026-03-15 00:00:00'),"
                + " (11, 'good',    'ISBN-SCI-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (12, 'good',    'ISBN-SCI-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (13, 'good',    'ISBN-SCI-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (14, 'lost',    'ISBN-SCI-01', '2025-12-01 00:00:00', '2026-04-08 00:00:00'),"
                + " (15, 'good',    'ISBN-CS-04', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (16, 'good',    'ISBN-CS-04', '2025-12-01 00:00:00', '2025-12-01 00:00:00'),"
                + " (17, 'good',    'ISBN-CS-01', '2025-12-01 00:00:00', '2025-12-01 00:00:00')"
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
                + " ( 1, 'SV001', 3, 'borrowed', '2026-01-10 00:00:00', '2026-01-10 00:00:00'),"
                + " ( 2, 'SV002', 3, 'borrowed', '2026-01-15 00:00:00', '2026-01-15 00:00:00'),"
                + " ( 3, 'SV003', 3, 'borrowed', '2026-02-01 00:00:00', '2026-02-01 00:00:00'),"
                + " ( 4, 'SV004', 3, 'borrowed', '2026-02-12 00:00:00', '2026-02-12 00:00:00'),"
                + " ( 5, 'SV005', 3, 'borrowed', '2026-03-05 00:00:00', '2026-03-05 00:00:00'),"
                + " ( 6, 'SV001', 3, 'borrowed', '2026-03-20 00:00:00', '2026-03-20 00:00:00'),"
                + " ( 7, 'SV001', 3, 'borrowed', '2026-04-02 00:00:00', '2026-04-02 00:00:00'),"
                + " ( 8, 'SV002', 3, 'borrowed', '2026-04-15 00:00:00', '2026-04-15 00:00:00'),"
                + " ( 9, 'SV003', 3, 'borrowed', '2026-04-28 00:00:00', '2026-04-28 00:00:00'),"
                + " (10, 'SV004', 3, 'borrowed', '2026-05-10 00:00:00', '2026-05-10 00:00:00')"
            );
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM tblBorrowedBook");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblBorrowedBook (tblBorrowingID, tblBookItemID, expectedReturnDate, actualReturnDate, status) VALUES"
                + " ( 1,  1, '2026-01-24', '2026-01-22', 'good'),"
                + " ( 2,  2, '2026-01-29', '2026-01-30', 'good'),"
                + " ( 2,  4, '2026-01-29', '2026-01-28', 'good'),"
                + " ( 3,  1, '2026-02-15', '2026-02-14', 'good'),"
                + " ( 3,  7, '2026-02-15', '2026-02-15', 'good'),"
                + " ( 4,  2, '2026-02-26', NULL,         'good'),"
                + " ( 4,  9, '2026-02-26', '2026-02-28', 'good'),"
                + " ( 5,  1, '2026-03-19', '2026-03-18', 'good'),"
                + " ( 5,  4, '2026-03-19', '2026-03-20', 'good'),"
                + " ( 6,  2, '2026-04-03', '2026-04-05', 'good'),"
                + " ( 6, 11, '2026-04-03', '2026-04-02', 'good'),"
                + " ( 7,  1, '2026-04-16', NULL,         'good'),"
                + " ( 7, 12, '2026-04-16', NULL,         'good'),"
                + " ( 8,  4, '2026-04-29', NULL,         'good'),"
                + " ( 8, 11, '2026-04-29', '2026-04-30', 'good'),"
                + " ( 9,  7, '2026-05-12', NULL,         'good'),"
                + " (10,  9, '2026-05-24', NULL,         'good')"
            );
        }
    }
}
