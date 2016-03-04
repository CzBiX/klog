package com.czbix.klog.http

import com.google.common.collect.ImmutableList

data class UrlSpec(val path: String) {
    private lateinit var regex: Regex
    private lateinit var groupNameByIndex: Array<String>

    init {
        require(path.startsWith('/')) { "must be absolute path: $path" }

        val groups = ImmutableList.builder<String>()
        regex = path.replace(REGEX_KEY) {
            val name = it.groupValues[1]
            groups.add(name)

            "(?<$name>\\w+)"
        }.toRegex()

        groupNameByIndex = groups.build().toTypedArray()
    }

    fun match(url: String): Map<String, String>? {
        val result = regex.matchEntire(url) ?: return null

        val groups = result.groups
        if (groups.size == 1) {
            return mapOf()
        }

        return groups.drop(1).mapIndexedNotNull { i, matchGroup ->
            groupNameByIndex[i] to matchGroup!!.value
        }.toMap()
    }

    fun reverse(data: Map<String, String>): String {
        return path.replace(REGEX_KEY) {
            data[it.groupValues[1]]!!
        }
    }

    companion object {
        private val REGEX_KEY = "<(\\w+)>".toRegex()
    }
}