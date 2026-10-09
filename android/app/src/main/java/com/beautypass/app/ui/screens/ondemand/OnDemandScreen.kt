package com.beautypass.app.ui.screens.ondemand

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.beautypass.app.data.PricingEngine
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.model.Service
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.RadarScanView

enum class OnDemandMatchType(
    val title: String,
    val subtitle: String,
    val badgeColor: Color,
    val iconColor: Color
) {
    IDEAL(
        title = "★ MATCH IDEAL",
        subtitle = "Melhor custo-benefício e reputação estelar",
        badgeColor = MintSurface,
        iconColor = SereneTeal
    ),
    CLOSEST(
        title = "⚡ MAIS PRÓXIMO",
        subtitle = "Menor tempo de caminhada a pé",
        badgeColor = Color(0xFFFEF3C7),
        iconColor = Color(0xFFD97706)
    ),
    ECONOMIC(
        title = "💰 MAIS ECONÔMICO",
        subtitle = "Maior desconto de ociosidade imediata",
        badgeColor = EconomyGreenBg,
        iconColor = EconomyGreen
    )
}

data class OnDemandOption(
    val type: OnDemandMatchType,
    val salon: Salon,
    val service: Service,
    val basePrice: Double,
    val finalPrice: Double,
    val discountPct: Int
)

/**
 * Tela 4: Pedir Agora / Encaixe Imediato sob Demanda estilo Uber.
 *
 * Implementado conforme especificações de:
 * - ORIGINAL_REQUEST.md (R1.4)
 * - Inspiração 2 (documenta_o_ui_ux_app_de_mobilidade_urbana.md)
 * - RadarScanView de 1.8s, cálculo de caminhada a pé (12 min/km), rota visual e 3 opções calibradas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnDemandScreen(
    onBackClick: () -> Unit,
    onProceedToCheckout: (salonId: String, serviceId: String, slotTime: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isScanning by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("hair") }

    val categories = remember {
        listOf(
            "hair" to "Cabelo",
            "nails" to "Unhas",
            "barber" to "Barbearia",
            "massage" to "Massagem",
            "facial" to "Estética"
        )
    }

    // Filtra e calibra as 3 opções inteligentes de match
    val matchOptions = remember(selectedCategory) {
        val candidates = SalonRepository.salons.filter { it.category == selectedCategory }
            .ifEmpty { SalonRepository.salons }

        // Opção 1: Ideal (Maior Rating e bom equilíbrio)
        val idealSalon = candidates.maxByOrNull { it.rating } ?: candidates.first()
        val idealService = idealSalon.services.first()
        val idealPricing = PricingEngine.calculateFairOnDemandPricing(idealService.basePrice, 25)

        // Opção 2: Mais Próximo (Menor distância km)
        val closestSalon = candidates.minByOrNull { it.distanceKm } ?: candidates.first()
        val closestService = closestSalon.services.first()
        val closestPricing = PricingEngine.calculateFairOnDemandPricing(closestService.basePrice, 20)

        // Opção 3: Mais Econômico (Menor preço base ou maior desconto)
        val economicSalon = candidates.sortedBy { it.services.first().basePrice }.firstOrNull() ?: candidates.first()
        val economicService = economicSalon.services.first()
        val economicPricing = PricingEngine.calculateFairOnDemandPricing(economicService.basePrice, 35)

        listOf(
            OnDemandOption(
                type = OnDemandMatchType.IDEAL,
                salon = idealSalon,
                service = idealService,
                basePrice = idealPricing.basePrice,
                finalPrice = idealPricing.finalPrice,
                discountPct = idealPricing.discountPct
            ),
            OnDemandOption(
                type = OnDemandMatchType.CLOSEST,
                salon = closestSalon,
                service = closestService,
                basePrice = closestPricing.basePrice,
                finalPrice = closestPricing.finalPrice,
                discountPct = closestPricing.discountPct
            ),
            OnDemandOption(
                type = OnDemandMatchType.ECONOMIC,
                salon = economicSalon,
                service = economicService,
                basePrice = economicPricing.basePrice,
                finalPrice = economicPricing.finalPrice,
                discountPct = economicPricing.discountPct
            )
        )
    }

    var selectedOptionIndex by remember { mutableStateOf(0) }
    val currentSelectedOption = matchOptions[selectedOptionIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = SereneTeal)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pedir Agora (Encaixe Imediato)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal
                        )
                    }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Encaixe Imediato • ${currentSelectedOption.salon.name}",
                            fontSize = 11.sp,
                            color = NeutralMuted,
                            maxLines = 1
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "R$ ${String.format("%.2f", currentSelectedOption.basePrice)}",
                                fontSize = 12.sp,
                                color = Slate400,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Text(
                                text = "R$ ${String.format("%.2f", currentSelectedOption.finalPrice)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SereneTeal
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onProceedToCheckout(
                                currentSelectedOption.salon.id,
                                currentSelectedOption.service.id,
                                "Agora"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Confirmar Encaixe",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }
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
        ) {
            // Seletor de Categoria
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (catId, catLabel) ->
                    val isSelected = selectedCategory == catId
                    Box(
                        modifier = Modifier
                            .clip(CapsuleShape)
                            .background(if (isSelected) SereneTeal else CanvasBase)
                            .border(1.dp, if (isSelected) SereneTeal else OutlineVariant, CapsuleShape)
                            .clickable {
                                selectedCategory = catId
                                isScanning = true
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = catLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) SurfaceWhite else OceanicCharcoal
                        )
                    }
                }
            }

            // Radar de Varredura Visual de Ociosidade (1.8s)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                RadarScanView(
                    isScanning = isScanning,
                    onScanFinished = { isScanning = false }
                )
            }

            // Traçado Visual de Rota e Distância a Pé da Opção Selecionada
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rota a Pé Estimada",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal
                        )

                        // Badge Flutuante: "X min a pé • Y m" (12 min/km)
                        Box(
                            modifier = Modifier
                                .background(MintSurface, CapsuleShape)
                                .border(1.dp, OutlineVariant, CapsuleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = SereneTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${currentSelectedOption.salon.walkTimeMinutes} min a pé • ${currentSelectedOption.salon.walkDistanceMeters} m",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SereneTealDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Traçado Curvilíneo Bézier em Canvas Representando o Deslocamento Urbano
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(CanvasBase, RoundedCornerShape(12.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val path = Path().apply {
                                moveTo(w * 0.15f, h * 0.7f)
                                quadraticBezierTo(w * 0.5f, h * 0.15f, w * 0.85f, h * 0.4f)
                            }

                            // Linha tracejada em SereneTeal
                            drawPath(
                                path = path,
                                color = SereneTeal,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f))
                                )
                            )

                            // Ponto Inicial (Você)
                            drawCircle(color = SereneTeal, radius = 6.dp.toPx(), center = Offset(w * 0.15f, h * 0.7f))
                            drawCircle(color = SurfaceWhite, radius = 2.dp.toPx(), center = Offset(w * 0.15f, h * 0.7f))

                            // Ponto Final (Salão Destino)
                            drawCircle(color = MintLight, radius = 8.dp.toPx(), center = Offset(w * 0.85f, h * 0.4f))
                            drawCircle(color = SereneTeal, radius = 5.dp.toPx(), center = Offset(w * 0.85f, h * 0.4f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Seção: 3 Opções Inteligentes de Match
            Text(
                text = "Melhores Encaixes Disponíveis",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = OceanicCharcoal,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            matchOptions.forEachIndexed { idx, opt ->
                val isSelected = selectedOptionIndex == idx
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { selectedOptionIndex = idx },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MintSurface else SurfaceWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) SereneTeal else OutlineVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(opt.type.badgeColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = opt.type.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = opt.type.iconColor
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${opt.salon.walkTimeMinutes} min a pé",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanicCharcoal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = opt.salon.imageUrl,
                                contentDescription = opt.salon.name,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = opt.salon.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanicCharcoal
                                )
                                Text(
                                    text = "${opt.salon.neighborhood} • ★ ${opt.salon.rating}",
                                    fontSize = 11.sp,
                                    color = NeutralMuted
                                )
                                Text(
                                    text = "${opt.service.name} • ${opt.service.durationMinutes} min",
                                    fontSize = 11.sp,
                                    color = SereneTeal,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Preço com Desconto On-Demand
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "R$ ${String.format("%.2f", opt.basePrice)}",
                                    fontSize = 11.sp,
                                    color = Slate400,
                                    textDecoration = TextDecoration.LineThrough
                                )
                                Text(
                                    text = "R$ ${String.format("%.2f", opt.finalPrice)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SereneTeal
                                )
                                Box(
                                    modifier = Modifier
                                        .background(CoralPromo, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "-${opt.discountPct}% OFF",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SurfaceWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
