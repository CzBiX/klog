package com.czbix.klog.handler

import com.czbix.klog.http.ContentType
import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.HttpHandler
import com.czbix.klog.http.core.DefaultHttpResponse
import com.czbix.klog.utils.LogManager
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.*

abstract class BaseHttpHandler : HttpHandler {
    companion object {
        private val logger = LogManager.getLogger(BaseHttpHandler::class)

        fun newHtmlResponse(content: CharSequence, status: HttpResponseStatus = OK): DefaultHttpResponse {
            return DefaultHttpResponse(status, fullContent = newContent(content)).apply {
                setContentType(ContentType.HTML)
            }
        }

        fun newContent(content: CharSequence?): ByteBuf {
            return if (content == null) {
                Unpooled.EMPTY_BUFFER
            } else {
                Unpooled.copiedBuffer(content, Charsets.UTF_8)
            }
        }
    }

    override fun handleRequest(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        try {
            return when (request.method()) {
                HttpMethod.GET -> get(request, context)
                HttpMethod.POST -> post(request, context)
                else -> throw NotImplementedError()
            }
        } catch (e: Exception) {
            logger.error("handle request failed", e)

            return DefaultHttpResponse(INTERNAL_SERVER_ERROR).apply {
                setContentType(ContentType.PLAIN_TEXT)
                newContent(e.message)
            }
        }
    }

    protected open fun get(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        throw NotImplementedError()
    }

    protected open fun post(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        throw NotImplementedError()
    }

    protected fun redirect(url: String, permanently: Boolean = false): DefaultHttpResponse {
        require(url.startsWith('/')) {
            "$url is not absolute url"
        }

        val status = if (permanently) MOVED_PERMANENTLY else SEE_OTHER
        return DefaultHttpResponse(status).apply {
            headers.add(HttpHeaderNames.LOCATION, url)
        }
    }

    protected fun accessDenied(): DefaultHttpResponse {
        return DefaultHttpResponse(FORBIDDEN)
    }
}
