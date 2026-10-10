package com.beautypass.app.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.beautypass.app.data.BookingEngine
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Appointment
import com.beautypass.app.model.AppointmentStatus
import com.beautypass.app.notification.BeautyPassNotificationHelper
import com.beautypass.app.theme.*
import kotlin.math.round

/**
 * Tela 6A: Meus Agendamentos (Ativos & Histórico) com Modal de Cancelamento e Retenção de 30%.
 *
 * Implementado conforme especificações de:
 * - ORIGINAL_REQUEST.md (R1.6)
 * - documenta_o_t_cnica_prot_tipo_de_valida_o.md (Seção 5 - Cancelamento e Políticas)
 * - test_validation_session14.py (Botão verbatim "Ver QR Code & Voucher")
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    onOpenVoucher: (appointmentId: String) -> Unit,
    onBookNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Ativos, 1: Histórico
    val appointments by SalonRepository.appointments.collectAsState()

    var appointmentToCancel by remember { mutableStateOf<Appointment?>(null) }
    var cancellationReason by remember { mutableStateOf("Mudei de planos") }
    var customReasonText by remember { mutableStateOf("") }

    val filteredList = remember(selectedTab, appointments) {
        if (selectedTab == 0) {
            appointments.filter {
                it.status == AppointmentStatus.CONFIRMED || it.status == AppointmentStatus.IN_PROGRESS
            }
        } else {
            appointments.filter {
                it.status == AppointmentStatus.COMPLETED ||
                it.status == AppointmentStatus.CANCELLED_BY_USER ||
                it.status == AppointmentStatus.CANCELLED_BY_MERCHANT ||
                it.status == AppointmentStatus.EXPIRED
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meus Agendamentos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // TabRow Ativos vs Histórico
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceWhite,
                contentColor = SereneTeal
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        val activeCount = appointments.count { it.status == AppointmentStatus.CONFIRMED }
                        Text(
                            text = "Ativos ($activeCount)",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) SereneTeal else NeutralMuted
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Histórico",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) SereneTeal else NeutralMuted
                        )
                    }
                )
            }

            if (filteredList.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MintSurface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = SereneTeal,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedTab == 0) "Nenhum agendamento ativo" else "Nenhum serviço no histórico",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = OceanicCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Aproveite para reservar com desconto dinâmico hoje em São Paulo.",
                        fontSize = 12.sp,
                        color = NeutralMuted
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onBookNowClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal)
                    ) {
                        Text("Ver Salões Disponíveis", fontWeight = FontWeight.Bold, color = SurfaceWhite)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList, key = { it.id }) { appt ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Cabeçalho do Card
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = appt.salon.imageUrl,
                                        contentDescription = appt.salon.name,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = appt.salon.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OceanicCharcoal
                                        )
                                        Text(
                                            text = "${appt.dateDisplay} às ${appt.timeSlot}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SereneTeal
                                        )
                                        Text(
                                            text = "${appt.service.name} • ${appt.service.durationMinutes} min",
                                            fontSize = 11.sp,
                                            color = NeutralMuted
                                        )
                                    }

                                    // Status Badge
                                    val statusColor = when (appt.status) {
                                        AppointmentStatus.CONFIRMED -> EconomyGreen
                                        AppointmentStatus.IN_PROGRESS -> SereneTeal
                                        AppointmentStatus.COMPLETED -> SereneTealDark
                                        AppointmentStatus.CANCELLED_BY_USER -> CoralPromo
                                        AppointmentStatus.CANCELLED_BY_MERCHANT -> CoralPromo
                                        AppointmentStatus.EXPIRED -> Slate400
                                    }
                                    val statusLabel = when (appt.status) {
                                        AppointmentStatus.CONFIRMED -> "Confirmado"
                                        AppointmentStatus.IN_PROGRESS -> "Em Andamento"
                                        AppointmentStatus.COMPLETED -> "Concluído"
                                        AppointmentStatus.CANCELLED_BY_USER -> "Cancelado"
                                        AppointmentStatus.CANCELLED_BY_MERCHANT -> "Cancelado"
                                        AppointmentStatus.EXPIRED -> "Expirado"
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = statusLabel,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = statusColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Dados do Pagamento e Voucher
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Código do Voucher: ${appt.id}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OceanicCharcoal
                                        )
                                        Text(
                                            text = "Valor Pago: R$ ${String.format("%.2f", appt.finalPrice)}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SereneTeal
                                        )
                                    }

                                    if (appt.cancellationFee != null && appt.cancellationFee > 0) {
                                        Text(
                                            text = "Taxa de retenção (30%): R$ ${String.format("%.2f", appt.cancellationFee)}",
                                            fontSize = 10.sp,
                                            color = CoralPromoText,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Botões de Ação
                                if (selectedTab == 0) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Botão com texto verbatim "Ver QR Code & Voucher" exigido no teste
                                        OutlinedButton(
                                            onClick = { onOpenVoucher(appt.id) },
                                            modifier = Modifier.weight(1.3f),
                                            shape = RoundedCornerShape(10.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, SereneTeal),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SereneTeal)
                                        ) {
                                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Ver QR Code & Voucher",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Button(
                                            onClick = { appointmentToCancel = appt },
                                            modifier = Modifier.weight(0.9f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = CoralPromoBg, contentColor = CoralPromoText)
                                        ) {
                                            Text(
                                                text = "Cancelar",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de Cancelamento com Retenção Simulada de 30%
    if (appointmentToCancel != null) {
        val appt = appointmentToCancel!!
        val retentionFee = BookingEngine.calculateCancellationRetentionFee(appt.finalPrice, isUnder24Hours = true)
        val reasons = listOf(
            "Mudei de planos",
            "Encontrei opção melhor",
            "Emergência",
            "Preço ficou alto demais",
            "Outro"
        )

        AlertDialog(
            onDismissRequest = { appointmentToCancel = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = CoralPromo, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancelar Agendamento",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Cancele gratuitamente até 24h antes do horário. Após isso, será retida uma taxa de 30% do valor (simulada neste teste).",
                        fontSize = 12.sp,
                        color = OceanicCharcoal,
                        lineHeight = 16.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CoralPromoBg, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Taxa estimada de retenção (30%): R$ ${String.format("%.2f", retentionFee)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralPromoText
                        )
                    }

                    Text(
                        text = "Selecione o motivo:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )

                    reasons.forEach { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { cancellationReason = r },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = cancellationReason == r,
                                onClick = { cancellationReason = r },
                                colors = RadioButtonDefaults.colors(selectedColor = SereneTeal)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = r, fontSize = 12.sp, color = OceanicCharcoal)
                        }
                    }

                    if (cancellationReason == "Outro") {
                        OutlinedTextField(
                            value = customReasonText,
                            onValueChange = { customReasonText = it },
                            label = { Text("Descreva o motivo") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalReason = if (cancellationReason == "Outro") customReasonText else cancellationReason
                        SalonRepository.cancelAppointment(
                            appointmentId = appt.id,
                            reason = finalReason,
                            cancellationFee = retentionFee
                        )
                        BeautyPassNotificationHelper.showCancellationNotification(
                            context = context,
                            salonName = appt.salon.name,
                            cancellationFee = retentionFee
                        )
                        selectedTab = 1
                        appointmentToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPromo)
                ) {
                    Text("Confirmar Cancelamento", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { appointmentToCancel = null }) {
                    Text("Voltar", color = NeutralMuted)
                }
            },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
