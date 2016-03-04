package com.czbix.klog.http

enum class HttpContextKey(val key: String) {
    REQUEST("http.request"),
    QUERY_DATA("ctx.query.data"),
    POST_DATA("ctx.post.data"),
    COOKIES("ctx.cookies"),
    COOKIES_SET("ctx.cookies.set"),
    USER("ctx.user"),
    HANDLER_ARGS("handler.args"),
    HANDLER_MAPPER("handler.mapper"),
}
