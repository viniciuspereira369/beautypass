package com.beautypass.app.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.BeautyPassImage
import com.beautypass.app.ui.components.LocalImageFallbacks
import com.beautypass.app.ui.components.SalonCard
import com.beautypass.app.ui.components.bounceClick
import java.text.Normalizer

enum class QuickFilter(val id: String, val label: String, val icon: ImageVector) {
    ALL("all", "Todos", Icons.Default.GridView),
    ECONOMY("economy", "Até 35% OFF", Icons.Outlined.LocalOffer),
    PROXIMITY("proximity", "Mais Próximos", Icons.Outlined.Explore),
    FAVORITES("favorites", "Favoritos", Icons.Outlined.FavoriteBorder)
}

data class StoryCategory(
    val id: String,
    val label: String,
    val imageUrl: String
)

private fun normalizeSearchText(input: String): String {
    val temp = Normalizer.normalize(input.lowercase().trim(), Normalizer.Form.NFD)
    return temp.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}

/**
 * Tela 1: Home (Social Proof Feed & Quick Hub) — Densidade de app nativo com primeiro SalonCard
 * acima da dobra, ícones vetoriais e resposta elástica (bounceClick) em todos os controles.
 */
@Composable
fun HomeScreen(
    onSalonClick: (String) -> Unit,
    onNavigateToOnDemand: () -> Unit,
    onNavigateToMap: () -> Unit = {},
    participantName: String = "Camila",
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("all") }
    var selectedFilter by remember { mutableStateOf(QuickFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val favoriteIds by SalonRepository.favoriteIds.collectAsState()
    val userProfile by SalonRepository.userProfile.collectAsState()

    val firstName = remember(userProfile, participantName) {
        val raw = userProfile?.name?.takeIf { it.isNotBlank() } ?: participantName
        raw.trim().split(" ").firstOrNull()?.takeIf { it.isNotBlank() && it != "Participante" } ?: "Camila"
    }

    val storyCategories = remember {
        listOf(
            StoryCategory("all", "Todos", "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=300&auto=format&fit=crop&q=80"),
            StoryCategory("hair", "Cabelo", "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=300&auto=format&fit=crop&q=80"),
            StoryCategory("barber", "Barba", "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=300&auto=format&fit=crop&q=80"),
            StoryCategory("nails", "Unhas", "https://images.unsplash.com/photo-1604654894610-df63bc536371?w=300&auto=format&fit=crop&q=80"),
            StoryCategory("massage", "Massagem", "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=300&auto=format&fit=crop&q=80"),
            StoryCategory("esthetic", "Estética", "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?w=300&auto=format&fit=crop&q=80")
        )
    }

    val displayedSalons = remember(selectedCategory, selectedFilter, searchQuery, favoriteIds) {
        var list = SalonRepository.salons

        if (selectedCategory != "all") {
            list = list.filter { s ->
                s.category.equals(selectedCategory, ignoreCase = true) ||
                    (selectedCategory == "barber" && s.category.equals("barbearia", ignoreCase = true)) ||
                    (selectedCategory == "esthetic" && (s.category.equals("facial", ignoreCase = true) || s.category.equals("estetica", ignoreCase = true))) ||
                    s.services.any { srv ->
                        srv.category.equals(selectedCategory, ignoreCase = true) ||
                            (selectedCategory == "esthetic" && srv.category.equals("facial", ignoreCase = true))
                    }
            }
        }

        if (searchQuery.isNotBlank()) {
            val q = normalizeSearchText(searchQuery)
            list = list.filter { salon ->
                normalizeSearchText(salon.name).contains(q) ||
                    normalizeSearchText(salon.neighborhood).contains(q) ||
                    normalizeSearchText(salon.address).contains(q) ||
                    salon.services.any { srv -> normalizeSearchText(srv.name).contains(q) }
            }
        }

        when (selectedFilter) {
            QuickFilter.ALL -> list
            QuickFilter.ECONOMY -> list.sortedByDescending { salon ->
                salon.discountSlots.maxOfOrNull { it.discountPct } ?: 0
            }
            QuickFilter.PROXIMITY -> list.sortedBy { it.distanceKm }
            QuickFilter.FAVORITES -> list.filter { favoriteIds.contains(it.id) }
        }
    }

    val hasActiveFilterOrSearch = selectedCategory != "all" || selectedFilter != QuickFilter.ALL || searchQuery.isNotBlank()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBase),
        contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Header Contextual Compacto + Location Pill com bounceClick
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bem-vindo(a)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeutralMuted
                    )
                    Text(
                        text = "Olá, $firstName!",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OceanicCharcoal,
                        letterSpacing = (-0.3).sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CapsuleShape)
                        .background(MintSurface)
                        .border(1.dp, MintLight.copy(alpha = 0.65f), CapsuleShape)
                        .bounceClick(scaleDown = 0.95f, onClick = onNavigateToMap)
                        .padding(horizontal = 11.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SereneTeal,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "São Paulo (Centro)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SereneTeal
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = SereneTeal.copy(alpha = 0.7f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        // 2. Search Bar em Pílula com Botão de Mapa Integrado (movido para o topo para busca imediata)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .height(44.dp)
                    .shadow(2.dp, CapsuleShape)
                    .clip(CapsuleShape)
                    .background(SurfaceWhite)
                    .border(1.dp, OutlineVariant, CapsuleShape)
                    .padding(start = 14.dp, end = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = SereneTeal,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Buscar salão, bairro ou serviço...",
                            fontSize = 12.5.sp,
                            color = NeutralMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = OceanicCharcoal
                        ),
                        cursorBrush = SolidColor(SereneTeal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpar",
                            tint = NeutralMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MintSurface)
                        .bounceClick(scaleDown = 0.90f, onClick = onNavigateToMap),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Map,
                        contentDescription = "Mapa",
                        tint = SereneTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 3. Faixa Compacta de Retenção ("CLUBE BEAUTYPASS • 15% OFF") — economiza altura mantendo os dados
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .shadow(2.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF003D37), SereneTeal, Color(0xFF0B7A6E)),
                            start = Offset(0f, 0f),
                            end = Offset(900f, 300f)
                        )
                    )
                    .bounceClick(scaleDown = 0.98f, onClick = onNavigateToOnDemand)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MintLight.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CardGiftcard,
                                contentDescription = null,
                                tint = MintLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "CLUBE BEAUTYPASS",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MintLight,
                                    letterSpacing = 0.4.sp
                                )
                                Text(
                                    text = "• 15% OFF",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                            }
                            Text(
                                text = "15% de volta nos 3 primeiros agendamentos",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "1/3 concluídos",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(5.dp)
                                .clip(CapsuleShape)
                                .background(SurfaceWhite.copy(alpha = 0.22f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.35f)
                                    .fillMaxHeight()
                                    .clip(CapsuleShape)
                                    .background(MintLight)
                            )
                        }
                    }
                }
            }
        }

        // 4. Categorias Populares (Círculos Fotográficos Compactos 56dp + Filtros Rápidos Integrados)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(storyCategories) { cat ->
                        val isSelected = selectedCategory == cat.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .bounceClick(scaleDown = 0.92f) { selectedCategory = cat.id }
                                .padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        brush = if (isSelected) {
                                            Brush.linearGradient(
                                                listOf(
                                                    SereneTeal,
                                                    MintPrimary,
                                                    Color(0xFFD4AF37)
                                                )
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                listOf(
                                                    OutlineVariant,
                                                    MintLight.copy(alpha = 0.5f)
                                                )
                                            )
                                        },
                                        shape = CircleShape
                                    )
                                    .padding(2.2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(SurfaceWhite, CircleShape)
                                        .padding(1.5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    BeautyPassImage(
                                        imageUrl = cat.imageUrl,
                                        fallbackResId = LocalImageFallbacks.forStoryCategory(cat.id),
                                        contentDescription = cat.label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cat.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isSelected) SereneTeal else OceanicCharcoal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chips de Filtro Rápido com bounceClick
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(QuickFilter.values()) { filter ->
                        val isSelected = selectedFilter == filter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(32.dp)
                                .clip(CapsuleShape)
                                .background(if (isSelected) SereneTeal else SurfaceWhite)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) SereneTeal else OutlineVariant,
                                    shape = CapsuleShape
                                )
                                .bounceClick(scaleDown = 0.94f) { selectedFilter = filter }
                                .padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = filter.icon,
                                contentDescription = null,
                                tint = if (isSelected) SurfaceWhite else NeutralMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = filter.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) SurfaceWhite else OceanicCharcoal
                            )
                        }
                    }
                }
            }
        }

        // Barra Dinâmica de Status de Filtro Ativo
        if (hasActiveFilterOrSearch) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MintSurface)
                        .border(1.dp, MintLight.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = SereneTeal,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${displayedSalons.size} salões disponíveis",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "Limpar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SereneTeal,
                        modifier = Modifier
                            .clickable {
                                selectedCategory = "all"
                                selectedFilter = QuickFilter.ALL
                                searchQuery = ""
                            }
                            .padding(start = 8.dp)
                    )
                }
            }
        }

        // 5. Cabeçalho do Feed Social ("Salões Recomendados • Tempo real") logo acima do primeiro SalonCard (Acima da Dobra!)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Amigas estão agendando",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OceanicCharcoal
                )
                Text(
                    text = "${displayedSalons.size} em São Paulo",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SereneTeal
                )
            }
        }

        // 6. Lista de Salões (com FeaturedPartnersGrid integrado de forma compacta)
        if (displayedSalons.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (selectedFilter == QuickFilter.FAVORITES) {
                            "Nenhum salão salvo nos favoritos"
                        } else {
                            "Nenhum estabelecimento encontrado"
                        },
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (selectedFilter == QuickFilter.FAVORITES) {
                            "Toque no ícone de coração nos estabelecimentos para salvá-los aqui!"
                        } else {
                            "Tente selecionar outra categoria ou limpar os filtros ativos."
                        },
                        fontSize = 11.5.sp,
                        color = NeutralMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            selectedCategory = "all"
                            selectedFilter = QuickFilter.ALL
                            searchQuery = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ver Todos os Salões", color = SurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        } else {
            // Primeiros 2 salões imediatamente visíveis acima da dobra
            val topSalons = displayedSalons.take(2)
            val remainingSalons = displayedSalons.drop(2)

            items(topSalons, key = { it.id }) { salon ->
                Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                    SalonCard(
                        salon = salon,
                        isFavorite = favoriteIds.contains(salon.id),
                        onFavoriteClick = { SalonRepository.toggleFavorite(salon.id) },
                        onClick = { onSalonClick(salon.id) },
                        activeFilterId = selectedFilter.id
                    )
                }
            }

            // Carrossel Compacto de Salões Parceiros em Destaque após os 2 primeiros cards
            item {
                FeaturedPartnersGrid(
                    salons = SalonRepository.salons,
                    onSalonClick = onSalonClick,
                    onSeeOnMapClick = onNavigateToMap
                )
            }

            items(remainingSalons, key = { it.id }) { salon ->
                Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                    SalonCard(
                        salon = salon,
                        isFavorite = favoriteIds.contains(salon.id),
                        onFavoriteClick = { SalonRepository.toggleFavorite(salon.id) },
                        onClick = { onSalonClick(salon.id) },
                        activeFilterId = selectedFilter.id
                    )
                }
            }
        }
    }
}

/**
 * Carrossel Horizontal Compacto de Salões Parceiros em Destaque.
 */
@Composable
fun FeaturedPartnersGrid(
    salons: List<Salon>,
    onSalonClick: (String) -> Unit,
    onSeeOnMapClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val featured = remember(salons) { salons.take(8) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Salões Parceiros em Destaque",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OceanicCharcoal
            )
            Text(
                text = "Ver no mapa",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = SereneTeal,
                modifier = Modifier.bounceClick(scaleDown = 0.94f, onClick = onSeeOnMapClick)
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(featured, key = { "feat_${it.id}" }) { salon ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
                    modifier = Modifier
                        .width(126.dp)
                        .bounceClick(scaleDown = 0.95f) { onSalonClick(salon.id) }
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(SereneTeal, MintPrimary, MintLight)
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                BeautyPassImage(
                                    imageUrl = salon.imageUrl,
                                    fallbackResId = LocalImageFallbacks.forSalon(salon.category, salon.id),
                                    contentDescription = salon.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .border(1.5.dp, SurfaceWhite, CircleShape)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(SereneTeal, CircleShape)
                                    .border(1.5.dp, SurfaceWhite, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Verificado",
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(9.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = salon.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = StarAmber,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${salon.rating} • ${salon.distanceKm} km",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeutralMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "BeautyPass Home Screen — Pixel 8 Pro",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=412dp,height=915dp,dpi=480"
)
@Preview(
    name = "BeautyPass Home Screen — Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomeScreenPreview() {
    BeautyPassTheme {
        HomeScreen(
            onSalonClick = {},
            onNavigateToOnDemand = {},
            onNavigateToMap = {}
        )
    }
}
