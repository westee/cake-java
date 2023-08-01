package com.westee.cake.generate;

import java.io.Serializable;
import java.math.BigDecimal;

public class Discount implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT.ID
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT.DISCOUNT
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    private BigDecimal discount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DISCOUNT.GOODS_ID
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    private Long goodsId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DISCOUNT
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    public Discount(Long id, BigDecimal discount, Long goodsId) {
        this.id = id;
        this.discount = discount;
        this.goodsId = goodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    public Discount() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT.ID
     *
     * @return the value of DISCOUNT.ID
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT.ID
     *
     * @param id the value for DISCOUNT.ID
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT.DISCOUNT
     *
     * @return the value of DISCOUNT.DISCOUNT
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT.DISCOUNT
     *
     * @param discount the value for DISCOUNT.DISCOUNT
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DISCOUNT.GOODS_ID
     *
     * @return the value of DISCOUNT.GOODS_ID
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    public Long getGoodsId() {
        return goodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DISCOUNT.GOODS_ID
     *
     * @param goodsId the value for DISCOUNT.GOODS_ID
     *
     * @mbg.generated Tue Aug 01 22:05:43 CST 2023
     */
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}