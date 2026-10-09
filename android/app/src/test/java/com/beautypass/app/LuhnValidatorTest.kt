package com.beautypass.app

import com.beautypass.app.data.LuhnValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Suíte de Testes Unitários para o Algoritmo de Luhn (Módulo 10).
 *
 * Valida rigorosamente:
 * 1. Cartões válidos dos principais emissores (Visa, Mastercard, Amex).
 * 2. Formatações diversas (com espaços, hífens, sem formatação).
 * 3. Cartões inválidos (dígito verificador incorreto, letras, símbolos).
 * 4. Limites de comprimento (13 a 19 dígitos).
 * 5. Integridade do cálculo matemático Módulo 10.
 */
class LuhnValidatorTest {

    // =========================================================================
    // 1. CARTÕES VÁLIDOS (DIVERSAS BANDEIRAS E COMPRIMENTOS)
    // =========================================================================

    @Test
    fun testValidVisaCards() {
        // Visa 16 dígitos canônicos de teste
        assertTrue("Visa canônico 4532... deve ser válido", LuhnValidator.isValid("4532 0151 1283 0366"))
        assertTrue("Visa canônico 4000... deve ser válido", LuhnValidator.isValid("4000 0000 0000 0002"))
        assertTrue("Visa canônico 4111... deve ser válido", LuhnValidator.isValid("4111 1111 1111 1111"))
        assertTrue("Visa canônico 4242... deve ser válido", LuhnValidator.isValid("4242 4242 4242 4242"))

        // Visa 13 dígitos
        assertTrue("Visa 13 dígitos 4929... deve ser válido", LuhnValidator.isValid("4929 0000 0000 6"))
    }

    @Test
    fun testValidMastercardCards() {
        // Mastercard 16 dígitos canônicos de teste
        assertTrue("Mastercard 5555... deve ser válido", LuhnValidator.isValid("5555 5555 5555 4444"))
        assertTrue("Mastercard 5105... deve ser válido", LuhnValidator.isValid("5105 1051 0510 5100"))
        assertTrue("Mastercard 5200... deve ser válido", LuhnValidator.isValid("5200 8282 8282 8210"))
    }

    @Test
    fun testValidAmexCards() {
        // American Express 15 dígitos canônicos de teste
        assertTrue("Amex 15 dígitos 3782... deve ser válido", LuhnValidator.isValid("3782 822463 10005"))
        assertTrue("Amex 15 dígitos 3400... deve ser válido", LuhnValidator.isValid("3400 000000 00009"))
        assertTrue("Amex 15 dígitos 3714... deve ser válido", LuhnValidator.isValid("3714 496353 98431"))
    }

    @Test
    fun testValidCardsWithFormattingSpacesAndDashes() {
        val rawNumber = "4532015112830366"
        assertTrue("Número puro sem espaços deve ser válido", LuhnValidator.isValid(rawNumber))
        assertTrue("Número com blocos de 4 espaços deve ser válido", LuhnValidator.isValid("4532 0151 1283 0366"))
        assertTrue("Número com hífens deve ser válido", LuhnValidator.isValid("4532-0151-1283-0366"))
        assertTrue("Número com espaçamentos irregulares deve ser válido", LuhnValidator.isValid("  4532  0151   1283 0366  "))
        assertTrue("Amex formatado 4-6-5 deve ser válido", LuhnValidator.isValid("3782-822463-10005"))
    }

    // =========================================================================
    // 2. CARTÕES INVÁLIDOS (DÍGITO VERIFICADOR INCORRETO)
    // =========================================================================

    @Test
    fun testInvalidChecksumAltered() {
        // Alteração no último dígito (dígito de verificação)
        assertFalse("4532 0151 1283 0367 (dígito final alterado) deve ser inválido", LuhnValidator.isValid("4532 0151 1283 0367"))
        assertFalse("4111 1111 1111 1112 (dígito final alterado) deve ser inválido", LuhnValidator.isValid("4111 1111 1111 1112"))
        assertFalse("4000 0000 0000 0003 (dígito final alterado) deve ser inválido", LuhnValidator.isValid("4000 0000 0000 0003"))
        assertFalse("5555 5555 5555 4443 (dígito final alterado) deve ser inválido", LuhnValidator.isValid("5555 5555 5555 4443"))
        assertFalse("3782 822463 10004 (dígito final alterado) deve ser inválido", LuhnValidator.isValid("3782 822463 10004"))

        // Transposição de dígitos vizinhos (falha canônica de digitação detectada por Luhn)
        assertFalse("Transposição de dígitos 01 -> 10 deve falhar", LuhnValidator.isValid("4532 1051 1283 0366"))
    }

    // =========================================================================
    // 3. CARTÕES INVÁLIDOS (CARACTERES NÃO NUMÉRICOS E ENTRADAS CORROMPIDAS)
    // =========================================================================

    @Test
    fun testInvalidCharactersLettersAndAlphanumeric() {
        assertFalse("Número contendo letras insuficientes deve falhar", LuhnValidator.isValid("4532 ABCD 1283 0366"))
        assertFalse("String puramente alfabética deve falhar", LuhnValidator.isValid("CARTAO INVALIDO TESTE"))
        assertFalse("Apenas caracteres especiais deve falhar", LuhnValidator.isValid("!@#$%^&*()_+"))
        assertFalse("String vazia deve falhar", LuhnValidator.isValid(""))
        assertFalse("String apenas de espaços deve falhar", LuhnValidator.isValid("    "))
    }

    // =========================================================================
    // 4. LIMITES DE COMPRIMENTO (13 A 19 DÍGITOS)
    // =========================================================================

    @Test
    fun testInvalidLengthsTooShortAndTooLong() {
        // Muito curto (< 13 dígitos)
        assertFalse("12 dígitos deve ser rejeitado", LuhnValidator.isValid("123456789012"))
        assertFalse("8 dígitos deve ser rejeitado", LuhnValidator.isValid("1234 5678"))
        assertFalse("4 dígitos deve ser rejeitado", LuhnValidator.isValid("4532"))
        assertFalse("1 dígito deve ser rejeitado", LuhnValidator.isValid("4"))

        // Muito longo (> 19 dígitos)
        assertFalse("20 dígitos deve ser rejeitado", LuhnValidator.isValid("12345678901234567890"))
        assertFalse("25 dígitos deve ser rejeitado", LuhnValidator.isValid("1234567890123456789012345"))
    }

    // =========================================================================
    // 5. INTEGRIDADE DO CÁLCULO MÓDULO 10
    // =========================================================================

    @Test
    fun testModulo10CalculationIntegrity() {
        // Sequência de zeros não deve ser aceita (soma total == 0)
        assertFalse("Sequência de 16 zeros deve falhar", LuhnValidator.isValid("0000 0000 0000 0000"))

        // Cartão Amex canônico de 15 dígitos com soma exata múltipla de 10 (soma = 80)
        assertTrue("Cartão de 15 dígitos com soma exata mod 10", LuhnValidator.isValid("3714 496353 98431"))
    }
}
