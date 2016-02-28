package com.czbix.klog.common

import com.google.template.soy.SoyFileSet
import com.google.template.soy.data.SoyMapData
import com.google.template.soy.parseinfo.SoyTemplateInfo
import com.google.template.soy.shared.SoyAstCache
import com.google.template.soy.tofu.SoyTofu
import java.nio.file.Files
import java.nio.file.Paths

object SoyHelper {
    private val IS_DEBUG = Config.isSoyDebug()
    private fun getTemplatePath(): String {
        return Config.TEMPLATE_PATH.toString()
    }
    private val soyFileSet = init(getTemplatePath())

    private val _soy by lazy {
        soyFileSet.compileToTofu()
    }
    private val soy: SoyTofu
        get() {
            return if (IS_DEBUG) soyFileSet.compileToTofu() else _soy
        }

    private fun init(path: String): SoyFileSet {
        val builder = SoyFileSet.builder()
        if (IS_DEBUG) {
            builder.setSoyAstCache(SoyAstCache())
        }

        Files.list(Paths.get(path))
                .map { it.toFile() }
                .filter { it.isFile && it.name.endsWith(".soy") }
                .forEach {
                    if (IS_DEBUG) builder.addVolatile(it) else builder.add(it)
                }

        return builder.build()
    }

    fun newRenderer(template: SoyTemplateInfo): SoyTofu.Renderer {
        return soy.newRenderer(template)
    }

    fun render(template: SoyTemplateInfo): String {
        return newRenderer(template).render()
    }

    fun render(template: SoyTemplateInfo, name: String, value: Any): String {
        return newRenderer(template)
                .setData(SoyMapData(name, value))
                .render()
    }

    fun render(template: SoyTemplateInfo, data: Map<String, Any>): String {
        return newRenderer(template)
                .setData(data)
                .render()
    }
}
