package com.library.dao;

import com.library.model.BorrowingStat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy vấn CSDL thống kê sách mượn nhiều và xuất Excel
 * (spec §1.a: BorrowingStatDAO - getTopBorrowedBooks(), exportToExcel()).
 */
public class BorrowingStatDAO extends DAO {

    /**
     * Lấy danh sách top N đầu sách được mượn nhiều nhất trong khoảng [from, to].
     * Tương ứng spec §1.b bước 14-19.
     */
    public List<BorrowingStat> getTopBorrowedBooks(LocalDate from, LocalDate to, int topN) {
        List<BorrowingStat> result = new ArrayList<>();
        String sql =
            "SELECT b.book_id, b.title, b.author, b.category, COUNT(bb.id) AS borrow_count " +
            "FROM books b " +
            "JOIN book_items bi    ON bi.book_id  = b.book_id " +
            "JOIN borrowed_books bb ON bb.barcode  = bi.barcode " +
            "JOIN borrowings br    ON br.borrow_id = bb.borrow_id " +
            "WHERE br.borrow_date BETWEEN ? AND ? " +
            "GROUP BY b.book_id, b.title, b.author, b.category " +
            "ORDER BY borrow_count DESC " +
            "LIMIT ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ps.setInt(3, topN);
            rs = ps.executeQuery();
            while (rs.next()) {
                // Đóng gói kết quả vào BorrowingStat (kế thừa Book) - spec §1.b bước 15-18
                result.add(new BorrowingStat(
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    rs.getInt("borrow_count")
                ));
            }
        } catch (Exception e) {
            System.err.println("[BorrowingStatDAO] getTopBorrowedBooks lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close(ps, rs);
        }
        return result;
    }

    /**
     * Xuất danh sách thống kê ra file .xlsx (spec §1.b bước 38-42).
     * Cột: Mã sách, Tên sách, Tác giả, Thể loại, Lượt mượn (theo bước 20).
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
                row.createCell(0).setCellValue(s.getBookId());
                row.createCell(1).setCellValue(s.getTitle());
                row.createCell(2).setCellValue(s.getAuthor());
                row.createCell(3).setCellValue(s.getCategory());
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
