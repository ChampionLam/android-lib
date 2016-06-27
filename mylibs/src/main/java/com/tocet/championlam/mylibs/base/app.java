package com.tocet.championlam.mylibs.base;

import android.app.Application;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * init Application
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initXutil();
    }

    /**
     * init xutils3
     */
    private void initXutil() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
