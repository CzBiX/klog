package com.czbix.klog.handler

import com.czbix.klog.template.SoyHelper
import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.soy.IndexSoyInfo
import com.czbix.klog.soy.IndexSoyInfo.IndexSoyTemplateInfo
import com.google.template.soy.data.SoyMapData
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext


class IndexHandler : BaseRequestHandler() {
    override fun getPattern() = "/"

    override fun get(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        val posts = PostDao.getAll().map {
            mapOf(
                    "id" to it.id,
                    "title" to it.title,
                    "text" to it.text
            )
        }

        val render = SoyHelper.newRenderer(IndexSoyInfo.INDEX, context).setData(mapOf(IndexSoyTemplateInfo.POSTS to posts))
        response.entity = NStringEntityEx.fromHtml(render.render())
    }
}
