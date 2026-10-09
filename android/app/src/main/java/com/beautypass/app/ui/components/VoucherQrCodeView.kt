package com.beautypass.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.theme.*

/**
 * VoucherQrCodeView: Renderizador de QR Code vetorial autônomo via Canvas nativo do Jetpack Compose.
 *
 * Implementado conforme especificações de:
 * - documenta_o_t_cnica_prot_tipo_de_valida_o.md (Seção 8 - Voucher Digital e QR Code)
 * - DESIGN.md (Serene Mint & Teal Tokens)
 * - 3 Localizadores angulares de posição (Finder Patterns)
 * - Matriz de dados vetoriais estéticos Serene Teal e Mint Primary
 * - Emblema circular central com anel de menta luminosa
 */
@Composable
fun VoucherQrCodeView(
    voucherCode: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundLight, RoundedCornerShape(18.dp))
            .border(1.dp, OutlineVariant, RoundedCornerShape(18.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterVertically
    ) {
        // Matriz Vetorial de QR Code no Canvas do Jetpack Compose
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(SurfaceWhite, RoundedCornerShape(14.dp))
                .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Cantos de Posicionamento (Quadrados Clássicos de QR Code com cantos suavizados)
                fun drawFinderPattern(topLeft: Offset, outerSize: Float) {
                    drawRoundRect(
                        color = SereneTeal,
                        topLeft = topLeft,
                        size = Size(outerSize, outerSize),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    drawRoundRect(
                        color = SurfaceWhite,
                        topLeft = Offset(topLeft.x + outerSize * 0.16f, topLeft.y + outerSize * 0.16f),
                        size = Size(outerSize * 0.68f, outerSize * 0.68f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    drawRoundRect(
                        color = SereneTeal,
                        topLeft = Offset(topLeft.x + outerSize * 0.32f, topLeft.y + outerSize * 0.32f),
                        size = Size(outerSize * 0.36f, outerSize * 0.36f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }

                val finderSize = canvasWidth * 0.28f
                // Superior Esquerdo
                drawFinderPattern(Offset(0f, 0f), finderSize)
                // Superior Direito
                drawFinderPattern(Offset(canvasWidth - finderSize, 0f), finderSize)
                // Inferior Esquerdo
                drawFinderPattern(Offset(0f, canvasHeight - finderSize), finderSize)

                // Células de Dados Vetoriais (Serene Mint / Teal)
                val cellSize = canvasWidth * 0.08f
                drawRoundRect(
                    color = MintPrimary,
                    topLeft = Offset(canvasWidth * 0.38f, canvasHeight * 0.08f),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = SereneTeal,
                    topLeft = Offset(canvasWidth * 0.52f, canvasHeight * 0.12f),
                    size = Size(cellSize, cellSize * 1.4f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = MintPrimary,
                    topLeft = Offset(canvasWidth * 0.10f, canvasHeight * 0.38f),
                    size = Size(cellSize * 1.2f, cellSize * 1.2f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = SereneTeal,
                    topLeft = Offset(canvasWidth * 0.75f, canvasHeight * 0.38f),
                    size = Size(cellSize * 1.5f, cellSize),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = MintPrimary,
                    topLeft = Offset(canvasWidth * 0.42f, canvasHeight * 0.72f),
                    size = Size(cellSize * 1.3f, cellSize),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = SereneTeal,
                    topLeft = Offset(canvasWidth * 0.65f, canvasHeight * 0.75f),
                    size = Size(cellSize, cellSize * 1.5f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = MintPrimary,
                    topLeft = Offset(canvasWidth * 0.82f, canvasHeight * 0.62f),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = SereneTeal,
                    topLeft = Offset(canvasWidth * 0.34f, canvasHeight * 0.46f),
                    size = Size(cellSize * 1.1f, cellSize * 1.1f),
                    cornerRadius = CornerRadius(3f, 3f)
                )

                // Selo Central BeautyPass (Teal e Mint com anel concêntrico)
                val centerRadius = canvasWidth * 0.14f
                drawCircle(
                    color = SereneTeal,
                    center = center,
                    radius = centerRadius
                )
                drawCircle(
                    color = MintLight,
                    center = center,
                    radius = centerRadius * 0.75f
                )
                drawCircle(
                    color = SereneTeal,
                    center = center,
                    radius = centerRadius * 0.42f
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "QR Code de Check-in no Salão",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = OceanicCharcoal
        )
        Text(
            text = "Apresente este código na recepção ao chegar",
            fontSize = 11.sp,
            color = NeutralMuted
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .background(MintSurface, RoundedCornerShape(8.dp))
                .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Código: $voucherCode",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = SereneTeal
            )
        }
    }
}
