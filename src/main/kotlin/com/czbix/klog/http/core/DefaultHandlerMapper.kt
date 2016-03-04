package com.czbix.klog.http.core

import com.czbix.klog.handler.IndexHandler
import com.czbix.klog.handler.NotFoundHandler
import com.czbix.klog.handler.PostHandler
import com.czbix.klog.handler.UserHandler
import com.czbix.klog.handler.backend.AdminHandler
import com.czbix.klog.http.PatternHandlerMapper
import com.czbix.klog.handler.backend.PostHandler as BackendPostHandler

class DefaultHandlerMapper : PatternHandlerMapper() {
    init {
        arrayOf(
                IndexHandler(),
                UserHandler(),
                PostHandler(),
                // backend handler
                AdminHandler(),
                BackendPostHandler(),
                // not found always be the last one
                NotFoundHandler()
        ).forEach { add(it) }
    }
}
