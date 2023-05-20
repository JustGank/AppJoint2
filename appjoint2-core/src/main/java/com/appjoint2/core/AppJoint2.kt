package com.appjoint2.core

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import com.appjoint2.annotations.Constants
import java.lang.ref.SoftReference
import java.lang.reflect.InvocationTargetException

/**
 * @Author:JustGank
 * */
object AppJoint2 {

    lateinit var moduleApplications: List<Application>
    lateinit var routersMap: HashMap<String, Class<*>>
    lateinit var softRouterInstanceMap: SoftReference<HashMap<String, Any?>>

    @JvmStatic
    fun get(): AppJoint2 {
        return this
    }

    @Synchronized
    fun <T> service(routerType: Class<T>): T? {
        return service(routerType, Constants.APPJOINT2_DEFAULT_TAG)
    }

    @Synchronized
    fun <T> service(routerType: Class<T>, name: String): T? {

        val key = "${routerType.name}&$name"

        var requiredRouter = getRouterInstanceMap()?.get(key) as T?

        if (requiredRouter == null) {
            try {
                val type = routersMap[key]
                requiredRouter = type?.newInstance() as T?
                getRouterInstanceMap()?.put(key, requiredRouter)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
        return requiredRouter
    }

    fun attachBaseContext(context: Context?) {
        for (app in moduleApplications) {
            try {
                // invoke each application's attachBaseContext
                val attachBaseContext = ContextWrapper::class.java.getDeclaredMethod(
                    "attachBaseContext",
                    Context::class.java
                )
                attachBaseContext.isAccessible = true
                attachBaseContext.invoke(app, context)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
    }

    fun onCreate() {
        for (app in moduleApplications) {
            app.onCreate()
        }
    }


    fun onConfigurationChanged(configuration: Configuration?) {
        for (app in moduleApplications) {
            app.onConfigurationChanged(configuration!!)
        }
    }

    fun onLowMemory() {
        for (app in moduleApplications) {
            app.onLowMemory()
        }
    }

    fun onTerminate() {
        for (app in moduleApplications) {
            app.onTerminate()
        }
    }

    fun onTrimMemory(level: Int) {
        for (app in moduleApplications) {
            app.onTrimMemory(level)
        }
    }

    fun getRouterInstanceMap(): HashMap<String, Any?>? {
        if (softRouterInstanceMap.get() == null) {
            softRouterInstanceMap = SoftReference(HashMap())
        }
        return softRouterInstanceMap.get()
    }

}