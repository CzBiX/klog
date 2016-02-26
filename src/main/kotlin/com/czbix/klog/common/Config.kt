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

    fun readConfig() {
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

    fun getPort(): Int {
        return getInt("port")
    }

    fun getSoyDebug(): Boolean {
        return getBool("soy.debug")
    }
}