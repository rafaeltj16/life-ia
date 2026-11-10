package com.example.features.finance.presentation.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.core.ui.*
import com.example.core.common.LifeOSViewModel
import java.util.Locale

@Composable
fun FinancesScreen(viewModel: LifeOSViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val aiFinanceMsg by viewModel.aiFinanceAdvice.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("") }

    val periods = listOf("Hoje", "Ontem", "Esta semana", "Este mês")
    var selectedPeriod by remember { mutableStateOf("Este mês") }

    val filteredTransactions = remember(transactions, selectedPeriod) {
        transactions.filter { tx ->
            when (selectedPeriod) {
                "Hoje" -> tx.date == "14 Jul" || tx.date.contains("Hoje", ignoreCase = true)
                "Ontem" -> tx.date == "13 Jul" || tx.date.contains("Ontem", ignoreCase = true)
                "Esta semana" -> {
                    val weekDates = listOf("14 Jul", "13 Jul", "12 Jul", "11 Jul", "10 Jul", "09 Jul", "08 Jul")
                    tx.date in weekDates || tx.date.contains("Hoje", ignoreCase = true) || tx.date.contains("Ontem", ignoreCase = true)
                }
                "Este mês" -> {
                    tx.date.contains("Jul", ignoreCase = true) || tx.date.contains("Hoje", ignoreCase = true) || tx.date.contains("Ontem", ignoreCase = true)
                }
                else -> true
            }
        }
    }

    val totalIncome = filteredTransactions.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { !it.isIncome }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

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
                Text("Gestão Financeira", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                LifeOSButton(
                    text = "Registrar",
                    onClick = { showAddDialog = true },
                    icon = Icons.Default.Add,
                    modifier = Modifier.testTag("add_transaction_button")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Financial Suggestions inside main screen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.generateAIFinancialAdvice() }
                    .background(Color(0xFF66BB6A).copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                    .border(0.5.dp, Color(0xFF66BB6A).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF66BB6A), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Gerar análise do balanço de gastos com IA",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF66BB6A),
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF66BB6A), modifier = Modifier.size(18.dp))
            }

            if (aiFinanceMsg != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = aiFinanceMsg ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Period Filter Selector Row (Hoje, Ontem, Esta semana, Este mês)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEach { period ->
                    val isSelected = selectedPeriod == period
                    val activeColor = when (period) {
                        "Hoje" -> MaterialTheme.colorScheme.primary
                        "Ontem" -> Color(0xFFFFA726)
                        "Esta semana" -> Color(0xFF29B6F6)
                        "Este mês" -> Color(0xFF7E57C2)
                        else -> MaterialTheme.colorScheme.primary
                    }
                    val containerColor = if (isSelected) activeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    val borderColor = if (isSelected) activeColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    val textColor = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant

                    Box(
                        modifier = Modifier
                            .background(containerColor, RoundedCornerShape(10.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                            .clickable { selectedPeriod = period }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Balance Overview Box
            LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Recebido", style = MaterialTheme.typography.labelSmall, color = Color(0xFF81C784))
                        Text("R$ ${String.format(Locale.US, "%,.2f", totalIncome)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Despendido", style = MaterialTheme.typography.labelSmall, color = Color(0xFFEF5350))
                        Text("R$ ${String.format(Locale.US, "%,.2f", totalExpense)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Saldo Líquido", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    Text(
                        "R$ ${String.format(Locale.US, "%,.2f", balance)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (balance >= 0) Color(0xFF81C784) else Color(0xFFEF5350)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Histórico de Lançamentos", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(filteredTransactions) { tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (tx.isIncome) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (tx.isIncome) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = if (tx.isIncome) Color(0xFF4CAF50) else Color(0xFFF44336),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(tx.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text("${tx.category} • ${tx.date}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Text(
                            "${if (tx.isIncome) "+" else "-"} R$ ${String.format(Locale.US, "%,.2f", tx.amount)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (tx.isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        )
                    }
                }
            }

            if (showAddDialog) {
                LifeOSDialog(
                    title = "Novo Lançamento",
                    onDismissRequest = { showAddDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                val amt = amountStr.toDoubleOrNull() ?: 0.0
                                if (title.isNotBlank() && amt > 0.0) {
                                    viewModel.addTransaction(title, amt, isIncome, category)
                                    title = ""
                                    amountStr = ""
                                    category = ""
                                    showAddDialog = false
                                }
                            },
                            modifier = Modifier.testTag("confirm_add_transaction_button")
                        ) {
                            Text("Adicionar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
                    }
                ) {
                    LifeOSInput(value = title, onValueChange = { title = it }, placeholder = "Descrição (ex: Compra de teclado)")
                    LifeOSInput(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        placeholder = "Valor (ex: 250.00)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    LifeOSInput(value = category, onValueChange = { category = it }, placeholder = "Categoria (ex: Alimentação, Lazer)")

                    Text("Tipo de Lançamento", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { isIncome = true },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Receita", color = if (isIncome) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                        Button(
                            onClick = { isIncome = false },
                            colors = ButtonDefaults.buttonColors(containerColor = if (!isIncome) Color(0xFFF44336) else MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Despesa", color = if (!isIncome) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}
