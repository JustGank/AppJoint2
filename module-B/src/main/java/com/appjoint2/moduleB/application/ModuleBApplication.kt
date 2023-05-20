package com.appjoint2.moduleB.application

import android.app.Application
import android.util.Log
import com.appjoint2.annotations.ModuleSpec

/**
 * @Author:JustGank
 * */
@ModuleSpec(priority = 1)
class ModuleBApplication : Application() {

    private val TAG = "ModuleBApplication"

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "$TAG onCreate")
    }


}