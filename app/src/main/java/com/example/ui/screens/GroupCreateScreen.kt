package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Contact
import com.example.ui.components.AvatarView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCreateScreen(
    contacts: List<Contact>,
    onBackClick: () -> Unit,
    onCreateGroup: (name: String, description: String, selectedCodes: List<String>, avatarEmoji: String) -> Unit
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    var groupName by remember { mutableStateOf("") }
    var groupDesc by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("⚡") }
    val selectedMemberCodes = remember { mutableStateListOf<String>() }

    val groupEmojis = listOf("⚡", "💻", "🛡️", "🔥", "🚀", "🎮", "☕", "🌟")

    Scaffold(
        containerColor = FaDarkBg,
        topBar = {
            TopAppBar(
                title = { Text("New Group", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FaDarkTextPrimary)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (groupName.isBlank()) {
                                Toast.makeText(context, "Please enter a group subject", Toast.LENGTH_SHORT).show()
                            } else {
                                onCreateGroup(groupName.trim(), groupDesc.trim(), selectedMemberCodes.toList(), selectedEmoji)
                            }
                        }
                    ) {
                        Text("Create", color = FaTealGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FaDarkSurface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1F2C34)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = selectedEmoji, fontSize = 30.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group Subject") },
                        placeholder = { Text("e.g. FA97 Core Team") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FaTealGreen,
                            focusedTextColor = FaDarkTextPrimary,
                            unfocusedTextColor = FaDarkTextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = groupDesc,
                    onValueChange = { groupDesc = it },
                    label = { Text("Group Description (Optional)") },
                    placeholder = { Text("e.g. Encrypted discussions & project plans") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FaTealGreen,
                        focusedTextColor = FaDarkTextPrimary,
                        unfocusedTextColor = FaDarkTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Choose Group Icon", color = FaDarkTextSecondary, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    groupEmojis.forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1F2C34))
                                .border(
                                    width = if (selectedEmoji == emoji) 2.dp else 0.dp,
                                    color = if (selectedEmoji == emoji) FaGreenAccent else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Add Members (${selectedMemberCodes.size} selected)",
                    color = FaDarkTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(contacts, key = { it.fa97Code }) { contact ->
                val isSelected = selectedMemberCodes.contains(contact.fa97Code)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) selectedMemberCodes.remove(contact.fa97Code)
                            else selectedMemberCodes.add(contact.fa97Code)
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarView(emoji = contact.avatarEmoji, size = 44.dp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(contact.name, color = FaDarkTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text(contact.fa97Code, color = FaDarkTextSecondary, fontSize = 12.sp)
                    }
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            if (checked) selectedMemberCodes.add(contact.fa97Code)
                            else selectedMemberCodes.remove(contact.fa97Code)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = FaTealGreen)
                    )
                }
            }
        }
    }
}
