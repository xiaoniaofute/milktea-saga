package com.tea.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;



public class TeaOrder {
     private Long id;
    private Long memberId;             // 可空：散客订单为 null
    private BigDecimal totalAmount = BigDecimal.valueOf(0);
    private String status;             // PAID / CANCELLED
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

     @Override
    public String toString() {
        return String.format("Order[%d] member=%s ￥%s %s",
                id, memberId, totalAmount.toPlainString(), status);
    }
}