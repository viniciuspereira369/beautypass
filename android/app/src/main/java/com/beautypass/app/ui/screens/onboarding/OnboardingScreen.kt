package com.beautypass.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.R
import com.beautypass.app.data.SalonRepository
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.bounceClick

/**
 * Tela 0: Onboarding & Termos de Teste LGPD — Espelhada 1:1 com #screen-onboarding (beautypass_app.html):
 * - Hero com o novo ícone HD full-bleed (silhueta feminina branca sobre gradiente #00685F -> #0D9488 -> #14B8A6)
 * - Card de Termo de Teste e Privacidade (LGPD) no topo com checkbox de aceite
 * - Formulário na ordem exata: Nome Completo, Telefone Celular, Código do Participante, Código de Acesso (0000)
 * - Botão "Iniciar Sessão de Teste".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    initialParticipantCode: String = "P01",
    onOnboardingComplete: (name: String, phone: String, participantCode: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var participantCode by remember { mutableStateOf(initialParticipantCode) }
    var accessCode by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    val isNameValid = name.trim().length >= 2
    val isPhoneValid = phone.filter { it.isDigit() }.length >= 8 || phone.trim().length >= 8
    val isParticipantValid = participantCode.trim().isNotEmpty()
    val isAccessCodeValid = accessCode.trim() == "0000"
    val isFormValid = isNameValid && isPhoneValid && isParticipantValid && isAccessCodeValid && termsAccepted

    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = CanvasBase
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Onboarding Hero (.onboarding-hero) com Ícone HD Full-Bleed
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(6.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, SurfaceWhite, RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_beautypass_hd_icon),
                    contentDescription = "BeautyPass",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BeautyPass",
                fontSize = 23.sp,
                color = OceanicCharcoal,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Protótipo de Validação de Serviços de Beleza",
                fontSize = 13.sp,
                color = NeutralMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Card de Termos LGPD da Seção 13 (.lgpd-card)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MintSurface)
                    .border(1.dp, MintPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = SereneTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Termo de Teste e Privacidade (LGPD)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SereneTeal
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "\"Este é um protótipo em teste. Coletamos seu nome, telefone e registros de uso das telas apenas para avaliar o aplicativo, sem qualquer finalidade comercial. Os dados serão apagados até 90 dias após o teste. Você pode solicitar a exclusão imediata a qualquer momento.\"",
                    fontSize = 11.5.sp,
                    lineHeight = 16.5.sp,
                    color = OceanicCharcoal
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                        .clickable { termsAccepted = !termsAccepted }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = SereneTeal,
                            uncheckedColor = NeutralMuted
                        ),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Li e concordo em participar do teste do protótipo com registro de métricas de navegação.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 15.sp,
                        color = OceanicCharcoal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Formulário de Login Simulado (.onboarding-form)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Nome Completo
                    Column {
                        Text(
                            text = "Nome Completo",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Digite seu nome", fontSize = 13.sp, color = NeutralMuted) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SereneTeal,
                                unfocusedBorderColor = OutlineVariant,
                                focusedContainerColor = CanvasBase,
                                unfocusedContainerColor = CanvasBase
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Telefone Celular
                    Column {
                        Text(
                            text = "Telefone Celular",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            placeholder = { Text("(11) 90000-0000", fontSize = 13.sp, color = NeutralMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SereneTeal,
                                unfocusedBorderColor = OutlineVariant,
                                focusedContainerColor = CanvasBase,
                                unfocusedContainerColor = CanvasBase
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Código do Participante
                    Column {
                        Text(
                            text = "Código do Participante",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = participantCode,
                            onValueChange = { participantCode = it.uppercase().take(8) },
                            placeholder = { Text("Ex.: P01", fontSize = 13.sp, color = NeutralMuted) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SereneTeal,
                                unfocusedBorderColor = OutlineVariant,
                                focusedContainerColor = CanvasBase,
                                unfocusedContainerColor = CanvasBase
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SereneTeal,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Informado pelo moderador da sessão",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeutralMuted
                            )
                        }
                    }

                    // Código de Acesso do Teste (0000)
                    Column {
                        Text(
                            text = "Código de Acesso do Teste",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanicCharcoal,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = accessCode,
                            onValueChange = { accessCode = it.take(4) },
                            placeholder = { Text("0000", fontSize = 13.sp, color = NeutralMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SereneTeal,
                                unfocusedBorderColor = OutlineVariant,
                                focusedContainerColor = CanvasBase,
                                unfocusedContainerColor = CanvasBase
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SereneTeal,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Código fixo de teste: 0000",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeutralMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Botão "Iniciar Sessão de Teste"
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .bounceClick(
                                scaleDown = 0.96f,
                                enabled = isFormValid,
                                onClick = {
                                    if (isFormValid) {
                                        SalonRepository.saveUserProfile(
                                            participantId = participantCode.trim(),
                                            name = name.trim(),
                                            phone = phone.trim(),
                                            lgpdAccepted = true
                                        )
                                        onOnboardingComplete(name.trim(), phone.trim(), participantCode.trim())
                                    }
                                }
                            ),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isFormValid) SereneTeal else Slate400.copy(alpha = 0.45f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Iniciar Sessão de Teste",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
