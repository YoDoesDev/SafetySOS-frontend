package com.resq.features.presets.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.resq.features.presets.presentation.viewmodels.PresetsView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsScreen(
    navController: NavController,
    viewModel: PresetsView = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Message Presets",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFFD32F2F)
                  )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF010101)
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF121212))
        ) {
            // Presets canvas
        }
    }
}
