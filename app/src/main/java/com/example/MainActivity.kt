package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.*
import com.example.ui.screens.*
import com.example.ui.theme.FA97ChatTheme
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FA97ChatTheme(darkTheme = true) {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val chats by viewModel.chats.collectAsStateWithLifecycle()
                val contacts by viewModel.contacts.collectAsStateWithLifecycle()
                val messagesMap by viewModel.messages.collectAsStateWithLifecycle()
                val statuses by viewModel.statuses.collectAsStateWithLifecycle()
                val calls by viewModel.calls.collectAsStateWithLifecycle()
                val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val typingStatus by viewModel.typingStatus.collectAsStateWithLifecycle()

                val isRecordingVoice by viewModel.isRecordingVoice.collectAsStateWithLifecycle()
                val recordingDurationSeconds by viewModel.recordingDurationSeconds.collectAsStateWithLifecycle()

                val callConnected by viewModel.callConnected.collectAsStateWithLifecycle()
                val callDurationSeconds by viewModel.callDurationSeconds.collectAsStateWithLifecycle()
                val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
                val isSpeaker by viewModel.isSpeaker.collectAsStateWithLifecycle()
                val isCameraOff by viewModel.isCameraOff.collectAsStateWithLifecycle()
                val isFrontCamera by viewModel.isFrontCamera.collectAsStateWithLifecycle()

                when (val screen = currentScreen) {
                    is Screen.Onboarding -> {
                        OnboardingScreen(
                            onComplete = { name, about, emoji ->
                                viewModel.completeOnboarding(name, about, emoji)
                            },
                            onRestore = { codeOrKey ->
                                viewModel.restoreAccount(codeOrKey)
                            }
                        )
                    }

                    is Screen.Main -> {
                        MainScreen(
                            activeTab = activeTab,
                            onTabSelected = { viewModel.setActiveTab(it) },
                            currentUser = currentUser,
                            chats = chats,
                            contacts = contacts,
                            statuses = statuses,
                            calls = calls,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onChatClick = { chatId ->
                                viewModel.navigateTo(Screen.ChatDetail(chatId))
                            },
                            onNewChatClick = {
                                viewModel.navigateTo(Screen.AddFriend)
                            },
                            onCreateGroupClick = {
                                viewModel.navigateTo(Screen.GroupCreate)
                            },
                            onMyQrClick = {
                                viewModel.navigateTo(Screen.MyQrCode)
                            },
                            onStatusClick = { statusId ->
                                viewModel.markStatusViewed(statusId)
                                viewModel.navigateTo(Screen.StatusViewer(statusId))
                            },
                            onPostStatus = { text, colorHex ->
                                viewModel.postStatus(text, colorHex)
                            },
                            onStartCall = { contactCode, isVideo ->
                                viewModel.startCall(contactCode, isVideo)
                            },
                            onSimulateIncomingCall = { contactCode, isVideo ->
                                viewModel.simulateIncomingCall(contactCode, isVideo)
                            },
                            onClearCalls = {
                                viewModel.clearCallHistory()
                            },
                            onTogglePin = { viewModel.togglePinChat(it) },
                            onToggleMute = { viewModel.toggleMuteChat(it) },
                            onToggleArchive = { viewModel.toggleArchiveChat(it) },
                            onUpdateProfile = { name, about, emoji, email ->
                                viewModel.updateUserProfile(name, about, emoji, email)
                            }
                        )
                    }

                    is Screen.ChatDetail -> {
                        val activeChat = chats.find { it.id == screen.chatId }
                        val activeMessages = messagesMap[screen.chatId] ?: emptyList()
                        val isTyping = typingStatus[screen.chatId] ?: false

                        if (activeChat != null) {
                            ChatDetailScreen(
                                chat = activeChat,
                                messages = activeMessages,
                                currentUserCode = currentUser.fa97Code,
                                isTyping = isTyping,
                                onBackClick = { viewModel.navigateBack() },
                                onSendMessage = { text, type, extra ->
                                    val fileName = extra["fileName"]
                                    val fileSize = extra["fileSize"]
                                    val locationTitle = extra["locationTitle"]
                                    viewModel.sendMessage(
                                        chatId = activeChat.id,
                                        text = text,
                                        type = type,
                                        fileName = fileName,
                                        fileSize = fileSize,
                                        locationTitle = locationTitle
                                    )
                                },
                                onStartVoiceRecording = { viewModel.startVoiceRecording() },
                                onFinishVoiceRecording = { viewModel.finishVoiceRecording(activeChat.id) },
                                onCancelVoiceRecording = { viewModel.cancelVoiceRecording() },
                                isRecordingVoice = isRecordingVoice,
                                recordingDurationSeconds = recordingDurationSeconds,
                                onStartCall = { contactCode, isVideo ->
                                    viewModel.startCall(contactCode, isVideo)
                                },
                                onAddReaction = { messageId, emoji ->
                                    viewModel.addReaction(activeChat.id, messageId, emoji)
                                },
                                onDeleteMessage = { messageId, forEveryone ->
                                    viewModel.deleteMessage(activeChat.id, messageId, forEveryone)
                                },
                                onToggleStar = { messageId ->
                                    viewModel.toggleStarMessage(activeChat.id, messageId)
                                },
                                onEditMessage = { messageId, newText ->
                                    viewModel.editMessage(activeChat.id, messageId, newText)
                                },
                                onSetDisappearingHours = { hours ->
                                    viewModel.setDisappearingMessages(activeChat.id, hours)
                                },
                                onClearChat = { viewModel.clearChat(activeChat.id) },
                                onToggleBlock = { contactCode ->
                                    viewModel.toggleBlockContact(contactCode)
                                },
                                onViewMedia = { title, subtitle ->
                                    viewModel.navigateTo(Screen.MediaViewer(title, subtitle))
                                }
                            )
                        } else {
                            viewModel.navigateBack()
                        }
                    }

                    is Screen.GroupCreate -> {
                        GroupCreateScreen(
                            contacts = contacts,
                            onBackClick = { viewModel.navigateBack() },
                            onCreateGroup = { name, desc, selectedCodes, emoji ->
                                viewModel.createGroup(name, desc, selectedCodes, emoji)
                            }
                        )
                    }

                    is Screen.MyQrCode -> {
                        MyQrCodeScreen(
                            user = currentUser,
                            onBackClick = { viewModel.navigateBack() }
                        )
                    }

                    is Screen.AddFriend -> {
                        AddFriendScreen(
                            onBackClick = { viewModel.navigateBack() },
                            onAddFriend = { code, nickname ->
                                viewModel.addFriend(code, nickname)
                            }
                        )
                    }

                    is Screen.ActiveCall -> {
                        ActiveCallScreen(
                            contactCode = screen.contactCode,
                            contactName = screen.contactName,
                            contactAvatarEmoji = screen.contactAvatarEmoji,
                            isVideo = screen.isVideo,
                            isIncoming = screen.isIncoming,
                            isConnected = callConnected,
                            callDurationSeconds = callDurationSeconds,
                            isMuted = isMuted,
                            isSpeaker = isSpeaker,
                            isCameraOff = isCameraOff,
                            isFrontCamera = isFrontCamera,
                            onAcceptCall = { viewModel.acceptCall() },
                            onEndCall = { viewModel.endCall(screen.contactCode, screen.isVideo) },
                            onToggleMute = { viewModel.toggleMute() },
                            onToggleSpeaker = { viewModel.toggleSpeaker() },
                            onToggleCameraOff = { viewModel.toggleCameraOff() },
                            onToggleCameraSwitch = { viewModel.toggleCameraSwitch() }
                        )
                    }

                    is Screen.StatusViewer -> {
                        val story = statuses.find { it.id == screen.statusId }
                        if (story != null) {
                            StatusViewerScreen(
                                story = story,
                                onClose = { viewModel.navigateBack() },
                                onReply = { replyText ->
                                    // Find chat with this contact or create one
                                    val existingChat = chats.find { it.participantCodes.contains(story.userCode) }
                                    if (existingChat != null) {
                                        viewModel.sendMessage(
                                            chatId = existingChat.id,
                                            text = "Replied to your status: \"${story.textContent.take(20)}...\"\n$replyText"
                                        )
                                    }
                                }
                            )
                        } else {
                            viewModel.navigateBack()
                        }
                    }

                    is Screen.MediaViewer -> {
                        MediaViewerScreen(
                            title = screen.title,
                            subtitle = screen.subtitle,
                            onBackClick = { viewModel.navigateBack() }
                        )
                    }

                    else -> {
                        viewModel.navigateBack()
                    }
                }
            }
        }
    }
}
