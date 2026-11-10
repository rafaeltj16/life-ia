package com.example.features.health.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.ui.*
import com.example.core.common.LifeOSViewModel

@Composable
fun HealthWorkoutScreen(viewModel: LifeOSViewModel) {
    val water by viewModel.waterDrankMl.collectAsState()

    ResponsiveScreenContainer {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Saúde e Treinos", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            }

            // Large Water card
            item {
                LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF29B6F6), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Consumo de Água", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Meta: 2500ml • Registrado: ${water}ml", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(progress = { (water.toFloat() / 2500f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape))
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.logWater(250) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("log_water_250")
                        ) {
                            Text("+250ml")
                        }
                        Button(
                            onClick = { viewModel.logWater(500) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("log_water_500")
                        ) {
                            Text("+500ml")
                        }
                    }
                }
            }

            // Workout Log list
            item {
                Text("Meus Treinos", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            item {
                WorkoutLogItem(title = "💪 Hipertrofia (Peito & Tríceps)", duration = "1h 15m", date = "Hoje • 07:00")
            }
            item {
                WorkoutLogItem(title = "🏃 Corrida ao Ar Livre (Cardio)", duration = "45 min", date = "Ontem • 18:30")
            }
        }
    }
}

@Composable
fun WorkoutLogItem(
    title: String,
    duration: String,
    date: String
) {
    LifeOSCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            LifeOSBadge(text = duration, color = MaterialTheme.colorScheme.primary)
        }
    }
}
