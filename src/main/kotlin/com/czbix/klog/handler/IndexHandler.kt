package com.czbix.klog.handler

import com.czbix.klog.common.SoyHelper
import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.soy.IndexSoyInfo
import com.czbix.klog.soy.IndexSoyInfo.IndexSoyTemplateInfo
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

        response.entity = NStringEntityEx.fromHtml(SoyHelper.render(IndexSoyInfo.INDEX,
                IndexSoyTemplateInfo.POSTS, posts))
    }
}
