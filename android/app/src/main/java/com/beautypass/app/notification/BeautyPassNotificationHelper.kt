package com.beautypass.app.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.beautypass.app.MainActivity
import com.beautypass.app.R
import java.util.Locale

/**
 * Payload imutável de notificação desacoplado do Android Context,
 * permitindo testes unitários determinísticos na JVM.
 */
data class NotificationPayload(
    val id: Int,
    val title: String,
    val message: String,
    val channelId: String = BeautyPassNotificationHelper.CHANNEL_ID,
    val priority: Int = NotificationCompat.PRIORITY_HIGH
)

/**
 * Gerenciador de Notificações Nativas do BeautyPass Android.
 *
 * Responsável por:
 * 1. Registro do canal de notificações de alta prioridade (`beautypass_booking_channel`)
 *    com suporte a luzes Serene Teal, padrão de vibração e som.
 * 2. Formatação pura de payloads para avisos de congelamento de preço, confirmação de
 *    reserva e cancelamento.
 * 3. Despacho seguro com verificação de permissões em runtime (Android 13+ POST_NOTIFICATIONS)
 *    e tratamento defensivo de exceções.
 */
object BeautyPassNotificationHelper {

    const val CHANNEL_ID = "beautypass_booking_channel"
    const val CHANNEL_NAME = "Alertas de Reservas BeautyPass"
    const val CHANNEL_DESCRIPTION = "Alertas de alta prioridade para confirmação de agendamentos, expiração de preço e cancelamentos."

    const val NOTIF_ID_FREEZE_WARNING = 1001
    const val NOTIF_ID_BOOKING_CONFIRMED = 1002
    const val NOTIF_ID_CANCELLATION = 1003

    /**
     * Cria e registra o canal de notificação no NotificationManager (Android 8.0+ / API 26+).
     * Configura alta prioridade, vibração personalizada e luz indicadora Serene Teal.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = 0xFF00685F.toInt() // Serene Teal Oficial
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Verifica se o aplicativo possui permissão para postar notificações.
     * No Android 13+ (API 33+ / Tiramisu), valida a permissão de runtime Manifest.permission.POST_NOTIFICATIONS.
     * Em versões anteriores, valida via NotificationManagerCompat.areNotificationsEnabled().
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    // =========================================================================
    // Formatadores Puros de Payload (Desacoplados para Testes Unitários na JVM)
    // =========================================================================

    /**
     * Formata o payload do alerta de limiar do congelamento de preço (2 minutos restantes).
     */
    fun formatFreezeWarningPayload(
        salonName: String,
        minutesLeft: Int,
        priceFinal: Double
    ): NotificationPayload {
        return NotificationPayload(
            id = NOTIF_ID_FREEZE_WARNING,
            title = "Atenção: Vaga quase expirando!",
            message = "Restam apenas $minutesLeft minutos para concluir sua reserva no salão $salonName com tarifa congelada de R$ ${String.format(Locale.US, "%.2f", priceFinal)}."
        )
    }

    /**
     * Formata o payload de confirmação instantânea de agendamento.
     */
    fun formatBookingConfirmedPayload(
        appointmentId: String,
        salonName: String,
        serviceName: String,
        dateDisplay: String,
        timeSlot: String
    ): NotificationPayload {
        return NotificationPayload(
            id = NOTIF_ID_BOOKING_CONFIRMED,
            title = "Reserva Confirmada com Sucesso! ✂️",
            message = "$serviceName no $salonName • $dateDisplay às $timeSlot. Voucher: $appointmentId"
        )
    }

    /**
     * Formata o payload de notificação de cancelamento de agendamento com cálculo de taxa.
     */
    fun formatCancellationPayload(
        salonName: String,
        cancellationFee: Double?
    ): NotificationPayload {
        val feeText = if (cancellationFee != null && cancellationFee > 0.0) {
            " (Taxa de retenção de 30%: R$ ${String.format(Locale.US, "%.2f", cancellationFee)})"
        } else {
            ""
        }
        return NotificationPayload(
            id = NOTIF_ID_CANCELLATION,
            title = "Agendamento Cancelado",
            message = "Seu agendamento no $salonName foi cancelado com sucesso$feeText. Veja os detalhes em Meus Agendamentos."
        )
    }

    // =========================================================================
    // Métodos de Despacho Nativo Android (Seguros contra Exceções)
    // =========================================================================

    /**
     * Dispara notificação de alerta quando restarem minutos para a expiração do congelamento de preço.
     */
    fun showFreezeTimerAlert(
        context: Context,
        salonName: String,
        minutesLeft: Int,
        priceFinal: Double
    ) {
        if (!hasNotificationPermission(context)) return
        val payload = formatFreezeWarningPayload(salonName, minutesLeft, priceFinal)
        dispatchNotification(context, payload)
    }

    /**
     * Dispara notificação instantânea quando um agendamento é confirmado.
     */
    fun showBookingConfirmedNotification(
        context: Context,
        appointmentId: String,
        salonName: String,
        serviceName: String,
        dateDisplay: String,
        timeSlot: String
    ) {
        if (!hasNotificationPermission(context)) return
        val payload = formatBookingConfirmedPayload(appointmentId, salonName, serviceName, dateDisplay, timeSlot)
        dispatchNotification(context, payload)
    }

    /**
     * Dispara notificação simples e elegante de cancelamento de agendamento.
     */
    fun showCancellationNotification(
        context: Context,
        salonName: String,
        cancellationFee: Double?
    ) {
        if (!hasNotificationPermission(context)) return
        val payload = formatCancellationPayload(salonName, cancellationFee)
        dispatchNotification(context, payload)
    }

    /**
     * Executa a construção e o envio da notificação nativa ao sistema operacional.
     */
    private fun dispatchNotification(context: Context, payload: NotificationPayload) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            payload.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, payload.channelId)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(payload.title)
            .setContentText(payload.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(payload.message))
            .setPriority(payload.priority)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(payload.id, notification)
        } catch (_: SecurityException) {
            // Permissão negada pelo SO em tempo de execução
        } catch (_: Exception) {
            // Tratamento defensivo para evitar falha no fluxo da aplicação
        }
    }
}
