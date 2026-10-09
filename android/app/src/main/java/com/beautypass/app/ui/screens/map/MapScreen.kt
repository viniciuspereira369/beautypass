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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.theme.*

/**
 * Tela de Mapa: Mapa Vetorial Urbano & Bottom Sheet com Tempos de Caminhada a Pé.
 *
 * Implementado conforme especificações de:
 * - documenta_o_ui_ux_app_de_mobilidade_urbana.md (Inspiração 2)
 * - test_validation_session14.py (Asserção mandatória de badges contendo "min a pé")
 * - DESIGN.md (Serene Mint & Teal Tokens)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onSalonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSalon by remember { mutableStateOf<Salon?>(SalonRepository.salons.firstOrNull()) }
    val salons = remember { SalonRepository.salons }

    val infiniteTransition = rememberInfiniteTransition(label = "mapUserPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "userHalo"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mapa de Salões Próximos",
                        fontSize = 17.sp,
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
            // Seção 1: Viewport Cartográfico Vetorial em Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE2E8F0))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Avenidas Principais de São Paulo (Faria Lima, Rebouças, Oscar Freire, Paulista)
                    drawLine(color = Color(0xFFCBD5E1), start = Offset(0f, h * 0.28f), end = Offset(w, h * 0.72f), strokeWidth = 16f)
                    drawLine(color = Color(0xFFCBD5E1), start = Offset(w * 0.18f, 0f), end = Offset(w * 0.82f, h), strokeWidth = 14f)
                    drawLine(color = SurfaceWhite, start = Offset(0f, h * 0.28f), end = Offset(w, h * 0.72f), strokeWidth = 8f)
                    drawLine(color = SurfaceWhite, start = Offset(w * 0.18f, 0f), end = Offset(w * 0.82f, h), strokeWidth = 7f)

                    // Vias Secundárias
                    drawLine(color = Color(0xFFE2E8F0), start = Offset(0f, h * 0.6f), end = Offset(w, h * 0.4f), strokeWidth = 8f)
                    drawLine(color = SurfaceWhite, start = Offset(0f, h * 0.6f), end = Offset(w, h * 0.4f), strokeWidth = 4f)

                    // Ponto Central do Usuário (Halo Pulsante e Marcador Teal)
                    val userCenter = Offset(w * 0.46f, h * 0.52f)
                    drawCircle(color = SereneTeal.copy(alpha = 0.22f), center = userCenter, radius = 24.dp.toPx() * pulseScale)
                    drawCircle(color = SereneTeal, center = userCenter, radius = 8.dp.toPx())
                    drawCircle(color = SurfaceWhite, center = userCenter, radius = 3.dp.toPx())

                    // Pins dos Salões na Região
                    val pinCoords = listOf(
                        Offset(w * 0.32f, h * 0.34f),
                        Offset(w * 0.68f, h * 0.42f),
                        Offset(w * 0.24f, h * 0.20f),
                        Offset(w * 0.76f, h * 0.66f),
                        Offset(w * 0.52f, h * 0.75f)
                    )

                    pinCoords.forEachIndexed { idx, coord ->
                        val isPinSelected = idx == 0
                        val pinRadius = if (isPinSelected) 13.dp.toPx() else 9.dp.toPx()
                        val pinColor = if (isPinSelected) EconomyGreen else SereneTeal

                        drawCircle(color = pinColor, center = coord, radius = pinRadius)
                        drawCircle(color = SurfaceWhite, center = coord, radius = pinRadius * 0.4f)
                    }

                    // Linha conectora de caminhada a pé entre usuário e salão selecionado
                    val targetPin = pinCoords[0]
                    val walkPath = Path().apply {
                        moveTo(userCenter.x, userCenter.y)
                        quadraticBezierTo((userCenter.x + targetPin.x) / 2f + 20f, (userCenter.y + targetPin.y) / 2f - 20f, targetPin.x, targetPin.y)
                    }
                    drawPath(
                        path = walkPath,
                        color = EconomyGreen,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                        )
                    )
                }

                // Tag Flutuante de Localização
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                        .background(SurfaceWhite.copy(alpha = 0.94f), CapsuleShape)
                        .border(1.dp, OutlineVariant, CapsuleShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Pinheiros & Jardins • 24 salões na rede",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal
                        )
                    }
                }
            }

            // Seção 2: Bottom Sheet Deslizante com Salões Próximos e Tempos a Pé
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = SurfaceWhite,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Alça de arraste visual (Sheet Handle)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(42.dp)
                            .height(4.dp)
                            .background(OutlineVariant, CapsuleShape)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Salões Mais Próximos a Pé",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(salons, key = { it.id }) { salon ->
                            val isSelected = selectedSalon?.id == salon.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedSalon = salon
                                        onSalonClick(salon.id)
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MintSurface else CanvasBase
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) SereneTeal else OutlineVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = salon.imageUrl,
                                        contentDescription = salon.name,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = salon.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OceanicCharcoal
                                        )
                                        Text(
                                            text = "${salon.neighborhood} • ${salon.category.replaceFirstChar { it.uppercase() }}",
                                            fontSize = 11.sp,
                                            color = NeutralMuted
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Badge Exata de Caminhada a Pé ("min a pé") exigida no teste
                                        Box(
                                            modifier = Modifier
                                                .background(EconomyGreenBg, CapsuleShape)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.DirectionsWalk,
                                                    contentDescription = null,
                                                    tint = EconomyGreen,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "${salon.walkTimeMinutes} min a pé (${salon.walkDistanceMeters} m)",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EconomyGreen
                                                )
                                            }
                                        }
                                    }

                                    // Nota e Ação
                                    Column(horizontalAlignment = Alignment.End) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .background(StarAmberBg, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = StarAmber, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(text = "${salon.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OceanicCharcoal)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Ver Vagas",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SereneTeal
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
