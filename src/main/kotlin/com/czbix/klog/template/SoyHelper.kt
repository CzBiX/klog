package com.czbix.klog.template

import com.czbix.klog.common.Config
import com.czbix.klog.http.HttpContext
import com.google.inject.Guice
import com.google.template.soy.SoyFileSet
import com.google.template.soy.SoyModule
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
    private val ijDataHook = mutableMapOf<String, (HttpContext) -> Any?>()

    private val _soy by lazy {
        soyFileSet.compileToTofu()
    }
    private val soy: SoyTofu
        get() {
            return if (IS_DEBUG) soyFileSet.compileToTofu() else _soy
        }

    private fun init(path: String): SoyFileSet {
        val injector = Guice.createInjector(SoyModule(), SoyCustomModule())
        val builder = injector.getInstance(SoyFileSet.Builder::class.java)

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
        val ijData = ijDataHook.mapValues { it.value(context) }
        return soy.newRenderer(template).setIjData(ijData)
    }

    fun render(template: SoyTemplateInfo): String {
        return newRenderer(template).render()
    }

    fun addIjDataHook(name: String, hook: (HttpContext) -> Any?) {
        ijDataHook[name] = hook
    }

    fun SoyTofu.Renderer.setData(name: String, value: Any) = setData(mapOf(name to value))
}
