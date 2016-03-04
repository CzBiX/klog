package com.czbix.klog.http.core

import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.multipart.Attribute
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import java.io.Closeable

class DefaultHttpPostRequestDecoder(request: HttpRequest) : HttpPostRequestDecoder(request), Closeable {
    override fun close() {
        destroy()
    }

    fun getAttr(key: String): Attribute = getBodyHttpData(key) as Attribute
    fun getAttrs(key: String): List<Attribute> = getBodyHttpDatas(key) as List<Attribute>
}