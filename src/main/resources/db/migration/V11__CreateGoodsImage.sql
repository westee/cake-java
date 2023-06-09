CREATE TABLE GOODS_IMAGE
(
    ID             BIGINT AUTO_INCREMENT PRIMARY KEY,
    OWNER_GOODS_ID BIGINT       NOT NULL,
    URL            varchar(100) NOT NULL,
    DELETED        INT                   DEFAULT 0, -- 0 未删除; 1 已删除
    CREATED_AT     TIMESTAMP    NOT NULL DEFAULT NOW(),
    UPDATED_AT     TIMESTAMP    NOT NULL DEFAULT NOW()
)