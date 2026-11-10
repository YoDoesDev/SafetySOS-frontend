package com.resq.features.presets.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq.core.data.local.database.entities.ContactsEntity

@Composable
fun SelectableContactCard(
    contact: ContactsEntity,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() },
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E1E1E)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp), // 👈 Tighter padding for a smaller, cleaner look
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = contact.savedName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            // Compact radio button so it takes up less vertical footprint
            RadioButton(
                selected = isSelected,
                onClick = onToggleSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFFD32F2F),
                    unselectedColor = Color(0xFF757575)
                ),
                modifier = Modifier.size(36.dp) // 👈 Tighter touch target sizing
            )
        }
    }
}
