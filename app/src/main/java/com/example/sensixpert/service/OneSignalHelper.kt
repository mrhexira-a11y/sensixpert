package com.example.sensixpert.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.onesignal.OneSignal

/**
 * Helper object for OneSignal push notification management.
 * Handles user login/logout and subscription tagging.
 */
object OneSignalHelper {

    private const val TAG = "OneSignalHelper"

    /**
     * Login user to OneSignal with their Firebase UID as External ID.
     * This links the device to the user for targeted notifications.
     */
    fun loginUser() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        OneSignal.login(user.uid)
        Log.d(TAG, "OneSignal login: ${user.uid}")
    }

    /**
     * Logout user from OneSignal when they sign out.
     */
    fun logoutUser() {
        OneSignal.logout()
        Log.d(TAG, "OneSignal logout")
    }

    /**
     * Update subscription tag so admin panel can target
     * subscribers vs non-subscribers.
     */
    fun updateSubscriptionTag(isSubscribed: Boolean) {
        OneSignal.User.addTag("subscribed", if (isSubscribed) "true" else "false")
        Log.d(TAG, "OneSignal tag updated: subscribed=$isSubscribed")
    }
}
