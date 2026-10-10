package com.beautypass.app

import com.beautypass.app.data.SalonRepository
import com.beautypass.app.data.local.Converters
import com.beautypass.app.data.local.entity.AppointmentEntity
import com.beautypass.app.data.local.entity.FavoriteEntity
import com.beautypass.app.data.local.entity.SusEvaluationEntity
import com.beautypass.app.data.local.entity.UserProfileEntity
import com.beautypass.app.model.Appointment
import com.beautypass.app.model.AppointmentStatus
import com.beautypass.app.model.SUSEvaluation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Suíte de Testes Unitários para a Camada Room Database, Entidades e Repositório.
 * Valida conversores determinísticos, mapeamento bidirecional de entidades,
 * ciclo de vida de persistência em memória e Hard Delete LGPD.
 */
class RoomDatabaseTest {

    private val testSalon = SalonRepository.salons.first()
    private val testService = testSalon.services.first()
    private val testStaff = testSalon.staff.firstOrNull()

    @Before
    fun setUp() {
        // Garante estado limpo antes de cada teste
        SalonRepository.clearAllData()
    }

    // =========================================================================
    // 1. TESTES DOS TYPECONVERTERS (SERIALIZAÇÃO / DESSERIALIZAÇÃO)
    // =========================================================================

    @Test
    fun testAppointmentStatusConverterRoundTrip() {
        val converters = Converters()
        for (status in AppointmentStatus.values()) {
            val serialized = converters.fromAppointmentStatus(status)
            assertEquals(status.name, serialized)
            val deserialized = converters.toAppointmentStatus(serialized)
            assertEquals(status, deserialized)
        }

        // Testes de robustez com valores nulos e corrompidos
        assertEquals(AppointmentStatus.CONFIRMED, converters.toAppointmentStatus(null))
        assertEquals(AppointmentStatus.CONFIRMED, converters.toAppointmentStatus(""))
        assertEquals(AppointmentStatus.CONFIRMED, converters.toAppointmentStatus("STATUS_INEXISTENTE"))
    }

    @Test
    fun testSusAnswersMapConverterRoundTrip() {
        val converters = Converters()
        val originalAnswers = mapOf(
            1 to 5, 2 to 2, 3 to 5, 4 to 1, 5 to 4,
            6 to 2, 7 to 5, 8 to 1, 9 to 4, 10 to 2
        )

        val serialized = converters.fromAnswersMap(originalAnswers)
        assertFalse(serialized.isBlank())
        assertTrue(serialized.contains("1:5"))
        assertTrue(serialized.contains("10:2"))

        val deserialized = converters.toAnswersMap(serialized)
        assertEquals(10, deserialized.size)
        assertEquals(originalAnswers, deserialized)
    }

    @Test
    fun testSusAnswersMapConverterEdgeCases() {
        val converters = Converters()

        // Mapa vazio
        assertEquals("", converters.fromAnswersMap(emptyMap()))
        assertEquals(emptyMap<Int, Int>(), converters.toAnswersMap(""))
        assertEquals(emptyMap<Int, Int>(), converters.toAnswersMap(null))

        // String malformada não lança exceção
        val malformed = "1:5;invalido;3:;abc:def"
        val parsed = converters.toAnswersMap(malformed)
        assertEquals(1, parsed.size)
        assertEquals(5, parsed[1])
    }

    // =========================================================================
    // 2. TESTES DE MAPEAMENTO ENTIDADE <-> DOMÍNIO
    // =========================================================================

    @Test
    fun testAppointmentEntityMapping() {
        val domain = Appointment(
            id = "appt_test_001",
            salon = testSalon,
            service = testService,
            staff = testStaff,
            dateDisplay = "Hoje, 10 Out",
            timeSlot = "14:30",
            finalPrice = 85.00,
            status = AppointmentStatus.CONFIRMED,
            bookedAtIso = "2026-10-10T14:30:00Z"
        )

        val entity = AppointmentEntity.fromDomain(domain)
        assertEquals("appt_test_001", entity.id)
        assertEquals(testSalon.id, entity.salonId)
        assertEquals(testService.id, entity.serviceId)
        assertEquals(testStaff?.id, entity.staffId)
        assertEquals(85.00, entity.finalPrice, 0.001)
        assertEquals(AppointmentStatus.CONFIRMED, entity.status)

        val restoredDomain = entity.toDomain(testSalon, testService, testStaff)
        assertEquals(domain.id, restoredDomain.id)
        assertEquals(domain.finalPrice, restoredDomain.finalPrice, 0.001)
        assertEquals(domain.status, restoredDomain.status)
        assertEquals(domain.timeSlot, restoredDomain.timeSlot)
    }

    @Test
    fun testSusEvaluationEntityMapping() {
        val domain = SUSEvaluation(
            participantCode = "P01",
            answers = mapOf(1 to 5, 2 to 1, 3 to 5, 4 to 1, 5 to 5, 6 to 1, 7 to 5, 8 to 1, 9 to 5, 10 to 1),
            susScore = 100.0,
            retentionYes = true,
            evaluatedAtIso = "2026-10-10T15:00:00Z"
        )

        val entity = SusEvaluationEntity.fromDomain(domain)
        assertEquals("P01", entity.participantId)
        assertEquals(100.0, entity.score, 0.001)
        assertTrue(entity.retentionYes)

        val restoredDomain = entity.toDomain()
        assertEquals(domain.participantCode, restoredDomain.participantCode)
        assertEquals(domain.susScore, restoredDomain.susScore, 0.001)
        assertEquals(domain.answers, restoredDomain.answers)
    }

    @Test
    fun testFavoriteEntityMapping() {
        val timestamp = 1728500000000L
        val entity = FavoriteEntity(salonId = testSalon.id, addedAtTimestamp = timestamp)
        assertEquals(testSalon.id, entity.salonId)
        assertEquals(timestamp, entity.addedAtTimestamp)
    }

    @Test
    fun testUserProfileEntityMapping() {
        val timestamp = 1728500000000L
        val entity = UserProfileEntity(
            participantId = "P01",
            name = "Juliana Silva",
            phone = "(11) 98765-4321",
            lgpdAccepted = true,
            acceptedAtTimestamp = timestamp
        )
        assertEquals("P01", entity.participantId)
        assertEquals("Juliana Silva", entity.name)
        assertEquals("(11) 98765-4321", entity.phone)
        assertTrue(entity.lgpdAccepted)
        assertEquals(timestamp, entity.acceptedAtTimestamp)
    }

    // =========================================================================
    // 3. TESTES DE CICLO DE VIDA DO REPOSITÓRIO E HARD DELETE LGPD
    // =========================================================================

    @Test
    fun testFavoriteToggleInRepository() {
        val salonId = testSalon.id
        assertFalse(SalonRepository.isFavorite(salonId))

        SalonRepository.toggleFavorite(salonId)
        assertTrue(SalonRepository.isFavorite(salonId))
        assertTrue(SalonRepository.favoriteIds.value.contains(salonId))

        SalonRepository.toggleFavorite(salonId)
        assertFalse(SalonRepository.isFavorite(salonId))
        assertFalse(SalonRepository.favoriteIds.value.contains(salonId))
    }

    @Test
    fun testAppointmentManagementInRepository() {
        val appointment = Appointment(
            id = "appt_repo_test",
            salon = testSalon,
            service = testService,
            staff = testStaff,
            dateDisplay = "Hoje",
            timeSlot = "10:00",
            finalPrice = 90.00,
            status = AppointmentStatus.CONFIRMED,
            bookedAtIso = "2026-10-10T10:00:00Z"
        )

        SalonRepository.addAppointment(appointment)
        assertEquals(1, SalonRepository.appointments.value.size)
        assertEquals("appt_repo_test", SalonRepository.appointments.value.first().id)

        // Cancelamento com taxa
        SalonRepository.cancelAppointment(
            appointmentId = "appt_repo_test",
            reason = "Imprevisto no trabalho",
            cancellationFee = 27.00
        )

        val cancelled = SalonRepository.appointments.value.first()
        assertEquals(AppointmentStatus.CANCELLED_BY_USER, cancelled.status)
        assertEquals("Imprevisto no trabalho", cancelled.cancellationReason)
        assertEquals(27.00, cancelled.cancellationFee!!, 0.001)
    }

    @Test
    fun testUserProfileAndSusPersistenceInRepository() {
        SalonRepository.saveUserProfile(
            participantId = "P01",
            name = "Juliana Silva",
            phone = "(11) 98765-4321",
            lgpdAccepted = true
        )

        val profile = SalonRepository.userProfile.value
        assertNotNull(profile)
        assertEquals("P01", profile?.participantId)
        assertEquals("Juliana Silva", profile?.name)
        assertTrue(profile?.lgpdAccepted == true)

        val evaluation = SUSEvaluation(
            participantCode = "P01",
            answers = mapOf(1 to 4, 2 to 2, 3 to 4, 4 to 2, 5 to 4, 6 to 2, 7 to 4, 8 to 2, 9 to 4, 10 to 2),
            susScore = 70.0,
            retentionYes = true,
            evaluatedAtIso = "2026-10-10T16:00:00Z"
        )
        SalonRepository.saveSusEvaluation(evaluation)

        val sus = SalonRepository.latestSusEvaluation.value
        assertNotNull(sus)
        assertEquals("P01", sus?.participantCode)
        assertEquals(70.0, sus?.susScore!!, 0.001)
    }

    @Test
    fun testLgpdHardDeleteRemovesAllUserData() {
        // Inserir dados em todas as 4 áreas
        SalonRepository.toggleFavorite(testSalon.id)
        SalonRepository.addAppointment(
            Appointment(
                id = "appt_lgpd_test",
                salon = testSalon,
                service = testService,
                staff = testStaff,
                dateDisplay = "Amanhã",
                timeSlot = "11:00",
                finalPrice = 120.00,
                status = AppointmentStatus.CONFIRMED,
                bookedAtIso = "2026-10-11T11:00:00Z"
            )
        )
        SalonRepository.saveUserProfile(
            participantId = "P01",
            name = "Usuário Teste",
            phone = "11999999999",
            lgpdAccepted = true
        )
        SalonRepository.saveSusEvaluation(
            SUSEvaluation(
                participantCode = "P01",
                answers = mapOf(1 to 5),
                susScore = 80.0,
                retentionYes = true,
                evaluatedAtIso = "2026-10-10T12:00:00Z"
            )
        )

        // Verificar pré-condição: dados presentes
        assertTrue(SalonRepository.favoriteIds.value.isNotEmpty())
        assertTrue(SalonRepository.appointments.value.isNotEmpty())
        assertNotNull(SalonRepository.userProfile.value)
        assertNotNull(SalonRepository.latestSusEvaluation.value)

        // Executar Hard Delete LGPD
        SalonRepository.clearAllData()

        // Verificar pós-condição: 100% dos dados expurgados
        assertTrue("Favoritos devem estar vazios após LGPD Hard Delete", SalonRepository.favoriteIds.value.isEmpty())
        assertTrue("Agendamentos devem estar vazios após LGPD Hard Delete", SalonRepository.appointments.value.isEmpty())
        assertNull("Perfil deve ser nulo após LGPD Hard Delete", SalonRepository.userProfile.value)
        assertNull("Avaliação SUS deve ser nula após LGPD Hard Delete", SalonRepository.latestSusEvaluation.value)
    }
}
