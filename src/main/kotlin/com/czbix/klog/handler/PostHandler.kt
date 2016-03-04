package com.czbix.klog.handler

import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.args
import com.czbix.klog.http.core.DefaultHttpResponse
import com.czbix.klog.soy.Post2SoyInfo
import com.czbix.klog.template.SoyHelper
import com.czbix.klog.template.SoyHelper.setData
import io.netty.handler.codec.http.HttpRequest

class PostHandler : BaseHttpHandler() {
    override fun getPattern() = "/post/<id>"

    override fun get(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        val id = context.args["id"]!!.toInt()
        val post = PostDao.get(id)!!

        val render = SoyHelper.newRenderer(Post2SoyInfo.POST, context).setData(Post2SoyInfo.PostSoyTemplateInfo.POST, post)
        return newHtmlResponse(render.render())
    }
}
