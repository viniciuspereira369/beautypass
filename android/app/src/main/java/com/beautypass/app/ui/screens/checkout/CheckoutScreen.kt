package com.beautypass.app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.LuhnValidator
import com.beautypass.app.data.PricingEngine
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Appointment
import com.beautypass.app.model.AppointmentStatus
import com.beautypass.app.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tela 5A: Checkout Seguro com Congelamento de Preço e Validação Luhn.
 *
 * Implementado conforme especificações de:
 * - ORIGINAL_REQUEST.md (R1.5)
 * - documenta_o_t_cnica_prot_tipo_de_valida_o.md (Seção 7 e Seção 6.2)
 * - Timer regressivo de 10 min, validação Luhn local, disclaimers obrigatórios e persistência.
 */
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
        SalonRepository.getSalonById(salonId) ?: SalonRepository.salons.first()
    }
    val service = remember(serviceId) {
        salon.services.find { it.id == serviceId } ?: salon.services.first()
    }

    // Congela imutavelmente o snapshot de preço no momento de abertura do checkout
    val frozenSnapshot = remember(slotTime, service) {
        val calculated = SalonRepository.calculatePricing(salon, slotTime, service)
        PricingEngine.freezeSnapshot(calculated, salon.id, service.id)
    }

    var paymentMethod by remember { mutableStateOf("card") } // "card" ou "pix"
    var rawCardNumber by remember { mutableStateOf("4532015112830366") }
    var holderName by remember { mutableStateOf("Camila Alves") }
    var expiryDate by remember { mutableStateOf("12/28") }
    var cvv by remember { mutableStateOf("789") }

    // Validação de Luhn em tempo real
    val isCardValid = remember(rawCardNumber) {
        LuhnValidator.isValid(rawCardNumber)
    }

    // Cronômetro regressivo de 10 minutos (600 segundos)
    var secondsLeft by remember { mutableStateOf(600) }
    var isExpired by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
        isExpired = true
    }

    val formattedTimer = remember(secondsLeft) {
        val min = secondsLeft / 60
        val sec = secondsLeft % 60
        String.format("%02d:%02d", min, sec)
    }

    val canProceed = remember(paymentMethod, isCardValid, holderName, isExpired) {
        if (isExpired) false
        else if (paymentMethod == "pix") true
        else isCardValid && holderName.trim().length >= 3
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout Seguro",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = OceanicCharcoal)
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Aviso de Transparência e Disclaimer Obrigatório (Seção 6.2)
                    Text(
                        text = "Pagamento simulado — nenhum valor será cobrado neste teste.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeutralMuted
                    )

                    Button(
                        onClick = {
                            if (canProceed) {
                                val generatedId = "BP-" + (100000..999999).random()
                                val nowIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                                    timeZone = TimeZone.getTimeZone("UTC")
                                }.format(Date())

                                val appointment = Appointment(
                                    id = generatedId,
                                    salon = salon,
                                    service = service,
                                    staff = salon.staff.firstOrNull(),
                                    dateDisplay = "Hoje",
                                    timeSlot = slotTime,
                                    finalPrice = frozenSnapshot.priceFinal,
                                    status = AppointmentStatus.CONFIRMED,
                                    bookedAtIso = nowIso,
                                    voucherQrCode = generatedId,
                                    walkingTimeMin = salon.walkTimeMinutes,
                                    pricingSnapshot = frozenSnapshot
                                )

                                SalonRepository.addAppointment(appointment)
                                onBookingConfirmed(generatedId)
                            }
                        },
                        enabled = canProceed,
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Confirmar Reserva • R$ ${String.format("%.2f", frozenSnapshot.priceFinal)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Disclaimer de autorização de retenção
                    Text(
                        text = "Ao concluir, você autoriza o salão a reter até 30% do valor em caso de não comparecimento.",
                        fontSize = 10.sp,
                        color = NeutralMuted
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Barra do Timer Regressivo de 10 minutos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (secondsLeft > 120) MintSurface else CoralPromoBg
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (secondsLeft > 120) OutlineVariant else CoralPromo
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (secondsLeft > 120) SereneTeal else CoralPromo,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isExpired) "Vaga Expirada!" else "Vaga retida por tempo limitado",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (secondsLeft > 120) OceanicCharcoal else CoralPromoText
                        )
                    }

                    Text(
                        text = formattedTimer,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = if (secondsLeft > 120) SereneTeal else CoralPromo
                    )
                }
            }

            // Resumo do Pedido com Snapshot de Preço Congelado
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumo da Reserva",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = salon.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                    Text(
                        text = "${salon.address} • ${salon.neighborhood}",
                        fontSize = 11.sp,
                        color = NeutralMuted
                    )

                    Divider(
                        color = OutlineVariant,
                        thickness = 0.8.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = service.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OceanicCharcoal
                            )
                            Text(
                                text = "Horário: $slotTime (${service.durationMinutes} min)",
                                fontSize = 11.sp,
                                color = NeutralMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            if (frozenSnapshot.discountPct > 0) {
                                Text(
                                    text = "R$ ${String.format("%.2f", frozenSnapshot.priceBase)}",
                                    fontSize = 11.sp,
                                    color = Slate400,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                            Text(
                                text = "R$ ${String.format("%.2f", frozenSnapshot.priceFinal)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SereneTeal
                            )
                        }
                    }

                    if (frozenSnapshot.discountPct > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(MintSurface, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${frozenSnapshot.badgeLabel} (-${frozenSnapshot.discountPct}%): ${frozenSnapshot.priceExplanation}",
                                fontSize = 10.sp,
                                color = EconomyGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Seleção de Método de Pagamento
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Forma de Pagamento",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Cartão
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (paymentMethod == "card") MintSurface else CanvasBase)
                                .border(
                                    width = if (paymentMethod == "card") 1.5.dp else 1.dp,
                                    color = if (paymentMethod == "card") SereneTeal else OutlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { paymentMethod = "card" }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = if (paymentMethod == "card") SereneTeal else NeutralMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cartão de Crédito",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (paymentMethod == "card") SereneTeal else OceanicCharcoal
                                )
                            }
                        }

                        // Pix
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (paymentMethod == "pix") MintSurface else CanvasBase)
                                .border(
                                    width = if (paymentMethod == "pix") 1.5.dp else 1.dp,
                                    color = if (paymentMethod == "pix") SereneTeal else OutlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { paymentMethod = "pix" }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = if (paymentMethod == "pix") SereneTeal else NeutralMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Pix Simulado",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (paymentMethod == "pix") SereneTeal else OceanicCharcoal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (paymentMethod == "card") {
                        // Formulário de Cartão com Validação de Luhn em Tempo Real
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = rawCardNumber,
                                onValueChange = { rawCardNumber = it.filter { c -> c.isDigit() } },
                                label = { Text("Número do Cartão de Crédito") },
                                trailingIcon = {
                                    if (rawCardNumber.isNotEmpty()) {
                                        if (isCardValid) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Válido",
                                                tint = SuccessGreen
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.Error,
                                                contentDescription = "Inválido",
                                                tint = ErrorRed
                                            )
                                        }
                                    }
                                },
                                supportingText = {
                                    if (rawCardNumber.isNotEmpty()) {
                                        if (isCardValid) {
                                            Text("✓ Cartão válido (Algoritmo de Luhn OK)", color = SuccessGreen, fontSize = 11.sp)
                                        } else {
                                            Text("✗ Cartão inválido pelo Algoritmo de Luhn", color = ErrorRed, fontSize = 11.sp)
                                        }
                                    }
                                },
                                isError = rawCardNumber.isNotEmpty() && !isCardValid,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SereneTeal,
                                    unfocusedBorderColor = OutlineVariant
                                )
                            )

                            OutlinedTextField(
                                value = holderName,
                                onValueChange = { holderName = it },
                                label = { Text("Nome Impresso no Cartão") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SereneTeal,
                                    unfocusedBorderColor = OutlineVariant
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = expiryDate,
                                    onValueChange = { expiryDate = it },
                                    label = { Text("Validade") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SereneTeal,
                                        unfocusedBorderColor = OutlineVariant
                                    )
                                )

                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = { cvv = it },
                                    label = { Text("CVV") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SereneTeal,
                                        unfocusedBorderColor = OutlineVariant
                                    )
                                )
                            }
                        }
                    } else {
                        // Painel Pix Simulado
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MintSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Chave Pix de Teste Automática", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OceanicCharcoal)
                                Text("A confirmação do voucher será imediata ao clicar no botão abaixo.", fontSize = 11.sp, color = NeutralMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}
