package com.example.features.focus.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun FocusControls(
    isPaused: Boolean,
    onPauseToggle: () -> Unit,
    onFinish: () -> Unit,
    onLogInterruption: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onPauseToggle,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("focus_controls_pause_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Continuar" else "Pausar"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isPaused) "Continuar" else "Pausar")
            }

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("focus_controls_finish_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Finalizar"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Finalizar")
            }
        }

        TextButton(
            onClick = onLogInterruption,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("focus_controls_log_interruption")
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Registrar Interrupção", style = MaterialTheme.typography.labelMedium)
        }
    }
}
