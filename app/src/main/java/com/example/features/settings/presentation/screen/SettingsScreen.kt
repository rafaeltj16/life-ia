package com.example.features.settings.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.ui.*
import com.example.core.common.AuthState
import com.example.core.common.LifeOSViewModel

@Composable
fun SettingsScreen(
    viewModel: LifeOSViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkTheme by viewModel.isDarkMode.collectAsState()
    val authState by viewModel.authState.collectAsState()

    val userEmail = when (val state = authState) {
        is AuthState.Authenticated -> state.email
        else -> "sem.email@lifeos.ai"
    }

    val userName = when (val state = authState) {
        is AuthState.Authenticated -> state.name
        else -> "Usuário"
    }

    ResponsiveScreenContainer {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Configurações", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            }

            // Profile brief
            item {
                LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LifeOSAvatar(name = userName, size = 54.dp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(userName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(userEmail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Appearance settings
            item {
                Text("Aparência e Design", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            item {
                LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Tema Escuro (Dark Mode)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Alterna as cores globais da interface", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        LifeOSSwitch(
                            checked = isDarkTheme,
                            onCheckedChange = { viewModel.toggleTheme() },
                            modifier = Modifier.testTag("dark_theme_switch")
                        )
                    }
                }
            }

            // Subscription plans section
            item {
                Text("Assinatura e Planos", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            item {
                PricingSection(
                    onPlanSelected = { plan ->
                        viewModel.showToast(
                            if (plan == "annual") "Assinatura do plano Anual com 30% de desconto iniciada com sucesso!"
                            else "Assinatura do plano Mensal iniciada com sucesso!",
                            com.example.core.common.ToastType.SUCCESS
                        )
                    },
                    onFooterLinkClicked = { link ->
                        val msg = when (link) {
                            "dashboard" -> "Visualizando visão geral das ferramentas do Dashboard..."
                            "roadmap" -> "Abrindo Roadmap oficial com recursos futuros..."
                            "blog" -> "Abrindo Blog do LifeOS: dicas de produtividade e foco..."
                            "contact" -> "Abrindo canal de atendimento e contato com suporte..."
                            "instagram" -> "Redirecionando para o Instagram do LifeOS..."
                            "linkedin" -> "Redirecionando para o LinkedIn do LifeOS..."
                            "discord" -> "Abrindo convite para a comunidade oficial no Discord..."
                            "privacy" -> "Carregando Política de Privacidade..."
                            "terms" -> "Carregando Termos de Uso e Condições..."
                            "lgpd" -> "Abrindo termos de conformidade e segurança LGPD..."
                            "cookies" -> "Gerenciando preferências de Cookies e rastreamento..."
                            else -> "Iniciando canal de Suporte com nossa equipe..."
                        }
                        viewModel.showToast(msg, com.example.core.common.ToastType.INFO)
                    }
                )
            }

            // Gemini AI configuration brief
            item {
                Text("Inteligência Artificial (Gemini)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            item {
                LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Conectividade de IA", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "O LifeOS AI está configurado para ler sua chave da API do Gemini diretamente de suas credenciais seguras do AI Studio (Secrets Panel).\n\n" +
                                "Caso queira que o chatbot faça requisições reais de texto, configure a chave 'GEMINI_API_KEY' no painel de segredos do editor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF66BB6A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fallback automático de simulação ativo", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF66BB6A))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Log out action button
            item {
                Button(
                    onClick = { viewModel.signOut() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sign_out_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Encerrar Sessão", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}
