package com.tea.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;



public class TeaOrder {
     private Long id;
    private Long memberId;             // 可空：散客订单为 null
    private BigDecimal totalAmount = BigDecimal.valueOf(0);
    private String status;             // PAID / CANCELLED
    private LocalDateTime createdAt;

     @Override
    public String toString() {
        return String.format("Order[%d] member=%s ¥%s %s",
                id, memberId, totalAmount.toPlainString(), status);
    }
}
