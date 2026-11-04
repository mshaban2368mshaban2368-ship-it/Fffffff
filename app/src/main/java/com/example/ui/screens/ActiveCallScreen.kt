package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AvatarView
import com.example.ui.theme.FaDarkBg
import com.example.ui.theme.FaGreenAccent
import com.example.ui.theme.FaTealGreen

@Composable
fun ActiveCallScreen(
    contactCode: String,
    contactName: String,
    contactAvatarEmoji: String,
    isVideo: Boolean,
    isIncoming: Boolean,
    isConnected: Boolean,
    callDurationSeconds: Int,
    isMuted: Boolean,
    isSpeaker: Boolean,
    isCameraOff: Boolean,
    isFrontCamera: Boolean,
    onAcceptCall: () -> Unit,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleCameraOff: () -> Unit,
    onToggleCameraSwitch: () -> Unit
) {
    BackHandler { onEndCall() }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val durationFormatted = remember(callDurationSeconds) {
        String.format("%02d:%02d", callDurationSeconds / 60, callDurationSeconds % 60)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isVideo && isConnected && !isCameraOff) Color(0xFF131D24) else FaDarkBg)
    ) {
        // Video Stream Background Simulation
        if (isVideo && isConnected && !isCameraOff) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = contactAvatarEmoji,
                        fontSize = 110.sp,
                        modifier = Modifier.scale(pulseScale)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Encrypted WebRTC Video Feed (1080p)",
                        color = FaGreenAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // PiP Local Camera Preview in top corner
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 150.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp, end = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF003728))
                        .border(1.5.dp, FaGreenAccent, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("You", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(if (isFrontCamera) "🤳 Front" else "📷 Rear", color = FaGreenAccent, fontSize = 11.sp)
                    }
                }
            }
        }

        // Voice Call or Ringing Center Details
        if (!isVideo || !isConnected || isCameraOff) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulsing Avatar
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(if (!isConnected) pulseScale else 1f),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarView(
                        emoji = contactAvatarEmoji,
                        size = 120.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = contactName,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = contactCode,
                    color = FaTealGreen,
                    fontSize = 14.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when {
                        isIncoming && !isConnected -> "Incoming ${if (isVideo) "Video" else "Voice"} Call..."
                        !isConnected -> "Calling FA97 Node..."
                        else -> durationFormatted
                    },
                    color = if (isConnected) FaGreenAccent else Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = Color(0xFFFFD279),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End-to-End Encrypted",
                        color = Color(0xFFFFD279),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Bottom Controls Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 40.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isIncoming && !isConnected) {
                // Incoming Call Accept / Decline Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decline
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FloatingActionButton(
                            onClick = onEndCall,
                            containerColor = Color(0xFFFF5252),
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = "Decline", modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Decline", color = Color.White, fontSize = 13.sp)
                    }

                    // Accept
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FloatingActionButton(
                            onClick = onAcceptCall,
                            containerColor = FaGreenAccent,
                            contentColor = Color.Black,
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(if (isVideo) Icons.Default.Videocam else Icons.Default.Call, contentDescription = "Accept", modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Accept", color = Color.White, fontSize = 13.sp)
                    }
                }
            } else {
                // Active Ongoing Call Controls
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2C34)),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute Mic
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isMuted) Color.White else Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = if (isMuted) Color.Black else Color.White
                            )
                        }

                        // Speakerphone
                        IconButton(
                            onClick = onToggleSpeaker,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaker) FaTealGreen else Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = if (isSpeaker) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                                contentDescription = "Speaker",
                                tint = Color.White
                            )
                        }

                        // Video toggles if video call
                        if (isVideo) {
                            IconButton(
                                onClick = onToggleCameraOff,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isCameraOff) Color.White else Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                    contentDescription = "Camera Toggle",
                                    tint = if (isCameraOff) Color.Black else Color.White
                                )
                            }

                            IconButton(
                                onClick = onToggleCameraSwitch,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlipCameraAndroid,
                                    contentDescription = "Switch Camera",
                                    tint = Color.White
                                )
                            }
                        }

                        // End Call Button
                        FloatingActionButton(
                            onClick = onEndCall,
                            containerColor = Color(0xFFFF5252),
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "End Call",
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
