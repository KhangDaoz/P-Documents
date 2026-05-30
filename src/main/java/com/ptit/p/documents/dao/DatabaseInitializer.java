package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.DriverManager;
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
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(SERVER_URL, DB_USER, DB_PASSWORD);
            Statement st = con.createStatement();

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
                + " ('SV220002', 'Pham Thi Binh','pham.binh@student.edu.vn',  '0934567890', 'Ha Noi')"
            );
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM tblUser");
        rs.next();
        if (rs.getInt(1) == 0) {
            st.executeUpdate(
                "INSERT INTO tblUser (username, password, fullName, phone, role) VALUES"
                + " ('admin',      '123456', 'Nguyen Van Admin', '0901234567', 'admin'),"
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
                + " ('978-604-1-09012-3', 'Giai Tich Toan Hoc',   'Le Dinh Phuong', 'Mathematics', 'NXB Giao Duc',  2021, 75000.00, 'Tai lieu giai tich toan hoc')"
            );
            st.executeUpdate(
                "INSERT INTO tblBookItem (status, tblBookISBN) VALUES"
                + " ('good', '978-604-1-01234-5'),"
                + " ('good', '978-604-1-01234-5'),"
                + " ('good', '978-604-1-01234-5'),"
                + " ('good', '978-604-1-05678-9'),"
                + " ('good', '978-604-1-05678-9')"
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
    }
}
