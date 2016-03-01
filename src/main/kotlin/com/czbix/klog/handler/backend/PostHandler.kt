package com.czbix.klog.handler.backend

import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.http.postData
import com.czbix.klog.template.SoyHelper
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext
import com.czbix.klog.soy.Post1SoyInfo as PostSoyInfo

class PostHandler : BackendHandler() {
    override fun getPattern() = "/admin/post/*"

    override fun get(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        val action = getAction(request)
        when (action) {
            "add" ->
                response.entity = NStringEntityEx.fromHtml(SoyHelper.newRenderer(PostSoyInfo.ADD_POST, context).render())
            else -> throw NotImplementedError()
        }
    }

    override fun post(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        val data = context.postData
        val title = data["title"]
        val text = data["text"]

        if (title.isNullOrBlank()) {
            return
        }

        PostDao.insert(title!!, text)

        redirect(response, "/")
    }

    fun getAction(request: HttpRequest): String {
        return request.requestLine.uri.substringAfterLast('/')
    }
}