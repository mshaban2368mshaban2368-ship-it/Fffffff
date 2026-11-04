package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Chat
import com.example.ui.components.AvatarView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    chats: List<Chat>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onChatClick: (String) -> Unit,
    onNewChatClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onMyQrClick: () -> Unit,
    onTogglePin: (String) -> Unit,
    onToggleMute: (String) -> Unit,
    onToggleArchive: (String) -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var showActionDialog by remember { mutableStateOf(false) }
    var selectedChatForOptions by remember { mutableStateOf<Chat?>(null) }

    val filteredChats = remember(chats, searchQuery) {
        if (searchQuery.isBlank()) chats.filter { !it.isArchived }
        else chats.filter {
            !it.isArchived && (it.title.contains(searchQuery, ignoreCase = true) ||
                    it.lastMessageText.contains(searchQuery, ignoreCase = true))
        }
    }

    val pinnedChats = filteredChats.filter { it.isPinned }
    val normalChats = filteredChats.filter { !it.isPinned }
    val displayList = pinnedChats + normalChats

    Scaffold(
        containerColor = FaDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search chats or messages...", color = FaDarkTextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = FaDarkTextPrimary,
                                unfocusedTextColor = FaDarkTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Column {
                            Text(
                                text = "FA97 Chat",
                                color = FaDarkTextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "End-to-End Encrypted",
                                color = FaTealGreen,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSearchActive = !isSearchActive
                        if (!isSearchActive) onSearchQueryChange("")
                    }) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = FaDarkTextPrimary
                        )
                    }

                    IconButton(onClick = onMyQrClick) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "My QR Code",
                            tint = FaDarkTextPrimary
                        )
                    }

                    IconButton(onClick = { showActionDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = FaDarkTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FaDarkSurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewChatClick,
                containerColor = FaTealGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "New Chat",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Archived Row if any
            val archivedCount = chats.count { it.isArchived }
            if (archivedCount > 0) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Toggle archived view */ }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archived",
                            tint = FaDarkTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = "Archived",
                            color = FaDarkTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$archivedCount",
                            color = FaTealGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Divider(color = FaDarkDivider, thickness = 0.5.dp)
                }
            }

            // Chat Items
            items(displayList, key = { it.id }) { chat ->
                ChatItemRow(
                    chat = chat,
                    onClick = { onChatClick(chat.id) },
                    onLongClick = { selectedChatForOptions = chat }
                )
            }

            if (displayList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "💬",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No chats found" else "No chats yet",
                                color = FaDarkTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the message button to add a friend with their FA97 Code or scan their QR code.",
                                color = FaDarkTextSecondary,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Dropdown Menu
        if (showActionDialog) {
            AlertDialog(
                onDismissRequest = { showActionDialog = false },
                containerColor = FaDarkSurface,
                title = { Text("Chat Options", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ListItem(
                            headlineContent = { Text("New Group", color = FaDarkTextPrimary) },
                            leadingContent = { Icon(Icons.Default.GroupAdd, contentDescription = null, tint = FaGreenAccent) },
                            modifier = Modifier.clickable {
                                showActionDialog = false
                                onCreateGroupClick()
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Add Friend with FA97 Code", color = FaDarkTextPrimary) },
                            leadingContent = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = FaTealGreen) },
                            modifier = Modifier.clickable {
                                showActionDialog = false
                                onNewChatClick()
                            }
                        )
                        ListItem(
                            headlineContent = { Text("My FA97 Code & QR", color = FaDarkTextPrimary) },
                            leadingContent = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = FaTealGreen) },
                            modifier = Modifier.clickable {
                                showActionDialog = false
                                onMyQrClick()
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showActionDialog = false }) {
                        Text("Close", color = FaTealGreen)
                    }
                }
            )
        }

        // Long Press Chat Options Dialog
        if (selectedChatForOptions != null) {
            val chat = selectedChatForOptions!!
            AlertDialog(
                onDismissRequest = { selectedChatForOptions = null },
                containerColor = FaDarkSurface,
                title = { Text(chat.title, color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(
                            onClick = {
                                onTogglePin(chat.id)
                                selectedChatForOptions = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PushPin, contentDescription = null, tint = FaTealGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(if (chat.isPinned) "Unpin Chat" else "Pin Chat to Top", color = FaDarkTextPrimary)
                        }
                        TextButton(
                            onClick = {
                                onToggleMute(chat.id)
                                selectedChatForOptions = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VolumeMute, contentDescription = null, tint = FaTealGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(if (chat.isMuted) "Unmute Notifications" else "Mute Notifications", color = FaDarkTextPrimary)
                        }
                        TextButton(
                            onClick = {
                                onToggleArchive(chat.id)
                                selectedChatForOptions = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Archive, contentDescription = null, tint = FaTealGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Archive Chat", color = FaDarkTextPrimary)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedChatForOptions = null }) {
                        Text("Cancel", color = FaTealGreen)
                    }
                }
            )
        }
    }
}

@Composable
private fun ChatItemRow(
    chat: Chat,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val timeFormatted = remember(chat.lastMessageTime) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(chat.lastMessageTime))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarView(
            emoji = chat.avatarEmoji,
            size = 52.dp,
            isOnline = !chat.isGroup
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chat.title,
                    color = FaDarkTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = timeFormatted,
                    color = if (chat.unreadCount > 0) FaGreenAccent else FaDarkTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Read ticks on outgoing
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Read",
                    tint = FaBlueTick,
                    modifier = Modifier
                        .size(15.dp)
                        .padding(end = 4.dp)
                )

                Text(
                    text = chat.lastMessageText,
                    color = FaDarkTextSecondary,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (chat.isMuted) {
                    Icon(
                        imageVector = Icons.Default.VolumeOff,
                        contentDescription = "Muted",
                        tint = FaDarkTextSecondary,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 6.dp)
                    )
                }

                if (chat.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = FaDarkTextSecondary,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 6.dp)
                    )
                }

                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(FaGreenAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${chat.unreadCount}",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
