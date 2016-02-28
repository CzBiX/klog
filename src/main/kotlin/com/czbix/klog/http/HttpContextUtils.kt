package com.czbix.klog.http

import com.czbix.klog.common.Config
import com.czbix.klog.database.dao.UserDao
import com.czbix.klog.utils.now
import com.czbix.klog.utils.or
import com.google.common.collect.ImmutableMap
import org.apache.http.Consts
import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.HttpRequest
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.protocol.HttpContext
import org.apache.http.protocol.HttpCoreContext
import java.net.HttpCookie
import java.util.*
import java.util.concurrent.TimeUnit

inline fun <reified T> HttpContext.getAttributeWithType(key: String): T {
    return getAttribute(key) as T
}

val HttpContext.request: HttpRequest
    get() {
        return getAttributeWithType(HttpCoreContext.HTTP_REQUEST)
    }

val HttpContext.queryData: Map<String, String>
    get() {
        val cachedData = getAttributeWithType<Map<String, String>>(HttpContextKey.QUERY_DATA.key)
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

val HttpContext.postData: Map<String, String>
    get() {
        var map = getAttributeWithType<Map<String, String>?>(HttpContextKey.POST_DATA.key)
        if (map == null) {
            val request = request
            if (request !is HttpEntityEnclosingRequest) {
                return Collections.emptyMap()
            }

            val data = URLEncodedUtils.parse(request.entity)
            val builder = ImmutableMap.builder<String, String>()
            data.forEach { builder.put(it.name, it.value) }

            map = builder.build()
            setAttribute(HttpContextKey.POST_DATA.key, map)
        }

        return map!!
    }

val HttpContext.cookies: Map<String, HttpCookie>
    get() {
        return getAttributeWithType<Map<String, HttpCookie>?>(HttpContextKey.COOKIES.key).or(mapOf())
    }

fun HttpContext.setCookie(name: String, value: String?, domain: String? = null, expiresDate: Date? = null,
                          path: String = "/", expiresDays: Int? = null, httpOnly: Boolean = true,
                          secureValue: Boolean = false) {
    var cookies = getAttributeWithType<MutableMap<String, HttpCookie>?>(HttpContextKey.COOKIES_SET.key)
    if (cookies == null) {
        cookies = mutableMapOf()
        setAttribute(HttpContextKey.COOKIES_SET.key, cookies)
    }

    val cookie = HttpCookie(name, value).let {
        if (secureValue) {
            it.secureValue = value
        }
        if (domain != null) {
            it.domain = domain
        }

        var maxAge = if (expiresDays != null && expiresDate == null) {
            TimeUnit.DAYS.toSeconds(expiresDays.toLong())
        } else if (expiresDate != null) {
            TimeUnit.MILLISECONDS.toSeconds(expiresDate.time - now())
        } else 0
        it.maxAge = maxAge

        it.path = path
        it.isHttpOnly = httpOnly
        if (Config.isCookieSecure()) {
            it.secure = true
        }
        return@let it
    }

    cookies[name] = cookie
}

var HttpContext.user: UserDao.User?
    get() {
        var user = getAttributeWithType<UserDao.User?>(HttpContextKey.USER.key)
        if (user == null) {
            val username = cookies["username"]?.secureValue ?: return null

            user = UserDao.get(username)
            setAttribute(HttpContextKey.USER.key, user)
        }

        return user
    }
    set(value) {
        setAttribute(HttpContextKey.USER.key, value)
        if (value == null) {
            setCookie("username", null, expiresDays = -365)
        } else {
            setCookie("username", value.username, expiresDays = 365, secureValue = true)
        }
    }
