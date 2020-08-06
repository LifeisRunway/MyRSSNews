package com.imra.mynews.di.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.imra.mynews.di.common.MyAppScope;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Date: 31.07.2020
 * Time: 14:03
 *
 * @author IMRA027
 */
@Module
public class PreferenceModule {

//    private final Application mApp;
//
//    public PreferenceModule (Application application) {
//        mApp = application;
//    }
//
//    @Provides
//    @Singleton
//    Application application() {return mApp;}

//    private Context mContext;
//
//    public PreferenceModule(Context context) {
//        mContext = context;
//    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreference (Context context) {
        SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean hasVisited = sp.getBoolean("hasVisited", false);
        if (!hasVisited) { sp.edit().putBoolean("hasVisited", true).putString("url", "").apply(); }
        return sp;
    }

    @Provides
    @Singleton
    SharedPreferences.Editor provideSharedPreferenceEditor (SharedPreferences sharedPreferences) {
        return sharedPreferences.edit();
    }
}
