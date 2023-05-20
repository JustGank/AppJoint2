package com.appjoint2.moduleB.impl

import android.content.Context
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import com.appjoint2.common.router.moduleB.ModuleBService
import com.appjoint2.common.router.moduleB.entry.ModuleBEntity
import com.appjoint2.moduleB.activity.ModuleBActivity


import com.appjoint2.annotations.ServiceProvider
import com.appjoint2.moduleB.fragment.ModuleBFragment
import com.appjoint2.moduleB.fragment.ModuleBTabFragment

/**
 * @Author:JustGank
 * */
@ServiceProvider
class ModuleBServiceImpl : ModuleBService {
  override fun moduleBTabFragment(): Fragment {
    return ModuleBTabFragment.newInstance()
  }

  override fun startActivityOfModuleB(context: Context?) {
    ModuleBActivity.start(context!!)
  }

  override fun obtainFragmentOfModuleB(): Fragment {
    return ModuleBFragment.newInstance()
  }

  override fun callMethodSyncOfModuleB(): String {
    return "syncMethodResultModuleB"
  }

  override fun callMethodAsyncOfModuleB(callback: Consumer<ModuleBEntity>) {
    Thread { callback.accept(ModuleBEntity("asyncMethodResultModuleB")) }.start()
  }


}