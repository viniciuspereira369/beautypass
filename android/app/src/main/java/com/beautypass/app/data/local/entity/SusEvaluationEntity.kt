package com.beautypass.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beautypass.app.data.local.Converters
import com.beautypass.app.model.SUSEvaluation

/**
 * Entidade Room para persistência das avaliações SUS (System Usability Scale).
 * Tabela: sus_evaluations
 */
@Entity(tableName = "sus_evaluations")
data class SusEvaluationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "participant_id")
    val participantId: String,

    @ColumnInfo(name = "answers_serialized")
    val answersSerialized: String,

    @ColumnInfo(name = "score")
    val score: Double,

    @ColumnInfo(name = "retention_yes")
    val retentionYes: Boolean,

    @ColumnInfo(name = "evaluated_at_iso")
    val evaluatedAtIso: String
) {
    fun toDomain(): SUSEvaluation {
        return SUSEvaluation(
            participantCode = participantId,
            answers = Converters.deserializeAnswersMap(answersSerialized),
            susScore = score,
            retentionYes = retentionYes,
            evaluatedAtIso = evaluatedAtIso
        )
    }

    companion object {
        fun fromDomain(domain: SUSEvaluation): SusEvaluationEntity {
            return SusEvaluationEntity(
                participantId = domain.participantCode,
                answersSerialized = Converters.serializeAnswersMap(domain.answers),
                score = domain.susScore,
                retentionYes = domain.retentionYes,
                evaluatedAtIso = domain.evaluatedAtIso
            )
        }
    }
}
