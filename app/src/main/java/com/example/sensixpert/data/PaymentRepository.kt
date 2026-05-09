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
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Calls backend /create-payment to initiate a ZapUPI payment.
     * Returns PaymentResponse with paymentUrl and orderId.
     * Retries once after warmup if first attempt fails.
     */
    suspend fun createPayment(userId: String, plan: String): Result<PaymentResponse> {
        return withContext(Dispatchers.IO) {
            // Warmup first to wake Render server
            try {
                val warmupReq = Request.Builder().url(BACKEND_BASE_URL).get().build()
                client.newCall(warmupReq).execute().close()
            } catch (_: Exception) { }

            var lastError: Exception? = null
            for (attempt in 1..2) {
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
                            return@withContext Result.success(PaymentResponse(paymentUrl, orderId))
                        } else {
                            lastError = Exception("No payment URL returned")
                        }
                    } else {
                        Log.e("PaymentRepo", "Error: ${response.code} - $responseBody")
                        lastError = Exception("Payment creation failed: ${response.code}")
                    }
                } catch (e: Exception) {
                    Log.e("PaymentRepo", "Attempt $attempt failed", e)
                    lastError = e
                    if (attempt == 1) {
                        Thread.sleep(2000) // wait before retry
                    }
                }
            }
            Result.failure(lastError ?: Exception("Payment failed"))
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
