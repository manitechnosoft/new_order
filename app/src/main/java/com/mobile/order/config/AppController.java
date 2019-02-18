package com.mobile.order.config;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;
import com.mobile.order.helper.FontHelper;
import com.mobile.order.model.DaoMaster;
import com.mobile.order.model.DaoSession;
import org.greenrobot.greendao.database.Database;

/**
 */

public class AppController extends Application {

	public static final boolean ENCRYPTED = true;
	DaoSession daoSession;
	@Override
	public void onCreate() {
		super.onCreate();
		// Create an InitializerBuilder
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

		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"grocery-db"); //The users-db here is the name of our database.
		Database db = helper.getWritableDb();
		daoSession = new DaoMaster(db).newSession();

		FirebaseApp.initializeApp(this);
	}

	public DaoSession getDaoSession() {
		return daoSession;
	}

}
