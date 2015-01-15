package com.cnezsoft.zentao;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.Date;

/**
 * Zentao user
 * Created by Catouse on 2015/1/12.
 */
public class User {

    public enum Status {
        Unknown,
        Offline,
        Online
    };

    private final String PASSWORD_WITH_MD5_FLAG = "%PASSWORD_WITH_MD5_FLAG% ";
    private String account;
    private String address;
    private String passwordMD5;
    private Date lastLoginTime;
    private Status status = Status.Unknown;
    private UserPreferences userPreferences;
    private String identify;
    private final String ACCOUNT = "ACCOUNT";
    private final String PASSWORDMD5 = "PASSWORDMD5";
    private final String ADDRESS = "ADDRESS";
    private final String LASTLOGINTIME = "LASTLOGINTIME";

    /**
     * Get current user identify
     * @return
     */
    public String getIdentify()
    {
        return (identify != null ? identify : (account + "@" + address));
    }

    /**
     * Status getter
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Status setter
     * @param status
     */
    private void setStatus(Status status) {
        this.status = status;
    }

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
     * Get PasswordMd5 with prefix flag
     * @return
     */
    public String getPasswordMD5WithFlag()
    {
        return PASSWORD_WITH_MD5_FLAG + passwordMD5;
    }

    /**
     * Check and save data after user info changed
     */
    private void checkChange(boolean saveData)
    {
        if(status == Status.Unknown
                && !Helper.isNullOrEmpty(account)
                && !Helper.isNullOrEmpty(address)
                && !Helper.isNullOrEmpty(passwordMD5))
        {
            setStatus(Status.Offline);
        }

        identify = account + "@" + address;

        if(saveData) save();
    }

    /**
     * Check and save data after user info changed
     */
    private void checkChange()
    {
        checkChange(true);
    }

    /**
     * Save data with prefrences
     */
    public void save()
    {
        if(userPreferences != null)
        {
            userPreferences.edit()
                    .setIdentify(getIdentify())
                    .putString(ACCOUNT, getAccount())
                    .putString(ADDRESS, getAddress())
                    .putString(PASSWORDMD5, getPasswordMD5())
                    .putLong(LASTLOGINTIME, getLastLoginTime().getTime())
                    .commit();
        }
    }

    /**
     * Account setter
     * @param account
     */
    public User setAccount(String account, boolean saveData) {
        this.account = account;
        if(saveData) checkChange();
        return this;
    }

    /**
     * Account setter
     * @param account
     */
    public User setAccount(String account)
    {
        return this.setAccount(account, true);
    }

    /**
     * Address setter
     * @param address
     */
    public User setAddress(String address, boolean saveData) {
        if(address !=null && !address.startsWith("http://") && !address.startsWith("https://"))
        {
            address = "http://" + address;
        }
        this.address = address;
        if(saveData) checkChange();
        return this;
    }

    /**
     * Address setter
     * @param address
     */
    public User setAddress(String address)
    {
        return setAddress(address, true);
    }

    /**
     * PasswordMD5 setter
     * @param passwordMD5
     */
    public User setPasswordMD5(String passwordMD5, boolean saveData) {
        this.passwordMD5 = passwordMD5;
        if(saveData) checkChange();
        return this;
    }

    /**
     * Passwrod setter
     * @param password
     * @param saveData
     */
    public User setPassword(String password, boolean saveData) {
        if(password != null)
        {
            if(password.startsWith(PASSWORD_WITH_MD5_FLAG))
            {
                password = password.substring(PASSWORD_WITH_MD5_FLAG.length());
            }
            else
            {
                password = ZentaoAPI.md5(password);
            }
        }
        setPasswordMD5(password, saveData);
        return this;
    }

    public User setPassword(String password) {
        return setPassword(password, true);
    }

    /**
     * PasswordMD5 setter
     * @param passwordMD5
     */
    public User setPasswordMD5(String passwordMD5)
    {
        return setPasswordMD5(passwordMD5, true);
    }

    /**
     * 'LastLoginTime' setter
     * @param lastLoginTime
     */
    public User setLastLoginTime(Date lastLoginTime, boolean saveData) {
        this.lastLoginTime = lastLoginTime;
        if(saveData) checkChange();
        return this;
    }

    /**
     * 'LastLoginTime' setter
     * @param lastLoginTime
     */
    public User setLastLoginTime(Date lastLoginTime)
    {
        return setLastLoginTime(lastLoginTime, true);
    }

    /**
     * Turn the user status to online
     */
    public void online()
    {
        setStatus(Status.Online);
        this.setLastLoginTime(new Date());
    }

    /**
     * Turn the user status offline
     */
    public void offline()
    {
        setStatus(Status.Offline);
    }

    /**
     * Constructor with account, address, passwordMD5 and userPreferences
     * @param account
     * @param address
     * @param passwordMD5
     */
    public User(String account, String address, String passwordMD5, UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        this.userPreferences.setIdentify(getAccount());
        this.setAccount(account, false).setPasswordMD5(passwordMD5, false).setAddress(address);
    }

    /**
     * Constructor with userPreferences
     */
    public User(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        this.setAccount(this.userPreferences.getString(ACCOUNT, null), false)
                .setPasswordMD5(this.userPreferences.getString(PASSWORDMD5, null), false)
                .setAddress(this.userPreferences.getString(ADDRESS, null), false)
                .setLastLoginTime(new Date(this.userPreferences.getLong(LASTLOGINTIME, 0)), false)
                .checkChange(false);
    }

    /**
     * To JSON string
     * @return
     * @throws JSONException
     */
    public String toJSONString() {
        try {
            return new JSONStringer().object()
                    .key("account").value(this.account)
                    .key("address").value(this.address)
                    .key("passwordMD5").value(this.passwordMD5)
                    .key("lastLoginTime").value(this.lastLoginTime)
                    .key("status").value(this.status)
                    .endObject().toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "[EMPTY USER]";
        }
    }
}
