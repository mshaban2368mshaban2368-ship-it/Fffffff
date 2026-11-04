package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chat: Chat,
    messages: List<Message>,
    currentUserCode: String,
    isTyping: Boolean,
    onBackClick: () -> Unit,
    onSendMessage: (text: String, type: MessageType, extra: Map<String, String>) -> Unit,
    onStartVoiceRecording: () -> Unit,
    onFinishVoiceRecording: () -> Unit,
    onCancelVoiceRecording: () -> Unit,
    isRecordingVoice: Boolean,
    recordingDurationSeconds: Int,
    onStartCall: (contactCode: String, isVideo: Boolean) -> Unit,
    onAddReaction: (messageId: String, emoji: String) -> Unit,
    onDeleteMessage: (messageId: String, forEveryone: Boolean) -> Unit,
    onToggleStar: (messageId: String) -> Unit,
    onEditMessage: (messageId: String, newText: String) -> Unit,
    onSetDisappearingHours: (hours: Int) -> Unit,
    onClearChat: () -> Unit,
    onToggleBlock: (contactCode: String) -> Unit,
    onViewMedia: (title: String, subtitle: String) -> Unit
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    val listState = rememberLazyListState()

    var showMenu by remember { mutableStateOf(false) }
    var showDisappearingDialog by remember { mutableStateOf(false) }
    var showSecurityInfoDialog by remember { mutableStateOf(false) }
    var selectedMessageForOptions by remember { mutableStateOf<Message?>(null) }
    var replyingToMessage by remember { mutableStateOf<Message?>(null) }
    var editingMessage by remember { mutableStateOf<Message?>(null) }
    var editInputText by remember { mutableStateOf("") }

    val partnerCode = remember(chat) {
        chat.participantCodes.firstOrNull { it != currentUserCode } ?: "GROUP"
    }

    // Scroll to bottom on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = FaDarkBg,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FaDarkTextPrimary
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showSecurityInfoDialog = true }
                    ) {
                        AvatarView(
                            emoji = chat.avatarEmoji,
                            size = 38.dp,
                            isOnline = !chat.isGroup
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = chat.title,
                                color = FaDarkTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = if (isTyping) "typing..." else if (chat.isGroup) "${chat.participantCodes.size} members" else "online",
                                color = if (isTyping) FaTealGreen else FaDarkTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onStartCall(partnerCode, true) }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = FaDarkTextPrimary
                        )
                    }
                    IconButton(onClick = { onStartCall(partnerCode, false) }) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Voice Call",
                            tint = FaDarkTextPrimary
                        )
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = FaDarkTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FaDarkTopBar)
            )
        },
        bottomBar = {
            ChatInputBar(
                onSendMessage = { text, type, extra ->
                    onSendMessage(text, type, extra)
                    replyingToMessage = null
                },
                onStartVoiceRecording = onStartVoiceRecording,
                onFinishVoiceRecording = onFinishVoiceRecording,
                onCancelVoiceRecording = onCancelVoiceRecording,
                isRecordingVoice = isRecordingVoice,
                recordingDurationSeconds = recordingDurationSeconds,
                replyToMessage = replyingToMessage,
                onClearReply = { replyingToMessage = null }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Authentic WhatsApp Doodle Background
            Image(
                painter = painterResource(id = R.drawable.img_chat_wallpaper),
                contentDescription = "Chat wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.35f
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Encryption Security Card at top of chat
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF182229))
                            .clickable { showSecurityInfoDialog = true }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Encrypted",
                                tint = Color(0xFFFFD279),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Messages and calls are end-to-end encrypted. No one outside of this chat, not even FA97 Chat, can read or listen to them. Tap to learn more.",
                                color = Color(0xFFFFD279),
                                fontSize = 11.5.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Disappearing banner indicator if active
                if (chat.disappearingDurationHours > 0) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1F2C34))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⏱️ Disappearing messages enabled (${chat.disappearingDurationHours}h)",
                                color = FaTealGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Chat Messages List
                items(messages, key = { it.id }) { msg ->
                    val isOutgoing = msg.senderCode == currentUserCode
                    MessageBubble(
                        message = msg,
                        isOutgoing = isOutgoing,
                        onLongClick = { selectedMessageForOptions = msg },
                        onMediaClick = {
                            onViewMedia(
                                if (msg.type == MessageType.PHOTO) "Photo from ${msg.senderName}" else msg.text,
                                "FA97 Encrypted Media"
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Floating Reaction Picker if message selected
            if (selectedMessageForOptions != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { selectedMessageForOptions = null },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        ReactionPickerBar(
                            isVisible = true,
                            onReactionSelected = { emoji ->
                                onAddReaction(selectedMessageForOptions!!.id, emoji)
                                selectedMessageForOptions = null
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = FaDarkSurface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                val isOutgoing = selectedMessageForOptions!!.senderCode == currentUserCode

                                MessageActionRow(
                                    icon = Icons.Default.Reply,
                                    title = "Reply",
                                    onClick = {
                                        replyingToMessage = selectedMessageForOptions
                                        selectedMessageForOptions = null
                                    }
                                )
                                MessageActionRow(
                                    icon = Icons.Default.ContentCopy,
                                    title = "Copy Text",
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("FA97 Message", selectedMessageForOptions!!.text)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                        selectedMessageForOptions = null
                                    }
                                )
                                MessageActionRow(
                                    icon = Icons.Default.Star,
                                    title = if (selectedMessageForOptions!!.isStarred) "Unstar" else "Star Message",
                                    onClick = {
                                        onToggleStar(selectedMessageForOptions!!.id)
                                        selectedMessageForOptions = null
                                    }
                                )
                                if (isOutgoing) {
                                    MessageActionRow(
                                        icon = Icons.Default.Edit,
                                        title = "Edit Message",
                                        onClick = {
                                            editingMessage = selectedMessageForOptions
                                            editInputText = selectedMessageForOptions!!.text
                                            selectedMessageForOptions = null
                                        }
                                    )
                                    MessageActionRow(
                                        icon = Icons.Default.Delete,
                                        title = "Delete for Everyone",
                                        color = Color(0xFFFF5252),
                                        onClick = {
                                            onDeleteMessage(selectedMessageForOptions!!.id, true)
                                            selectedMessageForOptions = null
                                        }
                                    )
                                }
                                MessageActionRow(
                                    icon = Icons.Default.DeleteOutline,
                                    title = "Delete for Me",
                                    color = Color(0xFFFF5252),
                                    onClick = {
                                        onDeleteMessage(selectedMessageForOptions!!.id, false)
                                        selectedMessageForOptions = null
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Edit Message Dialog
        if (editingMessage != null) {
            AlertDialog(
                onDismissRequest = { editingMessage = null },
                containerColor = FaDarkSurface,
                title = { Text("Edit Message", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = editInputText,
                        onValueChange = { editInputText = it },
                        singleLine = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FaTealGreen,
                            focusedTextColor = FaDarkTextPrimary,
                            unfocusedTextColor = FaDarkTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (editInputText.isNotBlank()) {
                            onEditMessage(editingMessage!!.id, editInputText.trim())
                        }
                        editingMessage = null
                    }) {
                        Text("Save", color = FaTealGreen)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingMessage = null }) {
                        Text("Cancel", color = FaDarkTextSecondary)
                    }
                }
            )
        }

        // Chat More Menu
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(FaDarkSurface)
        ) {
            DropdownMenuItem(
                text = { Text("Disappearing Messages", color = FaDarkTextPrimary) },
                leadingIcon = { Icon(Icons.Default.Timelapse, contentDescription = null, tint = FaTealGreen) },
                onClick = {
                    showMenu = false
                    showDisappearingDialog = true
                }
            )
            DropdownMenuItem(
                text = { Text("Verify End-to-End Encryption", color = FaDarkTextPrimary) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FaGreenAccent) },
                onClick = {
                    showMenu = false
                    showSecurityInfoDialog = true
                }
            )
            DropdownMenuItem(
                text = { Text("Clear Messages", color = FaDarkTextPrimary) },
                leadingIcon = { Icon(Icons.Default.CleaningServices, contentDescription = null, tint = FaDarkTextSecondary) },
                onClick = {
                    showMenu = false
                    onClearChat()
                    Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text("Block Contact", color = Color(0xFFFF5252)) },
                leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFFF5252)) },
                onClick = {
                    showMenu = false
                    onToggleBlock(partnerCode)
                    Toast.makeText(context, "Contact blocked", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Disappearing Messages Selector Dialog
        if (showDisappearingDialog) {
            AlertDialog(
                onDismissRequest = { showDisappearingDialog = false },
                containerColor = FaDarkSurface,
                title = { Text("Disappearing Messages", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("New messages in this chat will disappear for everyone after the chosen duration:", color = FaDarkTextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        listOf(
                            0 to "Off",
                            24 to "24 Hours",
                            168 to "7 Days",
                            2160 to "90 Days"
                        ).forEach { (hours, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSetDisappearingHours(hours)
                                        showDisappearingDialog = false
                                        Toast.makeText(context, "Disappearing messages: $label", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = chat.disappearingDurationHours == hours,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = FaTealGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = label, color = FaDarkTextPrimary, fontSize = 15.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDisappearingDialog = false }) {
                        Text("Done", color = FaTealGreen)
                    }
                }
            )
        }

        // Security Verification Info Dialog
        if (showSecurityInfoDialog) {
            AlertDialog(
                onDismissRequest = { showSecurityInfoDialog = false },
                containerColor = FaDarkSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = FaGreenAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Security & Encryption", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "FA97 Chat utilizes end-to-end cryptographic key pairs derived solely from your permanent FA97 Code and device enclave.",
                            color = FaDarkTextSecondary,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0B141B))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Safety Number Verification", color = FaTealGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("48912 • 94021 • 65839 • 10294\n77321 • 59302 • 18492 • 33019", color = FaDarkTextPrimary, fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            }
                        }
                        Text(
                            text = "To verify security, compare this number with the number on your friend's device or scan their FA97 QR code.",
                            color = FaDarkTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSecurityInfoDialog = false }) {
                        Text("Understood", color = FaTealGreen)
                    }
                }
            )
        }
    }
}

@Composable
private fun MessageActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color = FaDarkTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, color = color, fontSize = 14.sp)
    }
}
