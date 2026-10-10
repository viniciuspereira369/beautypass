package com.beautypass.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.beautypass.app.data.local.entity.AppointmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operações reativas e assíncronas de agendamentos no SQLite.
 */
@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointments ORDER BY booked_at_iso DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE salon_id = :salonId ORDER BY booked_at_iso DESC")
    fun getAppointmentsBySalon(salonId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE date_display = :dateDisplay ORDER BY time_slot ASC")
    fun getAppointmentsByDate(dateDisplay: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun getAppointmentById(id: String): AppointmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :status, cancellation_reason = :reason, cancellation_fee = :fee WHERE id = :id")
    suspend fun cancelAppointment(
        id: String,
        status: String = "CANCELLED_BY_USER",
        reason: String? = null,
        fee: Double? = null
    )

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointmentById(id: String)

    @Query("DELETE FROM appointments")
    suspend fun deleteAllAppointments()
}
