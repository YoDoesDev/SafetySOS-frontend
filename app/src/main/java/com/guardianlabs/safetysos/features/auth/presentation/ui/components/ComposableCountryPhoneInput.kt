package com.guardianlabs.safetysos.features.auth.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class CountryCode(
    val name: String,
    val code: String,
    val flag: String
) {
    val displayLabel: String get() = "$flag $code"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableCountryPhoneInput(
    onPhoneNumberChanged: (fullPhoneNumber: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val countryList = remember {
        listOf(
            CountryCode("India", "+91", "🇮🇳"),
            CountryCode("United States", "+1", "🇺🇸"),
            CountryCode("United Kingdom", "+44", "🇬🇧"),
            CountryCode("UAE", "+971", "🇦🇪"),
            CountryCode("Australia", "+61", "🇦🇺")
        )
    }

    var selectedCountry by remember { mutableStateOf(countryList[0]) }
    var rawPhone by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // --- Country Code Dropdown Menu ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.width(130.dp)
        ) {
            OutlinedTextField(
                value = selectedCountry.displayLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Code") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                countryList.forEach { country ->
                    DropdownMenuItem(
                        text = { Text("${country.flag} ${country.name} (${country.code})") },
                        onClick = {
                            selectedCountry = country
                            expanded = false
                            onPhoneNumberChanged("${selectedCountry.code}$rawPhone")
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // --- Raw Phone Number Text Field ---
        OutlinedTextField(
            value = rawPhone,
            onValueChange = { newValue ->
                rawPhone = newValue
                onPhoneNumberChanged("${selectedCountry.code}$newValue")
            },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }
}
