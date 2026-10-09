package com.beautypass.app

import com.beautypass.app.data.PricingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Suíte de Testes Unitários para o Motor de Precificação Dinâmica (PricingEngine).
 *
 * Valida rigorosamente:
 * 1. Cálculo de descontos variáveis entre 5% e 40%.
 * 2. Respeito absoluto ao piso de preço mínimo operacional (R$ 25,00 padrão e R$ 30,00/45% on-demand).
 * 3. Geração correta de badges ("Horário Econômico", "Última Hora", "Padrão").
 * 4. Subtextos mandatórios da Seção 6.2 ("preço menor em horário de menor procura", "desconto para hoje").
 * 5. Congelamento imutável de snapshot (freezeSnapshot).
 */
class PricingEngineTest {

    // =========================================================================
    // 1. CÁLCULO DE DESCONTOS ENTRE 5% E 40%
    // =========================================================================

    @Test
    fun testDiscountsBetween5And40PercentOn100Base() {
        val basePrice = 100.00

        val discounts = listOf(5, 10, 15, 20, 25, 30, 35, 40)
        val expectedPrices = listOf(95.00, 90.00, 85.00, 80.00, 75.00, 70.00, 65.00, 60.00)

        for (i in discounts.indices) {
            val discount = discounts[i]
            val expected = expectedPrices[i]
            val pricing = PricingEngine.calculateSlotPricing(
                basePrice = basePrice,
                discountPct = discount,
                isUrgent = false,
                time = "14:00"
            )

            assertEquals("Preço com $discount% de desconto sobre R$ 100", expected, pricing.finalPrice, 0.001)
            assertEquals("Percentual gravado deve ser $discount%", discount, pricing.discountPct)
            assertTrue("Deve indicar que possui desconto", pricing.hasDiscount)
            assertEquals("Badge deve ser Horário Econômico", PricingEngine.BADGE_ECONOMY, pricing.badgeLabel)
            assertEquals("Subtexto deve ser o canônico de menor procura", PricingEngine.SUBTEXT_ECONOMY, pricing.subtext)
        }
    }

    @Test
    fun testRealServicesDiscounts() {
        // Escova Modeladora (Base R$ 120.00) com 30% de desconto
        val escova = PricingEngine.calculateSlotPricing(
            basePrice = 120.00,
            discountPct = 30,
            isUrgent = false,
            time = "13:30"
        )
        // 120 * 0.70 = 84.00
        assertEquals(84.00, escova.finalPrice, 0.001)
        assertEquals(120.00, escova.basePrice, 0.001)
        assertEquals(30, escova.discountPct)
        assertTrue(escova.hasDiscount)

        // Corte Visagista (Base R$ 140.00) com 25% de desconto
        val corte = PricingEngine.calculateSlotPricing(
            basePrice = 140.00,
            discountPct = 25,
            isUrgent = false,
            time = "15:00"
        )
        // 140 * 0.75 = 105.00
        assertEquals(105.00, corte.finalPrice, 0.001)
        assertEquals(25, corte.discountPct)
        assertTrue(corte.hasDiscount)
    }

    @Test
    fun testZeroDiscountPreservesBasePrice() {
        val standard = PricingEngine.calculateSlotPricing(
            basePrice = 90.00,
            discountPct = 0,
            isUrgent = false,
            time = "11:00"
        )
        assertEquals(90.00, standard.finalPrice, 0.001)
        assertEquals(0, standard.discountPct)
        assertFalse(standard.hasDiscount)
        assertEquals(PricingEngine.BADGE_STANDARD, standard.badgeLabel)
        assertEquals(PricingEngine.SUBTEXT_STANDARD, standard.subtext)
    }

    // =========================================================================
    // 2. RESPEITO ABSOLUTO AO PISO OPERACIONAL (R$ 25,00 PADRÃO & 45%/R$ 30 ON-DEMAND)
    // =========================================================================

    @Test
    fun testMinimumPriceFloorClampingStandard() {
        // Serviço de R$ 30,00 com 30% de desconto -> 30 * 0.7 = R$ 21,00 (< R$ 25,00)
        // Deve travar no piso de R$ 25,00
        val clamped30 = PricingEngine.calculateSlotPricing(
            basePrice = 30.00,
            discountPct = 30,
            isUrgent = false,
            time = "16:00"
        )
        assertEquals("Preço com desconto abaixo do piso deve ser travado em R$ 25,00", 25.00, clamped30.finalPrice, 0.001)
        assertTrue("Ainda possui desconto em relação aos R$ 30,00 originais", clamped30.hasDiscount)

        // Serviço de R$ 26,00 com 40% de desconto -> 26 * 0.6 = R$ 15,60 (< R$ 25,00)
        // Deve travar em R$ 25,00
        val clamped26 = PricingEngine.calculateSlotPricing(
            basePrice = 26.00,
            discountPct = 40,
            isUrgent = false,
            time = "17:00"
        )
        assertEquals("Preço deve ser travado no piso de R$ 25,00", 25.00, clamped26.finalPrice, 0.001)

        // Serviço já no piso de R$ 25,00 com 20% de desconto -> 25 * 0.8 = R$ 20,00
        // Deve travar em R$ 25,00 e hasDiscount ser false (pois 25.0 não é menor que 25.0)
        val atFloor = PricingEngine.calculateSlotPricing(
            basePrice = 25.00,
            discountPct = 20,
            isUrgent = false,
            time = "18:00"
        )
        assertEquals(25.00, atFloor.finalPrice, 0.001)
        assertFalse("Não há desconto efetivo pois atingiu o piso igual à base", atFloor.hasDiscount)

        // Serviço de R$ 20,00 com qualquer desconto não pode cair abaixo de R$ 25,00
        val belowFloor = PricingEngine.calculateSlotPricing(
            basePrice = 20.00,
            discountPct = 10,
            isUrgent = false,
            time = "10:00"
        )
        assertEquals(25.00, belowFloor.finalPrice, 0.001)
    }

    @Test
    fun testOnDemandEthicalFloorPricing() {
        // Modo Pedir Agora (On-Demand): piso de R$ 30,00 ou 45% do valor base
        // Caso 1: Serviço de R$ 50,00 com 50% de desconto -> R$ 25,00 calculado.
        // Piso ético: max(30.00, 50 * 0.45 = 22.50) = 30.00.
        // Preço final deve ser R$ 30,00.
        val onDemand50 = PricingEngine.calculateFairOnDemandPricing(basePrice = 50.00, discountPct = 50)
        assertEquals("Piso ético on-demand para serviço de R$ 50 deve ser R$ 30,00", 30.00, onDemand50.finalPrice, 0.001)
        assertTrue(onDemand50.isUrgent)

        // Caso 2: Serviço caro de R$ 200,00 com 60% de desconto -> R$ 80,00 calculado.
        // Piso ético de 45%: max(30.00, 200 * 0.45 = 90.00) = 90.00.
        // Preço final deve ser R$ 90,00.
        val onDemand200 = PricingEngine.calculateFairOnDemandPricing(basePrice = 200.00, discountPct = 60)
        assertEquals("Piso ético de 45% para R$ 200 deve ser R$ 90,00", 90.00, onDemand200.finalPrice, 0.001)

        // Caso 3: Serviço de R$ 100,00 com 30% de desconto -> R$ 70,00 calculado.
        // Piso ético: max(30.00, 45.00) = 45.00.
        // Como 70.00 > 45.00, prevalece o cálculo de 70.00.
        val onDemand100 = PricingEngine.calculateFairOnDemandPricing(basePrice = 100.00, discountPct = 30)
        assertEquals(70.00, onDemand100.finalPrice, 0.001)
    }

    // =========================================================================
    // 3. BADGES GERADAS E SUBTEXTOS MANDATÓRIOS DA SEÇÃO 6.2
    // =========================================================================

    @Test
    fun testBadgeAndSubtextForEconomyDiscount() {
        val slot = PricingEngine.calculateSlotPricing(
            basePrice = 100.00,
            discountPct = 20,
            isUrgent = false,
            time = "14:15"
        )
        assertEquals("Badge para desconto não urgente", "Horário Econômico", slot.badgeLabel)
        assertEquals("Subtexto da Seção 6.2 para horário econômico", "preço menor em horário de menor procura", slot.subtext)
    }

    @Test
    fun testBadgeAndSubtextForUrgentDiscount() {
        val slot = PricingEngine.calculateSlotPricing(
            basePrice = 100.00,
            discountPct = 30,
            isUrgent = true,
            time = "17:45"
        )
        assertEquals("Badge para slot urgente", "Última Hora", slot.badgeLabel)
        assertEquals("Subtexto da Seção 6.2 para última hora", "desconto para hoje", slot.subtext)
        assertTrue("Flag isUrgent deve ser true", slot.isUrgent)
    }

    @Test
    fun testBadgeAndSubtextForStandardFullPrice() {
        val slot = PricingEngine.calculateSlotPricing(
            basePrice = 100.00,
            discountPct = 0,
            isUrgent = false,
            time = "10:00"
        )
        assertEquals("Badge para preço cheio", "Padrão", slot.badgeLabel)
        assertEquals("Subtexto para alta procura", "horário de alta procura (preço integral)", slot.subtext)
        assertFalse(slot.hasDiscount)
    }

    // =========================================================================
    // 4. CONGELAMENTO IMUTÁVEL DE SNAPSHOT (FREEZE SNAPSHOT)
    // =========================================================================

    @Test
    fun testFreezeSnapshotIntegrityAndImmutability() {
        val slotPricing = PricingEngine.calculateSlotPricing(
            basePrice = 150.00,
            discountPct = 20,
            isUrgent = false,
            time = "14:30"
        )

        val salonId = "s1"
        val serviceId = "srv_s1_1"

        val snapshot = PricingEngine.freezeSnapshot(
            slotPricing = slotPricing,
            salonId = salonId,
            serviceId = serviceId
        )

        assertNotNull("Snapshot não pode ser nulo", snapshot)
        assertEquals("salonId deve corresponder", salonId, snapshot.salonId)
        assertEquals("serviceId deve corresponder", serviceId, snapshot.serviceId)
        assertEquals("Horário deve corresponder", "14:30", snapshot.time)
        assertEquals("Preço base preservado", 150.00, snapshot.priceBase, 0.001)
        assertEquals("Preço final preservado (150 * 0.8 = 120)", 120.00, snapshot.priceFinal, 0.001)
        assertEquals("Desconto percentual preservado", 20, snapshot.discountPct)
        assertEquals("Badge preservada", PricingEngine.BADGE_ECONOMY, snapshot.badgeLabel)
        assertEquals("Explicação de preço preservada", PricingEngine.SUBTEXT_ECONOMY, snapshot.priceExplanation)
        assertTrue("Timestamp auditável deve ser maior que zero", snapshot.frozenAtTimestamp > 0)

        // Garantir imutabilidade: re-executar cálculo com outros parâmetros não altera o snapshot existente
        val otherPricing = PricingEngine.calculateSlotPricing(basePrice = 200.00, discountPct = 40)
        assertEquals(120.00, snapshot.priceFinal, 0.001)
        assertEquals(150.00, snapshot.priceBase, 0.001)
    }
}
