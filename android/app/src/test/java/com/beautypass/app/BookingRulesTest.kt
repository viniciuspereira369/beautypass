package com.beautypass.app

import com.beautypass.app.data.BookingEngine
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Appointment
import com.beautypass.app.model.AppointmentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Suíte de Testes Unitários para as Regras de Agendamento (BookingEngine).
 *
 * Valida rigorosamente:
 * 1. Blocos de 15 minutos (00, 15, 30, 45 como válidos; outros horários como inválidos).
 * 2. Buffer obrigatório de higienização de 10 minutos entre atendimentos consecutivos.
 * 3. Prevenção estrita de double-booking no mesmo horário para o mesmo profissional.
 * 4. Isolamento de agendas entre profissionais distintos.
 * 5. Liberação de slots para agendamentos cancelados.
 */
class BookingRulesTest {

    private val testSalon = SalonRepository.salons.first()
    private val testService = testSalon.services.first() // Escova Modeladora (45 min)
    private val staff1 = testSalon.staff[0] // Juliana (st1)
    private val staff2 = testSalon.staff[1] // Rodrigo (st2)

    // =========================================================================
    // 1. TESTES DE BLOCOS DE 15 MINUTOS
    // =========================================================================

    @Test
    fun testValid15MinuteBlocks() {
        val validTimes = listOf(
            "00:00", "08:15", "09:30", "10:45",
            "12:00", "13:15", "14:30", "15:45",
            "18:00", "19:15", "20:30", "23:45"
        )
        for (time in validTimes) {
            assertTrue("Horário '$time' deve ser aceito como bloco de 15 min", BookingEngine.isValid15MinuteBlock(time))
        }
    }

    @Test
    fun testInvalidNon15MinuteBlocks() {
        val invalidTimes = listOf(
            "09:05", "09:10", "10:20", "10:25",
            "14:35", "14:40", "16:50", "17:55",
            "24:00", "25:15", "-01:00", "invalido", ""
        )
        for (time in invalidTimes) {
            assertFalse("Horário '$time' NÃO deve ser aceito como bloco de 15 min", BookingEngine.isValid15MinuteBlock(time))
        }
    }

    @Test
    fun testValidationFailsForUnalignedSlotTime() {
        val result = BookingEngine.validateBookingSlot(
            slotTime = "14:20", // Inválido (não é múltiplo de 15 min)
            serviceDurationMinutes = 45,
            existingAppointments = emptyList()
        )
        assertFalse("Slot desalinhado de 15 min deve ser rejeitado", result.isValid)
        assertNotNull("Deve conter justificativa de recusa", result.reason)
        assertTrue("Justificativa deve citar blocos de 15 minutos", result.reason!!.contains("15 minutos"))
    }

    @Test
    fun testValidationFailsForZeroOrNegativeDuration() {
        val resultZero = BookingEngine.validateBookingSlot(
            slotTime = "14:00",
            serviceDurationMinutes = 0,
            existingAppointments = emptyList()
        )
        assertFalse("Duração 0 deve ser rejeitada", resultZero.isValid)

        val resultNegative = BookingEngine.validateBookingSlot(
            slotTime = "14:00",
            serviceDurationMinutes = -15,
            existingAppointments = emptyList()
        )
        assertFalse("Duração negativa deve ser rejeitada", resultNegative.isValid)
    }

    // =========================================================================
    // 2. BUFFER DE HIGIENIZAÇÃO DE 10 MINUTOS ENTRE AGENDAMENTOS
    // =========================================================================

    @Test
    fun testSanitizationBufferConstantAndSlotsCalculation() {
        assertEquals("Buffer obrigatório deve ser de 10 minutos", 10, BookingEngine.SANITIZATION_BUFFER_MINUTES)

        // Serviço de 45 min + 10 min buffer = 55 min -> 4 slots de 15 min (60 min)
        val slots45 = BookingEngine.calculateSlotsUsed(45, 10)
        assertEquals(4, slots45)

        // Serviço de 30 min + 10 min buffer = 40 min -> 3 slots de 15 min (45 min)
        val slots30 = BookingEngine.calculateSlotsUsed(30, 10)
        assertEquals(3, slots30)

        // Serviço de 50 min + 10 min buffer = 60 min -> 4 slots exatos
        val slots50 = BookingEngine.calculateSlotsUsed(50, 10)
        assertEquals(4, slots50)
    }

    @Test
    fun testConsecutiveBookingRejectedDueToSanitizationBuffer() {
        // Cenário: Atendimento existente das 10:00 às 10:45 (45 min).
        // Com 10 min de higienização, o profissional fica ocupado até 10:55.
        val existingAppointment = Appointment(
            id = "appt_exist_1",
            salon = testSalon,
            service = testService.copy(durationMinutes = 45),
            staff = staff1,
            dateDisplay = "Hoje, 14 Out",
            timeSlot = "10:00",
            finalPrice = 84.00,
            status = AppointmentStatus.CONFIRMED,
            bookedAtIso = "2026-10-08T10:00:00Z"
        )
        val existingList = listOf(existingAppointment)

        // Tentativa de agendamento às 10:45 (imediatamente ao término do serviço):
        // 10:45 é ANTES das 10:55 (fim do buffer de higienização). DEVE SER REJEITADO!
        val conflictImmediate = BookingEngine.validateBookingSlot(
            slotTime = "10:45",
            serviceDurationMinutes = 45,
            existingAppointments = existingList,
            staffId = staff1.id,
            dateDisplay = "Hoje, 14 Out"
        )
        assertFalse("Tentativa às 10:45 deve falhar pelo buffer de higienização até 10:55", conflictImmediate.isValid)
        assertEquals("ID conflitante deve ser appt_exist_1", "appt_exist_1", conflictImmediate.conflictAppointmentId)
        assertTrue(conflictImmediate.reason!!.contains("buffer de higienização"))

        // Tentativa de agendamento às 11:00 (após as 10:55):
        // 11:00 >= 10:55 -> DEVE SER APROVADO!
        val validNextSlot = BookingEngine.validateBookingSlot(
            slotTime = "11:00",
            serviceDurationMinutes = 45,
            existingAppointments = existingList,
            staffId = staff1.id,
            dateDisplay = "Hoje, 14 Out"
        )
        assertTrue("Tentativa às 11:00 respeita o buffer e deve ser aprovada", validNextSlot.isValid)
        assertNull(validNextSlot.conflictAppointmentId)
    }

    @Test
    fun testPrecedingBookingBufferProtection() {
        // Cenário: Atendimento existente agendado para as 11:00 (45 min).
        val existingAppointment = Appointment(
            id = "appt_exist_2",
            salon = testSalon,
            service = testService.copy(durationMinutes = 45),
            staff = staff1,
            dateDisplay = "Hoje, 14 Out",
            timeSlot = "11:00",
            finalPrice = 84.00,
            status = AppointmentStatus.CONFIRMED,
            bookedAtIso = "2026-10-08T10:00:00Z"
        )
        val existingList = listOf(existingAppointment)

        // Candidato tenta agendar às 10:15 (45 min + 10 min buffer = até 11:10).
        // Invade o horário das 11:00. DEVE SER REJEITADO!
        val conflictBefore = BookingEngine.validateBookingSlot(
            slotTime = "10:15",
            serviceDurationMinutes = 45,
            existingAppointments = existingList,
            staffId = staff1.id,
            dateDisplay = "Hoje, 14 Out"
        )
        assertFalse("Agendamento às 10:15 (vai até 11:10 com buffer) invade as 11:00 e deve ser rejeitado", conflictBefore.isValid)

        // Candidato tenta agendar às 10:00 (45 min + 10 min buffer = até 10:55).
        // 10:55 <= 11:00 -> Respeita o buffer e desocupa antes do próximo. DEVE SER APROVADO!
        val validBefore = BookingEngine.validateBookingSlot(
            slotTime = "10:00",
            serviceDurationMinutes = 45,
            existingAppointments = existingList,
            staffId = staff1.id,
            dateDisplay = "Hoje, 14 Out"
        )
        assertTrue("Agendamento às 10:00 desocupa às 10:55 e deve ser aprovado", validBefore.isValid)
    }

    // =========================================================================
    // 3. PREVENÇÃO RIGOROSA DE DOUBLE-BOOKING & CONFLITOS DE PROFISSIONAL
    // =========================================================================

    @Test
    fun testExactTimeSlotConflictForSameStaff() {
        val existingAppointment = Appointment(
            id = "appt_juliana_14h",
            salon = testSalon,
            service = testService,
            staff = staff1,
            dateDisplay = "Hoje, 14 Out",
            timeSlot = "14:00",
            finalPrice = 84.00,
            status = AppointmentStatus.CONFIRMED,
            bookedAtIso = "2026-10-08T10:00:00Z"
        )
        val existingList = listOf(existingAppointment)

        // Tentativa de double-booking exatamente no mesmo horário com a mesma profissional
        val conflict = BookingEngine.validateBookingSlot(
            slotTime = "14:00",
            serviceDurationMinutes = 45,
            existingAppointments = existingList,
            staffId = staff1.id,
            dateDisplay = "Hoje, 14 Out"
        )
        assertFalse("Double-booking exato deve ser rejeitado", conflict.isValid)
        assertEquals("appt_juliana_14h", conflict.conflictAppointmentId)

        // Método auxiliar isDoubleBooking deve retornar true
        assertTrue(BookingEngine.isDoubleBooking(
            slotTime = "14:00",
            serviceDurationMinutes = 45,
            existingAppointments = existingList,
            staffId = staff1.id
        ))
    }

    @Test
    fun testDifferentStaffAllowsSimultaneousBooking() {
        // Juliana (staff1) tem agendamento às 14:00
        val julianaAppt = Appointment(
            id = "appt_juliana_14h",
            salon = testSalon,
            service = testService,
            staff = staff1,
            dateDisplay = "Hoje, 14 Out",
            timeSlot = "14:00",
            finalPrice = 84.00,
            status = AppointmentStatus.CONFIRMED,
            bookedAtIso = "2026-10-08T10:00:00Z"
        )
        val existingList = listOf(julianaAppt)

        // Rodrigo (staff2) NÃO tem conflito às 14:00
        val rodrigoResult = BookingEngine.validateBookingSlot(
            slotTime = "14:00",
            serviceDurationMinutes = 45,
            existingAppointments = existingList,
            staffId = staff2.id,
            dateDisplay = "Hoje, 14 Out"
        )
        assertTrue("Profissional diferente (Rodrigo) deve estar disponível no mesmo horário", rodrigoResult.isValid)
        assertNull(rodrigoResult.conflictAppointmentId)
    }

    @Test
    fun testCancelledAppointmentsDoNotBlockSlots() {
        // Agendamento cancelado pelo usuário
        val cancelledAppt = Appointment(
            id = "appt_cancelled",
            salon = testSalon,
            service = testService,
            staff = staff1,
            dateDisplay = "Hoje, 14 Out",
            timeSlot = "14:00",
            finalPrice = 84.00,
            status = AppointmentStatus.CANCELLED_BY_USER,
            bookedAtIso = "2026-10-08T10:00:00Z",
            cancellationReason = "Mudei de planos"
        )
        val existingList = listOf(cancelledAppt)

        // Slot deve estar livre para novo agendamento
        val result = BookingEngine.validateBookingSlot(
            slotTime = "14:00",
            serviceDurationMinutes = 45,
            existingAppointments = existingList,
            staffId = staff1.id,
            dateDisplay = "Hoje, 14 Out"
        )
        assertTrue("Slot cancelado deve estar livre para novas reservas", result.isValid)
        assertNull(result.conflictAppointmentId)
    }

    // =========================================================================
    // 4. TAXA DE RETENÇÃO DE 30% PARA CANCELAMENTOS TARDIOS (< 24H)
    // =========================================================================

    @Test
    fun testCancellationRetentionFeeCalculationUnder24Hours() {
        // Regra de Negócio R1.6: retenção de 30% do valor final para cancelamento < 24h
        val basePrice100 = 100.00
        val retention100 = BookingEngine.calculateCancellationRetentionFee(basePrice100, isUnder24Hours = true)
        assertEquals("Retenção de 30% sobre R$ 100 deve ser R$ 30,00", 30.00, retention100, 0.001)

        val finalPrice84 = 84.00
        val retention84 = BookingEngine.calculateCancellationRetentionFee(finalPrice84, isUnder24Hours = true)
        // 84.00 * 0.30 = 25.20
        assertEquals("Retenção de 30% sobre R$ 84 deve ser R$ 25,20", 25.20, retention84, 0.001)

        val finalPrice140 = 140.00
        val retention140 = BookingEngine.calculateCancellationRetentionFee(finalPrice140, isUnder24Hours = true)
        // 140.00 * 0.30 = 42.00
        assertEquals("Retenção de 30% sobre R$ 140 deve ser R$ 42,00", 42.00, retention140, 0.001)

        // Cancelamento com mais de 24h de antecedência: taxa zero (gratuito)
        val freeRetention = BookingEngine.calculateCancellationRetentionFee(finalPrice84, isUnder24Hours = false)
        assertEquals("Cancelamento com > 24h é gratuito (taxa R$ 0,00)", 0.00, freeRetention, 0.001)
    }

    @Test
    fun testAppointmentCancellationWithRetentionFeePersistedInRepository() {
        val appt = Appointment(
            id = "appt_audit_cancel_retention",
            salon = testSalon,
            service = testService,
            staff = staff1,
            dateDisplay = "Hoje, 14 Out",
            timeSlot = "16:00",
            finalPrice = 84.00,
            status = AppointmentStatus.CONFIRMED,
            bookedAtIso = "2026-10-08T10:00:00Z"
        )
        SalonRepository.addAppointment(appt)

        // Simula cancelamento tardio com cálculo de 30%
        val retentionFee = BookingEngine.calculateCancellationRetentionFee(appt.finalPrice, isUnder24Hours = true)
        SalonRepository.cancelAppointment(
            appointmentId = "appt_audit_cancel_retention",
            reason = "Emergência de trabalho",
            cancellationFee = retentionFee
        )

        val updated = SalonRepository.appointments.value.find { it.id == "appt_audit_cancel_retention" }
        assertNotNull("Agendamento deve existir no repositório", updated)
        assertEquals(AppointmentStatus.CANCELLED_BY_USER, updated!!.status)
        assertEquals("Emergência de trabalho", updated.cancellationReason)
        assertNotNull("Taxa de cancelamento deve ser persistida", updated.cancellationFee)
        assertEquals(25.20, updated.cancellationFee!!, 0.001)
    }
}
