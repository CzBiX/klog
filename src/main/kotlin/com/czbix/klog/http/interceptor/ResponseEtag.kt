package com.czbix.klog.http.interceptor

import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.HttpResponseInterceptor
import com.czbix.klog.http.request
import com.czbix.klog.utils.toHexString
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import java.util.zip.CRC32

class ResponseEtag : HttpResponseInterceptor {
    companion object {
        const val MAX_SIZE_LIMIT = 8192
    }

    override fun process(response: HttpResponse, context: HttpContext) {
        if (response.status() != HttpResponseStatus.OK) return
        val request = context.request

        if (response !is FullHttpResponse) return
        val content = response.content()
        if (content.readableBytes() in 0..MAX_SIZE_LIMIT
                && (response.headers().get(HttpHeaderNames.CONTENT_TYPE) ?: "").startsWith("text/")) {
            val crc = CRC32()
            val buf = content.array()
            crc.update(buf)

            val value = crc.value
            val etag = value.toHexString()

            val clientEtag = request.headers().get(HttpHeaderNames.IF_NONE_MATCH)
            if (etag.equals(clientEtag)) {
                // don't return content
                content.clear()
                response.setStatus(HttpResponseStatus.NOT_MODIFIED)
            }

            response.headers().add(HttpHeaderNames.ETAG, etag)
        }
    }
}
