package com.beautypass.app.ui.screens.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.BeautyPassImage

/**
 * Tela de Mapa (#screen-map): Mapa Vetorial Urbano de São Paulo & Bottom Sheet com Tempos de Caminhada a Pé,
 * espelhada 1:1 com beautypass_app.html.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onSalonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allSalons = remember { SalonRepository.salons }
    var mapSearchQuery by remember { mutableStateOf("") }
    val filteredSalons = remember(allSalons, mapSearchQuery) {
        if (mapSearchQuery.isBlank()) allSalons
        else allSalons.filter {
            it.name.contains(mapSearchQuery, ignoreCase = true) ||
                it.neighborhood.contains(mapSearchQuery, ignoreCase = true) ||
                it.address.contains(mapSearchQuery, ignoreCase = true)
        }
    }
    var selectedSalon by remember(filteredSalons) {
        mutableStateOf<Salon?>(filteredSalons.firstOrNull() ?: allSalons.firstOrNull())
    }
    var showLocationWarning by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "mapUserPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AlabasterBg)
    ) {
        // 1. Área do Mapa Vetorial Urbano de São Paulo (50% da altura)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.50f)
                .background(Color(0xFFE8EEF2))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Parques e quadras urbanas
                drawRect(
                    color = Color(0xFFD5E8DC),
                    topLeft = Offset(w * 0.08f, h * 0.12f),
                    size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.35f)
                )
                drawRect(
                    color = Color(0xFFDDE3EA),
                    topLeft = Offset(w * 0.48f, h * 0.15f),
                    size = androidx.compose.ui.geometry.Size(w * 0.42f, h * 0.32f)
                )
                drawRect(
                    color = Color(0xFFDDE3EA),
                    topLeft = Offset(w * 0.14f, h * 0.56f),
                    size = androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.32f)
                )
                drawRect(
                    color = Color(0xFFD5E8DC),
                    topLeft = Offset(w * 0.58f, h * 0.58f),
                    size = androidx.compose.ui.geometry.Size(w * 0.34f, h * 0.30f)
                )

                // Avenidas e Ruas Principais
                val streetColor = Color.White
                for (i in 1..4) {
                    val y = h * (i / 5f)
                    drawLine(
                        color = streetColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 14f
                    )
                }
                for (j in 1..4) {
                    val x = w * (j / 5f)
                    drawLine(
                        color = streetColor,
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 14f
                    )
                }

                // Rota tracejada de caminhada entre o usuário (centro) e o salão selecionado
                val userCenter = Offset(w * 0.48f, h * 0.54f)
                val targetIndex = allSalons.indexOfFirst { it.id == selectedSalon?.id }.coerceAtLeast(0)
                val pinPositions = listOf(
                    Offset(w * 0.25f, h * 0.28f),
                    Offset(w * 0.72f, h * 0.30f),
                    Offset(w * 0.26f, h * 0.74f),
                    Offset(w * 0.74f, h * 0.68f),
                    Offset(w * 0.60f, h * 0.22f)
                )
                val destOffset = pinPositions[targetIndex % pinPositions.size]

                val routePath = Path().apply {
                    moveTo(userCenter.x, userCenter.y)
                    cubicTo(
                        (userCenter.x + destOffset.x) / 2f,
                        userCenter.y,
                        (userCenter.x + destOffset.x) / 2f,
                        destOffset.y,
                        destOffset.x,
                        destOffset.y
                    )
                }
                drawPath(
                    path = routePath,
                    color = DeepTeal,
                    style = Stroke(
                        width = 8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                    )
                )

                // Marcador Pulsante da Localização do Usuário ("Você")
                drawCircle(
                    color = DeepTeal.copy(alpha = 0.22f),
                    radius = 28f * pulseScale,
                    center = userCenter
                )
                drawCircle(
                    color = Color.White,
                    radius = 13f,
                    center = userCenter
                )
                drawCircle(
                    color = DeepTeal,
                    radius = 9f,
                    center = userCenter
                )
            }

            // Barra de Busca Flutuante sobre o Mapa (igual ao .map-floating-header da Web)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .shadow(6.dp, RoundedCornerShape(999.dp)),
                        shape = RoundedCornerShape(999.dp),
                        color = WarmWhite
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = SlateGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                if (mapSearchQuery.isEmpty()) {
                                    Text(
                                        text = "Buscar bairro ou salão em SP...",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF9CA3AF),
                                            fontSize = 12.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                                BasicTextField(
                                    value = mapSearchQuery,
                                    onValueChange = { mapSearchQuery = it },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        color = ObsidianCharcoal,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    cursorBrush = SolidColor(DeepTeal),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (mapSearchQuery.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Limpar",
                                    tint = SlateGray,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { mapSearchQuery = "" }
                                )
                            }
                        }
                    }

                    // Botão Circular Minha Localização (GPS)
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(6.dp, CircleShape)
                            .clickable { showLocationWarning = !showLocationWarning },
                        shape = CircleShape,
                        color = WarmWhite
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = "Minha Localização (GPS)",
                                tint = DeepTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Banner Amarelo de Fallback de Localização no Mapa
                if (showLocationWarning) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = Color(0xFF92400E),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "GPS indisponível: exibindo região central de São Paulo (Av. Paulista)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Pins Interativos dos Salões com Desconto e Preço sobre o Mapa
            val pinSpecs = listOf(
                Triple(0, 0.08f, 0.24f),
                Triple(1, 0.56f, 0.25f),
                Triple(2, 0.08f, 0.68f),
                Triple(3, 0.56f, 0.64f)
            )
            pinSpecs.forEach { (idx, relX, relY) ->
                if (idx < allSalons.size) {
                    val salon = allSalons[idx]
                    val isSelected = selectedSalon?.id == salon.id
                    val maxDisc = salon.discountSlots.maxOfOrNull { it.discountPercent } ?: 25
                    val minPrice = salon.services.minOfOrNull { it.basePrice } ?: 120.0
                    val promoPrice = (minPrice * (100 - maxDisc) / 100.0).toInt()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = (relX * 320).dp,
                                top = (relY * 240).dp
                            )
                    ) {
                        Surface(
                            modifier = Modifier.clickable { selectedSalon = salon },
                            color = if (isSelected) DeepTeal else WarmWhite,
                            shape = RoundedCornerShape(999.dp),
                            shadowElevation = 6.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) LuminousMint else DeepTeal
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "-$maxDisc% • R$ $promoPrice",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) WarmWhite else ObsidianCharcoal,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Badge Inferior Flutuante de Rota a Pé Selecionada
            selectedSalon?.let { current ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    color = ObsidianCharcoal,
                    shape = RoundedCornerShape(999.dp),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = LuminousMint,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${current.walkMinutes} min a pé (${current.distanceKm} km) até ${current.name}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = WarmWhite,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 2. Bottom Sheet com Trajeto a Pé e Lista de Salões e Badges "min a pé" (50% da altura)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.50f),
            color = WarmWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(BorderSubtle)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Salões Próximos em São Paulo • Raio 10 km",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Passo a passo da rota a pé (#map-route-steps-box da Web)
                selectedSalon?.let { current ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rota a pé: ${current.name}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF065F46),
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "${current.walkMinutes} min a pé • ${current.distanceKm} km",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepTeal,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Text(
                                text = "Siga por ${current.neighborhood} até ${current.address}. Toque no card abaixo para escolher o horário.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF047857),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 86.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredSalons, key = { it.id }) { salon ->
                        val isSelected = selectedSalon?.id == salon.id
                        val maxDisc = salon.discountSlots.maxOfOrNull { it.discountPercent } ?: 20
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSalon = salon
                                    onSalonClick(salon.id)
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFFECFDF5) else AlabasterBg,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) DeepTeal else BorderSubtle
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    BeautyPassImage(
                                        model = salon.imageUrl,
                                        contentDescription = salon.name,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column {
                                        Text(
                                            text = salon.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = ObsidianCharcoal
                                            )
                                        )
                                        Text(
                                            text = "${salon.neighborhood} • ${salon.distanceKm} km",
                                            style = MaterialTheme.typography.labelSmall.copy(color = SlateGray)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                color = WarmWhite,
                                                shape = RoundedCornerShape(999.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.DirectionsWalk,
                                                        contentDescription = null,
                                                        tint = DeepTeal,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Text(
                                                        text = "${salon.walkMinutes} min a pé",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = ObsidianCharcoal,
                                                            fontWeight = FontWeight.ExtraBold
                                                        )
                                                    )
                                                }
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Star,
                                                    contentDescription = null,
                                                    tint = AmberWarning,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = "${salon.rating}",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = ObsidianCharcoal
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                Surface(
                                    color = EconomyGreenBg,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "-$maxDisc% OFF",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EconomyGreen,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
