package com.beautypass.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
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

enum class QuickFilter(val label: String, val icon: ImageVector) {
    ALL("Todos", Icons.Default.Search),
    ECONOMY("Maior Economia", Icons.Outlined.Percent),
    PROXIMITY("Mais Próximos", Icons.Outlined.Explore),
    FAVORITES("Favoritos", Icons.Default.Favorite)
}

data class CategoryItem(val id: String, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSalonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("all") }
    var selectedFilter by remember { mutableStateOf(QuickFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val favoriteIds by SalonRepository.favoriteIds.collectAsState()

    val categories = listOf(
        CategoryItem("all", "Todos"),
        CategoryItem("hair", "Cabelo"),
        CategoryItem("nails", "Unhas"),
        CategoryItem("barber", "Barbearia"),
        CategoryItem("massage", "Massagem"),
        CategoryItem("esthetic", "Estética")
    )

    val displayedSalons = remember(selectedCategory, selectedFilter, searchQuery, favoriteIds) {
        var list = SalonRepository.salons

        if (selectedCategory != "all") {
            list = list.filter { it.category == selectedCategory }
        }

        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.neighborhood.contains(searchQuery, ignoreCase = true)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BeautyPass",
                            fontWeight = FontWeight.ExtraBold,
                            color = SereneTeal,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(MintSurface, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Triple Fusion",
                                color = SereneTeal,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
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
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Barra de Busca
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar salões, serviços ou bairros...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Slate400
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite,
                        focusedBorderColor = SereneTeal,
                        unfocusedBorderColor = OutlineBorder
                    ),
                    singleLine = true
                )
            }

            // Categorias Horizontais
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) SereneTeal else SurfaceWhite)
                                .clickable { selectedCategory = cat.id }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat.label,
                                color = if (isSelected) Color.White else Slate800,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Chips de Filtro Rápido (Todos, Maior Economia, Mais Próximos, Favoritos)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        AssistChip(
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = filter.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = filter.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = if (isSelected) Color.White else if (filter == QuickFilter.FAVORITES) ErrorRed else SereneTeal
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) SereneTeal else SurfaceWhite,
                                labelColor = if (isSelected) Color.White else Slate800
                            ),
                            border = AssistChipDefaults.assistChipBorder(
                                borderColor = if (isSelected) SereneTeal else OutlineBorder
                            ),
                            shape = CircleShape
                        )
                    }
                }
            }

            // Empty State ou Lista de Cards
            if (displayedSalons.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedFilter == QuickFilter.FAVORITES)
                                "Nenhum salão salvo nos favoritos"
                            else "Nenhum estabelecimento encontrado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate900
                        )
                        Text(
                            text = if (selectedFilter == QuickFilter.FAVORITES)
                                "Toque no ícone de coração nos salões para salvá-los aqui!"
                            else "Tente selecionar outra categoria ou limpar a busca.",
                            fontSize = 12.sp,
                            color = Slate600
                        )
                    }
                }
            } else {
                items(displayedSalons) { salon ->
                    SalonCard(
                        salon = salon,
                        isFavorite = favoriteIds.contains(salon.id),
                        onFavoriteClick = { SalonRepository.toggleFavorite(salon.id) },
                        onClick = { onSalonClick(salon.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
