package com.beautypass.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.BookingEngine
import com.beautypass.app.data.PricingEngine
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.model.Service
import com.beautypass.app.model.Staff
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.BeautyPassImage
import com.beautypass.app.ui.components.RadialGaugeTimeSelector
import com.beautypass.app.ui.components.bounceClick
import java.text.SimpleDateFormat
import java.util.*

data class DatePill(val dayOfWeek: String, val dayNumber: String, val fullDisplay: String)

/**
 * Tela 3: Detalhe do Salão & Agendamento com Seletor Radial Analógico-Digital (espelhada 1:1 com #screen-detail da web).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonDetailScreen(
    salonId: String,
    onBackClick: () -> Unit,
    onProceedToCheckout: (salonId: String, serviceId: String, slotTime: String, fulfillmentMode: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val salon = remember(salonId) {
        SalonRepository.getSalonById(salonId) ?: SalonRepository.salons.first()
    }

    val favoriteIds by SalonRepository.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(salon.id)

    // Mini-galeria de 4 imagens HD
    val galleryImages = remember(salon) {
        val list = salon.galleryImages.toMutableList()
        if (!list.contains(salon.imageUrl)) {
            list.add(0, salon.imageUrl)
        }
        list.take(4)
    }

    var selectedHeroImage by remember { mutableStateOf(salon.imageUrl) }
    var fulfillmentMode by remember { mutableStateOf("salon") } // "salon" ou "homecare"
    var selectedServiceCategory by remember { mutableStateOf("Todos") }
    var selectedServiceIds by remember { mutableStateOf(setOf(salon.services.first().id)) }
    val selectedServices = remember(selectedServiceIds, salon) {
        val list = salon.services.filter { selectedServiceIds.contains(it.id) }
        if (list.isNotEmpty()) list else listOf(salon.services.first())
    }
    val totalBasePrice = remember(selectedServices) {
        selectedServices.sumOf { it.basePrice }
    }
    val totalDurationMinutes = remember(selectedServices) {
        selectedServices.sumOf { it.durationMinutes }
    }
    var selectedStaffId by remember { mutableStateOf("any") } // "any" ou id do staff
    var selectedSlotTime by remember {
        mutableStateOf(salon.discountSlots.firstOrNull()?.time ?: "14:00")
    }
    var showReviewsExpanded by remember { mutableStateOf(false) }
    var showPriceHelpModal by remember { mutableStateOf(false) }

    // 5 datas reais a partir de hoje
    val datePills = remember { generateNext5Days() }
    var selectedDateIndex by remember { mutableIntStateOf(0) }
    val activeDateDisplay = datePills[selectedDateIndex].fullDisplay

    // Categorias de serviço dinâmicas para filtro
    val serviceCategories = remember(salon) {
        val cats = salon.services.map { it.category }.distinct()
        listOf("Todos") + cats
    }

    val filteredServices = remember(salon, selectedServiceCategory) {
        if (selectedServiceCategory == "Todos") salon.services
        else salon.services.filter { it.category.equals(selectedServiceCategory, ignoreCase = true) }
    }

    // Cálculo de precificação em tempo real via PricingEngine
    val discountSlot = salon.discountSlots.find { it.time == selectedSlotTime }
    val discountPercent = discountSlot?.discountPercent ?: 0
    val staffSelected = salon.staff.find { it.id == selectedStaffId }
    val isTopSpecialist = staffSelected != null && salon.staff.firstOrNull()?.id == staffSelected.id

    val pricingQuote = remember(totalBasePrice, discountPercent, selectedSlotTime, isTopSpecialist) {
        val slotHour = selectedSlotTime.split(":").firstOrNull()?.toIntOrNull() ?: 14
        val isPeakHour = slotHour in listOf(18, 19, 20)
        PricingEngine.calculateQuote(
            basePrice = totalBasePrice,
            discountPercent = discountPercent,
            occupancyRatio = if (discountPercent >= 25) 0.25 else if (discountPercent > 0) 0.50 else 0.78,
            minutesUntilSlot = 180,
            isPeakHour = isPeakHour,
            staffRatingDelta = if (isTopSpecialist) 0.3 else 0.0,
            isLoyaltySubscriber = false
        )
    }

    val displacementFee = if (fulfillmentMode == "homecare") 25.0 else 0.0
    val finalPrice = pricingQuote.finalPrice + displacementFee

    // Horários ocupados neste salão/data via BookingEngine
    val allAppointments by SalonRepository.appointments.collectAsState()
    val bookedSlotsForSalon = remember(allAppointments, salon.id, activeDateDisplay) {
        BookingEngine.getBookedSlotsForSalon(
            appointments = allAppointments,
            salonId = salon.id,
            dateDisplay = activeDateDisplay
        )
    }

    if (showPriceHelpModal) {
        AlertDialog(
            onDismissRequest = { showPriceHelpModal = false },
            containerColor = WarmWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = DeepTeal
                    )
                    Text(
                        text = "Transparência de Tarifa BeautyPass",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ObsidianCharcoal
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "O motor de precificação dinâmica BeautyPass calibra os descontos conforme a ociosidade da agenda do salão em tempo real:",
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGray)
                    )
                    Text(
                        text = "• Horário Econômico (-15% a -35%): janelas fora de pico com cadeiras livres.\n• Última Hora: descontos extras liberados a menos de 2h do horário.\n• Especialista Destaque (+5%): profissionais com nota superior a +0.3 da média da casa.\n• Teto Garantido: o valor do serviço nunca ultrapassa a tabela oficial nas janelas econômicas.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ObsidianCharcoal,
                            lineHeight = 18.sp
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPriceHelpModal = false }) {
                    Text("Entendi", color = DeepTeal, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AlabasterBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 114.dp)
        ) {
            // 1. Sticky Top Bar ("Agendamento" conforme solicitado pelo usuário)
            Surface(
                color = WarmWhite.copy(alpha = 0.96f),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6))
                            .bounceClick(scaleDown = 0.92f) { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = ObsidianCharcoal,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Agendamento",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ObsidianCharcoal,
                            fontSize = 15.sp
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F4F6))
                                .bounceClick(scaleDown = 0.92f) { SalonRepository.toggleFavorite(salon.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favoritar Salão",
                                tint = if (isFavorite) CoralStatus else ObsidianCharcoal,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F4F6))
                                .bounceClick(scaleDown = 0.92f) { showPriceHelpModal = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = "Como funciona a Tarifa Justa",
                                tint = ObsidianCharcoal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Unified Salon Hero Card (16:9 Foto + Nome + Rating + Endereço + SERVIÇO SELECIONADO no mesmo card, igual à Web)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.06f)
                ),
            shape = RoundedCornerShape(20.dp),
            color = WarmWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0EFEA))
        ) {
            Column {
                BeautyPassImage(
                    model = selectedHeroImage,
                    contentDescription = salon.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = salon.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ObsidianCharcoal,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF92400E),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${salon.rating} (${salon.reviewCount})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF92400E),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = SlateGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${salon.address} (${salon.distanceKm} km)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGray,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Bloco SERVIÇO SELECIONADO embutido no mesmo card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SERVIÇO SELECIONADO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepTeal,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                fontSize = 11.sp
                            )
                        )
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = if (selectedServices.size == 1) "1 serviço" else "${selectedServices.size} serviços (Pacote)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DeepTeal,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val summaryTitle = if (selectedServices.size == 1) {
                        selectedServices.first().name
                    } else {
                        selectedServices.joinToString(" + ") { it.name }
                    }
                    Text(
                        text = summaryTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ObsidianCharcoal,
                            fontSize = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    val summaryDesc = if (selectedServices.size == 1) {
                        "${selectedServices.first().durationMinutes} min • Preço base R$ ${selectedServices.first().basePrice.toInt()} • ${selectedServices.first().description}"
                    } else {
                        "Duração total: $totalDurationMinutes min • Base combinada: R$ ${totalBasePrice.toInt()} • Economia aplicada sobre o pacote completo"
                    }
                    Text(
                        text = summaryDesc,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGray,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Mini-Galeria de 4 Fotos HD ("Ambiente & Espaço")
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ambiente & Espaço",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = "${galleryImages.size} fotos",
                    style = MaterialTheme.typography.labelSmall.copy(color = SlateGray)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                galleryImages.forEach { imgUrl ->
                    val isSelected = imgUrl == selectedHeroImage
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) DeepTeal else BorderSubtle,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedHeroImage = imgUrl }
                    ) {
                        BeautyPassImage(
                            model = imgUrl,
                            contentDescription = "Foto do salão",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Seletor de Modalidade de Atendimento ("Onde deseja ser atendido(a)?")
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = WarmWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Onde deseja ser atendido(a)?",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isSalon = fulfillmentMode == "salon"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { fulfillmentMode = "salon" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSalon) Color(0xFFECFDF5) else WarmWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSalon) 2.dp else 1.5.dp,
                            color = if (isSalon) DeepTeal else Color(0xFFE5E7EB)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Salão",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSalon) DeepTeal else ObsidianCharcoal,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "Sem taxa de deslocamento",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSalon) Color(0xFF065F46) else SlateGray,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    val isHomecare = fulfillmentMode == "homecare"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { fulfillmentMode = "homecare" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isHomecare) Color(0xFFECFDF5) else WarmWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isHomecare) 2.dp else 1.5.dp,
                            color = if (isHomecare) DeepTeal else Color(0xFFE5E7EB)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "A Domicílio (Home Care)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isHomecare) DeepTeal else ObsidianCharcoal,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "+ R$ 25 taxa de deslocamento",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isHomecare) Color(0xFF065F46) else SlateGray,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Catálogo Completo de Serviços com Chips de Categoria e Radio/Check Circular
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catálogo de Serviços",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = "${salon.services.size} serviços disponíveis",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DeepTeal,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chips de filtro de categoria do catálogo ("Todos", "Cabelo", "Unhas", etc.)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                serviceCategories.forEach { cat ->
                    val isCatActive = cat == selectedServiceCategory
                    Surface(
                        modifier = Modifier.clickable { selectedServiceCategory = cat },
                        shape = RoundedCornerShape(999.dp),
                        color = if (isCatActive) DeepTeal else WarmWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCatActive) DeepTeal else Color(0xFFE5E7EB)
                        )
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isCatActive) WarmWhite else SlateGray,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredServices.forEach { service ->
                    val isSelected = selectedServiceIds.contains(service.id)
                    ServiceSelectionItem(
                        service = service,
                        isSelected = isSelected,
                        onClick = {
                            selectedServiceIds = if (isSelected && selectedServiceIds.size > 1) {
                                selectedServiceIds - service.id
                            } else if (isSelected) {
                                selectedServiceIds
                            } else {
                                selectedServiceIds + service.id
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Escolha o Profissional (Opcional) + selo "Destaque da Casa" + "+0.3"
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Escolha o Profissional",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = "Opcional",
                    style = MaterialTheme.typography.labelSmall.copy(color = SlateGray)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    StaffAnyPill(
                        isSelected = selectedStaffId == "any",
                        onClick = { selectedStaffId = "any" }
                    )
                }
                itemsIndexed(salon.staff) { index, staff ->
                    StaffPill(
                        staff = staff,
                        isSelected = selectedStaffId == staff.id,
                        isTopSpecialist = index == 0,
                        onClick = { selectedStaffId = staff.id }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 7. Botão de Avaliações Verificadas Expansível (igual à Web)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showReviewsExpanded = !showReviewsExpanded },
                shape = RoundedCornerShape(12.dp),
                color = WarmWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Ver todas as avaliações verificadas (${salon.reviewCount})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ObsidianCharcoal,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Text(
                        text = if (showReviewsExpanded) "Ocultar" else "Abrir",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepTeal
                        )
                    )
                }
            }

            if (showReviewsExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = WarmWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val reviews = listOf(
                            Triple(
                                "Camila M.",
                                "5.0",
                                "Atendimento impecável! Agendei pelo horário econômico do BeautyPass e economizei 30% sem perder qualidade."
                            ),
                            Triple(
                                "Beatriz R.",
                                "4.9",
                                "Ambiente super acolhedor, pontualidade britânica no check-in com QR Code."
                            ),
                            Triple(
                                "Larissa F.",
                                "5.0",
                                "Profissionais excelentes e produtos de primeira linha. Recomendo muito!"
                            )
                        )
                        reviews.forEach { (author, score, comment) ->
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$author • Check-in Verificado",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ObsidianCharcoal
                                        )
                                    )
                                    Text(
                                        text = "Nota $score",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF92400E)
                                        )
                                    )
                                }
                                Text(
                                    text = comment,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateGray,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 8. Date Strip (5 próximos dias reais)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(datePills.size) { index ->
                val pill = datePills[index]
                val isSelected = index == selectedDateIndex
                Surface(
                    modifier = Modifier
                        .width(64.dp)
                        .clickable { selectedDateIndex = index },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) DeepTeal else WarmWhite,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = pill.dayOfWeek,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) WarmWhite.copy(alpha = 0.8f) else SlateGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = pill.dayNumber,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (isSelected) WarmWhite else ObsidianCharcoal,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 9. Horários Disponíveis + Legenda de 4 Estados + Quick Slots Strip Sincronizado
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Horários Disponíveis",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = "Toque rápido ou gire o relógio",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SlateGray,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Legenda dos 4 estados de horário (Disponível, Alta Procura, Desconto, Ocupado)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SlotLegendItem(
                    bgColor = WarmWhite,
                    borderColor = Color(0xFFD1D5DB),
                    label = "Disponível"
                )
                SlotLegendItem(
                    bgColor = Color(0xFFFEF3C7),
                    borderColor = Color(0xFFF59E0B),
                    label = "Alta Procura"
                )
                SlotLegendItem(
                    bgColor = Color(0xFFECFDF5),
                    borderColor = EmeraldSuccess,
                    label = "Desconto"
                )
                SlotLegendItem(
                    bgColor = Color(0xFFF3F4F6),
                    borderColor = Color(0xFF9CA3AF),
                    label = "Ocupado"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Slots Strip sincronizado com o Radial Gauge
            val allHours = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allHours.forEach { slotTime ->
                    val slotDisc = salon.discountSlots.find { it.time == slotTime }?.discountPercent ?: 0
                    val isOccupied = bookedSlotsForSalon.contains(slotTime)
                    val isSelected = selectedSlotTime == slotTime
                    val hourNum = slotTime.split(":").firstOrNull()?.toIntOrNull() ?: 14
                    val isPeak = hourNum in listOf(18, 19, 20) && slotDisc == 0

                    val bgColor = when {
                        isOccupied -> Color(0xFFF3F4F6)
                        isSelected -> DeepTeal
                        slotDisc > 0 -> Color(0xFFECFDF5)
                        isPeak -> Color(0xFFFEF3C7)
                        else -> WarmWhite
                    }
                    val borderColor = when {
                        isOccupied -> Color(0xFFD1D5DB)
                        isSelected -> DeepTeal
                        slotDisc > 0 -> EmeraldSuccess
                        isPeak -> Color(0xFFF59E0B)
                        else -> Color(0xFFD1D5DB)
                    }
                    val textColor = when {
                        isOccupied -> Color(0xFF9CA3AF)
                        isSelected -> WarmWhite
                        slotDisc > 0 -> DeepTeal
                        isPeak -> Color(0xFF92400E)
                        else -> ObsidianCharcoal
                    }

                    Surface(
                        modifier = Modifier.clickable(enabled = !isOccupied) {
                            selectedSlotTime = slotTime
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = bgColor,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = slotTime,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textColor,
                                    textDecoration = if (isOccupied) TextDecoration.LineThrough else TextDecoration.None
                                )
                            )
                            Text(
                                text = when {
                                    isOccupied -> "Ocupado"
                                    slotDisc > 0 -> "-$slotDisc%"
                                    isPeak -> "Alta proc."
                                    else -> "R$ ${totalBasePrice.toInt()}"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) LuminousMint else textColor,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 10. Seletor Radial Analógico-Digital (Inspiração 1)
        RadialGaugeTimeSelector(
            discountSlots = salon.discountSlots,
            selectedTime = selectedSlotTime,
            basePrice = totalBasePrice,
            occupiedTimes = bookedSlotsForSalon,
            onTimeSelected = { time -> selectedSlotTime = time }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 11. Guarantee Tiles (Garantia de Preço & Cancelamento Grátis)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = WarmWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFECFDF5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = DeepTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Teto de Preço",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ObsidianCharcoal
                            )
                        )
                        Text(
                            text = "Garantido sem surpresas",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGray,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = WarmWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFECFDF5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = DeepTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Cancelamento Grátis",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ObsidianCharcoal
                            )
                        )
                        Text(
                            text = "Até 4h antes do horário",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGray,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        }

        // 12. Sticky Bottom Booking Bar (Permanentemente visível durante a rolagem)
        val isSelectedSlotOccupied = bookedSlotsForSalon.contains(selectedSlotTime)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = WarmWhite.copy(alpha = 0.98f),
            shadowElevation = 18.dp,
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (discountPercent > 0) "Horário Econômico (-$discountPercent%)" else "Tarifa Oficial",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DeepTeal,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.5.sp
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "R$ ${finalPrice.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ObsidianCharcoal,
                                fontSize = 22.sp
                            )
                        )
                        if (pricingQuote.savingsAmount > 0) {
                            Text(
                                text = "R$ ${totalBasePrice.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateGray,
                                    textDecoration = TextDecoration.LineThrough,
                                    fontSize = 11.5.sp
                                ),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (fulfillmentMode == "homecare") {
                            "Home Care (+R$ 25) • $activeDateDisplay às $selectedSlotTime"
                        } else {
                            "$activeDateDisplay às $selectedSlotTime"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateGray,
                            fontSize = 10.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    modifier = Modifier.bounceClick(
                        scaleDown = 0.95f,
                        enabled = !isSelectedSlotOccupied,
                        onClick = {
                            onProceedToCheckout(
                                salon.id,
                                selectedServices.joinToString(",") { it.id },
                                selectedSlotTime,
                                fulfillmentMode
                            )
                        }
                    ),
                    color = if (isSelectedSlotOccupied) SlateGray else DeepTeal,
                    shape = RoundedCornerShape(14.dp),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isSelectedSlotOccupied) {
                                "Horário Ocupado"
                            } else {
                                "Avançar para Pagamento"
                            },
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = WarmWhite,
                                fontSize = 12.5.sp
                            )
                        )
                        if (!isSelectedSlotOccupied) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = WarmWhite,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotLegendItem(
    bgColor: Color,
    borderColor: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(bgColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(3.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = SlateGray,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun ServiceSelectionItem(
    service: Service,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFFECFDF5) else WarmWhite,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.5.dp,
            color = if (isSelected) DeepTeal else Color(0xFFE5E7EB)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ObsidianCharcoal,
                            fontSize = 13.sp
                        )
                    )
                    Surface(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = service.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${service.durationMinutes} min • Base R$ ${service.basePrice.toInt()} • ${service.description}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SlateGray,
                        fontSize = 11.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Radio/Check circular igual à Web (.service-item-radio)
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

@Composable
private fun StaffAnyPill(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(142.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFFECFDF5) else WarmWhite,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.5.dp,
            color = if (isSelected) DeepTeal else Color(0xFFE5E7EB)
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCCFBF1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = DeepTeal,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = "Qualquer Profissional",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 11.sp,
                        lineHeight = 13.sp
                    ),
                    maxLines = 2
                )
                Text(
                    text = "Sem taxa extra",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DeepTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun StaffPill(
    staff: Staff,
    isSelected: Boolean,
    isTopSpecialist: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(172.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFFECFDF5) else WarmWhite,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.5.dp,
            color = if (isSelected) DeepTeal else Color(0xFFE5E7EB)
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BeautyPassImage(
                model = staff.avatarUrl,
                contentDescription = staff.name,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) DeepTeal else Color(0xFFE5E7EB), CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = staff.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = staff.role,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SlateGray,
                        fontSize = 10.sp
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
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = if (isTopSpecialist) "5.0 (+0.3)" else "4.9",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                    )
                    if (isTopSpecialist) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Destaque da Casa",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 8.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun generateNext5Days(): List<DatePill> {
    val calendar = Calendar.getInstance()
    val dayOfWeekFormat = SimpleDateFormat("EEE", Locale("pt", "BR"))
    val dayNumberFormat = SimpleDateFormat("dd", Locale("pt", "BR"))
    val monthFormat = SimpleDateFormat("MMM", Locale("pt", "BR"))

    return (0 until 5).map { index ->
        val date = calendar.time
        val dowRaw = dayOfWeekFormat.format(date).replace(".", "").uppercase(Locale("pt", "BR"))
        val dayNum = dayNumberFormat.format(date)
        val monthRaw = monthFormat.format(date).replace(".", "").replaceFirstChar { it.uppercase() }
        val label = if (index == 0) "HOJE" else if (index == 1) "AMANHÃ" else dowRaw
        val fullDisplay = if (index == 0) "Hoje, $dayNum $monthRaw" else "$dowRaw, $dayNum $monthRaw"
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        DatePill(dayOfWeek = label, dayNumber = dayNum, fullDisplay = fullDisplay)
    }
}
