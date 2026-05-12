package com.example.sensixpert.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Repository for Refer & Earn feature.
 * Handles referral code generation, wallet, withdrawals via backend API.
 */
class ReferralRepository {

    companion object {
        private const val BACKEND_BASE_URL = PaymentRepository.BACKEND_BASE_URL
    }

    private val client = OkHttpClient()
    private val JSON_TYPE = "application/json; charset=utf-8".toMediaType()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Generate or retrieve referral code for a premium user.
     */
    suspend fun generateReferralCode(userId: String): Result<String> {
        return try {
            val body = JSONObject().put("userId", userId).toString()
                .toRequestBody(JSON_TYPE)
            val request = Request.Builder()
                .url("$BACKEND_BASE_URL/generate-referral-code")
                .post(body)
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            val json = JSONObject(responseBody)
            if (json.optBoolean("success")) {
                Result.success(json.getString("code"))
            } else {
                Result.failure(Exception(json.optString("error", "Failed to generate code")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get referral info (stats + wallet + logs) for a user.
     */
    suspend fun getReferralInfo(userId: String): Result<Triple<ReferralInfo, WalletInfo, List<ReferralLog>>> {
        return try {
            val request = Request.Builder()
                .url("$BACKEND_BASE_URL/referral-info/$userId")
                .get()
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            val json = JSONObject(responseBody)

            if (!json.optBoolean("success")) {
                return Result.failure(Exception(json.optString("error", "Failed")))
            }

            val code = json.optString("code", "")
            val stats = json.optJSONObject("stats")
            val walletJson = json.optJSONObject("wallet")

            val referralInfo = ReferralInfo(
                code = code,
                totalReferrals = stats?.optInt("totalReferrals", 0) ?: 0,
                successfulReferrals = stats?.optInt("successfulReferrals", 0) ?: 0,
                totalEarnings = stats?.optDouble("totalEarnings", 0.0) ?: 0.0
            )

            val walletInfo = WalletInfo(
                balance = walletJson?.optDouble("balance", 0.0) ?: 0.0,
                totalEarnings = walletJson?.optDouble("totalEarnings", 0.0) ?: 0.0,
                totalWithdrawn = walletJson?.optDouble("totalWithdrawn", 0.0) ?: 0.0
            )

            val logsArray = json.optJSONArray("logs")
            val logs = mutableListOf<ReferralLog>()
            if (logsArray != null) {
                for (i in 0 until logsArray.length()) {
                    val log = logsArray.getJSONObject(i)
                    val createdAt = log.optJSONObject("createdAt")
                    val seconds = createdAt?.optLong("_seconds", 0L) ?: 0L
                    logs.add(
                        ReferralLog(
                            id = log.optString("id", ""),
                            referredUserId = log.optString("referredUserId", ""),
                            referralCode = log.optString("referralCode", ""),
                            status = log.optString("status", "pending"),
                            plan = log.optString("plan", null),
                            amount = log.optDouble("amount", 0.0),
                            commission = log.optDouble("commission", 0.0),
                            createdAt = seconds * 1000
                        )
                    )
                }
            }

            Result.success(Triple(referralInfo, walletInfo, logs))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Apply a referral code during or after registration.
     */
    suspend fun applyReferralCode(userId: String, code: String): Result<String> {
        return try {
            val body = JSONObject()
                .put("userId", userId)
                .put("code", code.trim().uppercase())
                .toString().toRequestBody(JSON_TYPE)
            val request = Request.Builder()
                .url("$BACKEND_BASE_URL/apply-referral")
                .post(body)
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            val json = JSONObject(responseBody)
            if (json.optBoolean("success")) {
                Result.success(json.optString("message", "Referral code applied!"))
            } else {
                Result.failure(Exception(json.optString("error", "Failed to apply code")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get wallet balance and withdrawal history.
     */
    suspend fun getWalletInfo(userId: String): Result<Pair<WalletInfo, List<Withdrawal>>> {
        return try {
            val request = Request.Builder()
                .url("$BACKEND_BASE_URL/wallet/$userId")
                .get()
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            val json = JSONObject(responseBody)

            if (!json.optBoolean("success")) {
                return Result.failure(Exception(json.optString("error", "Failed")))
            }

            val walletJson = json.optJSONObject("wallet")
            val walletInfo = WalletInfo(
                balance = walletJson?.optDouble("balance", 0.0) ?: 0.0,
                totalEarnings = walletJson?.optDouble("totalEarnings", 0.0) ?: 0.0,
                totalWithdrawn = walletJson?.optDouble("totalWithdrawn", 0.0) ?: 0.0
            )

            val wArray = json.optJSONArray("withdrawals")
            val withdrawals = mutableListOf<Withdrawal>()
            if (wArray != null) {
                for (i in 0 until wArray.length()) {
                    val w = wArray.getJSONObject(i)
                    val reqAt = w.optJSONObject("requestedAt")
                    val procAt = w.optJSONObject("processedAt")
                    withdrawals.add(
                        Withdrawal(
                            id = w.optString("id", ""),
                            amount = w.optDouble("amount", 0.0),
                            upiId = w.optString("upiId", ""),
                            status = w.optString("status", "pending"),
                            adminNote = w.optString("adminNote", ""),
                            requestedAt = (reqAt?.optLong("_seconds", 0L) ?: 0L) * 1000,
                            processedAt = (procAt?.optLong("_seconds", 0L) ?: 0L) * 1000
                        )
                    )
                }
            }

            Result.success(Pair(walletInfo, withdrawals))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Request a withdrawal (minimum ₹50).
     */
    suspend fun requestWithdrawal(userId: String, amount: Double, upiId: String): Result<String> {
        return try {
            val body = JSONObject()
                .put("userId", userId)
                .put("amount", amount)
                .put("upiId", upiId.trim())
                .toString().toRequestBody(JSON_TYPE)
            val request = Request.Builder()
                .url("$BACKEND_BASE_URL/request-withdrawal")
                .post(body)
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            val json = JSONObject(responseBody)
            if (json.optBoolean("success")) {
                Result.success("Withdrawal request submitted!")
            } else {
                Result.failure(Exception(json.optString("error", "Failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Validate if a referral code exists (used on register screen).
     */
    suspend fun validateReferralCode(code: String): Boolean {
        return try {
            val doc = db.collection("referrals").document(code.trim().uppercase()).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}
