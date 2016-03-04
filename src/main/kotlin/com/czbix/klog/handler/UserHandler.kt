package com.czbix.klog.handler

import com.czbix.klog.database.dao.UserDao
import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.core.DefaultHttpResponse
import com.czbix.klog.http.postData
import com.czbix.klog.http.user
import com.czbix.klog.soy.UserSoyInfo
import com.czbix.klog.template.SoyHelper
import com.czbix.klog.template.SoyHelper.setData
import io.netty.handler.codec.http.HttpRequest

class UserHandler : BaseHttpHandler() {
    override fun getPattern() = "/user/"

    override fun get(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        val user = context.user
        val html = if (user != null) {
            SoyHelper.newRenderer(UserSoyInfo.SIGNED, context).setData("user", user).render()
        } else {
            SoyHelper.render(UserSoyInfo.SIGN_IN)
        }
        return newHtmlResponse(html)
    }

    override fun post(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        val (username, pwd) = context.postData.use {
            val username = it.getAttr("username").value
            val pwd = it.getAttr("password").value

            listOf(username, pwd)
        }

        val user = UserDao.get(username)

        val renderer = if (user == null || !user.validatePwd(pwd)) {
            SoyHelper.newRenderer(UserSoyInfo.SIGN_IN, context)
                    .setData(UserSoyInfo.SignInSoyTemplateInfo.MSG, "username or password is invalid!")
        } else {
            context.user = user
            SoyHelper.newRenderer(UserSoyInfo.SIGNED, context).setData("user", user)
        }

        return newHtmlResponse(renderer.render())
    }
}
