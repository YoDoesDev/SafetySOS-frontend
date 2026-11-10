package com.resq.features.presets.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.resq.features.presets.presentation.ui.components.PresetCard
import com.resq.features.presets.presentation.ui.components.PresetInputSection
import com.resq.features.presets.presentation.ui.components.SelectContactsDialog
import com.resq.features.presets.presentation.viewmodels.PresetsView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsScreen(
    navController: NavController,
    viewModel: PresetsView = hiltViewModel()
) {
    val allContacts by viewModel.allContacts.collectAsState()
    val allPresets by viewModel.allPresets.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Message Presets",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        // Trigger contact selection dialog when requested
        if (viewModel.showContactDialog) {
            SelectContactsDialog(
                allContacts = allContacts,
                currentlySelectedNumbers = viewModel.selectedContacts,
                onDismiss = viewModel::dismissContactDialog,
                onConfirm = { selectedList ->
                    viewModel.onContactsSelected(selectedList)
                }
            )
        }

        // Full Message View Dialog (triggered on card click)
        viewModel.viewingPresetMessage?.let { fullMessage ->
            Dialog(onDismissRequest = viewModel::dismissMessageDetails) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Emergency Message",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = fullMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = viewModel::dismissMessageDetails) {
                                Text("Close", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Creation Form Header Card
            item {
                PresetInputSection(
                    title = viewModel.title,
                    onTitleChange = viewModel::onTitleChange,
                    message = viewModel.message,
                    onMessageChange = viewModel::onMessageChange,
                    isLocationEnabled = viewModel.isLocationEnabled,
                    onLocationChanged = viewModel::onLocationChanged,
                    notifyAllContacts = viewModel.notifyAllContacts,
                    onNotifyAllChanged = viewModel::onNotifyAllChanged,
                    selectedContacts = viewModel.selectedContacts,
                    presetError = viewModel.presetError,
                    onSelectContactsClick = viewModel::onSelectContactsClick,
                    onSavePreset = viewModel::savePreset
                )
            }

            // 2. Saved Presets List Header
            item {
                Text(
                    text = "Saved Presets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            // 3. Dynamic Items List or Empty State
            if (allPresets.isEmpty()) {
                item {
                    Text(
                        text = "No saved presets yet. Create one above!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            } else {
                items(allPresets, key = { it.id }) { preset ->
                    PresetCard(
                        title = preset.title,
                        message = preset.message,
                        isDefault = preset.isDefault,
                        onCardClick = {
                            viewModel.openMessageDetails(preset.message)
                        },
                        onEditClick = { 
                            navController.navigate("preset_edit/${preset.id}") 
                        },
                        onSetDefaultClick = { viewModel.onSetDefaultClicked(preset.id) },
                        onDeleteClick = { viewModel.onDeletePresetClicked(preset.id) } // 👈 Delete action added
                    )
                }
            }
        }
    }
}
