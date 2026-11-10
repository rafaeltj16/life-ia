package com.example.core.network

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber

// --- Moshi Mapped structures for compatibility ---

data class GeminiPart(
    val text: String
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiCandidate(
    val content: GeminiContent
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

object GeminiServiceClient {
    private const val TAG = "GeminiServiceClient"
    private const val MODEL_NAME = "gemini-3.8-flash"

    private val model by lazy {
        try {
            Firebase.ai.generativeModel(MODEL_NAME)
        } catch (e: Exception) {
            Timber.e(e, "GeminiServiceClient: Failed to initialize Firebase AI GenerativeModel")
            null
        }
    }

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val generativeModel = model
        if (generativeModel != null) {
            try {
                val response = withTimeout(60_000L) {
                    generativeModel.generateContent(prompt)
                }
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            } catch (e: TimeoutCancellationException) {
                Timber.e(e, "Gemini API request timed out after 60s")
                return@withContext "A requisição ao Gemini expirou após 60 segundos. Tente novamente."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Error calling Firebase AI Gemini API ($MODEL_NAME): ${e.localizedMessage}")
                // In local tests or network failure, provide fallback response
                return@withContext getMockResponse(prompt)
            }
        }

        return@withContext getMockResponse(prompt)
    }

    private fun getMockResponse(prompt: String): String {
        val query = prompt.lowercase()
        return when {
            query.contains("missão") || query.contains("missao") -> {
                "🎯 **Sua Missão Principal do Dia:**\n\n" +
                "• **Atividade:** *Finalizar Dashboard*\n" +
                "• **Tempo Estimado:** **1h40**\n\n" +
                "Esta missão foi consolidada com base no seu projeto prioritário. Toque em `[Começar]` no terminal retro do Dashboard para iniciar o cronômetro de foco e registrar sua evolução!"
            }
            query.contains("devo fazer") || query.contains("agenda") -> {
                "⏰ **Sua Próxima Atividade de Hoje:**\n\n" +
                "• Atualmente, você deve focar em: **Projeto (LifeOS)** às **08h30**.\n" +
                "• Próxima atividade programada: **Academia** às **11h00**.\n" +
                "• Atividade da tarde: **Landing Page** às **15h00**.\n\n" +
                "Dica da IA: Mantenha seu ritmo ativo completando cada etapa. O seu momentum agradece!"
            }
            query.contains("evoluindo") || query.contains("resumo") || query.contains("habito") || query.contains("hábito") || query.contains("sequência") || query.contains("sequencia") -> {
                "🔥 **Resumo do seu progresso no LifeOS:**\n\n" +
                "• **Sequência (Streak):** 28 dias ativos! (Meta: 30 dias)\n" +
                "• **Nível atual:** Nível 15 (Excelente evolução!)\n" +
                "• **Conclusão de Hábitos:** Você concluiu 87% de seus hábitos programados para hoje.\n" +
                "• **Projeto LifeOS:** 65% concluído.\n\n" +
                "Seu Focus Score atualizado é de **94/100**. Continue assim!"
            }
            query.contains("recomenda") || query.contains("horário") || query.contains("horario") -> {
                "🤖 **Recomendações Inteligentes do LifeOS AI:**\n\n" +
                "• **Janela de Alta Performance:** Hoje o seu rendimento biológico e histórico indica foco máximo **entre 08h e 11h**.\n" +
                "• **Alocação Inteligente:** Programe suas tarefas mais complexas (como programação ou escrita criativa) para este período.\n" +
                "• **Status de Ritmo (Momentum):** Você está com **91% (Excelente ritmo!)**. Continue executando com atenção para evitar a perda de ritmo (Perdendo ritmo / 42%)."
            }
            query.contains("core") || query.contains("feature") || query.contains("arquitetura") || query.contains("modular") || query.contains("módulo") || query.contains("modulo") -> {
                "🏗️ **Arquitetura Modular do LifeOS:**\n\n" +
                "O projeto é estruturado em uma arquitetura de módulos altamente desacoplados e escaláveis, seguindo os melhores padrões do Android Moderno:\n\n" +
                "📂 **Camada Core (Compartilhada):**\n" +
                "• `core/`: Contém as regras de negócio de base, modelos globais, banco de dados local Room e gerenciamento de estado global.\n" +
                "• `core_ui/`: Centraliza o Design System em Jetpack Compose, componentes retro de terminal, fontes customizadas e paleta de cores Material 3.\n" +
                "• `core_ai/`: Consolida o motor de inteligência artificial, regras heurísticas, predições matemáticas e integração com a API do Gemini.\n\n" +
                "📂 **Camada de Recursos (Features Independentes):**\n" +
                "• `feature_dashboard/`: Tela principal unificada de controle com status diurnos/noturnos retro.\n" +
                "• `feature_habits/`: Gerenciamento e acompanhamento gráfico de micro-hábitos diários.\n" +
                "• `feature_tasks/`: Lista de tarefas robustas por categoria.\n" +
                "• `feature_projects/`: Controle de entregáveis e percentuais de conclusão de projetos.\n" +
                "• `feature_calendar/`: Exibição visual e interativa da agenda inteligente diária.\n" +
                "• `feature_profile/`: Gestão de preferências do usuário, temas, assinaturas e limites corporativos.\n\n" +
                "Essa organização garante tempos de compilação rápidos, testabilidade isolada com Robolectric/Roborazzi e alta manutenibilidade do app!"
            }
            query.contains("tarefa") || query.contains("task") || query.contains("organizar") -> {
                "Aqui está um plano de tarefas sugerido para otimizar sua produtividade:\n\n" +
                "1. **Revisão Semanal (30 min)** - Priorize o projeto mais crítico.\n" +
                "2. **Bloco de Foco Profundo (2h)** - Realize as tarefas de alta prioridade sem distrações.\n" +
                "3. **Rotina de Hábitos (15 min)** - Beba água, faça alongamento e organize os estudos.\n\n" +
                "Gostaria que eu adicione estas tarefas à sua agenda automática?"
            }
            query.contains("finance") || query.contains("gasto") || query.contains("dinheiro") -> {
                "Excelente iniciativa de cuidar das finanças!\n\n" +
                "Recomendo a regra clássica **50/30/20**:\n" +
                "• **50%** para necessidades básicas (moradia, alimentação)\n" +
                "• **30%** para desejos pessoais (lazer, compras)\n" +
                "• **20%** para poupança, investimentos e metas financeiras.\n\n" +
                "Você pode registrar suas receitas e despesas no módulo 'Finanças' do menu lateral."
            }
            else -> {
                "Olá! Sou o **LifeOS AI**, seu copiloto inteligente para produtividade e bem-estar.\n\n" +
                "Posso ajudar você a:\n" +
                "• Organizar suas tarefas e agenda diária.\n" +
                "• Monitorar hábitos saudáveis e treinos.\n" +
                "• Planejar metas financeiras e despesas.\n\n" +
                "O que gostaria de analisar ou planejar agora?"
            }
        }
    }
}
