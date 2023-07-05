package com.westee.cake.generate;

import java.io.Serializable;
import java.util.Date;

public class UserCoupon implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER_COUPON.ID
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER_COUPON.USER_ID
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER_COUPON.COUPON_ID
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    private Long couponId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER_COUPON.USED
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    private Boolean used;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER_COUPON.USED_TIME
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    private Date usedTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER_COUPON.CREATED_AT
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    private Date createdAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER_COUPON.UPDATED_AT
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table USER_COUPON
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER_COUPON
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public UserCoupon(Long id, Long userId, Long couponId, Boolean used, Date usedTime, Date createdAt, Date updatedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.used = used;
        this.usedTime = usedTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER_COUPON
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public UserCoupon() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER_COUPON.ID
     *
     * @return the value of USER_COUPON.ID
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER_COUPON.ID
     *
     * @param id the value for USER_COUPON.ID
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER_COUPON.USER_ID
     *
     * @return the value of USER_COUPON.USER_ID
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER_COUPON.USER_ID
     *
     * @param userId the value for USER_COUPON.USER_ID
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER_COUPON.COUPON_ID
     *
     * @return the value of USER_COUPON.COUPON_ID
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public Long getCouponId() {
        return couponId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER_COUPON.COUPON_ID
     *
     * @param couponId the value for USER_COUPON.COUPON_ID
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER_COUPON.USED
     *
     * @return the value of USER_COUPON.USED
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public Boolean getUsed() {
        return used;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER_COUPON.USED
     *
     * @param used the value for USER_COUPON.USED
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public void setUsed(Boolean used) {
        this.used = used;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER_COUPON.USED_TIME
     *
     * @return the value of USER_COUPON.USED_TIME
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public Date getUsedTime() {
        return usedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER_COUPON.USED_TIME
     *
     * @param usedTime the value for USER_COUPON.USED_TIME
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public void setUsedTime(Date usedTime) {
        this.usedTime = usedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER_COUPON.CREATED_AT
     *
     * @return the value of USER_COUPON.CREATED_AT
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER_COUPON.CREATED_AT
     *
     * @param createdAt the value for USER_COUPON.CREATED_AT
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER_COUPON.UPDATED_AT
     *
     * @return the value of USER_COUPON.UPDATED_AT
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER_COUPON.UPDATED_AT
     *
     * @param updatedAt the value for USER_COUPON.UPDATED_AT
     *
     * @mbg.generated Wed Jul 05 15:39:25 CST 2023
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}