package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.StockStat;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy vấn sách hư hỏng / thất lạc và xuất PDF.
 * Schema final KHÔNG có bảng stock_reports.
 * Thay vào đó, dùng tblBookItem.status ('damaged'/'lost') và tblBookItem.updatedAt
 * làm ngày báo cáo (khi status bị cập nhật thành damaged/lost).
 *
 * Mapping hiển thị:
 *   'damaged' -> "Hư hỏng"
 *   'lost'    -> "Thất lạc"
 */
public class StockStatDAO extends DAO {

    /**
     * Tra cứu sách hư hỏng/thất lạc theo khoảng thời gian và lý do.
     * @param reason "Tất cả" | "Hư hỏng" | "Thất lạc"
     */
    public List<StockStat> searchDamageLossRecords(LocalDate from, LocalDate to, String reason) {
        List<StockStat> result = new ArrayList<>();
        boolean filterAll = reason == null || reason.equalsIgnoreCase("Tất cả");

        // Map lý do tiếng Việt -> giá trị ENUM tiếng Anh
        String statusFilter = null;
        if (!filterAll) {
            if ("Hư hỏng".equals(reason)) {
                statusFilter = "damaged";
            } else if ("Thất lạc".equals(reason)) {
                statusFilter = "lost";
            }
        }

        StringBuilder sql = new StringBuilder(
            "SELECT bi.ID AS itemId, bi.status AS item_status, bi.updatedAt AS reported_date, " +
            "       b.ISBN, b.title, b.author, b.genre " +
            "FROM tblBookItem bi " +
            "JOIN tblBook b ON b.ISBN = bi.tblBookISBN " +
            "WHERE bi.status IN ('damaged', 'lost') " +
            "AND bi.updatedAt BETWEEN ? AND ? "
        );
        if (!filterAll && statusFilter != null) {
            sql.append("AND bi.status = ? ");
        }
        sql.append("ORDER BY bi.updatedAt DESC");

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql.toString());
            ps.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(to.atTime(23, 59, 59)));
            if (!filterAll && statusFilter != null) {
                ps.setString(3, statusFilter);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                    rs.getString("ISBN"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre")
                );
                book.addItem(new BookItem(
                    rs.getInt("itemId"),
                    rs.getString("item_status")
                ));

                // Map status ENUM -> hiển thị tiếng Việt
                String statusEnum = rs.getString("item_status");
                String displayReason;
                if ("damaged".equals(statusEnum)) {
                    displayReason = "Hư hỏng";
                } else if ("lost".equals(statusEnum)) {
                    displayReason = "Thất lạc";
                } else {
                    displayReason = statusEnum;
                }

                Timestamp reportedTs = rs.getTimestamp("reported_date");
                StockStat ss = new StockStat(
                    book,
                    displayReason,
                    reportedTs == null ? null : reportedTs.toLocalDateTime().toLocalDate()
                );
                result.add(ss);
            }
        } catch (Exception e) {
            System.err.println("[StockStatDAO] searchDamageLossRecords lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
        }
        return result;
    }

    /**
     * Xuất danh sách báo cáo ra PDF.
     * Cột: Tên sách, Mã bản sách, Tình trạng, Ngày báo cáo.
     */
    public boolean exportToPDF(List<StockStat> rows, String filePath) {
        Document doc = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(filePath));
            doc.open();

            Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);

            Paragraph title = new Paragraph("BAO CAO SACH HU HONG / THAT LAC", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            doc.add(title);

            Paragraph meta = new Paragraph(
                "Ngay xuat: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                "    -    So dong: " + rows.size(), cellFont);
            meta.setSpacingAfter(10);
            doc.add(meta);

            PdfPTable table = new PdfPTable(new float[]{4, 2, 2, 2});
            table.setWidthPercentage(100);

            String[] cols = {"Ten sach", "Ma ban sach", "Tinh trang", "Ngay bao cao"};
            for (String h : cols) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(70, 130, 180));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (StockStat s : rows) {
                Book b = s.getBook();
                String titleText = b != null ? b.getTitle() : "";
                String itemId = b != null && !b.getItems().isEmpty()
                        ? String.valueOf(b.getItems().get(0).getId()) : "";
                table.addCell(new PdfPCell(new Phrase(titleText, cellFont)));
                table.addCell(new PdfPCell(new Phrase(itemId, cellFont)));
                table.addCell(new PdfPCell(new Phrase(s.getReason(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(
                    s.getReportedDate() == null ? "" : s.getReportedDate().format(df), cellFont)));
            }
            doc.add(table);
            return true;
        } catch (Exception e) {
            System.err.println("[StockStatDAO] exportToPDF lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (doc.isOpen()) doc.close();
        }
    }
}
