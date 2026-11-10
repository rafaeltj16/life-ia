package com.example.features.projects.data.repository

import timber.log.Timber
import com.example.core.common.Project
import com.example.core.database.ProjectDao
import com.example.core.database.ProjectEntity
import com.example.features.projects.domain.repository.ProjectRepository
import com.example.core.network.SupabaseProject
import com.example.core.network.SupabaseServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao,
    private val context: android.content.Context
) : ProjectRepository {

    private val TAG = "ProjectRepositoryImpl"

    private fun mapToDomain(entity: ProjectEntity): Project {
        return Project(
            id = entity.id,
            name = entity.name,
            color = entity.color,
            userId = entity.userId
        )
    }

    override fun getProjectsFlow(): Flow<List<Project>> {
        return projectDao.getProjectsFlow().map { list ->
            list.map { mapToDomain(it) }
        }
    }

    override suspend fun getProjects(userId: String): List<Project> = withContext(Dispatchers.IO) {
        val local = projectDao.getProjects(userId)
        if (local.isNotEmpty()) {
            return@withContext local.map { mapToDomain(it) }
        }

        try {
            syncWithRemote(userId)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed remote sync on getProjects")
        }
        return@withContext projectDao.getProjects(userId).map { mapToDomain(it) }
    }

    override suspend fun saveProject(project: Project): Unit = withContext(Dispatchers.IO) {
        // 1. Save to local Room database immediately
        val entity = ProjectEntity(
            id = project.id,
            name = project.name,
            color = project.color,
            userId = project.userId
        )
        projectDao.insertProject(entity)

        // 2. Sync via SyncQueueProcessor
        val sProject = SupabaseProject(
            id = project.id,
            name = project.name,
            color = project.color,
            userId = project.userId
        )
        com.example.services.sync.SyncQueueProcessor.getInstance(context).enqueue(
            userId = project.userId ?: "",
            action = com.example.services.sync.SyncQueueProcessor.ACTION_SAVE_PROJECT,
            payloadObj = sProject
        )
    }

    override suspend fun deleteProject(userId: String, projectId: String): Unit = withContext(Dispatchers.IO) {
        // 1. Delete locally from Room
        projectDao.deleteProject(projectId)

        // 2. Sync via SyncQueueProcessor
        com.example.services.sync.SyncQueueProcessor.getInstance(context).enqueue(
            userId = userId,
            action = com.example.services.sync.SyncQueueProcessor.ACTION_DELETE_PROJECT,
            payloadObj = projectId
        )
    }

    override suspend fun syncWithRemote(userId: String): Unit = withContext(Dispatchers.IO) {
        try {
            val supabaseProjects = SupabaseServiceClient.getProjects(userId)
            val entities = supabaseProjects.map { sProject ->
                ProjectEntity(
                    id = sProject.id,
                    name = sProject.name,
                    color = sProject.color,
                    userId = sProject.userId
                )
            }

            // Perform transaction/caching update
            projectDao.clearProjects()
            if (entities.isNotEmpty()) {
                projectDao.insertProjects(entities)
            }
            Timber.tag(TAG).i("Successfully synced ${entities.size} projects from Supabase to Room.")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed syncWithRemote for projects")
        }
    }
}

