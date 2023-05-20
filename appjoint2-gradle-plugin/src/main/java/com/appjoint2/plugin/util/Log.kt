package com.appjoint2.plugin.util

import com.appjoint2.plugin.bean.LogConfig
import java.text.SimpleDateFormat


/**
 * @Author:JustGank
 * */
class Log {
    companion object {

        const val TAG: String = "AppJoint2-Plugin"

        var logConfig = LogConfig()

        const val ANSI_RESET = "\u001B[0m"
        const val ANSI_BLACK = "\u001B[30m"
        const val ANSI_RED = "\u001B[31m"
        const val ANSI_GREEN = "\u001B[32m"
        const val ANSI_YELLOW = "\u001B[33m"
        const val ANSI_BLUE = "\u001B[34m"
        const val ANSI_PURPLE = "\u001B[35m"
        const val ANSI_CYAN = "\u001B[36m"
        const val ANSI_WHITE = "\u001B[37m"

        val timeFormat= SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")

        fun i(message: String) {
            if (logConfig.openLog) {
                val tag=if(logConfig.outputTime) timeFormat.format(System.currentTimeMillis()) else "" +TAG
                println("${ANSI_GREEN}$tag : $message$ANSI_RESET")
            }
        }

        fun d(message: String) {
            if (logConfig.openLog && logConfig.debug) {
                val tag=if(logConfig.outputTime) timeFormat.format(System.currentTimeMillis()) else "" +TAG
                println("$ANSI_BLUE$tag : $message$ANSI_RESET")
            }
        }

    }
}