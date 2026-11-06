package com.beautypass.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.PricingEngine
import com.beautypass.app.model.Salon
import com.beautypass.app.theme.*

/**
 * SalonCard: Card compacto de alta densidade estilo app nativo, sincronizado 1:1 com .social-salon-card:
 * - Foto 156dp com badges nos 4 quadrantes (prova social ao vivo, caminhada a pé, favorito tátil)
 * - Cabeçalho com nome + bairro + nota
 * - Faixa unificada de credibilidade (Especialista responsável + Amigas em comum na mesma linha)
 * - Bloco integrado de Serviço + Tarifa Dinâmica + Botão com ícone vetorial e resposta elástica (bounceClick).
 */
@Composable
fun SalonCard(
    salon: Salon,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    activeFilterId: String = "all",
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

    val topDiscount = salon.discountSlots.maxOfOrNull { it.discountPct } ?: 0
    val primaryService = salon.services.firstOrNull()
    val bestPromo = salon.discountSlots.maxByOrNull { it.discountPct }
    val hasDiscount = bestPromo != null && bestPromo.discountPct > 0
    val isUrgent = bestPromo?.type == "urgent"
    val finalPrice = if (primaryService != null && hasDiscount) {
        val raw = primaryService.basePrice * (1.0 - bestPromo!!.discountPct / 100.0)
        kotlin.math.max(25.00, kotlin.math.round(raw * 100.0) / 100.0)
    } else primaryService?.basePrice ?: 90.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(scaleDown = 0.98f, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, OutlineVariant)
    ) {
        Column {
            // 1. Foto Compacta (156.dp) com Badges nos Quadrantes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
            ) {
                BeautyPassImage(
                    imageUrl = salon.imageUrl,
                    fallbackResId = LocalImageFallbacks.forSalon(salon.category, salon.id),
                    contentDescription = salon.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Canto Superior Esquerdo: Prova Social em tempo real
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 10.dp, top = 10.dp)
                        .widthIn(max = 195.dp)
                        .background(
                            color = Slate900.copy(alpha = 0.84f),
                            shape = CapsuleShape
                        )
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(SuccessGreen.copy(alpha = blipAlpha), CircleShape)
                        )
                        Text(
                            text = salon.socialProof.replace("^#".toRegex(), ""),
                            color = SurfaceWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Canto Superior Direito: Pílula Branca de Caminhada ("10 min a pé • 0.8 km")
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp, top = 10.dp)
                        .background(
                            color = SurfaceWhite.copy(alpha = 0.95f),
                            shape = CapsuleShape
                        )
                        .border(0.5.dp, OutlineVariant, CapsuleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = OceanicCharcoal,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${salon.walkTimeMinutes} min a pé • ${salon.distanceKm} km",
                            color = OceanicCharcoal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                // Canto Inferior Esquerdo: Badge Contextual de Prioridade ou Categoria de Horário
                if (activeFilterId == "proximity") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .background(SereneTeal.copy(alpha = 0.92f), CapsuleShape)
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Explore,
                                contentDescription = null,
                                tint = MintLight,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "${salon.distanceKm} km de você",
                                color = SurfaceWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (hasDiscount) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .background(
                                color = if (isUrgent) CoralPromo.copy(alpha = 0.94f) else SereneTeal.copy(alpha = 0.92f),
                                shape = CapsuleShape
                            )
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = if (isUrgent) "Última Hora • -${bestPromo!!.discountPct}%" else "Horário Econômico • -${bestPromo!!.discountPct}%",
                                color = SurfaceWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Canto Inferior Direito: Botão Circular de Favoritar com resposta elástica
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (isFavorite) SurfaceWhite.copy(alpha = 0.95f) else Slate900.copy(alpha = 0.68f),
                            shape = CircleShape
                        )
                        .border(0.8.dp, SurfaceWhite.copy(alpha = 0.35f), CircleShape)
                        .bounceClick(scaleDown = 0.86f, onClick = onFavoriteClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = if (isFavorite) CoralPromo else SurfaceWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // 2. Corpo Compacto do Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                // Linha 1: Nome do Salão + Bairro + Chip de Nota
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = salon.name,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OceanicCharcoal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Verificado",
                                tint = SereneTeal,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "${salon.neighborhood} • ${primaryService?.name ?: "Beleza"} (${primaryService?.durationMinutes ?: 45} min)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeutralMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Chip de Avaliação
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MintSurface, CapsuleShape)
                            .border(0.8.dp, OutlineVariant, CapsuleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Avaliação",
                            tint = StarAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${salon.rating}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OceanicCharcoal
                        )
                        Text(
                            text = " (${salon.reviewsCount})",
                            fontSize = 10.sp,
                            color = NeutralMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Linha 2 Unificada: Especialista Responsável + Rede de Amigas na mesma faixa compacta
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MintSurface, RoundedCornerShape(10.dp))
                        .border(0.8.dp, OutlineVariant, RoundedCornerShape(10.dp))
                        .padding(horizontal = 9.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Especialista líder
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        BeautyPassImage(
                            imageUrl = salon.leadStaff.avatarUrl,
                            fallbackResId = LocalImageFallbacks.forOwnerAvatar(salon.leadStaff.name),
                            contentDescription = salon.leadStaff.name,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(1.dp, SurfaceWhite, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${salon.leadStaff.name} • ${salon.leadStaff.role}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OceanicCharcoal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Avatares sobrepostos de amigas em comum
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(modifier = Modifier.padding(end = 2.dp)) {
                            salon.mutualNetwork.avatars.take(3).forEachIndexed { index, avatar ->
                                BeautyPassImage(
                                    imageUrl = avatar,
                                    fallbackResId = LocalImageFallbacks.forFriendAvatar(index),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .offset(x = (-index * 4).dp)
                                        .size(18.dp)
                                        .border(1.dp, SurfaceWhite, CircleShape)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Text(
                            text = "+${salon.mutualNetwork.friendsCount} amigas",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EconomyGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Linha 3 Integrada: Preço Dinâmico (Esquerda) + Botão Tátil com Ícone Vetorial (Direita)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CanvasBase, RoundedCornerShape(12.dp))
                        .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                        .padding(horizontal = 11.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (hasDiscount && primaryService != null) {
                                Text(
                                    text = "R$ ${primaryService.basePrice.toInt()}",
                                    fontSize = 11.5.sp,
                                    color = Slate400,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                            Text(
                                text = "R$ ${finalPrice.toInt()}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SereneTeal
                            )
                            if (hasDiscount) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isUrgent) CoralPromoBg else EconomyGreenBg,
                                            RoundedCornerShape(5.dp)
                                        )
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "-${bestPromo!!.discountPct}%",
                                        color = if (isUrgent) CoralPromoText else EconomyGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isUrgent) PricingEngine.SUBTEXT_URGENT else PricingEngine.SUBTEXT_ECONOMY,
                            fontSize = 9.5.sp,
                            color = NeutralMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botão Nativo Compacto com Ícones Vetoriais
                    Surface(
                        color = SereneTeal,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .bounceClick(scaleDown = 0.94f, onClick = onClick)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Escolher Horário",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
