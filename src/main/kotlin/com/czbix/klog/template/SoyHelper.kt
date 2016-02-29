package com.czbix.klog.template

import com.czbix.klog.common.Config
import com.google.template.soy.SoyFileSet
import com.google.template.soy.data.SoyMapData
import com.google.template.soy.data.SoyValue
import com.google.template.soy.parseinfo.SoyTemplateInfo
import com.google.template.soy.shared.SoyAstCache
import com.google.template.soy.tofu.SoyTofu
import org.apache.http.protocol.HttpContext
import java.nio.file.Files
import java.nio.file.Paths

object SoyHelper {
    private val IS_DEBUG = Config.isSoyDebug()
    private fun getTemplatePath(): String {
        return Config.TEMPLATE_PATH.toString()
    }
    private val soyFileSet = init(getTemplatePath())
    private val ijDataHook = mutableMapOf<String, (HttpContext) -> SoyValue?>()

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

        Files.walk(Paths.get(path))
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

    fun newRenderer(template: SoyTemplateInfo, context: HttpContext): SoyTofu.Renderer {
        val ijData = SoyMapData(ijDataHook.map { it.key to it.value(context)}.toMap())
        return soy.newRenderer(template).setIjData(ijData)
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

    fun addIjDataHook(name: String, hook: (HttpContext) -> SoyValue?) {
        ijDataHook[name] = hook
    }

    fun SoyTofu.Renderer.setData(name: String, value: SoyValue) = setData(mapOf(name to value))
}
