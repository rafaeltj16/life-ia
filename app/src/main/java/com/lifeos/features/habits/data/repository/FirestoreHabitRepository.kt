package com.lifeos.features.habits.data.repository

import android.util.Log
import com.example.core.common.HabitItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class FirestoreHabitRepository {
    private val tag = "FirestoreHabitRepo"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: IllegalStateException) {
            Log.w(tag, "FirebaseApp is not initialized: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(tag, "Firestore initialization error: ${e.message}")
            null
        }
    }

    fun observeHabits(onSuccess: (List<HabitItem>) -> Unit, onError: (Exception) -> Unit = {}): ListenerRegistration? {
        val db = firestore ?: run {
            Log.w(tag, "Firestore is not available")
            return null
        }

        return try {
            db.collection("habits")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(tag, "Error observing Firestore habits", error)
                        onError(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val habitList = snapshot.documents.mapNotNull { doc ->
                            try {
                                val id = doc.getString("id") ?: doc.id
                                val name = doc.getString("name") ?: return@mapNotNull null
                                val streak = (doc.getLong("streak") ?: 0L).toInt()
                                val isCompletedToday = doc.getBoolean("isCompletedToday") ?: false
                                val icon = doc.getString("icon") ?: "⚡"
                                val category = doc.getString("category") ?: "Geral"
                                val targetDaysPerWeek = (doc.getLong("targetDaysPerWeek") ?: 7L).toInt()
                                val completedThisWeek = (doc.getLong("completedThisWeek") ?: 0L).toInt()

                                HabitItem(
                                    id = id,
                                    name = name,
                                    streak = streak,
                                    isCompletedToday = isCompletedToday,
                                    icon = icon,
                                    category = category,
                                    targetDaysPerWeek = targetDaysPerWeek,
                                    completedThisWeek = completedThisWeek,
                                    isFirestoreSynced = true
                                )
                            } catch (e: Exception) {
                                Log.e(tag, "Error parsing habit doc", e)
                                null
                            }
                        }
                        if (habitList.isNotEmpty()) {
                            onSuccess(habitList)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(tag, "Failed to attach snapshot listener", e)
            onError(e)
            null
        }
    }

    suspend fun saveHabit(habit: HabitItem): Boolean {
        val db = firestore ?: return false
        return try {
            val habitMap = mapOf(
                "id" to habit.id,
                "name" to habit.name,
                "streak" to habit.streak,
                "isCompletedToday" to habit.isCompletedToday,
                "icon" to habit.icon,
                "category" to habit.category,
                "targetDaysPerWeek" to habit.targetDaysPerWeek,
                "completedThisWeek" to habit.completedThisWeek,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("habits").document(habit.id).set(habitMap).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Error saving habit to Firestore: ${e.message}")
            false
        }
    }

    suspend fun deleteHabit(habitId: String): Boolean {
        val db = firestore ?: return false
        return try {
            db.collection("habits").document(habitId).delete().await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Error deleting habit from Firestore: ${e.message}")
            false
        }
    }
}
