package com.czbix.klog.http

import com.czbix.klog.common.Config
import com.czbix.klog.database.dao.UserDao
import com.czbix.klog.http.HttpContextKey.*
import com.czbix.klog.http.SecureCookie.secureValue
import com.czbix.klog.http.core.DefaultHttpPostRequestDecoder
import com.czbix.klog.utils.now
import io.netty.handler.codec.http.*
import java.util.*
import java.util.concurrent.TimeUnit

inline fun <reified T> HttpContext.getWithType(key: HttpContextKey): T? {
    return get(key) as T?
}

inline fun <reified T : Any> HttpContext.getWithType(key: HttpContextKey, block: () -> T?): T? {
    val value = get(key) as T?
    return value ?: block()?.let {
        put(key, it)
        it
    }
}

val HttpContext.args: Map<String, String>
    get() {
        return getWithType(HANDLER_ARGS)!!
    }

val HttpContext.request: HttpRequest
    get() {
        return getWithType(REQUEST)!!
    }

val HttpContext.postData: DefaultHttpPostRequestDecoder
    get() {
        return DefaultHttpPostRequestDecoder(request)
    }

val HttpContext.cookies: Map<String, Cookie>
    get() {
        return getWithType<Map<String, Cookie>>(COOKIES) {
            val cookiesHeader = request.headers().get(HttpHeaderNames.COOKIE)?.toString()
            if (cookiesHeader == null) return@getWithType mapOf()
            ServerCookieDecoder.decode(cookiesHeader).associateBy { it.name() }
        }!!
    }

fun HttpContext.setCookie(name: String, value: String?, domain: String? = null, expiresDate: Date? = null,
                          path: String = "/", expiresDays: Int? = null, httpOnly: Boolean = true,
                          secureValue: Boolean = false) {
    var cookies = getWithType<MutableMap<String, Cookie>>(COOKIES_SET)
    if (cookies == null) {
        cookies = mutableMapOf()
        this[COOKIES_SET] = cookies
    }

    val cookie = DefaultCookie(name, value).let {
        if (secureValue) {
            it.secureValue = value
        }
        if (domain != null) {
            it.setDomain(domain)
        }

        var maxAge = if (expiresDays != null && expiresDate == null) {
            TimeUnit.DAYS.toSeconds(expiresDays.toLong())
        } else if (expiresDate != null) {
            TimeUnit.MILLISECONDS.toSeconds(expiresDate.time - now())
        } else 0
        it.setMaxAge(maxAge)

        it.setPath(path)
        it.isHttpOnly = httpOnly
        if (Config.isCookieSecure()) {
            it.isSecure = true
        }
        return@let it
    }

    cookies[name] = cookie
}

var HttpContext.user: UserDao.User?
    get() {
        var user = getWithType<UserDao.User?>(USER)
        if (user == null) {
            val username = cookies["username"]?.secureValue
            if (username == null) {
                return null
            }

            user = UserDao.get(username)
            user?.let {
                this[USER] = it
            }
        }

        return user
    }
    set(value) {
        if (value == null) {
            remove(USER)
            setCookie("username", null, expiresDays = -365)
        } else {
            this[USER] = value
            setCookie("username", value.username, expiresDays = 365, secureValue = true)
        }
    }
