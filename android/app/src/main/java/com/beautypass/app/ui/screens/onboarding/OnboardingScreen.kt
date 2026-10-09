package com.beautypass.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.theme.*

/**
 * Tela 1: Onboarding & Autenticação Reativa de Participante (LGPD).
 *
 * Implementado conforme especificações de:
 * - ORIGINAL_REQUEST.md (R1.1)
 * - documenta_o_t_cnica_prot_tipo_de_valida_o.md (Seção 4 - Termos LGPD e Autenticação)
 * - Validação estrita: Nome >= 2 chars, Telefone >= 8 chars, Código participante, Código 0000 e aceite LGPD.
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Ícone Hero de Marca Serene Mint
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(MintSurface, CircleShape)
                    .border(1.5.dp, MintLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "BeautyPass Logo",
                    tint = SereneTeal,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BeautyPass",
                style = MaterialTheme.typography.headlineLarge,
                color = OceanicCharcoal,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Beleza sob demanda com precificação inteligente",
                fontSize = 13.sp,
                color = NeutralMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badge de Protótipo Científico de Validação
            Box(
                modifier = Modifier
                    .background(MintSurface, CapsuleShape)
                    .border(1.dp, OutlineVariant, CapsuleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Sessão de Teste de Usabilidade",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SereneTeal
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Card do Formulário
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Identificação do Participante",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanicCharcoal
                    )

                    // Campo Código do Participante
                    OutlinedTextField(
                        value = participantCode,
                        onValueChange = { participantCode = it },
                        label = { Text("Código do Participante") },
                        leadingIcon = {
                            Icon(Icons.Default.Security, contentDescription = null, tint = SereneTeal)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SereneTeal,
                            unfocusedBorderColor = OutlineVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Campo Nome
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Seu Nome Completo") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SereneTeal)
                        },
                        supportingText = {
                            if (name.isNotEmpty() && !isNameValid) {
                                Text("Mínimo de 2 caracteres", color = ErrorRed, fontSize = 11.sp)
                            }
                        },
                        isError = name.isNotEmpty() && !isNameValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SereneTeal,
                            unfocusedBorderColor = OutlineVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Campo Telefone / WhatsApp
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone / WhatsApp") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = SereneTeal)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        supportingText = {
                            if (phone.isNotEmpty() && !isPhoneValid) {
                                Text("Informe pelo menos 8 dígitos", color = ErrorRed, fontSize = 11.sp)
                            }
                        },
                        isError = phone.isNotEmpty() && !isPhoneValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SereneTeal,
                            unfocusedBorderColor = OutlineVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Campo Código de Acesso Fixo (0000)
                    OutlinedTextField(
                        value = accessCode,
                        onValueChange = { accessCode = it },
                        label = { Text("Código de Acesso do Teste (0000)") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SereneTeal)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        supportingText = {
                            if (accessCode.isNotEmpty() && !isAccessCodeValid) {
                                Text("Código incorreto. Digite 0000.", color = ErrorRed, fontSize = 11.sp)
                            } else {
                                Text("Código do protocolo: 0000", color = NeutralMuted, fontSize = 11.sp)
                            }
                        },
                        isError = accessCode.isNotEmpty() && !isAccessCodeValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SereneTeal,
                            unfocusedBorderColor = OutlineVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Divider(color = OutlineVariant, thickness = 0.8.dp)

                    // Checkbox LGPD Obrigatório com Texto Verbatim
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { termsAccepted = !termsAccepted },
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SereneTeal,
                                uncheckedColor = NeutralMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Este é um protótipo em teste. Coletamos seu nome, telefone e registros de uso das telas apenas para avaliar o aplicativo, sem qualquer finalidade comercial. Os dados serão apagados até 90 dias após o teste. Você pode solicitar a exclusão imediata a qualquer momento.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = OceanicCharcoal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Avançar / Entrar no Aplicativo
            Button(
                onClick = {
                    if (isFormValid) {
                        onOnboardingComplete(name.trim(), phone.trim(), participantCode.trim())
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SereneTeal,
                    disabledContainerColor = Slate400.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "Avançar para o Aplicativo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
