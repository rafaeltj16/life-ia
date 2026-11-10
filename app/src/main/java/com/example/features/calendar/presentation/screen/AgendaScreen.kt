package com.example.features.calendar.presentation.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.ui.*
import com.example.core.common.LifeOSViewModel

@Composable
fun AgendaScreen(viewModel: LifeOSViewModel) {
    val events by viewModel.agendaEvents.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Minha Agenda", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                LifeOSButton(
                    text = "Novo Compromisso",
                    onClick = { showAddDialog = true },
                    icon = Icons.Default.Add,
                    modifier = Modifier.testTag("add_event_button")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (events.isEmpty()) {
                EmptyStateView(
                    title = "Agenda Vazia",
                    description = "Você não possui nenhum compromisso agendado para hoje. Clique no botão acima para adicionar."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(events) { event ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(event.time, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium))
                                }
                                if (event.description.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                LifeOSBadge(text = event.category, color = MaterialTheme.colorScheme.secondary)
                                IconButton(
                                    onClick = { viewModel.deleteEvent(event.id) },
                                    modifier = Modifier.testTag("delete_event_${event.id}")
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Deletar evento", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            if (showAddDialog) {
                LifeOSDialog(
                    title = "Novo Compromisso",
                    onDismissRequest = { showAddDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (title.isNotBlank() && time.isNotBlank()) {
                                    viewModel.addEvent(title, time, category, description)
                                    title = ""
                                    time = ""
                                    category = ""
                                    description = ""
                                    showAddDialog = false
                                }
                            },
                            modifier = Modifier.testTag("confirm_add_event_button")
                        ) {
                            Text("Adicionar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
                    }
                ) {
                    LifeOSInput(value = title, onValueChange = { title = it }, placeholder = "Título do evento")
                    LifeOSInput(value = time, onValueChange = { time = it }, placeholder = "Horário (ex: 14:00 - 15:00)")
                    LifeOSInput(value = category, onValueChange = { category = it }, placeholder = "Categoria (Trabalho, Saúde, etc.)")
                    LifeOSInput(value = description, onValueChange = { description = it }, placeholder = "Descrição rápida", singleLine = false)
                }
            }
        }
    }
}
