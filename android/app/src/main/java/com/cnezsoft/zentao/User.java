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
    private void onChange(boolean saveData)
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
    private void onChange()
    {
        onChange(true);
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
        Log.v("USER", "SAVE:" + toJSONString());
    }

    /**
     * Account setter
     * @param account
     */
    public void setAccount(String account, boolean saveData) {
        this.account = account;
        if(saveData) onChange();
    }

    /**
     * Account setter
     * @param account
     */
    public void setAccount(String account)
    {
        this.setAccount(account, true);
    }

    /**
     * Address setter
     * @param address
     */
    public void setAddress(String address, boolean saveData) {
        if(address !=null && !address.startsWith("http://") && !address.startsWith("https://"))
        {
            address = "http://" + address;
        }
        this.address = address;
        if(saveData) onChange();
    }

    /**
     * Address setter
     * @param address
     */
    public void setAddress(String address)
    {
        setAddress(address, true);
    }

    /**
     * PasswordMD5 setter
     * @param passwordMD5
     */
    public void setPasswordMD5(String passwordMD5, boolean saveData) {
        if(passwordMD5 != null && passwordMD5.startsWith(PASSWORD_WITH_MD5_FLAG))
        {
            passwordMD5 = passwordMD5.substring(PASSWORD_WITH_MD5_FLAG.length());
        }
        else
        {
            passwordMD5 = ZentaoAPI.md5(passwordMD5);
        }
        this.passwordMD5 = passwordMD5;
        if(saveData) onChange();
    }

    /**
     * PasswordMD5 setter
     * @param passwordMD5
     */
    public void setPasswordMD5(String passwordMD5)
    {
        setPasswordMD5(passwordMD5, true);
    }

    /**
     * 'LastLoginTime' setter
     * @param lastLoginTime
     */
    public void setLastLoginTime(Date lastLoginTime, boolean saveData) {
        this.lastLoginTime = lastLoginTime;
        if(saveData) onChange();
    }

    /**
     * 'LastLoginTime' setter
     * @param lastLoginTime
     */
    public void setLastLoginTime(Date lastLoginTime)
    {
        setLastLoginTime(lastLoginTime, true);
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
     * Set account, address and passwordMD5
     * @param account
     * @param address
     * @param passwordMD5
     * @param save
     */
    public void set(String account, String address, String passwordMD5, boolean save) {
        this.setAccount(account, false);
        this.setAddress(address, false);
        this.setPasswordMD5(passwordMD5, false);
        onChange(save);
    }

    /**
     * Set account, address and passwordMD5
     * @param account
     * @param address
     * @param passwordMD5
     */
    public void set(String account, String address, String passwordMD5)
    {
        set(account, address, passwordMD5, true);
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
        set(account, address, passwordMD5);
    }

    /**
     * Constructor with userPreferences
     */
    public User(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        set(this.userPreferences.getString(ACCOUNT, null),
                this.userPreferences.getString(ADDRESS, null),
                this.userPreferences.getString(PASSWORDMD5, null), false);
        setLastLoginTime(new Date(this.userPreferences.getLong(LASTLOGINTIME, 0)), false);
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
