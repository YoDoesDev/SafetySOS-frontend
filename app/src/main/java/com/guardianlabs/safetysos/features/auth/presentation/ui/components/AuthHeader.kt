package com.guardianlabs.safetysos.features.auth.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthHeader(
    title: String,
    subtitle: String
) {
    val colors = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = colors.onBackground.copy(alpha = 0.7f)
        )
    }
}
