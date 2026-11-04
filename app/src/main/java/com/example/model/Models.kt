package com.example.model

enum class LastSeenPrivacy(val title: String) {
    EVERYONE("Everyone"),
    MY_CONTACTS("My Contacts"),
    NOBODY("Nobody")
}

data class UserProfile(
    val fa97Code: String,
    val name: String,
    val about: String = "Hey there! I am using FA97 Chat.",
    val avatarEmoji: String = "👤",
    val backupKey: String,
    val recoveryEmail: String = "",
    val lastSeenPrivacy: LastSeenPrivacy = LastSeenPrivacy.MY_CONTACTS,
    val isOnline: Boolean = true,
    val readReceipts: Boolean = true,
    val isRegistered: Boolean = false
)

data class Contact(
    val fa97Code: String,
    val name: String,
    val about: String = "Available",
    val avatarEmoji: String = "😊",
    val isBlocked: Boolean = false,
    val isOnline: Boolean = true,
    val lastSeenText: String = "online",
    val isFriend: Boolean = true
)

enum class MessageType {
    TEXT, PHOTO, VOICE, LOCATION, DOCUMENT
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

data class Message(
    val id: String,
    val chatId: String,
    val senderCode: String,
    val senderName: String,
    val type: MessageType = MessageType.TEXT,
    val text: String = "",
    val mediaDurationSeconds: Int = 0,
    val fileName: String? = null,
    val fileSize: String? = null,
    val locationTitle: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT,
    val isStarred: Boolean = false,
    val isDeletedForEveryone: Boolean = false,
    val isEdited: Boolean = false,
    val replyToId: String? = null,
    val replyToSender: String? = null,
    val replyToText: String? = null,
    val reactions: Map<String, String> = emptyMap() // UserCode -> Emoji
)

data class Chat(
    val id: String,
    val isGroup: Boolean = false,
    val title: String,
    val avatarEmoji: String = "💬",
    val participantCodes: List<String>,
    val adminCodes: List<String> = emptyList(),
    val groupDescription: String = "",
    val lastMessageText: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val disappearingDurationHours: Int = 0, // 0 = off
    val wallpaperColorHex: String? = null
)

enum class StatusType {
    TEXT, PHOTO
}

data class StatusStory(
    val id: String,
    val userCode: String,
    val userName: String,
    val userAvatarEmoji: String,
    val type: StatusType = StatusType.TEXT,
    val textContent: String = "",
    val backgroundColorHex: String = "#00A884",
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false
)

enum class CallStatus {
    INCOMING, OUTGOING, MISSED
}

data class CallRecord(
    val id: String,
    val contactCode: String,
    val contactName: String,
    val contactAvatarEmoji: String,
    val isVideo: Boolean,
    val status: CallStatus,
    val timestamp: Long,
    val durationText: String = "0:45"
)
