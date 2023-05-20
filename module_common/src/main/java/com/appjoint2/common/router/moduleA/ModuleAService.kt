package com.appjoint2.common.router.moduleA

import android.content.Context
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import com.appjoint2.common.router.moduleA.entry.ModuleAEntity

/**
 * @Author:JustGank
 * */
interface ModuleAService {

    /**
     * 启动 moduelA 模块的 Activity
     */
    fun startActivityOfModuleA(context: Context)

    /**
     * 调用 moduleA 模块的 Fragment
     */
    fun obtainFragmentOfModuleA(): Fragment

    /**
     * 普通的同步方法调用
     */
    fun callMethodSyncOfModuleA(): String

    /**
     * 以 Callback 形式封装的异步方法
     */
    fun callMethodAsyncOfModuleA(callback: Consumer<ModuleAEntity>)


}
