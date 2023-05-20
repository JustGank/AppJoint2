package com.appjoint2.moduleA.application

import android.app.Application
import android.util.Log

import com.appjoint2.annotations.ModuleSpec

/**
 * @Author:JustGank
 * */
@ModuleSpec(priority = 2)
class ModuleAApplication : Application() {

    private   val TAG = "ModuleAApplication"

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
    }
}
