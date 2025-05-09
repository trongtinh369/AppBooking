package com.example.booktourdemo.manager.api.zalopay.Api

import com.example.booktourdemo.manager.api.zalopay.Constant.AppInfo
import com.example.booktourdemo.manager.api.zalopay.Helper.Helpers
import okhttp3.FormBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.Date

class CreateOrder {

    private data class CreateOrderData(
        val AppId: String,
        val AppUser: String,
        val AppTime: String,
        val Amount: String,
        val AppTransId: String,
        val EmbedData: String,
        val Items: String,
        val BankCode: String,
        val Description: String,
        val Mac: String
    )

    private fun createOrderData(amount: String): CreateOrderData {
        val appTime = Date().time.toString()
        val appId = AppInfo.APP_ID.toString()
        val appUser = "Android_Demo"
        val appTransId = Helpers.getAppTransId()
        val embedData = "{}"
        val items = "[]"
        val bankCode = "zalopayapp"
        val description = "Merchant pay for order #${Helpers.getAppTransId()}"
        val inputHMac = "$appId|$appTransId|$appUser|$amount|$appTime|$embedData|$items"
        val mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac)

        return CreateOrderData(
            AppId = appId,
            AppUser = appUser,
            AppTime = appTime,
            Amount = amount,
            AppTransId = appTransId,
            EmbedData = embedData,
            Items = items,
            BankCode = bankCode,
            Description = description,
            Mac = mac
        )
    }

    fun createOrder(amount: String): JSONObject? {
        val input = createOrderData(amount)

        val formBody: RequestBody = FormBody.Builder()
            .add("app_id", input.AppId)
            .add("app_user", input.AppUser)
            .add("app_time", input.AppTime)
            .add("amount", input.Amount)
            .add("app_trans_id", input.AppTransId)
            .add("embed_data", input.EmbedData)
            .add("item", input.Items)
            .add("bank_code", input.BankCode)
            .add("description", input.Description)
            .add("mac", input.Mac)
            .build()

        return HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody)
    }
}