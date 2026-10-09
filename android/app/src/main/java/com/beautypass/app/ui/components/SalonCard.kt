package com.beautypass.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.beautypass.app.model.Salon
import com.beautypass.app.theme.*

/**
 * SalonCard: Card rico com 4 quadrantes heróicos, prova social em tempo real,
 * selo de responsável técnico e bloco de amigas em comum.
 *
 * Implementado conforme especificações de:
 * - documenta_o_ui_ux_app_social_food_delivery.md (Inspiração 3)
 * - DESIGN.md (Serene Mint & Teal Tokens)
 */
@Composable
fun SalonCard(
    salon: Salon,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "socialPulse")
    val blipAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blipAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Column {
            // Imagem e Badges Sobrepostos nos 4 Quadrantes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
            ) {
                AsyncImage(
                    model = salon.imageUrl,
                    contentDescription = salon.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Canto Superior Esquerdo: Prova Social ("#Mariana agendou há 12 min") com blip pulsante
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(
                            color = Slate900.copy(alpha = 0.82f),
                            shape = CapsuleShape
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(SuccessGreen.copy(alpha = blipAlpha), CircleShape)
                        )
                        Text(
                            text = salon.socialProof,
                            color = SurfaceWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Canto Superior Direito: Métrica de Distância ("0.8 km") - Totalmente Desobstruída
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            color = SurfaceWhite.copy(alpha = 0.94f),
                            shape = CapsuleShape
                        )
                        .border(0.5.dp, OutlineVariant, CapsuleShape)
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Explore,
                            contentDescription = "Distância",
                            tint = SereneTeal,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${salon.distanceKm} km",
                            color = OceanicCharcoal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Canto Inferior Direito: Botão de Favoritar Ergonômico (Touch Friendly)
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(38.dp)
                        .background(
                            color = if (isFavorite) SurfaceWhite.copy(alpha = 0.95f) else Slate900.copy(alpha = 0.65f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = if (isFavorite) CoralPromo else SurfaceWhite,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // Corpo Informativo do Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Linha 1: Nome do Salão e Nota
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = salon.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = OceanicCharcoal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Verificado",
                            tint = SereneTeal,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Chip de Avaliação com Âmbar Suave
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(StarAmberBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Avaliação",
                            tint = StarAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format("%.2f", salon.rating),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Bairro e Categoria
                Text(
                    text = "${salon.neighborhood} • ${salon.category.replaceFirstChar { it.uppercase() }}",
                    fontSize = 12.sp,
                    color = NeutralMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Selo do Responsável Técnico / Fundador
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MintSurface, RoundedCornerShape(10.dp))
                        .border(0.5.dp, OutlineVariant, RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = salon.leadStaff.avatarUrl,
                        contentDescription = salon.leadStaff.name,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = salon.leadStaff.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OceanicCharcoal
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SereneTeal,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = salon.leadStaff.role,
                            fontSize = 10.sp,
                            color = NeutralMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Clúster Social: Amigas em Comum com Avatares Sobrepostos (-6dp offset)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EconomyGreenBg, RoundedCornerShape(10.dp))
                        .border(0.5.dp, OutlineVariant, RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.padding(end = 6.dp)) {
                        salon.mutualNetwork.avatars.take(3).forEachIndexed { index, avatar ->
                            AsyncImage(
                                model = avatar,
                                contentDescription = null,
                                modifier = Modifier
                                    .offset(x = (-index * 6).dp)
                                    .size(20.dp)
                                    .border(1.dp, SurfaceWhite, CircleShape)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Text(
                        text = salon.mutualNetwork.text,
                        fontSize = 11.sp,
                        color = OceanicCharcoal,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Preço e Botão de Ação
                val primaryService = salon.services.firstOrNull() ?: return@Column
                val bestPromo = salon.discountSlots.maxByOrNull { it.discountPct }
                val hasDiscount = bestPromo != null && bestPromo.discountPct > 0
                val finalPrice = if (hasDiscount) {
                    val raw = primaryService.basePrice * (1.0 - bestPromo!!.discountPct / 100.0)
                    kotlin.math.max(25.00, kotlin.math.round(raw * 100.0) / 100.0)
                } else primaryService.basePrice

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${primaryService.name} • ${primaryService.durationMinutes} min",
                            fontSize = 12.sp,
                            color = NeutralMuted
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (hasDiscount) {
                                Text(
                                    text = "R$ ${String.format("%.2f", primaryService.basePrice)}",
                                    fontSize = 12.sp,
                                    color = Slate400,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                            Text(
                                text = "R$ ${String.format("%.2f", finalPrice)}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SereneTeal
                            )
                            if (hasDiscount) {
                                Box(
                                    modifier = Modifier
                                        .background(CoralPromo, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "-${bestPromo!!.discountPct}%",
                                        color = SurfaceWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Ver Vagas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                    }
                }
            }
        }
    }
}
