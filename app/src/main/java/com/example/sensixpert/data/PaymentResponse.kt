package com.example.sensixpert.data

/**
 * Response from the backend /create-payment endpoint.
 */
data class PaymentResponse(
    val paymentUrl: String,
    val orderId: String
)
