package com.appjoint2.demo.impl

import android.content.Context
import android.content.Intent
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import com.appjoint2.demo.activity.AppActivity
import com.appjoint2.demo.fragment.AppFragment.Companion.newInstance
import com.appjoint2.common.router.app.AppService
import com.appjoint2.common.router.app.entry.AppEntity
import com.appjoint2.annotations.ServiceProvider

/**
 * @Author:JustGank
 * */
@ServiceProvider("another")
class AppServiceImpl2 : AppService {
    override fun callMethodSyncOfApp(): String {
        return "syncMethodResultApp(another implementation)"
    }

    override fun callMethodAsyncOfApp(callback: Consumer<AppEntity>) {
        Thread {
            callback.accept(
                AppEntity("asyncMethodResultApp(another implementation)")
            )
        }.start()
    }

    override fun startActivityOfApp(context: Context) {
        val intent = Intent(context, AppActivity::class.java)
        context.startActivity(intent)
    }

    override fun obtainFragmentOfApp(): Fragment {
        return newInstance()
    }
}