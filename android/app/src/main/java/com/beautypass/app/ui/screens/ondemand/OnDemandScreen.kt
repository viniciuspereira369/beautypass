package com.beautypass.app.ui.screens.ondemand

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.PricingEngine
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.model.Service
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.BeautyPassImage
import com.beautypass.app.ui.components.RadarScanView
import com.beautypass.app.ui.components.bounceClick

enum class OnDemandMatchType(
    val title: String,
    val subtitle: String,
    val badgeColor: Color,
    val iconColor: Color
) {
    IDEAL(
        title = "MATCH IDEAL",
        subtitle = "Melhor equilíbrio distância + avaliação + desconto",
        badgeColor = Color(0xFFECFDF5),
        iconColor = DeepTeal
    ),
    CLOSEST(
        title = "MAIS PRÓXIMO",
        subtitle = "Menor caminhada a pé agora",
        badgeColor = Color(0xFFFEF3C7),
        iconColor = Color(0xFF92400E)
    ),
    ECONOMIC(
        title = "MAIS ECONÔMICO",
        subtitle = "Maior desconto de ociosidade imediata",
        badgeColor = Color(0xFFECFDF5),
        iconColor = EmeraldSuccess
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

private data class DemandServiceCategoryOption(
    val categoryKey: String,
    val label: String,
    val title: String,
    val subtitle: String,
    val baseFrom: Int,
    val promoFrom: Int,
    val discountBadge: String,
    val icon: ImageVector,
    val bgTint: Color,
    val iconTint: Color
)

private data class DemandWindowOption(
    val key: String,
    val title: String,
    val subtitle: String
)

/**
 * Tela 4: Fluxo de 2 Passos "Pedir Agora" (#screen-demand-service -> #screen-uber-demand)
 * espelhado 1:1 com beautypass_app.html.
 */
@Composable
fun OnDemandScreen(
    onBackClick: () -> Unit,
    onProceedToCheckout: (salonId: String, serviceId: String, slotTime: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val serviceCategoryOptions = remember {
        listOf(
            DemandServiceCategoryOption(
                categoryKey = "Cabelo",
                label = "Cabelo & Escova",
                title = "Cabelo & Escova",
                subtitle = "Corte, escova modelada, hidratação e finalização rápida",
                baseFrom = 120,
                promoFrom = 78,
                discountBadge = "ATÉ -35%",
                icon = Icons.Filled.ContentCut,
                bgTint = Color(0xFFCCFBF1),
                iconTint = DeepTeal
            ),
            DemandServiceCategoryOption(
                categoryKey = "Unhas",
                label = "Unhas & Manicure",
                title = "Unhas & Manicure",
                subtitle = "Esmaltação em gel, blindagem, manicure & pedicure",
                baseFrom = 65,
                promoFrom = 45,
                discountBadge = "ATÉ -30%",
                icon = Icons.Filled.Spa,
                bgTint = Color(0xFFFCE7F3),
                iconTint = Color(0xFFBE185D)
            ),
            DemandServiceCategoryOption(
                categoryKey = "Barba",
                label = "Barba & Corte Masculino",
                title = "Barba & Corte",
                subtitle = "Corte executivo, barba terapia com toalha quente e acabamento",
                baseFrom = 90,
                promoFrom = 67,
                discountBadge = "ATÉ -25%",
                icon = Icons.Filled.ContentCut,
                bgTint = Color(0xFFFEF3C7),
                iconTint = Color(0xFFB45309)
            ),
            DemandServiceCategoryOption(
                categoryKey = "Massagem",
                label = "Massagem & Spa",
                title = "Massagem & Spa",
                subtitle = "Drenagem linfática, pedras quentes e relaxamento profundo",
                baseFrom = 190,
                promoFrom = 133,
                discountBadge = "ATÉ -30%",
                icon = Icons.Filled.Spa,
                bgTint = Color(0xFFE0E7FF),
                iconTint = Color(0xFF4338CA)
            ),
            DemandServiceCategoryOption(
                categoryKey = "Estética",
                label = "Estética Facial",
                title = "Estética Facial",
                subtitle = "Limpeza de pele profunda, peeling de diamante e design de olhar",
                baseFrom = 80,
                promoFrom = 56,
                discountBadge = "ATÉ -30%",
                icon = Icons.Filled.Face,
                bgTint = Color(0xFFDCFCE7),
                iconTint = Color(0xFF15803D)
            )
        )
    }

    val timeWindows = remember {
        listOf(
            DemandWindowOption("2h", "Próximas 2h", "Maior desconto"),
            DemandWindowOption("tarde", "Hoje à Tarde", "14h às 18h"),
            DemandWindowOption("noite", "Hoje à Noite", "18h às 21h")
        )
    }

    // Passo 1 = Escolha do Serviço + Janela (#screen-demand-service)
    // Passo 2 = Radar + Mapa Estilo Uber + 3 Opções Calibradas (#screen-uber-demand)
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedCategoryOption by remember { mutableStateOf(serviceCategoryOptions.first()) }
    var selectedWindow by remember { mutableStateOf(timeWindows.first()) }
    var isScanning by remember { mutableStateOf(false) }

    val options = remember(selectedCategoryOption, selectedWindow) {
        buildCalibratedOptions(
            salons = SalonRepository.salons,
            selectedCategory = selectedCategoryOption.categoryKey
        )
    }

    var selectedOption by remember(options) { mutableStateOf(options.first()) }

    if (currentStep == 1) {
        // ============================================================
        // PASSO 1: #screen-demand-service ("O que você deseja fazer agora?")
        // ============================================================
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(AlabasterBg)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp)
        ) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(WarmWhite)
                        .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = ObsidianCharcoal,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(999.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = DeepTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Encaixe Imediato • Tarifa Justa",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepTeal,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(38.dp))
            }

            // Hero Header ("O que você deseja fazer agora?")
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "O que você deseja\nfazer agora?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 23.sp,
                        lineHeight = 28.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Escolha o serviço desejado para localizarmos os salões parceiros com cadeiras ociosas nos próximos minutos.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lista dos 5 Cards Ricos de Serviço
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                serviceCategoryOptions.forEach { item ->
                    val isSelected = item.categoryKey == selectedCategoryOption.categoryKey
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .bounceClick(scaleDown = 0.97f) { selectedCategoryOption = item },
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) Color(0xFFECFDF5) else WarmWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.5.dp,
                            color = if (isSelected) DeepTeal else Color(0xFFE5E7EB)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(item.bgTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = item.iconTint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ObsidianCharcoal,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(999.dp)
                                    ) {
                                        Text(
                                            text = item.discountBadge,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF065F46),
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateGray,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Tabela: R$ ${item.baseFrom}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF9CA3AF),
                                            textDecoration = TextDecoration.LineThrough,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = "A partir de R$ ${item.promoFrom}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DeepTeal,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) DeepTeal else Color.Transparent)
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) DeepTeal else Color(0xFFD1D5DB),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = WarmWhite,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seletor de Janela de Horário ("Quando você quer ser atendido(a)?")
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Quando você quer ser atendido(a)?",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeWindows.forEach { win ->
                        val isWinSelected = win.key == selectedWindow.key
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .bounceClick(scaleDown = 0.96f) { selectedWindow = win },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isWinSelected) Color(0xFFECFDF5) else WarmWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isWinSelected) 2.dp else 1.5.dp,
                                color = if (isWinSelected) DeepTeal else Color(0xFFE5E7EB)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = win.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isWinSelected) DeepTeal else ObsidianCharcoal,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = win.subtitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isWinSelected) Color(0xFF065F46) else SlateGray,
                                        fontSize = 10.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Botão Principal "Buscar Encaixe no Mapa"
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .bounceClick(scaleDown = 0.96f) {
                            isScanning = true
                            currentStep = 2
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = DeepTeal,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Buscar Encaixe no Mapa",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = WarmWhite,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = WarmWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    } else if (isScanning) {
        // ============================================================
        // PASSO 2A: Radar Scanner Animado antes de abrir o mapa estilo Uber
        // ============================================================
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianCharcoal)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RadarScanView(
                categoryName = "${selectedCategoryOption.label} • ${selectedWindow.title}",
                onScanFinished = { isScanning = false }
            )
        }
    } else {
        // ============================================================
        // PASSO 2B: #screen-uber-demand (Mapa de Rota a Pé + Bottom Sheet com 3 Matches)
        // ============================================================
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(AlabasterBg)
        ) {
            // 1. Mapa Superior com Rota a Pé estilo Uber (40% da altura)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color(0xFFE8EEF2))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Quadras e parques urbanos
                    drawRect(
                        color = Color(0xFFD5E8DC),
                        topLeft = Offset(w * 0.08f, h * 0.12f),
                        size = androidx.compose.ui.geometry.Size(w * 0.34f, h * 0.36f)
                    )
                    drawRect(
                        color = Color(0xFFDDE3EA),
                        topLeft = Offset(w * 0.52f, h * 0.18f),
                        size = androidx.compose.ui.geometry.Size(w * 0.38f, h * 0.32f)
                    )

                    // Ruas principais
                    val streetPaint = Color.White
                    for (i in 1..4) {
                        val y = h * (i / 5f)
                        drawLine(streetPaint, Offset(0f, y), Offset(w, y), strokeWidth = 14f)
                    }
                    for (j in 1..4) {
                        val x = w * (j / 5f)
                        drawLine(streetPaint, Offset(x, 0f), Offset(x, h), strokeWidth = 14f)
                    }

                    // Rota tracejada a pé entre o usuário e o salão selecionado
                    val routePath = Path().apply {
                        moveTo(w * 0.22f, h * 0.76f)
                        cubicTo(
                            w * 0.38f, h * 0.72f,
                            w * 0.52f, h * 0.38f,
                            w * 0.74f, h * 0.34f
                        )
                    }
                    drawPath(
                        path = routePath,
                        color = DeepTeal,
                        style = Stroke(
                            width = 9f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 0f)
                        )
                    )

                    // Ponto do Usuário ("Você")
                    drawCircle(
                        color = DeepTeal.copy(alpha = 0.22f),
                        radius = 28f,
                        center = Offset(w * 0.22f, h * 0.76f)
                    )
                    drawCircle(
                        color = DeepTeal,
                        radius = 11f,
                        center = Offset(w * 0.22f, h * 0.76f)
                    )

                    // Ponto do Salão Selecionado
                    drawCircle(
                        color = LuminousMint.copy(alpha = 0.4f),
                        radius = 32f,
                        center = Offset(w * 0.74f, h * 0.34f)
                    )
                    drawCircle(
                        color = ObsidianCharcoal,
                        radius = 14f,
                        center = Offset(w * 0.74f, h * 0.34f)
                    )
                }

                // Top Bar Flutuante sobre o mapa (Voltar para Serviço + Chip do serviço ativo)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(WarmWhite)
                            .clickable { currentStep = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Alterar Serviço",
                            tint = ObsidianCharcoal,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier.clickable { currentStep = 1 },
                        color = WarmWhite.copy(alpha = 0.96f),
                        shape = RoundedCornerShape(999.dp),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess)
                            )
                            Text(
                                text = "${selectedCategoryOption.label} • ${selectedWindow.title}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = ObsidianCharcoal,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "Alterar",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DeepTeal,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(38.dp))
                }

                // Tag flutuante de tempo de caminhada até o salão selecionado
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 14.dp),
                    color = ObsidianCharcoal,
                    shape = RoundedCornerShape(999.dp),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = LuminousMint,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${selectedOption.salon.walkMinutes} min a pé (${selectedOption.salon.distanceKm} km) até ${selectedOption.salon.name}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = WarmWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 2. Bottom Sheet estilo Uber com Trajeto a Pé + 3 Opções Calibradas
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .padding(bottom = 96.dp)
            ) {
                // Handle do Bottom Sheet
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFD1D5DB))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Salões com Encaixe Imediato",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ObsidianCharcoal,
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = "Toque em um salão para ver a rota a pé e reservar",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGray,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = "3 disponíveis",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepTeal,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Passo a passo da caminhada até o salão selecionado (igual ao #uber-route-steps-box da Web)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DirectionsWalk,
                                    contentDescription = null,
                                    tint = DeepTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Trajeto a pé até ${selectedOption.salon.name}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF065F46),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Text(
                                text = "${selectedOption.salon.walkMinutes} min • ${selectedOption.salon.distanceKm} km",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepTeal,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Siga em direção a ${selectedOption.salon.neighborhood} • 2. Acesso principal em ${selectedOption.salon.address} • 3. Apresente o QR Code na recepção.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF047857),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Os 3 Cards Calibrados (MATCH IDEAL, MAIS PRÓXIMO, MAIS ECONÔMICO)
                options.forEach { option ->
                    val isSelected = selectedOption.type == option.type
                    OnDemandOptionCard(
                        option = option,
                        isSelected = isSelected,
                        onClick = { selectedOption = option }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Botão de Confirmação estilo Uber ("Confirmar Reserva Agora")
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .bounceClick(scaleDown = 0.96f) {
                            val slotTime = selectedOption.salon.discountSlots.firstOrNull()?.time ?: "14:00"
                            onProceedToCheckout(
                                selectedOption.salon.id,
                                selectedOption.service.id,
                                slotTime
                            )
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = DeepTeal,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Confirmar Reserva Agora",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = WarmWhite,
                                fontSize = 14.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = WarmWhite,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnDemandOptionCard(
    option: OnDemandOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(scaleDown = 0.97f) { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) Color(0xFFECFDF5) else WarmWhite,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) DeepTeal else BorderSubtle
        ),
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = option.type.badgeColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = option.type.iconColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = option.type.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = option.type.iconColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Text(
                    text = "${option.salon.walkMinutes} min a pé • ${option.salon.distanceKm} km",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ObsidianCharcoal,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    BeautyPassImage(
                        model = option.salon.imageUrl,
                        contentDescription = option.salon.name,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column {
                        Text(
                            text = option.salon.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ObsidianCharcoal,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = option.service.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGray,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${option.salon.rating} (${option.salon.reviewCount})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ObsidianCharcoal,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "• ${option.salon.neighborhood}",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateGray),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "R$ ${option.basePrice.toInt()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateGray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                    Text(
                        text = "R$ ${option.finalPrice.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepTeal
                        )
                    )
                    Surface(
                        color = EconomyGreenBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "-${option.discountPct}% OFF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EconomyGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun buildCalibratedOptions(
    salons: List<Salon>,
    selectedCategory: String
): List<OnDemandOption> {
    val matchingSalons = salons.filter { s ->
        s.category.equals(selectedCategory, ignoreCase = true) ||
            s.services.any { it.category.equals(selectedCategory, ignoreCase = true) }
    }.ifEmpty { salons }

    val idealSalon = matchingSalons.first()
    val closestSalon = salons.minByOrNull { it.walkMinutes } ?: salons[1 % salons.size]
    val economicSalon = salons.maxByOrNull { s ->
        s.discountSlots.maxOfOrNull { it.discountPercent } ?: 0
    } ?: salons.last()

    fun makeOption(type: OnDemandMatchType, salon: Salon, forcedDisc: Int): OnDemandOption {
        val service = salon.services.find {
            it.category.equals(selectedCategory, ignoreCase = true)
        } ?: salon.services.first()

        val disc = salon.discountSlots.maxOfOrNull { it.discountPercent } ?: forcedDisc
        val quote = PricingEngine.calculateQuote(
            basePrice = service.basePrice,
            discountPercent = disc,
            occupancyRatio = 0.25,
            minutesUntilSlot = 35,
            isPeakHour = false
        )
        return OnDemandOption(
            type = type,
            salon = salon,
            service = service,
            basePrice = service.basePrice,
            finalPrice = quote.finalPrice,
            discountPct = quote.effectiveDiscountPercent
        )
    }

    return listOf(
        makeOption(OnDemandMatchType.IDEAL, idealSalon, 25),
        makeOption(OnDemandMatchType.CLOSEST, closestSalon, 20),
        makeOption(OnDemandMatchType.ECONOMIC, economicSalon, 35)
    )
}
