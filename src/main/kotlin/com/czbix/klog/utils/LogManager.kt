package com.czbix.klog.utils

import kotlin.reflect.KClass
import org.apache.logging.log4j.LogManager as Manager

object LogManager {
    fun getLogger(clazz: KClass<*>) = Manager.getLogger(clazz.java)
}
