package com.beautypass.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beautypass.app.theme.MintPrimary
import com.beautypass.app.theme.SereneTeal
import com.beautypass.app.theme.Slate600
import com.beautypass.app.theme.Slate900

@Composable
fun VoucherQrCodeView(
    voucherCode: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAF9), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterVertically
    ) {
        // Matriz Vetorial de QR Code no Canvas do Jetpack Compose
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Cantos de Posicionamento (Quadrados Clássicos de QR Code)
                fun drawFinderPattern(topLeft: Offset, outerSize: Float) {
                    drawRoundRect(
                        color = SereneTeal,
                        topLeft = topLeft,
                        size = Size(outerSize, outerSize),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(topLeft.x + outerSize * 0.15f, topLeft.y + outerSize * 0.15f),
                        size = Size(outerSize * 0.70f, outerSize * 0.70f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    drawRoundRect(
                        color = SereneTeal,
                        topLeft = Offset(topLeft.x + outerSize * 0.30f, topLeft.y + outerSize * 0.30f),
                        size = Size(outerSize * 0.40f, outerSize * 0.40f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                }

                val finderSize = canvasWidth * 0.28f
                // Top-Left
                drawFinderPattern(Offset(0f, 0f), finderSize)
                // Top-Right
                drawFinderPattern(Offset(canvasWidth - finderSize, 0f), finderSize)
                // Bottom-Left
                drawFinderPattern(Offset(0f, canvasHeight - finderSize), finderSize)

                // Células de Dados Vetoriais (Serene Mint / Teal)
                val cellSize = canvasWidth * 0.08f
                drawRoundRect(
                    color = MintPrimary,
                    topLeft = Offset(canvasWidth * 0.38f, canvasHeight * 0.08f),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = SereneTeal,
                    topLeft = Offset(canvasWidth * 0.52f, canvasHeight * 0.12f),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = MintPrimary,
                    topLeft = Offset(canvasWidth * 0.10f, canvasHeight * 0.38f),
                    size = Size(cellSize, cellSize * 1.2f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = SereneTeal,
                    topLeft = Offset(canvasWidth * 0.75f, canvasHeight * 0.40f),
                    size = Size(cellSize * 1.5f, cellSize),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = MintPrimary,
                    topLeft = Offset(canvasWidth * 0.42f, canvasHeight * 0.72f),
                    size = Size(cellSize * 1.2f, cellSize),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = SereneTeal,
                    topLeft = Offset(canvasWidth * 0.65f, canvasHeight * 0.75f),
                    size = Size(cellSize, cellSize * 1.5f),
                    cornerRadius = CornerRadius(2f, 2f)
                )

                // Selo Central BeautyPass
                val centerRadius = canvasWidth * 0.14f
                drawCircle(
                    color = SereneTeal,
                    center = center,
                    radius = centerRadius
                )
                drawCircle(
                    color = Color(0xFF5EEAD4),
                    center = center,
                    radius = centerRadius * 0.75f
                )
                drawCircle(
                    color = SereneTeal,
                    center = center,
                    radius = centerRadius * 0.40f
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "QR Code de Check-in no Salão",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
        Text(
            text = "Apresente este código na recepção ao chegar",
            fontSize = 10.sp,
            color = Slate600
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Código: $voucherCode",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            color = SereneTeal
        )
    }
}
