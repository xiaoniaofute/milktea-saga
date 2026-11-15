
package com.tea.service;

import com.tea.dao.DrinkDao;
import com.tea.dao.MemberDao;
import com.tea.dao.OrderItemDao;
import com.tea.dao.TeaOrderDao;
import com.tea.entity.OrderItem;
import com.tea.entity.TeaOrder;
import com.tea.util.DbUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {

    public record PlaceOrderResult(Long orderId, BigDecimal totalAmount, Integer pointsAfter) {}

    private final TeaOrderDao orderDao = new TeaOrderDao();
    private final OrderItemDao itemDao = new OrderItemDao();
    private final DrinkDao drinkDao = new DrinkDao();
    private final MemberDao memberDao = new MemberDao();

    public PlaceOrderResult placeOrder(Long memberId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("订单不能为空");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem it : items) {
            total = total.add(it.getSubtotal());
        }

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                TeaOrder order = new TeaOrder();
                order.setMemberId(memberId);
                order.setTotalAmount(total);
                order.setStatus("PAID");
                orderDao.insertWithConn(conn, order);

                for (OrderItem it : items) {
                    int rows = drinkDao.reduceStockWithConn(conn, it.getDrinkId(), it.getQuantity());
                    if (rows == 0) {
                        throw new IllegalStateException("库存不足，下单已回滚");
                    }
                    it.setOrderId(order.getId());
                    itemDao.insertWithConn(conn, it);
                }

                Integer pointsAfter = null;
                if (memberId != null) {
                    pointsAfter = memberDao.addPointsWithConn(conn, memberId, total.intValue());
                }

                conn.commit();
                return new PlaceOrderResult(order.getId(), total, pointsAfter);
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("下单失败：" + e.getMessage(), e);
        }
    }

    public TeaOrder findOrderById(Long id) {
        return orderDao.findById(id);
    }

    public List<OrderItem> findItemsByOrderId(Long id) {
        return itemDao.findByOrderIdWithDrinkName(id);
    }
}