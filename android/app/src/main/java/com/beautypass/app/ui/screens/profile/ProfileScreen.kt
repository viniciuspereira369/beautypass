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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.model.SUSEvaluation
import com.beautypass.app.model.SUSQuestion
import com.beautypass.app.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

/**
 * Tela 6B: Perfil do Participante, Métricas de Validação H1/H2 e Questionário SUS de 10 Itens.
 *
 * Implementado conforme especificações de:
 * - ORIGINAL_REQUEST.md (R1.6)
 * - test_validation_sprint.py (10 perguntas Likert iniciando vazias, botão desabilitado até 100% preenchido)
 * - PROJECT.md (Fórmula SUS canônica: ((sum(odd - 1)) + (sum(5 - even))) * 2.5)
 * - Modal de exclusão de dados LGPD com reset para o Onboarding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    participantCode: String = "P01",
    participantName: String = "Participante do Teste",
    onResetToOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appointments by SalonRepository.appointments.collectAsState()
    val favoriteIds by SalonRepository.favoriteIds.collectAsState()
    val userProfile by SalonRepository.userProfile.collectAsState()
    val latestSusEvaluation by SalonRepository.latestSusEvaluation.collectAsState()

    val effectiveParticipantCode = remember(userProfile, participantCode) {
        userProfile?.participantId?.takeIf { it.isNotBlank() } ?: participantCode
    }
    val effectiveParticipantName = remember(userProfile, participantName) {
        userProfile?.name?.takeIf { it.isNotBlank() } ?: participantName
    }

    // 10 Perguntas Literais da Escala SUS (Português)
    val susQuestions = remember {
        listOf(
            SUSQuestion(1, "1. Acho que gostaria de usar este aplicativo com frequência.", true),
            SUSQuestion(2, "2. Achei o aplicativo desnecessariamente complexo.", false),
            SUSQuestion(3, "3. Achei o aplicativo fácil e intuitivo de usar.", true),
            SUSQuestion(4, "4. Acho que precisaria do apoio de uma pessoa técnica para usar o app.", false),
            SUSQuestion(5, "5. Achei que as várias funções deste sistema estavam bem integradas.", true),
            SUSQuestion(6, "6. Achei que havia muita inconsistência ou contradições no aplicativo.", false),
            SUSQuestion(7, "7. Imagino que a maioria das pessoas aprenderia a usar este app muito rapidamente.", true),
            SUSQuestion(8, "8. Achei o sistema muito complicado e truncado de usar.", false),
            SUSQuestion(9, "9. Senti-me muito confiante e seguro(a) usando o aplicativo.", true),
            SUSQuestion(10, "10. Precisei aprender muitas coisas novas antes de poder agendar.", false)
        )
    }

    // Estado das respostas: ID da pergunta (1..10) -> resposta (1..5)
    var susAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var retentionResponse by remember { mutableStateOf<Boolean?>(null) } // true: Sim, false: Não
    var savedEvaluation by remember { mutableStateOf<SUSEvaluation?>(null) }
    val activeEvaluation = savedEvaluation ?: latestSusEvaluation
    var showSusModal by remember { mutableStateOf(false) }
    var showDeleteDataModal by remember { mutableStateOf(false) }

    // Métricas H1/H2
    val totalSaved = remember(appointments) {
        appointments.sumOf { appt ->
            val base = appt.pricingSnapshot?.priceBase ?: (appt.finalPrice * 1.25)
            val diff = base - appt.finalPrice
            diff.coerceAtLeast(0.0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meu Perfil & Usabilidade",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de Identificação do Participante
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(MintSurface, CircleShape)
                            .border(1.5.dp, MintLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SereneTeal,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = effectiveParticipantName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OceanicCharcoal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(MintSurface, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = effectiveParticipantCode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SereneTealDark
                                )
                            }
                        }
                        val phoneSubtitle = userProfile?.phone?.takeIf { it.isNotBlank() }
                        Text(
                            text = if (phoneSubtitle != null) "Participante da Sessão • $phoneSubtitle" else "Participante da Sessão de Teste",
                            fontSize = 12.sp,
                            color = NeutralMuted
                        )
                    }
                }
            }

            // Cards de Métricas de Validação H1 e H2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Economia Acumulada
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = EconomyGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Economia Gerada", fontSize = 11.sp, color = NeutralMuted)
                        Text(
                            text = "R$ ${String.format("%.2f", totalSaved)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OceanicCharcoal
                        )
                    }
                }

                // Card 2: Agendamentos
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Reservas Criadas", fontSize = 11.sp, color = NeutralMuted)
                        Text(
                            text = "${appointments.size}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OceanicCharcoal
                        )
                    }
                }

                // Card 3: Favoritos
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = StarAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Favoritos", fontSize = 11.sp, color = NeutralMuted)
                        Text(
                            text = "${favoriteIds.size}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OceanicCharcoal
                        )
                    }
                }
            }

            // Seção: Avaliação de Usabilidade SUS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assignment, contentDescription = null, tint = SereneTeal, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Avaliação de Usabilidade (Escala SUS)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "A escala SUS (System Usability Scale) é o padrão internacional para avaliar a usabilidade e clareza de aplicativos.",
                        fontSize = 12.sp,
                        color = NeutralMuted,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (activeEvaluation != null) {
                        // Resultado SUS Calculado
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MintSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, MintLight, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Pontuação SUS Calculada:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OceanicCharcoal)
                                    Text(
                                        text = "${activeEvaluation.susScore.toInt()} / 100",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SereneTeal
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val classification = when {
                                    activeEvaluation.susScore >= 80.3 -> "Usabilidade Excelente (Grau A)"
                                    activeEvaluation.susScore >= 68.0 -> "Boa Usabilidade (Aprovado no Benchmark)"
                                    else -> "Marginal / Requer Melhorias"
                                }
                                Text(
                                    text = classification,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeEvaluation.susScore >= 68) EconomyGreen else CoralPromoText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Usaria o app novamente na rotina? " + if (activeEvaluation.retentionYes) "Sim" else "Não",
                                    fontSize = 11.sp,
                                    color = NeutralMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Button(
                        onClick = { showSusModal = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SereneTeal)
                    ) {
                        Text(
                            text = if (activeEvaluation == null) "Avaliar Sessão (SUS)" else "Reavaliar Sessão (SUS)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }
                }
            }

            // Seção: LGPD Privacidade & Exclusão de Dados
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Privacidade & Termos LGPD",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Este é um protótipo em teste. Coletamos seu nome, telefone e registros de uso das telas apenas para avaliar o aplicativo, sem qualquer finalidade comercial. Os dados serão apagados até 90 dias após o teste. Você pode solicitar a exclusão imediata a qualquer momento.",
                        fontSize = 11.sp,
                        color = NeutralMuted,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { showDeleteDataModal = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Excluir Meus Dados e Reiniciar Teste",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Modal de Avaliação SUS com as 10 Perguntas
    if (showSusModal) {
        val allAnswered = susQuestions.all { q -> susAnswers.containsKey(q.id) }
        val isSubmitEnabled = allAnswered && retentionResponse != null

        AlertDialog(
            onDismissRequest = { showSusModal = false },
            title = {
                Text(
                    text = "Avaliação de Usabilidade SUS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanicCharcoal
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 480.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Classifique de 1 (Discordo Totalmente) a 5 (Concordo Totalmente):",
                        fontSize = 12.sp,
                        color = NeutralMuted
                    )

                    susQuestions.forEach { question ->
                        val currentScore = susAnswers[question.id]
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CanvasBase, RoundedCornerShape(10.dp))
                                .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = question.questionText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OceanicCharcoal
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 5 Botões Likert (1 a 5) iniciando desmarcados
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                (1..5).forEach { score ->
                                    val isSelected = currentScore == score
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) SereneTeal else SurfaceWhite)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) SereneTeal else OutlineVariant,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                susAnswers = susAnswers + (question.id to score)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = score.toString(),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) SurfaceWhite else OceanicCharcoal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Pergunta Final de Retenção
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MintSurface, RoundedCornerShape(10.dp))
                            .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Você usaria este aplicativo novamente na sua rotina de beleza?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { retentionResponse = true }
                            ) {
                                RadioButton(
                                    selected = retentionResponse == true,
                                    onClick = { retentionResponse = true },
                                    colors = RadioButtonDefaults.colors(selectedColor = SereneTeal)
                                )
                                Text("Sim", fontSize = 12.sp, color = OceanicCharcoal)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { retentionResponse = false }
                            ) {
                                RadioButton(
                                    selected = retentionResponse == false,
                                    onClick = { retentionResponse = false },
                                    colors = RadioButtonDefaults.colors(selectedColor = SereneTeal)
                                )
                                Text("Não", fontSize = 12.sp, color = OceanicCharcoal)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isSubmitEnabled) {
                            // Cálculo Canônico SUS: ((sum(odd - 1)) + (sum(5 - even))) * 2.5
                            var oddSum = 0
                            var evenSum = 0
                            for (q in 1..10) {
                                val ans = susAnswers[q] ?: 3
                                if (q % 2 != 0) {
                                    oddSum += (ans - 1)
                                } else {
                                    evenSum += (5 - ans)
                                }
                            }
                            val finalScore = (oddSum + evenSum) * 2.5
                            val nowIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }.format(Date())

                            val eval = SUSEvaluation(
                                participantCode = effectiveParticipantCode,
                                answers = susAnswers,
                                susScore = finalScore,
                                retentionYes = retentionResponse == true,
                                evaluatedAtIso = nowIso
                            )
                            savedEvaluation = eval
                            SalonRepository.saveSusEvaluation(eval)
                            showSusModal = false
                        }
                    },
                    enabled = isSubmitEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SereneTeal,
                        disabledContainerColor = Slate400.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Concluir Avaliação (SUS)", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSusModal = false }) {
                    Text("Cancelar", color = NeutralMuted)
                }
            },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal de Confirmação de Exclusão de Dados LGPD
    if (showDeleteDataModal) {
        AlertDialog(
            onDismissRequest = { showDeleteDataModal = false },
            title = {
                Text("Confirmar Exclusão de Dados", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ErrorRed)
            },
            text = {
                Text(
                    text = "Em conformidade com a LGPD, todos os registros da sua sessão, agendamentos simulados e favoritos serão permanentemente excluídos do dispositivo.",
                    fontSize = 13.sp,
                    color = OceanicCharcoal
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        SalonRepository.clearAllData()
                        showDeleteDataModal = false
                        onResetToOnboarding()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Excluir Definitivamente", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDataModal = false }) {
                    Text("Cancelar", color = NeutralMuted)
                }
            },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
