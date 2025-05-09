package com.example.booktourdemo.manager.api.zalopay.Helper

import android.annotation.SuppressLint
import com.example.booktourdemo.manager.api.zalopay.Helper.HMac.HMacUtil
import java.text.SimpleDateFormat
import java.util.Date

object Helpers {
    private var transIdDefault = 1

    @SuppressLint("DefaultLocale", "SimpleDateFormat")
    fun getAppTransId(): String {
        if (transIdDefault >= 100000) {
            transIdDefault = 1
        }
        transIdDefault += 1

        val formatDateTime = SimpleDateFormat("yyMMdd_hhmmss")
        val timeString = formatDateTime.format(Date())
        return String.format("%s%06d", timeString, transIdDefault)
    }

    fun getMac(key: String, data: String): String {
        return HMacUtil.HMacHexStringEncode(HMacUtil.HMACSHA256, key, data) ?: throw IllegalStateException("Failed to compute HMAC")
    }
}