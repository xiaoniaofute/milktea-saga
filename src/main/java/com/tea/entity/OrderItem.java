package com.tea.entity;

import java.math.BigDecimal;


public class OrderItem {
    private Long id;
    private Long orderId;
    private Long drinkId;
    private Integer quantity;
    private BigDecimal unitPrice = BigDecimal.valueOf(0);    // 下单时价格快照
    private BigDecimal subtotal = BigDecimal.valueOf(0);     

    private String drinkName;

    @Override
    public String toString() {
        return String.format("  %s × %d   ¥%s = ¥%s",
                drinkName != null ? drinkName : ("drink#" + drinkId),
                quantity, unitPrice.toPlainString(), subtotal.toPlainString());
    }
}
