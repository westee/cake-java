ALTER TABLE ORDER_TABLE
    ADD PICKUP_TYPE TINYINT; -- 自取 外卖

ALTER TABLE ORDER_TABLE
    ADD PICKUP_CODE VARCHAR(10); -- 自取 外卖