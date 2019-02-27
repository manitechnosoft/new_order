package com.mobile.order;

import android.app.Application;
import android.content.ContextWrapper;

import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;
import com.mobile.order.helper.FontHelper;
import com.mobile.order.model.Config;
import com.mobile.order.model.DaoMaster;
import com.mobile.order.model.DaoSession;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.greendao.database.Database;

public class BaseApplication extends Application {

    private static final String SORT_ID = "sort_id";
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    private static boolean activityVisibility = false;
    private static DaoSession daoSession;
    protected String userAgent;
    private Long lastSyncedSortId;
    private boolean firebaseSyncStarted = false;


    /**
     * Method which returns DaoSession instance.
     *
     * @return {@link DaoSession}
     */
    public static DaoSession getDaoInstance() {
        return daoSession;
    }


    public static boolean isActivityVisibility() {
        return activityVisibility;
    }

    public static void setActivityVisibility(boolean activityVisibility) {
        BaseApplication.activityVisibility = activityVisibility;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initializePreferences();

        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);

// Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));

// Enable command line interface
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(getApplicationContext()));

// Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

// Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
        FontHelper.loadFonts(this);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"collectiondb"); //The users-db here is the name of our database.
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        FirebaseApp.initializeApp(this);
    }

    /**
     * Setter method to set firebaseSyncStarted
     *
     * @param firebaseSyncStarted boolean
     */
    public void setFirebaseSyncStarted(boolean firebaseSyncStarted) {
        this.firebaseSyncStarted = firebaseSyncStarted;
    }

    /**
     * Method which returns config object every where in the whole project
     *
     * @return {@link Config}
     */
    public Config getConfig() {
        return Config.getInstance(daoSession);
    }
    /**
     * Method which initializes the preferences.
     */
    private void initializePreferences() {
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
