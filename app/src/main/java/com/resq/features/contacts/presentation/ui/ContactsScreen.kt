package com.resq.features.contacts.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.resq.features.contacts.presentation.ui.components.*
import com.resq.features.contacts.presentation.viewmodels.ContactsView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: ContactsView = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val isAddDialogVisible by viewModel.isAddDialogVisible.collectAsState()
    val dialogError by viewModel.dialogError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val sessionExpiredMsg = "Session expired. Reopen the app."

    // Intercept session expired error: close dialog & show Snackbar
    LaunchedEffect(dialogError) {
        if (dialogError == sessionExpiredMsg) {
            viewModel.dismissAddDialog()
            snackbarHostState.showSnackbar(
                message = sessionExpiredMsg,
                duration = SnackbarDuration.Short
            )
        }
    }

    if (isAddDialogVisible && dialogError != sessionExpiredMsg) {
        AddContactDialog(
            errorMessage = dialogError,
            onDismiss = { viewModel.dismissAddDialog() },
            onConfirm = { savedName, fullPhone, username ->
                viewModel.onAddContactSubmitted(savedName, fullPhone, "", username)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Emergency Contacts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFB00020),
                    contentColor = Color.White
                )
            }
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF121212))
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContactSearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.weight(1f)
                )

                AddContactButton(
                    onClick = viewModel::showAddDialog
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "No contacts saved yet." else "No contacts match search.",
                        color = Color(0xFFA0A0A0),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(contacts, key = { it.id }) { contact ->
                        ContactCard(
                            contact = contact,
                            onDeleteClick = viewModel::deleteContact
                        )
                    }
                }
            }
        }
    }
}
