package com.westee.cake.generate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DiscountDay implements Serializable {
    public String goodsName;

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal originPrice;

    public BigDecimal getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(BigDecimal originPrice) {
        this.originPrice = originPrice;
    }

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT_DAY.ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT_DAY.GOODS_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private Long goodsId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT_DAY.DISCOUNT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private BigDecimal discount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT_DAY.PRICE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private BigDecimal price;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT_DAY.DAYS
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String days;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT_DAY.DISABLED
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private Boolean disabled;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT_DAY.CREATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private LocalDateTime createdAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT_DAY.UPDATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private LocalDateTime updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public DiscountDay(Integer id, Long goodsId, BigDecimal discount, BigDecimal price, String days, Boolean disabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.goodsId = goodsId;
        this.discount = discount;
        this.price = price;
        this.days = days;
        this.disabled = disabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public DiscountDay() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT_DAY.ID
     *
     * @return the value of DISCOUNT_DAY.ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT_DAY.ID
     *
     * @param id the value for DISCOUNT_DAY.ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT_DAY.GOODS_ID
     *
     * @return the value of DISCOUNT_DAY.GOODS_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public Long getGoodsId() {
        return goodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT_DAY.GOODS_ID
     *
     * @param goodsId the value for DISCOUNT_DAY.GOODS_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT_DAY.DISCOUNT
     *
     * @return the value of DISCOUNT_DAY.DISCOUNT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT_DAY.DISCOUNT
     *
     * @param discount the value for DISCOUNT_DAY.DISCOUNT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT_DAY.PRICE
     *
     * @return the value of DISCOUNT_DAY.PRICE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT_DAY.PRICE
     *
     * @param price the value for DISCOUNT_DAY.PRICE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT_DAY.DAYS
     *
     * @return the value of DISCOUNT_DAY.DAYS
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getDays() {
        return days;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT_DAY.DAYS
     *
     * @param days the value for DISCOUNT_DAY.DAYS
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setDays(String days) {
        this.days = days == null ? null : days.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT_DAY.DISABLED
     *
     * @return the value of DISCOUNT_DAY.DISABLED
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT_DAY.DISABLED
     *
     * @param disabled the value for DISCOUNT_DAY.DISABLED
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT_DAY.CREATED_AT
     *
     * @return the value of DISCOUNT_DAY.CREATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT_DAY.CREATED_AT
     *
     * @param createdAt the value for DISCOUNT_DAY.CREATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT_DAY.UPDATED_AT
     *
     * @return the value of DISCOUNT_DAY.UPDATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT_DAY.UPDATED_AT
     *
     * @param updatedAt the value for DISCOUNT_DAY.UPDATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}