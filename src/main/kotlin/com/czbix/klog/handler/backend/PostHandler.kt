package com.czbix.klog.handler.backend

import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.http.postData
import com.czbix.klog.template.SoyHelper
import com.czbix.klog.template.SoyHelper.setData
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext
import com.czbix.klog.soy.Post2SoyInfo as PostSoyInfo

class PostHandler : BackendHandler() {
    override fun getPattern() = "/admin/post/*"

    override fun get(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        val action = getAction(request)
        when (action) {
            "add" ->
                response.entity = NStringEntityEx.fromHtml(SoyHelper.newRenderer(PostSoyInfo.ADD_POST, context).render())
            "list" ->
                response.entity = NStringEntityEx.fromHtml(SoyHelper.newRenderer(PostSoyInfo.LIST_POST, context)
                        .setData("posts", PostDao.getAll()).render())
            else -> throw NotImplementedError("$action action not implemented")
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

        redirect(response, "/admin/post/")
    }

    fun getAction(request: HttpRequest): String {
        val uri = request.requestLine.uri
        return if (uri.endsWith('/')) "list" else uri.substringAfterLast('/')
    }
}
