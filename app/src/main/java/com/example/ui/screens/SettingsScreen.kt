package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.components.AvatarView
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    user: UserProfile,
    onMyQrClick: () -> Unit,
    onUpdateProfile: (name: String, about: String, emoji: String, email: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showBackupKeyDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showChatSettingsDialog by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf(user.name) }
    var editAbout by remember { mutableStateOf(user.about) }
    var editEmail by remember { mutableStateOf(user.recoveryEmail) }
    var readReceiptsEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FaDarkBg)
    ) {
        // User Profile Header Card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEditProfileDialog = true }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarView(emoji = user.avatarEmoji, size = 64.dp)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        color = FaDarkTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user.about,
                        color = FaDarkTextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.fa97Code,
                            color = FaTealGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy code",
                            tint = FaDarkTextSecondary,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("FA97 Code", user.fa97Code))
                                    Toast.makeText(context, "FA97 Code copied!", Toast.LENGTH_SHORT).show()
                                }
                        )
                    }
                }

                IconButton(onClick = onMyQrClick) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "My QR Code",
                        tint = FaGreenAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Divider(color = FaDarkDivider, thickness = 0.5.dp)
        }

        // Settings items
        item {
            SettingsCategoryRow(
                icon = Icons.Default.Key,
                title = "Account Backup Key & Security",
                subtitle = "View permanent recovery key, email, 2-step verification",
                onClick = { showBackupKeyDialog = true }
            )
            SettingsCategoryRow(
                icon = Icons.Default.Lock,
                title = "Privacy",
                subtitle = "Last seen, profile photo, read receipts, blocked contacts",
                onClick = { showPrivacyDialog = true }
            )
            SettingsCategoryRow(
                icon = Icons.Default.Chat,
                title = "Chats",
                subtitle = "Theme, wallpapers, chat history, export",
                onClick = { showChatSettingsDialog = true }
            )
            SettingsCategoryRow(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Message tones, group tones, call vibration",
                onClick = {
                    Toast.makeText(context, "High priority notifications active", Toast.LENGTH_SHORT).show()
                }
            )
            SettingsCategoryRow(
                icon = Icons.Default.DataUsage,
                title = "Storage and Data",
                subtitle = "Network usage, auto-download media settings",
                onClick = {
                    Toast.makeText(context, "All media stored securely locally", Toast.LENGTH_SHORT).show()
                }
            )
            SettingsCategoryRow(
                icon = Icons.Default.Info,
                title = "About FA97 Chat",
                subtitle = "Version 1.0 (Zero Phone Numbers Protocol)",
                onClick = {
                    Toast.makeText(context, "FA97 Chat: Pure privacy messaging", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Footer branding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔒 FA97 Protocol 256-bit",
                    color = FaDarkTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "No Phone Numbers • No SMS Tracking • No Spam",
                    color = FaDarkTextSecondary.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }
    }

    // Backup Key Modal Dialog
    if (showBackupKeyDialog) {
        AlertDialog(
            onDismissRequest = { showBackupKeyDialog = false },
            containerColor = FaDarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = FaGreenAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Account Backup Key", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Because FA97 Chat requires NO phone numbers, your account is backed up with this unique cryptographic key. Save it securely to restore your profile on any device.",
                        color = FaDarkTextSecondary,
                        fontSize = 13.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0B141B))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.backupKey,
                            color = FaGreenAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("FA97 Backup Key", user.backupKey))
                            Toast.makeText(context, "Backup Key copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FaTealGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Backup Key")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBackupKeyDialog = false }) {
                    Text("Done", color = FaTealGreen)
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = FaDarkSurface,
            title = { Text("Edit Profile", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FaTealGreen,
                            focusedTextColor = FaDarkTextPrimary,
                            unfocusedTextColor = FaDarkTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editAbout,
                        onValueChange = { editAbout = it },
                        label = { Text("About Status") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FaTealGreen,
                            focusedTextColor = FaDarkTextPrimary,
                            unfocusedTextColor = FaDarkTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Recovery Email (Optional)") },
                        placeholder = { Text("your.email@domain.com") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FaTealGreen,
                            focusedTextColor = FaDarkTextPrimary,
                            unfocusedTextColor = FaDarkTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editName, editAbout, user.avatarEmoji, editEmail)
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FaTealGreen)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = FaDarkTextSecondary)
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = FaDarkSurface,
            title = { Text("Privacy Controls", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Read Receipts (Blue Ticks)", color = FaDarkTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text("If turned off, you won't send or receive read receipts", color = FaDarkTextSecondary, fontSize = 12.sp)
                        }
                        Switch(
                            checked = readReceiptsEnabled,
                            onCheckedChange = { readReceiptsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = FaTealGreen)
                        )
                    }

                    Divider(color = FaDarkDivider)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Last Seen & Online", color = FaDarkTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text("Currently set to: My Contacts", color = FaTealGreen, fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Blocked Contacts", color = FaDarkTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text("Manage blocked FA97 codes", color = FaDarkTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Done", color = FaTealGreen)
                }
            }
        )
    }

    // Chat Settings Dialog
    if (showChatSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showChatSettingsDialog = false },
            containerColor = FaDarkSurface,
            title = { Text("Chat Settings", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Wallpaper", color = FaDarkTextPrimary, fontWeight = FontWeight.Medium)
                    Text("Authentic WhatsApp Doodle background is active for all chats.", color = FaDarkTextSecondary, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Font Size", color = FaDarkTextPrimary, fontWeight = FontWeight.Medium)
                    Text("Medium (System Default)", color = FaTealGreen, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("End-to-End Encryption", color = FaDarkTextPrimary, fontWeight = FontWeight.Medium)
                    Text("All 1:1 and Group chats are locked with ratchet encryption keys.", color = FaDarkTextSecondary, fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showChatSettingsDialog = false }) {
                    Text("Close", color = FaTealGreen)
                }
            }
        )
    }
}

@Composable
private fun SettingsCategoryRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = FaDarkTextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = FaDarkTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = FaDarkTextSecondary, fontSize = 13.sp)
        }
    }
}
