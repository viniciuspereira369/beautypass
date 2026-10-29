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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.beautypass.app.data.BookingEngine
import com.beautypass.app.data.PricingEngine
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.Salon
import com.beautypass.app.model.Service
import com.beautypass.app.model.Staff
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.RadialGaugeTimeSelector
import java.text.SimpleDateFormat
import java.util.*

data class DatePill(val dayOfWeek: String, val dayNumber: String, val fullDisplay: String)

/**
 * Tela 3: Detalhe do Salão & Agendamento com Seletor Radial Analógico-Digital.
 *
 * Implementado conforme especificações de:
 * - ORIGINAL_REQUEST.md (R1.3)
 * - Inspiração 1 (documenta_o_ui_ux_agendamento_de_corridas_aut_nomas.md)
 * - Inspiração 3 (documenta_o_ui_ux_app_social_food_delivery.md)
 * - Mini-galeria de 4 fotos HD com switcher interativo, catálogo, profissionais,
 *   date strip de 5 dias, RadialGaugeTimeSelector e guarantee tiles.
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

    // Faixa dinâmica de 5 dias (Date Strip)
    val datePills = remember {
        val pills = mutableListOf<DatePill>()
        val cal = Calendar.getInstance()
        val dayOfWeekFormat = SimpleDateFormat("EEE", Locale("pt", "BR"))
        val dayNumberFormat = SimpleDateFormat("dd", Locale.getDefault())
        val fullFormat = SimpleDateFormat("dd/MM (EEE)", Locale("pt", "BR"))

        for (i in 0 until 5) {
            val dOfWeek = dayOfWeekFormat.format(cal.time).replace(".", "").uppercase()
            val dNum = dayNumberFormat.format(cal.time)
            val fullDisp = fullFormat.format(cal.time)
            pills.add(DatePill(dOfWeek, dNum, fullDisp))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        pills
    }

    var selectedDateIndex by remember { mutableStateOf(0) }

    // Cálculo da Precificação Dinâmica Multi-Serviço
    val slotPromo = remember(selectedSlotTime, salon) {
        salon.discountSlots.find { it.time == selectedSlotTime }
    }
    val discountPct = slotPromo?.discountPct ?: 0
    val isUrgent = slotPromo?.type == "urgent"
    val pricing = remember(totalBasePrice, discountPct, isUrgent, selectedSlotTime) {
        PricingEngine.calculateSlotPricing(
            basePrice = totalBasePrice,
            discountPct = discountPct,
            isUrgent = isUrgent,
            time = selectedSlotTime
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = salon.name,
                        maxLines = 1,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = OceanicCharcoal)
                    }
                },
                actions = {
                    IconButton(onClick = { SalonRepository.toggleFavorite(salon.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (isFavorite) CoralPromo else OceanicCharcoal
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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${selectedServices.size} serviço(s) • ${totalDurationMinutes} min • ${selectedSlotTime}",
                            fontSize = 11.sp,
                            color = NeutralMuted
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (pricing.hasDiscount) {
                                Text(
                                    text = "R$ ${String.format("%.2f", pricing.basePrice)}",
                                    fontSize = 12.sp,
                                    color = Slate400,
                                    textDecoration = TextDecoration.LineThrough
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
                            onProceedToCheckout(
                                salon.id,
                                selectedServices.joinToString(",") { it.id },
                                selectedSlotTime,
                                fulfillmentMode
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Continuar com este Horário",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .verticalScroll(scrollState)
        ) {
            // Seção 1: Hero Image Principal Interativa (Proporção Rígida 16/9)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = selectedHeroImage,
                    contentDescription = salon.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Badge de Prova Social Flutuante
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                        .background(Slate900.copy(alpha = 0.8f), CapsuleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = salon.socialProof,
                        color = SurfaceWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Seção 2: Mini-Galeria de 4 Fotos HD com Switcher Interativo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                galleryImages.forEachIndexed { index, imgUrl ->
                    val isSelected = imgUrl == selectedHeroImage
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) SereneTeal else OutlineVariant,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedHeroImage = imgUrl }
                    ) {
                        AsyncImage(
                            model = imgUrl,
                            contentDescription = "Foto ${index + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Seção 3: Informações do Salão & Responsável Técnico
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = salon.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = OceanicCharcoal,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verificado",
                                tint = SereneTeal,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "${salon.neighborhood} • ${salon.address}",
                            fontSize = 12.sp,
                            color = NeutralMuted
                        )
                    }

                    // Chip de Avaliação
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(StarAmberBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${salon.rating} (${salon.reviewsCount})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selo do Responsável Técnico / Fundador
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MintSurface, RoundedCornerShape(12.dp))
                        .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = salon.leadStaff.avatarUrl,
                        contentDescription = salon.leadStaff.name,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = salon.leadStaff.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = OceanicCharcoal
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SereneTeal,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = salon.leadStaff.role,
                            fontSize = 11.sp,
                            color = NeutralMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seção 3.5: Modalidade de Atendimento (Fulfillment Switch: No Salão vs A Domicílio)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Modalidade de Atendimento",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanicCharcoal
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CanvasBase, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Botão No Salão
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (fulfillmentMode == "salon") SereneTeal else Color.Transparent)
                            .clickable { fulfillmentMode = "salon" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (fulfillmentMode == "salon") SurfaceWhite else OceanicCharcoal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "No Salão",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (fulfillmentMode == "salon") SurfaceWhite else OceanicCharcoal
                            )
                        }
                    }

                    // Botão A Domicílio
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (fulfillmentMode == "homecare") SereneTeal else Color.Transparent)
                            .clickable { fulfillmentMode = "homecare" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = if (fulfillmentMode == "homecare") SurfaceWhite else OceanicCharcoal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "A Domicílio",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (fulfillmentMode == "homecare") SurfaceWhite else OceanicCharcoal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MintSurface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (fulfillmentMode == "salon")
                            "Atendimento presencial no salão parceiro com toda a infraestrutura e produtos profissionais."
                        else
                            "O profissional vai até você! Taxa de deslocamento calculada no checkout (R$ 10,00 base até 2 km + R$ 5,00/km adicional).",
                        fontSize = 11.sp,
                        color = OceanicCharcoal,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seção 4: Catálogo de Serviços do Salão
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Catálogo de Serviços",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanicCharcoal
                )
                Spacer(modifier = Modifier.height(10.dp))

                salon.services.forEach { srv ->
                    val isSelected = selectedServiceIds.contains(srv.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedServiceIds = if (isSelected) {
                                    if (selectedServiceIds.size > 1) selectedServiceIds - srv.id else selectedServiceIds
                                } else {
                                    selectedServiceIds + srv.id
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            if (isSelected) SereneTeal else SurfaceWhite,
                                            RoundedCornerShape(5.dp)
                                        )
                                        .border(
                                            1.5.dp,
                                            if (isSelected) SereneTeal else OutlineVariant,
                                            RoundedCornerShape(5.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = SurfaceWhite,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = srv.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OceanicCharcoal
                                    )
                                    Text(
                                        text = "${srv.durationMinutes} min • ${srv.description}",
                                        fontSize = 11.sp,
                                        color = NeutralMuted
                                    )
                                }
                            }
                            Text(
                                text = "R$ ${String.format("%.2f", srv.basePrice)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) SereneTeal else OceanicCharcoal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seção 5: Seletor de Profissional (Equipe)
            if (salon.staff.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Profissional",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            val isAnySelected = selectedStaffId == "any"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isAnySelected) MintSurface else CanvasBase)
                                    .border(
                                        width = if (isAnySelected) 1.5.dp else 1.dp,
                                        color = if (isAnySelected) SereneTeal else OutlineVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedStaffId = "any" }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Qualquer Profissional",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAnySelected) SereneTeal else OceanicCharcoal
                                )
                            }
                        }

                        items(salon.staff) { st ->
                            val isSelected = selectedStaffId == st.id
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MintSurface else CanvasBase)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) SereneTeal else OutlineVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedStaffId = st.id }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = st.avatarUrl,
                                    contentDescription = st.name,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = st.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OceanicCharcoal
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = StarAmber,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${st.rating}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NeutralMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Seção 6: Faixa de Datas (Date Strip)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Escolha a Data",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanicCharcoal
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    datePills.forEachIndexed { idx, pill ->
                        val isSelected = selectedDateIndex == idx
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 3.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SereneTeal else CanvasBase)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) SereneTeal else OutlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedDateIndex = idx }
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = pill.dayOfWeek,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SurfaceWhite else NeutralMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = pill.dayNumber,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) SurfaceWhite else OceanicCharcoal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seção 7: Seletor Radial Analógico-Digital de Horários (Inspiração 1)
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                RadialGaugeTimeSelector(
                    selectedTime = selectedSlotTime,
                    basePrice = totalBasePrice,
                    discountSlots = salon.discountSlots,
                    onTimeSelected = { selectedSlotTime = it }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Seção 8: Guarantee Tiles de Alívio de Fricção (Inspiração 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tile 1: Cancelamento Grátis
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancelamento", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OceanicCharcoal)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Grátis até 24h antes do horário", fontSize = 10.sp, color = NeutralMuted)
                    }
                }

                // Tile 2: Tolerância e Higienização
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = EconomyGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Higienização", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OceanicCharcoal)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("10 min de buffer garantido", fontSize = 10.sp, color = NeutralMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
