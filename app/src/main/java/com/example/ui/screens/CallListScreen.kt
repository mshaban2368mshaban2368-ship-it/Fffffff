package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallRecord
import com.example.model.CallStatus
import com.example.model.Contact
import com.example.ui.components.AvatarView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallListScreen(
    calls: List<CallRecord>,
    contacts: List<Contact>,
    onStartCall: (contactCode: String, isVideo: Boolean) -> Unit,
    onSimulateIncomingCall: (contactCode: String, isVideo: Boolean) -> Unit,
    onClearCalls: () -> Unit
) {
    var showStartCallDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = FaDarkBg,
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Simulate Incoming Call helper button
                ExtendedFloatingActionButton(
                    onClick = {
                        val sample = contacts.firstOrNull()?.fa97Code ?: "FA97-4N9X2W"
                        onSimulateIncomingCall(sample, true)
                    },
                    containerColor = Color(0xFF1F2C34),
                    contentColor = FaGreenAccent,
                    icon = { Icon(Icons.Default.PhoneCallback, contentDescription = null) },
                    text = { Text("Test Incoming Call", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )

                FloatingActionButton(
                    onClick = { showStartCallDialog = true },
                    containerColor = FaTealGreen,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "New Call"
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent calls",
                        color = FaDarkTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (calls.isNotEmpty()) {
                        TextButton(onClick = onClearCalls) {
                            Text("Clear History", color = FaTealGreen, fontSize = 12.sp)
                        }
                    }
                }
            }

            items(calls, key = { it.id }) { call ->
                CallRowItem(
                    call = call,
                    onVoiceCallBack = { onStartCall(call.contactCode, false) },
                    onVideoCallBack = { onStartCall(call.contactCode, true) }
                )
            }

            if (calls.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📞", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No call history",
                                color = FaDarkTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "To start a private, end-to-end encrypted call, tap the call button below.",
                                color = FaDarkTextSecondary,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Contact Picker Dialog for new call
        if (showStartCallDialog) {
            AlertDialog(
                onDismissRequest = { showStartCallDialog = false },
                containerColor = FaDarkSurface,
                title = { Text("Start a Call", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        contacts.forEach { contact ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AvatarView(emoji = contact.avatarEmoji, size = 40.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(contact.name, color = FaDarkTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                    Text(contact.fa97Code, color = FaDarkTextSecondary, fontSize = 12.sp)
                                }
                                IconButton(onClick = {
                                    showStartCallDialog = false
                                    onStartCall(contact.fa97Code, false)
                                }) {
                                    Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = FaTealGreen)
                                }
                                IconButton(onClick = {
                                    showStartCallDialog = false
                                    onStartCall(contact.fa97Code, true)
                                }) {
                                    Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = FaGreenAccent)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showStartCallDialog = false }) {
                        Text("Cancel", color = FaTealGreen)
                    }
                }
            )
        }
    }
}

@Composable
private fun CallRowItem(
    call: CallRecord,
    onVoiceCallBack: () -> Unit,
    onVideoCallBack: () -> Unit
) {
    val timeFormatted = remember(call.timestamp) {
        val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        sdf.format(Date(call.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarView(emoji = call.contactAvatarEmoji, size = 48.dp)

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = call.contactName,
                color = if (call.status == CallStatus.MISSED) Color(0xFFFF5252) else FaDarkTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (call.status) {
                    CallStatus.INCOMING -> Icons.Default.CallReceived
                    CallStatus.OUTGOING -> Icons.Default.CallMade
                    CallStatus.MISSED -> Icons.Default.CallMissed
                }
                val tint = when (call.status) {
                    CallStatus.MISSED -> Color(0xFFFF5252)
                    CallStatus.INCOMING -> FaTealGreen
                    CallStatus.OUTGOING -> FaGreenAccent
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$timeFormatted • ${call.durationText}",
                    color = FaDarkTextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        IconButton(onClick = if (call.isVideo) onVideoCallBack else onVoiceCallBack) {
            Icon(
                imageVector = if (call.isVideo) Icons.Default.Videocam else Icons.Default.Call,
                contentDescription = "Call back",
                tint = FaTealGreen
            )
        }
    }
}
