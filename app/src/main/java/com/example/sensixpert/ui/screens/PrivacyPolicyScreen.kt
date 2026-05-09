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
                    content = "Welcome to SensiXpert. Your privacy is critically important to us. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application."
                )

                PolicySection(
                    title = "Information We Collect",
                    content = "We may collect information that you provide directly, such as device model information used for sensitivity recommendations. We also collect anonymous usage data to improve app performance and user experience. We do NOT collect any personally identifiable information (PII) unless explicitly provided by you."
                )

                PolicySection(
                    title = "How We Use Your Information",
                    content = "The information we collect is used to:\n• Provide and maintain the app\n• Improve and optimize the app experience\n• Generate device-specific sensitivity settings\n• Show relevant advertisements through Google AdMob\n• Monitor app usage analytics"
                )

                PolicySection(
                    title = "Advertisements",
                    content = "We use Google AdMob to display advertisements. AdMob may collect and use certain data, including your device's advertising identifier, to serve personalized or non-personalized ads. You can learn more about Google's data practices at Google's Privacy Policy page."
                )

                PolicySection(
                    title = "Data Security",
                    content = "We implement industry-standard security measures to protect your data. However, no method of electronic storage or transmission is 100% secure. We strive to use commercially acceptable means to protect your information but cannot guarantee its absolute security."
                )

                PolicySection(
                    title = "Third-Party Services",
                    content = "Our app may contain links to third-party services. We are not responsible for the privacy practices of these third parties. We encourage you to read the privacy policies of any third-party services you access."
                )

                PolicySection(
                    title = "Children's Privacy",
                    content = "Our app does not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13. If we discover that a child under 13 has provided us with personal information, we immediately delete this from our servers."
                )

                PolicySection(
                    title = "Changes to This Policy",
                    content = "We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy within the app. You are advised to review this page periodically for any changes."
                )

                PolicySection(
                    title = "Contact Us",
                    content = "If you have any questions about this Privacy Policy, please contact us through the app's support section."
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Last updated: April 2026",
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
