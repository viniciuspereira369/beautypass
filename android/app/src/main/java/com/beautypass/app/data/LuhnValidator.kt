package com.beautypass.app.data

// =====================================================================
// MOTOR DE VALIDAÇÃO DE CARTÕES — ALGORITMO DE LUHN (MÓDULO 10)
// Conforme especificação em documenta_o_t_cnica_prot_tipo_de_valida_o.md
// =====================================================================

object LuhnValidator {

    /**
     * Valida um número de cartão de crédito de acordo com a fórmula canônica de Luhn.
     *
     * Regras:
     * 1. Remove quaisquer caracteres não numéricos (espaços, hífens, etc.).
     * 2. Comprimento obrigatório: entre 13 e 19 dígitos inclusive.
     * 3. Percorre os dígitos da direita para a esquerda.
     * 4. Dobra os dígitos alternados iniciando pelo segundo dígito a partir da direita.
     * 5. Se o resultado da multiplicação for maior que 9, subtrai 9 (equivalente à soma dos dígitos).
     * 6. Soma todos os valores processados.
     * 7. O número é válido se e somente se a soma total for múltipla de 10 (sum % 10 == 0).
     */
    fun isValid(cardNumber: String): Boolean {
        val cleanNum = cardNumber.filter { it.isDigit() }
        if (cleanNum.length !in 13..19) {
            return false
        }

        var sum = 0
        var alternate = false

        for (i in cleanNum.length - 1 downTo 0) {
            val charDigit = cleanNum[i]
            var n = charDigit.digitToIntOrNull() ?: return false

            if (alternate) {
                n *= 2
                if (n > 9) {
                    n -= 9
                }
            }

            sum += n
            alternate = !alternate
        }

        return sum > 0 && sum % 10 == 0
    }
}
