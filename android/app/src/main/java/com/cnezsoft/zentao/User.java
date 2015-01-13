package com.cnezsoft.zentao;

import java.util.Date;

/**
 * Created by Catouse on 2015/1/12.
 */
public class User {
    private String account;
    private String address;
    private String passwordMD5;
    private Date lastLoginTime;

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public String getAccount() {
        return account;
    }

    public String getAddress() {
        return address;
    }

    public String getPasswordMD5() {
        return passwordMD5;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPasswordMD5(String passwordMD5) {
        this.passwordMD5 = passwordMD5;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void setLastLoginTime() {
        this.setLastLoginTime(new Date());
    }

    public User(String account, String address, String passwordMD5) {
        this.account = account;
        this.address = address;
        this.passwordMD5 = passwordMD5;
    }

    public User() {
    }
}
