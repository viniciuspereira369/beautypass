package com.beautypass.app.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// =====================================================================
// DESIGN SYSTEM OFICIAL BEAUTYPASS — SHAPES & RAIO ERGONÔMICO (M3)
// Conforme especificações de DESIGN.md (Linhas 113-119 e 187-193)
// =====================================================================

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),     // Tags minúsculas de desconto (-20%)
    small = RoundedCornerShape(8.dp),          // Botões compactos, chips, time slot blocks
    medium = RoundedCornerShape(12.dp),        // Caixas de texto, miniaturas de fotos, inputs
    large = RoundedCornerShape(16.dp),         // Cards principais, itinerary cards, modal sheets
    extraLarge = RoundedCornerShape(24.dp)      // Salon cards heróicos, bottom sheet corners
)

// Pílula / Cápsula Completa para Badges Interativos e Search Bar
val CapsuleShape = RoundedCornerShape(9999.dp)

// Formato ergonômico padrão para Bottom Sheets
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
