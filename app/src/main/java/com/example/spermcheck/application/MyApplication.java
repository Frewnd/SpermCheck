package com.example.spermcheck.application;


import android.app.Application;

import com.example.spermcheck.utils.CrashHandler;


public class MyApplication extends Application {
    private CrashHandler mCrashHandler;
    // 创建文件夹
    public static final String DIRECTORY_NAME = "USBCamera";

    @Override
    public void onCreate() {
        super.onCreate();
        mCrashHandler = CrashHandler.getInstance();
        mCrashHandler.init(getApplicationContext(), getClass());
    }
}