package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    isOutgoing: Boolean,
    onLongClick: () -> Unit,
    onMediaClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val bubbleBg = if (isOutgoing) FaDarkOutgoingBubble else FaDarkIncomingBubble
    val bubbleShape = if (isOutgoing) {
        RoundedCornerShape(topStart = 14.dp, topEnd = 4.dp, bottomEnd = 14.dp, bottomStart = 14.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 14.dp, bottomEnd = 14.dp, bottomStart = 14.dp)
    }

    val timeFormatted = remember(message.timestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    // Voice note simulation state
    var isPlayingVoice by remember { mutableStateOf(false) }
    var voiceProgress by remember { mutableStateOf(0.3f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .widthIn(min = 80.dp, max = 300.dp)
                    .clip(bubbleShape)
                    .background(bubbleBg)
                    .combinedClickable(
                        onClick = {
                            if (message.type == MessageType.PHOTO) onMediaClick?.invoke()
                        },
                        onLongClick = onLongClick
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Column {
                    // Group sender label if incoming
                    if (!isOutgoing && message.senderName.isNotBlank()) {
                        Text(
                            text = message.senderName,
                            color = FaGreenAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Quoted Reply Preview
                    if (message.replyToText != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.2f))
                                .padding(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(28.dp)
                                    .background(FaTealGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = message.replyToSender ?: "Reply",
                                    color = FaTealGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = message.replyToText,
                                    color = FaDarkTextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Content by type
                    when (message.type) {
                        MessageType.TEXT -> {
                            Text(
                                text = message.text,
                                color = FaDarkTextPrimary,
                                fontSize = 15.sp,
                                lineHeight = 20.sp
                            )
                        }

                        MessageType.VOICE -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                IconButton(
                                    onClick = { isPlayingVoice = !isPlayingVoice },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlayingVoice) FaGreenAccent else Color.White.copy(alpha = 0.15f))
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play voice note",
                                        tint = if (isPlayingVoice) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Slider(
                                        value = voiceProgress,
                                        onValueChange = { voiceProgress = it },
                                        colors = SliderDefaults.colors(
                                            thumbColor = FaGreenAccent,
                                            activeTrackColor = FaGreenAccent,
                                            inactiveTrackColor = Color.Gray.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier.height(20.dp)
                                    )
                                    Text(
                                        text = "0:${String.format("%02d", message.mediaDurationSeconds)}",
                                        color = FaDarkTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        MessageType.LOCATION -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.25f))
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = message.locationTitle ?: "Shared Location",
                                        color = FaDarkTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap to open in Google Maps",
                                    color = FaTealGreen,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        MessageType.DOCUMENT -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.2f))
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.InsertDriveFile,
                                    contentDescription = "Document",
                                    tint = FaGreenAccent,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = message.fileName ?: "Document.pdf",
                                        color = FaDarkTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${message.fileSize ?: "2.4 MB"} • PDF",
                                        color = FaDarkTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download",
                                    tint = FaDarkTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        MessageType.PHOTO -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF003728))
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Photo,
                                    contentDescription = "Photo",
                                    tint = FaGreenAccent,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (message.text.isNotBlank()) message.text else "📷 Encrypted Photo",
                                    color = FaDarkTextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Timestamp & Status Ticks Row
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.isEdited) {
                            Text(
                                text = "edited ",
                                color = FaDarkTextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        if (message.isStarred) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Starred",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier
                                    .size(11.dp)
                                    .padding(end = 3.dp)
                            )
                        }

                        Text(
                            text = timeFormatted,
                            color = FaDarkTextSecondary,
                            fontSize = 11.sp
                        )

                        if (isOutgoing) {
                            Spacer(modifier = Modifier.width(3.dp))
                            when (message.status) {
                                MessageStatus.SENDING -> {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Sending",
                                        tint = FaDarkTextSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                MessageStatus.SENT -> {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sent",
                                        tint = FaGreyTick,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                MessageStatus.DELIVERED -> {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = "Delivered",
                                        tint = FaGreyTick,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                MessageStatus.READ -> {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = "Read",
                                        tint = FaBlueTick,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reactions pill below bubble
            if (message.reactions.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .offset(y = (-6).dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F2C34))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val uniqueReactions = message.reactions.values.distinct()
                    uniqueReactions.forEach { emoji ->
                        Text(text = emoji, fontSize = 11.sp)
                    }
                    if (message.reactions.size > 1) {
                        Text(
                            text = "${message.reactions.size}",
                            color = FaDarkTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
