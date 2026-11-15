package com.tea.service;

import com.tea.util.DbUtil;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// 数据统计 Service：GROUP BY + ORDER BY + LIMIT
public class StatService {

    // 销量榜的一行数据（DTO record）
    public record SaleRank(String drinkName, int totalQty, BigDecimal totalAmount) {}

    // 营业额汇总
    public record MonthlySummary(String period, int orderCount,
                                 BigDecimal totalAmount, BigDecimal avgAmount) {}

    /**
     * 本月销量榜 TOP N
     *   SELECT d.name, SUM(oi.quantity), SUM(oi.subtotal)
     *   FROM order_item oi
     *   JOIN drink d ON d.id = oi.drink_id
     *   JOIN tea_order o ON o.id = oi.order_id
     *   WHERE o.created_at >= 本月初 AND o.status = 'PAID'
     *   GROUP BY d.id
     *   ORDER BY SUM(oi.quantity) DESC
     *   LIMIT N
     */
    public List<SaleRank> topSalesThisMonth(int topN) {
        String sql = "SELECT d.name AS drink_name, "
                   + "       SUM(oi.quantity)  AS total_qty, "
                   + "       SUM(oi.subtotal)  AS total_amount "
                   + "FROM order_item oi "
                   + "JOIN drink d      ON d.id = oi.drink_id "
                   + "JOIN tea_order o  ON o.id = oi.order_id "
                   + "WHERE o.status = 'PAID' "
                   + "  AND o.created_at >= DATE_FORMAT(NOW(), '%Y-%m-01') "
                   + "GROUP BY d.id, d.name "
                   + "ORDER BY total_qty DESC "
                   + "LIMIT ?";
        List<SaleRank> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, topN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SaleRank(
                            rs.getString("drink_name"),
                            rs.getInt("total_qty"),
                            rs.getBigDecimal("total_amount")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查销量榜失败", e);
        }
        return list;
    }

    /**
     * 本月营业额汇总
     *   SELECT COUNT(*), SUM(total_amount), AVG(total_amount)
     */
    public MonthlySummary monthlySummary() {
        String sql = "SELECT COUNT(*)               AS cnt, "
                   + "       IFNULL(SUM(total_amount), 0) AS sum_amt, "
                   + "       IFNULL(AVG(total_amount), 0) AS avg_amt, "
                   + "       DATE_FORMAT(NOW(), '%Y-%m') AS period "
                   + "FROM tea_order "
                   + "WHERE status = 'PAID' "
                   + "  AND created_at >= DATE_FORMAT(NOW(), '%Y-%m-01')";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new MonthlySummary(
                        rs.getString("period"),
                        rs.getInt("cnt"),
                        rs.getBigDecimal("sum_amt"),
                        rs.getBigDecimal("avg_amt"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查营业额失败", e);
        }
        return new MonthlySummary("-", 0, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
