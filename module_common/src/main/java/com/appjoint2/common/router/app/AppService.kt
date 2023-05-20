package com.appjoint2.common.router.app

import android.content.Context
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.appjoint2.common.router.app.entry.AppEntity

/**
 * @Author:JustGank
 * */
interface AppService {
    /**
     * 普通的同步方法调用
     */
    fun callMethodSyncOfApp(): String?

    /**
     * 以 Callback 形式封装的异步方法
     */
    fun callMethodAsyncOfApp(callback: Consumer<AppEntity>)

    /*
     * 启动 App 模块的 Activity
     */
    fun startActivityOfApp(context: Context)

    /*
     * 调用 App 模块的 Fragment
     */
    fun obtainFragmentOfApp(): Fragment?
}