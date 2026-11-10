package com.lifeos.core.automation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun AutomationsWidget(
    modifier: Modifier = Modifier,
    initialRules: List<AutomationRule> = defaultAutomationRules(),
    onRuleToggle: (String, Boolean) -> Unit = { _, _ -> },
    onAddAutomation: (AutomationRule) -> Unit = {}
) {
    var rules by remember { mutableStateOf(initialRules) }
    var showAddDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "⚡ Automações",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            rules.forEach { rule ->
                AutomationItemRow(
                    rule = rule,
                    onCheckedChange = { checked ->
                        rules = rules.map {
                            if (it.id == rule.id) it.copy(enabled = checked) else it
                        }
                        onRuleToggle(rule.id, checked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Nova automação")
            }
        }
    }

    if (showAddDialog) {
        AddAutomationDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newRule ->
                rules = rules + newRule
                onAddAutomation(newRule)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AutomationItemRow(
    rule: AutomationRule,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!rule.enabled) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = rule.enabled,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = rule.name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (rule.enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AddAutomationDialog(
    onDismiss: () -> Unit,
    onConfirm: (AutomationRule) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Criar Nova Automação") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Automação") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            AutomationRule(
                                id = UUID.randomUUID().toString(),
                                name = name.trim(),
                                enabled = true,
                                trigger = AutomationTrigger.AppOpened,
                                actions = listOf(AutomationAction.SendNotification)
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun defaultAutomationRules(): List<AutomationRule> = listOf(
    AutomationRule(
        id = "1",
        name = "Bom dia inteligente",
        enabled = true,
        trigger = AutomationTrigger.WakeUp,
        actions = listOf(AutomationAction.SendNotification, AutomationAction.CreateDailyMission)
    ),
    AutomationRule(
        id = "2",
        name = "Revisão noturna",
        enabled = true,
        trigger = AutomationTrigger.BedTime,
        actions = listOf(AutomationAction.SendNotification, AutomationAction.RecalculateLifeScore)
    ),
    AutomationRule(
        id = "3",
        name = "Recalcular Life Score",
        enabled = true,
        trigger = AutomationTrigger.Time(hour = 20),
        actions = listOf(AutomationAction.RecalculateLifeScore)
    ),
    AutomationRule(
        id = "4",
        name = "Iniciar foco após reunião",
        enabled = true,
        trigger = AutomationTrigger.AppOpened,
        actions = listOf(AutomationAction.StartFocusMode)
    ),
    AutomationRule(
        id = "5",
        name = "Planejamento semanal",
        enabled = false,
        trigger = AutomationTrigger.Time(hour = 9),
        actions = listOf(AutomationAction.AskLifeAI)
    )
)
