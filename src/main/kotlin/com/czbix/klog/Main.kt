package com.czbix.klog

import com.czbix.klog.common.Config
import com.czbix.klog.database.Database
import com.czbix.klog.http.user
import com.czbix.klog.template.SoyHelper
import com.czbix.klog.handler.backend.PostHandler as BackendPostHandler

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val main = Main()
            main.init(args)
            main.run()
        }
    }

    private lateinit var bootstrap: ServerBootstrap

    fun init(args: Array<String>) {
        Config.initConfig()
        Database.initDatabase()
        initSoyHelper()

        bootstrap = ServerBootstrap()
    }

    fun initSoyHelper() {
        SoyHelper.apply {
            addIjDataHook("user") { it.user }
//            addIjDataHook("mapper") { it.getWithType<DefaultHandlerMapper>(HttpContextKey.HANDLER_MAPPER) }
        }
    }

    fun run() = bootstrap.run()
}
