package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.StockStat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho StockStatDAO áp dụng kỹ thuật Phân vùng tương đương (EP) 
 * và Phân tích giá trị biên (BVA).
 * Lưu ý: Yêu cầu CSDL MySQL localhost:3306/p_documents đã chạy file schema.sql.
 *
 * Schema final không có bảng stock_reports.
 * StockStatDAO truy vấn tblBookItem.status ('damaged'/'lost') + updatedAt.
 */
class StockStatDAOTest {

    private StockStatDAO dao;

    @BeforeAll
    static void initDb() {
        DatabaseInitializer.init(true);
    }

    @BeforeEach
    void setUp() {
        dao = new StockStatDAO();
    }

    @Test
    void test_EP_D1_EP_R1_AllReasonsInRange() {
        // EP-D1: Khoảng thời gian bao trùm toàn bộ (01/01/2026 -> 31/05/2026)
        // EP-R1: Lý do = "Tất cả"
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Tất cả");

        assertEquals(4, result.size(), "Trả về đúng 4 bản ghi hư hỏng/thất lạc");
    }

    @Test
    void test_EP_R2_ReasonDamaged() {
        // EP-R2: Lý do = "Hư hỏng"
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Hư hỏng");

        assertEquals(2, result.size(), "Trả về 2 bản ghi hư hỏng");
        assertTrue(result.stream().allMatch(s -> s.getReason().equals("Hư hỏng")), "Tất cả phải có lý do 'Hư hỏng'");
    }

    @Test
    void test_EP_R3_ReasonLost() {
        // EP-R3: Lý do = "Thất lạc"
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Thất lạc");

        assertEquals(2, result.size(), "Trả về 2 bản ghi thất lạc");
        assertTrue(result.stream().allMatch(s -> s.getReason().equals("Thất lạc")), "Tất cả phải có lý do 'Thất lạc'");
    }

    @Test
    void test_BVA_D2_ToDateIsFirstReportDate() {
        // BVA-D2: Đến ngày = đúng ngày báo cáo sớm nhất (10/02/2026)
        // tblBookItem ID=3 (BC0003), updatedAt = 2026-02-10
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 10);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Tất cả");

        assertEquals(1, result.size(), "Chỉ trả về 1 dòng của ngày 10/02");
        assertEquals(3, result.get(0).getBook().getItems().get(0).getId());
    }

    @Test
    void test_BVA_D4_FromDateIsAfterLastReportDate() {
        // BVA-D4: Từ ngày = 1 ngày sau báo cáo muộn nhất (09/04/2026) -> Rỗng
        // tblBookItem ID=14, updatedAt = 2026-04-08
        LocalDate from = LocalDate.of(2026, 4, 9);
        LocalDate to = LocalDate.of(2026, 12, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Tất cả");

        assertTrue(result.isEmpty(), "Bảng phải rỗng do thời gian sau ngày báo cáo cuối");
    }

    @Test
    void test_EP_D3_PartialRange() {
        // EP-D3: Khoảng thời gian chỉ chứa 1 phần dữ liệu (01/03/2026 -> 31/05/2026)
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Tất cả");

        assertEquals(2, result.size(), "Chứa 2 dòng: ID=10 (15/03) và ID=14 (08/04)");
    }
}
