package com.tea.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;





public class drink {
     private Long id;
    private String name;
    private String category;
    private BigDecimal price;     // 金额必须用 BigDecimal
    private Integer stock;
    private Integer isActive;     // 1=在售 0=下架
    private LocalDateTime createdAt;

     @Override
    public String toString() {
        return String.format("[%d] %-12s ¥%-6s 库存 %d",
                id, name, price.toPlainString(), stock);
    }

    
}
