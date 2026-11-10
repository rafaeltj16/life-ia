package com.lifeos.core.command

sealed interface LifeCommand

data class CreateTask(
    val title: String
) : LifeCommand

data class StartFocus(
    val taskId: String
) : LifeCommand

object GenerateDailyMission : LifeCommand
