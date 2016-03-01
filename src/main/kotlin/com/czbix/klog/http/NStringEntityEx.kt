package com.czbix.klog.http

import org.apache.http.Consts
import org.apache.http.entity.ContentType
import org.apache.http.nio.entity.NStringEntity

class NStringEntityEx(html: String, contentType: ContentType) : NStringEntity(html, contentType) {
    companion object {
        val CONTENT_TYPE_HTML = ContentType.create("text/html", Consts.UTF_8)
        val CONTENT_TYPE_TEXT = ContentType.create("text/plain", Consts.UTF_8)

        fun fromText(text: String): NStringEntity {
            return NStringEntity(text, CONTENT_TYPE_TEXT)
        }

        fun fromHtml(html: String): NStringEntity {
            return NStringEntity(html, CONTENT_TYPE_HTML)
        }

        fun fromJson(json: String): NStringEntity {
            return NStringEntity(json, ContentType.APPLICATION_JSON)
        }
    }
}
