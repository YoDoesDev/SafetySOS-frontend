package com.guardianlabs.safetysos.features.splash.presentation.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guardianlabs.safetysos.R
import com.guardianlabs.safetysos.core.AppDestinations
import com.guardianlabs.safetysos.features.auth.presentation.AuthActivity
import com.guardianlabs.safetysos.features.home.presentation.HomeActivity
import com.guardianlabs.safetysos.features.splash.presentation.viewmodels.SplashView

@Composable
fun SplashScreen(
    viewModel: SplashView = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(destination) {
        val targetActivity = when (destination) {
            AppDestinations.AUTH -> AuthActivity::class.java
            AppDestinations.HOME -> HomeActivity::class.java
            else -> null
        }

        targetActivity?.let { activityClass ->
            val intent = Intent(context, activityClass).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            (context as? Activity)?.finish()
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ResQ Logo Image (Ensure resq_logo is added in res/drawable)
            Image(
                painter = painterResource(id = R.drawable.resq_logo),
                contentDescription = "ResQ Logo",
                modifier = Modifier
                    .size(220.dp)
            )

            /* 
            CircularProgressIndicator(
                color = colors.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
            */
        }
    }
}
