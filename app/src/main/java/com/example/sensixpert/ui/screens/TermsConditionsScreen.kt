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
fun TermsConditionsScreen(
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
                    text = "Terms & Conditions",
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
                TermsSection(
                    title = "1. Acceptance of Terms",
                    content = "By downloading, installing, or using the SensiXpert application, you agree to be bound by these Terms and Conditions. If you do not agree to these terms, please do not use the app."
                )

                TermsSection(
                    title = "2. Description of Service",
                    content = "SensiXpert is a premium gaming utility application that provides sensitivity settings, device optimization tools, gameplay improvement features, and a Refer & Earn program. The app offers subscription-based premium features and a referral commission system for premium users."
                )

                TermsSection(
                    title = "3. User Eligibility",
                    content = "You must be at least 13 years of age to use this application. By using the app, you represent and warrant that you meet this age requirement. To participate in the Refer & Earn program and receive wallet withdrawals, you must be at least 18 years of age."
                )

                TermsSection(
                    title = "4. User Conduct",
                    content = "You agree to use the app only for lawful purposes and in accordance with these Terms. You agree not to:\n• Use the app in any way that violates applicable laws\n• Attempt to reverse-engineer or decompile the app\n• Use the app for any unauthorized commercial purpose\n• Interfere with the app's operation or security\n• Create multiple accounts to exploit the referral system\n• Engage in any form of referral fraud or abuse"
                )

                TermsSection(
                    title = "5. Subscription & Payments",
                    content = "SensiXpert offers premium subscription plans that unlock full access to all features. Payments are processed securely through ZapUPI. All subscription payments are non-refundable unless required by applicable law. Subscription benefits are activated immediately upon successful payment verification."
                )

                TermsSection(
                    title = "6. Intellectual Property",
                    content = "All content, features, and functionality of the SensiXpert app, including but not limited to text, graphics, logos, icons, and software, are the exclusive property of SensiXpert and are protected by international copyright, trademark, and other intellectual property laws."
                )

                TermsSection(
                    title = "7. Referral Program",
                    content = "Premium subscribers can participate in the Refer & Earn program under the following terms:\n\n• Each premium user receives a unique, non-transferable referral code.\n• When a referred user purchases any subscription plan using your referral code, you earn ₹15 to ₹120 per referral depending on the plan purchased.\n• Commission is credited to your in-app wallet after successful payment verification.\n• Referral commissions are earned only on the first subscription purchase by each referred user.\n• SensiXpert reserves the right to modify the commission amounts with prior notice."
                )

                TermsSection(
                    title = "8. Wallet & Withdrawals",
                    content = "Your in-app wallet balance represents earned referral commissions. The following terms apply:\n\n• Minimum withdrawal amount is ₹50 (Indian Rupees).\n• Withdrawals are processed to the UPI ID provided by you.\n• Processing time is 24-72 business hours after admin approval.\n• You are responsible for providing a correct and active UPI ID.\n• SensiXpert reserves the right to review and verify all withdrawal requests.\n• Wallet balance cannot be used to purchase subscriptions.\n• Wallet balance is non-transferable between accounts."
                )

                TermsSection(
                    title = "9. Referral Abuse Policy",
                    content = "Any form of referral abuse is strictly prohibited, including but not limited to:\n\n• Self-referrals (using your own code on another account)\n• Creating fake or duplicate accounts to earn commissions\n• Spam sharing or misleading promotional practices\n• Manipulation of the referral system through any means\n• Colluding with others to artificially inflate referral earnings\n\nViolation of this policy will result in immediate account termination, forfeiture of all wallet balance, and potential legal action. SensiXpert reserves the sole right to determine what constitutes referral abuse."
                )

                TermsSection(
                    title = "10. Commission Modification",
                    content = "SensiXpert reserves the right to change the commission percentage, minimum withdrawal amount, or any other aspect of the referral program at any time. Users will be notified of significant changes through in-app notifications. Continued participation in the referral program after changes constitutes acceptance."
                )

                TermsSection(
                    title = "11. Tax Responsibility",
                    content = "Users are solely responsible for any tax obligations arising from referral earnings and wallet withdrawals. SensiXpert does not withhold taxes on referral commissions. It is your responsibility to report and pay any applicable taxes in accordance with the laws of your jurisdiction."
                )

                TermsSection(
                    title = "12. Disclaimer of Warranties",
                    content = "The app is provided \"as is\" and \"as available\" without any warranties of any kind, either express or implied. We do not guarantee that the app will be uninterrupted, error-free, or free of viruses. Sensitivity settings are recommendations and results may vary based on your device and game version."
                )

                TermsSection(
                    title = "13. Limitation of Liability",
                    content = "In no event shall SensiXpert, its developers, or affiliates be liable for any indirect, incidental, special, consequential, or punitive damages arising out of or related to your use of the app, including any losses related to the referral program or wallet transactions."
                )

                TermsSection(
                    title = "14. Termination",
                    content = "We may terminate or suspend your access to the app immediately, without prior notice, for any reason whatsoever, including without limitation if you breach these Terms, engage in referral abuse, or violate any applicable laws. Upon termination, any remaining wallet balance may be forfeited if the termination is due to policy violations."
                )

                TermsSection(
                    title = "15. Governing Law",
                    content = "These Terms shall be governed by and construed in accordance with the laws of India. Any disputes arising from these Terms shall be subject to the exclusive jurisdiction of the courts in India."
                )

                TermsSection(
                    title = "16. Contact",
                    content = "If you have any questions about these Terms and Conditions, please contact us through the app's Customer Support section."
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
private fun TermsSection(title: String, content: String) {
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
