package com.cnezsoft.zentao;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cnezsoft.zentao.data.DbHelper;

import org.json.JSONException;
import org.json.JSONStringer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    }
    
    public interface  OnLastSyncTimeChangeListener {
        void onLastSyncTimeChange(Date thisSyncTime);
    }

    public interface  OnAccountChangeListener {
        void onAccountChange(String account);
    }

    public interface OnUserInfoChangeListener {
        void onUserInfoChnage(String name);
    }

    public interface OnStatusChangeListner {
        void onStatusChange(Status status);
    }

    public static final String PASSWORD_WITH_MD5_FLAG = "%PASSWORD_WITH_MD5_FLAG% ";
    public static final String ACCOUNT = "ACCOUNT";
    public static final String PASSWORD_MD5 = "PASSWORD_MD5";
    public static final String ADDRESS = "ADDRESS";
    public static final String LAST_LOGIN_TIME = "LAST_LOGIN_TIME";
    public static final String LAST_SYNC_TIME = "LAST_SYNC_TIME";
    public static final String EMAIL = "EMAIL";
    public static final String REALNAME = "REALNAME";
    public static final String ROLE = "ROLE";
    public static final String GENDER = "GENDER";
    public static final String DB_VERSION = "DB_VERSION";
    public static final String ID = "ID";

    private OnLastSyncTimeChangeListener onLastSyncTimeChangeListener;
    private OnAccountChangeListener onAccountChangeListener;
    private OnUserInfoChangeListener onUserInfoChangeListener;
    private OnStatusChangeListner onStatusChangeListner;
    private int dbVersion;

    private String account;
    private String address;
    private String passwordMD5;
    private Date lastLoginTime;
    private Status status = Status.Unknown;
    private UserPreferences userPreferences;
    private String identify;
    private String email;
    private String realname;
    private String role;
    private String gender;
    private String id;
    private Date lastSyncTime = new Date(0);
    private Long lastLoadTime = 0l;
    private Long lastChangeTime = 0l;

    public void setOnLastSyncTimeChangeListener(OnLastSyncTimeChangeListener listener) {
        onLastSyncTimeChangeListener = listener;
    }

    public void setOnAccountChangeListener(OnAccountChangeListener listener) {
        onAccountChangeListener = listener;
    }

    public void setOnUserInfoChangeListener(OnUserInfoChangeListener listener) {
        onUserInfoChangeListener = listener;
    }

    public String getHelloText(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR);
        int resId = 0;

        if(hour < 11) {
            resId = R.string.text_hello_morning;
        } else if(hour < 14) {
            resId = R.string.text_hello_noon;
        } else if(hour < 18) {
            resId = R.string.text_hello_afternoon;
        } else {
            resId = R.string.text_hello_night;
        }
        return String.format(context.getResources().getString(resId), getRealname());
    }

    /**
     * Get last sync time
     * @return
     */
    public Date getLastSyncTime() {
        if(dbVersion != DbHelper.DATABASE_VERSION) return new Date(0);
        return lastSyncTime;
    }

    /**
     * Get last sync time as string
     * @param context
     * @return
     */
    public String getLastSyncTimeStr(Context context) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastSyncTime);
        return new SimpleDateFormat(now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) ?
                context.getString(R.string.text_medium_datetime_format)
                : context.getString(R.string.text_long_datetime_format)).format(lastSyncTime);
    }

    /**
     * Set database version
     * @param version
     * @return
     */
    public User setDbVersion(int version) {
        this.dbVersion = version;
        return this;
    }

    /**
     * Get database version for this user
     * @return
     */
    public int getDbVersion() {
        return dbVersion;
    }

    /**
     * Get last sync time
     * @param load
     * @return
     */
    public Date getLastSyncTime(boolean load) {
        if(load) setLastSyncTime(new Date(this.userPreferences.getLong(LAST_SYNC_TIME, 0)));
        return lastSyncTime;
    }

    /**
     * Set sync time
     * @param syncTime
     */
    public void setSyncTime(Date syncTime) {
        this.dbVersion = DbHelper.DATABASE_VERSION;
        this.lastSyncTime = syncTime;
        save();
    }

    /**
     * Set last sync time with a date
     * @param lastSyncTime
     */
    public User setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
        return this;
    }

    /**
     * Get id
     * @return
     */
    public String getId() {
        return  this.id;
    }

    /**
     * Set gender
     * @param id
     */
    public User setId(String id, boolean saveData) {
        this.id = id;
        if(saveData) checkChange();
        return this;
    }

    /**
     * Get gender
     * @return
     */
    public String getGender() {
        return gender;
    }

    /**
     * Set gender
     * @param gender
     */
    public User setGender(String gender, boolean saveData) {
        this.gender = gender;
        if(saveData) checkChange();
        return this;
    }

    /**
     * Get role
     * @return
     */
    public String getRole() {
        return role;
    }

    /**
     * Set role
     * @param role
     */
    public User setRole(String role, boolean saveData) {
        this.role = role;
        if(saveData) checkChange();
        return this;
    }

    /**
     * Get user name for display
     * @return
     */
    public String getName() {
        return Helper.isNullOrEmpty(realname) ? account : realname;
    }

    /**
     * Get realname
     * @return
     */
    public String getRealname() {
        return realname;
    }

    /**
     * Set realname
     * @param realname
     */
    public User setRealname(String realname, boolean saveData) {
        this.realname = realname;
        if(saveData) checkChange();
        return this;
    }

    /**
     * Get email
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email
     * @param email
     */
    public User setEmail(String email, boolean saveData) {
        this.email = email;
        if(saveData) checkChange();
        return this;
    }

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
        if(status != this.status) {
            this.status = status;
            if(onStatusChangeListner != null) {
                onStatusChangeListner.onStatusChange(status);
            }
        }
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
     * Get account
     * @param load
     * @return
     */
    public String getAccount(boolean load) {
        if(load) {
            setAccount(this.userPreferences.getString(ACCOUNT, null));
        }
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
     * Check user status
     * @return
     */
    public Status checkUserStatus() {
        if(Helper.isNullOrEmpty(account)
                || Helper.isNullOrEmpty(address)
                || Helper.isNullOrEmpty(passwordMD5)) {
            setStatus(Status.Unknown);
        } else if(status == Status.Unknown) {
            setStatus(Status.Offline);
        }
        return status;
    }

    /**
     * Check and save data after user info changed
     */
    private void checkChange(boolean saveData)
    {
        checkUserStatus();

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
    public void save() {
        if (userPreferences != null) {
            userPreferences.edit()
                    .setIdentify(getIdentify())
                    .putString(ACCOUNT, getAccount())
                    .putString(ADDRESS, getAddress())
                    .putString(PASSWORD_MD5, getPasswordMD5())
                    .putString(REALNAME, getRealname())
                    .putString(EMAIL, getEmail())
                    .putString(ROLE, getRole())
                    .putString(GENDER, getGender())
                    .putString(ID, getId())
                    .putInt(DB_VERSION, getDbVersion())
                    .putLong(LAST_LOGIN_TIME, getLastLoginTime().getTime())
                    .putLong(LAST_SYNC_TIME, getLastSyncTime().getTime())
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
        if(!Helper.isNullOrEmpty(address) && !address.startsWith("http://") && !address.startsWith("https://"))
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
     * Password setter
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

    /**
     * Set password
     * @param password
     * @return
     */
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
     * Load data from userPreferences
     */
    public void load() {
        if(lastChangeTime > lastLoadTime) {
            this.setAccount(this.userPreferences.getString(ACCOUNT, null), false)
                    .setPasswordMD5(this.userPreferences.getString(PASSWORD_MD5, null), false)
                    .setAddress(this.userPreferences.getString(ADDRESS, null), false)
                    .setLastLoginTime(new Date(this.userPreferences.getLong(LAST_LOGIN_TIME, 0)), false)
                    .setEmail(this.userPreferences.getString(EMAIL, null), false)
                    .setRealname(this.userPreferences.getString(REALNAME, ""), false)
                    .setGender(this.userPreferences.getString(GENDER, ""), false)
                    .setRole(this.userPreferences.getString(ROLE, ""), false)
                    .setId(this.userPreferences.getString(ID, ""), false)
                    .setDbVersion(this.userPreferences.getInt(DB_VERSION, 0))
                    .setLastSyncTime(new Date(this.userPreferences.getLong(LAST_SYNC_TIME, 0)))
                    .checkChange(false);
            Log.v("USER", "load: " + toJSONString());
        }

        lastLoadTime = lastChangeTime;
    }

    /**
     * Constructor with userPreferences
     */
    public User(final UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        lastChangeTime = 1l;
        this.load();

        this.userPreferences.registerOnUserChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.v("USER", "changed: " + key + " | " + toJSONString());
                lastChangeTime = new Date().getTime();

                key = key.replace(getIdentify() + "::", "");
                switch (key) {
                    case LAST_SYNC_TIME:
                        Date date = getLastSyncTime(true);
                        if(onLastSyncTimeChangeListener != null) {
                            onLastSyncTimeChangeListener.onLastSyncTimeChange(date);
                        }
                        lastLoadTime = lastChangeTime;
                        break;
                    case ACCOUNT:
                        String account = getAccount(true);
                        if(onAccountChangeListener != null) {
                            onAccountChangeListener.onAccountChange(account);
                        }
                        lastLoadTime = lastChangeTime;
                        break;
                    default:
                        load();
                        if(onUserInfoChangeListener != null) {
                            onUserInfoChangeListener.onUserInfoChnage(key);
                        }
                        break;
                }
            }
        });
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
                    .key("lastSyncTime").value(this.getLastSyncTime())
                    .key("status").value(this.status)
                    .key("email").value(this.email)
                    .key("realname").value(this.realname)
                    .key("gender").value(this.gender)
                    .key("role").value(this.role)
                    .key("id").value(this.id)
                    .key("dbVersion").value(this.dbVersion)
                    .endObject().toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "[EMPTY USER]";
        }
    }
}
