package com.resq.features.presets.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PresetInputSection(
    title: String,
    onTitleChange: (String) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    isLocationEnabled: Boolean,
    onLocationChanged: (Boolean) -> Unit,
    notifyAllContacts: Boolean,
    selectedContacts: List<String>, // 👈 Added this parameter to fix the compile error
    presetError: String?,
    onNotifyAllChanged: (Boolean) -> Unit,
    onSelectContactsClick: () -> Unit,
    onSavePreset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Create Emergency Preset",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 1. Preset Title Input
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Preset Title (e.g., Medical Emergency)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. Preset Message Input
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                label = { Text("Emergency Message") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            if (presetError != null) {
                Text(
                    text = presetError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // 3. Live Location Sharing Options
            Text(
                text = "Live Location Sharing",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    RadioButton(
                        selected = isLocationEnabled,
                        onClick = { onLocationChanged(true) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Enable", style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    RadioButton(
                        selected = !isLocationEnabled,
                        onClick = { onLocationChanged(false) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Disable", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // 4. Contact Notification Scope Options
            Text(
                text = "Notify Contacts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    RadioButton(
                        selected = notifyAllContacts,
                        onClick = { onNotifyAllChanged(true) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("All Contacts", style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    RadioButton(
                        selected = !notifyAllContacts,
                        onClick = { onNotifyAllChanged(false) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Custom Choice", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Conditional Button for Custom Contact Selection with Count
            if (!notifyAllContacts) {
                OutlinedButton(
                    onClick = onSelectContactsClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val countText = if (selectedContacts.isNotEmpty()) " (${selectedContacts.size} selected)" else ""
                    Text("Select Trusted Contacts$countText")
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Save Preset Button
            Button(
                onClick = onSavePreset,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Preset")
            }
        }
    }
}
