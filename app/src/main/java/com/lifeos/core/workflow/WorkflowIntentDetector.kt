package com.lifeos.core.workflow

sealed class WorkflowIntent(
    val intentName: String,
    val confidence: Float,
    val suggestedCategory: String,
    val suggestedTrigger: Trigger,
    val suggestedActions: List<Action>
) {
    class TaskCompletionFocusFlow(confidence: Float = 0.98f) : WorkflowIntent(
        intentName = "TaskCompleted → Focus → Dashboard → Score",
        confidence = confidence,
        suggestedCategory = "Produtividade",
        suggestedTrigger = Trigger.TaskCompleted("current_task"),
        suggestedActions = listOf(Action.StartFocus, Action.UpdateDashboard, Action.CalculateLifeScore)
    )

    class MorningKickoff(confidence: Float = 0.95f) : WorkflowIntent(
        intentName = "Planejamento Matinal",
        confidence = confidence,
        suggestedCategory = "Produtividade",
        suggestedTrigger = Trigger.Time(8, 0),
        suggestedActions = listOf(Action.SyncSupabase, Action.AskGemini, Action.CreateTask, Action.CalculateLifeScore)
    )

    class FocusSession(confidence: Float = 0.92f) : WorkflowIntent(
        intentName = "Sessão de Foco",
        confidence = confidence,
        suggestedCategory = "Foco",
        suggestedTrigger = Trigger.Manual,
        suggestedActions = listOf(Action.StartFocus, Action.SendNotification)
    )

    class NightlyReview(confidence: Float = 0.94f) : WorkflowIntent(
        intentName = "Revisão e Fechamento Noturno",
        confidence = confidence,
        suggestedCategory = "Hábitos",
        suggestedTrigger = Trigger.BedTime,
        suggestedActions = listOf(Action.GenerateWeeklyReview, Action.CalculateLifeScore, Action.SendNotification)
    )

    class TaskCreation(confidence: Float = 0.88f) : WorkflowIntent(
        intentName = "Criação Automatizada de Tarefas",
        confidence = confidence,
        suggestedCategory = "Tarefas",
        suggestedTrigger = Trigger.Manual,
        suggestedActions = listOf(Action.CreateTask, Action.SendNotification)
    )

    class LifeScoreRecalculation(confidence: Float = 0.90f) : WorkflowIntent(
        intentName = "Recálculo do Life Score",
        confidence = confidence,
        suggestedCategory = "Analytics",
        suggestedTrigger = Trigger.AppOpened,
        suggestedActions = listOf(Action.CalculateLifeScore, Action.AskGemini)
    )

    class CloudSyncAndInsights(confidence: Float = 0.91f) : WorkflowIntent(
        intentName = "Sincronização em Nuvem e IA",
        confidence = confidence,
        suggestedCategory = "Sistema",
        suggestedTrigger = Trigger.AppOpened,
        suggestedActions = listOf(Action.SyncSupabase, Action.AskGemini, Action.GenerateMission)
    )

    class MissionGeneration(confidence: Float = 0.89f) : WorkflowIntent(
        intentName = "Geração de Missões Inteligentes",
        confidence = confidence,
        suggestedCategory = "Missões",
        suggestedTrigger = Trigger.WakeUp,
        suggestedActions = listOf(Action.GenerateMission, Action.CreateTask, Action.CalculateLifeScore)
    )

    class CustomWorkflow(
        intentName: String,
        confidence: Float = 0.85f,
        category: String = "Personalizado",
        trigger: Trigger = Trigger.Manual,
        actions: List<Action> = listOf(Action.AskGemini, Action.CalculateLifeScore)
    ) : WorkflowIntent(intentName, confidence, category, trigger, actions)
}

/**
 * Intent Detector for analyzing user messages (Mensagem)
 * and extracting workflow structures, triggers, and action intents.
 */
class WorkflowIntentDetector {

    fun detect(message: String): WorkflowIntent {
        val text = message.trim().lowercase()

        return when {
            // TaskCompleted -> StartFocus -> Atualizar Dashboard -> Recalcular Score
            text.contains("taskcompleted") || text.contains("task completed") ||
            (text.contains("task") && (text.contains("focus") || text.contains("foco")) && (text.contains("dashboard") || text.contains("score"))) ||
            (text.contains("tarefa") && (text.contains("foco") || text.contains("focus")) && (text.contains("dashboard") || text.contains("score"))) ||
            (text.contains("startfocus") && (text.contains("dashboard") || text.contains("score"))) -> {
                WorkflowIntent.TaskCompletionFocusFlow()
            }

            // Morning kickoff routine
            text.contains("manhã") || text.contains("matinal") || text.contains("morning") || 
            (text.contains("acordar") && (text.contains("rotina") || text.contains("planejar"))) -> {
                WorkflowIntent.MorningKickoff()
            }

            // Focus session
            text.contains("foco") || text.contains("focus") || text.contains("pomodoro") || 
            text.contains("imersivo") || text.contains("concentração") -> {
                WorkflowIntent.FocusSession()
            }

            // Nightly review & habits
            text.contains("noite") || text.contains("noturno") || text.contains("dormir") || 
            text.contains("bedtime") || text.contains("fechamento") || text.contains("revisão") -> {
                WorkflowIntent.NightlyReview()
            }

            // Life score update
            text.contains("score") || text.contains("pontuação") || text.contains("calcular") -> {
                WorkflowIntent.LifeScoreRecalculation()
            }

            // Mission generation
            text.contains("missão") || text.contains("mission") || text.contains("objetivo") || text.contains("meta") -> {
                WorkflowIntent.MissionGeneration()
            }

            // Cloud sync & Gemini AI
            text.contains("sync") || text.contains("sincronizar") || text.contains("supabase") || text.contains("gemini") -> {
                WorkflowIntent.CloudSyncAndInsights()
            }

            // Task creation
            text.contains("tarefa") || text.contains("task") || text.contains("afazer") -> {
                WorkflowIntent.TaskCreation()
            }

            // Dynamic custom workflow detection
            else -> {
                val extractedActions = mutableListOf<Action>()
                if (text.contains("ia") || text.contains("ai") || text.contains("perguntar") || text.contains("dica")) {
                    extractedActions.add(Action.AskGemini)
                }
                if (text.contains("notificar") || text.contains("lembrete") || text.contains("aviso")) {
                    extractedActions.add(Action.SendNotification)
                }
                if (text.contains("missão") || text.contains("plano")) {
                    extractedActions.add(Action.GenerateMission)
                }
                if (extractedActions.isEmpty()) {
                    extractedActions.addAll(listOf(Action.AskGemini, Action.CalculateLifeScore))
                }

                val detectedTrigger = when {
                    text.contains("ao acordar") || text.contains("acordo") -> Trigger.WakeUp
                    text.contains("ao dormir") || text.contains("deitar") -> Trigger.BedTime
                    text.contains("ao abrir") || text.contains("abertura") -> Trigger.AppOpened
                    else -> Trigger.Manual
                }

                val customTitle = if (message.isNotBlank() && message.length <= 40) {
                    message.replaceFirstChar { it.uppercase() }
                } else {
                    "Workflow Automatizado LifeOS"
                }

                WorkflowIntent.CustomWorkflow(
                    intentName = customTitle,
                    confidence = 0.82f,
                    trigger = detectedTrigger,
                    actions = extractedActions
                )
            }
        }
    }
}
