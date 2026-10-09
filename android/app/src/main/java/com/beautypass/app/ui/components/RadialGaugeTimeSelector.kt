package com.beautypass.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.BookingEngine
import com.beautypass.app.data.PricingEngine
import com.beautypass.app.model.DiscountSlot
import com.beautypass.app.theme.*

/**
 * Seletor Radial Analógico-Digital de Horários (180° Semicircular Gauge Clock).
 *
 * Implementado conforme especificações de:
 * - documenta_o_ui_ux_agendamento_de_corridas_aut_nomas.md (Inspiração 1)
 * - DESIGN.md (Serene Mint & Teal Tokens)
 * - documenta_o_t_cnica_prot_tipo_de_valida_o.md (Seção 6.2 - Subtextos Literais Obrigatórios)
 */
@Composable
fun RadialGaugeTimeSelector(
    selectedTime: String,
    basePrice: Double,
    discountSlots: List<DiscountSlot> = emptyList(),
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 41 slots de 15 minutos: das 09:00 às 19:00
    val timeSlots = remember {
        val slots = mutableListOf<String>()
        var currentMinutes = 9 * 60 // 09:00
        val endMinutes = 19 * 60   // 19:00
        while (currentMinutes <= endMinutes) {
            slots.add(BookingEngine.minutesToTime(currentMinutes))
            currentMinutes += 15
        }
        slots
    }

    // Índice atual selecionado na lista
    val currentIndex = remember(selectedTime, timeSlots) {
        val idx = timeSlots.indexOf(selectedTime)
        if (idx != -1) idx else 20 // padrão: ~14:00
    }

    var showInfoDialog by remember { mutableStateOf(false) }

    // Calcula precificação dinâmica determinística do horário selecionado
    val slotPromo = remember(selectedTime, discountSlots) {
        discountSlots.find { it.time == selectedTime }
    }

    val pricing = remember(selectedTime, basePrice, slotPromo) {
        PricingEngine.calculateSlotPricing(
            time = selectedTime,
            basePrice = basePrice,
            discountPct = slotPromo?.discountPct ?: 0,
            isUrgent = slotPromo?.type == "urgent"
        )
    }

    val currentMinutes = remember(selectedTime) {
        BookingEngine.timeToMinutes(selectedTime)
    }
    val isAfternoon = currentMinutes >= 12 * 60

    // Animação suave do ângulo do gauge
    val targetFraction = (currentIndex.toFloat() / (timeSlots.size - 1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 250),
        label = "gaugeProgress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceWhite, RoundedCornerShape(18.dp))
            .border(1.dp, OutlineVariant, RoundedCornerShape(18.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterVertically
    ) {
        // Cabeçalho do Seletor com Botão de Informação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Seletor de Horário Inteligente",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanicCharcoal
                )
                Text(
                    text = "Ajuste fino em blocos de 15 minutos",
                    fontSize = 11.sp,
                    color = NeutralMuted
                )
            }

            IconButton(
                onClick = { showInfoDialog = true },
                modifier = Modifier
                    .size(28.dp)
                    .background(MintSurface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Por que o preço varia?",
                    tint = SereneTeal,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Gauge Semicircular de 180° em Canvas
        Box(
            modifier = Modifier
                .width(240.dp)
                .height(135.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.width / 2f) - strokeWidth
                val topLeft = Offset(strokeWidth, size.height - radius)
                val arcSize = Size(radius * 2f, radius * 2f)

                // 1. Trilha de Fundo Semicircular (180°)
                drawArc(
                    color = OutlineVariant,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // 2. Arco de Progresso Preenchido Dinamicamente
                val sweepAngle = 180f * animatedProgress
                val arcColor = if (pricing.hasDiscount) EconomyGreen else SereneTeal

                drawArc(
                    color = arcColor,
                    startAngle = 180f,
                    sweepAngle = sweepAngle.coerceAtLeast(1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // 3. Ponteiro indicador de extremidade
                val angleRad = Math.toRadians((180f + sweepAngle).toDouble())
                val center = Offset(size.width / 2f, size.height)
                val indicatorX = center.x + radius * Math.cos(angleRad).toFloat()
                val indicatorY = center.y + radius * Math.sin(angleRad).toFloat()

                drawCircle(
                    color = SurfaceWhite,
                    radius = strokeWidth * 0.45f,
                    center = Offset(indicatorX, indicatorY)
                )
                drawCircle(
                    color = arcColor,
                    radius = strokeWidth * 0.25f,
                    center = Offset(indicatorX, indicatorY)
                )
            }

            // Mostrador Digital Grande Central & Cápsula AM/PM
            Column(
                horizontalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = selectedTime,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OceanicCharcoal,
                    letterSpacing = (-0.03).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Alternador de Período em Cápsula Dupla (MANHÃ / TARDE) - Nielsen #5
                Row(
                    modifier = Modifier
                        .background(CanvasBase, CapsuleShape)
                        .border(1.dp, OutlineVariant, CapsuleShape)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (!isAfternoon) SereneTeal else Color.Transparent,
                                shape = CapsuleShape
                            )
                            .clickable {
                                // Pula para horário da manhã (ex: 10:00)
                                val morningIdx = timeSlots.indexOfFirst {
                                    BookingEngine.timeToMinutes(it) < 12 * 60
                                }.coerceAtLeast(0)
                                onTimeSelected(timeSlots[morningIdx])
                            }
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "MANHÃ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isAfternoon) SurfaceWhite else NeutralMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isAfternoon) SereneTeal else Color.Transparent,
                                shape = CapsuleShape
                            )
                            .clickable {
                                // Pula para horário da tarde (ex: 14:00)
                                val afternoonIdx = timeSlots.indexOfFirst {
                                    BookingEngine.timeToMinutes(it) >= 12 * 60
                                }.coerceAtLeast(12)
                                onTimeSelected(timeSlots[afternoonIdx])
                            }
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "TARDE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAfternoon) SurfaceWhite else NeutralMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Slider para regulação fina em passos de 15 minutos
        Slider(
            value = currentIndex.toFloat(),
            onValueChange = { newIdxFloat ->
                val newIdx = newIdxFloat.toInt().coerceIn(0, timeSlots.size - 1)
                onTimeSelected(timeSlots[newIdx])
            },
            valueRange = 0f..(timeSlots.size - 1).toFloat(),
            steps = timeSlots.size - 2,
            colors = SliderDefaults.colors(
                thumbColor = SereneTeal,
                activeTrackColor = if (pricing.hasDiscount) EconomyGreen else SereneTeal,
                inactiveTrackColor = OutlineVariant
            ),
            modifier = Modifier.fillMaxWidth(0.92f)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Atalhos de Horários Rápidos Sugeridos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("09:00", fontSize = 10.sp, color = NeutralMuted)
            Text("12:00", fontSize = 10.sp, color = NeutralMuted)
            Text("15:00", fontSize = 10.sp, color = NeutralMuted)
            Text("19:00", fontSize = 10.sp, color = NeutralMuted)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card de Preço Dinâmico e Subtexto de Transparência (Seção 6.2)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (pricing.hasDiscount) MintSurface else BackgroundLight
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = pricing.badgeLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pricing.hasDiscount) EconomyGreen else OceanicCharcoal
                        )
                        if (pricing.hasDiscount) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(CoralPromo, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "-${pricing.discountPct}% OFF",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = pricing.subtext,
                        fontSize = 11.sp,
                        color = NeutralMuted
                    )
                }

                // Par de Preços Obrigatório
                Column(horizontalAlignment = Alignment.End) {
                    if (pricing.hasDiscount) {
                        Text(
                            text = "R$ ${String.format("%.2f", pricing.basePrice)}",
                            fontSize = 12.sp,
                            color = Slate400,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                    Text(
                        text = "R$ ${String.format("%.2f", pricing.finalPrice)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SereneTeal
                    )
                }
            }
        }
    }

    // Modal de Transparência: "Por que o preço varia?"
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = "Por que o preço varia?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OceanicCharcoal
                )
            },
            text = {
                Text(
                    text = "Salões aplicam preços menores em horários de menor movimento para ocupar a agenda. O desconto é definido pelo salão e não muda após a confirmação do seu agendamento.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = OceanicCharcoal
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Entendido", color = SereneTeal, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
