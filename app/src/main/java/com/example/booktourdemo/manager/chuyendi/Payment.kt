package com.example.booktourdemo.manager.chuyendi

import android.app.Activity
import android.util.Log
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Booking.Booking
import com.example.booktourdemo.manager.api.zalopay.Api.CreateOrder
import org.json.JSONObject
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener

class Payment(private val activity: Activity) {
    fun menthodPayment(oldBooking: Booking, databaseAPI: DatabaseAPI, loadData: () -> Unit, updateTourStatic: () -> Unit){
        val orderApi = CreateOrder()
        val tatolString: String = String.format("%.0f",oldBooking.total_price)

        try {
            val data: JSONObject? = orderApi.createOrder(tatolString)
            val code = data?.getString("return_code")

            if (code == "1") {

                val token = data.getString("zp_trans_token")
                ZaloPaySDK.getInstance().payOrder(activity, token, "demozpdk://app", object :
                    PayOrderListener {
                    override fun onPaymentSucceeded(transactionId: String, transToken: String, appTransID: String) {
                        // Xử lý khi thanh toán thành công

                        oldBooking.payment.status = true
                        databaseAPI.capNhapBooking(oldBooking, object : OnDatabaseCallback {
                            override fun onSuccess(temp: Any) {
                                loadData()
                                updateTourStatic()
                            }

                            override fun onFailure(e: Exception) {}
                        })

                    }

                    override fun onPaymentCanceled(transToken: String, appTransID: String) {
                        // Xử lý khi người dùng hủy thanh toán
                    }

                    override fun onPaymentError(error: ZaloPayError, transToken: String, appTransID: String) {
                        // Xử lý lỗi khi thanh toán
                        Log.d("test","$error")
                    }
                })

            }

        } catch (e: Exception) {
            Log.d("test","$e")
        }
    }
}