package com.example.sensixpert.data

/**
 * Subscription plan definitions.
 */
enum class SubscriptionPlan(
    val displayName: String,
    val price: Int,
    val days: Int,
    val planId: String
) {
    BASIC("7 Days", 49, 7, "7days"),
    STANDARD("1 Month", 169, 30, "monthly"),
    PREMIUM("3 Months", 399, 90, "3months")
}

/**
 * Subscription status data from Firestore.
 */
data class SubscriptionInfo(
    val plan: String = "none",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val status: String = "inactive"
) {
    val isActive: Boolean
        get() = status == "active" && endDate > System.currentTimeMillis()
}
