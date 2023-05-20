package com.appjoint2.common.router.moduleB

import android.content.Context
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import com.appjoint2.common.router.moduleB.entry.ModuleBEntity

/**
 * @Author:JustGank
 * */
interface ModuleBService {
    /**
     * 启动 moduleB 模块的 Activity
     */
    fun startActivityOfModuleB(context: Context?)

    /**
     * 调用 moduleB 模块的 Fragment
     */
    fun obtainFragmentOfModuleB(): Fragment?

    /**
     * 普通的同步方法调用
     */
    fun callMethodSyncOfModuleB(): String?

    /**
     * 以 Callback 形式封装的异步方法
     */
    fun callMethodAsyncOfModuleB(callback: Consumer<ModuleBEntity>)

    fun moduleBTabFragment(): Fragment?
}