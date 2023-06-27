package com.westee.cake.generate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderTable implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.USER_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.TOTAL_PRICE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private BigDecimal totalPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.ADDRESS_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private Long addressId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.EXPRESS_COMPANY
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private String expressCompany;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.EXPRESS_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private String expressId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.STATUS
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private String status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.CREATED_AT
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private Date createdAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.UPDATED_AT
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private Date updatedAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.SHOP_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private Long shopId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.PICKUP_TYPE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private Byte pickupType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.PICKUP_CODE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private String pickupCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public OrderTable(Long id, Long userId, BigDecimal totalPrice, Long addressId, String expressCompany, String expressId, String status, Date createdAt, Date updatedAt, Long shopId, Byte pickupType, String pickupCode) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.addressId = addressId;
        this.expressCompany = expressCompany;
        this.expressId = expressId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.shopId = shopId;
        this.pickupType = pickupType;
        this.pickupCode = pickupCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public OrderTable() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.ID
     *
     * @return the value of ORDER_TABLE.ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.ID
     *
     * @param id the value for ORDER_TABLE.ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.USER_ID
     *
     * @return the value of ORDER_TABLE.USER_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.USER_ID
     *
     * @param userId the value for ORDER_TABLE.USER_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.TOTAL_PRICE
     *
     * @return the value of ORDER_TABLE.TOTAL_PRICE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.TOTAL_PRICE
     *
     * @param totalPrice the value for ORDER_TABLE.TOTAL_PRICE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.ADDRESS_ID
     *
     * @return the value of ORDER_TABLE.ADDRESS_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public Long getAddressId() {
        return addressId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.ADDRESS_ID
     *
     * @param addressId the value for ORDER_TABLE.ADDRESS_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.EXPRESS_COMPANY
     *
     * @return the value of ORDER_TABLE.EXPRESS_COMPANY
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public String getExpressCompany() {
        return expressCompany;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.EXPRESS_COMPANY
     *
     * @param expressCompany the value for ORDER_TABLE.EXPRESS_COMPANY
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany == null ? null : expressCompany.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.EXPRESS_ID
     *
     * @return the value of ORDER_TABLE.EXPRESS_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public String getExpressId() {
        return expressId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.EXPRESS_ID
     *
     * @param expressId the value for ORDER_TABLE.EXPRESS_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setExpressId(String expressId) {
        this.expressId = expressId == null ? null : expressId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.STATUS
     *
     * @return the value of ORDER_TABLE.STATUS
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.STATUS
     *
     * @param status the value for ORDER_TABLE.STATUS
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.CREATED_AT
     *
     * @return the value of ORDER_TABLE.CREATED_AT
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.CREATED_AT
     *
     * @param createdAt the value for ORDER_TABLE.CREATED_AT
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.UPDATED_AT
     *
     * @return the value of ORDER_TABLE.UPDATED_AT
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.UPDATED_AT
     *
     * @param updatedAt the value for ORDER_TABLE.UPDATED_AT
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.SHOP_ID
     *
     * @return the value of ORDER_TABLE.SHOP_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.SHOP_ID
     *
     * @param shopId the value for ORDER_TABLE.SHOP_ID
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.PICKUP_TYPE
     *
     * @return the value of ORDER_TABLE.PICKUP_TYPE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public Byte getPickupType() {
        return pickupType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.PICKUP_TYPE
     *
     * @param pickupType the value for ORDER_TABLE.PICKUP_TYPE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setPickupType(Byte pickupType) {
        this.pickupType = pickupType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.PICKUP_CODE
     *
     * @return the value of ORDER_TABLE.PICKUP_CODE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public String getPickupCode() {
        return pickupCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.PICKUP_CODE
     *
     * @param pickupCode the value for ORDER_TABLE.PICKUP_CODE
     *
     * @mbg.generated Tue Jun 27 19:46:25 CST 2023
     */
    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode == null ? null : pickupCode.trim();
    }
}