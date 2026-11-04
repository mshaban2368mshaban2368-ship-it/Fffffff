package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Onboarding : Screen()
    object Main : Screen()
    data class ChatDetail(val chatId: String) : Screen()
    object GroupCreate : Screen()
    object MyQrCode : Screen()
    object AddFriend : Screen()
    data class ActiveCall(val contactCode: String, val contactName: String, val contactAvatarEmoji: String, val isVideo: Boolean, val isIncoming: Boolean) : Screen()
    data class StatusViewer(val statusId: String) : Screen()
    data class MediaViewer(val title: String, val subtitle: String) : Screen()
    object SettingsAccount : Screen()
    object SettingsPrivacy : Screen()
    object SettingsChat : Screen()
}

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    val currentUser = repository.currentUser
    val contacts = repository.contacts
    val chats = repository.chats
    val messages = repository.messages
    val statuses = repository.statuses
    val calls = repository.calls
    val blockedCodes = repository.blockedCodes
    val starredMessageIds = repository.starredMessageIds

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Main)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenHistory = mutableListOf<Screen>()

    private val _activeTab = MutableStateFlow(0) // 0: Chats, 1: Status, 2: Calls, 3: Settings
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active typing indicator map: chatId -> isTyping
    private val _typingStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val typingStatus: StateFlow<Map<String, Boolean>> = _typingStatus.asStateFlow()

    // Voice recording simulation
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()
    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()
    private var voiceRecordingJob: Job? = null

    // Calling state
    private val _callConnected = MutableStateFlow(false)
    val callConnected: StateFlow<Boolean> = _callConnected.asStateFlow()
    private val _callDurationSeconds = MutableStateFlow(0)
    val callDurationSeconds: StateFlow<Int> = _callDurationSeconds.asStateFlow()
    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()
    private val _isSpeaker = MutableStateFlow(true)
    val isSpeaker: StateFlow<Boolean> = _isSpeaker.asStateFlow()
    private val _isCameraOff = MutableStateFlow(false)
    val isCameraOff: StateFlow<Boolean> = _isCameraOff.asStateFlow()
    private val _isFrontCamera = MutableStateFlow(true)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()
    private var callTimerJob: Job? = null

    // Wallpaper preference
    private val _chatWallpaper = MutableStateFlow("doodle") // doodle, dark, emerald
    val chatWallpaper: StateFlow<String> = _chatWallpaper.asStateFlow()

    init {
        if (!currentUser.value.isRegistered) {
            _currentScreen.value = Screen.Onboarding
        }
    }

    fun navigateTo(screen: Screen) {
        _screenHistory.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (_screenHistory.isNotEmpty()) {
            _currentScreen.value = _screenHistory.removeAt(_screenHistory.size - 1)
            return true
        }
        if (_currentScreen.value !is Screen.Main) {
            _currentScreen.value = Screen.Main
            return true
        }
        return false
    }

    fun setActiveTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun completeOnboarding(name: String, about: String, avatarEmoji: String) {
        repository.registerNewUser(name, about, avatarEmoji)
        _screenHistory.clear()
        _currentScreen.value = Screen.Main
    }

    fun restoreAccount(codeOrKey: String): Boolean {
        val success = repository.restoreAccount(codeOrKey)
        if (success) {
            _screenHistory.clear()
            _currentScreen.value = Screen.Main
        }
        return success
    }

    fun addFriend(code: String, nickname: String): Pair<Boolean, String> {
        return repository.addContactByFa97Code(code, nickname)
    }

    fun createGroup(name: String, description: String, selectedCodes: List<String>, avatarEmoji: String) {
        repository.createGroup(name, description, selectedCodes, avatarEmoji)
        navigateBack()
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
        repository.sendMessage(chatId, text, type, mediaDurationSeconds, fileName, fileSize, locationTitle, replyTo)

        // Trigger smart automated reply after a realistic pause
        viewModelScope.launch {
            delay(1000)
            _typingStatus.value = _typingStatus.value + (chatId to true)
            delay(1500)
            _typingStatus.value = _typingStatus.value - chatId

            val reply = when {
                type == MessageType.VOICE -> "Listened to your voice note! Loud and clear. 🎧"
                type == MessageType.LOCATION -> "Got the coordinates! See you there shortly. 📍"
                type == MessageType.DOCUMENT -> "Downloaded the file securely. Reviewing it now! 📄"
                text.contains("hello", ignoreCase = true) || text.contains("hey", ignoreCase = true) ->
                    "Hey! Hope you're having an awesome day testing FA97 Chat!"
                text.contains("how are you", ignoreCase = true) ->
                    "Doing great! All messages are encrypted with 256-bit keys."
                text.contains("code", ignoreCase = true) ->
                    "My FA97 code is always permanent and private! 🛡️"
                else -> "Got your message: \"${text.take(25)}\"! FA97 Chat is super responsive. ⚡"
            }
            repository.simulateIncomingReply(chatId, reply)
        }
    }

    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _recordingDurationSeconds.value = 0
        voiceRecordingJob?.cancel()
        voiceRecordingJob = viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(1000)
                _recordingDurationSeconds.value += 1
            }
        }
    }

    fun finishVoiceRecording(chatId: String) {
        val duration = _recordingDurationSeconds.value
        _isRecordingVoice.value = false
        voiceRecordingJob?.cancel()
        if (duration > 0) {
            sendMessage(
                chatId = chatId,
                text = "Voice message",
                type = MessageType.VOICE,
                mediaDurationSeconds = duration
            )
        }
    }

    fun cancelVoiceRecording() {
        _isRecordingVoice.value = false
        voiceRecordingJob?.cancel()
        _recordingDurationSeconds.value = 0
    }

    fun addReaction(chatId: String, messageId: String, emoji: String) {
        repository.addReaction(chatId, messageId, emoji)
    }

    fun deleteMessage(chatId: String, messageId: String, forEveryone: Boolean) {
        repository.deleteMessage(chatId, messageId, forEveryone)
    }

    fun toggleStarMessage(chatId: String, messageId: String) {
        repository.toggleStarMessage(chatId, messageId)
    }

    fun editMessage(chatId: String, messageId: String, newText: String) {
        repository.editMessage(chatId, messageId, newText)
    }

    fun togglePinChat(chatId: String) {
        repository.togglePinChat(chatId)
    }

    fun toggleArchiveChat(chatId: String) {
        repository.toggleArchiveChat(chatId)
    }

    fun toggleMuteChat(chatId: String) {
        repository.toggleMuteChat(chatId)
    }

    fun clearChat(chatId: String) {
        repository.clearChat(chatId)
    }

    fun setDisappearingMessages(chatId: String, hours: Int) {
        repository.setDisappearingMessages(chatId, hours)
    }

    fun postStatus(textContent: String, colorHex: String) {
        repository.postStatus(textContent, colorHex)
    }

    fun markStatusViewed(statusId: String) {
        repository.markStatusViewed(statusId)
    }

    fun startCall(contactCode: String, isVideo: Boolean) {
        val contact = contacts.value.find { it.fa97Code == contactCode }
        _callConnected.value = false
        _callDurationSeconds.value = 0
        _isMuted.value = false
        _isSpeaker.value = true
        _isCameraOff.value = false
        _isFrontCamera.value = true

        navigateTo(
            Screen.ActiveCall(
                contactCode = contactCode,
                contactName = contact?.name ?: contactCode,
                contactAvatarEmoji = contact?.avatarEmoji ?: "👤",
                isVideo = isVideo,
                isIncoming = false
            )
        )

        // Simulate pickup after 2.5 seconds
        viewModelScope.launch {
            delay(2500)
            _callConnected.value = true
            startCallTimer()
        }
    }

    fun simulateIncomingCall(contactCode: String, isVideo: Boolean) {
        val contact = contacts.value.find { it.fa97Code == contactCode }
        _callConnected.value = false
        _callDurationSeconds.value = 0
        _isMuted.value = false
        _isSpeaker.value = true

        navigateTo(
            Screen.ActiveCall(
                contactCode = contactCode,
                contactName = contact?.name ?: "Contact",
                contactAvatarEmoji = contact?.avatarEmoji ?: "👤",
                isVideo = isVideo,
                isIncoming = true
            )
        )
    }

    fun acceptCall() {
        _callConnected.value = true
        startCallTimer()
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _callDurationSeconds.value += 1
            }
        }
    }

    fun endCall(contactCode: String, isVideo: Boolean) {
        val duration = _callDurationSeconds.value
        val durationFormatted = String.format("%02d:%02d", duration / 60, duration % 60)
        callTimerJob?.cancel()
        repository.logCall(
            contactCode = contactCode,
            isVideo = isVideo,
            status = if (_callConnected.value) CallStatus.OUTGOING else CallStatus.MISSED,
            duration = if (_callConnected.value) durationFormatted else "Cancelled"
        )
        _callConnected.value = false
        _callDurationSeconds.value = 0
        navigateBack()
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun toggleSpeaker() {
        _isSpeaker.value = !_isSpeaker.value
    }

    fun toggleCameraOff() {
        _isCameraOff.value = !_isCameraOff.value
    }

    fun toggleCameraSwitch() {
        _isFrontCamera.value = !_isFrontCamera.value
    }

    fun clearCallHistory() {
        repository.clearCallHistory()
    }

    fun toggleBlockContact(code: String) {
        repository.toggleBlockContact(code)
    }

    fun setChatWallpaper(theme: String) {
        _chatWallpaper.value = theme
    }

    fun updateUserProfile(name: String, about: String, avatarEmoji: String, email: String) {
        repository.updateUserProfile(name, about, avatarEmoji, email)
    }
}
