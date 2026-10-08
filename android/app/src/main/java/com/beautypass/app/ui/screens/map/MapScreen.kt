package com.beautypass.app.ui.screens.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onSalonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSalon by remember { mutableStateOf<Salon?>(SalonRepository.salons.firstOrNull()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Salões Próximos", fontSize = 17.sp, fontWeight = FontWeight.Bold) },
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
            // Representação Vetorial de Mapa (São Paulo - Jardins & Pinheiros)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .background(Color(0xFFE2E8F0))
            ) {
                // Desenho Vetorial com Malha de Vias e Marcadores
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Linhas simulando avenidas principais (Rebouças, Faria Lima, Oscar Freire)
                    drawLine(color = Color(0xFFCBD5E1), start = Offset(0f, h * 0.3f), end = Offset(w, h * 0.7f), strokeWidth = 14f)
                    drawLine(color = Color(0xFFCBD5E1), start = Offset(w * 0.2f, 0f), end = Offset(w * 0.8f, h), strokeWidth = 12f)
                    drawLine(color = Color.White, start = Offset(0f, h * 0.3f), end = Offset(w, h * 0.7f), strokeWidth = 8f)
                    drawLine(color = Color.White, start = Offset(w * 0.2f, 0f), end = Offset(w * 0.8f, h), strokeWidth = 6f)

                    // Ponto do Usuário (Ponto Azul Pulsante com Anel)
                    drawCircle(color = Color(0x3300685F), center = Offset(w * 0.45f, h * 0.55f), radius = 24f)
                    drawCircle(color = SereneTeal, center = Offset(w * 0.45f, h * 0.55f), radius = 10f)
                    drawCircle(color = Color.White, center = Offset(w * 0.45f, h * 0.55f), radius = 4f)

                    // Marcador Salão 1 (Pinheiros)
                    drawCircle(color = SereneTeal, center = Offset(w * 0.32f, h * 0.35f), radius = 12f)
                    drawCircle(color = MintLight, center = Offset(w * 0.32f, h * 0.35f), radius = 6f)

                    // Marcador Salão 2 (Jardins)
                    drawCircle(color = SereneTeal, center = Offset(w * 0.65f, h * 0.45f), radius = 12f)
                    drawCircle(color = MintLight, center = Offset(w * 0.65f, h * 0.45f), radius = 6f)

                    // Marcador Salão 3 (Vila Madalena)
                    drawCircle(color = SereneTeal, center = Offset(w * 0.25f, h * 0.22f), radius = 12f)
                    drawCircle(color = MintLight, center = Offset(w * 0.25f, h * 0.22f), radius = 6f)
                }

                // Tag Flutuante de Localização
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(14.dp)
                        .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(14.dp))
                        Text("Jardins & Pinheiros (24 salões na rede)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    }
                }
            }

            // Bottom Sheet com Lista de Salões e Tempo a Pé
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = SurfaceWhite,
                shadowElevation = 10.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Salões Próximos de Você",
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(SalonRepository.salons) { salon ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BackgroundLight)
                                    .clickable { onSalonClick(salon.id) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = salon.imageUrl,
                                    contentDescription = salon.name,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(salon.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("${salon.neighborhood} • ${salon.distanceKm} km", fontSize = 11.sp, color = Slate600)
                                        Text("•", fontSize = 10.sp, color = Slate400)
                                        // Badge de Tempo a Pé
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.DirectionsWalk,
                                                contentDescription = null,
                                                tint = SereneTeal,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Text(
                                                text = "${salon.walkTimeMinutes} min a pé",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SereneTeal
                                            )
                                        }
                                    }
                                }
                                Button(
                                    onClick = { onSalonClick(salon.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Ver", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
