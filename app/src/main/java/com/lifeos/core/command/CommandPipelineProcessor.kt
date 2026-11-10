package com.lifeos.core.command

import java.util.UUID

sealed interface CommandResult {
    data class Success(val message: String) : CommandResult
    data class Error(val reason: String) : CommandResult
}

class PermissionChecker {
    fun hasPermission(command: LifeCommand): Boolean {
        // All default commands have permission in local user context
        return true
    }
}

class CommandValidator {
    fun validate(command: LifeCommand): Boolean {
        return when (command) {
            is CreateTask -> command.title.isNotBlank()
            is StartFocus -> command.taskId.isNotBlank()
            is GenerateDailyMission -> true
        }
    }
}

class CommandExecutor {
    fun execute(command: LifeCommand): CommandResult {
        return when (command) {
            is CreateTask -> CommandResult.Success("Task created: ${command.title}")
            is StartFocus -> CommandResult.Success("Focus session started for task: ${command.taskId}")
            is GenerateDailyMission -> CommandResult.Success("Daily mission generated")
        }
    }
}

class AuditLogger {
    private val logs = mutableListOf<String>()

    fun log(command: LifeCommand, result: CommandResult) {
        val entry = "[AUDIT] [${System.currentTimeMillis()}] Executed $command -> $result"
        logs.add(entry)
    }

    fun getLogs(): List<String> = logs.toList()
}

class CommandSync {
    fun sync(command: LifeCommand, result: CommandResult) {
        // Syncs command state changes with local & remote bus
    }
}

class CommandPipelineProcessor(
    private val permissionChecker: PermissionChecker = PermissionChecker(),
    private val validator: CommandValidator = CommandValidator(),
    private val executor: CommandExecutor = CommandExecutor(),
    private val auditLogger: AuditLogger = AuditLogger(),
    private val commandSync: CommandSync = CommandSync()
) {
    fun processCommand(command: LifeCommand): CommandResult {
        // 1. Permission Checker
        if (!permissionChecker.hasPermission(command)) {
            val error = CommandResult.Error("Permission denied for command: $command")
            auditLogger.log(command, error)
            return error
        }

        // 2. Validator
        if (!validator.validate(command)) {
            val error = CommandResult.Error("Validation failed for command: $command")
            auditLogger.log(command, error)
            return error
        }

        // 3. Executor
        val result = executor.execute(command)

        // 4. Audit Log
        auditLogger.log(command, result)

        // 5. Sync
        commandSync.sync(command, result)

        return result
    }
}
