package com.czbix.klog.http

import org.apache.http.Consts
import org.apache.http.entity.ContentType
import org.apache.http.nio.entity.NStringEntity

class NStringEntityEx(html: String, contentType: ContentType) : NStringEntity(html, contentType) {
    companion object {
        val CONTENT_TYPE_HTML = ContentType.create("text/html", Consts.UTF_8)
        val CONTENT_TYPE_TEXT = ContentType.create("text/plain", Consts.UTF_8)

        fun fromText(html: String): NStringEntity {
            return NStringEntity(html, CONTENT_TYPE_TEXT)
        }

        fun fromHtml(html: String): NStringEntity {
            return NStringEntity(html, CONTENT_TYPE_HTML)
        }

        fun fromJson(html: String): NStringEntity {
            return NStringEntity(html, ContentType.APPLICATION_JSON)
        }
    }
}
