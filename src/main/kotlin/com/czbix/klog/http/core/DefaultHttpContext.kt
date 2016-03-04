package com.czbix.klog.http.core

import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.HttpContextKey

class DefaultHttpContext private constructor(val map: MutableMap<HttpContextKey, Any>) : HttpContext {
    constructor() : this(mutableMapOf()) {}

    override fun clear() {
        map.clear()
    }

    override val entries: MutableSet<MutableMap.MutableEntry<HttpContextKey, Any>>
        get() = map.entries
    override val keys: MutableSet<HttpContextKey>
        get() = map.keys

    override fun put(key: HttpContextKey, value: Any): Any? {
        return map.put(key, value)
    }

    override fun putAll(from: Map<out HttpContextKey, Any>) {
        map.putAll(from)
    }

    override fun remove(key: HttpContextKey): Any? {
        return map.remove(key)
    }

    override val values: MutableCollection<Any>
        get() = map.values

    override fun containsKey(key: HttpContextKey): Boolean {
        return map.containsKey(key)
    }

    override fun containsValue(value: Any): Boolean {
        return map.containsValue(value)
    }

    override fun get(key: HttpContextKey): Any? {
        return map[key]
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    override val size: Int
        get() = map.size
}