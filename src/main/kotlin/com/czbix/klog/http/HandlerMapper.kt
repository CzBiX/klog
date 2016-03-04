package com.czbix.klog.http

interface HandlerMapper {
    fun find(url: String): Pair<HttpHandler, Map<String, String>>
    fun reserve(cls: Class<*>): UrlSpec
}