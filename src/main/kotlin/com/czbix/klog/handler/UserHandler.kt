package com.czbix.klog.handler

import com.czbix.klog.common.SoyHelper
import com.czbix.klog.database.dao.UserDao
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.http.postData
import com.czbix.klog.http.user
import com.czbix.klog.soy.NotFoundSoyInfo
import com.czbix.klog.soy.UserSoyInfo
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext

class UserHandler : BaseRequestHandler() {
    override fun getPattern(): String = "/user/*"

    override fun get(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        val user = context.user
        val html = if (user != null) {
            SoyHelper.render(UserSoyInfo.SIGNED, "user", user.let {
                mapOf(
                        "displayName" to it.displayName
                )
            })
        } else {
            SoyHelper.render(UserSoyInfo.SIGN_IN)
        }
        response.entity = NStringEntityEx.fromHtml(html)
    }

    override fun post(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        val params = context.postData
        val user = UserDao.get(params["username"]!!)
        val pwd = params["password"]!!

        val html = if (user == null || !user.validatePwd(pwd)) {
            SoyHelper.render(UserSoyInfo.SIGN_IN, UserSoyInfo.SignInSoyTemplateInfo.MSG, "username or password is invalid!")
        } else {
            context.user = user
            SoyHelper.render(UserSoyInfo.SIGNED, "user", user.let {
                mapOf(
                        "displayName" to it.displayName
                )
            })
        }

        response.entity = NStringEntityEx.fromHtml(html)
    }
}
