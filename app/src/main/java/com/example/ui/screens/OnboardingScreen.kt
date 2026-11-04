package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: (name: String, about: String, avatarEmoji: String) -> Unit,
    onRestore: (codeOrKey: String) -> Boolean
) {
    var isRestoring by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("Hey there! I am using FA97 Chat.") }
    var selectedEmoji by remember { mutableStateOf("😎") }
    var restoreInput by remember { mutableStateOf("") }
    var restoreError by remember { mutableStateOf<String?>(null) }

    val avatarOptions = listOf("😎", "👩🏻‍💻", "👨🏽‍💼", "🚀", "🦊", "🌟", "🔥", "💎")

    Scaffold(
        containerColor = FaDarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Logo & Shield
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF003728)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Security",
                    tint = FaGreenAccent,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to FA97 Chat",
                color = FaDarkTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Private messaging without phone numbers or SMS tracking. Connect solely via your permanent FA97 Code.",
                color = FaDarkTextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isRestoring) {
                // Profile Setup Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = FaDarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Choose Your Avatar",
                            color = FaDarkTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Avatar Selector Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            avatarOptions.take(4).forEach { emoji ->
                                AvatarOptionItem(
                                    emoji = emoji,
                                    isSelected = selectedEmoji == emoji,
                                    onClick = { selectedEmoji = emoji }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            avatarOptions.drop(4).forEach { emoji ->
                                AvatarOptionItem(
                                    emoji = emoji,
                                    isSelected = selectedEmoji == emoji,
                                    onClick = { selectedEmoji = emoji }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Your Display Name") },
                            placeholder = { Text("e.g. Alex Morgan") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FaTealGreen,
                                unfocusedBorderColor = FaDarkDivider,
                                focusedTextColor = FaDarkTextPrimary,
                                unfocusedTextColor = FaDarkTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = about,
                            onValueChange = { about = it },
                            label = { Text("About Status") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FaTealGreen,
                                unfocusedBorderColor = FaDarkDivider,
                                focusedTextColor = FaDarkTextPrimary,
                                unfocusedTextColor = FaDarkTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Feature Highlights
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FaTealGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Generates permanent FA97-XXXXXX Code",
                                color = FaDarkTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FaTealGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Includes 16-character Account Backup Key",
                                color = FaDarkTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                onComplete(
                                    name.ifBlank { "Alex Morgan" },
                                    about.ifBlank { "Hey there! I am using FA97 Chat." },
                                    selectedEmoji
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FaTealGreen),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Agree & Generate My Code",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { isRestoring = true }) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = FaTealGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Already have a Backup Key or FA97 Code? Restore",
                        color = FaTealGreen,
                        fontSize = 13.sp
                    )
                }
            } else {
                // Restore Account Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = FaDarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Restore Account",
                            color = FaDarkTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Enter your 16-character Backup Key (e.g. FA97-SEC-8491-3042) or FA97 Code to recover your chats and contacts.",
                            color = FaDarkTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = restoreInput,
                            onValueChange = {
                                restoreInput = it
                                restoreError = null
                            },
                            label = { Text("Backup Key or FA97 Code") },
                            placeholder = { Text("FA97-8K3D9Q") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FaTealGreen,
                                unfocusedBorderColor = FaDarkDivider,
                                focusedTextColor = FaDarkTextPrimary,
                                unfocusedTextColor = FaDarkTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (restoreError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = restoreError!!,
                                color = Color(0xFFFF5252),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (restoreInput.isBlank()) {
                                    restoreError = "Please enter your code or backup key."
                                } else {
                                    val success = onRestore(restoreInput)
                                    if (!success) {
                                        restoreError = "Invalid code. Please verify your backup key."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FaTealGreen),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Restore Account",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(onClick = { isRestoring = false }) {
                            Text("Back to New Account", color = FaTealGreen)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarOptionItem(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color(0xFF1F2C34))
            .border(
                width = if (isSelected) 2.5.dp else 0.dp,
                color = if (isSelected) FaGreenAccent else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 26.sp)
    }
}
