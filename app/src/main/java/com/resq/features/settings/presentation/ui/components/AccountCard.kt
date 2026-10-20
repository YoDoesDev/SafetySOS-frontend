package com.resq.features.settings.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon


@Composable
fun AccountCard(
    username: String,
    phoneNumber: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 1. "Logged in as:" Label
            Text(
                text = "Logged in as:",
                color = Color(0xFFD32F2F),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Icon + Right-Aligned User Info Row
            Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    // Clean Material Icon on the Left
    Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "User Profile Icon",
        tint = Color(0xFFD32F2F),
        modifier = Modifier.size(120.dp)
    )

    // User Info Stacked & Aligned to the Right
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = username,
            color = Color(0xFFD32F2F),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = phoneNumber,
            color = Color(0xFFD32F2F),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

        }
    }
}
