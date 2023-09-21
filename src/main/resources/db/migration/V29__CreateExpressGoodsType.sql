CREATE TABLE EXPRESS_GOODS_TYPE
(
    ID         BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME       VARCHAR(20),
    GOODS_TYPE INT,
    CREATED_AT TIMESTAMP NOT NULL DEFAULT NOW(),
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO EXPRESS_GOODS_TYPE (name, goods_type)
VALUES ('快餐', 1),
       ('百货', 3),
       ('生鲜', 6),
       ('蛋糕', 13),
       ('饮料', 32),
       ('其他', 99);