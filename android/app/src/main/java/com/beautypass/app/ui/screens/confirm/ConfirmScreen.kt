package com.beautypass.app.ui.screens.confirm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.VoucherQrCodeView

/**
 * Tela 5B: Voucher Digital & Confirmação de Agendamento.
 *
 * Implementado conforme especificações de:
 * - ORIGINAL_REQUEST.md (R1.5)
 * - documenta_o_t_cnica_prot_tipo_de_valida_o.md (Seção 8 - Voucher Digital e QR Code)
 * - Renderiza VoucherQrCodeView vetorial em Canvas e itinerário de confirmação.
 */
@Composable
fun ConfirmScreen(
    appointmentId: String,
    onGoToAppointments: () -> Unit,
    onGoToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appointment = remember(appointmentId) {
        SalonRepository.appointments.value.find { it.id == appointmentId }
            ?: SalonRepository.appointments.value.firstOrNull()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Ícone de Sucesso com Pulso Suave
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(MintSurface, CircleShape)
                .border(2.dp, MintLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Confirmado",
                tint = SereneTeal,
                modifier = Modifier.size(38.dp)
            )
        }

        Text(
            text = "Agendamento Confirmado!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = OceanicCharcoal
        )

        Text(
            text = "Seu horário foi garantido no sistema com tarifa congelada.",
            fontSize = 13.sp,
            color = NeutralMuted
        )

        // Componente Especializado: Voucher com QR Code Vetorial
        VoucherQrCodeView(
            voucherCode = appointment?.id ?: appointmentId
        )

        // Card do Itinerário e Dados da Reserva
        if (appointment != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Salão e Endereço
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = SereneTeal,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = appointment.salon.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = OceanicCharcoal
                            )
                            Text(
                                text = appointment.salon.address,
                                fontSize = 12.sp,
                                color = NeutralMuted
                            )
                        }
                    }

                    // Estimativa de Caminhada a Pé
                    Box(
                        modifier = Modifier
                            .background(MintSurface, CapsuleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsWalk,
                                contentDescription = null,
                                tint = SereneTeal,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${appointment.walkingTimeMin ?: appointment.salon.walkTimeMinutes} min a pé (${appointment.salon.walkDistanceMeters} m)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SereneTealDark
                            )
                        }
                    }

                    Divider(color = OutlineVariant, thickness = 0.8.dp)

                    // Serviço e Horário
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = appointment.service.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanicCharcoal
                                )
                                Text(
                                    text = "${appointment.dateDisplay} às ${appointment.timeSlot} • ${appointment.service.durationMinutes} min",
                                    fontSize = 11.sp,
                                    color = NeutralMuted
                                )
                            }
                        }

                        Text(
                            text = "R$ ${String.format("%.2f", appointment.finalPrice)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SereneTeal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botões de Ação
        Button(
            onClick = onGoToAppointments,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SereneTeal)
        ) {
            Text(
                text = "Ver Meus Agendamentos",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SurfaceWhite
            )
        }

        OutlinedButton(
            onClick = onGoToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SereneTeal)
        ) {
            Text(
                text = "Voltar ao Início",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SereneTeal
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
