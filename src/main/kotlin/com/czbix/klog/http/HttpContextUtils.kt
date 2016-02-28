package com.czbix.klog.http

import com.czbix.klog.utils.or
import com.google.common.collect.ImmutableMap
import org.apache.http.Consts
import org.apache.http.HttpRequest
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.protocol.HttpContext
import org.apache.http.protocol.HttpCoreContext

val HttpContext.request: HttpRequest
    get() {
        return getAttribute(HttpCoreContext.HTTP_REQUEST) as HttpRequest
    }

val HttpContext.queryData: Map<String, String>
    get() {
        @Suppress("UNCHECKED_CAST")
        val cachedData = getAttribute(HttpContextKey.QUERY_DATA.key)
                as Map<String, String>?
        return cachedData.or {
            val uri = request.requestLine.uri
            val data = URLEncodedUtils.parse(uri, Consts.UTF_8)
                    .fold(ImmutableMap.builder<String, String>(), { builder, pair ->
                        builder.apply { put(pair.name, pair.value) }
                    }).build()
            setAttribute(HttpContextKey.QUERY_DATA.key, data)

            return@or data
        }
    }
