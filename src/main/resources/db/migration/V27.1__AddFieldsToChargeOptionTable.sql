ALTER TABLE CHARGE_OPTION
ADD COLUMN DELETED      BOOLEAN DEFAULT FALSE,
ADD COLUMN OWNER_USR_ID BIGINT,
ADD COLUMN SHOP_ID      BIGINT;