package com.lifeos.core.ai.lifescore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LifeScoreWidget(
    result: LifeScoreResult,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Life Score",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${result.overall}",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (result.overall / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            ScoreItem("Produtividade", result.productivity)
            ScoreItem("Hábitos", result.habits)
            ScoreItem("Saúde", result.health)
            ScoreItem("Projetos", result.projects)
            ScoreItem("Finanças", result.finance)
            ScoreItem("Foco", result.focus)
        }
    }
}

@Composable
fun ScoreItem(
    label: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$score",
            style = MaterialTheme.typography.titleSmall
        )
    }
}
