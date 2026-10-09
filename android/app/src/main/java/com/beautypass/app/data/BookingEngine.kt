package com.beautypass.app.data

import com.beautypass.app.model.Appointment
import com.beautypass.app.model.AppointmentStatus
import com.beautypass.app.model.BookingValidationResult
import kotlin.math.ceil

// =====================================================================
// MOTOR DE REGRAS DE AGENDAMENTO BEAUTYPASS
// Blocos de 15 min, buffer de 10 min de higienização e prevenção de double-booking
// =====================================================================

object BookingEngine {

    const val SANITIZATION_BUFFER_MINUTES = 10
    const val TIME_SLOT_INTERVAL_MINUTES = 15

    /**
     * Valida se a string de horário respeita estritamente blocos de 15 minutos (00, 15, 30, 45).
     */
    fun isValid15MinuteBlock(time: String): Boolean {
        val parts = time.trim().split(":")
        if (parts.size != 2) return false
        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false

        if (hour !in 0..23) return false
        return minute == 0 || minute == 15 || minute == 30 || minute == 45
    }

    /**
     * Converte horário no formato "HH:mm" para minutos a partir de 00:00.
     */
    fun timeToMinutes(time: String): Int {
        val parts = time.trim().split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return hour * 60 + minute
    }

    /**
     * Converte minutos a partir de 00:00 para string no formato "HH:mm".
     */
    fun minutesToTime(minutes: Int): String {
        val h = (minutes / 60) % 24
        val m = minutes % 60
        return String.format("%02d:%02d", h, m)
    }

    /**
     * Calcula quantos slots de 15 minutos um serviço consome somando o buffer de 10 minutos.
     */
    fun calculateSlotsUsed(durationMinutes: Int, bufferMinutes: Int = SANITIZATION_BUFFER_MINUTES): Int {
        val totalTime = durationMinutes + bufferMinutes
        return ceil(totalTime.toDouble() / TIME_SLOT_INTERVAL_MINUTES.toDouble()).toInt()
    }

    /**
     * Valida se um horário pretendido pode ser agendado sem violar as regras de negócio:
     * 1. Alinhamento a blocos de 15 minutos.
     * 2. Duração válida do serviço.
     * 3. Prevenção estrita de double-booking considerando o buffer de higienização de 10 min.
     */
    fun validateBookingSlot(
        slotTime: String,
        serviceDurationMinutes: Int,
        existingAppointments: List<Appointment>,
        staffId: String? = null,
        dateDisplay: String? = null,
        bufferMinutes: Int = SANITIZATION_BUFFER_MINUTES
    ): BookingValidationResult {
        if (!isValid15MinuteBlock(slotTime)) {
            return BookingValidationResult(
                isValid = false,
                reason = "Horário deve respeitar blocos de 15 minutos (ex: 10:00, 10:15, 10:30)."
            )
        }

        if (serviceDurationMinutes <= 0) {
            return BookingValidationResult(
                isValid = false,
                reason = "Duração do serviço deve ser maior que zero."
            )
        }

        val candidateStart = timeToMinutes(slotTime)
        val candidateEnd = candidateStart + serviceDurationMinutes + bufferMinutes

        // Filtra apenas agendamentos ativos na mesma data e com o mesmo profissional (se especificados)
        val activeAppointments = existingAppointments.filter { appt ->
            val isActive = appt.status == AppointmentStatus.CONFIRMED || appt.status == AppointmentStatus.IN_PROGRESS
            val isSameDate = dateDisplay == null || appt.dateDisplay == dateDisplay
            val isSameStaff = staffId == null || appt.staff == null || appt.staff.id == staffId || appt.staff.id == "any"
            isActive && isSameDate && isSameStaff
        }

        for (existing in activeAppointments) {
            val existingStart = timeToMinutes(existing.timeSlot)
            val existingEnd = existingStart + existing.service.durationMinutes + bufferMinutes

            // Condição de sobreposição entre intervalos semiabertos [Start, End):
            val overlaps = candidateStart < existingEnd && existingStart < candidateEnd
            if (overlaps) {
                return BookingValidationResult(
                    isValid = false,
                    reason = "Conflito de agenda: profissional indisponível (buffer de higienização de 10 min exigido entre atendimentos).",
                    conflictAppointmentId = existing.id
                )
            }
        }

        return BookingValidationResult(
            isValid = true,
            reason = null,
            conflictAppointmentId = null
        )
    }

    /**
     * Sobrecarga sem filtros opcionais de data/staff para chamadas diretas simples.
     */
    fun validateBookingSlot(
        slotTime: String,
        serviceDurationMinutes: Int,
        existingAppointments: List<Appointment>
    ): BookingValidationResult {
        return validateBookingSlot(
            slotTime = slotTime,
            serviceDurationMinutes = serviceDurationMinutes,
            existingAppointments = existingAppointments,
            staffId = null,
            dateDisplay = null,
            bufferMinutes = SANITIZATION_BUFFER_MINUTES
        )
    }

    /**
     * Verifica se há double-booking direto (retorna booleano).
     */
    fun isDoubleBooking(
        slotTime: String,
        serviceDurationMinutes: Int,
        existingAppointments: List<Appointment>,
        staffId: String? = null
    ): Boolean {
        val result = validateBookingSlot(
            slotTime = slotTime,
            serviceDurationMinutes = serviceDurationMinutes,
            existingAppointments = existingAppointments,
            staffId = staffId
        )
        return !result.isValid
    }

    /**
     * Calcula a taxa de retenção para cancelamento tardio (< 24h antes do horário).
     * Conforme regra de negócio da Seção 6 e R1.6: 30% do valor final agendado.
     */
    fun calculateCancellationRetentionFee(finalPrice: Double, isUnder24Hours: Boolean = true): Double {
        if (!isUnder24Hours) return 0.0
        return kotlin.math.round(finalPrice * 0.30 * 100.0) / 100.0
    }
}

