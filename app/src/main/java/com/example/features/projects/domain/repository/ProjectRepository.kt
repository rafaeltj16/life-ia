package com.example.features.projects.domain.repository

import com.example.core.common.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getProjectsFlow(): Flow<List<Project>>
    suspend fun getProjects(userId: String): List<Project>
    suspend fun saveProject(project: Project)
    suspend fun deleteProject(userId: String, projectId: String)
    suspend fun syncWithRemote(userId: String)
}
