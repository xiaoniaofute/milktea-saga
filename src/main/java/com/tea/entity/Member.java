package com.tea.entity;

import java.time.LocalDateTime;


public class Member {
     private Long id;
    private String name;
    private String phone;
    private Integer points;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return String.format("会员[%d] %s（%s）积分 %d",
                id, name, phone, points);
}
