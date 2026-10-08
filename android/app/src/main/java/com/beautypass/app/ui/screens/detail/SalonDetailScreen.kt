package com.beautypass.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.model.Service
import com.beautypass.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonDetailScreen(
    salonId: String,
    onBackClick: () -> Unit,
    onProceedToCheckout: (salonId: String, serviceId: String, slotTime: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val salon = remember(salonId) {
        SalonRepository.salons.find { it.id == salonId } ?: SalonRepository.salons.first()
    }

    val favoriteIds by SalonRepository.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(salon.id)

    var currentHeroImage by remember { mutableStateOf(salon.imageUrl) }
    var selectedService by remember { mutableStateOf(salon.services.first()) }
    var selectedSlotTime by remember {
        mutableStateOf(salon.discountSlots.firstOrNull()?.time ?: "14:00")
    }

    val pricing = remember(selectedSlotTime, selectedService) {
        SalonRepository.calculatePricing(salon, selectedSlotTime, selectedService)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(salon.name, maxLines = 1, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { SalonRepository.toggleFavorite(salon.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (isFavorite) ErrorRed else Slate800
                        )
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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Simulado",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (pricing.hasDiscount) {
                                Text(
                                    text = "R$ ${String.format("%.2f", pricing.basePrice)}",
                                    fontSize = 12.sp,
                                    color = Slate400,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    )
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

                    Button(
                        onClick = {
                            onProceedToCheckout(salon.id, selectedService.id, selectedSlotTime)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Continuar (${selectedSlotTime})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
        ) {
            // Foto Principal Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = currentHeroImage,
                    contentDescription = salon.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Mini-Galeria de Miniaturas (Troca interativa ao clicar)
            if (salon.galleryImages.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Galeria de Fotos do Espaço",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(salon.galleryImages) { imgUrl ->
                            val isSelected = currentHeroImage == imgUrl
                            AsyncImage(
                                model = imgUrl,
                                contentDescription = "Foto do Salão",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) SereneTeal else OutlineBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { currentHeroImage = imgUrl },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Informações do Salão e Endereço
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp)
            ) {
                Text(salon.name, style = MaterialTheme.typography.headlineMedium)
                Text(salon.address, fontSize = 12.sp, color = Slate600)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEF9C3), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = AmberEconomy, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("${salon.rating} (${salon.reviewsCount} avaliações)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("•", color = Slate400)
                    Text("${salon.distanceKm} km • ${salon.walkTimeMinutes} min a pé", fontSize = 11.sp, color = SereneTeal, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Seletor de Serviços
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Selecione o Serviço",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                salon.services.forEach { service ->
                    val isSelected = selectedService.id == service.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedService = service },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MintSurface else BackgroundLight
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) SereneTeal else OutlineBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(service.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                Text("${service.durationMinutes} min • ${service.description}", fontSize = 11.sp, color = Slate600)
                            }
                            Text(
                                text = "R$ ${String.format("%.2f", service.basePrice)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SereneTeal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Seletor de Horários com Precificação Dinâmica
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Horários Disponíveis (Hoje)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    salon.discountSlots.forEach { slot ->
                        val isSelected = selectedSlotTime == slot.time
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SereneTeal else BackgroundLight)
                                .border(1.dp, if (isSelected) SereneTeal else OutlineBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedSlotTime = slot.time }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = slot.time,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Slate900
                                )
                                if (slot.discountPct > 0) {
                                    Text(
                                        text = "-${slot.discountPct}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) MintLight else CoralPromo
                                    )
                                } else {
                                    Text(
                                        text = "Normal",
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else Slate400
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subtexto Obrigatório de Transparência (Seção 6.2)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MintSurface, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = pricing.subtext,
                        fontSize = 11.sp,
                        color = SereneTealDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
