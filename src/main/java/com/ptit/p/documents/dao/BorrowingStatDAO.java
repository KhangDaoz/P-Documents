package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BorrowingStat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy vấn CSDL thống kê sách mượn nhiều và xuất Excel.
 * Truy vấn trên schema p_documents: tblBook, tblBookItem, tblBorrowedBook, tblBorrowing.
 * Dùng tblBorrowing.createdAt thay cho borrow_date.
 */
public class BorrowingStatDAO extends DAO {

    /**
     * Lấy danh sách top N đầu sách được mượn nhiều nhất trong khoảng [from, to].
     * Lọc theo tblBorrowing.createdAt (thay cho borrow_date không còn trong schema final).
     */
    public List<BorrowingStat> getTopBorrowedBooks(LocalDate from, LocalDate to, int topN) {
        List<BorrowingStat> result = new ArrayList<>();
        String sql =
            "SELECT b.ISBN, b.title, b.author, b.genre, COUNT(bb.ID) AS borrow_count " +
            "FROM tblBook b " +
            "JOIN tblBookItem bi    ON bi.tblBookISBN  = b.ISBN " +
            "JOIN tblBorrowedBook bb ON bb.tblBookItemID = bi.ID " +
            "JOIN tblBorrowing br   ON br.ID = bb.tblBorrowingID " +
            "WHERE br.createdAt BETWEEN ? AND ? " +
            "GROUP BY b.ISBN, b.title, b.author, b.genre " +
            "ORDER BY borrow_count DESC " +
            "LIMIT ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            // createdAt là TIMESTAMP, nên dùng Timestamp cho phạm vi ngày đầy đủ
            ps.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(to.atTime(23, 59, 59)));
            ps.setInt(3, topN);
            rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new BorrowingStat(
                    rs.getString("ISBN"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getInt("borrow_count")
                ));
            }
        } catch (Exception e) {
            System.err.println("[BorrowingStatDAO] getTopBorrowedBooks lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
        }
        return result;
    }

    /**
     * Xuất danh sách thống kê ra file .xlsx.
     * Cột: Mã sách, Tên sách, Tác giả, Thể loại, Lượt mượn.
     */
    public boolean exportToExcel(List<BorrowingStat> rows, String filePath) {
        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(filePath)) {

            Sheet sheet = wb.createSheet("Thống kê sách mượn nhiều");

            // Header
            Row header = sheet.createRow(0);
            String[] cols = {"Mã sách", "Tên sách", "Tác giả", "Thể loại", "Lượt mượn"};
            CellStyle headerStyle = wb.createCellStyle();
            Font bold = wb.createFont();
            bold.setBold(true);
            headerStyle.setFont(bold);
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            // Data
            int r = 1;
            for (BorrowingStat s : rows) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(s.getIsbn());
                row.createCell(1).setCellValue(s.getTitle());
                row.createCell(2).setCellValue(s.getAuthor());
                row.createCell(3).setCellValue(s.getGenre());
                row.createCell(4).setCellValue(s.getBorrowCount());
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
            return true;
        } catch (Exception e) {
            System.err.println("[BorrowingStatDAO] exportToExcel lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
