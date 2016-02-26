package com.czbix.klog

import com.czbix.klog.common.Config
import com.czbix.klog.database.Database
import com.czbix.klog.handler.IndexHandler
import com.czbix.klog.handler.NotFoundHandler
import com.czbix.klog.http.interceptor.ResponseEtagInterceptor
import org.apache.http.impl.nio.bootstrap.HttpServer
import org.apache.http.impl.nio.bootstrap.ServerBootstrap
import org.apache.http.impl.nio.reactor.IOReactorConfig
import org.apache.http.nio.protocol.UriHttpAsyncRequestHandlerMapper
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
        Config.readConfig()
        Database.initDatabase()

        val ioReacterConfig = IOReactorConfig.custom()
                .setSoReuseAddress(true)
                .build()
        val handlerMapper = buildHandlerMapper()

        httpServer = ServerBootstrap.bootstrap()
                .setListenerPort(Config.getPort())
                .setIOReactorConfig(ioReacterConfig)
                .setServerInfo("Klog Server")
                .addInterceptorLast(ResponseEtagInterceptor())
                .setHandlerMapper(handlerMapper)
                .setExceptionLogger(org.apache.http.ExceptionLogger.STD_ERR)
                .create()
    }

    fun buildHandlerMapper(): UriHttpAsyncRequestHandlerMapper {
        val handlerMapper = UriHttpAsyncRequestHandlerMapper()

        val handlers = arrayOf(
                IndexHandler(),
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
