package com.resq.features.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.resq.features.home.presentation.navigation.NavDestination
import com.resq.features.home.presentation.ui.components.ResQNavigationBar
import com.resq.features.home.presentation.viewmodels.HomeView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeView = hiltViewModel()
) {
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ResQ",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
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
            // Screen content canvas ready for future components
        }
    }
}
