package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun MainScreen(
    activeTab: Int,
    onTabSelected: (Int) -> Unit,
    currentUser: UserProfile,
    chats: List<Chat>,
    contacts: List<Contact>,
    statuses: List<StatusStory>,
    calls: List<CallRecord>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onChatClick: (String) -> Unit,
    onNewChatClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onMyQrClick: () -> Unit,
    onStatusClick: (String) -> Unit,
    onPostStatus: (text: String, colorHex: String) -> Unit,
    onStartCall: (contactCode: String, isVideo: Boolean) -> Unit,
    onSimulateIncomingCall: (contactCode: String, isVideo: Boolean) -> Unit,
    onClearCalls: () -> Unit,
    onTogglePin: (String) -> Unit,
    onToggleMute: (String) -> Unit,
    onToggleArchive: (String) -> Unit,
    onUpdateProfile: (name: String, about: String, emoji: String, email: String) -> Unit
) {
    val totalUnread = chats.sumOf { it.unreadCount }
    val hasUnviewedStatus = statuses.any { it.userCode != currentUser.fa97Code && !it.isViewed }

    Scaffold(
        containerColor = FaDarkBg,
        bottomBar = {
            NavigationBar(
                containerColor = FaDarkSurface,
                tonalElevation = 8.dp
            ) {
                // Chats Tab
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (totalUnread > 0) {
                                    Badge(containerColor = FaGreenAccent) {
                                        Text("$totalUnread", color = Color.Black)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Chats")
                        }
                    },
                    label = { Text("Chats") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FaTealGreen,
                        selectedTextColor = FaTealGreen,
                        indicatorColor = FaDarkOutgoingBubble.copy(alpha = 0.5f),
                        unselectedIconColor = FaDarkTextSecondary,
                        unselectedTextColor = FaDarkTextSecondary
                    )
                )

                // Status Tab
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (hasUnviewedStatus) {
                                    Badge(containerColor = FaGreenAccent)
                                }
                            }
                        ) {
                            Icon(Icons.Default.DonutLarge, contentDescription = "Status")
                        }
                    },
                    label = { Text("Status") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FaTealGreen,
                        selectedTextColor = FaTealGreen,
                        indicatorColor = FaDarkOutgoingBubble.copy(alpha = 0.5f),
                        unselectedIconColor = FaDarkTextSecondary,
                        unselectedTextColor = FaDarkTextSecondary
                    )
                )

                // Calls Tab
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = { Icon(Icons.Default.Call, contentDescription = "Calls") },
                    label = { Text("Calls") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FaTealGreen,
                        selectedTextColor = FaTealGreen,
                        indicatorColor = FaDarkOutgoingBubble.copy(alpha = 0.5f),
                        unselectedIconColor = FaDarkTextSecondary,
                        unselectedTextColor = FaDarkTextSecondary
                    )
                )

                // Settings Tab
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FaTealGreen,
                        selectedTextColor = FaTealGreen,
                        indicatorColor = FaDarkOutgoingBubble.copy(alpha = 0.5f),
                        unselectedIconColor = FaDarkTextSecondary,
                        unselectedTextColor = FaDarkTextSecondary
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (activeTab) {
                0 -> ChatListScreen(
                    chats = chats,
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    onChatClick = onChatClick,
                    onNewChatClick = onNewChatClick,
                    onCreateGroupClick = onCreateGroupClick,
                    onMyQrClick = onMyQrClick,
                    onTogglePin = onTogglePin,
                    onToggleMute = onToggleMute,
                    onToggleArchive = onToggleArchive
                )
                1 -> StatusScreen(
                    currentUser = currentUser,
                    statuses = statuses,
                    onStatusClick = onStatusClick,
                    onPostStatus = onPostStatus
                )
                2 -> CallListScreen(
                    calls = calls,
                    contacts = contacts,
                    onStartCall = onStartCall,
                    onSimulateIncomingCall = onSimulateIncomingCall,
                    onClearCalls = onClearCalls
                )
                3 -> SettingsScreen(
                    user = currentUser,
                    onMyQrClick = onMyQrClick,
                    onUpdateProfile = onUpdateProfile
                )
            }
        }
    }
}
