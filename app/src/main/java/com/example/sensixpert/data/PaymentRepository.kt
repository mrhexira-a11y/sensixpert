package com.example.sensixpert.data

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Repository for communicating with the backend to create payments.
 * The app NEVER directly handles payment verification — only the webhook does that.
 */
class PaymentRepository {

    companion object {
        // Render.com backend URL
        const val BACKEND_BASE_URL = "https://sensixpert-backend.onrender.com"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Calls backend /create-payment to initiate a ZapUPI payment.
     * Returns PaymentResponse with paymentUrl and orderId.
     */
    suspend fun createPayment(userId: String, plan: String): Result<PaymentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val body = gson.toJson(
                    mapOf("userId" to userId, "plan" to plan)
                ).toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("$BACKEND_BASE_URL/create-payment")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val responseMap = gson.fromJson(responseBody, Map::class.java)
                    val paymentUrl = responseMap["paymentUrl"] as? String
                    val orderId = responseMap["orderId"] as? String ?: ""
                    if (paymentUrl != null) {
                        Result.success(PaymentResponse(paymentUrl, orderId))
                    } else {
                        Result.failure(Exception("No payment URL returned"))
                    }
                } else {
                    Log.e("PaymentRepo", "Error: ${response.code} - $responseBody")
                    Result.failure(Exception("Payment creation failed: ${response.code}"))
                }
            } catch (e: Exception) {
                Log.e("PaymentRepo", "Exception creating payment", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Ping the backend health endpoint to wake up the Render.com server.
     * Call this early (e.g. when subscription screen opens) so the server
     * is warm by the time the user clicks Subscribe.
     */
    suspend fun warmup() {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(BACKEND_BASE_URL)
                    .get()
                    .build()
                client.newCall(request).execute().close()
                Log.d("PaymentRepo", "Backend warmup successful")
            } catch (e: Exception) {
                Log.d("PaymentRepo", "Backend warmup failed (non-critical): ${e.message}")
            }
        }
    }
}
