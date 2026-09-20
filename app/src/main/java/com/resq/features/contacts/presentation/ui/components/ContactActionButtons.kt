package com.resq.features.contacts.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E1E1E),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Toggle Search",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun AddContactButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD32F2F),
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = modifier.height(48.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = "Add Contact",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Add Contact",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
