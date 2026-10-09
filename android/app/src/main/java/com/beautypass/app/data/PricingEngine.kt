package com.beautypass.app.data

import com.beautypass.app.model.PricingSnapshot
import com.beautypass.app.model.SlotPricing
import kotlin.math.max
import kotlin.math.round

// =====================================================================
// MOTOR DE PRECIFICAÇÃO DINÂMICA BEAUTYPASS
// Conforme especificação em documenta_o_t_cnica_prot_tipo_de_valida_o.md
// e Seção 6.2 (Subtextos Literais Obrigatórios de Conformidade)
// =====================================================================

object PricingEngine {

    const val MIN_PRICE_STANDARD: Double = 25.00
    const val MIN_PRICE_ON_DEMAND: Double = 30.00
    const val ETHICAL_FLOOR_PERCENTAGE: Double = 0.45

    // Subtextos literais canônicos para auditoria de validação H1
    const val SUBTEXT_ECONOMY = "preço menor em horário de menor procura"
    const val SUBTEXT_URGENT = "desconto para hoje"
    const val SUBTEXT_STANDARD = "horário de alta procura (preço integral)"
    const val SUBTEXT_OCCUPIED = "slot já preenchido por outro cliente"

    // Badges oficiais
    const val BADGE_ECONOMY = "Horário Econômico"
    const val BADGE_URGENT = "Última Hora"
    const val BADGE_STANDARD = "Padrão"

    /**
     * Calcula o preço dinâmico de um horário com garantia de respeito ao piso operacional:
     * finalPrice = max(minPrice, round(basePrice * (1.0 - discountPct / 100.0) * 100.0) / 100.0)
     */
    fun calculateSlotPricing(
        basePrice: Double,
        discountPct: Int,
        isUrgent: Boolean = false,
        time: String = ""
    ): SlotPricing {
        val minPrice = MIN_PRICE_STANDARD
        val validDiscount = discountPct.coerceIn(0, 100)

        val discountedPrice = if (validDiscount > 0) {
            val raw = basePrice * (1.0 - validDiscount / 100.0)
            round(raw * 100.0) / 100.0
        } else {
            basePrice
        }

        val finalPrice = max(minPrice, discountedPrice)
        val hasDiscount = validDiscount > 0 && finalPrice < basePrice

        val badgeLabel = when {
            isUrgent -> BADGE_URGENT
            hasDiscount -> BADGE_ECONOMY
            else -> BADGE_STANDARD
        }

        val subtext = when {
            isUrgent -> SUBTEXT_URGENT
            hasDiscount -> SUBTEXT_ECONOMY
            else -> SUBTEXT_STANDARD
        }

        return SlotPricing(
            time = time,
            basePrice = basePrice,
            finalPrice = finalPrice,
            discountPct = if (hasDiscount) validDiscount else 0,
            hasDiscount = hasDiscount,
            subtext = subtext,
            badgeLabel = badgeLabel,
            isUrgent = isUrgent,
            snapshotFrozenAt = System.currentTimeMillis()
        )
    }

    /**
     * Sobrecarga conveniente que aceita o horário como primeiro parâmetro.
     */
    fun calculateSlotPricing(
        time: String,
        basePrice: Double,
        discountPct: Int,
        isUrgent: Boolean = false
    ): SlotPricing {
        return calculateSlotPricing(
            basePrice = basePrice,
            discountPct = discountPct,
            isUrgent = isUrgent,
            time = time
        )
    }

    /**
     * Congela um snapshot imutável para checkout e prevenção de alteração de preço pós-reserva.
     */
    fun freezeSnapshot(
        slotPricing: SlotPricing,
        salonId: String,
        serviceId: String
    ): PricingSnapshot {
        return PricingSnapshot(
            salonId = salonId,
            serviceId = serviceId,
            time = slotPricing.time,
            priceBase = slotPricing.basePrice,
            priceFinal = slotPricing.finalPrice,
            discountPct = slotPricing.discountPct,
            badgeLabel = slotPricing.badgeLabel,
            priceExplanation = slotPricing.subtext,
            frozenAtTimestamp = slotPricing.snapshotFrozenAt ?: System.currentTimeMillis()
        )
    }

    /**
     * Cálculo para o modo Encaixe Imediato / Pedir Agora (On-Demand),
     * respeitando o piso ético de R$ 30,00 ou 45% do preço base.
     */
    fun calculateFairOnDemandPricing(
        basePrice: Double,
        discountPct: Int
    ): SlotPricing {
        val ethicalFloor = max(MIN_PRICE_ON_DEMAND, round(basePrice * ETHICAL_FLOOR_PERCENTAGE * 100.0) / 100.0)
        val calculated = round(basePrice * (1.0 - discountPct.coerceIn(0, 100) / 100.0) * 100.0) / 100.0
        val finalPrice = max(ethicalFloor, calculated)
        val hasDiscount = finalPrice < basePrice

        return SlotPricing(
            time = "Agora",
            basePrice = basePrice,
            finalPrice = finalPrice,
            discountPct = if (hasDiscount) discountPct else 0,
            hasDiscount = hasDiscount,
            subtext = "tarifa justa on-demand (encaixe imediato)",
            badgeLabel = BADGE_ECONOMY,
            isUrgent = true,
            snapshotFrozenAt = System.currentTimeMillis()
        )
    }
}
