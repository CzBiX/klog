package com.czbix.klog.handler

import com.czbix.klog.common.SoyHelper
import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.soy.PostSoyInfo
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext

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

        response.entity = NStringEntityEx.fromHtml(SoyHelper.render(PostSoyInfo.POST,
                PostSoyInfo.PostSoyTemplateInfo.POST, postData))
    }
}
