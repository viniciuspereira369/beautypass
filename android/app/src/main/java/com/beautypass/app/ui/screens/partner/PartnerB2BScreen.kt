package com.beautypass.app.ui.screens.partner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
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
import com.beautypass.app.model.Appointment
import com.beautypass.app.model.B2BDiscountRule
import com.beautypass.app.model.H3LoiRecord
import com.beautypass.app.model.Salon
import com.beautypass.app.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tela B2B: Portal do Salão Parceiro, Gestão de Slots Ociosos, Descontos Dinâmicos e Validação H3.
 *
 * Implementado conforme especificações de DESIGN_EXTRACT_SPEC.md e frentes de validação do BeautyPass:
 * - Aba 1: Agenda com Slots Ociosos e Ativação de Promoções Dinâmicas (-25% OFF).
 * - Aba 2: Configurador de Regras de Desconto Dinâmico (15% a 35%) com Simulador de Repasse Líquido.
 * - Aba 3: Validação de Voucher QR do Cliente e Formulário de Adesão / Carta de Intenção (LOI - H3).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerB2BScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val salons = SalonRepository.salons
    var selectedSalonId by remember { mutableStateOf(salons.first().id) }
    val currentSalon = remember(selectedSalonId) {
        SalonRepository.getSalonById(selectedSalonId) ?: salons.first()
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Agenda, 1: Regras, 2: Check-in & H3
    val b2bRules by SalonRepository.b2bRules.collectAsState()
    val h3Report by SalonRepository.h3Report.collectAsState()
    val appointments by SalonRepository.appointments.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Painel do Salão Parceiro (B2B)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = OceanicCharcoal)
                    }
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
            // Seletor de Salão Parceiro
            Surface(
                color = SurfaceWhite,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Salão Selecionado:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(salons) { salon ->
                            val isSelected = salon.id == selectedSalonId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MintSurface else CanvasBase)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) SereneTeal else OutlineVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedSalonId = salon.id }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = salon.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SereneTeal else OceanicCharcoal
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SereneTeal,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Barra de Abas (3 Abas Principais)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceWhite,
                contentColor = SereneTeal
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Agenda & Slots",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTab == 0) SereneTeal else NeutralMuted
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Regras & Repasse",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTab == 1) SereneTeal else NeutralMuted
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "Check-in & H3",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTab == 2) SereneTeal else NeutralMuted
                        )
                    }
                )
            }

            // Conteúdo da Aba Ativa
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                when (selectedTab) {
                    0 -> PartnerAgendaTab(salon = currentSalon, appointments = appointments)
                    1 -> PartnerDiscountRulesTab(salon = currentSalon, rules = b2bRules)
                    2 -> PartnerCheckInAndH3Tab(salon = currentSalon, h3Report = h3Report)
                }
            }
        }
    }
}

/**
 * Aba 1: Agenda com Slots Ociosos & Ativação de Promoções Dinâmicas.
 */
@Composable
fun PartnerAgendaTab(
    salon: Salon,
    appointments: List<Appointment>
) {
    var activatedSlots by remember(salon.id) {
        mutableStateOf(salon.discountSlots.map { it.time }.toSet())
    }

    val daySlots = listOf(
        "09:00" to "Ocupado",
        "10:30" to "Vago",
        "11:30" to "Vago",
        "14:00" to "Ocioso",
        "15:00" to "Ocioso",
        "16:30" to "Ocupado",
        "17:30" to "Vago",
        "19:00" to "Ocioso"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Card Explicativo de Monetização de Cadeira Ociosa
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MintSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MintLight)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Otimização de Ocupação da Agenda",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Cadeiras vazias em horários de vale geram R$ 0,00 de receita fixa. Ao ativar tarifas dinâmicas de 15% a 35% de desconto, o salão preenche a capacidade ociosa e atrai novas clientes da região.",
                    fontSize = 11.sp,
                    color = OceanicCharcoal,
                    lineHeight = 15.sp
                )
            }
        }

        Text(
            text = "Grade de Horários de Hoje — ${salon.name}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = OceanicCharcoal
        )

        daySlots.forEach { (time, baseStatus) ->
            val isPromoActive = activatedSlots.contains(time)
            val isOccupied = baseStatus == "Ocupado" || appointments.any { it.salon.id == salon.id && it.timeSlot == time }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPromoActive) MintSurface else SurfaceWhite
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isPromoActive) SereneTeal else OutlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isPromoActive) SereneTeal else NeutralMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = time,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = OceanicCharcoal
                            )
                            val statusDisplay = when {
                                isOccupied -> "Horário Reservado"
                                isPromoActive -> "Oferta Dinâmica Ativa (-25% OFF)"
                                else -> "Horário Vago / Sem Promoção"
                            }
                            Text(
                                text = statusDisplay,
                                fontSize = 11.sp,
                                color = if (isPromoActive) EconomyGreen else NeutralMuted,
                                fontWeight = if (isPromoActive) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    if (isOccupied) {
                        Box(
                            modifier = Modifier
                                .background(MintSurface, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Ocupado",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SereneTealDark
                            )
                        }
                    } else if (isPromoActive) {
                        Button(
                            onClick = { activatedSlots = activatedSlots - time },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralPromoBg, contentColor = CoralPromoText),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Pausar -25%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { activatedSlots = activatedSlots + time },
                            colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Ativar -25%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Aba 2: Configurador de Regras de Desconto Dinâmico & Simulador de Repasse Líquido.
 */
@Composable
fun PartnerDiscountRulesTab(
    salon: Salon,
    rules: List<B2BDiscountRule>
) {
    var discountPct by remember { mutableStateOf(25) }
    var selectedDay by remember { mutableStateOf("Terça-feira") }
    var startTime by remember { mutableStateOf("13:00") }
    var endTime by remember { mutableStateOf("16:00") }
    var selectedServiceCategory by remember { mutableStateOf("Todos os Serviços") }

    val salonRules = remember(rules, salon.id) {
        rules.filter { it.salonId == salon.id }
    }

    // Cálculos do Simulador de Repasse (Base R$ 100,00)
    val baseSimulated = 100.0
    val clientPriceSimulated = baseSimulated * (1.0 - (discountPct / 100.0))
    val platformFeeSimulated = clientPriceSimulated * 0.15 // 15% take rate BeautyPass
    val netPayoutSimulated = clientPriceSimulated - platformFeeSimulated

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Card do Configurador de Regra
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Criar Regra de Desconto Dinâmico",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanicCharcoal
                )

                Text(
                    text = "Defina horários recorrentes em que o algoritmo ativará descontos para acelerar o preenchimento da agenda.",
                    fontSize = 11.sp,
                    color = NeutralMuted
                )

                // Percentual de Desconto (15% a 35%)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Percentual de Desconto:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OceanicCharcoal)
                        Text("-$discountPct%", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = SereneTeal)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(15, 20, 25, 30, 35).forEach { pct ->
                            val isSelected = discountPct == pct
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SereneTeal else CanvasBase)
                                    .border(1.dp, if (isSelected) SereneTeal else OutlineVariant, RoundedCornerShape(8.dp))
                                    .clickable { discountPct = pct }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$pct%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) SurfaceWhite else OceanicCharcoal
                                )
                            }
                        }
                    }
                }

                // Dia da Semana e Horário
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = selectedDay,
                        onValueChange = { selectedDay = it },
                        label = { Text("Dia da Semana") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = "$startTime - $endTime",
                        onValueChange = { },
                        label = { Text("Janela Horária") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        readOnly = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Simulador de Repasse Líquido
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MintSurface, RoundedCornerShape(12.dp))
                        .border(1.dp, MintLight, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Simulador de Repasse (Exemplo R$ 100,00 base):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Preço com Desconto (-$discountPct%):", fontSize = 11.sp, color = NeutralMuted)
                            Text("R$ ${String.format("%.2f", clientPriceSimulated)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OceanicCharcoal)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Taxa BeautyPass (15%):", fontSize = 11.sp, color = NeutralMuted)
                            Text("- R$ ${String.format("%.2f", platformFeeSimulated)}", fontSize = 11.sp, color = Slate400)
                        }
                        Divider(color = OutlineVariant, thickness = 0.8.dp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Repasse Líquido ao Salão:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SereneTealDark)
                            Text("R$ ${String.format("%.2f", netPayoutSimulated)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = SereneTeal)
                        }
                        Text(
                            text = "Ganho real: Em vez de R$ 0,00 de horário vago, seu caixa recebe R$ ${String.format("%.2f", netPayoutSimulated)} líquidos.",
                            fontSize = 10.sp,
                            color = EconomyGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = {
                        val newRule = B2BDiscountRule(
                            id = "rule_${System.currentTimeMillis()}",
                            salonId = salon.id,
                            dayOfWeek = selectedDay,
                            time = startTime,
                            startTime = startTime,
                            endTime = endTime,
                            discountPct = discountPct,
                            discountPercent = discountPct,
                            serviceCategory = selectedServiceCategory,
                            isActive = true
                        )
                        SalonRepository.saveB2BRule(newRule)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SereneTeal)
                ) {
                    Text("Salvar Regra de Desconto", fontWeight = FontWeight.Bold, color = SurfaceWhite)
                }
            }
        }

        // Lista de Regras Ativas do Salão
        Text(
            text = "Regras Ativas para ${salon.name}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = OceanicCharcoal
        )

        if (salonRules.isEmpty()) {
            Text(
                text = "Nenhuma regra customizada cadastrada. O salão utiliza a grade padrão.",
                fontSize = 12.sp,
                color = NeutralMuted
            )
        } else {
            salonRules.forEach { rule ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "-${rule.discountPercent}% OFF",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SereneTeal
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${rule.dayOfWeek}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanicCharcoal
                                )
                            }
                            Text(
                                text = "Horário: ${rule.startTime} às ${rule.endTime} • ${rule.serviceCategory}",
                                fontSize = 11.sp,
                                color = NeutralMuted
                            )
                        }

                        IconButton(onClick = { SalonRepository.removeB2BRule(rule.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remover", tint = CoralPromo)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Aba 3: Validação de Voucher QR do Cliente & Validação da Hipótese H3 (Carta de Intenção / LOI).
 */
@Composable
fun PartnerCheckInAndH3Tab(
    salon: Salon,
    h3Report: com.beautypass.app.model.H3Report
) {
    var voucherCodeInput by remember { mutableStateOf("") }
    var validatedAppointment by remember { mutableStateOf<Appointment?>(null) }
    var checkinMessage by remember { mutableStateOf<String?>(null) }

    // Formulário de Adesão H3 (Carta de Intenção / LOI)
    var partnerNameInput by remember(salon) { mutableStateOf(salon.name) }
    var managerNameInput by remember(salon) { mutableStateOf(salon.leadStaff.name) }
    var cnpjInput by remember { mutableStateOf("12.345.678/0001-90") }
    var isAgreed by remember { mutableStateOf(true) }
    var loiSuccessMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Seção 1: Check-in de Voucher do Cliente
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Validação de Voucher & Check-in",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                }

                Text(
                    text = "Digite o código do voucher impresso na tela do cliente para confirmar a presença e liberar o repasse.",
                    fontSize = 11.sp,
                    color = NeutralMuted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = voucherCodeInput,
                        onValueChange = { voucherCodeInput = it },
                        placeholder = { Text("Ex: BP-100234") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            val appt = SalonRepository.checkinVoucher(voucherCodeInput.trim())
                            if (appt != null) {
                                validatedAppointment = appt
                                checkinMessage = "Voucher validado com sucesso! Atendimento em andamento."
                            } else {
                                validatedAppointment = null
                                checkinMessage = "Código de voucher não encontrado ou já concluído."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text("Validar", fontWeight = FontWeight.Bold, color = SurfaceWhite)
                    }
                }

                if (checkinMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (validatedAppointment != null) MintSurface else CoralPromoBg,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = checkinMessage!!,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (validatedAppointment != null) EconomyGreen else CoralPromoText
                        )
                    }
                }

                if (validatedAppointment != null) {
                    val appt = validatedAppointment!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CanvasBase, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text("Cliente: Camila Alves (P01)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OceanicCharcoal)
                        Text("Serviço: ${appt.service.name}", fontSize = 11.sp, color = NeutralMuted)
                        Text("Modalidade: ${if (appt.fulfillmentMode == "homecare") "A Domicílio" else "No Salão"}", fontSize = 11.sp, color = SereneTealDark, fontWeight = FontWeight.Bold)
                        if (!appt.customerNotes.isNullOrBlank()) {
                            Text("Obs: ${appt.customerNotes}", fontSize = 11.sp, color = OceanicCharcoal)
                        }
                    }
                }
            }
        }

        // Seção 2: Validação da Hipótese H3 (Adesão Salões Parceiros ≥ 60%)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ThumbUp, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Status da Validação H3 (Adesão)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(if (h3Report.isHypothesisValidated) MintSurface else CoralPromoBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (h3Report.isHypothesisValidated) "Aprovado" else "Em Andamento",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (h3Report.isHypothesisValidated) EconomyGreen else CoralPromoText
                        )
                    }
                }

                // Indicador Numérico
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MintSurface, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Salões Formalizados (LOI):", fontSize = 11.sp, color = NeutralMuted)
                        Text(
                            text = "${h3Report.signedCount} de ${h3Report.targetCount} Salões (${(h3Report.adhesionRate * 100).toInt()}%)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SereneTeal
                        )
                    }
                    Text(
                        text = "Meta: ≥ 60%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
                }

                Divider(color = OutlineVariant, thickness = 0.8.dp)

                // Formulário de Adesão / Assinatura de Carta de Intenção (LOI)
                Text(
                    text = "Formalizar Carta de Intenção (LOI) do Salão",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanicCharcoal
                )

                OutlinedTextField(
                    value = partnerNameInput,
                    onValueChange = { partnerNameInput = it },
                    label = { Text("Nome do Salão Parceiro") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = managerNameInput,
                    onValueChange = { managerNameInput = it },
                    label = { Text("Nome do Responsável Legal / Gerente") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = cnpjInput,
                    onValueChange = { cnpjInput = it },
                    label = { Text("CNPJ da Empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAgreed = !isAgreed },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAgreed,
                        onCheckedChange = { isAgreed = it },
                        colors = CheckboxDefaults.colors(checkedColor = SereneTeal)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Concordo em disponibilizar slots ociosos com descontos dinâmicos de 15% a 35% no BeautyPass.",
                        fontSize = 11.sp,
                        color = OceanicCharcoal,
                        lineHeight = 15.sp
                    )
                }

                Button(
                    onClick = {
                        val record = H3LoiRecord(
                            salonId = salon.id,
                            salonName = partnerNameInput,
                            managerName = managerNameInput,
                            cnpj = cnpjInput,
                            isAgreed = isAgreed,
                            discountRangeAgreed = "15% - 35%",
                            signedAtIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }.format(Date())
                        )
                        SalonRepository.submitH3Loi(record)
                        loiSuccessMessage = "Carta de Intenção formalizada com sucesso! O salão agora faz parte da rede homologada BeautyPass."
                    },
                    enabled = isAgreed && partnerNameInput.isNotBlank() && managerNameInput.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SereneTeal)
                ) {
                    Text("Assinar Carta de Intenção (LOI)", fontWeight = FontWeight.Bold, color = SurfaceWhite)
                }

                if (loiSuccessMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MintSurface, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = loiSuccessMessage!!,
                            fontSize = 11.sp,
                            color = EconomyGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
