package com.example.features.dashboard.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.LifeOSCard

@Composable
fun StatsCard(
    rates: Map<String, Float> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val barColor = MaterialTheme.colorScheme.primary
    val barTrackColor = MaterialTheme.colorScheme.surfaceVariant

    LifeOSCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Leaderboard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Desempenho da Semana",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        val days = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
        val scores = days.map { rates[it] ?: 0.5f }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val columnCount = days.size
            val spacing = 20.dp.toPx()
            val availableWidth = size.width - (spacing * (columnCount + 1))
            val barWidth = availableWidth / columnCount

            for (i in 0 until columnCount) {
                val score = scores[i]
                val xOffset = spacing + i * (barWidth + spacing)

                val barMaxHeight = size.height - 24.dp.toPx()
                val barTopY = size.height - 20.dp.toPx() - (score * barMaxHeight)
                val barBottomY = size.height - 20.dp.toPx()

                drawRoundRect(
                    color = barTrackColor,
                    topLeft = Offset(xOffset, 0f),
                    size = Size(barWidth, barBottomY),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(xOffset, barTopY),
                    size = Size(barWidth, barBottomY - barTopY),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
