package com.czbix.klog

import com.czbix.klog.common.Config
import com.czbix.klog.common.SoyHelper
import com.czbix.klog.database.Database
import com.czbix.klog.handler.IndexHandler
import com.czbix.klog.handler.NotFoundHandler
import com.czbix.klog.handler.PostHandler
import com.czbix.klog.handler.UserHandler
import com.czbix.klog.http.interceptor.RequestCookies
import com.czbix.klog.http.interceptor.ResponseCookies
import com.czbix.klog.http.interceptor.ResponseEtag
import com.czbix.klog.http.user
import com.czbix.klog.utils.or
import com.google.template.soy.data.SoyAbstractRecord
import com.google.template.soy.data.SoyRecord
import com.google.template.soy.data.SoyValue
import com.google.template.soy.data.restricted.StringData
import org.apache.http.HttpRequest
import org.apache.http.impl.nio.bootstrap.HttpServer
import org.apache.http.impl.nio.bootstrap.ServerBootstrap
import org.apache.http.impl.nio.reactor.IOReactorConfig
import org.apache.http.nio.protocol.UriHttpAsyncRequestHandlerMapper
import org.apache.http.protocol.HttpContext
import java.util.concurrent.TimeUnit

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val main = Main()
            main.init(args)
            main.run()
        }
    }

    lateinit var httpServer: HttpServer

    fun init(args: Array<String>) {
        Config.initConfig()
        Database.initDatabase()

        val ioReactorConfig = IOReactorConfig.custom().run {
            setSoReuseAddress(true)
            build()
        }

        val handlerMapper = buildHandlerMapper()

        httpServer = ServerBootstrap.bootstrap().run {
            setListenerPort(Config.getPort())
            setIOReactorConfig(ioReactorConfig)
            setServerInfo("Klog Server")

            addInterceptorLast(RequestCookies())

            addInterceptorLast(ResponseEtag())
            addInterceptorLast(ResponseCookies())

            setHandlerMapper(handlerMapper)
            setExceptionLogger(org.apache.http.ExceptionLogger.STD_ERR)
            create()
        }
    }

    fun buildHandlerMapper(): UriHttpAsyncRequestHandlerMapper {
        val handlerMapper = UriHttpAsyncRequestHandlerMapper()

        val handlers = arrayOf(
                IndexHandler(),
                PostHandler(),
                UserHandler(),
                NotFoundHandler()
        )

        handlers.forEach { handlerMapper.register(it.getPattern(), it) }

        return handlerMapper
    }

    fun run() {
        httpServer.start()
        println("Server listening...")
        httpServer.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

        Runtime.getRuntime().addShutdownHook(Thread() {
            println("Shutdown server...")
            httpServer.shutdown(3, TimeUnit.SECONDS);
        })
    }
}
