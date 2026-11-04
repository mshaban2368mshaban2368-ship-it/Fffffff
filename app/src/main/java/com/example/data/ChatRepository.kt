package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ChatRepository {

    private val _currentUser = MutableStateFlow(
        UserProfile(
            fa97Code = "FA97-8K3D9Q",
            name = "Alex Morgan",
            about = "Privacy first with FA97 Chat 🛡️",
            avatarEmoji = "😎",
            backupKey = "FA97-SEC-8491-3042",
            recoveryEmail = "alex.morgan@fa97.net",
            isRegistered = true
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    private val _statuses = MutableStateFlow<List<StatusStory>>(emptyList())
    val statuses: StateFlow<List<StatusStory>> = _statuses.asStateFlow()

    private val _calls = MutableStateFlow<List<CallRecord>>(emptyList())
    val calls: StateFlow<List<CallRecord>> = _calls.asStateFlow()

    private val _blockedCodes = MutableStateFlow<Set<String>>(emptySet())
    val blockedCodes: StateFlow<Set<String>> = _blockedCodes.asStateFlow()

    private val _starredMessageIds = MutableStateFlow<Set<String>>(emptySet())
    val starredMessageIds: StateFlow<Set<String>> = _starredMessageIds.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val aminaCode = "FA97-4N9X2W"
        val tariqCode = "FA97-7M2P1Z"
        val sarahCode = "FA97-9V5K8J"
        val davidCode = "FA97-3R8H4L"

        val initialContacts = listOf(
            Contact(
                fa97Code = aminaCode,
                name = "Amina Khalid",
                about = "Coding with FA97 Chat 🚀",
                avatarEmoji = "👩🏻‍💻",
                isOnline = true,
                lastSeenText = "online"
            ),
            Contact(
                fa97Code = tariqCode,
                name = "Tariq Al-Mansoor",
                about = "At work, urgent calls only ☕",
                avatarEmoji = "👨🏽‍💼",
                isOnline = false,
                lastSeenText = "last seen today at 10:45 AM"
            ),
            Contact(
                fa97Code = sarahCode,
                name = "Sarah Connor",
                about = "Exploring new horizons 🌌",
                avatarEmoji = "👩🏼‍🚀",
                isOnline = true,
                lastSeenText = "online"
            ),
            Contact(
                fa97Code = davidCode,
                name = "David Miller",
                about = "Available",
                avatarEmoji = "🧔🏼",
                isOnline = false,
                lastSeenText = "last seen yesterday"
            )
        )
        _contacts.value = initialContacts

        val chatAmina = Chat(
            id = "chat_amina",
            isGroup = false,
            title = "Amina Khalid",
            avatarEmoji = "👩🏻‍💻",
            participantCodes = listOf(_currentUser.value.fa97Code, aminaCode),
            lastMessageText = "The end-to-end encryption test passed smoothly! 🔐",
            lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 5,
            unreadCount = 2,
            isPinned = true
        )

        val chatTariq = Chat(
            id = "chat_tariq",
            isGroup = false,
            title = "Tariq Al-Mansoor",
            avatarEmoji = "👨🏽‍💼",
            participantCodes = listOf(_currentUser.value.fa97Code, tariqCode),
            lastMessageText = "🎙️ Voice message (0:18)",
            lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 45,
            unreadCount = 0
        )

        val chatGroup = Chat(
            id = "chat_group_fa97",
            isGroup = true,
            title = "FA97 Core Engineers",
            avatarEmoji = "⚡",
            participantCodes = listOf(_currentUser.value.fa97Code, aminaCode, tariqCode, sarahCode),
            adminCodes = listOf(_currentUser.value.fa97Code, aminaCode),
            groupDescription = "Privacy, P2P WebRTC, and FA97 Codes development group.",
            lastMessageText = "Sarah: Check out the new dark mode UI! ✨",
            lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 120,
            unreadCount = 0
        )

        val chatSarah = Chat(
            id = "chat_sarah",
            isGroup = false,
            title = "Sarah Connor",
            avatarEmoji = "👩🏼‍🚀",
            participantCodes = listOf(_currentUser.value.fa97Code, sarahCode),
            lastMessageText = "Sent my FA97 code to the team.",
            lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
            unreadCount = 0
        )

        _chats.value = listOf(chatAmina, chatTariq, chatGroup, chatSarah)

        // Seed messages for Amina
        val msgsAmina = listOf(
            Message(
                id = "m1",
                chatId = "chat_amina",
                senderCode = aminaCode,
                senderName = "Amina Khalid",
                type = MessageType.TEXT,
                text = "Hey Alex! Just added you with your FA97 code `FA97-8K3D9Q`! Loving no phone numbers needed.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                status = MessageStatus.READ
            ),
            Message(
                id = "m2",
                chatId = "chat_amina",
                senderCode = _currentUser.value.fa97Code,
                senderName = "Alex Morgan",
                type = MessageType.TEXT,
                text = "Awesome! It is super fast and clean. No spam, no SIM swapping risks.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 55,
                status = MessageStatus.READ,
                reactions = mapOf(aminaCode to "❤️")
            ),
            Message(
                id = "m3",
                chatId = "chat_amina",
                senderCode = aminaCode,
                senderName = "Amina Khalid",
                type = MessageType.LOCATION,
                text = "📍 Meeting spot for our sync",
                locationTitle = "Innovation Hub, Tech Park, 4th Floor",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                status = MessageStatus.READ
            ),
            Message(
                id = "m4",
                chatId = "chat_amina",
                senderCode = aminaCode,
                senderName = "Amina Khalid",
                type = MessageType.TEXT,
                text = "The end-to-end encryption test passed smoothly! 🔐",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                status = MessageStatus.DELIVERED
            )
        )

        // Seed messages for Tariq
        val msgsTariq = listOf(
            Message(
                id = "m_t1",
                chatId = "chat_tariq",
                senderCode = _currentUser.value.fa97Code,
                senderName = "Alex Morgan",
                type = MessageType.TEXT,
                text = "Hi Tariq, did you get the architecture design document?",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 180,
                status = MessageStatus.READ
            ),
            Message(
                id = "m_t2",
                chatId = "chat_tariq",
                senderCode = tariqCode,
                senderName = "Tariq Al-Mansoor",
                type = MessageType.DOCUMENT,
                text = "FA97_Security_Architecture.pdf",
                fileName = "FA97_Security_Architecture.pdf",
                fileSize = "3.8 MB",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 90,
                status = MessageStatus.READ
            ),
            Message(
                id = "m_t3",
                chatId = "chat_tariq",
                senderCode = tariqCode,
                senderName = "Tariq Al-Mansoor",
                type = MessageType.VOICE,
                text = "Voice note",
                mediaDurationSeconds = 18,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                status = MessageStatus.READ
            )
        )

        // Seed messages for Group
        val msgsGroup = listOf(
            Message(
                id = "m_g1",
                chatId = "chat_group_fa97",
                senderCode = aminaCode,
                senderName = "Amina Khalid",
                type = MessageType.TEXT,
                text = "Welcome everyone to FA97 Core Engineers! All messages in this room are E2E encrypted.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 300,
                status = MessageStatus.READ
            ),
            Message(
                id = "m_g2",
                chatId = "chat_group_fa97",
                senderCode = sarahCode,
                senderName = "Sarah Connor",
                type = MessageType.TEXT,
                text = "Check out the new dark mode UI! ✨",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                status = MessageStatus.READ
            )
        )

        _messages.value = mapOf(
            "chat_amina" to msgsAmina,
            "chat_tariq" to msgsTariq,
            "chat_group_fa97" to msgsGroup
        )

        // Seed Status Stories
        val initialStatuses = listOf(
            StatusStory(
                id = "st_amina",
                userCode = aminaCode,
                userName = "Amina Khalid",
                userAvatarEmoji = "👩🏻‍💻",
                type = StatusType.TEXT,
                textContent = "Excited about launching FA97 Chat! Total privacy with zero phone numbers required. 🛡️✨",
                backgroundColorHex = "#005C4B",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 90,
                isViewed = false
            ),
            StatusStory(
                id = "st_tariq",
                userCode = tariqCode,
                userName = "Tariq Al-Mansoor",
                userAvatarEmoji = "👨🏽‍💼",
                type = StatusType.TEXT,
                textContent = "Sunday coffee brewing & code reviewing ☕📖",
                backgroundColorHex = "#6B38FB",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 240,
                isViewed = false
            ),
            StatusStory(
                id = "st_sarah",
                userCode = sarahCode,
                userName = "Sarah Connor",
                userAvatarEmoji = "👩🏼‍🚀",
                type = StatusType.TEXT,
                textContent = "WebRTC peer connection established over signaling! 📞🎧",
                backgroundColorHex = "#C2185B",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 400,
                isViewed = true
            )
        )
        _statuses.value = initialStatuses

        // Seed Calls
        val initialCalls = listOf(
            CallRecord(
                id = "call_1",
                contactCode = tariqCode,
                contactName = "Tariq Al-Mansoor",
                contactAvatarEmoji = "👨🏽‍💼",
                isVideo = false,
                status = CallStatus.INCOMING,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                durationText = "2:14"
            ),
            CallRecord(
                id = "call_2",
                contactCode = aminaCode,
                contactName = "Amina Khalid",
                contactAvatarEmoji = "👩🏻‍💻",
                isVideo = true,
                status = CallStatus.MISSED,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 12,
                durationText = "Missed"
            ),
            CallRecord(
                id = "call_3",
                contactCode = sarahCode,
                contactName = "Sarah Connor",
                contactAvatarEmoji = "👩🏼‍🚀",
                isVideo = false,
                status = CallStatus.OUTGOING,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 36,
                durationText = "14:50"
            )
        )
        _calls.value = initialCalls
    }

    fun updateUserProfile(name: String, about: String, avatarEmoji: String, email: String) {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            about = about,
            avatarEmoji = avatarEmoji,
            recoveryEmail = email,
            isRegistered = true
        )
    }

    fun generateNewFa97Code(): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        val randomString = (1..6)
            .map { allowedChars.random() }
            .joinToString("")
        return "FA97-$randomString"
    }

    fun generateBackupKey(): String {
        val p1 = (1000..9999).random()
        val p2 = (1000..9999).random()
        return "FA97-SEC-$p1-$p2"
    }

    fun registerNewUser(name: String, about: String, avatarEmoji: String): UserProfile {
        val newCode = generateNewFa97Code()
        val backupKey = generateBackupKey()
        val profile = UserProfile(
            fa97Code = newCode,
            name = name.ifBlank { "FA97 User" },
            about = about.ifBlank { "Hey there! I am using FA97 Chat." },
            avatarEmoji = avatarEmoji,
            backupKey = backupKey,
            isRegistered = true
        )
        _currentUser.value = profile
        return profile
    }

    fun restoreAccount(backupKeyOrCode: String): Boolean {
        if (backupKeyOrCode.isNotBlank()) {
            _currentUser.value = _currentUser.value.copy(
                fa97Code = if (backupKeyOrCode.startsWith("FA97-")) backupKeyOrCode.trim() else "FA97-${backupKeyOrCode.take(6).uppercase()}",
                isRegistered = true
            )
            return true
        }
        return false
    }

    fun addContactByFa97Code(code: String, nickname: String): Pair<Boolean, String> {
        val cleanCode = code.trim().uppercase()
        if (cleanCode == _currentUser.value.fa97Code) {
            return Pair(false, "You cannot add your own FA97 code!")
        }
        if (_contacts.value.any { it.fa97Code.equals(cleanCode, ignoreCase = true) }) {
            return Pair(false, "Contact already exists in your contacts list.")
        }
        val emojis = listOf("😎", "🥳", "🤠", "🤖", "🦊", "🌟", "🔥", "🚀", "💎")
        val randomEmoji = emojis.random()
        val newContact = Contact(
            fa97Code = cleanCode,
            name = nickname.ifBlank { "Friend ($cleanCode)" },
            about = "Connected via FA97 Code",
            avatarEmoji = randomEmoji,
            isOnline = true,
            lastSeenText = "online"
        )
        _contacts.value = _contacts.value + newContact

        // Also create a chat for them
        val newChat = Chat(
            id = "chat_${UUID.randomUUID()}",
            isGroup = false,
            title = newContact.name,
            avatarEmoji = newContact.avatarEmoji,
            participantCodes = listOf(_currentUser.value.fa97Code, cleanCode),
            lastMessageText = "Added contact via FA97 Code",
            lastMessageTime = System.currentTimeMillis()
        )
        _chats.value = listOf(newChat) + _chats.value
        return Pair(true, "Added ${newContact.name} successfully!")
    }

    fun createGroup(name: String, description: String, selectedMemberCodes: List<String>, avatarEmoji: String) {
        val newGroupId = "grp_${UUID.randomUUID()}"
        val newGroup = Chat(
            id = newGroupId,
            isGroup = true,
            title = name,
            avatarEmoji = avatarEmoji,
            participantCodes = listOf(_currentUser.value.fa97Code) + selectedMemberCodes,
            adminCodes = listOf(_currentUser.value.fa97Code),
            groupDescription = description,
            lastMessageText = "Group created: $name",
            lastMessageTime = System.currentTimeMillis()
        )
        _chats.value = listOf(newGroup) + _chats.value

        val welcomeMsg = Message(
            id = UUID.randomUUID().toString(),
            chatId = newGroupId,
            senderCode = _currentUser.value.fa97Code,
            senderName = _currentUser.value.name,
            type = MessageType.TEXT,
            text = "Welcome to $name! Messages in this group are end-to-end encrypted.",
            status = MessageStatus.SENT
        )
        val currentMap = _messages.value.toMutableMap()
        currentMap[newGroupId] = listOf(welcomeMsg)
        _messages.value = currentMap
    }

    fun sendMessage(
        chatId: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        mediaDurationSeconds: Int = 0,
        fileName: String? = null,
        fileSize: String? = null,
        locationTitle: String? = null,
        replyTo: Message? = null
    ) {
        val user = _currentUser.value
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderCode = user.fa97Code,
            senderName = user.name,
            type = type,
            text = text,
            mediaDurationSeconds = mediaDurationSeconds,
            fileName = fileName,
            fileSize = fileSize,
            locationTitle = locationTitle,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT,
            replyToId = replyTo?.id,
            replyToSender = replyTo?.senderName,
            replyToText = replyTo?.text
        )

        val currentMap = _messages.value.toMutableMap()
        val list = currentMap[chatId]?.toMutableList() ?: mutableListOf()
        list.add(newMsg)
        currentMap[chatId] = list
        _messages.value = currentMap

        // Update chat snippet
        val preview = when (type) {
            MessageType.TEXT -> text
            MessageType.PHOTO -> "📷 Photo"
            MessageType.VOICE -> "🎙️ Voice message (${mediaDurationSeconds}s)"
            MessageType.LOCATION -> "📍 Location: $locationTitle"
            MessageType.DOCUMENT -> "📄 Document: $fileName"
        }

        _chats.value = _chats.value.map { chat ->
            if (chat.id == chatId) {
                chat.copy(
                    lastMessageText = preview,
                    lastMessageTime = System.currentTimeMillis()
                )
            } else chat
        }
    }

    fun simulateIncomingReply(chatId: String, replyText: String) {
        val chat = _chats.value.find { it.id == chatId } ?: return
        val partnerCode = chat.participantCodes.firstOrNull { it != _currentUser.value.fa97Code } ?: "UNKNOWN"
        val partner = _contacts.value.find { it.fa97Code == partnerCode }

        val replyMsg = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderCode = partnerCode,
            senderName = partner?.name ?: chat.title,
            type = MessageType.TEXT,
            text = replyText,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.DELIVERED
        )

        val currentMap = _messages.value.toMutableMap()
        val list = currentMap[chatId]?.toMutableList() ?: mutableListOf()
        list.add(replyMsg)
        currentMap[chatId] = list
        _messages.value = currentMap

        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) {
                c.copy(
                    lastMessageText = replyText,
                    lastMessageTime = System.currentTimeMillis()
                )
            } else c
        }
    }

    fun addReaction(chatId: String, messageId: String, emoji: String) {
        val userCode = _currentUser.value.fa97Code
        val currentMap = _messages.value.toMutableMap()
        val list = currentMap[chatId]?.map { msg ->
            if (msg.id == messageId) {
                val newReactions = msg.reactions.toMutableMap()
                if (newReactions[userCode] == emoji) {
                    newReactions.remove(userCode)
                } else {
                    newReactions[userCode] = emoji
                }
                msg.copy(reactions = newReactions)
            } else msg
        }
        if (list != null) {
            currentMap[chatId] = list
            _messages.value = currentMap
        }
    }

    fun deleteMessage(chatId: String, messageId: String, forEveryone: Boolean) {
        val currentMap = _messages.value.toMutableMap()
        val list = currentMap[chatId]?.mapNotNull { msg ->
            if (msg.id == messageId) {
                if (forEveryone) {
                    msg.copy(
                        isDeletedForEveryone = true,
                        text = "🚫 This message was deleted."
                    )
                } else {
                    null // delete for me
                }
            } else msg
        }
        if (list != null) {
            currentMap[chatId] = list
            _messages.value = currentMap
        }
    }

    fun toggleStarMessage(chatId: String, messageId: String) {
        val currentMap = _messages.value.toMutableMap()
        val list = currentMap[chatId]?.map { msg ->
            if (msg.id == messageId) {
                val toggled = !msg.isStarred
                if (toggled) {
                    _starredMessageIds.value = _starredMessageIds.value + messageId
                } else {
                    _starredMessageIds.value = _starredMessageIds.value - messageId
                }
                msg.copy(isStarred = toggled)
            } else msg
        }
        if (list != null) {
            currentMap[chatId] = list
            _messages.value = currentMap
        }
    }

    fun editMessage(chatId: String, messageId: String, newText: String) {
        val currentMap = _messages.value.toMutableMap()
        val list = currentMap[chatId]?.map { msg ->
            if (msg.id == messageId) {
                msg.copy(text = newText, isEdited = true)
            } else msg
        }
        if (list != null) {
            currentMap[chatId] = list
            _messages.value = currentMap
        }
    }

    fun togglePinChat(chatId: String) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(isPinned = !it.isPinned) else it
        }
    }

    fun toggleArchiveChat(chatId: String) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(isArchived = !it.isArchived) else it
        }
    }

    fun toggleMuteChat(chatId: String) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(isMuted = !it.isMuted) else it
        }
    }

    fun clearChat(chatId: String) {
        val currentMap = _messages.value.toMutableMap()
        currentMap[chatId] = emptyList()
        _messages.value = currentMap
    }

    fun setDisappearingMessages(chatId: String, hours: Int) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(disappearingDurationHours = hours) else it
        }
    }

    fun postStatus(textContent: String, colorHex: String) {
        val user = _currentUser.value
        val newStatus = StatusStory(
            id = UUID.randomUUID().toString(),
            userCode = user.fa97Code,
            userName = "My Status",
            userAvatarEmoji = user.avatarEmoji,
            type = StatusType.TEXT,
            textContent = textContent,
            backgroundColorHex = colorHex,
            timestamp = System.currentTimeMillis(),
            isViewed = true
        )
        _statuses.value = listOf(newStatus) + _statuses.value
    }

    fun markStatusViewed(statusId: String) {
        _statuses.value = _statuses.value.map {
            if (it.id == statusId) it.copy(isViewed = true) else it
        }
    }

    fun logCall(contactCode: String, isVideo: Boolean, status: CallStatus, duration: String) {
        val contact = _contacts.value.find { it.fa97Code == contactCode }
        val newCall = CallRecord(
            id = UUID.randomUUID().toString(),
            contactCode = contactCode,
            contactName = contact?.name ?: contactCode,
            contactAvatarEmoji = contact?.avatarEmoji ?: "👤",
            isVideo = isVideo,
            status = status,
            timestamp = System.currentTimeMillis(),
            durationText = duration
        )
        _calls.value = listOf(newCall) + _calls.value
    }

    fun clearCallHistory() {
        _calls.value = emptyList()
    }

    fun toggleBlockContact(code: String) {
        if (_blockedCodes.value.contains(code)) {
            _blockedCodes.value = _blockedCodes.value - code
        } else {
            _blockedCodes.value = _blockedCodes.value + code
        }
    }
}
