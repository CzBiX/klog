package com.czbix.klog.http.core

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.*
import io.netty.handler.stream.ChunkedInput

data class DefaultHttpResponse(
        var status: HttpResponseStatus = HttpResponseStatus.OK,
        var headers: HttpHeaders = DefaultHttpHeaders(),
        var fullContent: ByteBuf? = null,
        var chunkedContent: ChunkedInput<ByteBuf>? = null) {

    fun setContentType(type: String) {
        headers.add(HttpHeaderNames.CONTENT_TYPE, type)
    }

    fun toFullHttpResponse(): DefaultFullHttpResponse {
        return DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, fullContent ?: Unpooled.EMPTY_BUFFER).apply {
            headers().setAll(headers)
        }
    }
}