package com.beautypass.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beautypass.app.model.Appointment
import com.beautypass.app.model.AppointmentStatus
import com.beautypass.app.model.Salon
import com.beautypass.app.model.Service
import com.beautypass.app.model.Staff

/**
 * Entidade Room para persistência de agendamentos no SQLite.
 * Tabela: appointments
 */
@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "salon_id", index = true)
    val salonId: String,

    @ColumnInfo(name = "service_id")
    val serviceId: String,

    @ColumnInfo(name = "staff_id")
    val staffId: String? = null,

    @ColumnInfo(name = "date_display")
    val dateDisplay: String,

    @ColumnInfo(name = "time_slot")
    val timeSlot: String,

    @ColumnInfo(name = "final_price")
    val finalPrice: Double,

    @ColumnInfo(name = "status")
    val status: AppointmentStatus = AppointmentStatus.CONFIRMED,

    @ColumnInfo(name = "booked_at_iso")
    val bookedAtIso: String,

    @ColumnInfo(name = "cancellation_reason")
    val cancellationReason: String? = null,

    @ColumnInfo(name = "cancellation_fee")
    val cancellationFee: Double? = null
) {
    /**
     * Mapeador para o modelo de domínio Appointment resolvendo referências do catálogo determinístico.
     */
    fun toDomain(salon: Salon, service: Service, staff: Staff? = null): Appointment {
        return Appointment(
            id = id,
            salon = salon,
            service = service,
            staff = staff,
            dateDisplay = dateDisplay,
            timeSlot = timeSlot,
            finalPrice = finalPrice,
            status = status,
            bookedAtIso = bookedAtIso,
            voucherQrCode = id,
            walkingTimeMin = salon.walkTimeMinutes,
            cancellationReason = cancellationReason,
            cancellationFee = cancellationFee
        )
    }

    companion object {
        fun fromDomain(appointment: Appointment): AppointmentEntity {
            return AppointmentEntity(
                id = appointment.id,
                salonId = appointment.salon.id,
                serviceId = appointment.service.id,
                staffId = appointment.staff?.id,
                dateDisplay = appointment.dateDisplay,
                timeSlot = appointment.timeSlot,
                finalPrice = appointment.finalPrice,
                status = appointment.status,
                bookedAtIso = appointment.bookedAtIso,
                cancellationReason = appointment.cancellationReason,
                cancellationFee = appointment.cancellationFee
            )
        }
    }
}
