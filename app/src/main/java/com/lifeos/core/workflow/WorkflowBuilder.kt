package com.lifeos.core.workflow

import java.time.Instant
import java.util.UUID

/**
 * WorkflowBuilder implements the Builder pattern and Kotlin DSL for constructing
 * strongly-typed [Workflow] instances within the LifeOS execution pipeline.
 */
class WorkflowBuilder {
    private var id: String = UUID.randomUUID().toString()
    private var name: String = "Novo Workflow"
    private var description: String = ""
    private var category: String = "Geral"
    private var trigger: Trigger = Trigger.Manual
    private val actions: MutableList<Action> = mutableListOf()
    private var enabled: Boolean = true
    private var status: WorkflowStatus = WorkflowStatus.ACTIVE
    private val metadata: MutableMap<String, String> = mutableMapOf()

    fun id(id: String) = apply { this.id = id }
    fun name(name: String) = apply { this.name = name }
    fun description(description: String) = apply { this.description = description }
    fun category(category: String) = apply { this.category = category }
    fun trigger(trigger: Trigger) = apply { this.trigger = trigger }
    fun enabled(enabled: Boolean) = apply { this.enabled = enabled }
    fun status(status: WorkflowStatus) = apply { this.status = status }

    fun addAction(action: Action) = apply { this.actions.add(action) }
    fun addActions(vararg actions: Action) = apply { this.actions.addAll(actions) }
    fun addActions(actions: List<Action>) = apply { this.actions.addAll(actions) }
    fun clearActions() = apply { this.actions.clear() }

    fun addMetadata(key: String, value: String) = apply { this.metadata[key] = value }
    fun metadata(metadata: Map<String, String>) = apply { 
        this.metadata.clear()
        this.metadata.putAll(metadata)
    }

    // DSL / Helper methods for actions
    fun calculateLifeScore() = addAction(Action.CalculateLifeScore)
    fun generateMission() = addAction(Action.GenerateMission)
    fun startFocus() = addAction(Action.StartFocus)
    fun createTask() = addAction(Action.CreateTask)
    fun sendNotification() = addAction(Action.SendNotification)
    fun generateWeeklyReview() = addAction(Action.GenerateWeeklyReview)
    fun syncSupabase() = addAction(Action.SyncSupabase)
    fun askGemini() = addAction(Action.AskGemini)
    fun updateDashboard() = addAction(Action.UpdateDashboard)

    // Trigger helper methods
    fun triggeredBy(trigger: Trigger) = apply { this.trigger = trigger }
    fun triggeredOnAppOpened() = apply { this.trigger = Trigger.AppOpened }
    fun triggeredOnWakeUp() = apply { this.trigger = Trigger.WakeUp }
    fun triggeredOnBedTime() = apply { this.trigger = Trigger.BedTime }
    fun triggeredAtTime(hour: Int, minute: Int = 0) = apply { this.trigger = Trigger.Time(hour, minute) }
    fun triggeredByHabit(habitId: String) = apply { this.trigger = Trigger.HabitCompleted(habitId) }
    fun triggeredByTask(taskId: String) = apply { this.trigger = Trigger.TaskCompleted(taskId) }
    fun triggeredByGoal(goalId: String) = apply { this.trigger = Trigger.GoalCompleted(goalId) }

    fun build(): Workflow {
        return Workflow(
            id = id,
            name = name,
            description = description,
            category = category,
            trigger = trigger,
            actions = actions.toList(),
            enabled = enabled,
            status = status,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            metadata = metadata.toMap()
        )
    }

    companion object {
        fun create(): WorkflowBuilder = WorkflowBuilder()

        /**
         * Kotlin DSL entry point for building a Workflow
         */
        inline fun buildWorkflow(name: String = "Novo Workflow", block: WorkflowBuilder.() -> Unit): Workflow {
            return WorkflowBuilder().apply {
                name(name)
                block()
            }.build()
        }

        fun fromTemplate(template: WorkflowTemplate): WorkflowBuilder {
            val base = template.defaultWorkflow
            return WorkflowBuilder().apply {
                id(UUID.randomUUID().toString())
                name(base.name)
                description(base.description)
                category(base.category)
                trigger(base.trigger)
                addActions(base.actions)
                enabled(base.enabled)
                status(base.status)
                metadata(base.metadata)
            }
        }
    }
}
