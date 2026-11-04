package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.StatusStory
import com.example.model.UserProfile
import com.example.ui.components.AvatarView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatusScreen(
    currentUser: UserProfile,
    statuses: List<StatusStory>,
    onStatusClick: (String) -> Unit,
    onPostStatus: (text: String, colorHex: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddStatusDialog by remember { mutableStateOf(false) }
    var newStatusText by remember { mutableStateOf("") }
    val colorPalettes = listOf("#00A884", "#6B38FB", "#C2185B", "#FF5722", "#009688", "#1E88E5")
    var selectedColorHex by remember { mutableStateOf(colorPalettes[0]) }

    val myStatuses = statuses.filter { it.userCode == currentUser.fa97Code }
    val recentStatuses = statuses.filter { it.userCode != currentUser.fa97Code && !it.isViewed }
    val viewedStatuses = statuses.filter { it.userCode != currentUser.fa97Code && it.isViewed }

    Scaffold(
        containerColor = FaDarkBg,
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { showAddStatusDialog = true },
                    containerColor = Color(0xFF1F2C34),
                    contentColor = FaDarkTextPrimary,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Text Status")
                }

                FloatingActionButton(
                    onClick = { showAddStatusDialog = true },
                    containerColor = FaTealGreen,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Add Status")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // My Status Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (myStatuses.isNotEmpty()) {
                                onStatusClick(myStatuses.first().id)
                            } else {
                                showAddStatusDialog = true
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(54.dp)) {
                        AvatarView(
                            emoji = currentUser.avatarEmoji,
                            size = 54.dp,
                            hasStory = myStatuses.isNotEmpty(),
                            storyViewed = false
                        )
                        if (myStatuses.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(FaGreenAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Status",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My Status",
                            color = FaDarkTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (myStatuses.isNotEmpty()) "Tap to view your 24h status" else "Tap to add status update",
                            color = FaDarkTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                Divider(color = FaDarkDivider, thickness = 0.5.dp)
            }

            // Recent Updates Section
            if (recentStatuses.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent updates",
                        color = FaDarkTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                items(recentStatuses, key = { it.id }) { story ->
                    StatusRowItem(
                        story = story,
                        onClick = { onStatusClick(story.id) }
                    )
                }
            }

            // Viewed Updates Section
            if (viewedStatuses.isNotEmpty()) {
                item {
                    Text(
                        text = "Viewed updates",
                        color = FaDarkTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                items(viewedStatuses, key = { it.id }) { story ->
                    StatusRowItem(
                        story = story,
                        onClick = { onStatusClick(story.id) }
                    )
                }
            }

            // Security note about status
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = FaDarkTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Your status updates are end-to-end encrypted",
                        color = FaDarkTextSecondary,
                        fontSize = 11.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Add Text/Photo Status Dialog
        if (showAddStatusDialog) {
            AlertDialog(
                onDismissRequest = { showAddStatusDialog = false },
                containerColor = Color(android.graphics.Color.parseColor(selectedColorHex)),
                title = {
                    Text("New Status", color = Color.White, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = newStatusText,
                            onValueChange = { newStatusText = it },
                            placeholder = { Text("Type a status...", color = Color.White.copy(alpha = 0.7f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )

                        // Color picker palette
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            colorPalettes.forEach { hex ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(hex)))
                                        .border(
                                            width = if (selectedColorHex == hex) 2.5.dp else 0.dp,
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColorHex = hex }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newStatusText.isNotBlank()) {
                                onPostStatus(newStatusText.trim(), selectedColorHex)
                                newStatusText = ""
                            }
                            showAddStatusDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Post Status", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddStatusDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
private fun StatusRowItem(
    story: StatusStory,
    onClick: () -> Unit
) {
    val timeFormatted = remember(story.timestamp) {
        val diffMinutes = ((System.currentTimeMillis() - story.timestamp) / (1000 * 60)).toInt()
        when {
            diffMinutes < 60 -> "$diffMinutes minutes ago"
            diffMinutes < 1440 -> "${diffMinutes / 60} hours ago"
            else -> "Today"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarView(
            emoji = story.userAvatarEmoji,
            size = 52.dp,
            hasStory = true,
            storyViewed = story.isViewed
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = story.userName,
                color = FaDarkTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timeFormatted,
                color = FaDarkTextSecondary,
                fontSize = 13.sp
            )
        }
    }
}
