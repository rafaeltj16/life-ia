package com.lifeos.core.ai.orchestrator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lifeos.core.ai.models.AIContext
import com.lifeos.core.ai.models.AIRequest
import kotlinx.coroutines.launch

@Composable
fun AIOrchestratorWidget(
    modifier: Modifier = Modifier,
    orchestrator: AIOrchestrator = remember { AIOrchestrator() },
    contextBuilder: AIContextBuilder = remember { AIContextBuilder() }
) {
    val scope = rememberCoroutineScope()
    var userQuery by remember { mutableStateOf("") }
    var detectedIntentName by remember { mutableStateOf<String?>(null) }
    var lastFormattedResponse by remember { mutableStateOf<String?>(null) }
    var providerUsedName by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val responseFormatter = remember { AIResponseFormatter() }
    val intentDetector = remember { AIIntentDetector() }

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LifeOS AI Orchestrator",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Suggestions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SuggestionChip(
                    onClick = { userQuery = "Criar tarefa para amanhã" },
                    label = { Text("Ação Local (Tarefa)") }
                )
                SuggestionChip(
                    onClick = { userQuery = "Como otimizar meu foco?" },
                    label = { Text("Consulta IA (Gemini)") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Question Input
            OutlinedTextField(
                value = userQuery,
                onValueChange = { userQuery = it },
                label = { Text("Pergunta do usuário") },
                placeholder = { Text("Ex: Iniciar modo de foco de 25 min") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (userQuery.isNotBlank()) {
                                isProcessing = true
                                scope.launch {
                                    val detected = intentDetector.detectIntent(userQuery)
                                    detectedIntentName = detected.name

                                    val request = AIRequest(
                                        prompt = userQuery,
                                        context = contextBuilder.build()
                                    )
                                    val rawResponse = orchestrator.processRequest(request)
                                    providerUsedName = rawResponse.providerUsed
                                    lastFormattedResponse = responseFormatter.format(rawResponse)
                                    isProcessing = false
                                }
                            }
                        },
                        enabled = userQuery.isNotBlank() && !isProcessing
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar Pergunta"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Flow Visualizer Steps
            if (detectedIntentName != null || lastFormattedResponse != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    detectedIntentName?.let { intent ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AIIntentDetector: $intent",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    providerUsedName?.let { provider ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isLocal = provider == "LocalRules"
                            Icon(
                                imageVector = if (isLocal) Icons.Default.Bolt else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isLocal) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isLocal) "Ação Local / Executor ($provider)" else "Consulta IA ($provider)",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isLocal) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    lastFormattedResponse?.let { response ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Resposta Formatada:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = response,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
