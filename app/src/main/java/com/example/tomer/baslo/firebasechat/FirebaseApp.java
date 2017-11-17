package com.example.tomer.baslo.firebasechat;

import android.app.Application;

/**
 * Created by Tomer on 15/11/2017.
 */


public class FirebaseApp extends Application {
    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        FirebaseApp.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
