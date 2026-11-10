package com.lifeos.widgets

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun KnowledgeWidget(
    modifier: Modifier = Modifier,
    recentNotesCount: Int = 12,
    unreviewedIdeasCount: Int = 3,
    favoriteDocsCount: Int = 2,
    onOpenKnowledgeBase: () -> Unit = {}
) {
    var selectedCategory by remember { mutableIntStateOf(0) } // 0: Notas, 1: Ideias, 2: Favoritos

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
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
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Conhecimento",
                            tint = Color(0xFF00695C),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "📚 Conhecimento",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Base de Notas, Ideias e Documentos",
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
                        text = "${recentNotesCount + unreviewedIdeasCount + favoriteDocsCount} Itens",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Knowledge Stats Row Items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Recent Notes Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedCategory == 0) Color(0xFFE0F2F1)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .border(
                            1.dp,
                            if (selectedCategory == 0) Color(0xFF00897B) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCategory = 0 }
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = Color(0xFF00695C),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Notas",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF00695C)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$recentNotesCount recentes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Unreviewed Ideas Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedCategory == 1) Color(0xFFFFF8E1)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .border(
                            1.dp,
                            if (selectedCategory == 1) Color(0xFFFFB300) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCategory = 1 }
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFFF8F00),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ideias",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF8F00)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$unreviewedIdeasCount pendentes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Favorite Docs Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedCategory == 2) Color(0xFFEDE7F6)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .border(
                            1.dp,
                            if (selectedCategory == 2) Color(0xFF7E57C2) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCategory = 2 }
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF512DA8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Favoritos",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF512DA8)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$favoriteDocsCount docs",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Preview Snippets
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                when (selectedCategory) {
                    0 -> {
                        Text(
                            text = "Destaques das Notas Recentes",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "• Arquitetura do LifeOS & Compose", style = MaterialTheme.typography.bodySmall)
                        Text(text = "• Resumo do Livro 'Hábitos Atômicos'", style = MaterialTheme.typography.bodySmall)
                        Text(text = "• Insights do Planejamento Trimestral", style = MaterialTheme.typography.bodySmall)
                    }
                    1 -> {
                        Text(
                            text = "Ideias para Revisar",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8F00)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "💡 Widget para controle de água inteligente", style = MaterialTheme.typography.bodySmall)
                        Text(text = "💡 Integração de voz no AI Copilot", style = MaterialTheme.typography.bodySmall)
                        Text(text = "💡 Modo escuro automático por horário", style = MaterialTheme.typography.bodySmall)
                    }
                    2 -> {
                        Text(
                            text = "Documentos Favoritos",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF512DA8)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "⭐ Manual de Estilo & UI Design System", style = MaterialTheme.typography.bodySmall)
                        Text(text = "⭐ Guia de Produtividade Pessoal 2026", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Open Action Button
            Button(
                onClick = onOpenKnowledgeBase,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Abrir Base de Conhecimento")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
