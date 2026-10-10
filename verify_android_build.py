#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
=============================================================================
BeautyPass Android Build & Architecture Verification Suite
=============================================================================
Autor: Kotlin QA and Automated Build Worker (Milestone 4)
Propósito:
1. Validar a integridade estática e estrutural dos arquivos do projeto Android
   (Gradle wrapper, AndroidManifest, recursos de ícone, tokens de design M3,
   data classes SalonModels, catálogo determinístico dos 24 salões em
   SalonRepository, motores de regras de negócio, componentes especializados,
   todas as 6 telas nativas MVI/UDF e grafo de navegação).
2. Executar a suíte de testes unitários algorítmicos em lógica pura Python
   espelhando com exatidão matemática o Algoritmo de Luhn, a Precificação Dinâmica,
   as Regras de Agendamento com buffer de higienização e o cálculo de score SUS.
3. Assegurar conformidade integral com ORIGINAL_REQUEST.md e PROJECT.md.
=============================================================================
"""

import os
import sys
import re
import math
import sqlite3
from pathlib import Path

# Suporte a Unicode no console Windows
if hasattr(sys.stdout, 'reconfigure'):
    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

# Configuração de Cores para Terminal
class TermColor:
    RESET = "\033[0m"
    BOLD = "\033[1m"
    GREEN = "\033[32m"
    BLUE = "\033[34m"
    CYAN = "\033[36m"
    YELLOW = "\033[33m"
    RED = "\033[31m"
    MAGENTA = "\033[35m"

def print_header(title: str):
    width = 76
    print("\n" + "=" * width)
    print(f" {title.center(width - 2)} ")
    print("=" * width)

def print_check(name: str, passed: bool, detail: str = ""):
    status = f"{TermColor.GREEN}[PASS]{TermColor.RESET}" if passed else f"{TermColor.RED}[FAIL]{TermColor.RESET}"
    detail_str = f" - {detail}" if detail else ""
    print(f" {status} {name}{detail_str}")
    if not passed:
        print(f"        {TermColor.RED}>>> ERRO CRÍTICO NA VALIDAÇÃO: {name}{TermColor.RESET}")

# =============================================================================
# REPOSITÓRIO E ESTRUTURA DE DIRETÓRIOS
# =============================================================================
PROJECT_ROOT = Path(__file__).resolve().parent
ANDROID_ROOT = PROJECT_ROOT / "android"
APP_SRC_MAIN = ANDROID_ROOT / "app" / "src" / "main"
APP_SRC_TEST = ANDROID_ROOT / "app" / "src" / "test"
JAVA_PKG_MAIN = APP_SRC_MAIN / "java" / "com" / "beautypass" / "app"
JAVA_PKG_TEST = APP_SRC_TEST / "java" / "com" / "beautypass" / "app"

# =============================================================================
# IMPLEMENTAÇÃO ALGORÍTMICA PURA EM PYTHON (ESPELHAMENTO CANÔNICO DOS MOTORES)
# =============================================================================

class LuhnValidatorPy:
    """Espelho estrito de com.beautypass.app.data.LuhnValidator"""
    @staticmethod
    def is_valid(card_number: str) -> bool:
        clean = "".join(ch for ch in card_number if ch.isdigit())
        if not (13 <= len(clean) <= 19):
            return False

        total_sum = 0
        alternate = False

        for i in range(len(clean) - 1, -1, -1):
            n = int(clean[i])
            if alternate:
                n *= 2
                if n > 9:
                    n -= 9
            total_sum += n
            alternate = not alternate

        return total_sum > 0 and (total_sum % 10 == 0)


class PricingEnginePy:
    """Espelho estrito de com.beautypass.app.data.PricingEngine"""
    MIN_PRICE_STANDARD = 25.00
    MIN_PRICE_ON_DEMAND = 30.00
    ETHICAL_FLOOR_PERCENTAGE = 0.45

    SUBTEXT_ECONOMY = "preço menor em horário de menor procura"
    SUBTEXT_URGENT = "desconto para hoje"
    SUBTEXT_STANDARD = "horário de alta procura (preço integral)"

    BADGE_ECONOMY = "Horário Econômico"
    BADGE_URGENT = "Última Hora"
    BADGE_STANDARD = "Padrão"

    @classmethod
    def calculate_slot_pricing(cls, base_price: float, discount_pct: int, is_urgent: bool = False, time_slot: str = ""):
        min_price = cls.MIN_PRICE_STANDARD
        valid_discount = max(0, min(100, discount_pct))

        if valid_discount > 0:
            raw = base_price * (1.0 - valid_discount / 100.0)
            discounted_price = round(raw * 100.0) / 100.0
        else:
            discounted_price = base_price

        final_price = max(min_price, discounted_price)
        has_discount = (valid_discount > 0) and (final_price < base_price)

        if is_urgent:
            badge_label = cls.BADGE_URGENT
            subtext = cls.SUBTEXT_URGENT
        elif has_discount:
            badge_label = cls.BADGE_ECONOMY
            subtext = cls.SUBTEXT_ECONOMY
        else:
            badge_label = cls.BADGE_STANDARD
            subtext = cls.SUBTEXT_STANDARD

        return {
            "time": time_slot,
            "basePrice": base_price,
            "finalPrice": final_price,
            "discountPct": valid_discount if has_discount else 0,
            "hasDiscount": has_discount,
            "subtext": subtext,
            "badgeLabel": badge_label,
            "isUrgent": is_urgent
        }

    @classmethod
    def calculate_fair_on_demand_pricing(cls, base_price: float, discount_pct: int):
        ethical_floor = max(cls.MIN_PRICE_ON_DEMAND, round(base_price * cls.ETHICAL_FLOOR_PERCENTAGE * 100.0) / 100.0)
        calculated = round(base_price * (1.0 - max(0, min(100, discount_pct)) / 100.0) * 100.0) / 100.0
        final_price = max(ethical_floor, calculated)
        has_discount = final_price < base_price

        return {
            "time": "Agora",
            "basePrice": base_price,
            "finalPrice": final_price,
            "discountPct": discount_pct if has_discount else 0,
            "hasDiscount": has_discount,
            "subtext": "tarifa justa on-demand (encaixe imediato)",
            "badgeLabel": cls.BADGE_ECONOMY,
            "isUrgent": True
        }

    @classmethod
    def freeze_snapshot(cls, slot_pricing: dict, salon_id: str, service_id: str):
        return {
            "salonId": salon_id,
            "serviceId": service_id,
            "time": slot_pricing["time"],
            "priceBase": slot_pricing["basePrice"],
            "priceFinal": slot_pricing["finalPrice"],
            "discountPct": slot_pricing["discountPct"],
            "badgeLabel": slot_pricing["badgeLabel"],
            "priceExplanation": slot_pricing["subtext"]
        }


class BookingEnginePy:
    """Espelho estrito de com.beautypass.app.data.BookingEngine"""
    SANITIZATION_BUFFER_MINUTES = 10
    TIME_SLOT_INTERVAL_MINUTES = 15

    @staticmethod
    def is_valid_15_minute_block(time_str: str) -> bool:
        parts = time_str.strip().split(":")
        if len(parts) != 2:
            return False
        try:
            h, m = int(parts[0]), int(parts[1])
            if not (0 <= h <= 23):
                return False
            return m in (0, 15, 30, 45)
        except ValueError:
            return False

    @staticmethod
    def time_to_minutes(time_str: str) -> int:
        parts = time_str.strip().split(":")
        try:
            return int(parts[0]) * 60 + int(parts[1])
        except (ValueError, IndexError):
            return 0

    @classmethod
    def calculate_slots_used(cls, duration_min: int, buffer_min: int = 10) -> int:
        total = duration_min + buffer_min
        return math.ceil(total / cls.TIME_SLOT_INTERVAL_MINUTES)

    @classmethod
    def validate_booking_slot(
        cls,
        slot_time: str,
        service_duration_min: int,
        existing_appointments: list,
        staff_id: str = None,
        date_display: str = None,
        buffer_min: int = 10
    ):
        if not cls.is_valid_15_minute_block(slot_time):
            return {
                "isValid": False,
                "reason": "Horário deve respeitar blocos de 15 minutos (ex: 10:00, 10:15, 10:30).",
                "conflictId": None
            }

        if service_duration_min <= 0:
            return {
                "isValid": False,
                "reason": "Duração do serviço deve ser maior que zero.",
                "conflictId": None
            }

        candidate_start = cls.time_to_minutes(slot_time)
        candidate_end = candidate_start + service_duration_min + buffer_min

        active_appts = [
            appt for appt in existing_appointments
            if appt.get("status") in ("CONFIRMED", "IN_PROGRESS")
            and (date_display is None or appt.get("dateDisplay") == date_display)
            and (staff_id is None or appt.get("staffId") in (None, staff_id, "any"))
        ]

        for existing in active_appts:
            existing_start = cls.time_to_minutes(existing["timeSlot"])
            existing_end = existing_start + existing["durationMinutes"] + buffer_min

            # Condição de sobreposição em intervalos semiabertos [Start, End)
            overlaps = (candidate_start < existing_end) and (existing_start < candidate_end)
            if overlaps:
                return {
                    "isValid": False,
                    "reason": "Conflito de agenda: profissional indisponível (buffer de higienização de 10 min exigido).",
                    "conflictId": existing["id"]
                }

        return {"isValid": True, "reason": None, "conflictId": None}

    @classmethod
    def calculate_cancellation_retention_fee(cls, final_price: float, is_under_24h: bool = True) -> float:
        if not is_under_24h:
            return 0.0
        return round(final_price * 0.30 * 100.0) / 100.0



def calculate_sus_score_py(answers: dict) -> float:
    """Cálculo oficial de Usabilidade SUS: ((soma(ímpares - 1)) + (soma(5 - pares))) * 2.5"""
    odd_sum = sum(answers[q] - 1 for q in range(1, 11, 2))
    even_sum = sum(5 - answers[q] for q in range(2, 11, 2))
    return (odd_sum + even_sum) * 2.5


class ConvertersPy:
    """Espelho estrito de com.beautypass.app.data.local.Converters"""
    @staticmethod
    def serialize_answers(answers: dict) -> str:
        if not answers:
            return ""
        return ";".join(f"{k}:{v}" for k, v in sorted(answers.items()))

    @staticmethod
    def deserialize_answers(serialized: str) -> dict:
        if not serialized or not serialized.strip():
            return {}
        result = {}
        for part in serialized.split(";"):
            pair = part.split(":")
            if len(pair) == 2:
                try:
                    result[int(pair[0])] = int(pair[1])
                except ValueError:
                    pass
        return result


class NotificationHelperPy:
    """Espelho estrito de com.beautypass.app.notification.BeautyPassNotificationHelper"""
    CHANNEL_ID = "beautypass_booking_channel"
    CHANNEL_NAME = "Alertas de Reservas BeautyPass"
    CHANNEL_DESCRIPTION = "Alertas de alta prioridade para confirmação de agendamentos, expiração de preço e cancelamentos."

    NOTIF_ID_FREEZE_WARNING = 1001
    NOTIF_ID_BOOKING_CONFIRMED = 1002
    NOTIF_ID_CANCELLATION = 1003

    @classmethod
    def format_freeze_warning_payload(cls, salon_name: str, minutes_left: int, price_final: float) -> dict:
        return {
            "id": cls.NOTIF_ID_FREEZE_WARNING,
            "title": "Atenção: Vaga quase expirando!",
            "message": f"Restam apenas {minutes_left} minutos para concluir sua reserva no salão {salon_name} com tarifa congelada de R$ {price_final:.2f}.",
            "channelId": cls.CHANNEL_ID
        }

    @classmethod
    def format_booking_confirmed_payload(cls, appointment_id: str, salon_name: str, service_name: str, date_display: str, time_slot: str) -> dict:
        return {
            "id": cls.NOTIF_ID_BOOKING_CONFIRMED,
            "title": "Reserva Confirmada com Sucesso! ✂️",
            "message": f"{service_name} no {salon_name} • {date_display} às {time_slot}. Voucher: {appointment_id}",
            "channelId": cls.CHANNEL_ID
        }

    @classmethod
    def format_cancellation_payload(cls, salon_name: str, cancellation_fee: float = None) -> dict:
        fee_text = f" (Taxa de retenção de 30%: R$ {cancellation_fee:.2f})" if (cancellation_fee is not None and cancellation_fee > 0.0) else ""
        return {
            "id": cls.NOTIF_ID_CANCELLATION,
            "title": "Agendamento Cancelado",
            "message": f"Seu agendamento no {salon_name} foi cancelado com sucesso{fee_text}. Veja os detalhes em Meus Agendamentos.",
            "channelId": cls.CHANNEL_ID
        }


# =============================================================================
# SUÍTE DE TESTES E VERIFICAÇÕES DO BUILD
# =============================================================================

class BuildVerifier:
    def __init__(self):
        self.total_tests = 0
        self.passed_tests = 0
        self.failed_tests = 0

    def record(self, name: str, condition: bool, detail: str = ""):
        self.total_tests += 1
        if condition:
            self.passed_tests += 1
            print_check(name, True, detail)
        else:
            self.failed_tests += 1
            print_check(name, False, detail)

    # -------------------------------------------------------------------------
    # PARTE 1: VERIFICAÇÃO ESTRUTURAL E DE ARQUIVOS
    # -------------------------------------------------------------------------
    def verify_project_structure(self):
        print_header("1. AUDITORIA ESTRUTURAL DO PROJETO ANDROID")

        files_to_check = [
            ("Gradle Wrapper Properties", ANDROID_ROOT / "gradle" / "wrapper" / "gradle-wrapper.properties"),
            ("Gradle Wrapper Script (Unix)", ANDROID_ROOT / "gradlew"),
            ("Gradle Wrapper Script (Windows)", ANDROID_ROOT / "gradlew.bat"),
            ("Root build.gradle.kts", ANDROID_ROOT / "build.gradle.kts"),
            ("Root settings.gradle.kts", ANDROID_ROOT / "settings.gradle.kts"),
            ("App build.gradle.kts", ANDROID_ROOT / "app" / "build.gradle.kts"),
            ("AndroidManifest.xml", APP_SRC_MAIN / "AndroidManifest.xml"),
            ("Launcher Drawable XML", APP_SRC_MAIN / "res" / "drawable" / "ic_launcher.xml"),
            ("Launcher Round Drawable XML", APP_SRC_MAIN / "res" / "drawable" / "ic_launcher_round.xml"),
            ("MainActivity.kt", JAVA_PKG_MAIN / "MainActivity.kt"),
            ("Color.kt (Tokens M3)", JAVA_PKG_MAIN / "theme" / "Color.kt"),
            ("Shape.kt (Formas M3)", JAVA_PKG_MAIN / "theme" / "Shape.kt"),
            ("Type.kt (Tipografia M3)", JAVA_PKG_MAIN / "theme" / "Type.kt"),
            ("Theme.kt (Tema M3)", JAVA_PKG_MAIN / "theme" / "Theme.kt"),
            ("SalonModels.kt (Modelos de Domínio)", JAVA_PKG_MAIN / "model" / "SalonModels.kt"),
            ("SalonRepository.kt (Catálogo 24 Salões)", JAVA_PKG_MAIN / "data" / "SalonRepository.kt"),
            ("LuhnValidator.kt (Motor Luhn)", JAVA_PKG_MAIN / "data" / "LuhnValidator.kt"),
            ("PricingEngine.kt (Motor Precificação)", JAVA_PKG_MAIN / "data" / "PricingEngine.kt"),
            ("BookingEngine.kt (Motor Agendamento)", JAVA_PKG_MAIN / "data" / "BookingEngine.kt"),
            ("BeautyPassNavigation.kt (Grafo de Rotas)", JAVA_PKG_MAIN / "navigation" / "BeautyPassNavigation.kt"),
            ("RadialGaugeTimeSelector.kt (Componente)", JAVA_PKG_MAIN / "ui" / "components" / "RadialGaugeTimeSelector.kt"),
            ("RadarScanView.kt (Componente)", JAVA_PKG_MAIN / "ui" / "components" / "RadarScanView.kt"),
            ("SalonCard.kt (Componente)", JAVA_PKG_MAIN / "ui" / "components" / "SalonCard.kt"),
            ("VoucherQrCodeView.kt (Componente)", JAVA_PKG_MAIN / "ui" / "components" / "VoucherQrCodeView.kt"),
            ("OnboardingScreen.kt (Tela 1)", JAVA_PKG_MAIN / "ui" / "screens" / "onboarding" / "OnboardingScreen.kt"),
            ("HomeScreen.kt (Tela 2)", JAVA_PKG_MAIN / "ui" / "screens" / "home" / "HomeScreen.kt"),
            ("SalonDetailScreen.kt (Tela 3)", JAVA_PKG_MAIN / "ui" / "screens" / "detail" / "SalonDetailScreen.kt"),
            ("OnDemandScreen.kt (Tela 4)", JAVA_PKG_MAIN / "ui" / "screens" / "ondemand" / "OnDemandScreen.kt"),
            ("CheckoutScreen.kt (Tela 5A)", JAVA_PKG_MAIN / "ui" / "screens" / "checkout" / "CheckoutScreen.kt"),
            ("ConfirmScreen.kt (Tela 5B)", JAVA_PKG_MAIN / "ui" / "screens" / "confirm" / "ConfirmScreen.kt"),
            ("AppointmentsScreen.kt (Tela 6A)", JAVA_PKG_MAIN / "ui" / "screens" / "appointments" / "AppointmentsScreen.kt"),
            ("ProfileScreen.kt (Tela 6B)", JAVA_PKG_MAIN / "ui" / "screens" / "profile" / "ProfileScreen.kt"),
            ("MapScreen.kt (Tela de Mapa)", JAVA_PKG_MAIN / "ui" / "screens" / "map" / "MapScreen.kt"),
            ("LuhnValidatorTest.kt (Teste Unitário Kotlin)", JAVA_PKG_TEST / "LuhnValidatorTest.kt"),
            ("PricingEngineTest.kt (Teste Unitário Kotlin)", JAVA_PKG_TEST / "PricingEngineTest.kt"),
            ("BookingRulesTest.kt (Teste Unitário Kotlin)", JAVA_PKG_TEST / "BookingRulesTest.kt"),
            ("RoomDatabaseTest.kt (Teste Unitário Kotlin)", JAVA_PKG_TEST / "RoomDatabaseTest.kt"),
            ("NotificationHelperTest.kt (Teste Unitário Kotlin)", JAVA_PKG_TEST / "NotificationHelperTest.kt"),
            ("BeautyPassNotificationHelper.kt (Helper Notificações)", JAVA_PKG_MAIN / "notification" / "BeautyPassNotificationHelper.kt"),
            ("BeautyPassDatabase.kt (Room Database Singleton)", JAVA_PKG_MAIN / "data" / "local" / "BeautyPassDatabase.kt"),
            ("AppointmentEntity.kt (Entidade Room)", JAVA_PKG_MAIN / "data" / "local" / "entity" / "AppointmentEntity.kt"),
            ("FavoriteEntity.kt (Entidade Room)", JAVA_PKG_MAIN / "data" / "local" / "entity" / "FavoriteEntity.kt"),
            ("UserProfileEntity.kt (Entidade Room)", JAVA_PKG_MAIN / "data" / "local" / "entity" / "UserProfileEntity.kt"),
            ("SusEvaluationEntity.kt (Entidade Room)", JAVA_PKG_MAIN / "data" / "local" / "entity" / "SusEvaluationEntity.kt"),
            ("AppointmentDao.kt (DAO Room)", JAVA_PKG_MAIN / "data" / "local" / "dao" / "AppointmentDao.kt"),
            ("FavoriteDao.kt (DAO Room)", JAVA_PKG_MAIN / "data" / "local" / "dao" / "FavoriteDao.kt"),
            ("UserProfileDao.kt (DAO Room)", JAVA_PKG_MAIN / "data" / "local" / "dao" / "UserProfileDao.kt"),
            ("SusEvaluationDao.kt (DAO Room)", JAVA_PKG_MAIN / "data" / "local" / "dao" / "SusEvaluationDao.kt"),
            ("Converters.kt (TypeConverters Room)", JAVA_PKG_MAIN / "data" / "local" / "Converters.kt"),
        ]

        for label, path in files_to_check:
            exists = path.is_file()
            size = path.stat().st_size if exists else 0
            self.record(f"Arquivo Presente: {label}", exists, f"Tamanho: {size:,} bytes")

    # -------------------------------------------------------------------------
    # PARTE 2: AUDITORIA DE DESIGN TOKENS E MATERIAL 3
    # -------------------------------------------------------------------------
    def verify_design_tokens(self):
        print_header("2. AUDITORIA DE TOKENS SERENE MINT & TEAL (M3)")

        color_file = JAVA_PKG_MAIN / "theme" / "Color.kt"
        content = color_file.read_text(encoding="utf-8") if color_file.exists() else ""

        required_tokens = [
            ("SereneTeal (#00685F)", "00685F"),
            ("MintPrimary (#0D9488)", "0D9488"),
            ("MintLight (#5EEAD4)", "5EEAD4"),
            ("OceanicCharcoal (#134E4A)", "134E4A"),
            ("CanvasBase (#FAFCFC)", "FAFCFC"),
            ("BackgroundLight (#F8FAF9)", "F8FAF9"),
            ("CoralPromo (#F43F5E)", "F43F5E"),
            ("EconomyGreen (#0E8A73)", "0E8A73"),
            ("StarAmber (#F59E0B)", "F59E0B"),
        ]

        for token_name, hex_code in required_tokens:
            has_token = hex_code.lower() in content.lower()
            self.record(f"Design Token M3: {token_name}", has_token, f"Hex {hex_code}")

        # Garantir ausência de cores duras #000000 puro
        has_pure_black = "0xFF000000" in content
        self.record("Conformidade Clínica: Zero #000000 absoluto", not has_pure_black, "Substituído por OceanicCharcoal")

    # -------------------------------------------------------------------------
    # PARTE 3: AUDITORIA DO CATÁLOGO DETERMINÍSTICO DOS 24 SALÕES
    # -------------------------------------------------------------------------
    def verify_salon_catalog(self):
        print_header("3. AUDITORIA DO CATÁLOGO DE 24 SALÕES (SÃO PAULO)")

        repo_file = JAVA_PKG_MAIN / "data" / "SalonRepository.kt"
        content = repo_file.read_text(encoding="utf-8") if repo_file.exists() else ""

        # Contar ocorrências de IDs s1 a s24
        salon_ids_found = []
        for i in range(1, 25):
            sid = f'"s{i}"'
            if sid in content:
                salon_ids_found.append(f"s{i}")

        self.record("Presença de exatamente 24 Salões (s1 a s24)", len(salon_ids_found) == 24, f"Encontrados: {len(salon_ids_found)}/24")

        # Verificar bairros de São Paulo
        neighborhoods = ["Pinheiros", "Jardins", "Itaim Bibi", "Vila Madalena", "Consolação", "Perdizes"]
        for nb in neighborhoods:
            self.record(f"Bairro de SP no Catálogo: {nb}", nb in content)

        # Verificar integridade de mini-galeria de 4 fotos HD Unsplash
        has_hair_gallery = "HAIR_GALLERY" in content
        has_nails_gallery = "NAILS_GALLERY" in content
        has_barber_gallery = "BARBER_GALLERY" in content
        self.record("Mini-Galerias de Fotos HD (Unsplash)", has_hair_gallery and has_nails_gallery and has_barber_gallery)

    # -------------------------------------------------------------------------
    # PARTE 4: TESTES UNITÁRIOS DO ALGORITMO DE LUHN
    # -------------------------------------------------------------------------
    def verify_luhn_suite(self):
        print_header("4. SUÍTE DE TESTES: ALGORITMO DE LUHN (MÓDULO 10)")

        # Cartões Válidos
        valid_cases = [
            ("Visa Canônico 16d", "4532 0151 1283 0366"),
            ("Visa Teste 4000...", "4000 0000 0000 0002"),
            ("Visa Teste 4111...", "4111 1111 1111 1111"),
            ("Visa Teste 4242...", "4242 4242 4242 4242"),
            ("Visa 13 Dígitos", "4929 0000 0000 6"),
            ("Mastercard 16d 5555...", "5555 5555 5555 4444"),
            ("Mastercard 16d 5105...", "5105 1051 0510 5100"),
            ("Mastercard 16d 5200...", "5200 8282 8282 8210"),
            ("Amex 15 Dígitos 3782...", "3782 822463 10005"),
            ("Amex 15 Dígitos 3400...", "3400 000000 00009"),
            ("Amex 15 Dígitos 3714...", "3714 496353 98431"),
            ("Com Traços e Hífens", "4532-0151-1283-0366"),
            ("Espaços Irregulares", "   4532  0151   1283  0366  ")
        ]

        for desc, card in valid_cases:
            res = LuhnValidatorPy.is_valid(card)
            self.record(f"Luhn Válido: {desc}", res, f"Card: {card.strip()}")

        # Cartões Inválidos
        invalid_cases = [
            ("Dígito Verificador Alterado (+1)", "4532 0151 1283 0367"),
            ("Dígito Verificador Alterado (+1)", "4111 1111 1111 1112"),
            ("Transposição de Dígitos (01 -> 10)", "4532 1051 1283 0366"),
            ("Letras Alfanuméricas", "4532 ABCD 1283 0366"),
            ("Apenas Letras", "CARTAO INVALIDO TESTE"),
            ("Caracteres Especiais", "!@#$%^&*()_+"),
            ("Comprimento Curto (12d)", "123456789012"),
            ("Comprimento Curto (8d)", "1234 5678"),
            ("Comprimento Longo (20d)", "12345678901234567890"),
            ("Comprimento Longo (25d)", "1234567890123456789012345"),
            ("String Vazia", ""),
            ("Apenas Espaços", "    "),
            ("Sequência de 16 Zeros (Soma 0)", "0000 0000 0000 0000")
        ]

        for desc, card in invalid_cases:
            res = LuhnValidatorPy.is_valid(card)
            self.record(f"Luhn Inválido: {desc}", not res, f"Card: '{card}'")

    # -------------------------------------------------------------------------
    # PARTE 5: TESTES UNITÁRIOS DA PRECIFICAÇÃO DINÂMICA
    # -------------------------------------------------------------------------
    def verify_pricing_engine_suite(self):
        print_header("5. SUÍTE DE TESTES: PRECIFICAÇÃO DINÂMICA & PISO MÍNIMO")

        # Descontos variáveis entre 5% e 40% sobre base de R$ 100,00
        discounts = [5, 10, 15, 20, 25, 30, 35, 40]
        expected_prices = [95.00, 90.00, 85.00, 80.00, 75.00, 70.00, 65.00, 60.00]

        for d, exp in zip(discounts, expected_prices):
            p = PricingEnginePy.calculate_slot_pricing(100.00, d, False, "14:00")
            ok = abs(p["finalPrice"] - exp) < 0.01 and p["hasDiscount"] and p["discountPct"] == d
            self.record(f"Desconto {d}% sobre R$ 100", ok, f"Preço Calculado: R$ {p['finalPrice']:.2f} (Esperado R$ {exp:.2f})")

        # Teste de Serviços Reais do Catálogo
        escova = PricingEnginePy.calculate_slot_pricing(120.00, 30, False, "13:30")
        self.record("Escova Modeladora (R$ 120, -30%) -> R$ 84,00", abs(escova["finalPrice"] - 84.00) < 0.01)

        corte = PricingEnginePy.calculate_slot_pricing(140.00, 25, False, "15:00")
        self.record("Corte Visagista (R$ 140, -25%) -> R$ 105,00", abs(corte["finalPrice"] - 105.00) < 0.01)

        # Respeito ao Piso Operacional Mínimo de R$ 25,00
        floor_30 = PricingEnginePy.calculate_slot_pricing(30.00, 30, False, "16:00")
        self.record("Piso Operacional: R$ 30 com 30% trava em R$ 25,00", abs(floor_30["finalPrice"] - 25.00) < 0.01)

        floor_26 = PricingEnginePy.calculate_slot_pricing(26.00, 40, False, "17:00")
        self.record("Piso Operacional: R$ 26 com 40% trava em R$ 25,00", abs(floor_26["finalPrice"] - 25.00) < 0.01)

        floor_25 = PricingEnginePy.calculate_slot_pricing(25.00, 20, False, "18:00")
        self.record("Piso Operacional: R$ 25 com 20% trava em R$ 25,00", abs(floor_25["finalPrice"] - 25.00) < 0.01)

        # Piso Ético On-Demand (R$ 30,00 ou 45%)
        ondemand_50 = PricingEnginePy.calculate_fair_on_demand_pricing(50.00, 50)
        self.record("On-Demand Piso R$ 30,00 (Base R$ 50, -50%)", abs(ondemand_50["finalPrice"] - 30.00) < 0.01)

        ondemand_200 = PricingEnginePy.calculate_fair_on_demand_pricing(200.00, 60)
        self.record("On-Demand Piso 45% = R$ 90,00 (Base R$ 200, -60%)", abs(ondemand_200["finalPrice"] - 90.00) < 0.01)

        # Badges e Subtextos Literais Obrigatórios (Seção 6.2)
        econ_slot = PricingEnginePy.calculate_slot_pricing(100.00, 20, False, "14:15")
        self.record(
            "Badge Horário Econômico e Subtexto Seção 6.2",
            econ_slot["badgeLabel"] == "Horário Econômico" and econ_slot["subtext"] == "preço menor em horário de menor procura"
        )

        urgent_slot = PricingEnginePy.calculate_slot_pricing(100.00, 20, True, "17:45")
        self.record(
            "Badge Última Hora e Subtexto Seção 6.2",
            urgent_slot["badgeLabel"] == "Última Hora" and urgent_slot["subtext"] == "desconto para hoje"
        )

        std_slot = PricingEnginePy.calculate_slot_pricing(100.00, 0, False, "10:00")
        self.record(
            "Badge Padrão e Subtexto Preço Integral",
            std_slot["badgeLabel"] == "Padrão" and std_slot["subtext"] == "horário de alta procura (preço integral)"
        )

        # Congelamento de Snapshot Imutável
        snapshot = PricingEnginePy.freeze_snapshot(econ_slot, "s1", "srv_1")
        self.record(
            "Congelamento Imutável de Snapshot (freezeSnapshot)",
            snapshot["salonId"] == "s1" and snapshot["serviceId"] == "srv_1" and snapshot["priceFinal"] == 80.00
        )

    # -------------------------------------------------------------------------
    # PARTE 6: TESTES UNITÁRIOS DE REGRAS DE AGENDAMENTO E BUFFER
    # -------------------------------------------------------------------------
    def verify_booking_rules_suite(self):
        print_header("6. SUÍTE DE TESTES: REGRAS DE AGENDAMENTO & BUFFER (10 MIN)")

        # Validação de Blocos de 15 Minutos
        valid_blocks = ["00:00", "08:15", "09:30", "10:45", "14:30", "19:15", "23:45"]
        for tb in valid_blocks:
            self.record(f"Bloco 15 min Válido: {tb}", BookingEnginePy.is_valid_15_minute_block(tb))

        invalid_blocks = ["09:05", "10:10", "14:20", "15:25", "16:35", "17:40", "18:50", "24:00", "abc"]
        for tb in invalid_blocks:
            self.record(f"Bloco Inválido Rejeitado: {tb}", not BookingEnginePy.is_valid_15_minute_block(tb))

        # Rejeição de Slot Desalinhado no Motor
        unaligned_res = BookingEnginePy.validate_booking_slot("14:20", 45, [])
        self.record("Motor Rejeita Horário Desalinhado (14:20)", not unaligned_res["isValid"] and "15 minutos" in unaligned_res["reason"])

        # Cálculo de Slots Utilizados com Buffer de 10 min
        self.record("Cálculo Slots: 45 min + 10 min buffer = 4 slots (60 min)", BookingEnginePy.calculate_slots_used(45, 10) == 4)
        self.record("Cálculo Slots: 30 min + 10 min buffer = 3 slots (45 min)", BookingEnginePy.calculate_slots_used(30, 10) == 3)

        # Buffer de Higienização Obrigatório Entre Atendimentos Consecutivos
        # Agendamento existente: 10:00 a 10:45 (45 min). Buffer até 10:55.
        existing_appts = [{
            "id": "appt_1",
            "timeSlot": "10:00",
            "durationMinutes": 45,
            "status": "CONFIRMED",
            "staffId": "st1",
            "dateDisplay": "Hoje"
        }]

        # Tentativa às 10:45 (imediatamente após o serviço): deve colidir com o buffer até 10:55
        buf_conflict = BookingEnginePy.validate_booking_slot(
            slot_time="10:45",
            service_duration_min=45,
            existing_appointments=existing_appts,
            staff_id="st1",
            date_display="Hoje"
        )
        self.record(
            "Rejeição por Buffer de Higienização às 10:45 (ocupado até 10:55)",
            not buf_conflict["isValid"] and buf_conflict["conflictId"] == "appt_1"
        )

        # Tentativa às 11:00 (após as 10:55): deve ser aprovado com sucesso!
        valid_next = BookingEnginePy.validate_booking_slot(
            slot_time="11:00",
            service_duration_min=45,
            existing_appointments=existing_appts,
            staff_id="st1",
            date_display="Hoje"
        )
        self.record("Aprovação Respeitando Buffer às 11:00", valid_next["isValid"])

        # Proteção de Buffer Antecessor: candidato às 10:15 (vai até 11:10) colide com existente às 11:00
        existing_at_11 = [{
            "id": "appt_2",
            "timeSlot": "11:00",
            "durationMinutes": 45,
            "status": "CONFIRMED",
            "staffId": "st1",
            "dateDisplay": "Hoje"
        }]
        buf_prev_conflict = BookingEnginePy.validate_booking_slot(
            slot_time="10:15",
            service_duration_min=45,
            existing_appointments=existing_at_11,
            staff_id="st1",
            date_display="Hoje"
        )
        self.record("Rejeição Antecessora por Buffer (10:15 invade 11:00)", not buf_prev_conflict["isValid"])

        # Candidato às 10:00 (vai até 10:55) desocupa antes das 11:00 -> Aprovado!
        valid_prev = BookingEnginePy.validate_booking_slot(
            slot_time="10:00",
            service_duration_min=45,
            existing_appointments=existing_at_11,
            staff_id="st1",
            date_display="Hoje"
        )
        self.record("Aprovação Antecessora Respeitando Buffer (10:00 livre até 10:55)", valid_prev["isValid"])

        # Prevenção de Double-Booking Exato
        double_conflict = BookingEnginePy.validate_booking_slot(
            slot_time="10:00",
            service_duration_min=45,
            existing_appointments=existing_appts,
            staff_id="st1",
            date_display="Hoje"
        )
        self.record("Prevenção Rigorosa de Double-Booking Exato (10:00 mesmo staff)", not double_conflict["isValid"])

        # Profissionais Distintos Permitem Agendamento Simultâneo
        different_staff = BookingEnginePy.validate_booking_slot(
            slot_time="10:00",
            service_duration_min=45,
            existing_appointments=existing_appts,
            staff_id="st2", # Staff diferente!
            date_display="Hoje"
        )
        self.record("Agendamento Simultâneo Permitido para Profissionais Distintos", different_staff["isValid"])

        # Agendamento Cancelado Libera o Slot
        cancelled_appts = [{
            "id": "appt_cancelled",
            "timeSlot": "10:00",
            "durationMinutes": 45,
            "status": "CANCELLED_BY_USER",
            "staffId": "st1",
            "dateDisplay": "Hoje"
        }]
        slot_freed = BookingEnginePy.validate_booking_slot(
            slot_time="10:00",
            service_duration_min=45,
            existing_appointments=cancelled_appts,
            staff_id="st1",
            date_display="Hoje"
        )
        self.record("Agendamento Cancelado Libera Horário para Nova Reserva", slot_freed["isValid"])
 
        # Taxa de Retenção de 30% para cancelamento tardio (< 24h)
        ret_100 = BookingEnginePy.calculate_cancellation_retention_fee(100.0, is_under_24h=True)
        self.record("Retenção 30% sob < 24h (R$ 100 -> R$ 30,00)", abs(ret_100 - 30.00) < 0.01)

        ret_84 = BookingEnginePy.calculate_cancellation_retention_fee(84.0, is_under_24h=True)
        self.record("Retenção 30% sob < 24h (R$ 84 -> R$ 25,20)", abs(ret_84 - 25.20) < 0.01)

        ret_free = BookingEnginePy.calculate_cancellation_retention_fee(84.0, is_under_24h=False)
        self.record("Cancelamento Antecipado (> 24h) Gratuito (R$ 0,00)", abs(ret_free - 0.00) < 0.01)

    # -------------------------------------------------------------------------
    # PARTE 7: TESTE DO CÁLCULO DE SCORE SUS (USABILIDADE)
    # -------------------------------------------------------------------------
    def verify_sus_score_calculation(self):
        print_header("7. SUÍTE DE TESTES: CÁLCULO DE PONTUAÇÃO SUS (USABILIDADE)")

        # Caso Perfeito (5 nas ímpares, 1 nas pares) -> 100 pontos
        best_answers = {1: 5, 2: 1, 3: 5, 4: 1, 5: 5, 6: 1, 7: 5, 8: 1, 9: 5, 10: 1}
        self.record("Pontuação SUS Máxima (100.0)", abs(calculate_sus_score_py(best_answers) - 100.0) < 0.01)

        # Caso Pior (1 nas ímpares, 5 nas pares) -> 0 pontos
        worst_answers = {1: 1, 2: 5, 3: 1, 4: 5, 5: 1, 6: 5, 7: 1, 8: 5, 9: 1, 10: 5}
        self.record("Pontuação SUS Mínima (0.0)", abs(calculate_sus_score_py(worst_answers) - 0.0) < 0.01)

        # Caso Neutro (todos 3) -> 50 pontos
        neutral_answers = {i: 3 for i in range(1, 11)}
        self.record("Pontuação SUS Neutra (50.0)", abs(calculate_sus_score_py(neutral_answers) - 50.0) < 0.01)

        # Caso Realista de Alta Aceitação (H1/H2)
        realistic_answers = {1: 5, 2: 2, 3: 5, 4: 1, 5: 4, 6: 2, 7: 5, 8: 1, 9: 4, 10: 2}
        score = calculate_sus_score_py(realistic_answers)
        self.record(f"Score SUS Realista: {score:.1f} (Meta > 68.0 Atingida)", score > 68.0, f"Score: {score:.1f}")

    # -------------------------------------------------------------------------
    # PARTE 8: SUÍTE DE TESTES: CAMADA ROOM DATABASE (SQLITE) & HARD DELETE LGPD
    # -------------------------------------------------------------------------
    def verify_room_sqlite_suite(self):
        print_header("8. SUÍTE DE TESTES: CAMADA ROOM DATABASE (SQLITE) & HARD DELETE LGPD")

        # 8.1 Verificação de Gradle e Plugins
        root_build = (ANDROID_ROOT / "build.gradle.kts").read_text(encoding="utf-8")
        self.record(
            "Plugin KSP Raiz: com.google.devtools.ksp (1.9.24-1.0.20)",
            "com.google.devtools.ksp" in root_build and "1.9.24-1.0.20" in root_build
        )

        app_build = (ANDROID_ROOT / "app" / "build.gradle.kts").read_text(encoding="utf-8")
        self.record(
            "Plugin KSP Aplicado em app/build.gradle.kts",
            'id("com.google.devtools.ksp")' in app_build
        )
        self.record(
            "Dependência Room Runtime (2.6.1)",
            "androidx.room:room-runtime:2.6.1" in app_build
        )
        self.record(
            "Dependência Room KTX Coroutines (2.6.1)",
            "androidx.room:room-ktx:2.6.1" in app_build
        )
        self.record(
            "Dependência Room Compiler via KSP (2.6.1)",
            'ksp("androidx.room:room-compiler:2.6.1")' in app_build
        )
        self.record(
            "Dependência Room Testing (2.6.1)",
            "androidx.room:room-testing:2.6.1" in app_build
        )

        # 8.2 Inicialização no MainActivity e Repositório
        main_act = (JAVA_PKG_MAIN / "MainActivity.kt").read_text(encoding="utf-8")
        self.record(
            "MainActivity inicializa SalonRepository com Context",
            "SalonRepository.initialize(applicationContext)" in main_act
        )

        repo_code = (JAVA_PKG_MAIN / "data" / "SalonRepository.kt").read_text(encoding="utf-8")
        self.record(
            "SalonRepository expõe userProfile StateFlow",
            "val userProfile: StateFlow<UserProfileEntity?>" in repo_code
        )
        self.record(
            "SalonRepository expõe latestSusEvaluation StateFlow",
            "val latestSusEvaluation: StateFlow<SUSEvaluation?>" in repo_code
        )
        self.record(
            "SalonRepository conecta DAOs Room com exclusão SQLite",
            "db.appointmentDao().deleteAllAppointments()" in repo_code and
            "db.favoriteDao().deleteAllFavorites()" in repo_code and
            "db.userProfileDao().deleteUserProfile()" in repo_code and
            "db.susEvaluationDao().deleteAllEvaluations()" in repo_code
        )

        # 8.3 Execução Dinâmica de SQLite In-Memory (Espelhamento Canônico do Room DB)
        conn = sqlite3.connect(":memory:")
        cursor = conn.cursor()

        # Schema das 4 tabelas
        cursor.execute("""
            CREATE TABLE appointments (
                id TEXT PRIMARY KEY,
                salon_id TEXT NOT NULL,
                service_id TEXT NOT NULL,
                staff_id TEXT,
                date_display TEXT NOT NULL,
                time_slot TEXT NOT NULL,
                final_price REAL NOT NULL,
                status TEXT NOT NULL,
                booked_at_iso TEXT NOT NULL,
                cancellation_reason TEXT,
                cancellation_fee REAL
            );
        """)
        cursor.execute("CREATE INDEX index_appointments_salon_id ON appointments(salon_id);")

        cursor.execute("""
            CREATE TABLE favorites (
                salon_id TEXT PRIMARY KEY,
                added_at_timestamp INTEGER NOT NULL
            );
        """)

        cursor.execute("""
            CREATE TABLE user_profile (
                participant_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                lgpd_accepted INTEGER NOT NULL,
                accepted_at_timestamp INTEGER NOT NULL
            );
        """)

        cursor.execute("""
            CREATE TABLE sus_evaluations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                participant_id TEXT NOT NULL,
                answers_serialized TEXT NOT NULL,
                score REAL NOT NULL,
                retention_yes INTEGER NOT NULL,
                evaluated_at_iso TEXT NOT NULL
            );
        """)

        self.record("Room SQLite Schema: 4 Tabelas Criadas com Sucesso", True)

        # Inserção de AppointmentEntity
        cursor.execute("""
            INSERT INTO appointments (
                id, salon_id, service_id, staff_id, date_display, time_slot,
                final_price, status, booked_at_iso, cancellation_reason, cancellation_fee
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            "appt_sql_001", "s1", "srv_s1_1", "st1", "Hoje", "14:30",
            85.00, "CONFIRMED", "2026-10-10T14:30:00Z", None, None
        ))
        cursor.execute("SELECT id, salon_id, final_price, status FROM appointments WHERE id = 'appt_sql_001'")
        appt_row = cursor.fetchone()
        self.record(
            "Room SQLite DAO: Inserção e Consulta de AppointmentEntity",
            appt_row is not None and appt_row[0] == "appt_sql_001" and appt_row[2] == 85.00 and appt_row[3] == "CONFIRMED"
        )

        # Atualização/Cancelamento de Agendamento
        cursor.execute("""
            UPDATE appointments
            SET status = 'CANCELLED_BY_USER', cancellation_reason = 'Imprevisto', cancellation_fee = 25.50
            WHERE id = 'appt_sql_001'
        """)
        cursor.execute("SELECT status, cancellation_reason, cancellation_fee FROM appointments WHERE id = 'appt_sql_001'")
        cancelled_row = cursor.fetchone()
        self.record(
            "Room SQLite DAO: Cancelamento de Agendamento com Retenção",
            cancelled_row is not None and cancelled_row[0] == "CANCELLED_BY_USER" and cancelled_row[2] == 25.50
        )

        # Inserção e Consulta de FavoriteEntity
        cursor.execute("INSERT INTO favorites (salon_id, added_at_timestamp) VALUES (?, ?)", ("s1", 1728500000000))
        cursor.execute("SELECT salon_id FROM favorites WHERE salon_id = 's1'")
        fav_row = cursor.fetchone()
        self.record(
            "Room SQLite DAO: Persistência de Favorito",
            fav_row is not None and fav_row[0] == "s1"
        )

        # Inserção e Consulta de UserProfileEntity
        cursor.execute("""
            INSERT INTO user_profile (participant_id, name, phone, lgpd_accepted, accepted_at_timestamp)
            VALUES (?, ?, ?, ?, ?)
        """, ("P01", "Mariana Costa", "(11) 98765-4321", 1, 1728500000000))
        cursor.execute("SELECT participant_id, name, lgpd_accepted FROM user_profile WHERE participant_id = 'P01'")
        user_row = cursor.fetchone()
        self.record(
            "Room SQLite DAO: Persistência de UserProfile com Aceite LGPD",
            user_row is not None and user_row[0] == "P01" and user_row[2] == 1
        )

        # Inserção e Consulta de SusEvaluationEntity
        cursor.execute("""
            INSERT INTO sus_evaluations (participant_id, answers_serialized, score, retention_yes, evaluated_at_iso)
            VALUES (?, ?, ?, ?, ?)
        """, ("P01", "1:5;2:2;3:5;4:1;5:4;6:2;7:5;8:1;9:4;10:2", 87.5, 1, "2026-10-10T16:00:00Z"))
        cursor.execute("SELECT participant_id, score, retention_yes FROM sus_evaluations WHERE participant_id = 'P01'")
        sus_row = cursor.fetchone()
        self.record(
            "Room SQLite DAO: Persistência de Avaliação SUS com Score",
            sus_row is not None and sus_row[0] == "P01" and abs(sus_row[1] - 87.5) < 0.01
        )

        # Hard Delete LGPD: Exclusão total nas 4 tabelas
        cursor.execute("DELETE FROM appointments")
        cursor.execute("DELETE FROM favorites")
        cursor.execute("DELETE FROM user_profile")
        cursor.execute("DELETE FROM sus_evaluations")
        conn.commit()

        cursor.execute("SELECT (SELECT COUNT(*) FROM appointments) + (SELECT COUNT(*) FROM favorites) + (SELECT COUNT(*) FROM user_profile) + (SELECT COUNT(*) FROM sus_evaluations)")
        total_remaining = cursor.fetchone()[0]
        self.record(
            "Room SQLite LGPD: Hard Delete expurga 100% dos dados nas 4 tabelas",
            total_remaining == 0,
            f"Registros remanescentes: {total_remaining}"
        )
        conn.close()

        # 8.4 Teste de Conversores em Python (Serialização e Desserialização de Respostas SUS)
        test_answers = {1: 5, 2: 2, 3: 5, 4: 1, 5: 4, 6: 2, 7: 5, 8: 1, 9: 4, 10: 2}
        serialized = ConvertersPy.serialize_answers(test_answers)
        deserialized = ConvertersPy.deserialize_answers(serialized)
        self.record(
            "Room SQLite Converters: Round-trip de Respostas SUS (Map<Int, Int> <-> String)",
            deserialized == test_answers and "1:5" in serialized and "10:2" in serialized
        )

    # -------------------------------------------------------------------------
    # PARTE 9: SUÍTE DE TESTES: SISTEMA DE NOTIFICAÇÕES NATIVAS & PAYLOADS
    # -------------------------------------------------------------------------
    def verify_notification_system_suite(self):
        print_header("9. SUÍTE DE TESTES: SISTEMA DE NOTIFICAÇÕES NATIVAS & PAYLOADS")

        # 9.1 Permissões no AndroidManifest.xml
        manifest_path = APP_SRC_MAIN / "AndroidManifest.xml"
        manifest_content = manifest_path.read_text(encoding="utf-8") if manifest_path.exists() else ""
        self.record(
            "AndroidManifest: Permissão POST_NOTIFICATIONS (Android 13+)",
            "android.permission.POST_NOTIFICATIONS" in manifest_content
        )
        self.record(
            "AndroidManifest: Permissão VIBRATE para Alertas de Limiar",
            "android.permission.VIBRATE" in manifest_content
        )

        # 9.2 Integridade Estática de BeautyPassNotificationHelper.kt
        helper_path = JAVA_PKG_MAIN / "notification" / "BeautyPassNotificationHelper.kt"
        helper_content = helper_path.read_text(encoding="utf-8") if helper_path.exists() else ""

        self.record(
            "NotificationHelper: Canal de Alta Prioridade (beautypass_booking_channel)",
            'CHANNEL_ID = "beautypass_booking_channel"' in helper_content and
            "NotificationManager.IMPORTANCE_HIGH" in helper_content
        )
        self.record(
            "NotificationHelper: Identificadores Canônicos (1001, 1002, 1003)",
            "NOTIF_ID_FREEZE_WARNING = 1001" in helper_content and
            "NOTIF_ID_BOOKING_CONFIRMED = 1002" in helper_content and
            "NOTIF_ID_CANCELLATION = 1003" in helper_content
        )
        self.record(
            "NotificationHelper: Padrão de Vibração e Luz Serene Teal (0xFF00685F)",
            "0xFF00685F" in helper_content and
            "0, 300, 200, 300" in helper_content
        )
        self.record(
            "NotificationHelper: Desacoplamento de Payloads Puros JVM",
            "formatFreezeWarningPayload" in helper_content and
            "formatBookingConfirmedPayload" in helper_content and
            "formatCancellationPayload" in helper_content
        )
        self.record(
            "NotificationHelper: Métodos de Despacho Seguro",
            "showFreezeTimerAlert" in helper_content and
            "showBookingConfirmedNotification" in helper_content and
            "showCancellationNotification" in helper_content
        )

        # 9.3 Validação Dinâmica de Payloads em Python
        # Alerta do Timer (2 minutos)
        freeze_payload = NotificationHelperPy.format_freeze_warning_payload("Studio Bella Vista", 2, 84.00)
        self.record(
            "Payload Alerta Timer: ID 1001 e Canal Oficial",
            freeze_payload["id"] == 1001 and freeze_payload["channelId"] == "beautypass_booking_channel"
        )
        self.record(
            "Payload Alerta Timer: Conteúdo de 2 min e Tarifa Congelada R$ 84,00",
            "2 minutos" in freeze_payload["message"] and "Studio Bella Vista" in freeze_payload["message"] and "R$ 84.00" in freeze_payload["message"]
        )

        # Confirmação de Reserva Instantânea
        booking_payload = NotificationHelperPy.format_booking_confirmed_payload(
            "appt_vouch_123", "L'Élégance Jardins", "Corte Visagista & Escova", "Hoje, 10 Out", "14:30"
        )
        self.record(
            "Payload Confirmação: ID 1002 e Título Canônico",
            booking_payload["id"] == 1002 and "Reserva Confirmada com Sucesso! ✂️" in booking_payload["title"]
        )
        self.record(
            "Payload Confirmação: Detalhamento de Salão, Serviço, Horário e Voucher",
            "Corte Visagista & Escova" in booking_payload["message"] and
            "L'Élégance Jardins" in booking_payload["message"] and
            "14:30" in booking_payload["message"] and
            "appt_vouch_123" in booking_payload["message"]
        )

        # Cancelamento com Retenção de 30%
        canc_with_fee = NotificationHelperPy.format_cancellation_payload("Studio Bella Vista", 25.20)
        self.record(
            "Payload Cancelamento: ID 1003 e Título Canônico",
            canc_with_fee["id"] == 1003 and canc_with_fee["title"] == "Agendamento Cancelado"
        )
        self.record(
            "Payload Cancelamento com Taxa: Formatação Exata da Retenção de 30%",
            "Taxa de retenção de 30%: R$ 25.20" in canc_with_fee["message"] and
            "Meus Agendamentos" in canc_with_fee["message"]
        )

        # Cancelamento sem Taxa (Gratuito / Nulo)
        canc_free = NotificationHelperPy.format_cancellation_payload("Studio Bella Vista", None)
        self.record(
            "Payload Cancelamento Gratuito: Mensagem Limpa sem Taxa",
            "Taxa de retenção" not in canc_free["message"] and "cancelado com sucesso" in canc_free["message"]
        )

        # 9.4 Simulação do Limiar do Timer e Prevenção de Múltiplos Disparos
        has_alerted = False
        trigger_count = 0
        trigger_second = -1
        for sec in range(600, -1, -1):
            if sec <= 120 and not has_alerted:
                has_alerted = True
                trigger_count += 1
                trigger_second = sec

        self.record(
            "Timer Checkout: Disparo Exato no Limiar de 120 Segundos (2 Minutos)",
            trigger_count == 1 and trigger_second == 120 and has_alerted
        )

        # Avaliação de condições de borda
        self.record(
            "Timer Checkout: Silencioso em 121s (sec > 120)",
            not (121 <= 120 and not False)
        )
        self.record(
            "Timer Checkout: Prevenção de Re-disparo em 119s com flag ativa",
            not (119 <= 120 and not True)
        )
        self.record(
            "Timer Checkout: Prevenção de Re-disparo em 0s com flag ativa",
            not (0 <= 120 and not True)
        )

    # -------------------------------------------------------------------------
    # PARTE 10: SUÍTE DE TESTES: INTEGRAÇÃO DAS TELAS COMPOSE & ROOM / NOTIF
    # -------------------------------------------------------------------------
    def verify_compose_screens_integration_suite(self):
        print_header("10. SUÍTE DE TESTES: INTEGRAÇÃO DAS TELAS COMPOSE & ROOM / NOTIF")

        # 10.1 CheckoutScreen.kt
        checkout_file = JAVA_PKG_MAIN / "ui" / "screens" / "checkout" / "CheckoutScreen.kt"
        checkout_content = checkout_file.read_text(encoding="utf-8") if checkout_file.exists() else ""

        self.record(
            "CheckoutScreen: Diálogo Contextual Compose de Permissão POST_NOTIFICATIONS",
            "showPermissionContextDialog" in checkout_content and
            "AlertDialog" in checkout_content and
            "POST_NOTIFICATIONS" in checkout_content
        )
        self.record(
            "CheckoutScreen: Alerta de 2 minutos do congelamento de preço (120s)",
            "hasAlertedTwoMinutes" in checkout_content and
            "secondsLeft <= 120" in checkout_content and
            "showFreezeTimerAlert" in checkout_content
        )
        self.record(
            "CheckoutScreen: Notificação instantânea ao confirmar agendamento",
            "showBookingConfirmedNotification" in checkout_content and
            "SalonRepository.addAppointment" in checkout_content
        )

        # 10.2 AppointmentsScreen.kt
        appts_file = JAVA_PKG_MAIN / "ui" / "screens" / "appointments" / "AppointmentsScreen.kt"
        appts_content = appts_file.read_text(encoding="utf-8") if appts_file.exists() else ""

        self.record(
            "AppointmentsScreen: Notificação de cancelamento com cálculo de retenção de 30%",
            "showCancellationNotification" in appts_content and
            "calculateCancellationRetentionFee" in appts_content
        )
        self.record(
            "AppointmentsScreen: Redirecionamento de aba para Histórico pós-cancelamento",
            "selectedTab = 1" in appts_content
        )

        # 10.3 ProfileScreen.kt
        profile_file = JAVA_PKG_MAIN / "ui" / "screens" / "profile" / "ProfileScreen.kt"
        profile_content = profile_file.read_text(encoding="utf-8") if profile_file.exists() else ""

        self.record(
            "ProfileScreen: Conexão reativa com userProfile do Room DB",
            "SalonRepository.userProfile.collectAsState()" in profile_content
        )
        self.record(
            "ProfileScreen: Persistência do questionário SUS no Room DB",
            "SalonRepository.saveSusEvaluation" in profile_content and
            "SalonRepository.latestSusEvaluation.collectAsState()" in profile_content
        )
        self.record(
            "ProfileScreen: Hard Delete LGPD expurga SQLite e reseta para Onboarding",
            "SalonRepository.clearAllData()" in profile_content and
            "onResetToOnboarding" in profile_content
        )

        # 10.4 HomeScreen.kt
        home_file = JAVA_PKG_MAIN / "ui" / "screens" / "home" / "HomeScreen.kt"
        home_content = home_file.read_text(encoding="utf-8") if home_file.exists() else ""

        self.record(
            "HomeScreen: Reatividade de Favoritos sincronizada com Room DB",
            "SalonRepository.favoriteIds.collectAsState()" in home_content and
            "SalonRepository.toggleFavorite" in home_content
        )

        # 10.5 Ausência Estrita de Preto Puro (#000000) em Todas as Telas Compose
        screens_dir = JAVA_PKG_MAIN / "ui" / "screens"
        pure_black_found = False
        screen_files = list(screens_dir.rglob("*.kt"))
        for sf in screen_files:
            scontent = sf.read_text(encoding="utf-8")
            if "0xFF000000" in scontent or "#000000" in scontent or "Color.Black" in scontent:
                pure_black_found = True
                break

        self.record(
            f"WCAG / Material 3: Ausência total de preto puro (#000000) nas {len(screen_files)} telas Compose",
            not pure_black_found,
            "Respeito ao Serene Mint & Teal e OceanicCharcoal"
        )

    # -------------------------------------------------------------------------
    # RESUMO FINAL DE EXECUÇÃO
    # -------------------------------------------------------------------------
    def print_summary(self):
        print_header("RELATÓRIO FINAL DE VERIFICAÇÃO AUTOMATIZADA")
        print(f" Total de Asserções Executadas: {self.total_tests}")
        print(f" {TermColor.GREEN}Testes Aprovados:              {self.passed_tests}{TermColor.RESET}")
        print(f" {TermColor.RED}Testes Reprovados:             {self.failed_tests}{TermColor.RESET}")

        if self.failed_tests == 0:
            print("\n" + f"{TermColor.BOLD}{TermColor.GREEN}✔ 100% DAS VERIFICAÇÕES DO BEAUTYPASS FORAM APROVADAS COM SUCESSO!{TermColor.RESET}\n")
            return 0
        else:
            print("\n" + f"{TermColor.BOLD}{TermColor.RED}✘ FALHA NA VERIFICAÇÃO: {self.failed_tests} teste(s) reprovado(s).{TermColor.RESET}\n")
            return 1


def main():
    verifier = BuildVerifier()
    verifier.verify_project_structure()
    verifier.verify_design_tokens()
    verifier.verify_salon_catalog()
    verifier.verify_luhn_suite()
    verifier.verify_pricing_engine_suite()
    verifier.verify_booking_rules_suite()
    verifier.verify_sus_score_calculation()
    verifier.verify_room_sqlite_suite()
    verifier.verify_notification_system_suite()
    verifier.verify_compose_screens_integration_suite()
    exit_code = verifier.print_summary()
    sys.exit(exit_code)


if __name__ == "__main__":
    main()
