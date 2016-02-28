package com.czbix.klog.common

import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object Config {
    val DATA_PATH = Paths.get("data")
    val CONFIG_PATH = DATA_PATH.resolve("app.conf")
    val DATABASE_PATH = DATA_PATH.resolve("data.db")
    val TEMPLATE_PATH = DATA_PATH.resolve("template")
    lateinit var props: Properties

    fun initConfig() {
        Files.newInputStream(CONFIG_PATH).use { fis ->
            props = Properties().apply {
                load(fis)
            }
        }
    }

    private fun getString(key: String): String {
        return props.getProperty(key) ?: throw IllegalStateException("missing config: $key")
    }

    private fun getBool(key: String): Boolean {
        return getString(key).toBoolean()
    }

    private fun getInt(key: String): Int {
        return getString(key).toInt()
    }

    fun getHost(): String = getString("host")

    fun getPort(): Int = getInt("port")

    fun isSoyDebug(): Boolean = getBool("soy.debug")

    fun isCookieSecure(): Boolean = getBool("cookie.is_secure")

    fun getCookieSecureKey(): String = getString("cookie.secure.key")
}
