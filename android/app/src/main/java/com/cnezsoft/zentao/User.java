package com.cnezsoft.zentao;

import java.util.Date;

/**
 * Zentao user
 * Created by Catouse on 2015/1/12.
 */
public class User {
    private String account;
    private String address;
    private String passwordMD5;
    private Date lastLoginTime;

    /**
     * Get the last login time
     * @return
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * Account getter
     * @return
     */
    public String getAccount() {
        return account;
    }

    /**
     * Address getter
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * PasswordMD5 getter
     * @return
     */
    public String getPasswordMD5() {
        return passwordMD5;
    }

    /**
     * Account setter
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Address setter
     * @param address
     */
    public void setAddress(String address) {
        if(!address.startsWith("http://") && !address.startsWith("https://"))
        {
            address = "http://" + address;
        }
        this.address = address;
    }

    /**
     * PasswordMD% setter
     * @param passwordMD5
     */
    public void setPasswordMD5(String passwordMD5) {
        this.passwordMD5 = passwordMD5;
    }

    /**
     * 'LastLoginTime' setter
     * @param lastLoginTime
     */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * Set 'LastLoginTime' with current time
     */
    public void setLastLoginTime() {
        this.setLastLoginTime(new Date());
    }

    /**
     * Constructor with account, address and passwordMD5
     * @param account
     * @param address
     * @param passwordMD5
     */
    public User(String account, String address, String passwordMD5) {
        this.account = account;
        this.address = address;
        this.passwordMD5 = passwordMD5;
    }

    /**
     * Constructor with account and address
     * @param account
     * @param address
     */
    public User(String account, String address) {
        this.account = account;
        this.address = address;
    }

    /**
     * Constructor without params
     */
    public User() {
    }
}
