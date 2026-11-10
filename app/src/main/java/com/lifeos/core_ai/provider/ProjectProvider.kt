package com.lifeos.core_ai.provider

import com.lifeos.projects.domain.model.Project

interface ProjectProvider {

    suspend fun getActiveProjects(): List<Project>

    suspend fun getInactiveProjects(): List<Project>

}
