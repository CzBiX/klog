package com.czbix.klog.handler.backend

import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.core.DefaultHttpResponse
import com.czbix.klog.http.postData
import com.czbix.klog.template.SoyHelper
import com.czbix.klog.template.SoyHelper.setData
import io.netty.handler.codec.http.HttpRequest
import com.czbix.klog.soy.Post1SoyInfo as PostSoyInfo

class PostHandler : BackendHandler() {
    override fun getPattern() = "/admin/post/<action>"

    override fun get(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        val action = getAction(request)
        val html = when (action) {
            "new" ->
                SoyHelper.newRenderer(PostSoyInfo.ADD_POST, context).render()
            "list" ->
                SoyHelper.newRenderer(PostSoyInfo.LIST_POST, context).setData("posts", PostDao.getAll()).render()
            else -> throw NotImplementedError("$action action not implemented")
        }

        return newHtmlResponse(html)
    }

    override fun post(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        val (title, text) = context.postData.use {
            val title = it.getAttr("title").value
            val text = it.getAttr("text").value

            listOf(title, text)
        }

        if (title.isNullOrBlank()) {
            return accessDenied()
        }

        PostDao.insert(title!!, text)

        return redirect("/admin/post/list")
    }

    fun getAction(request: HttpRequest): String {
        val uri = request.uri()
        return if (uri.endsWith('/')) "list" else uri.substringAfterLast('/')
    }
}
