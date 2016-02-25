package com.czbix.klog.common

import com.google.template.soy.data.SoyMapData
import com.google.template.soy.parseinfo.SoyTemplateInfo
import com.google.template.soy.tofu.SoyTofu

object SoyHelper {
    lateinit var soy: SoyTofu

    fun render(template: SoyTemplateInfo): String {
        return soy.newRenderer(template).render()
    }

    fun render(template: SoyTemplateInfo, name: String, value: Any): String {
        return soy.newRenderer(template)
                .setData(SoyMapData(name, value))
                .render()
    }

    fun render(template: SoyTemplateInfo, data: Map<String, Any>): String {
        return soy.newRenderer(template)
                .setData(data)
                .render()
    }
}