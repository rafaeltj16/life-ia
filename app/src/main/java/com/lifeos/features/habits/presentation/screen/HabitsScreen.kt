package com.lifeos.features.habits.presentation.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.common.LifeOSViewModel
import com.example.core.ui.ResponsiveScreenContainer
import com.lifeos.core.database.LifeOSDatabase
import com.lifeos.features.habits.data.repository.RoomHabitTrackerRepositoryImpl
import com.lifeos.features.habits.presentation.components.HabitTrackerComponent
import com.lifeos.features.habits.presentation.viewmodel.HabitTrackerViewModel

@Composable
fun HabitsScreen(
    viewModel: LifeOSViewModel,
    habitTrackerViewModel: HabitTrackerViewModel? = null
) {
    val context = LocalContext.current
    val effectiveTrackerViewModel: HabitTrackerViewModel = habitTrackerViewModel ?: run {
        val db = LifeOSDatabase.getInstance(context)
        val repo = remember(db) {
            RoomHabitTrackerRepositoryImpl(
                habitDao = db.habitDao(),
                habitLogDao = db.habitLogDao()
            )
        }
        viewModel(factory = HabitTrackerViewModel.Factory(repo))
    }

    val aiHabitRec by viewModel.aiHabitRecommendation.collectAsState()

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            // AI Habits Recommendation Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { viewModel.generateAIHabitsRecommendation() }
                    .background(Color(0xFFAB47BC).copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFAB47BC).copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFAB47BC),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Recomendar ritual de alto rendimento com IA",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFAB47BC)),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFFAB47BC),
                    modifier = Modifier.size(18.dp)
                )
            }

            if (aiHabitRec != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(Color(0xFFAB47BC).copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = aiHabitRec ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4A148C)
                    )
                }
            }

            // Room-backed Habit Tracker Component with definition dialog and completion logs
            HabitTrackerComponent(
                viewModel = effectiveTrackerViewModel
            )
        }
    }
}

@Composable
fun HabitsScreen(
    habitTrackerViewModel: HabitTrackerViewModel
) {
    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            HabitTrackerComponent(
                viewModel = habitTrackerViewModel
            )
        }
    }
}
