package com.example.booktourdemo.manager.api.zalopay.Helper.HMac

import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HMacUtil {
    const val HMACMD5: String = "HmacMD5"
    const val HMACSHA1: String = "HmacSHA1"
    const val HMACSHA256: String = "HmacSHA256"
    const val HMACSHA512: String = "HmacSHA512"
    val UTF8CHARSET = StandardCharsets.UTF_8

    val HMACS = listOf("UnSupport", "HmacSHA256", "HmacMD5", "HmacSHA384", "HMacSHA1", "HmacSHA512")

    private fun HMacEncode(algorithm: String, key: String, data: String): ByteArray? {
        return try {
            val macGenerator = Mac.getInstance(algorithm)
            val signingKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), algorithm)
            macGenerator.init(signingKey)
            val dataByte = data.toByteArray(charset("UTF-8"))
            macGenerator.doFinal(dataByte)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Calculating a message authentication code (MAC) involving a cryptographic
     * hash function in combination with a secret cryptographic key.
     *
     * The result will be represented base64-encoded string.
     *
     * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
     * @param key A secret cryptographic key
     * @param data The message to be authenticated
     * @return Base64-encoded HMAC String
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun HMacBase64Encode(algorithm: String, key: String, data: String): String? {
        val hmacEncodeBytes = HMacEncode(algorithm, key, data) ?: return null
        return Base64.getEncoder().encodeToString(hmacEncodeBytes)
    }

    /**
     * Calculating a message authentication code (MAC) involving a cryptographic
     * hash function in combination with a secret cryptographic key.
     *
     * The result will be represented hex string.
     *
     * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
     * @param key A secret cryptographic key
     * @param data The message to be authenticated
     * @return Hex HMAC String
     */
    fun HMacHexStringEncode(algorithm: String, key: String, data: String): String? {
        val hmacEncodeBytes = HMacEncode(algorithm, key, data) ?: return null
        return HexStringUtil.byteArrayToHexString(hmacEncodeBytes)
    }
}