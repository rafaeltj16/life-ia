package com.lifeos.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class GraphNode(
    val id: String,
    val title: String,
    val detail: String,
    val icon: ImageVector,
    val nodeColor: Color
)

@Composable
fun LifeGraphEngineWidget(
    modifier: Modifier = Modifier,
    onNodeSelected: (String) -> Unit = {}
) {
    var selectedNodeId by remember { mutableStateOf<String?>("life_ai") }

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Life Graph Engine
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00897B).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Life Graph Engine",
                            tint = Color(0xFF00695C),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🌐 Life Graph Engine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Grafo de Conhecimento e Conexões Pessoais",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE0F2F1),
                    contentColor = Color(0xFF00695C)
                ) {
                    Text(
                        text = "9 Nós Ativos",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Root Node: Usuário
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { selectedNodeId = "user" }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Usuário",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Arrow down to Engine
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(16.dp)
                )
            }

            // Central Engine Node: Life Graph Engine
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF004D40),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { selectedNodeId = "engine" }
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = Color(0xFF80CBC4),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Life Graph Engine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Connector lines down to 3 columns
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3 Columns Layout representing the Topology Graph
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Column 1: Objetivos -> Tarefas -> Hábitos
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GraphNodeCard(
                        node = GraphNode("objetivos", "Objetivos", "3 Metas ativas", Icons.Default.TrackChanges, Color(0xFF7E57C2)),
                        isSelected = selectedNodeId == "objetivos",
                        onClick = { selectedNodeId = "objetivos" }
                    )
                    ConnectorArrow()
                    GraphNodeCard(
                        node = GraphNode("tarefas", "Tarefas", "18 concluídas", Icons.Default.TaskAlt, Color(0xFF00897B)),
                        isSelected = selectedNodeId == "tarefas",
                        onClick = { selectedNodeId = "tarefas" }
                    )
                    ConnectorArrow()
                    GraphNodeCard(
                        node = GraphNode("habitos", "Hábitos", "7 mantidos", Icons.Default.Repeat, Color(0xFF43A047)),
                        isSelected = selectedNodeId == "habitos",
                        onClick = { selectedNodeId = "habitos" }
                    )
                }

                // Column 2: Projetos -> Calendário -> Focus Mode
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GraphNodeCard(
                        node = GraphNode("projetos", "Projetos", "2 em andamento", Icons.Default.FolderSpecial, Color(0xFF1E88E5)),
                        isSelected = selectedNodeId == "projetos",
                        onClick = { selectedNodeId = "projetos" }
                    )
                    ConnectorArrow()
                    GraphNodeCard(
                        node = GraphNode("calendario", "Calendário", "5 compromissos", Icons.Default.CalendarMonth, Color(0xFFFB8C00)),
                        isSelected = selectedNodeId == "calendario",
                        onClick = { selectedNodeId = "calendario" }
                    )
                    ConnectorArrow()
                    GraphNodeCard(
                        node = GraphNode("focus", "Focus Mode", "6h acumuladas", Icons.Default.Timer, Color(0xFFD81B60)),
                        isSelected = selectedNodeId == "focus",
                        onClick = { selectedNodeId = "focus" }
                    )
                }

                // Column 3: Pessoas -> Conversas -> Notas
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GraphNodeCard(
                        node = GraphNode("pessoas", "Pessoas", "12 contatos", Icons.Default.Person, Color(0xFF8E24AA)),
                        isSelected = selectedNodeId == "pessoas",
                        onClick = { selectedNodeId = "pessoas" }
                    )
                    ConnectorArrow()
                    GraphNodeCard(
                        node = GraphNode("conversas", "Conversas", "4 recentes", Icons.Default.Chat, Color(0xFF00ACC1)),
                        isSelected = selectedNodeId == "conversas",
                        onClick = { selectedNodeId = "conversas" }
                    )
                    ConnectorArrow()
                    GraphNodeCard(
                        node = GraphNode("notas", "Notas", "12 arquivadas", Icons.Default.Description, Color(0xFFFFB300)),
                        isSelected = selectedNodeId == "notas",
                        onClick = { selectedNodeId = "notas" }
                    )
                }
            }

            // Arrow down to LifeAI
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .size(18.dp)
                )
            }

            // Convergence Terminal Node: LifeAI
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF4A148C),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { selectedNodeId = "life_ai" }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFE1BEE7),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "LifeAI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFFB9F6CA),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Details card for selected node
            selectedNodeId?.let { nodeId ->
                Spacer(modifier = Modifier.height(14.dp))
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Integração em Tempo Real do Life Graph",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (nodeId) {
                                    "user" -> "👤 Perfil do Usuário: Ponto de partida de todas as decisões, hábitos e agendas do LifeOS."
                                    "engine" -> "⚙️ Life Graph Engine: Sincroniza dados relacionais entre projetos, agenda, contatos e hábitos com latência de milissegundos."
                                    "objetivos" -> "🎯 Objetivos: Decompostos automaticamente pela IA em projetos e tarefas no Kanban."
                                    "tarefas" -> "✅ Tarefas: Conectadas à agenda e às sessões de foco diárias."
                                    "habitos" -> "🔄 Hábitos: Alimentam o indicador de consistência e o Life Score do usuário."
                                    "projetos" -> "📁 Projetos: Estruturam o fluxo de desenvolvimento do app e milestones de 90 dias."
                                    "calendario" -> "📅 Calendário: Aloca dinamicamente blocos de trabalho sem conflito de horários."
                                    "focus" -> "⏱️ Focus Mode: Registra a produtividade líquida acumulada nas sessões Pomodoro."
                                    "pessoas" -> "👥 Pessoas: Grafo de contatos e mentores vinculados a reuniões e conversas."
                                    "conversas" -> "💬 Conversas: Transcrições e insights de alinhamentos capturados automaticamente."
                                    "notas" -> "📝 Notas: Base de conhecimento conectada aos objetivos e projetos ativos."
                                    else -> "🤖 LifeAI: IA Central unificada processando todos os 9 nós do grafo para gerar recomendações autônomas diárias."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hierarchical Execution Workflow
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(14.dp)
            ) {
                Text(
                    text = "🌿 Fluxo de Conexão do Grafo",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F)
                )
                Spacer(modifier = Modifier.height(10.dp))

                val workflowChain = listOf(
                    Triple("Objetivo", "Lançar LifeOS AI", Color(0xFF7E57C2)),
                    Triple("Projeto", "Dashboard Premium", Color(0xFF1E88E5)),
                    Triple("Tarefa", "Criar Widget Financeiro", Color(0xFF00897B)),
                    Triple("Sessão de Foco", "Bloco Pomodoro 25m", Color(0xFFD81B60)),
                    Triple("Nota", "Ideias da IA", Color(0xFFFFB300))
                )

                workflowChain.forEachIndexed { index, (type, title, color) ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = color.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF263238)
                                )
                            }
                        }

                        if (index < workflowChain.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GraphNodeCard(
    node: GraphNode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) node.nodeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, node.nodeColor) else null
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = node.icon,
                contentDescription = node.title,
                tint = if (isSelected) node.nodeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = node.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) node.nodeColor else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = node.detail,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ConnectorArrow() {
    Icon(
        imageVector = Icons.Default.ArrowDownward,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .size(12.dp)
    )
}
