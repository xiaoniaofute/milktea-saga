package com.tea;
import com.tea.util.DbUtil;
import java.sql.Connection;

public class Main {
     public static void main(String[] args) throws Exception {
        try (Connection conn = DbUtil.getConnection()) {
            System.out.println("✓ 连 接成功：" + conn.getCatalog());
        }
    }
}
