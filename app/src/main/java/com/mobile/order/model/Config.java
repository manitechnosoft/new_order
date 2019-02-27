package com.mobile.order.model;

import android.content.Context;
import android.content.Intent;
import com.mobile.order.activity.BaseActivity;
import com.mobile.order.activity.LoginActivity;
import com.pixplicity.easyprefs.library.Prefs;


public class Config {

    private static Config config;

    private boolean isUserLoggedIn;

    private User loggedInUser;

    private DaoSession daoSession;

    private FCMToken fcmToken;

    private Config() {

    }

    public static synchronized Config getInstance(DaoSession daoSession) {
        if (config == null) {
            config = new Config();
            config.initialize(daoSession);
        }

        return config;
    }

    private void initialize(DaoSession daoSession) {
        this.isUserLoggedIn = Prefs.getBoolean("isUserLoggedIn", false);
        this.daoSession = daoSession;
        if (this.daoSession != null && this.isUserLoggedIn) {
            this.loggedInUser = this.daoSession.getUserDao().queryBuilder().unique();
            this.fcmToken = this.daoSession.getFCMTokenDao().queryBuilder().unique();
        }
    }

    public boolean isUserLoggedIn() {
        return this.isUserLoggedIn && this.loggedInUser != null;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.isUserLoggedIn = userLoggedIn;
        Prefs.putBoolean("isUserLoggedIn", userLoggedIn);
        this.loggedInUser = null;
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        if (this.daoSession != null) {
            this.daoSession.getUserDao().deleteAll();
            this.daoSession.getUserDao().insertOrReplace(loggedInUser);
            this.daoSession.getUserPrivilegeDao().deleteAll();
            if (loggedInUser.getUserPrivilege() != null) {
                this.daoSession.getUserPrivilegeDao().insertOrReplace(loggedInUser.getUserPrivilege());
            }
        }
    }

    public void logoutUser(Context context) {
        setUserLoggedIn(false);
        if (this.daoSession != null) {
            this.daoSession.getUserDao().deleteAll();
            this.daoSession.getFCMTokenDao().deleteAll();
            this.daoSession.getUserPrivilegeDao().deleteAll();
            this.daoSession.clear();
            deleteSqliteSequence();
        }
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
        ((BaseActivity) context).finish();
    }

    /**
     * Method used to delete sqlite sequence table on logout
     */
    private void deleteSqliteSequence() {
        this.daoSession.getDatabase().execSQL("DELETE FROM" + " " + "sqlite_sequence");
    }


    public FCMToken getFcmToken() {
        return this.fcmToken;
    }

    public void setFcmToken(FCMToken fcmToken) {
        this.fcmToken = fcmToken;
        if (this.daoSession != null) {
            this.daoSession.getFCMTokenDao().deleteAll();
            this.daoSession.getFCMTokenDao().insertOrReplace(fcmToken);
        }
    }

    /**
     * Setting recent logged user.
     *
     * @param recentUser RecentUser object
     */
    public void setRecentUser(RecentUser recentUser) {
        if (this.daoSession != null) {
            this.daoSession.getRecentUserDao().insertOrReplace(recentUser);
        }
    }


}
