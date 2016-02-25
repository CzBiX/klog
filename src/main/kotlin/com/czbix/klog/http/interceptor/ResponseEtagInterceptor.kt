package com.czbix.klog.http.interceptor

import org.apache.http.*
import org.apache.http.protocol.HttpContext
import org.apache.http.protocol.HttpCoreContext
import org.apache.http.util.EntityUtils
import java.util.zip.CRC32

fun HttpContext.getRequest(): HttpRequest? {
    val context = HttpCoreContext.adapt(this)
    return context.request
}

class ResponseEtagInterceptor : HttpResponseInterceptor {
    companion object {
        val MAX_SIZE_LIMIT = 8192
    }

    override fun process(response: HttpResponse, context: HttpContext) {
        val request = context.getRequest() ?: return
        val entity = response.entity

        if (!response.containsHeader(HttpHeaders.ETAG) && entity.isRepeatable
                && entity.contentType.value.startsWith("text/")
                && entity.contentLength < MAX_SIZE_LIMIT) {
            val crc = CRC32()
            val buf = EntityUtils.toByteArray(entity)
            crc.update(buf)

            val value = crc.value
            val etag = java.lang.Long.toHexString(value);

            val clientEtag = request.getLastHeader(HttpHeaders.IF_NONE_MATCH).value
            if (etag.equals(clientEtag)) {
                response.setStatusCode(HttpStatus.SC_NOT_MODIFIED)
            }

            response.addHeader(HttpHeaders.ETAG, etag)
        }
    }
}
