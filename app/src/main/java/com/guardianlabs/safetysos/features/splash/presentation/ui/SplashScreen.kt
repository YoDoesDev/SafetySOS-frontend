package com.guardianlabs.safetysos.features.splash.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.guardianlabs.safetysos.features.splash.presentation.viewmodels.SplashViewModel
import com.guardianlabs.safetysos.features.splash.presentation.navigation.SplashDestination
import androidx.compose.runtime.LaunchedEffect
import android.app.Activity
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.guardianlabs.safetysos.features.auth.presentation.AuthActivity

@Composable
fun SplashScreen() {
    
    val viewModel: SplashViewModel = viewModel()
    val destination by viewModel.destination.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(destination) {

    when (destination) {

        SplashDestination.AUTH -> {
            context.startActivity(
    Intent(
        context,
        AuthActivity::class.java
    )
)

(context as? Activity)?.finish()
        }

        SplashDestination.HOME -> {
            // TODO:
            // Navigate to Home
        }

        null -> Unit
    }
}


    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "SafetySOS",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Stay Protected. Stay Connected.",
                color = colors.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(
                color = colors.primary
            )
        }
    }
}