-- ============================================================
-- 仓库管理系统 建表脚本 (MySQL 8.0)
-- 用法：mysql -uroot -p < warehouse_db.sql
-- 说明：应用启动时 DataInitializer 会在 sys_user 为空时创建默认账号
--       admin / 123456（BCrypt 加密），所以这里不写死密码哈希。
-- ============================================================

CREATE DATABASE IF NOT EXISTS warehouse_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE warehouse_db;

-- ------------------------------------------------------------ 用户表
DROP TABLE IF EXISTS inbound_record;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS warehouse;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(64)  NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT 'BCrypt 哈希',
    nickname VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    phone    VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '系统用户';

-- ------------------------------------------------------------ 仓库表
CREATE TABLE warehouse (
    id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    code    VARCHAR(64)  NOT NULL COMMENT '仓库编码',
    name    VARCHAR(128) NOT NULL COMMENT '仓库名称',
    address VARCHAR(255) DEFAULT NULL COMMENT '地址',
    manager VARCHAR(64)  DEFAULT NULL COMMENT '管理员',
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouse_code (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '仓库';

-- ------------------------------------------------------------ 商品表
CREATE TABLE product (
    id       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    sku      VARCHAR(64)   NOT NULL COMMENT '商品 SKU',
    name     VARCHAR(128)  NOT NULL COMMENT '商品名称',
    category VARCHAR(64)   DEFAULT NULL COMMENT '分类',
    price    DECIMAL(10, 2) DEFAULT 0.00 COMMENT '单价（用 DECIMAL 避免浮点误差）',
    stock    INT           NOT NULL DEFAULT 0 COMMENT '库存，由入库/撤销入库维护',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_sku (sku),
    KEY idx_product_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '商品';

-- ------------------------------------------------------------ 入库记录表
CREATE TABLE inbound_record (
    id           BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    product_id   BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    quantity     INT    NOT NULL COMMENT '入库数量',
    inbound_time DATETIME NOT NULL COMMENT '入库时间',
    PRIMARY KEY (id),
    KEY idx_inbound_product (product_id),
    KEY idx_inbound_warehouse (warehouse_id),
    KEY idx_inbound_time (inbound_time),
    CONSTRAINT fk_inbound_product FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT fk_inbound_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '入库记录';

-- ------------------------------------------------------------ 演示数据
INSERT INTO warehouse (code, name, address, manager) VALUES
('WH-LS-01', '乐山一号仓', '四川省乐山市市中区', '王嘉懿'),
('WH-CD-01', '成都中转仓', '四川省成都市双流区', '李工'),
('WH-YB-01', '宜宾前置仓', '四川省宜宾市翠屏区', '张工');

INSERT INTO product (sku, name, category, price, stock) VALUES
('SKU-KB-001', '机械键盘 87 键', '外设', 299.00, 0),
('SKU-MS-002', '无线鼠标', '外设', 89.90, 0),
('SKU-MN-003', '24 寸显示器', '显示设备', 799.00, 0),
('SKU-HD-004', '移动硬盘 1TB', '存储', 459.00, 0),
('SKU-CB-005', 'Type-C 数据线', '配件', 29.90, 0),
('SKU-ST-006', '显示器支架', '配件', 159.00, 0);

-- 入库演示记录（注意：直接写 INSERT 不会自动加库存，
-- 正常业务请走接口 POST /api/inbound/save，由服务层在同一事务里累加库存）
INSERT INTO inbound_record (product_id, warehouse_id, quantity, inbound_time) VALUES
(1, 1, 20, NOW()),
(2, 1, 50, NOW()),
(3, 2, 10, NOW());

-- 让库存与上面的入库记录一致，便于直接观察数据
UPDATE product p
SET p.stock = (SELECT COALESCE(SUM(r.quantity), 0) FROM inbound_record r WHERE r.product_id = p.id);
