package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FaGreenAccent
import com.example.ui.theme.FaTealGreen

@Composable
fun AvatarView(
    emoji: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    hasStory: Boolean = false,
    storyViewed: Boolean = false,
    isOnline: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val ringColor = when {
        hasStory && !storyViewed -> FaTealGreen
        hasStory && storyViewed -> Color.Gray
        else -> Color.Transparent
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Story ring if applicable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(if (hasStory) ringColor else Color.Transparent)
                .padding(if (hasStory) 2.5.dp else 0.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = (size.value * 0.48f).sp
            )
        }

        // Online status dot
        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF111B21))
                    .padding(1.5.dp)
                    .clip(CircleShape)
                    .background(FaGreenAccent)
            )
        }
    }
}
