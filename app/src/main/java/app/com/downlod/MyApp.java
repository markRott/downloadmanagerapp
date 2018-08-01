package app.com.downlod;

import android.app.Application;

import app.com.downlod.di.ComponentsHelper;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ComponentsHelper.initMainAppComponent(this);
    }
}
