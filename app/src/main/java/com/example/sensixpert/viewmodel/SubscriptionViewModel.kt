package com.example.sensixpert.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensixpert.PaymentWebViewActivity
import com.example.sensixpert.data.PaymentRepository
import com.example.sensixpert.data.SubscriptionInfo
import com.example.sensixpert.data.SubscriptionPlan
import com.example.sensixpert.data.SubscriptionRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SubscriptionViewModel : ViewModel() {

    private val subscriptionRepository = SubscriptionRepository()
    private val paymentRepository = PaymentRepository()

    var subscriptionInfo by mutableStateOf(SubscriptionInfo())
        private set

    var isSubscribed by mutableStateOf(false)
        private set

    var isPaymentLoading by mutableStateOf(false)
        private set

    var paymentError by mutableStateOf<String?>(null)
        private set

    /** Holds the payment intent to launch from the Composable */
    var pendingPaymentIntent by mutableStateOf<Intent?>(null)
        private set

    /**
     * Start observing subscription status from Firestore in real-time.
     */
    fun startObserving(userId: String) {
        viewModelScope.launch {
            subscriptionRepository.observeSubscription(userId).collectLatest { info ->
                subscriptionInfo = info
                isSubscribed = info.isActive
            }
        }
    }

    /**
     * One-time check of subscription status.
     */
    fun checkSubscription(userId: String) {
        viewModelScope.launch {
            val info = subscriptionRepository.getSubscription(userId)
            subscriptionInfo = info
            isSubscribed = info.isActive
        }
    }

    /**
     * Ping the backend to wake up Render.com server.
     * Call this when subscription screen opens so the server is warm
     * by the time user clicks Subscribe.
     */
    fun warmupBackend() {
        viewModelScope.launch {
            paymentRepository.warmup()
        }
    }

    /**
     * Initiate payment: calls backend → creates intent for PaymentWebViewActivity.
     * The SubscriptionScreen will observe pendingPaymentIntent and launch it.
     */
    fun purchasePlan(context: Context, userId: String, plan: SubscriptionPlan) {
        paymentError = null
        isPaymentLoading = true

        viewModelScope.launch {
            val result = paymentRepository.createPayment(userId, plan.planId)
            isPaymentLoading = false

            result.fold(
                onSuccess = { paymentResponse ->
                    // Create intent for WebView activity
                    val intent = Intent(context, PaymentWebViewActivity::class.java).apply {
                        putExtra(PaymentWebViewActivity.EXTRA_PAYMENT_URL, paymentResponse.paymentUrl)
                        putExtra(PaymentWebViewActivity.EXTRA_ORDER_ID, paymentResponse.orderId)
                    }
                    pendingPaymentIntent = intent
                },
                onFailure = { e ->
                    paymentError = e.message ?: "Payment failed"
                }
            )
        }
    }

    /**
     * Clear the pending intent after it has been launched.
     */
    fun clearPendingPaymentIntent() {
        pendingPaymentIntent = null
    }

    /**
     * Handle payment result from PaymentWebViewActivity.
     * Returns a user-friendly message for the UI.
     */
    fun handlePaymentResult(result: ActivityResult): String? {
        val data = result.data
        val status = data?.getStringExtra(PaymentWebViewActivity.EXTRA_STATUS) ?: "cancel"
        val orderId = data?.getStringExtra(PaymentWebViewActivity.EXTRA_ORDER_ID) ?: ""

        return when {
            result.resultCode == Activity.RESULT_OK -> {
                when (status) {
                    "success" -> "Payment successful! Subscription activating..."
                    "failed" -> "Payment failed. Please try again."
                    "timeout" -> "Payment timed out. Please try again."
                    else -> "Payment status unknown."
                }
            }
            else -> {
                // RESULT_CANCELED
                if (status == "cancel") {
                    "Payment cancelled."
                } else {
                    null
                }
            }
        }
    }

    fun clearPaymentError() {
        paymentError = null
    }

    /**
     * Get remaining days text for display.
     */
    fun getRemainingDaysText(): String {
        if (!isSubscribed) return ""
        val remaining = subscriptionInfo.endDate - System.currentTimeMillis()
        val days = (remaining / (1000 * 60 * 60 * 24)).toInt()
        return when {
            days > 1 -> "$days days remaining"
            days == 1 -> "1 day remaining"
            days == 0 -> "Expires today"
            else -> "Expired"
        }
    }
}
