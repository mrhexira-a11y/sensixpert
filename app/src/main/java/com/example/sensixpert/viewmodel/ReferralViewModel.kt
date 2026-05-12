package com.example.sensixpert.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensixpert.data.*
import kotlinx.coroutines.launch

class ReferralViewModel : ViewModel() {

    private val repository = ReferralRepository()

    // Referral info
    var referralCode by mutableStateOf("")
        private set
    var referralInfo by mutableStateOf(ReferralInfo())
        private set
    var walletInfo by mutableStateOf(WalletInfo())
        private set
    var referralLogs by mutableStateOf<List<ReferralLog>>(emptyList())
        private set
    var withdrawals by mutableStateOf<List<Withdrawal>>(emptyList())
        private set

    // UI states
    var isLoading by mutableStateOf(false)
        private set
    var isGeneratingCode by mutableStateOf(false)
        private set
    var isWithdrawing by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Load all referral data for a user.
     */
    fun loadReferralData(userId: String) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            val result = repository.getReferralInfo(userId)
            isLoading = false
            result.fold(
                onSuccess = { (info, wallet, logs) ->
                    referralCode = info.code
                    referralInfo = info
                    walletInfo = wallet
                    referralLogs = logs
                },
                onFailure = { e ->
                    errorMessage = e.message
                }
            )
        }
    }

    /**
     * Generate a new referral code (premium only).
     */
    fun generateCode(userId: String) {
        isGeneratingCode = true
        errorMessage = null
        viewModelScope.launch {
            val result = repository.generateReferralCode(userId)
            isGeneratingCode = false
            result.fold(
                onSuccess = { code ->
                    referralCode = code
                    // Reload full data
                    loadReferralData(userId)
                },
                onFailure = { e ->
                    errorMessage = e.message
                }
            )
        }
    }

    /**
     * Share referral code via Android share sheet.
     */
    fun shareReferralCode(context: Context) {
        if (referralCode.isEmpty()) return
        val shareText = "🎮 SensiXpert se apni gaming sensitivity best karo! " +
                "Mera referral code use karo: $referralCode aur hum dono ko benefit milega! " +
                "\n\nDownload karo: https://sensixpert.com"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share Referral Code"))
    }

    /**
     * Request withdrawal from wallet.
     */
    fun requestWithdrawal(userId: String, amount: Double, upiId: String) {
        if (amount < 50) {
            errorMessage = "Minimum withdrawal is ₹50"
            return
        }
        if (upiId.isBlank()) {
            errorMessage = "Please enter your UPI ID"
            return
        }
        if (amount > walletInfo.balance) {
            errorMessage = "Insufficient balance"
            return
        }

        isWithdrawing = true
        errorMessage = null
        viewModelScope.launch {
            val result = repository.requestWithdrawal(userId, amount, upiId)
            isWithdrawing = false
            result.fold(
                onSuccess = { msg ->
                    successMessage = msg
                    // Reload wallet data
                    loadWalletData(userId)
                },
                onFailure = { e ->
                    errorMessage = e.message
                }
            )
        }
    }

    /**
     * Load wallet + withdrawal history.
     */
    fun loadWalletData(userId: String) {
        viewModelScope.launch {
            val result = repository.getWalletInfo(userId)
            result.fold(
                onSuccess = { (wallet, wds) ->
                    walletInfo = wallet
                    withdrawals = wds
                },
                onFailure = { /* silently fail */ }
            )
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}
