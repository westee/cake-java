-- 修改 OrderTable 表的 address 列
ALTER TABLE ORDER_TABLE
    CHANGE COLUMN ADDRESS ADDRESS_ID BIGINT;