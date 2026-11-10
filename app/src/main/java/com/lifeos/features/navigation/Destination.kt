package com.lifeos.features.navigation

sealed class Destination(
    val route: String
) {
    object Dashboard : Destination("dashboard")
    object Habits : Destination("habits")
    object Tasks : Destination("tasks")
}
