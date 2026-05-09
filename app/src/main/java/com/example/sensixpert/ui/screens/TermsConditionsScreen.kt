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
                    content = "SensiXpert is a gaming utility application that provides sensitivity settings, device optimization tools, and gameplay improvement features. The app is designed to help players achieve better control and performance in mobile games."
                )

                TermsSection(
                    title = "3. User Eligibility",
                    content = "You must be at least 13 years of age to use this application. By using the app, you represent and warrant that you meet this age requirement."
                )

                TermsSection(
                    title = "4. User Conduct",
                    content = "You agree to use the app only for lawful purposes and in accordance with these Terms. You agree not to:\n• Use the app in any way that violates applicable laws\n• Attempt to reverse-engineer or decompile the app\n• Use the app for any unauthorized commercial purpose\n• Interfere with the app's operation or security"
                )

                TermsSection(
                    title = "5. Intellectual Property",
                    content = "All content, features, and functionality of the SensiXpert app, including but not limited to text, graphics, logos, icons, and software, are the exclusive property of SensiXpert and are protected by international copyright, trademark, and other intellectual property laws."
                )

                TermsSection(
                    title = "6. Advertisements",
                    content = "The app displays advertisements through third-party ad networks including Google AdMob. By using the app, you consent to receiving such advertisements. Ad content is provided by third parties and we are not responsible for the content of these advertisements."
                )

                TermsSection(
                    title = "7. Disclaimer of Warranties",
                    content = "The app is provided \"as is\" and \"as available\" without any warranties of any kind, either express or implied. We do not guarantee that the app will be uninterrupted, error-free, or free of viruses or other harmful components. Sensitivity settings are recommendations and results may vary based on your device and game version."
                )

                TermsSection(
                    title = "8. Limitation of Liability",
                    content = "In no event shall SensiXpert, its developers, or affiliates be liable for any indirect, incidental, special, consequential, or punitive damages arising out of or related to your use of the app."
                )

                TermsSection(
                    title = "9. Data Usage",
                    content = "Your use of the app is also governed by our Privacy Policy. We do not sell your personal data to third parties. Any data collected is used solely for improving app functionality and user experience."
                )

                TermsSection(
                    title = "10. Modifications",
                    content = "We reserve the right to modify these Terms and Conditions at any time. Changes will be effective immediately upon posting within the app. Your continued use of the app following any changes constitutes acceptance of the new terms."
                )

                TermsSection(
                    title = "11. Termination",
                    content = "We may terminate or suspend your access to the app immediately, without prior notice, for any reason whatsoever, including without limitation if you breach these Terms and Conditions."
                )

                TermsSection(
                    title = "12. Contact",
                    content = "If you have any questions about these Terms and Conditions, please contact us through the app's support section."
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
