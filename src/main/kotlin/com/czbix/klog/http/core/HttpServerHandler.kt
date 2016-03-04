package com.czbix.klog.http.core

import com.czbix.klog.http.HttpContextKey
import com.czbix.klog.http.HttpRequestInterceptor
import com.czbix.klog.http.HttpResponseInterceptor
import com.czbix.klog.http.interceptor.ResponseConnection
import com.czbix.klog.http.interceptor.ResponseContent
import com.czbix.klog.http.interceptor.ResponseEtag
import com.czbix.klog.http.interceptor.ResponseServer
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.HttpChunkedInput
import io.netty.handler.codec.http.HttpHeaderUtil
import io.netty.handler.codec.http.HttpRequest
import org.apache.logging.log4j.LogManager

class HttpServerHandler : SimpleChannelInboundHandler<HttpRequest>(HttpRequest::class.java) {
    companion object {
        private val logger = LogManager.getLogger(HttpServerHandler::class.java)

        private fun initResponseInterceptors(): List<HttpResponseInterceptor> {
            return listOf(
                    ResponseContent(),
                    ResponseEtag(),
                    ResponseConnection(),
                    ResponseServer()
            )
        }

        private fun initRequestInterceptors(): List<HttpRequestInterceptor> {
            return listOf()
        }
    }

    private val requestInterceptors = initRequestInterceptors()
    private val responseInterceptors = initResponseInterceptors()
    private val handlerMapper = DefaultHandlerMapper()

    override fun messageReceived(context: ChannelHandlerContext, request: HttpRequest) {
        logger.trace("request: {} {}", request.method(), request.uri())

        val httpContext = DefaultHttpContext()
        httpContext[HttpContextKey.REQUEST] = request
        httpContext[HttpContextKey.HANDLER_MAPPER] = handlerMapper

        requestInterceptors.forEach {
            it.process(request, httpContext)
        }

        val (handler, data) = handlerMapper.find(request.uri())
        httpContext[HttpContextKey.HANDLER_ARGS] = data

        val (chunkedContent, response) =handler.handleRequest(request, httpContext).let {
            it.chunkedContent to it.toFullHttpResponse()
        }

        responseInterceptors.forEach {
            it.process(response, httpContext)
        }

        val keepAlive = HttpHeaderUtil.isKeepAlive(response)

        context.write(response).let {
            if (chunkedContent == null) {
                context.flush()
                it
            } else {
                context.writeAndFlush(HttpChunkedInput(chunkedContent))
            }
        }.run {
            if (!keepAlive) {
                addListener(ChannelFutureListener.CLOSE)
            }
        }

    }
}
