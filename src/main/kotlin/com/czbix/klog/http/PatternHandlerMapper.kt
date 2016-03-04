package com.czbix.klog.http

open class PatternHandlerMapper() : HandlerMapper {
    private val handlers: MutableList<HandlerPattern> = mutableListOf()
    private val urlSpecMap: MutableMap<Class<*>, UrlSpec> = mutableMapOf()

    constructor(handlers: Iterable<HttpHandler>) : this() {
        handlers.forEach { add(it) }
    }

    override fun find(url: String): Pair<HttpHandler, Map<String, String>> {
        for (pair in handlers) {
            val result = pair.match(url) ?: continue

            return pair.handler to result
        }

        return handlers.last().handler to mapOf()
    }

    override fun reserve(cls: Class<*>): UrlSpec {
        return urlSpecMap[cls]!!
    }

    protected fun add(handler: HttpHandler) {
        add(HandlerPattern.create(handler))
    }

    private fun add(pattern: HandlerPattern) {
        urlSpecMap[pattern.handler.javaClass] = pattern.spec
        handlers.add(pattern)
    }

    data class HandlerPattern(val spec: UrlSpec, val handler: HttpHandler) {
        fun match(url: String) = spec.match(url)

        companion object {
            fun create(handler: HttpHandler): HandlerPattern {
                val spec = UrlSpec(handler.getPattern())
                return HandlerPattern(spec, handler)
            }
        }
    }
}
