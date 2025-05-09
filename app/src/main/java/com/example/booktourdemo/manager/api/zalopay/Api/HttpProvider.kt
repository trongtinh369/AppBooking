package com.example.booktourdemo.manager.api.zalopay.Api

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpProvider {
    fun sendPost(URL: String, formBody: RequestBody): JSONObject? {
        var data: JSONObject? = null
        try {
            val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                )
                .build()

            val client = OkHttpClient.Builder()
                .connectionSpecs(listOf(spec))
                .callTimeout(5000, TimeUnit.MILLISECONDS)
                .build()

            val request = Request.Builder()
                .url(URL)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("BAD_REQUEST", response.body?.string() ?: "No response body")
                    data = null
                } else {
                    data = JSONObject(response.body?.string() ?: "{}")
                }
            }
        } catch (e: Exception) {
            when (e) {
                is IOException, is org.json.JSONException -> e.printStackTrace()
                else -> throw e
            }
        }
        return data
    }
}