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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy vấn sách hư hỏng / thất lạc và xuất PDF
 * (spec §2.a: StockStatDAO - searchDamageLossRecords(), exportToPDF()).
 */
public class StockStatDAO extends DAO {

    /**
     * Tra cứu báo cáo theo khoảng thời gian và lý do.
     * @param reason "Tất cả" | "Hư hỏng" | "Thất lạc" (spec §2.b bước 11)
     */
    public List<StockStat> searchDamageLossRecords(LocalDate from, LocalDate to, String reason) {
        List<StockStat> result = new ArrayList<>();
        boolean filterAll = reason == null || reason.equalsIgnoreCase("Tất cả");

        StringBuilder sql = new StringBuilder(
            "SELECT sr.id, sr.reason, sr.reported_date, " +
            "       bi.barcode, bi.status AS item_status, " +
            "       b.book_id, b.title, b.author, b.category " +
            "FROM stock_reports sr " +
            "JOIN book_items bi ON bi.barcode = sr.barcode " +
            "JOIN books b       ON b.book_id  = bi.book_id " +
            "WHERE sr.reported_date BETWEEN ? AND ? "
        );
        if (!filterAll) sql.append("AND sr.reason = ? ");
        sql.append("ORDER BY sr.reported_date DESC");

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql.toString());
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            if (!filterAll) ps.setString(3, reason);
            rs = ps.executeQuery();
            while (rs.next()) {
                // Spec §2.b bước 15-18: StockStat -> Book -> BookItem
                Book book = new Book(
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category")
                );
                book.addItem(new BookItem(
                    rs.getString("barcode"),
                    rs.getString("item_status")
                ));
                StockStat ss = new StockStat(
                    book,
                    rs.getString("reason"),
                    rs.getDate("reported_date").toLocalDate()
                );
                result.add(ss);
            }
        } catch (Exception e) {
            System.err.println("[StockStatDAO] searchDamageLossRecords lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close(ps, rs);
        }
        return result;
    }

    /**
     * Xuất danh sách báo cáo ra PDF (spec §2.b bước 22-27).
     * Cột: Tên sách, Mã vạch, Tình trạng (theo bước 21).
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

            String[] cols = {"Ten sach", "Ma vach", "Tinh trang", "Ngay bao cao"};
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
                String barcode = b != null && !b.getItems().isEmpty()
                        ? b.getItems().get(0).getId() : "";
                table.addCell(new PdfPCell(new Phrase(titleText, cellFont)));
                table.addCell(new PdfPCell(new Phrase(barcode, cellFont)));
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
