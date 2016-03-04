package com.czbix.klog.http

import com.czbix.klog.common.Config
import com.czbix.klog.utils.now
import io.netty.handler.codec.http.Cookie
import io.netty.handler.codec.http.QueryStringDecoder
import org.apache.commons.codec.binary.Hex
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SecureCookie {
    private const val ALGORITHM = "HmacSHA256"
    private val KEY = Config.getCookieSecureKey()
    private val SPEC = SecretKeySpec(KEY.toByteArray(), ALGORITHM)

    fun createSign(ver: String, time: Long, name: String, value: String): String {
        val toSign = arrayOf(
                ver,
                time.toString(),
                name,
                value,
                "" // keep for sign
        ).joinToString("|")

        val mac = Mac.getInstance(ALGORITHM)
        mac.init(SPEC)
        val sign = Hex.encodeHexString(mac.doFinal(toSign.toByteArray()))

        return sign
    }

    var Cookie.secureValue: String?
        /**
         * The format consists of a version number and a series of
         * fields, the last of which is a signature, all separated
         * by pipes.  All numbers are in decimal format with no
         * leading zeros.  The signature is an HMAC-SHA256 of the
         * whole string up to that point, including the final pipe.
         *
         * The fields are:
         * - format version (i.e. 1; no length prefix)
         * - timestamp (integer seconds since epoch)
         * - value (not encoded; assumed to be ~alphanumeric)
         * - signature (hex-encoded; no length prefix)
         */
        get() {
            val decode = QueryStringDecoder.decodeComponent(value())
            val parts = decode.split('|')
            if (parts.size != 4) return null

            val (version, timestamp, value, signature) = parts
            if (version != "1") return null
            if (timestamp.toLong() > now()) return null

            val sign = createSign(version, timestamp.toLong(), name(), value)
            if (signature != sign) return null

            return value
        }
        set(newValue) {
            if (newValue == null) {
                setValue(null)
            } else {

                val time = TimeUnit.MILLISECONDS.toSeconds(now()) + maxAge()

                val actValue = arrayOf(
                        "1",
                        time.toString(),
                        value(),
                        "" // keep for sign
                ).joinToString("|")
                val result = actValue + createSign("1", time, name(), newValue)
                setValue(result)
            }
        }
}
