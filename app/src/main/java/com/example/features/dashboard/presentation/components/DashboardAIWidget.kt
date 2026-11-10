package com.example.features.dashboard.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.ai.model.AIInsight
import com.example.features.dashboard.domain.model.DashboardAIState

// Extension property to map the requested 'description' field to the existing 'message' property in AIInsight
val AIInsight.description: String
    get() = message

@Composable
fun DashboardAIWidget(
    state: DashboardAIState
) {
    Card(
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("🤖 LifeAI")

            state.mission?.let {
                Text(it.title)
                Text(it.description)
            }

            state.insights.take(3).forEach {
                Text("• ${it.title}")
            }
        }
    }
}
