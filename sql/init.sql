DROP DATABASE IF EXISTS milktea_saga;
CREATE DATABASE milktea_saga
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE milktea_saga;


CREATE TABLE admin (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(32)  NOT NULL                COMMENT '用户名（唯一）',
    password    VARCHAR(64)  NOT NULL                COMMENT '密码（明文，仅教学用）',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店员账号表';

CREATE TABLE drink (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(64)   NOT NULL                COMMENT '奶茶名称',
    category    VARCHAR(16)   NOT NULL                COMMENT '分类：奶茶/果茶/特调',
    price       DECIMAL(6,2)  NOT NULL                COMMENT '单价（元）',
    stock       INT           NOT NULL DEFAULT 0      COMMENT '库存（杯）',
    is_active   TINYINT(1)    NOT NULL DEFAULT 1      COMMENT '是否在售：1=在售 0=下架',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_category (category),
    KEY idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='奶茶商品表';

CREATE TABLE member (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(32)  NOT NULL                COMMENT '姓名',
    phone       VARCHAR(11)  NOT NULL                COMMENT '手机号（11位，唯一）',
    points      INT          NOT NULL DEFAULT 0      COMMENT '会员积分（满1元加1分）',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入会时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

CREATE TABLE tea_order (
    id             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    member_id      BIGINT        NULL                    COMMENT '会员ID，散客为NULL',
    total_amount   DECIMAL(8,2)  NOT NULL                COMMENT '订单总金额',
    status         VARCHAR(16)   NOT NULL DEFAULT 'PAID' COMMENT '订单状态：PAID=已支付/CANCELLED=已取消',
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    PRIMARY KEY (id),
    KEY idx_member_id (member_id),
    KEY idx_created_at (created_at),
    CONSTRAINT fk_order_member FOREIGN KEY (member_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

CREATE TABLE order_item (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id    BIGINT        NOT NULL                COMMENT '订单ID',
    drink_id    BIGINT        NOT NULL                COMMENT '奶茶ID',
    quantity    INT           NOT NULL                COMMENT '杯数',
    unit_price  DECIMAL(6,2)  NOT NULL                COMMENT '下单时单价快照（防止后期改价历史失真）',
    subtotal    DECIMAL(8,2)  NOT NULL                COMMENT '小计 = unit_price * quantity',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_drink_id (drink_id),
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES tea_order(id),
    CONSTRAINT fk_item_drink FOREIGN KEY (drink_id) REFERENCES drink(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

USE milktea_saga;
INSERT INTO admin (username, password) VALUES
('linboss',  '888888'),   -- 林老板娘
('xiaoli',   '123456'),   -- 兼职小李
('xiaowang', '123456');   -- 兼职小王
INSERT INTO drink (name, category, price, stock, is_active) VALUES
('珍珠奶茶',      '奶茶', 12.00,  50, 1),
('芋圆奶茶',      '奶茶', 15.00,  30, 1),
('黑糖鹿丸',      '奶茶', 16.00,  25, 1),
('葡萄柚柚',      '果茶', 18.00,  20, 1),
('多肉草莓',      '果茶', 20.00,   8, 1),    -- 库存预警样例
('百香果绿茶',    '果茶', 14.00,  40, 1),
('海盐芝士奶盖',  '特调', 22.00,  15, 1),
('生椰拿铁',      '特调', 25.00,   5, 1),    -- 库存预警样例
('莓莓抹茶',      '特调', 24.00,  18, 0);    -- 已下架样例

INSERT INTO member (name, phone, points) VALUES
('王同学', '13800138001',  280),
('张同学', '13800138002',   45),
('李学姐', '13900139003', 1024),
('小赵',  '13800138004',    0);

INSERT INTO tea_order (member_id, total_amount, status, created_at) VALUES
(1, 39.00, 'PAID', '2026-06-01 14:30:00');
INSERT INTO order_item (order_id, drink_id, quantity, unit_price, subtotal) VALUES
(1, 1, 2, 12.00, 24.00),
(1, 2, 1, 15.00, 15.00);

INSERT INTO tea_order (member_id, total_amount, status, created_at) VALUES
(3, 74.00, 'PAID', '2026-06-02 16:10:00');
INSERT INTO order_item (order_id, drink_id, quantity, unit_price, subtotal) VALUES
(2, 3, 1, 16.00, 16.00),
(2, 4, 2, 18.00, 36.00),
(2, 7, 1, 22.00, 22.00);

INSERT INTO tea_order (member_id, total_amount, status, created_at) VALUES
(NULL, 14.00, 'PAID', '2026-06-05 11:00:00');
INSERT INTO order_item (order_id, drink_id, quantity, unit_price, subtotal) VALUES
(3, 6, 1, 14.00, 14.00);


INSERT INTO tea_order (member_id, total_amount, status, created_at) VALUES
(2, 66.00, 'PAID', '2026-06-08 19:20:00');
INSERT INTO order_item (order_id, drink_id, quantity, unit_price, subtotal) VALUES
(4, 1, 3, 12.00, 36.00),
(4, 2, 2, 15.00, 30.00);