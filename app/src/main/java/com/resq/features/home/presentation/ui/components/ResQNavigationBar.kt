package com.resq.features.home.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq.features.home.presentation.navigation.NavDestination

@Composable
fun ResQNavigationBar(
    currentDestination: NavDestination,
    onDestinationSelected: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF1E1E1E), // Elevated Floating Surface
            tonalElevation = 8.dp,
            shadowElevation = 16.dp,
            modifier = Modifier.height(64.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavDestination.values().forEach { destination ->
                    val isSelected = destination == currentDestination

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) Color(0xFFD32F2F).copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .clickable { onDestinationSelected(destination) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.label,
                                tint = if (isSelected) Color(0xFFD32F2F) else Color(0xFFA0A0A0),
                                modifier = Modifier.size(20.dp)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = destination.label,
                                    color = Color(0xFFD32F2F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
