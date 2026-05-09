package com.example.sensixpert.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository for reading subscription data from Firestore.
 * Only the backend (via webhook) writes subscription data.
 */
class SubscriptionRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * One-time read of subscription info.
     */
    suspend fun getSubscription(userId: String): SubscriptionInfo {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            val sub = doc.get("subscription") as? Map<*, *> ?: return SubscriptionInfo()
            SubscriptionInfo(
                plan = sub["plan"] as? String ?: "none",
                startDate = (sub["startDate"] as? Long) ?: 0L,
                endDate = (sub["endDate"] as? Long) ?: 0L,
                status = sub["status"] as? String ?: "inactive"
            )
        } catch (e: Exception) {
            SubscriptionInfo()
        }
    }

    /**
     * Real-time listener for subscription changes.
     * This is critical — when webhook updates Firestore, the app reflects it instantly.
     */
    fun observeSubscription(userId: String): Flow<SubscriptionInfo> = callbackFlow {
        val listener: ListenerRegistration = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(SubscriptionInfo())
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val sub = snapshot.get("subscription") as? Map<*, *>
                    if (sub != null) {
                        val info = SubscriptionInfo(
                            plan = sub["plan"] as? String ?: "none",
                            startDate = (sub["startDate"] as? Long) ?: 0L,
                            endDate = (sub["endDate"] as? Long) ?: 0L,
                            status = sub["status"] as? String ?: "inactive"
                        )
                        trySend(info)
                    } else {
                        trySend(SubscriptionInfo())
                    }
                } else {
                    trySend(SubscriptionInfo())
                }
            }

        awaitClose { listener.remove() }
    }
}
