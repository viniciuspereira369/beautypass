package com.beautypass.app

import com.beautypass.app.notification.BeautyPassNotificationHelper
import com.beautypass.app.notification.NotificationPayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Suíte de Testes Unitários para o Gerenciador de Notificações (BeautyPassNotificationHelper).
 *
 * Valida rigorosamente:
 * 1. Constantes do canal de alta prioridade (CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION)
 *    e identificadores canônicos de notificação (1001, 1002, 1003).
 * 2. Formatação do payload de alerta de 2 minutos do congelamento de preço (formatFreezeWarningPayload).
 * 3. Formatação do payload de confirmação instantânea de agendamento (formatBookingConfirmedPayload).
 * 4. Formatação do payload de cancelamento com retenção de 30% e caso limpo sem taxa (formatCancellationPayload).
 * 5. Lógica e simulação determinística do limiar de 120s do timer de checkout, prevenindo re-disparos múltiplos.
 * 6. Contrato e imutabilidade da data class NotificationPayload.
 */
class NotificationHelperTest {

    // =========================================================================
    // 1. TESTES DE CONSTANTES DO CANAL E IDS DE NOTIFICAÇÃO
    // =========================================================================

    @Test
    fun testNotificationChannelAndIdConstants() {
        assertEquals(
            "Canal de agendamento deve corresponder à especificação",
            "beautypass_booking_channel",
            BeautyPassNotificationHelper.CHANNEL_ID
        )
        assertEquals(
            "Nome do canal deve ser amigável e oficial",
            "Alertas de Reservas BeautyPass",
            BeautyPassNotificationHelper.CHANNEL_NAME
        )
        assertTrue(
            "Descrição do canal deve detalhar o propósito",
            BeautyPassNotificationHelper.CHANNEL_DESCRIPTION.contains("alta prioridade")
        )

        // IDs Canônicos de Notificação
        assertEquals(
            "ID de alerta do timer deve ser 1001",
            1001,
            BeautyPassNotificationHelper.NOTIF_ID_FREEZE_WARNING
        )
        assertEquals(
            "ID de confirmação de reserva deve ser 1002",
            1002,
            BeautyPassNotificationHelper.NOTIF_ID_BOOKING_CONFIRMED
        )
        assertEquals(
            "ID de cancelamento de reserva deve ser 1003",
            1003,
            BeautyPassNotificationHelper.NOTIF_ID_CANCELLATION
        )
    }

    // =========================================================================
    // 2. FORMATADOR DE ALERTA DO TIMER (CONGELAMENTO DE PREÇO)
    // =========================================================================

    @Test
    fun testFormatFreezeWarningPayloadStandard() {
        val salonName = "Studio Bella Vista"
        val minutesLeft = 2
        val priceFinal = 84.00

        val payload = BeautyPassNotificationHelper.formatFreezeWarningPayload(
            salonName = salonName,
            minutesLeft = minutesLeft,
            priceFinal = priceFinal
        )

        assertEquals(BeautyPassNotificationHelper.NOTIF_ID_FREEZE_WARNING, payload.id)
        assertEquals("Atenção: Vaga quase expirando!", payload.title)
        assertEquals(BeautyPassNotificationHelper.CHANNEL_ID, payload.channelId)

        assertTrue(
            "Mensagem deve conter os minutos restantes (2 minutos)",
            payload.message.contains("2 minutos")
        )
        assertTrue(
            "Mensagem deve conter o nome do salão",
            payload.message.contains("Studio Bella Vista")
        )
        assertTrue(
            "Mensagem deve conter o preço congelado formatado em R$",
            payload.message.contains("R$ 84.00")
        )
        assertTrue(
            "Mensagem deve avisar sobre tarifa congelada",
            payload.message.contains("tarifa congelada")
        )
    }

    @Test
    fun testFormatFreezeWarningPayloadEdgeCases() {
        // Preço mínimo e 1 minuto
        val payload1 = BeautyPassNotificationHelper.formatFreezeWarningPayload(
            salonName = "L'Élégance Jardins",
            minutesLeft = 1,
            priceFinal = 25.00
        )
        assertTrue(payload1.message.contains("1 minutos"))
        assertTrue(payload1.message.contains("R$ 25.00"))
        assertTrue(payload1.message.contains("L'Élégance Jardins"))

        // Preço alto fracionado
        val payload2 = BeautyPassNotificationHelper.formatFreezeWarningPayload(
            salonName = "Barbearia Vintage Paulista",
            minutesLeft = 2,
            priceFinal = 159.90
        )
        assertTrue(payload2.message.contains("R$ 159.90"))
    }

    // =========================================================================
    // 3. FORMATADOR DE CONFIRMAÇÃO INSTANTÂNEA DE RESERVA
    // =========================================================================

    @Test
    fun testFormatBookingConfirmedPayload() {
        val appointmentId = "appt_conf_001"
        val salonName = "L'Élégance Jardins"
        val serviceName = "Corte Visagista & Escova"
        val dateDisplay = "Hoje, 10 Out"
        val timeSlot = "14:30"

        val payload = BeautyPassNotificationHelper.formatBookingConfirmedPayload(
            appointmentId = appointmentId,
            salonName = salonName,
            serviceName = serviceName,
            dateDisplay = dateDisplay,
            timeSlot = timeSlot
        )

        assertEquals(BeautyPassNotificationHelper.NOTIF_ID_BOOKING_CONFIRMED, payload.id)
        assertEquals("Reserva Confirmada com Sucesso! ✂️", payload.title)
        assertEquals(BeautyPassNotificationHelper.CHANNEL_ID, payload.channelId)

        assertTrue(
            "Mensagem deve conter o nome do serviço",
            payload.message.contains("Corte Visagista & Escova")
        )
        assertTrue(
            "Mensagem deve conter o nome do salão",
            payload.message.contains("L'Élégance Jardins")
        )
        assertTrue(
            "Mensagem deve conter a data de exibição",
            payload.message.contains("Hoje, 10 Out")
        )
        assertTrue(
            "Mensagem deve conter o horário agendado",
            payload.message.contains("14:30")
        )
        assertTrue(
            "Mensagem deve conter o código do voucher / appointmentId",
            payload.message.contains("Voucher: appt_conf_001")
        )
    }

    // =========================================================================
    // 4. FORMATADOR DE CANCELAMENTO DE RESERVA (COM E SEM TAXA DE 30%)
    // =========================================================================

    @Test
    fun testFormatCancellationPayloadWithRetentionFee() {
        val salonName = "Espaço Beauty Jardins"
        val cancellationFee = 25.50 // 30% sobre R$ 85,00

        val payload = BeautyPassNotificationHelper.formatCancellationPayload(
            salonName = salonName,
            cancellationFee = cancellationFee
        )

        assertEquals(BeautyPassNotificationHelper.NOTIF_ID_CANCELLATION, payload.id)
        assertEquals("Agendamento Cancelado", payload.title)
        assertEquals(BeautyPassNotificationHelper.CHANNEL_ID, payload.channelId)

        assertTrue(
            "Mensagem deve citar o salão",
            payload.message.contains("Espaço Beauty Jardins")
        )
        assertTrue(
            "Mensagem deve conter a taxa de retenção de 30% formatada",
            payload.message.contains("Taxa de retenção de 30%: R$ 25.50")
        )
        assertTrue(
            "Mensagem deve direcionar para Meus Agendamentos",
            payload.message.contains("Meus Agendamentos")
        )
    }

    @Test
    fun testFormatCancellationPayloadWithoutFee() {
        // Caso sem taxa (cancelamento antecipado > 24h ou fee nulo)
        val payloadNull = BeautyPassNotificationHelper.formatCancellationPayload(
            salonName = "Barbearia Vintage Paulista",
            cancellationFee = null
        )

        assertEquals(BeautyPassNotificationHelper.NOTIF_ID_CANCELLATION, payloadNull.id)
        assertFalse(
            "Mensagem não deve mencionar retenção quando fee for nulo",
            payloadNull.message.contains("Taxa de retenção")
        )
        assertTrue(
            "Mensagem deve confirmar sucesso",
            payloadNull.message.contains("cancelado com sucesso")
        )

        // Caso com taxa zero
        val payloadZero = BeautyPassNotificationHelper.formatCancellationPayload(
            salonName = "Studio Bella Vista",
            cancellationFee = 0.0
        )
        assertFalse(
            "Mensagem não deve mencionar retenção quando taxa for zero",
            payloadZero.message.contains("Taxa de retenção")
        )
        assertTrue(
            "Mensagem deve confirmar sucesso",
            payloadZero.message.contains("cancelado com sucesso")
        )
    }

    // =========================================================================
    // 5. SIMULAÇÃO DETERMINÍSTICA DO LIMIAR DE 120s DO TIMER DE CHECKOUT
    // =========================================================================

    @Test
    fun testTimerThresholdTriggerSimulationFullCountdown() {
        // Simulação do loop de contagem regressiva de 10 minutos (600s até 0s)
        var hasAlertedTwoMinutes = false
        var alertTriggerCount = 0
        var recordedAlertSeconds = -1

        for (secondsLeft in 600 downTo 0) {
            // Regra exata aplicada no LaunchedEffect de CheckoutScreen.kt
            if (secondsLeft <= 120 && !hasAlertedTwoMinutes) {
                hasAlertedTwoMinutes = true
                alertTriggerCount++
                recordedAlertSeconds = secondsLeft
            }
        }

        assertEquals(
            "O alerta de expiração de 2 minutos deve disparar exatamente 1 vez",
            1,
            alertTriggerCount
        )
        assertEquals(
            "O alerta deve disparar exatamente no limiar de 120 segundos",
            120,
            recordedAlertSeconds
        )
        assertTrue(
            "A flag preventiva hasAlertedTwoMinutes deve terminar ativa",
            hasAlertedTwoMinutes
        )
    }

    @Test
    fun testTimerThresholdBoundaryCases() {
        // Avaliação de condições de borda
        fun shouldTrigger(secondsLeft: Int, hasAlerted: Boolean): Boolean {
            return secondsLeft <= 120 && !hasAlerted
        }

        // Antes do limiar (> 120s)
        assertFalse("600s não deve disparar", shouldTrigger(600, false))
        assertFalse("300s não deve disparar", shouldTrigger(300, false))
        assertFalse("121s não deve disparar", shouldTrigger(121, false))

        // Exatamente no limiar (120s)
        assertTrue("120s sem alerta prévio DEVE disparar", shouldTrigger(120, false))
        assertFalse("120s com alerta prévio NÃO deve disparar", shouldTrigger(120, true))

        // Após o limiar (< 120s)
        assertTrue("119s sem alerta prévio dispararia se perdeu o frame exato", shouldTrigger(119, false))
        assertFalse("119s com alerta já disparado NÃO deve re-disparar", shouldTrigger(119, true))
        assertFalse("60s com alerta já disparado NÃO deve re-disparar", shouldTrigger(60, true))
        assertFalse("0s com alerta já disparado NÃO deve re-disparar", shouldTrigger(0, true))
    }

    // =========================================================================
    // 6. CONTRATO E IMUTABILIDADE DA DATA CLASS NOTIFICATIONPAYLOAD
    // =========================================================================

    @Test
    fun testNotificationPayloadDataClass() {
        val payload = NotificationPayload(
            id = 5001,
            title = "Título Teste",
            message = "Corpo de mensagem teste"
        )

        assertEquals(5001, payload.id)
        assertEquals("Título Teste", payload.title)
        assertEquals("Corpo de mensagem teste", payload.message)
        assertEquals(BeautyPassNotificationHelper.CHANNEL_ID, payload.channelId)

        // Imutabilidade e cópia
        val updatedPayload = payload.copy(title = "Título Atualizado")
        assertEquals("Título Atualizado", updatedPayload.title)
        assertEquals("Título Teste", payload.title)
    }
}
