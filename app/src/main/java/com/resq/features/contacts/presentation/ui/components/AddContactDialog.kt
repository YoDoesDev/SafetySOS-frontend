package com.resq.features.contacts.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

data class CountryCode(val code: String, val country: String)

val countryCodes = listOf(
    CountryCode("+91", "IN"),
    CountryCode("+1", "US"),
    CountryCode("+44", "UK"),
    CountryCode("+61", "AU"),
    CountryCode("+49", "DE")
)

@Composable
fun AddContactDialog(
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: (savedName: String, fullPhoneNumber: String, username: String?) -> Unit
) {
    var savedName by remember { mutableStateOf("") }
    var rawPhone by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf(countryCodes.first()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E1E1E),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Add Emergency Contact",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Saved Name Field
                OutlinedTextField(
                    value = savedName,
                    onValueChange = { savedName = it },
                    label = { Text("Saved Name (e.g., Mom)") },
                    singleLine = true,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Phone Input with Country Code Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Country Code Trigger Box
                    Box {
                        Box(
                            modifier = Modifier
                                .height(56.dp)
                                .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
                                .clickable { isDropdownExpanded = true }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = selectedCountryCode.code,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Country Code",
                                    tint = Color(0xFFA0A0A0)
                                )
                            }
                        }

                        // Styled Dropdown Menu
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .background(Color(0xFF2C2C2C))
                                .width(120.dp)
                        ) {
                            countryCodes.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${item.code} (${item.country})",
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    },
                                    onClick = {
                                        selectedCountryCode = item
                                        isDropdownExpanded = false
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Raw Phone Input
                    OutlinedTextField(
                        value = rawPhone,
                        onValueChange = { rawPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Username Field (Optional)
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("ResQ Username (Optional)") },
                    singleLine = true,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Inline Error Display
                if (!errorMessage.isNullOrBlank()) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = errorMessage,
        color = Color(0xFFD32F2F),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}


                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFFA0A0A0))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val fullPhone = selectedCountryCode.code + rawPhone.trim()
                            onConfirm(savedName, fullPhone, username)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFD32F2F),
    unfocusedBorderColor = Color(0xFF333333),
    focusedLabelColor = Color(0xFFD32F2F),
    unfocusedLabelColor = Color(0xFFA0A0A0),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
