package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.BorrowedBook;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.ptit.p.documents.model.BorrowedBookFine;

/*
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
*/

public class BorrowedBookDAO extends DAO {

    public List<BorrowedBook> findByBorrowingId(int borrowingId) {
        List<BorrowedBook> results = new ArrayList<>();
        String sql = "SELECT bb.ID, bb.expectedReturnDate, bb.actualReturnDate, bb.status, bb.note, bb.price, "
                   + "bi.ID AS bookItemId, bi.status AS bookItemStatus, bi.tblBookISBN AS bookISBN "
                   + "FROM tblBorrowedBook bb "
                   + "JOIN tblBookItem bi ON bb.tblBookItemID = bi.ID "
                   + "WHERE bb.tblBorrowingID = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, borrowingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {

                    BookItem bookItem = new BookItem();
                    bookItem.setId(resultSet.getInt("bookItemId"));
                    bookItem.setStatus(resultSet.getString("bookItemStatus"));

                    BorrowedBook borrowedBook = new BorrowedBook(
                        resultSet.getInt("ID"),
                        toLocalDate(resultSet.getDate("expectedReturnDate")),
                        toLocalDate(resultSet.getDate("actualReturnDate")),
                        resultSet.getString("status"),
                        resultSet.getString("note"),
                        resultSet.getDouble("price"),
                        bookItem,
                        new ArrayList<>()
                    );
                    // borrowedBook.setId(resultSet.getInt("ID"));
                    // borrowedBook.setExpectedReturnDate(toLocalDate(resultSet.getDate("expectedReturnDate")));
                    // borrowedBook.setActualReturnDate(toLocalDate(resultSet.getDate("actualReturnDate")));
                    // borrowedBook.setStatus(resultSet.getString("status"));
                    // borrowedBook.setNote(resultSet.getString("note"));
                    // borrowedBook.setPrice(resultSet.getDouble("price"));


                    results.add(borrowedBook);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return results;
    }

    public boolean updateReturnStatus(BorrowedBook bb) {
        String sql = "UPDATE tblBorrowedBook SET actualReturnDate = ?, status = ?, note = ?, price = ? WHERE ID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, bb.getActualReturnDate() != null ? Date.valueOf(bb.getActualReturnDate()) : null);
            statement.setString(2, bb.getStatus() != null ? bb.getStatus() : "good");
            statement.setString(3, bb.getNote());
            statement.setObject(4, bb.getPrice() != 0.0 ? bb.getPrice() : null);
            statement.setInt(5, bb.getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean setBorrowedBookFine(BorrowedBookFine fine, BorrowedBook bb) {
        String sql = "INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, fine.getFineRate());
            statement.setInt(2, bb.getId());
            statement.setInt(3, fine.getFine().getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private LocalDate toLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }
}
