package com.czbix.klog.handler

import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.core.DefaultHttpResponse
import com.czbix.klog.http.HttpContext
import com.czbix.klog.soy.Index2SoyInfo.IndexSoyTemplateInfo
import com.czbix.klog.template.SoyHelper
import com.czbix.klog.template.SoyHelper.setData
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import com.czbix.klog.soy.Index2SoyInfo as IndexSoyInfo


class IndexHandler : BaseHttpHandler() {
    override fun getPattern() = "/"

    override fun get(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        val posts = PostDao.getAll().map {
            mapOf(
                    "id" to it.id,
                    "title" to it.title,
                    "text" to it.text
            )
        }

        val render = SoyHelper.newRenderer(IndexSoyInfo.INDEX, context).setData(IndexSoyTemplateInfo.POSTS, posts)
        return newHtmlResponse(render.render())
    }
}
