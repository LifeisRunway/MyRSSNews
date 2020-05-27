package com.imra.mynews.app;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.imra.mynews.di.AppComponent;
import com.imra.mynews.di.DaggerAppComponent;
import com.imra.mynews.di.modules.ContextModule;

/**
 * Date: 27.07.2019
 * Time: 17:23
 *
 * @author IMRA027
 */
public class MyNewsApp extends Application {

    private static AppComponent mAppComp;

    @Override
    public void onCreate () {
        super.onCreate();

        mAppComp = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

    }

    public static AppComponent getAppComponent() {
        return mAppComp;
    }


    @VisibleForTesting
    public static void setAppComponent(@NonNull AppComponent appComponent) {
        mAppComp = appComponent;
    }

}
