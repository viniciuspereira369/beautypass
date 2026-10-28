import threading
import http.server
import socketserver
import os
from playwright.sync_api import sync_playwright

PORT = 8095

def start_server():
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    handler = http.server.SimpleHTTPRequestHandler
    with socketserver.TCPServer(("", PORT), handler) as httpd:
        httpd.serve_forever()

def run_frente2_tests():
    server_thread = threading.Thread(target=start_server, daemon=True)
    server_thread.start()

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        print("\n=== TESTANDO IMPLEMENTAÇÃO COMPLETA DA FRENTE 2 (PAINEL B2B & HIPÓTESE H3) ===")
        page.goto(f"http://localhost:{PORT}/beautypass_app.html?p=P01")
        page.wait_for_selector("#onboarding-participant")

        # 1. Login no Onboarding
        page.fill("#onboarding-name", "Pesquisador B2B H3")
        page.fill("#onboarding-phone", "11988887777")
        page.fill("#onboarding-code", "0000")
        page.check("#onboarding-terms-check")
        page.dispatch_event("#onboarding-name", "input")
        page.dispatch_event("#onboarding-phone", "input")
        page.dispatch_event("#onboarding-code", "input")
        page.dispatch_event("#onboarding-terms-check", "change")
        page.click("#onboarding-submit-btn")
        page.wait_for_selector("#screen-home.active", timeout=5000)

        # 2. Navegar para Ajustes -> Abrir Painel do Salão Parceiro (B2B — H3)
        page.click(".nav-item[data-screen='profile']")
        page.wait_for_selector("#screen-profile.active", timeout=5000)
        page.click("#btn-open-partner-b2b")
        page.wait_for_selector("#screen-partner-b2b.active", timeout=5000)
        print("[OK] Teste 1: Navegou para #screen-partner-b2b via botão no Painel de Hipóteses.")

        # 3. Validar Aba 1 (Agenda com Slots Ociosos Destacados)
        slot_cards = page.locator("#b2b-agenda-slots-grid .b2b-slot-card").count()
        idle_cards = page.locator("#b2b-agenda-slots-grid .slot-idle-opportunity").count()
        promo_cards = page.locator("#b2b-agenda-slots-grid .slot-promo-active").count()
        print(f"[OK] Teste 2 (Aba 1 - Agenda B2B): {slot_cards} slots renderizados ({idle_cards} ociosos, {promo_cards} com regra ativa).")
        assert slot_cards == 12, f"Esperado 12 slots na grade B2B, obtido {slot_cards}"

        # 4. Validar Aba 2 (Configuração de Regra de Desconto 15-35% e Reflexo em Tempo Real no App)
        page.click("#b2b-tab-btn-rules")
        page.wait_for_selector("#b2b-pane-rules.active", timeout=3000)
        page.select_option("#b2b-rule-time", "15:30")
        page.click(".b2b-disc-pill[data-disc='35']")
        sim_text = page.inner_text("#b2b-payout-simulator-box")
        assert "-35% OFF" in sim_text, "Simulador deve exibir o desconto selecionado de -35% OFF"
        page.click("#btn-save-b2b-rule")

        # Verificar reflexo em tempo real no cálculo de preço do cliente para s1 às 15:30
        live_pricing = page.evaluate("() => calculateSlotPrice(MOCK_SALONS[0], '15:30', MOCK_SALONS[0].service, '2026-10-21')")
        print(f"[OK] Teste 3 (Aba 2 - Regra em Tempo Real): Slot 15:30 atualizado no app com -{live_pricing['discountPct']}% OFF (R$ {live_pricing['finalPrice']}).")
        assert live_pricing["hasDiscount"] and live_pricing["discountPct"] == 35, "A regra salva no B2B deve atualizar os slots do salão em tempo real"

        # 5. Validar Aba 3 (Check-in de Voucher QR -> Status COMPLETED + Coleta de 3 LOIs para GO em H3)
        page.click("#b2b-tab-btn-loi")
        page.wait_for_selector("#b2b-pane-loi.active", timeout=3000)

        # Validar Check-in do voucher BP-108420
        page.fill("#b2b-voucher-input", "BP-108420")
        page.click("#btn-validate-b2b-voucher")
        appt_status = page.evaluate("() => AppState.appointmentsList.find(a => a.id === 'BP-108420').status")
        print(f"[OK] Teste 4 (Aba 3 - Check-in QR): Voucher BP-108420 atualizado em tempo real para status='{appt_status}'.")
        assert appt_status == "COMPLETED", "O check-in no painel B2B deve mudar o agendamento para COMPLETED"

        # Registrar 3 Cartas de Intenção (LOIs) para atingir o critério GO (>= 3/5 salões da Seção 2 e 5.4)
        for salon_id, owner in [("s1", "Juliana Paes"), ("s2", "Camila Coutinho"), ("s3", "Ricardo Alencar")]:
            page.select_option("#b2b-salon-selector", salon_id)
            page.fill("#h3-owner-name", owner)
            page.select_option("#h3-max-discount", "30")
            page.select_option("#h3-acceptable-commission", "12")
            page.fill("#h3-objections", "Repasse D+1 e proteção contra no-show")
            page.check("#h3-loi-signed-check")
            page.click("#btn-submit-h3-loi")

        h3_report = page.evaluate("() => calculateH3Report()")
        print(f"[OK] Teste 5 (Hipótese H3 - Cartas de Intenção): {h3_report['signedCount']}/{h3_report['targetCount']} LOIs assinadas | Validada (GO)={h3_report['isValidated']}")
        assert h3_report["signedCount"] == 3 and h3_report["isValidated"] is True, "Com 3 LOIs assinadas, a Hipótese H3 deve estar Validada (GO)"

        # Voltar para Ajustes e verificar card H3 no Dashboard
        page.click("#screen-partner-b2b .back-circle-btn >> nth=0")
        page.wait_for_selector("#screen-profile.active", timeout=3000)
        dashboard_text = page.inner_text("#validation-dashboard-metrics")
        assert "H3: Adesão Salões Parceiros (B2B)" in dashboard_text and "3/5" in dashboard_text, "O Dashboard deve exibir o KPI H3 com 3/5 Cartas de Intenção"
        print("[OK] Teste 6: KPI H3 renderizado como 'Validada (GO)' (3/5 LOIs) no Painel de Hipóteses!")

        print("\n=== TODOS OS TESTES DA FRENTE 2 (B2B & H3) PASSARAM COM 100% DE SUCESSO! ===\n")
        browser.close()

if __name__ == "__main__":
    run_frente2_tests()
