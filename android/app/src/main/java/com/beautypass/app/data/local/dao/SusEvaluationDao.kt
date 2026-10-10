package com.beautypass.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beautypass.app.data.local.entity.SusEvaluationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para avaliações SUS no SQLite.
 */
@Dao
interface SusEvaluationDao {

    @Query("SELECT * FROM sus_evaluations ORDER BY id DESC")
    fun getAllEvaluations(): Flow<List<SusEvaluationEntity>>

    @Query("SELECT * FROM sus_evaluations WHERE participant_id = :participantId ORDER BY id DESC LIMIT 1")
    fun getEvaluationByParticipant(participantId: String): Flow<SusEvaluationEntity?>

    @Query("SELECT * FROM sus_evaluations ORDER BY id DESC LIMIT 1")
    fun getLatestEvaluation(): Flow<SusEvaluationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(evaluation: SusEvaluationEntity): Long

    @Query("DELETE FROM sus_evaluations")
    suspend fun deleteAllEvaluations()
}
