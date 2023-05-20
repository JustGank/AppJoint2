package com.appjoint2.demo.application

import android.app.Application
import android.util.Log
import com.appjoint2.annotations.AppSpec


/**
 * @Author:JustGank
 * */
@AppSpec
class AppApplication : Application() {
    private val TAG = "AppApplication"
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "$TAG onCreate!")
    }
}
