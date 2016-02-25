package com.czbix.klog

import com.czbix.klog.common.SoyHelper
import com.czbix.klog.handler.IndexHandler
import com.czbix.klog.handler.NotFoundHandler
import com.czbix.klog.http.interceptor.ResponseEtagInterceptor
import com.google.template.soy.SoyFileSet
import com.google.template.soy.tofu.SoyTofu
import org.apache.http.impl.nio.bootstrap.HttpServer
import org.apache.http.impl.nio.bootstrap.ServerBootstrap
import org.apache.http.nio.protocol.UriHttpAsyncRequestHandlerMapper
import java.nio.file.Files
import java.nio.file.Paths
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
        SoyHelper.soy = buildSoy()
        val handlerMapper = buildHandlerMapper()

        httpServer = ServerBootstrap.bootstrap()
                .setListenerPort(8080)
                .setServerInfo("Klog Server")
                .addInterceptorLast(ResponseEtagInterceptor())
                .setHandlerMapper(handlerMapper)
                .setExceptionLogger(org.apache.http.ExceptionLogger.STD_ERR)
                .create()
    }

    fun buildSoy(): SoyTofu {
        val builder = SoyFileSet.builder()
        Files.list(Paths.get("data/template"))
                .map { it.toFile() }
                .filter { it.isFile }
                .forEach { builder.add(it) }

        return builder.build().compileToTofu()
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
