package com.example.sensixpert.data

/**
 * Data classes for the Refer & Earn feature.
 */

data class ReferralInfo(
    val code: String = "",
    val totalReferrals: Int = 0,
    val successfulReferrals: Int = 0,
    val totalEarnings: Double = 0.0
)

data class WalletInfo(
    val balance: Double = 0.0,
    val totalEarnings: Double = 0.0,
    val totalWithdrawn: Double = 0.0
)

data class ReferralLog(
    val id: String = "",
    val referredUserId: String = "",
    val referralCode: String = "",
    val status: String = "pending",   // pending, completed
    val plan: String? = null,
    val amount: Double = 0.0,
    val commission: Double = 0.0,
    val createdAt: Long = 0L
)

data class Withdrawal(
    val id: String = "",
    val amount: Double = 0.0,
    val upiId: String = "",
    val status: String = "pending",   // pending, approved, rejected, completed
    val adminNote: String = "",
    val requestedAt: Long = 0L,
    val processedAt: Long = 0L
)
