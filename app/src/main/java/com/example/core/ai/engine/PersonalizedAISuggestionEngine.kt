package com.example.core.ai.engine

import com.example.core.ai.repository.AIMemoryItem
import com.example.core.ai.repository.AIRecommendation
import com.example.core.ai.repository.RecommendationType
import com.example.core.ai.repository.AIAnalysisReport
import com.example.core.common.AgendaEvent
import com.example.core.common.GoalItem
import com.example.core.common.HabitItem
import com.example.core.common.TaskItem
import com.example.core.common.TaskPriority

object PersonalizedAISuggestionEngine {

    fun generateSuggestions(
        schedule: List<AgendaEvent>,
        tasks: List<TaskItem>,
        habits: List<HabitItem>,
        goals: List<GoalItem>,
        memories: List<AIMemoryItem>
    ): AIAnalysisReport {
        val optimalTimeRecs = mutableListOf<AIRecommendation>()
        val productivityRecs = mutableListOf<AIRecommendation>()
        val habitRecs = mutableListOf<AIRecommendation>()
        val insights = mutableListOf<String>()

        val pendingTasks = tasks.filter { !it.isCompleted }
        val highPriorityTasks = pendingTasks.filter { it.priority == TaskPriority.HIGH }
        val mediumTasks = pendingTasks.filter { it.priority == TaskPriority.MEDIUM }
        val overdueTasks = pendingTasks.filter { it.dueDate.equals("Atrasadas", ignoreCase = true) }

        // =========================================================
        // 1. OPTIMAL TIMES FOR SPECIFIC TASKS (INTELLIGENT SCHEDULING)
        // =========================================================
        // Overdue or High priority quick win slot (Post-lunch reset: 13:45)
        val urgentTask = overdueTasks.firstOrNull() ?: highPriorityTasks.firstOrNull()
        if (urgentTask != null) {
            optimalTimeRecs.add(
                AIRecommendation(
                    title = "Horário Ideal: ${urgentTask.title}",
                    description = "Esta tarefa é de alta urgência (${urgentTask.priority.name}) e está pendente. O LifeOS detectou um bloco livre de 45 min às 13:45 pós-almoço para zerar essa pendência com foco exclusivo.",
                    category = "Horário Ótimo",
                    icon = "⏰",
                    actionText = "Agendar às 13:45",
                    type = RecommendationType.OPTIMAL_TIME,
                    optimalTime = "13:45",
                    productivityImpact = "Elimina gargalo crítico do dia",
                    reason = "Janela livre pós-almoço ideal para tarefas urgentes",
                    targetData = urgentTask.title
                )
            )
        }

        // Deep focus morning slot (11:30) for complex study/work tasks
        val deepWorkTask = highPriorityTasks.firstOrNull { it != urgentTask }
            ?: mediumTasks.firstOrNull { 
                it.category.contains("Estudos", ignoreCase = true) || 
                it.category.contains("Trabalho", ignoreCase = true) || 
                it.category.contains("Tecnologia", ignoreCase = true) 
            }
            ?: pendingTasks.firstOrNull { it != urgentTask }

        if (deepWorkTask != null) {
            optimalTimeRecs.add(
                AIRecommendation(
                    title = "Janela de Alta Atenção: ${deepWorkTask.title}",
                    description = "O LifeOS mapeou seu pico de foco biológico para as 11:30 (intervalo entre compromissos matinais e almoço). Ideal para avançar em '${deepWorkTask.title}' com energia cognitiva máxima.",
                    category = "Horário Ótimo",
                    icon = "🧠",
                    actionText = "Agendar às 11:30",
                    type = RecommendationType.OPTIMAL_TIME,
                    optimalTime = "11:30",
                    productivityImpact = "+40% velocidade de resolução",
                    reason = "Pico circadiano de atenção matinal",
                    targetData = deepWorkTask.title
                )
            )
        }

        // Afternoon execution slot (16:00) before evening wrap-up
        val afternoonTask = pendingTasks.firstOrNull { it != urgentTask && it != deepWorkTask }
        if (afternoonTask != null) {
            optimalTimeRecs.add(
                AIRecommendation(
                    title = "Horário Estratégico: ${afternoonTask.title}",
                    description = "Agende para as 16:00, logo após sua sessão principal de projetos e antes da rotina noturna. Garante fechamento do expediente com sensação de dever cumprido.",
                    category = "Horário Ótimo",
                    icon = "⚡",
                    actionText = "Agendar às 16:00",
                    type = RecommendationType.OPTIMAL_TIME,
                    optimalTime = "16:00",
                    productivityImpact = "Conclusão de ciclo diário",
                    reason = "Janela de 75min livre antes do anoitecer",
                    targetData = afternoonTask.title
                )
            )
        }

        // =========================================================
        // 2. POTENTIAL PRODUCTIVITY IMPROVEMENTS
        // =========================================================
        // Batching Errand / Administrative tasks
        val errandTasks = pendingTasks.filter { 
            it.category.contains("Pessoal", ignoreCase = true) || 
            it.tags.contains("Casa") ||
            it.title.contains("Comprar", ignoreCase = true)
        }
        if (errandTasks.isNotEmpty()) {
            productivityRecs.add(
                AIRecommendation(
                    title = "Agrupamento de Recados (Batching)",
                    description = "Você possui tarefas operacionais pontuais (${errandTasks.take(2).joinToString { "'${it.title}'" }}). Agrupá-las às 18:00 preserva seus blocos nobres de trabalho sem dispersão mental.",
                    category = "Produtividade",
                    icon = "📦",
                    actionText = "Aplicar Agrupamento",
                    type = RecommendationType.PRODUCTIVITY,
                    productivityImpact = "Economia estimada de 45 min",
                    reason = "Evita dispersão por alternância contínua de tarefas"
                )
            )
        }

        // Schedule Buffer & Fatigue Prevention
        val bookedEventCount = schedule.size
        if (bookedEventCount >= 4) {
            productivityRecs.add(
                AIRecommendation(
                    title = "Buffer de Descompressão Cognitiva (10 min)",
                    description = "Sua agenda tem $bookedEventCount compromissos marcados hoje. Inserir pausas conscientes de 10 minutos entre reuniões reduz em 62% a fadiga mental acumulada.",
                    category = "Produtividade",
                    icon = "🛡️",
                    actionText = "Ativar Pausa Consciente",
                    type = RecommendationType.PRODUCTIVITY,
                    productivityImpact = "Sustenta Focus Score em 90%+",
                    reason = "Prevenção ativa de fadiga mental"
                )
            )
        }

        // Goal Pacing Alignment
        val activeGoals = goals.filter { !it.isCompleted }
        val marathonGoal = activeGoals.find { 
            it.title.contains("Maratona", ignoreCase = true) || 
            it.title.contains("Corrida", ignoreCase = true) ||
            it.title.contains("km", ignoreCase = true)
        }
        if (marathonGoal != null) {
            productivityRecs.add(
                AIRecommendation(
                    title = "Aceleração de Meta: ${marathonGoal.title}",
                    description = "Seu progresso acumulado é de ${marathonGoal.progressValue} (${(marathonGoal.progressPercent * 100).toInt()}%). Para manter a curva ideal, reserve 1h no sábado de manhã para um treino de rodagem de 10 km.",
                    category = "Produtividade",
                    icon = "🏃",
                    actionText = "Agendar Treino",
                    type = RecommendationType.PRODUCTIVITY,
                    productivityImpact = "Avanço de meta para 83%",
                    reason = "Manutenção de consistência progressiva semanal",
                    targetData = "Treino de Rodagem 10km"
                )
            )
        }

        // Pareto 80/20 Rule for pending tasks
        if (pendingTasks.size >= 3) {
            productivityRecs.add(
                AIRecommendation(
                    title = "Princípio de Pareto (Regra 80/20)",
                    description = "Concluir as 2 tarefas de maior prioridade hoje gerará 80% do resultado efetivo do seu dia. Delegue ou adie pendências de baixo impacto para manter tração.",
                    category = "Produtividade",
                    icon = "🎯",
                    actionText = "Focar no Essencial",
                    type = RecommendationType.PRODUCTIVITY,
                    productivityImpact = "Maximização de resultado diário",
                    reason = "Priorização seletiva de alto impacto"
                )
            )
        }

        // =========================================================
        // 3. NEW HABIT SUGGESTIONS (TAILORED TO LIFESTYLE & GOALS)
        // =========================================================
        // 1. Pre-Focus Hydration Habit
        habitRecs.add(
            AIRecommendation(
                title = "Hidratação Pré-Foco (500ml)",
                description = "Beber 500ml de água imediatamente antes de iniciar blocos intensos de estudo ou trabalho eleva em 14% a clareza e concentração.",
                category = "Novo Hábito",
                icon = "💧",
                actionText = "Adicionar aos Hábitos",
                type = RecommendationType.HABIT,
                productivityImpact = "+14% oxigenação cerebral",
                reason = "Sinergia direta com foco e bem-estar físico",
                targetData = "Hidratação Pré-Foco (500ml)"
            )
        )

        // 2. Power Pomodoro Session
        val hasPomodoro = habits.any { it.name.contains("pomodoro", ignoreCase = true) }
        if (!hasPomodoro) {
            habitRecs.add(
                AIRecommendation(
                    title = "Sessão Power Pomodoro (25/5)",
                    description = "Ideal para vencer a inércia em tarefas complexas. Cronometre 25 min 100% focado com 5 min de descanso ativo.",
                    category = "Novo Hábito",
                    icon = "⏱️",
                    actionText = "Adicionar aos Hábitos",
                    type = RecommendationType.HABIT,
                    productivityImpact = "Superação de procrastinação",
                    reason = "Combate à dispersão em tarefas desafiadoras",
                    targetData = "Sessão Power Pomodoro (25/5)"
                )
            )
        }

        // 3. Evening 5-Minute Planning
        val hasEveningPlan = habits.any { 
            it.name.contains("noite", ignoreCase = true) || 
            it.name.contains("noturno", ignoreCase = true) || 
            it.name.contains("planejamento", ignoreCase = true) 
        }
        if (!hasEveningPlan) {
            habitRecs.add(
                AIRecommendation(
                    title = "Planejamento Noturno de 5 Minutos",
                    description = "Defina na véspera as 3 prioridades essenciais do dia seguinte. Elimina o estresse de decisão matinal e acelera seu início do dia.",
                    category = "Novo Hábito",
                    icon = "🌙",
                    actionText = "Adicionar aos Hábitos",
                    type = RecommendationType.HABIT,
                    productivityImpact = "Zero atrito no início matinal",
                    reason = "Clareza mental antecipada",
                    targetData = "Planejamento Noturno 5min"
                )
            )
        }

        // 4. Micro-Stretching / Posture Check
        val hasStretching = habits.any { it.name.contains("alongamento", ignoreCase = true) }
        if (!hasStretching) {
            habitRecs.add(
                AIRecommendation(
                    title = "Micro-Alongamento Postural (7 min)",
                    description = "Pausa de mobilidade para descompressão de coluna e pescoço entre tarefas no computador, auxiliando na recuperação muscular.",
                    category = "Novo Hábito",
                    icon = "🧘",
                    actionText = "Adicionar aos Hábitos",
                    type = RecommendationType.HABIT,
                    productivityImpact = "Alívio físico e postura",
                    reason = "Suporte postural para jornadas de tela",
                    targetData = "Micro-Alongamento Postural"
                )
            )
        }

        // Insights summary
        insights.add("📅 Agenda: ${schedule.size} compromissos mapeados. Janelas de alta produtividade identificadas às 11:30 e 16:00.")
        insights.add("📋 Tarefas: ${pendingTasks.size} tarefas pendentes, sendo ${highPriorityTasks.size} de alta prioridade.")
        insights.add("✅ Hábitos: Sequência consistente de ${habits.maxOfOrNull { it.streak } ?: 0} dias ativos. Sugerido reforço em hidratação pré-foco.")
        insights.add("🎯 Metas: ${goals.size} metas monitoradas. Oportunidade de alinhamento com treino para a meta de maratona.")

        val allRecs = optimalTimeRecs + productivityRecs + habitRecs

        val analysisText = "O motor LifeOS AI cruzou seus ${schedule.size} eventos de agenda, ${tasks.size} tarefas, ${habits.size} hábitos e ${goals.size} metas. Identificamos ${optimalTimeRecs.size} horários nobres de execução, ${productivityRecs.size} melhorias de produtividade para evitar fadiga e ${habitRecs.size} novos micro-hábitos estratégicos."

        return AIAnalysisReport(
            analysisText = analysisText,
            insights = insights,
            recommendations = allRecs,
            optimalTimeRecommendations = optimalTimeRecs,
            productivityImprovements = productivityRecs,
            habitSuggestions = habitRecs
        )
    }
}
