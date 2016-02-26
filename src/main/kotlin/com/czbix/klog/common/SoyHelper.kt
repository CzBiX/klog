package com.czbix.klog.common

import com.google.template.soy.SoyFileSet
import com.google.template.soy.data.SoyMapData
import com.google.template.soy.parseinfo.SoyTemplateInfo
import com.google.template.soy.tofu.SoyTofu
import java.nio.file.Files
import java.nio.file.Paths

object SoyHelper {
    private val IS_DEBUG = Config.getSoyDebug()
    private fun getTemplatePath(): String {
        return Config.TEMPLATE_PATH.toString()
    }

    private val soy: SoyTofu by lazy {
        init(getTemplatePath())
    }

    private fun init(path: String): SoyTofu {
        val builder = SoyFileSet.builder()
        Files.list(Paths.get(path))
                .map { it.toFile() }
                .filter { it.isFile }
                .forEach { builder.add(it) }

        return builder.build().compileToTofu()
    }

    private fun getSoyInstance(): SoyTofu {
        return if (IS_DEBUG) init(getTemplatePath()) else soy
    }

    fun render(template: SoyTemplateInfo): String {
        return getSoyInstance().newRenderer(template).render()
    }

    fun render(template: SoyTemplateInfo, name: String, value: Any): String {
        return getSoyInstance().newRenderer(template)
                .setData(SoyMapData(name, value))
                .render()
    }

    fun render(template: SoyTemplateInfo, data: Map<String, Any>): String {
        return getSoyInstance().newRenderer(template)
                .setData(data)
                .render()
    }
}