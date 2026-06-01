package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.StockStat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockStatDAOTest {

    private StockStatDAO dao;

    @BeforeEach
    void setUp() {
        dao = new StockStatDAO();
    }

    @Test
    void test_EP_D1_EP_R1_AllReasonsInRange() {
        
        
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Tất cả");

        assertEquals(4, result.size(), "Trả về đúng 4 bản ghi hư hỏng/thất lạc");
    }

    @Test
    void test_EP_R2_ReasonDamaged() {
        
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Hư hỏng");

        assertEquals(2, result.size(), "Trả về 2 bản ghi hư hỏng");
        assertTrue(result.stream().allMatch(s -> s.getReason().equals("Hư hỏng")), "Tất cả phải có lý do 'Hư hỏng'");
    }

    @Test
    void test_EP_R3_ReasonLost() {
        
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Thất lạc");

        assertEquals(2, result.size(), "Trả về 2 bản ghi thất lạc");
        assertTrue(result.stream().allMatch(s -> s.getReason().equals("Thất lạc")), "Tất cả phải có lý do 'Thất lạc'");
    }

    @Test
    void test_BVA_D2_ToDateIsFirstReportDate() {
        
        
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 10);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Tất cả");

        assertEquals(1, result.size(), "Chỉ trả về 1 dòng của ngày 10/02");
        assertEquals(3, result.get(0).getBook().getItems().get(0).getId());
    }

    @Test
    void test_BVA_D4_FromDateIsAfterLastReportDate() {
        
        
        LocalDate from = LocalDate.of(2026, 4, 9);
        LocalDate to = LocalDate.of(2026, 12, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Tất cả");

        assertTrue(result.isEmpty(), "Bảng phải rỗng do thời gian sau ngày báo cáo cuối");
    }

    @Test
    void test_EP_D3_PartialRange() {
        
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        
        List<StockStat> result = dao.searchDamageLossRecords(from, to, "Tất cả");

        assertEquals(2, result.size(), "Chứa 2 dòng: ID=10 (15/03) và ID=14 (08/04)");
    }
}
