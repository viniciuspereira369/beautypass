package com.beautypass.app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Appointment
import com.beautypass.app.model.AppointmentStatus
import com.beautypass.app.theme.*
import kotlinx.coroutines.delay
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    salonId: String,
    serviceId: String,
    slotTime: String,
    onBackClick: () -> Unit,
    onBookingConfirmed: (appointmentId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val salon = remember(salonId) {
        SalonRepository.salons.find { it.id == salonId } ?: SalonRepository.salons.first()
    }
    val service = remember(serviceId) {
        salon.services.find { it.id == serviceId } ?: salon.services.first()
    }

    val pricing = remember(slotTime, service) {
        SalonRepository.calculatePricing(salon, slotTime, service)
    }

    var paymentMethod by remember { mutableStateOf("card") } // "card" ou "pix"
    var cardNumber by remember { mutableStateOf("4111 1111 1111 1111") }
    var holderName by remember { mutableStateOf("Camila Alves") }

    var secondsLeft by remember { mutableStateOf(600) } // 10 minutos de retenção de vaga

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
    }

    val formattedTimer = remember(secondsLeft) {
        val min = secondsLeft / 60
        val sec = secondsLeft % 60
        String.format("%02d:%02d", min, sec)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Seguro", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val appointmentId = "BP-" + (100000..999999).random()
                            val appointment = Appointment(
                                id = appointmentId,
                                salon = salon,
                                service = service,
                                staff = null,
                                dateDisplay = "Hoje",
                                timeSlot = slotTime,
                                finalPrice = pricing.finalPrice,
                                status = AppointmentStatus.CONFIRMED,
                                bookedAtIso = System.currentTimeMillis().toString(),
                                voucherQrCode = appointmentId,
                                walkingTimeMin = salon.walkTimeMinutes
                            )
                            SalonRepository.addAppointment(appointment)
                            onBookingConfirmed(appointmentId)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Confirmar Agendamento", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(BackgroundLight)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Barra de Timer de 10 min
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Horário reservado por $formattedTimer min",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
            }

            // Resumo do Agendamento
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Resumo da Reserva", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(color = OutlineBorder)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estabelecimento:", fontSize = 12.sp, color = Slate600)
                        Text(salon.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Serviço:", fontSize = 12.sp, color = Slate600)
                        Text(service.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Horário:", fontSize = 12.sp, color = Slate600)
                        Text("Hoje às $slotTime", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SereneTeal)
                    }
                    HorizontalDivider(color = OutlineBorder)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Valor Base:", fontSize = 12.sp, color = Slate600)
                        Text("R$ ${String.format("%.2f", pricing.basePrice)}", fontSize = 12.sp)
                    }
                    if (pricing.hasDiscount) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Desconto Aplicado:", fontSize = 12.sp, color = CoralPromo)
                            Text("- R$ ${String.format("%.2f", pricing.basePrice - pricing.finalPrice)} (${pricing.discountPct}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CoralPromo)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Final:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("R$ ${String.format("%.2f", pricing.finalPrice)}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = SereneTeal)
                    }
                }
            }

            // Formas de Pagamento
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Forma de Pagamento (Simulação)", style = MaterialTheme.typography.titleMedium)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Cartão
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (paymentMethod == "card") MintSurface else BackgroundLight, RoundedCornerShape(10.dp))
                                .clickable { paymentMethod = "card" }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = SereneTeal)
                                Text("Cartão", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Pix
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (paymentMethod == "pix") MintSurface else BackgroundLight, RoundedCornerShape(10.dp))
                                .clickable { paymentMethod = "pix" }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = SereneTeal)
                                Text("Pix Seguro", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (paymentMethod == "card") {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text("Número do Cartão de Teste (Luhn)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = holderName,
                            onValueChange = { holderName = it },
                            label = { Text("Nome do Titular") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // Aviso Obrigatório de Rodapé (Literalidade da Seção 6.2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Aviso de Teste: Pagamento simulado — nenhum valor será cobrado neste teste.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ao concluir, você autoriza o salão a reter até 30% do valor em caso de não comparecimento.",
                        fontSize = 10.sp,
                        color = Slate600
                    )
                }
            }
        }
    }
}
