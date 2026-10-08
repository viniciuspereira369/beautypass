package com.beautypass.app.ui.screens.confirm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DirectionsWalk
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

        // Ícone de Sucesso
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MintSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Confirmado",
                tint = SereneTeal,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            text = "Agendamento Confirmado!",
            style = MaterialTheme.typography.headlineMedium,
            color = Slate900
        )
        Text(
            text = "Seu horário foi garantido no sistema.",
            fontSize = 13.sp,
            color = Slate600
        )

        // Card do Itinerário & Voucher Digital
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Destino e Endereço
                Column {
                    Text(
                        text = "Destino Confirmado",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    Text(
                        text = appointment?.salon?.name ?: "Ateliê Belle Époque",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = appointment?.salon?.address ?: "R. Fradique Coutinho, 980 - Pinheiros",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                }

                // Trajeto a Pé (Métrica de Mobilidade)
                val walkMinutes = appointment?.walkingTimeMin ?: 10
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MintSurface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DirectionsWalk,
                        contentDescription = null,
                        tint = SereneTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Trajeto a pé: $walkMinutes min até o salão",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SereneTeal
                    )
                }

                HorizontalDivider(color = OutlineBorder)

                // Resumo do Serviço
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = appointment?.service?.name ?: "Serviço",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${appointment?.dateDisplay ?: "Hoje"} às ${appointment?.timeSlot ?: "14:00"}",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                    }
                    Text(
                        text = "R$ ${String.format("%.2f", appointment?.finalPrice ?: 84.0)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SereneTeal
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // QR Code Vetorial do Voucher Digital (Critério C6)
                VoucherQrCodeView(voucherCode = appointment?.voucherQrCode ?: "BP-772282")
            }
        }

        // Botões de Navegação
        Button(
            onClick = onGoToAppointments,
            colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Ver Meus Agendamentos", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onGoToHome,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Voltar ao Início", fontSize = 14.sp, color = Slate800)
        }
    }
}
