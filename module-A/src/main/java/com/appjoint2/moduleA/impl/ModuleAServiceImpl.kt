package com.appjoint2.moduleA.impl

import android.content.Context
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import com.appjoint2.common.router.moduleA.ModuleAService
import com.appjoint2.common.router.moduleA.entry.ModuleAEntity
import com.appjoint2.moduleA.activity.ModuleAActivity
import com.appjoint2.moduleA.fragment.ModuleAFragment

import com.appjoint2.annotations.ServiceProvider

/**
 * @Author:JustGank
 * */
@ServiceProvider
class ModuleAServiceImpl : ModuleAService {
    override fun startActivityOfModuleA(context: Context) {
        ModuleAActivity.start(context)
    }

    override fun obtainFragmentOfModuleA(): Fragment {
        return ModuleAFragment.newInstance()
    }

    override fun callMethodSyncOfModuleA(): String {
        return "syncMethodResultModuleA"
    }

    override fun callMethodAsyncOfModuleA(callback: Consumer<ModuleAEntity>) {
        Thread { callback.accept(ModuleAEntity("asyncMethodResultModuleA")) }.start()
    }

}
