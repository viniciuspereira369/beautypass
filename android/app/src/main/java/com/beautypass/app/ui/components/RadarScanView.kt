package com.beautypass.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.theme.*
import kotlinx.coroutines.delay

/**
 * RadarScanView: Scanner animado de ociosidade em tempo real.
 *
 * Implementado conforme especificações de:
 * - documenta_o_ui_ux_app_de_mobilidade_urbana.md (Inspiração 2 - Metáfora Uber On-Demand)
 * - DESIGN.md (Serene Mint & Teal Tokens)
 * - Varredura cônica contínua de 1.8s com blips pulsantes verdes simulando cadeiras vazias.
 */
@Composable
fun RadarScanView(
    isScanning: Boolean = true,
    onScanFinished: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radarTransition")

    // Rotação cônica contínua de 1.8s (1800ms)
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    // Pulso do halo do usuário
    val userHaloScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "userHaloScale"
    )

    // Pulso dos blips verdes detectados
    val blipPulse by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blipPulse"
    )

    var currentStepText by remember { mutableStateOf("Mapeando cadeiras e profissionais...") }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            currentStepText = "Mapeando cadeiras e profissionais..."
            delay(600L)
            currentStepText = "Calculando ociosidade em tempo real..."
            delay(600L)
            currentStepText = "Calibrando Tarifa Justa e ranqueando 3 melhores opções..."
            delay(600L)
            onScanFinished?.invoke()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterVertically
    ) {
        // Círculo Central do Radar
        Box(
            modifier = Modifier
                .size(170.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SereneTeal.copy(alpha = 0.08f),
                            MintPrimary.copy(alpha = 0.18f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(2.dp, SereneTeal.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f

                // Anéis concêntricos de alcance de radar
                drawCircle(
                    color = SereneTeal.copy(alpha = 0.18f),
                    radius = radius * 0.35f,
                    style = Stroke(width = 1.5f)
                )
                drawCircle(
                    color = SereneTeal.copy(alpha = 0.22f),
                    radius = radius * 0.65f,
                    style = Stroke(width = 1.5f)
                )
                drawCircle(
                    color = SereneTeal.copy(alpha = 0.30f),
                    radius = radius * 0.95f,
                    style = Stroke(width = 2.0f)
                )

                // Eixos cartesianos sutis
                drawLine(
                    color = SereneTeal.copy(alpha = 0.15f),
                    start = Offset(center.x, 0f),
                    end = Offset(center.x, size.height),
                    strokeWidth = 1f
                )
                drawLine(
                    color = SereneTeal.copy(alpha = 0.15f),
                    start = Offset(0f, center.y),
                    end = Offset(size.width, center.y),
                    strokeWidth = 1f
                )

                // Braço de varredura rotativa cônica em degradê suave
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            MintLight.copy(alpha = 0.65f),
                            MintPrimary.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = center
                    ),
                    startAngle = sweepAngle,
                    sweepAngle = 65f,
                    useCenter = true,
                    size = size,
                    topLeft = Offset.Zero
                )

                // Ponto central do usuário com halo pulsante
                drawCircle(
                    color = SereneTeal.copy(alpha = 0.25f),
                    radius = 12.dp.toPx() * userHaloScale,
                    center = center
                )
                drawCircle(
                    color = SereneTeal,
                    radius = 7.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = SurfaceWhite,
                    radius = 2.5.dp.toPx(),
                    center = center
                )

                // Blips verdes pulsantes simulando salões com cadeiras ociosas
                // Blip 1: Ideal
                val blip1 = Offset(size.width * 0.72f, size.height * 0.38f)
                drawCircle(
                    color = SuccessGreen.copy(alpha = 0.3f),
                    radius = 8.dp.toPx() * blipPulse,
                    center = blip1
                )
                drawCircle(
                    color = SuccessGreen,
                    radius = 4.dp.toPx(),
                    center = blip1
                )

                // Blip 2: Mais Próximo
                val blip2 = Offset(size.width * 0.32f, size.height * 0.68f)
                drawCircle(
                    color = SuccessGreen.copy(alpha = 0.3f),
                    radius = 7.dp.toPx() * blipPulse,
                    center = blip2
                )
                drawCircle(
                    color = SuccessGreen,
                    radius = 3.5.dp.toPx(),
                    center = blip2
                )

                // Blip 3: Mais Econômico
                val blip3 = Offset(size.width * 0.28f, size.height * 0.26f)
                drawCircle(
                    color = SuccessGreen.copy(alpha = 0.3f),
                    radius = 7.5.dp.toPx() * blipPulse,
                    center = blip3
                )
                drawCircle(
                    color = SuccessGreen,
                    radius = 3.8.dp.toPx(),
                    center = blip3
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Mensagens de Status de Varredura
        Text(
            text = currentStepText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = OceanicCharcoal
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Analisando estabelecimentos a até 3 km de você em São Paulo",
            fontSize = 12.sp,
            color = NeutralMuted
        )
    }
}
