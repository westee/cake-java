package com.westee.cake.generate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.NAME
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.TEL
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String tel;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.AVATAR_URL
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String avatarUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.ADDRESS
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String address;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.ROLE_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private Long roleId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.WX_OPEN_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String wxOpenId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.WX_SESSION_KEY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String wxSessionKey;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.BIRTHDAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private LocalDateTime birthday;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.SEX
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private Byte sex;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.PASSWORD
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String password;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.NICKNAME
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private String nickname;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.BALANCE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private BigDecimal balance;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.CREATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private LocalDateTime createdAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.UPDATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private LocalDateTime updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table USER
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public User(Long id, String name, String tel, String avatarUrl, String address, Long roleId, String wxOpenId, String wxSessionKey, LocalDateTime birthday, Byte sex, String password, String nickname, BigDecimal balance, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.avatarUrl = avatarUrl;
        this.address = address;
        this.roleId = roleId;
        this.wxOpenId = wxOpenId;
        this.wxSessionKey = wxSessionKey;
        this.birthday = birthday;
        this.sex = sex;
        this.password = password;
        this.nickname = nickname;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public User() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.ID
     *
     * @return the value of USER.ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.ID
     *
     * @param id the value for USER.ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.NAME
     *
     * @return the value of USER.NAME
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.NAME
     *
     * @param name the value for USER.NAME
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.TEL
     *
     * @return the value of USER.TEL
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getTel() {
        return tel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.TEL
     *
     * @param tel the value for USER.TEL
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.AVATAR_URL
     *
     * @return the value of USER.AVATAR_URL
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.AVATAR_URL
     *
     * @param avatarUrl the value for USER.AVATAR_URL
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl == null ? null : avatarUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.ADDRESS
     *
     * @return the value of USER.ADDRESS
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getAddress() {
        return address;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.ADDRESS
     *
     * @param address the value for USER.ADDRESS
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.ROLE_ID
     *
     * @return the value of USER.ROLE_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.ROLE_ID
     *
     * @param roleId the value for USER.ROLE_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.WX_OPEN_ID
     *
     * @return the value of USER.WX_OPEN_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getWxOpenId() {
        return wxOpenId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.WX_OPEN_ID
     *
     * @param wxOpenId the value for USER.WX_OPEN_ID
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId == null ? null : wxOpenId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.WX_SESSION_KEY
     *
     * @return the value of USER.WX_SESSION_KEY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getWxSessionKey() {
        return wxSessionKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.WX_SESSION_KEY
     *
     * @param wxSessionKey the value for USER.WX_SESSION_KEY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setWxSessionKey(String wxSessionKey) {
        this.wxSessionKey = wxSessionKey == null ? null : wxSessionKey.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.BIRTHDAY
     *
     * @return the value of USER.BIRTHDAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public LocalDateTime getBirthday() {
        return birthday;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.BIRTHDAY
     *
     * @param birthday the value for USER.BIRTHDAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.SEX
     *
     * @return the value of USER.SEX
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public Byte getSex() {
        return sex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.SEX
     *
     * @param sex the value for USER.SEX
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setSex(Byte sex) {
        this.sex = sex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.PASSWORD
     *
     * @return the value of USER.PASSWORD
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.PASSWORD
     *
     * @param password the value for USER.PASSWORD
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.NICKNAME
     *
     * @return the value of USER.NICKNAME
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.NICKNAME
     *
     * @param nickname the value for USER.NICKNAME
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.BALANCE
     *
     * @return the value of USER.BALANCE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.BALANCE
     *
     * @param balance the value for USER.BALANCE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.CREATED_AT
     *
     * @return the value of USER.CREATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.CREATED_AT
     *
     * @param createdAt the value for USER.CREATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.UPDATED_AT
     *
     * @return the value of USER.UPDATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.UPDATED_AT
     *
     * @param updatedAt the value for USER.UPDATED_AT
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}