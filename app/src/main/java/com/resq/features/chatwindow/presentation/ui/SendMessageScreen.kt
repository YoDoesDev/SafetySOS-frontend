package com.resq.features.chatwindow.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.features.chatwindow.presentation.ui.components.ChatSearchBar
import com.resq.features.chatwindow.presentation.viewmodels.SendMessageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMessageScreen(
    navController: NavController,
    threadId: String? = null,
    viewModel: SendMessageViewModel = hiltViewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val existingThread by viewModel.existingThread.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    var search by remember { mutableStateOf("") }
    var draft by remember { mutableStateOf("") }
    var selectedContact by remember { mutableStateOf<ContactsEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(threadId) {
        if (!threadId.isNullOrBlank()) viewModel.loadExistingThread(threadId)
    }

    LaunchedEffect(statusMessage) {
        val message = statusMessage ?: return@LaunchedEffect
        if (message == "Message sent.") {
            snackbarHostState.showSnackbar(message)
            viewModel.clearStatus()
            navController.popBackStack()
        } else {
            snackbarHostState.showSnackbar(message)
            viewModel.clearStatus()
        }
    }

    val eligibleContacts = contacts.filter { contact ->
        contact.existsOnResQ && !contact.username.isNullOrBlank() &&
                (contact.savedName.contains(search, ignoreCase = true) ||
                        contact.username.orEmpty().contains(search, ignoreCase = true))
    }
    val recipientLabel = if (threadId.isNullOrBlank()) {
        selectedContact?.let { "${it.savedName} · @${it.username}" }
    } else {
        existingThread?.recipientName?.let { "$it · existing conversation" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Send a message", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(2.dp))

            if (threadId.isNullOrBlank()) {
                if (selectedContact == null) {
                    Text(
                        "To",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    ChatSearchBar(
                        query = search,
                        onQueryChange = { search = it },
                        placeholder = "Search ResQ contacts"
                    )

                    if (eligibleContacts.isEmpty()) {
                        Text(
                            "No ResQ contacts found. Add a contact with a ResQ username first.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(0.65f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(count = eligibleContacts.size, key = { index -> eligibleContacts[index].id }) { index ->
                                val contact = eligibleContacts[index]
                                RecipientRow(contact = contact, onClick = { selectedContact = contact })
                            }
                        }
                    }
                } else {
                    RecipientPill(
                        label = recipientLabel.orEmpty(),
                        onChange = { selectedContact = null }
                    )
                }
            } else if (existingThread == null) {
                Text(
                    "Loading conversation…",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                RecipientPill(label = recipientLabel.orEmpty(), onChange = null)
            }

            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                placeholder = { Text("Write your message…") },
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Button(
                onClick = {
                    if (threadId.isNullOrBlank()) viewModel.sendDraft(selectedContact, draft)
                    else viewModel.sendToExistingThread(threadId, draft)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSending && draft.isNotBlank() &&
                        (if (threadId.isNullOrBlank()) selectedContact != null else existingThread != null),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Text(if (isSending) "Sending…" else "Send")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RecipientRow(contact: ContactsEntity, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.padding(end = 12.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column {
                Text(contact.savedName, fontWeight = FontWeight.SemiBold)
                Text(
                    "@${contact.username}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun RecipientPill(label: String, onChange: (() -> Unit)?) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium
            )
            if (onChange != null) {
                Text(
                    text = "Change",
                    modifier = Modifier.clickable(onClick = onChange),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
