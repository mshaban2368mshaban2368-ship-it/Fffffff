package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(
    onBackClick: () -> Unit,
    onAddFriend: (code: String, nickname: String) -> Pair<Boolean, String>
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    var inputCode by remember { mutableStateOf("") }
    var inputNickname by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = FaDarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Add Friend with FA97 Code", color = FaDarkTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FaDarkTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FaDarkSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Enter FA97 Code",
                color = FaDarkTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Enter your friend's 6-character FA97 Code (e.g. FA97-8K3D9Q). No phone number or contact sync required.",
                color = FaDarkTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = FaDarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = {
                            inputCode = it.uppercase()
                            errorMessage = null
                        },
                        label = { Text("FA97 Code") },
                        placeholder = { Text("FA97-XXXXXX") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FaTealGreen,
                            unfocusedBorderColor = FaDarkDivider,
                            focusedTextColor = FaDarkTextPrimary,
                            unfocusedTextColor = FaDarkTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputNickname,
                        onValueChange = { inputNickname = it },
                        label = { Text("Friend's Nickname (Optional)") },
                        placeholder = { Text("e.g. Maya") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FaTealGreen,
                            unfocusedBorderColor = FaDarkDivider,
                            focusedTextColor = FaDarkTextPrimary,
                            unfocusedTextColor = FaDarkTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFFF5252),
                            fontSize = 12.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (inputCode.isBlank()) {
                                errorMessage = "Please enter an FA97 Code."
                            } else {
                                val (success, msg) = onAddFriend(inputCode, inputNickname)
                                if (success) {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    onBackClick()
                                } else {
                                    errorMessage = msg
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FaTealGreen),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Friend & Open Chat", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Or Scan QR Section
            Text(
                text = "Or Scan Friend's QR Code",
                color = FaDarkTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Point your camera at your friend's screen or choose a test code to instant-connect.",
                color = FaDarkTextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = FaDarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { isScanning = !isScanning },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2C34)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = FaGreenAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isScanning) "Hide QR Scanner" else "Open Camera QR Scanner", color = Color.White)
                    }

                    if (isScanning) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Color.Black, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = FaTealGreen, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Camera Viewfinder Active", color = Color.White, fontSize = 13.sp)
                                Text("Align friend's FA97 QR code in box", color = FaDarkTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Quick-test codes for instant friending:", color = FaDarkTextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    listOf(
                        "FA97-9V5K8J" to "Sarah Connor (AI Researcher)",
                        "FA97-3R8H4L" to "David Miller (Photographer)",
                        "FA97-2W7P4K" to "Kareem (Security Engineer)"
                    ).forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val (success, msg) = onAddFriend(code, name.substringBefore(" "))
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) onBackClick()
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "➕ Add $code ($name)", color = FaTealGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
