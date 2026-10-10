package com.beautypass.app.data.local

import androidx.room.TypeConverter
import com.beautypass.app.model.AppointmentStatus

/**
 * Conversores de tipo do Room para serialização determinística e bidirecional de dados.
 * Converte Enums e Mapas sem dependências externas (sem Gson/Jackson/Moshi).
 */
class Converters {

    @TypeConverter
    fun fromAppointmentStatus(status: AppointmentStatus?): String {
        return status?.name ?: AppointmentStatus.CONFIRMED.name
    }

    @TypeConverter
    fun toAppointmentStatus(value: String?): AppointmentStatus {
        if (value.isNullOrBlank()) return AppointmentStatus.CONFIRMED
        return try {
            AppointmentStatus.valueOf(value)
        } catch (_: Exception) {
            AppointmentStatus.CONFIRMED
        }
    }

    @TypeConverter
    fun fromAnswersMap(answers: Map<Int, Int>?): String {
        return serializeAnswersMap(answers ?: emptyMap())
    }

    @TypeConverter
    fun toAnswersMap(value: String?): Map<Int, Int> {
        return deserializeAnswersMap(value)
    }

    companion object {
        /**
         * Serializa mapa de respostas SUS (pergunta -> nota) em formato delimitado "1:5;2:4;3:5".
         */
        fun serializeAnswersMap(answers: Map<Int, Int>): String {
            if (answers.isEmpty()) return ""
            return answers.entries
                .sortedBy { it.key }
                .joinToString(separator = ";") { "${it.key}:${it.value}" }
        }

        /**
         * Desserializa string delimitada de volta em Map<Int, Int>.
         */
        fun deserializeAnswersMap(serialized: String?): Map<Int, Int> {
            if (serialized.isNullOrBlank()) return emptyMap()
            val result = mutableMapOf<Int, Int>()
            val pairs = serialized.split(";")
            for (pair in pairs) {
                val parts = pair.split(":")
                if (parts.size == 2) {
                    val k = parts[0].trim().toIntOrNull()
                    val v = parts[1].trim().toIntOrNull()
                    if (k != null && v != null) {
                        result[k] = v
                    }
                }
            }
            return result
        }
    }
}
