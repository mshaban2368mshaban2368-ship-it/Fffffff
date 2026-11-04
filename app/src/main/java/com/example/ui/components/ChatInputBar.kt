package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.model.MessageType
import com.example.ui.theme.*

@Composable
fun ChatInputBar(
    onSendMessage: (String, MessageType, Map<String, String>) -> Unit,
    onStartVoiceRecording: () -> Unit,
    onFinishVoiceRecording: () -> Unit,
    onCancelVoiceRecording: () -> Unit,
    isRecordingVoice: Boolean,
    recordingDurationSeconds: Int,
    replyToMessage: Message?,
    onClearReply: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var showAttachmentDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Quoted Reply Preview Bar
        AnimatedVisibility(
            visible = replyToMessage != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            if (replyToMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E2A30))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(FaTealGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = replyToMessage.senderName,
                            color = FaTealGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = replyToMessage.text,
                            color = FaDarkTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = onClearReply, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel reply",
                            tint = FaDarkTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B141B))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRecordingVoice) {
                // Voice Recording State UI
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1F2C34))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "0:${String.format("%02d", recordingDurationSeconds)}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "◀ Slide to cancel",
                        color = FaDarkTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Cancel",
                        color = Color(0xFFFF5252),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onCancelVoiceRecording() }
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Send Voice Note Button
                FloatingActionButton(
                    onClick = onFinishVoiceRecording,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    containerColor = FaTealGreen,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send voice note",
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                // Normal Text & Attachment Input Box
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1F2C34))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEmotions,
                        contentDescription = "Emoji picker",
                        tint = FaDarkTextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                text += "😊"
                            }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            color = FaDarkTextPrimary,
                            fontSize = 16.sp
                        ),
                        cursorBrush = SolidColor(FaTealGreen),
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(
                                    text = "Message",
                                    color = FaDarkTextSecondary,
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Attachment Paperclip
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attachments",
                        tint = FaDarkTextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showAttachmentDialog = true }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Quick Camera Button
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        tint = FaDarkTextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                onSendMessage("Captured Photo", MessageType.PHOTO, emptyMap())
                            }
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Send or Mic Button
                FloatingActionButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSendMessage(text.trim(), MessageType.TEXT, emptyMap())
                            text = ""
                        } else {
                            onStartVoiceRecording()
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    containerColor = FaTealGreen,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (text.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
                        contentDescription = if (text.isNotBlank()) "Send" else "Record voice",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Attachments Modal Sheet
        if (showAttachmentDialog) {
            AlertDialog(
                onDismissRequest = { showAttachmentDialog = false },
                containerColor = Color(0xFF1F2C34),
                title = {
                    Text("Share Content", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AttachmentOption(
                            icon = Icons.Default.Photo,
                            title = "Gallery / Photo",
                            color = Color(0xFFAC44CF),
                            onClick = {
                                showAttachmentDialog = false
                                onSendMessage("Project Screenshot", MessageType.PHOTO, emptyMap())
                            }
                        )
                        AttachmentOption(
                            icon = Icons.Default.InsertDriveFile,
                            title = "Document",
                            color = Color(0xFF5F66CD),
                            onClick = {
                                showAttachmentDialog = false
                                onSendMessage(
                                    "FA97_User_Guide.pdf",
                                    MessageType.DOCUMENT,
                                    mapOf("fileName" to "FA97_User_Guide.pdf", "fileSize" to "1.5 MB")
                                )
                            }
                        )
                        AttachmentOption(
                            icon = Icons.Default.LocationOn,
                            title = "Location",
                            color = Color(0xFF0F9D58),
                            onClick = {
                                showAttachmentDialog = false
                                onSendMessage(
                                    "📍 Shared Location",
                                    MessageType.LOCATION,
                                    mapOf("locationTitle" to "Coffee Central, Downtown")
                                )
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAttachmentDialog = false }) {
                        Text("Close", color = FaTealGreen)
                    }
                }
            )
        }
    }
}

@Composable
private fun AttachmentOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = FaDarkTextPrimary, fontSize = 16.sp)
    }
}
