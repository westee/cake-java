package com.westee.cake.generate;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GoodsImage implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column GOODS_IMAGE.ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column GOODS_IMAGE.OWNER_GOODS_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private Long ownerGoodsId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column GOODS_IMAGE.URL
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String url;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column GOODS_IMAGE.DELETED
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private Integer deleted;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column GOODS_IMAGE.CREATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private LocalDateTime createdAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column GOODS_IMAGE.UPDATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private LocalDateTime updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public GoodsImage(Long id, Long ownerGoodsId, String url, Integer deleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.ownerGoodsId = ownerGoodsId;
        this.url = url;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public GoodsImage() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column GOODS_IMAGE.ID
     *
     * @return the value of GOODS_IMAGE.ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column GOODS_IMAGE.ID
     *
     * @param id the value for GOODS_IMAGE.ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column GOODS_IMAGE.OWNER_GOODS_ID
     *
     * @return the value of GOODS_IMAGE.OWNER_GOODS_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public Long getOwnerGoodsId() {
        return ownerGoodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column GOODS_IMAGE.OWNER_GOODS_ID
     *
     * @param ownerGoodsId the value for GOODS_IMAGE.OWNER_GOODS_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setOwnerGoodsId(Long ownerGoodsId) {
        this.ownerGoodsId = ownerGoodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column GOODS_IMAGE.URL
     *
     * @return the value of GOODS_IMAGE.URL
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getUrl() {
        return url;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column GOODS_IMAGE.URL
     *
     * @param url the value for GOODS_IMAGE.URL
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column GOODS_IMAGE.DELETED
     *
     * @return the value of GOODS_IMAGE.DELETED
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public Integer getDeleted() {
        return deleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column GOODS_IMAGE.DELETED
     *
     * @param deleted the value for GOODS_IMAGE.DELETED
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column GOODS_IMAGE.CREATED_AT
     *
     * @return the value of GOODS_IMAGE.CREATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column GOODS_IMAGE.CREATED_AT
     *
     * @param createdAt the value for GOODS_IMAGE.CREATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column GOODS_IMAGE.UPDATED_AT
     *
     * @return the value of GOODS_IMAGE.UPDATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column GOODS_IMAGE.UPDATED_AT
     *
     * @param updatedAt the value for GOODS_IMAGE.UPDATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}