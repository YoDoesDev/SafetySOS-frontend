package com.resq.features.presets.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.resq.features.presets.presentation.ui.components.PresetInputSection
import com.resq.features.presets.presentation.ui.components.SelectContactsDialog
import com.resq.features.presets.presentation.viewmodels.PresetEditView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetEditScreen(
    navController: NavController,
    presetId: String?,
    viewModel: PresetEditView = hiltViewModel()
) {
    val allContacts by viewModel.allContacts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Preset",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Reusing your clean input section layout configured for Updating
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
                onSavePreset = {
                    viewModel.updatePreset {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
