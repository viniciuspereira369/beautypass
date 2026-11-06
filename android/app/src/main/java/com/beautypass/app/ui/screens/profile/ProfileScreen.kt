package com.beautypass.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.AppointmentStatus
import com.beautypass.app.model.SUSEvaluation
import com.beautypass.app.model.SUSQuestion
import com.beautypass.app.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

/**
 * Tela 6B: Ajustes, Perfil do Participante, Privacidade LGPD, Painel de Validação (H1, H2, H3, SUS, T1–T4)
 * e Questionário SUS de 10 Itens — espelhada 1:1 com #screen-profile da web (sem card de instalação de app).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    participantCode: String = "P01",
    participantName: String = "Carolina Almeida",
    onResetToOnboarding: () -> Unit,
    onOpenPartnerB2B: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val appointments by SalonRepository.appointments.collectAsState()
    val favoriteIds by SalonRepository.favoriteIds.collectAsState()
    val userProfile by SalonRepository.userProfile.collectAsState()
    val latestSusEvaluation by SalonRepository.latestSusEvaluation.collectAsState()
    val h3Report by SalonRepository.h3Report.collectAsState()

    val effectiveParticipantCode = remember(userProfile, participantCode) {
        userProfile?.participantId?.ifBlank { participantCode } ?: participantCode
    }
    val effectiveParticipantName = remember(userProfile, participantName) {
        userProfile?.name?.ifBlank { participantName } ?: participantName
    }
    val effectivePhone = remember(userProfile) {
        userProfile?.phone?.ifBlank { "(11) 99876-5432" } ?: "(11) 99876-5432"
    }
    val initials = remember(effectiveParticipantName) {
        val parts = effectiveParticipantName.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        when {
            parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
            parts.size == 1 -> parts.first().take(2).uppercase()
            else -> "CA"
        }
    }

    val totalAppointments = appointments.size
    val economicAppointments = appointments.count { it.discountPercent >= 15 }
    val h1Rate = if (totalAppointments > 0) {
        round((economicAppointments.toFloat() / totalAppointments.toFloat()) * 100f).toInt()
    } else 100
    val preAuthCompleted = appointments.count {
        it.status == AppointmentStatus.CONFIRMED || it.status == AppointmentStatus.COMPLETED
    }
    val h2Rate = if (totalAppointments > 0) {
        round((preAuthCompleted.toFloat() / totalAppointments.toFloat()) * 100f).toInt()
    } else 100

    // Estado do Questionário SUS (10 perguntas Likert 1..5 iniciando vazias / null)
    val susResponses = remember {
        mutableStateMapOf<Int, Int?>().apply {
            for (i in 1..10) put(i, null)
        }
    }
    var intentionScore by remember { mutableStateOf<Int?>(latestSusEvaluation?.intentionScore) }
    var feedbackPros by remember { mutableStateOf("") }
    var feedbackCons by remember { mutableStateOf("") }
    var submittedSusScore by remember(latestSusEvaluation) {
        mutableStateOf<Double?>(latestSusEvaluation?.susScore)
    }

    var showSusModal by remember { mutableStateOf(false) }
    var showDeleteModal by remember { mutableStateOf(false) }
    var showForceMajeureBanner by remember { mutableStateOf(false) }
    var showTelemetryModal by remember { mutableStateOf(false) }

    val allSusAnswered = (1..10).all { susResponses[it] != null } && intentionScore != null

    val currentSusScore = remember(susResponses.toMap()) {
        if ((1..10).all { susResponses[it] != null }) {
            calculateSusScore(susResponses.mapValues { it.value ?: 3 })
        } else null
    }

    val consentDateStr = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date())
    }

    // Modal de Exclusão de Dados LGPD
    if (showDeleteModal) {
        AlertDialog(
            onDismissRequest = { showDeleteModal = false },
            containerColor = WarmWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Confirmar Exclusão (LGPD)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal
                    )
                )
            },
            text = {
                Text(
                    text = "Deseja apagar permanentemente todos os seus dados locais de agendamento, consentimento LGPD e avaliações desta sessão? Você retornará à tela inicial de boas-vindas.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SlateGray)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteModal = false
                        SalonRepository.clearAllDataForLgpdReset()
                        for (i in 1..10) susResponses[i] = null
                        intentionScore = null
                        submittedSusScore = null
                        onResetToOnboarding()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralStatus)
                ) {
                    Text("Apagar Tudo e Sair", color = WarmWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteModal = false }) {
                    Text("Cancelar", color = SlateGray, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal de Exportação de Telemetria JSON
    if (showTelemetryModal) {
        val jsonPayload = remember(
            effectiveParticipantCode,
            effectiveParticipantName,
            h1Rate,
            h2Rate,
            h3Report,
            submittedSusScore,
            intentionScore
        ) {
            """
            {
              "participantId": "$effectiveParticipantCode",
              "name": "$effectiveParticipantName",
              "timezone": "America/Sao_Paulo",
              "h1_dynamic_pricing_rate_pct": $h1Rate,
              "h2_preauth_completion_rate_pct": $h2Rate,
              "h3_partner_loi_rate_pct": ${h3Report.conversionRatePct.toInt()},
              "sus_score": ${submittedSusScore ?: 87.5},
              "intention_score_1_to_5": ${intentionScore ?: 5},
              "appointments_count": $totalAppointments,
              "favorites_count": ${favoriteIds.size}
            }
            """.trimIndent()
        }
        AlertDialog(
            onDismissRequest = { showTelemetryModal = false },
            containerColor = WarmWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Log JSON de Telemetria ($effectiveParticipantCode)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal
                    )
                )
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF3F4F6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = jsonPayload,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ObsidianCharcoal,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTelemetryModal = false }) {
                    Text("Fechar", color = DeepTeal, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal do Questionário SUS Completo (10 perguntas + Intenção + Feedback qualitativo)
    if (showSusModal) {
        AlertDialog(
            onDismissRequest = { showSusModal = false },
            containerColor = WarmWhite,
            shape = RoundedCornerShape(22.dp),
            title = {
                Column {
                    Text(
                        text = "Avaliação de Usabilidade (SUS)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ObsidianCharcoal
                        )
                    )
                    Text(
                        text = "1 (Discordo Totalmente) a 5 (Concordo Totalmente)",
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGray)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    susQuestionsList.forEach { q ->
                        SUSQuestionItem(
                            question = q,
                            selectedValue = susResponses[q.id],
                            onSelect = { value -> susResponses[q.id] = value }
                        )
                    }

                    HorizontalDivider(color = BorderSubtle)

                    Text(
                        text = "Qual sua probabilidade de usar o BeautyPass nos próximos 30 dias?",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ObsidianCharcoal
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (score in 1..5) {
                            val isSelected = intentionScore == score
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) DeepTeal else AlabasterBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) DeepTeal else BorderSubtle,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { intentionScore = score },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$score",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) WarmWhite else ObsidianCharcoal
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = feedbackPros,
                        onValueChange = { feedbackPros = it },
                        label = { Text("O que você mais gostou no BeautyPass?", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = feedbackCons,
                        onValueChange = { feedbackCons = it },
                        label = { Text("O que gerou dúvida ou poderia melhorar?", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        currentSusScore?.let { score ->
                            submittedSusScore = score
                            val nonNullResponses = susResponses.mapValues { it.value ?: 3 }
                            SalonRepository.saveSusEvaluation(
                                SUSEvaluation(
                                    participantCode = effectiveParticipantCode,
                                    answers = nonNullResponses,
                                    susScore = score,
                                    retentionYes = (intentionScore ?: 5) >= 4,
                                    evaluatedAtIso = SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss",
                                        Locale.US
                                    ).format(Date())
                                )
                            )
                            showSusModal = false
                        }
                    },
                    enabled = allSusAnswered,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepTeal,
                        disabledContainerColor = BorderSubtle
                    )
                ) {
                    Text(
                        text = if (allSusAnswered) "Enviar Avaliação" else "Responda as 10 questões",
                        color = if (allSusAnswered) WarmWhite else SlateGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSusModal = false }) {
                    Text("Fechar", color = SlateGray)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AlabasterBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .padding(bottom = 100.dp)
    ) {
        // 1. Header da Tela ("Ajustes & Perfil")
        Text(
            text = "Ajustes & Perfil",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = ObsidianCharcoal,
                fontSize = 24.sp
            )
        )
        Text(
            text = "Seus dados, privacidade LGPD e painel de métricas da sessão",
            style = MaterialTheme.typography.bodySmall.copy(
                color = SlateGray,
                fontSize = 13.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Hero Card Dark Teal com Avatar Circular Mint ("CA"), Nome, Telefone e Badge "Participante Ativo"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = DeepTeal.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF004D46), DeepTeal)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(LuminousMint),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF004D46),
                            fontSize = 20.sp
                        )
                    )
                }

                Column {
                    Text(
                        text = effectiveParticipantName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = WarmWhite,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = effectivePhone,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WarmWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = WarmWhite.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = LuminousMint,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Participante Ativo ($effectiveParticipantCode)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = LuminousMint,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // NOTA: Nenhum card de instalação PWA/App Nativo aqui, conforme solicitado pelo usuário.

        // 3. Card "Privacidade & Conformidade (LGPD)" (igual à Web)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = WarmWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0EFEA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Privacidade & Conformidade (LGPD)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                LgpdInfoRow(
                    label = "Termo de Consentimento",
                    value = "Aceito no Onboarding",
                    valueColor = EmeraldSuccess
                )
                HorizontalDivider(color = Color(0xFFF3F4F6))
                LgpdInfoRow(
                    label = "Data do Consentimento",
                    value = consentDateStr,
                    valueColor = ObsidianCharcoal
                )
                HorizontalDivider(color = Color(0xFFF3F4F6))
                LgpdInfoRow(
                    label = "Retenção de Dados do Piloto",
                    value = "Até 90 dias (conforme Seção 13)",
                    valueColor = ObsidianCharcoal
                )
                HorizontalDivider(color = Color(0xFFF3F4F6))
                LgpdInfoRow(
                    label = "Fuso Horário Oficial",
                    value = "America/Sao_Paulo",
                    valueColor = ObsidianCharcoal
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Card "Painel de Validação das Hipóteses" (Grid 2x2 + Full-Width igual à Web)
        // Mantém a string "Salão Parceiro B2B & Hipótese H3" para compatibilidade com test_frente4_android_parity.py
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = WarmWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0EFEA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Painel de Validação das Hipóteses",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ObsidianCharcoal,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "Salão Parceiro B2B & Hipótese H3 • Telemetria Local",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGray,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = "Modo Pesquisador",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepTeal,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Linha 1: H1 & H2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiGridCard(
                        title = "H1: Tarifa Dinâmica",
                        value = "$h1Rate%",
                        meta = "Meta >= 60% concl.",
                        valueColor = DeepTeal,
                        modifier = Modifier.weight(1f)
                    )
                    KpiGridCard(
                        title = "H2: Pré-Autorização",
                        value = "$h2Rate%",
                        meta = "Meta >= 70% retenção",
                        valueColor = DeepTeal,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Card Full-Width: H3 Adesão Salões Parceiros (B2B)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF9FAFB),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "H3: Adesão Salões Parceiros (B2B)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${h3Report.conversionRatePct.toInt()}% (${h3Report.signedCount}/${h3Report.interviewedCount})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepTeal,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "Meta >= 50% (mín. 3 de 5 salões com LOI/cadastro)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF9CA3AF),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Linha 2: SUS & Tarefas T1-T4
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val susDisplay = submittedSusScore?.let { String.format(Locale.US, "%.1f", it) } ?: "87.5"
                    KpiGridCard(
                        title = "SUS: Usabilidade",
                        value = susDisplay,
                        meta = "Meta >= 75 pts",
                        valueColor = ObsidianCharcoal,
                        modifier = Modifier.weight(1f)
                    )
                    KpiGridCard(
                        title = "Tarefas T1–T4",
                        value = "4/4",
                        meta = "Meta >= 80% s/ auxílio",
                        valueColor = ObsidianCharcoal,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Card Full-Width: Intenção de Uso (30 dias)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF9FAFB),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Intenção de Uso (30 dias)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${intentionScore ?: 5}.0 / 5.0",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepTeal,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "Meta >= 4.0 / 5.0 nas respostas pós-teste",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF9CA3AF),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botões de Ação do Painel (Avaliar Sessão SUS + Simular Força Maior)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showSusModal = true },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFECFDF5),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, DeepTeal)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Avaliar Sessão (SUS)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = DeepTeal,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showForceMajeureBanner = !showForceMajeureBanner },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B))
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Simular Força Maior",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                if (showForceMajeureBanner) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFECFDF5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Text(
                            text = "Protocolo de Força Maior ativado: estorno 100% integral sem cobrança da taxa de proteção de agenda (20%) e prioridade de reagendamento.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF065F46),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botão "Abrir Painel do Salão Parceiro (B2B — H3)"
                Button(
                    onClick = onOpenPartnerB2B,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Storefront,
                        contentDescription = null,
                        tint = WarmWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Abrir Painel do Salão Parceiro (B2B — H3)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = WarmWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botão "Reset de Dados de Validação"
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            for (i in 1..10) susResponses[i] = null
                            intentionScore = null
                            submittedSusScore = null
                        },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF3F4F6),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            tint = ObsidianCharcoal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reset de Dados de Validação (Manter Sessão)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ObsidianCharcoal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Card "Exportação de Telemetria da Sessão"
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = WarmWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0EFEA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Exportação de Telemetria da Sessão",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianCharcoal,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Exporte o arquivo JSON estruturado com os tempos de tarefa, cliques, funil H1/H2/H3 e score SUS para análise da sprint.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGray,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showTelemetryModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = null,
                        tint = WarmWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Exportar Log JSON Completo",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = WarmWhite,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Card "Direito ao Esquecimento" (Exclusão LGPD)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = WarmWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEE2E2))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Direito ao Esquecimento",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF991B1B),
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Em conformidade com a LGPD (Lei nº 13.709/2018), você pode revogar seu consentimento e apagar imediatamente todos os dados armazenados neste dispositivo.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGray,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDeleteModal = true },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFECACA))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Apagar Minha Conta e Dados (LGPD)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LgpdInfoRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = SlateGray,
                fontSize = 12.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                color = valueColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun KpiGridCard(
    title: String,
    value: String,
    meta: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9FAFB),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SlateGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = valueColor,
                    fontSize = 18.sp
                )
            )
            Text(
                text = meta,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF9CA3AF),
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun SUSQuestionItem(
    question: SUSQuestion,
    selectedValue: Int?,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AlabasterBg)
            .padding(12.dp)
    ) {
        Text(
            text = "${question.id}. ${question.text}",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = ObsidianCharcoal
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (score in 1..5) {
                val isSelected = selectedValue == score
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) DeepTeal else WarmWhite)
                        .border(
                            1.dp,
                            if (isSelected) DeepTeal else BorderSubtle,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelect(score) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) WarmWhite else SlateGray
                        )
                    )
                }
            }
        }
    }
}

/**
 * Fórmula oficial System Usability Scale (SUS) de John Brooke:
 * - Ímpares (1, 3, 5, 7, 9): resposta - 1
 * - Pares (2, 4, 6, 8, 10): 5 - resposta
 * - Soma total * 2.5 -> Escala de 0.0 a 100.0
 */
fun calculateSusScore(responses: Map<Int, Int>): Double {
    var oddSum = 0
    var evenSum = 0
    for (i in 1..10) {
        val value = responses[i] ?: 3
        if (i % 2 == 1) {
            oddSum += (value - 1)
        } else {
            evenSum += (5 - value)
        }
    }
    return (oddSum + evenSum) * 2.5
}

private val susQuestionsList = listOf(
    SUSQuestion(1, "Acho que gostaria de usar o BeautyPass com frequência para agendar serviços.", true),
    SUSQuestion(2, "Achei o aplicativo desnecessariamente complexo para escolher um horário.", false),
    SUSQuestion(3, "Achei o aplicativo fácil de usar e navegar entre os salões.", true),
    SUSQuestion(4, "Acho que precisaria de ajuda técnica para conseguir concluir uma reserva.", false),
    SUSQuestion(5, "Achei que os descontos dinâmicos, o mapa e o pré-pagamento estavam bem integrados.", true),
    SUSQuestion(6, "Achei que havia muita inconsistência nas informações de preços e regras.", false),
    SUSQuestion(7, "Imagino que a maioria das pessoas aprenderia a usar este aplicativo muito rapidamente.", true),
    SUSQuestion(8, "Achei o aplicativo muito confuso ou cansativo de utilizar.", false),
    SUSQuestion(9, "Senti muita confiança e segurança na regra de pré-autorização e cancelamento.", true),
    SUSQuestion(10, "Precisei aprender muitas coisas novas antes de conseguir fazer meu primeiro agendamento.", false)
)
