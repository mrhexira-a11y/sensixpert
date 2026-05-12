package com.example.sensixpert.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*

@Composable
fun PrivacyPolicyScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── Header ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF8B1A1A),
                                Color(0xFF4A0A0A),
                                GamingBackground
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Back button
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(28.dp)
                        .clickable { onNavigateBack() }
                )

                // Title
                Text(
                    text = "Privacy Policy",
                    color = TextWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ── Content ──
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                PolicySection(
                    title = "Introduction",
                    content = "Welcome to SensiXpert. Your privacy is critically important to us. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application. By using SensiXpert, you consent to the practices described in this policy."
                )

                PolicySection(
                    title = "Information We Collect",
                    content = "We collect the following information:\n\n• Account Information: Name, email address, phone number provided during registration.\n• Device Information: Device model and specifications used for sensitivity recommendations.\n• Subscription Data: Plan details, payment status, and subscription history.\n• Referral Data: Referral codes, referral relationships, and commission history when you participate in our Refer & Earn program.\n• Wallet & Payment Data: Wallet balances, withdrawal requests, and UPI IDs provided for withdrawal processing.\n• Usage Data: Anonymous app usage analytics to improve performance.\n• Push Notification Tokens: For sending you important updates and notifications."
                )

                PolicySection(
                    title = "How We Use Your Information",
                    content = "The information we collect is used to:\n• Provide, maintain, and improve the app\n• Process subscription payments securely\n• Operate the Refer & Earn program including tracking referrals and crediting commissions\n• Process wallet withdrawals to your provided UPI ID\n• Generate device-specific sensitivity settings\n• Send push notifications for updates and promotions\n• Monitor app usage analytics\n• Prevent fraud and abuse of the referral system"
                )

                PolicySection(
                    title = "Referral Program Data",
                    content = "When you participate in our Refer & Earn program, we collect and store:\n• Your unique referral code\n• Referral relationships (who referred whom)\n• Commission amounts and payment history\n• Wallet balance and transaction history\n\nThis data is used solely to operate the referral program, calculate commissions, and process withdrawals. We do not share referral data with third parties."
                )

                PolicySection(
                    title = "Payment & Wallet Information",
                    content = "We store wallet balances, withdrawal requests, and UPI IDs that you provide for withdrawal processing. Your UPI ID is stored securely on our servers and is used exclusively for processing approved withdrawal payments. We do not store any bank account details, credit/debit card numbers, or other financial instruments."
                )

                PolicySection(
                    title = "Data Security",
                    content = "We implement industry-standard security measures to protect your data including encrypted data transmission (SSL/TLS), secure server infrastructure, and access controls. However, no method of electronic storage or transmission is 100% secure. We strive to use commercially acceptable means to protect your information but cannot guarantee its absolute security."
                )

                PolicySection(
                    title = "Information Sharing",
                    content = "We do not sell, trade, or rent your personal data to third parties. We may share information only in the following cases:\n• With payment processors to complete transactions\n• To comply with legal obligations or court orders\n• To protect our rights, property, or safety\n• With your explicit consent"
                )

                PolicySection(
                    title = "Data Retention",
                    content = "We retain your personal data for as long as your account is active. Referral data, wallet history, and withdrawal records are retained for a reasonable period after account closure for legal and accounting purposes. You may request deletion of your account and associated data by contacting our support team."
                )

                PolicySection(
                    title = "Children's Privacy",
                    content = "Our app does not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13. If we discover that a child under 13 has provided us with personal information, we immediately delete this from our servers."
                )

                PolicySection(
                    title = "Changes to This Policy",
                    content = "We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy within the app. You are advised to review this page periodically for any changes. Continued use of the app after changes constitutes acceptance of the updated policy."
                )

                PolicySection(
                    title = "Contact Us",
                    content = "If you have any questions about this Privacy Policy or wish to exercise your data rights, please contact us through the app's Customer Support section."
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Last updated: May 2026",
                    color = TextDimGrey,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            color = GamingOrangeRed,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
                .padding(16.dp)
        ) {
            Text(
                text = content,
                color = TextGrey,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
