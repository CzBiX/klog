package com.czbix.klog.http

enum class HttpContextKey(val key: String) {
    QUERY_DATA("ctx.query.data"),
    POST_DATA("ctx.post.data"),
    COOKIES("ctx.cookies"),
    COOKIES_SET("ctx.cookies.set"),
    USER("ctx.user"),
}
