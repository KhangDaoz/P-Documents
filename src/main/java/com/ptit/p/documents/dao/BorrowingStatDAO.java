package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
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

public class BorrowingStatDAO extends DAO {

    
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
            ps = getCon().prepareStatement(sql);
            
            ps.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(to.atTime(23, 59, 59)));
            ps.setInt(3, topN);
            rs = ps.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                    rs.getString("ISBN"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre")
                );
                result.add(new BorrowingStat(book, rs.getInt("borrow_count")));
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

    
    public boolean exportToExcel(List<BorrowingStat> rows, String filePath) {
        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(filePath)) {

            Sheet sheet = wb.createSheet("Thống kê sách mượn nhiều");

            
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
