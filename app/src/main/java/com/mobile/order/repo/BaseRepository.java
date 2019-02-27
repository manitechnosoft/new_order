package com.mobile.order.repo;


import com.mobile.order.BaseApplication;


public abstract class BaseRepository {

    /**
     * Method to begin transaction
     */
    public void beginTransaction() {
        BaseApplication.getDaoInstance().getDatabase().beginTransaction();
    }

    /**
     * Method to end transaction
     */
    public void endTransaction() {
        BaseApplication.getDaoInstance().getDatabase().setTransactionSuccessful();
        BaseApplication.getDaoInstance().getDatabase().endTransaction();
    }

}
