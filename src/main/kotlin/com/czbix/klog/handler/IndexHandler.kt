package com.czbix.klog.handler

import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.soy.Index2SoyInfo.IndexSoyTemplateInfo
import com.czbix.klog.template.SoyHelper
import com.czbix.klog.template.SoyHelper.setData
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext
import com.czbix.klog.soy.Index2SoyInfo as IndexSoyInfo


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

        val render = SoyHelper.newRenderer(IndexSoyInfo.INDEX, context).setData(IndexSoyTemplateInfo.POSTS, posts)
        response.entity = NStringEntityEx.fromHtml(render.render())
    }
}
