ALTER TABLE `CONFIG`
    ADD `SHOP_NAME` VARCHAR(100) AFTER `ID`,
    ADD `SHOP_ADDRESS` VARCHAR(100) AFTER `SHOP_NAME`;