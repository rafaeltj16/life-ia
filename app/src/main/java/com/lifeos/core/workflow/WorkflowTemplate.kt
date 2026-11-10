package com.lifeos.core.workflow

import java.time.Instant
import java.util.UUID

enum class WorkflowStatus {
    DRAFT,
    ACTIVE,
    PAUSED,
    ARCHIVED
}

enum class StepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    SKIPPED
}

enum class WorkflowActionType {
    CREATE_TASK,
    COMPLETE_TASK,
    START_FOCUS_SESSION,
    UPDATE_LIFE_SCORE,
    SEND_NOTIFICATION,
    SYNC_CALENDAR,
    LOG_HABIT,
    GENERATE_AI_INSIGHT,
    TRIGGER_AUTOMATION,
    CUSTOM_SCRIPT
}

sealed class Trigger {
    object AppOpened : Trigger()
    object WakeUp : Trigger()
    object BedTime : Trigger()

    data class HabitCompleted(
        val habitId: String
    ) : Trigger()

    data class TaskCompleted(
        val taskId: String
    ) : Trigger()

    data class GoalCompleted(
        val goalId: String
    ) : Trigger()

    data class Time(
        val hour: Int,
        val minute: Int = 0
    ) : Trigger()

    data class Schedule(val cronOrTime: String, val daysOfWeek: List<Int> = emptyList()) : Trigger()
    data class Event(val eventName: String, val entityId: String? = null) : Trigger()
    object Manual : Trigger()
    object OnAppLaunch : Trigger()
    object OnGoalProgress : Trigger()
}

typealias WorkflowTrigger = Trigger

data class WorkflowCondition(
    val field: String,
    val operator: String, // "EQUALS", "GREATER_THAN", "CONTAINS", "IS_TRUE"
    val value: String
)

sealed class Action {
    object CalculateLifeScore : Action()
    object GenerateMission : Action()
    object StartFocus : Action()
    object CreateTask : Action()
    object SendNotification : Action()
    object GenerateWeeklyReview : Action()
    object SyncSupabase : Action()
    object AskGemini : Action()
    object UpdateDashboard : Action()
}

typealias WorkflowStep = Action

data class Workflow(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val trigger: Trigger = Trigger.Manual,
    val actions: List<Action> = emptyList(),
    val enabled: Boolean = true,
    val description: String = "",
    val category: String = "Geral",
    val status: WorkflowStatus = WorkflowStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val metadata: Map<String, String> = emptyMap()
) {
    val steps: List<Action> get() = actions
    val isEnabled: Boolean get() = enabled
}

data class WorkflowTemplate(
    val templateId: String,
    val title: String,
    val description: String,
    val category: String,
    val defaultWorkflow: Workflow
) {
    companion object {
        fun getPredefinedTemplates(): List<WorkflowTemplate> = listOf(
            WorkflowTemplate(
                templateId = "tmpl_task_focus_dashboard_score",
                title = "Fluxo Foco & Recálculo de Score",
                description = "Ao concluir uma tarefa: Inicia Foco, Atualiza o Dashboard e Recalcula o Life Score",
                category = "Produtividade",
                defaultWorkflow = Workflow(
                    id = "wf_task_focus_dashboard_score",
                    name = "TaskCompleted → Focus → Dashboard → Score",
                    description = "Gatilho ao concluir tarefa: Inicia Foco, Atualiza Dashboard e Recalcula Life Score",
                    category = "Produtividade",
                    trigger = Trigger.TaskCompleted("current_task"),
                    actions = listOf(
                        Action.StartFocus,
                        Action.UpdateDashboard,
                        Action.CalculateLifeScore
                    ),
                    enabled = true
                )
            ),
            WorkflowTemplate(
                templateId = "tmpl_morning_kickoff",
                title = "Planejamento Matinal",
                description = "Gera missões do dia, sincroniza calendário e inicia com Life Score atualizado",
                category = "Produtividade",
                defaultWorkflow = Workflow(
                    id = "wf_morning_kickoff",
                    name = "Planejamento Matinal",
                    description = "Executa a rotina matinal de produtividade",
                    category = "Produtividade",
                    trigger = Trigger.Schedule("08:00"),
                    actions = listOf(
                        Action.SyncSupabase,
                        Action.AskGemini,
                        Action.CreateTask,
                        Action.CalculateLifeScore
                    ),
                    enabled = true
                )
            ),
            WorkflowTemplate(
                templateId = "tmpl_deep_focus",
                title = "Sessão de Foco Imersivo",
                description = "Inicia cronômetro pomodoro, ativa não perturbe e silencia notificações",
                category = "Foco",
                defaultWorkflow = Workflow(
                    id = "wf_deep_focus",
                    name = "Sessão de Foco Imersivo",
                    description = "Workflow para entrada rápida no estado de fluxo",
                    category = "Foco",
                    trigger = Trigger.Manual,
                    actions = listOf(
                        Action.StartFocus,
                        Action.SendNotification
                    ),
                    enabled = true
                )
            ),
            WorkflowTemplate(
                templateId = "tmpl_evening_reflection",
                title = "Revisão e Fechamento Noturno",
                description = "Registra hábitos concluídos, verifica metas e prepara o dia seguinte",
                category = "Hábitos",
                defaultWorkflow = Workflow(
                    id = "wf_evening_reflection",
                    name = "Revisão e Fechamento Noturno",
                    description = "Workflow para fechamento diário e consistência",
                    category = "Hábitos",
                    trigger = Trigger.Schedule("21:30"),
                    actions = listOf(
                        Action.GenerateWeeklyReview,
                        Action.CalculateLifeScore,
                        Action.SendNotification
                    ),
                    enabled = true
                )
            )
        )
    }
}
