package com.beautypass.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.SalonCard

enum class QuickFilter(val id: String, val label: String, val icon: ImageVector) {
    ALL("all", "Todos", Icons.Default.Search),
    ECONOMY("economy", "Maior Economia", Icons.Outlined.Percent),
    PROXIMITY("proximity", "Mais Próximos", Icons.Outlined.Explore),
    FAVORITES("favorites", "Favoritos", Icons.Default.Favorite)
}

data class CategoryItem(val id: String, val label: String)

/**
 * Tela 2: Home Feed & Hub Social BeautyPass.
 *
 * Implementado conforme especificações de:
 * - ORIGINAL_REQUEST.md (R1.2)
 * - DESIGN.md (Serene Mint & Teal)
 * - Carrossel de categorias, 4 filtros contextuais rápidos, busca e catálogo dos 24 salões de SP.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSalonClick: (String) -> Unit,
    onNavigateToOnDemand: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("all") }
    var selectedFilter by remember { mutableStateOf(QuickFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    // Favoritos persistidos de forma reativa no Room Database (SQLite via FavoriteDao)
    val favoriteIds by SalonRepository.favoriteIds.collectAsState()

    val categories = remember {
        listOf(
            CategoryItem("all", "Todos"),
            CategoryItem("hair", "Cabelo"),
            CategoryItem("nails", "Unhas"),
            CategoryItem("barber", "Barbearia"),
            CategoryItem("massage", "Massagem"),
            CategoryItem("facial", "Estética")
        )
    }

    val displayedSalons = remember(selectedCategory, selectedFilter, searchQuery, favoriteIds) {
        var list = SalonRepository.salons

        // 1. Filtro por Categoria
        if (selectedCategory != "all") {
            list = list.filter { it.category == selectedCategory }
        }

        // 2. Busca por Nome ou Bairro
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim()
            list = list.filter {
                it.name.contains(q, ignoreCase = true) ||
                it.neighborhood.contains(q, ignoreCase = true) ||
                it.address.contains(q, ignoreCase = true)
            }
        }

        // 3. Filtros Rápidos de Classificação/Seleção
        when (selectedFilter) {
            QuickFilter.ALL -> list
            QuickFilter.ECONOMY -> list.sortedByDescending { salon ->
                salon.discountSlots.maxOfOrNull { it.discountPct } ?: 0
            }
            QuickFilter.PROXIMITY -> list.sortedBy { it.distanceKm }
            QuickFilter.FAVORITES -> list.filter { favoriteIds.contains(it.id) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(MintSurface, CircleShape)
                                .border(1.dp, MintLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = SereneTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BeautyPass",
                            fontWeight = FontWeight.ExtraBold,
                            color = SereneTeal,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(MintSurface, CapsuleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "São Paulo",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SereneTealDark
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Seção 1: Barra de Busca com Filtro Instantâneo
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar por salão, serviço ou bairro...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = SereneTeal)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpar")
                                }
                            }
                        },
                        singleLine = true,
                        shape = CapsuleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SereneTeal,
                            unfocusedBorderColor = OutlineVariant,
                            focusedContainerColor = CanvasBase,
                            unfocusedContainerColor = CanvasBase
                        )
                    )
                }
            }

            // Seção 2: Banner de Ação Rápida On-Demand (Pedir Agora / Encaixe Imediato)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToOnDemand() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SereneTealDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(MintLight.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Pedir Agora",
                                    tint = MintLight,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Pedir Agora (Encaixe Imediato)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite
                                )
                                Text(
                                    text = "Encontre horários ociosos agora a até 3 km de você",
                                    fontSize = 11.sp,
                                    color = MintLight
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToOnDemand,
                            colors = ButtonDefaults.buttonColors(containerColor = MintPrimary),
                            shape = CapsuleShape,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Chamar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Seção 3: Carrossel de Categorias
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Categorias",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat.id },
                                label = { Text(cat.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SereneTeal,
                                    selectedLabelColor = SurfaceWhite,
                                    containerColor = SurfaceWhite,
                                    labelColor = OceanicCharcoal
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) SereneTeal else OutlineVariant
                                ),
                                shape = CapsuleShape
                            )
                        }
                    }
                }
            }

            // Seção 4: Filtros Rápidos (Todos, Maior Economia, Mais Próximos, Favoritos)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Filtros Rápidos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(QuickFilter.values()) { filter ->
                            val isSelected = selectedFilter == filter
                            ElevatedFilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = filter },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = filter.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(filter.label, fontSize = 12.sp)
                                    }
                                },
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = MintSurface,
                                    selectedLabelColor = SereneTeal,
                                    containerColor = SurfaceWhite,
                                    labelColor = OceanicCharcoal
                                ),
                                shape = CapsuleShape
                            )
                        }
                    }
                }
            }

            // Seção 5: Barra de Status do Feed
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${displayedSalons.size} estabelecimentos encontrados",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )

                    if (selectedFilter == QuickFilter.FAVORITES) {
                        Text(
                            text = "Filtro: Favoritos (${favoriteIds.size})",
                            fontSize = 11.sp,
                            color = CoralPromoText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Seção 6: Lista de Salões (Os 24 de São Paulo)
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
                            text = "Nenhum salão encontrado com os filtros selecionados.",
                            fontSize = 13.sp,
                            color = NeutralMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(onClick = {
                            selectedCategory = "all"
                            selectedFilter = QuickFilter.ALL
                            searchQuery = ""
                        }) {
                            Text("Limpar todos os filtros", color = SereneTeal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(displayedSalons, key = { it.id }) { salon ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SalonCard(
                            salon = salon,
                            isFavorite = favoriteIds.contains(salon.id),
                            onFavoriteClick = { SalonRepository.toggleFavorite(salon.id) },
                            onClick = { onSalonClick(salon.id) }
                        )
                    }
                }
            }
        }
    }
}
