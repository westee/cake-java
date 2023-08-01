CREATE TABLE SHOPPING_CART
(
    ID         BIGINT PRIMARY KEY AUTO_INCREMENT,
    USER_ID    BIGINT,
    GOODS_ID   BIGINT,
    SHOP_ID    BIGINT,
    NUMBER     INT,
    STATUS     VARCHAR(16),
    CREATED_AT TIMESTAMP NOT NULL DEFAULT NOW(),
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ADDRESS
(
    ID               BIGINT PRIMARY KEY AUTO_INCREMENT,
    USER_ID          BIGINT,
    CONTACT          VARCHAR(20),                  -- 联系人
    ADDRESS          VARCHAR(100),                 -- 小程序中返回的address
    NAME             VARCHAR(100),                 -- 小程序中返回的name
    LATITUDE         DECIMAL(10, 6),
    LONGITUDE        DECIMAL(10, 6),
    PHONE_NUMBER     VARCHAR(11),
    DEFAULT_ADDRESS  BIT,
    SPECIFIC_ADDRESS VARCHAR(50),                  -- 门牌号之类的具体地址
    GENDER           TINYINT,                      -- 0男 ； 1 女
    DELETED          INT                DEFAULT 0, -- 0 未删除; 1 已删除
    CREATED_AT       TIMESTAMP NOT NULL DEFAULT NOW(),
    UPDATED_AT       TIMESTAMP NOT NULL DEFAULT NOW()
)