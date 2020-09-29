package com.example.mediaplayer.songsdb;

import android.app.Application;
import android.content.Context;

public class SongsApplication extends Application {
    private static Context context;

    public static Context getContext() {
        return SongsApplication.context;
    }

    @Override

    public void onCreate() {
        super.onCreate();
        SongsApplication.context = getApplicationContext();
    }
}
