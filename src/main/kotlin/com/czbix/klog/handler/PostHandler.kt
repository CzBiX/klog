package com.czbix.klog.handler

import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.template.SoyHelper
import com.czbix.klog.template.SoyHelper.setData
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext
import com.czbix.klog.soy.Post2SoyInfo as PostSoyInfo

class PostHandler : BaseRequestHandler() {
    override fun getPattern() = "/post/*"

    override fun get(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        val id = request.requestLine.uri.substringAfterLast('/', "").toInt()
        val post = PostDao.get(id) ?: return NotFoundHandler().get(request, response, context)
        val postData = post.let {
            mapOf(
                    "id" to it.id,
                    "title" to it.title,
                    "text" to it.text
            )
        }

        response.entity = NStringEntityEx.fromSoy(SoyHelper.newRenderer(PostSoyInfo.POST, context)
                .setData(PostSoyInfo.PostSoyTemplateInfo.POST, postData))
    }
}
