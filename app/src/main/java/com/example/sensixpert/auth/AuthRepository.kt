package com.example.sensixpert.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository wrapping Firebase Authentication + Firestore user creation.
 */
class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    /**
     * Register a new user with email/password.
     * On success, creates a Firestore document at users/{uid}.
     * Optionally applies a referral/promo code.
     */
    suspend fun register(
        name: String,
        phone: String,
        email: String,
        password: String,
        promoCode: String? = null
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Registration failed")

            // Update display name
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profileUpdate).await()

            // Create Firestore user document
            val userData = hashMapOf<String, Any>(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "createdAt" to System.currentTimeMillis(),
                "subscription" to hashMapOf(
                    "plan" to "none",
                    "startDate" to 0L,
                    "endDate" to 0L,
                    "status" to "inactive"
                )
            )

            // Apply referral/promo code if provided
            if (!promoCode.isNullOrBlank()) {
                try {
                    val refDoc = firestore.collection("referrals")
                        .document(promoCode).get().await()
                    if (refDoc.exists()) {
                        val referrerUid = refDoc.getString("userId") ?: ""
                        // Prevent self-referral
                        if (referrerUid != user.uid) {
                            userData["referredBy"] = promoCode
                            userData["referralRewardPending"] = true
                            // Increment total referrals on the code
                            firestore.collection("referrals").document(promoCode)
                                .update("totalReferrals", com.google.firebase.firestore.FieldValue.increment(1))
                                .await()
                            // Create pending referral log
                            firestore.collection("referral_logs").add(
                                hashMapOf(
                                    "referrerUserId" to referrerUid,
                                    "referredUserId" to user.uid,
                                    "referralCode" to promoCode,
                                    "status" to "pending",
                                    "plan" to null,
                                    "amount" to 0,
                                    "commission" to 0,
                                    "completedAt" to null,
                                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                                )
                            ).await()
                        }
                    }
                } catch (refErr: Exception) {
                    // Non-fatal: promo code invalid, just skip
                }
            }

            firestore.collection("users").document(user.uid).set(userData).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign in with email/password.
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Login failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign out the current user.
     */
    fun logout() {
        auth.signOut()
    }
}
