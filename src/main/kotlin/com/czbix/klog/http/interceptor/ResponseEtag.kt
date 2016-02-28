package com.czbix.klog.http.interceptor

import com.czbix.klog.http.request
import org.apache.http.*
import org.apache.http.protocol.HttpContext
import org.apache.http.protocol.HttpCoreContext
import org.apache.http.util.EntityUtils
import java.util.zip.CRC32

class ResponseEtag : HttpResponseInterceptor {
    companion object {
        val MAX_SIZE_LIMIT = 8192
    }

    override fun process(response: HttpResponse, context: HttpContext) {
        if (response.statusLine.statusCode != HttpStatus.SC_OK) return
        val request = context.request

        val entity = response.entity
        if (!response.containsHeader(HttpHeaders.ETAG)
                && entity.isRepeatable
                && entity.contentType.value.startsWith("text/")
                && entity.contentLength < MAX_SIZE_LIMIT) {
            val crc = CRC32()
            val buf = EntityUtils.toByteArray(entity)
            crc.update(buf)

            val value = crc.value
            val etag = java.lang.Long.toHexString(value);

            val clientEtag = request.getLastHeader(HttpHeaders.IF_NONE_MATCH)?.value
            if (etag.equals(clientEtag)) {
                response.setStatusCode(HttpStatus.SC_NOT_MODIFIED)
            }

            response.addHeader(HttpHeaders.ETAG, etag)
        }
    }
}
