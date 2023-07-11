CREATE TABLE SWIPER
(
    ID          int PRIMARY KEY NOT NULL AUTO_INCREMENT,
    TITLE       varchar(255)             DEFAULT NULL,
    IMAGE_URL   varchar(255)    NOT NULL,
    SWIPER_TYPE varchar(10)              DEFAULT 'index',
    LINK_URL    varchar(255)             DEFAULT NULL,
    SORT_ORDER  int(11) NOT NULL DEFAULT '0',
    ENABLED     tinyint(1) NOT NULL DEFAULT '1',
    CREATED_AT  TIMESTAMP       NOT NULL DEFAULT NOW(),
    UPDATED_AT  TIMESTAMP       NOT NULL DEFAULT NOW()
);