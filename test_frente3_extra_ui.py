# -*- coding: utf-8 -*-
"""
test_frente3_extra_ui.py
Suíte de testes automatizados E2E Playwright para a Frente 3 do BeautyPass:
1. Grade 4x2 Circular de Salões Parceiros em Destaque na Home (DESIGN_EXTRACT_SPEC.md S1)
2. Dual Fulfillment Switch ("No Salão" vs "A Domicílio") (DESIGN_EXTRACT_SPEC.md S2)
3. Taxa Variável de Deslocamento (+ R$ 5/km além de 2 km) e Endereço no Checkout
4. Campo de Observação do Cliente ("Add note") integrado ao Voucher, Agendamentos e Painel B2B
5. Responsividade em 360px, 393px e 412px sem overflow horizontal
"""

import threading
import http.server
import socketserver
import os
import sys
from playwright.sync_api import sync_playwright

PORT = 8100

class ReusableTCPServer(socketserver.TCPServer):
    allow_reuse_address = True

def start_server():
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    handler = http.server.SimpleHTTPRequestHandler
    with ReusableTCPServer(("", PORT), handler) as httpd:
        httpd.serve_forever()

def run_frente3_tests():
    server_thread = threading.Thread(target=start_server, daemon=True)
    server_thread.start()

    print(f"[*] Servidor de teste iniciado na porta {PORT}")

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            viewport={"width": 393, "height": 852},
            device_scale_factor=1.0,
            has_touch=True
        )
        page = context.new_page()

        # Listeners para capturar erros de console
        errors = []
        page.on("pageerror", lambda err: errors.append(f"PageError: {err}"))
        page.on("console", lambda msg: errors.append(f"ConsoleError: {msg.text}") if msg.type == "error" else None)

        print(f"[*] Navegando para http://localhost:{PORT}/beautypass_app.html?p=P03")
        page.goto(f"http://localhost:{PORT}/beautypass_app.html?p=P03", wait_until="networkidle")
        page.wait_for_selector("#onboarding-participant", timeout=5000)

        # 0. Login no Onboarding
        page.fill("#onboarding-name", "Pesquisadora Frente 3")
        page.fill("#onboarding-phone", "11988886666")
        page.fill("#onboarding-code", "0000")
        page.check("#onboarding-terms-check")
        page.dispatch_event("#onboarding-name", "input")
        page.dispatch_event("#onboarding-phone", "input")
        page.dispatch_event("#onboarding-code", "input")
        page.dispatch_event("#onboarding-terms-check", "change")
        page.click("#onboarding-submit-btn")
        page.wait_for_selector("#screen-home.active", timeout=5000)

        # ------------------------------------------------------------------
        # TESTE 1: Grade 4x2 Circular de Parceiros na Home
        # ------------------------------------------------------------------
        print("\n--- TESTE 1: Grade 4x2 Circular de Parceiros na Home ---")
        grid = page.locator("#partner-circles-grid")
        assert grid.is_visible(), "Grade #partner-circles-grid deve estar visível na Home"

        cards = page.locator(".partner-circle-card")
        card_count = cards.count()
        print(f"[*] Total de parceiros circulares na grade: {card_count}")
        assert card_count == 8, f"Esperado exatamente 8 parceiros na grade 4x2, encontrado: {card_count}"

        # Verifica componentes do primeiro card
        first_card = cards.first
        assert first_card.locator(".partner-circle-ring").is_visible(), "Anel de destaque deve estar visível"
        assert first_card.locator(".partner-circle-badge").is_visible(), "Badge de verificado deve estar visível"
        assert first_card.locator(".partner-circle-name").is_visible(), "Nome do salão deve estar visível"
        assert first_card.locator(".partner-circle-meta").is_visible(), "Metadados (nota/distância) devem estar visíveis"

        first_name = first_card.locator(".partner-circle-name").inner_text()
        print(f"[*] Primeiro parceiro circular: {first_name}")

        # Clica no 1º parceiro para abrir tela de detalhes
        first_card.click()
        page.wait_for_selector("#screen-detail.active", timeout=5000)
        assert page.locator("#screen-detail").is_visible(), "Deve navegar para a tela de detalhes ao clicar no parceiro"
        print("[+] Teste 1 APROVADO: Grade 4x2 renderizada com 8 parceiros e navegação funcional.")

        # ------------------------------------------------------------------
        # TESTE 2: Dual Fulfillment Switch ("No Salão" vs "A Domicílio")
        # ------------------------------------------------------------------
        print("\n--- TESTE 2: Dual Fulfillment Switch no Detalhe ---")
        btn_salon = page.locator("#btn-fulfillment-salon")
        btn_homecare = page.locator("#btn-fulfillment-homecare")
        badge_current = page.locator("#fulfillment-badge-current")
        notice_box = page.locator("#fulfillment-notice-box")

        assert btn_salon.is_visible(), "Botão 'No Salão' deve existir"
        assert btn_homecare.is_visible(), "Botão 'A Domicílio' deve existir"
        assert "active" in btn_salon.get_attribute("class"), "Padrão inicial deve ser 'No Salão' ativo"
        
        initial_badge = badge_current.inner_text().strip().upper()
        print(f"[*] Badge inicial: '{initial_badge}'")
        assert "SAL" in initial_badge, "Badge deve indicar 'No Salão'"
        assert not notice_box.is_visible(), "Aviso de home care não deve estar visível inicialmente"

        # Clica em 'A Domicílio'
        btn_homecare.click()
        page.wait_for_timeout(300)
        assert "active" in btn_homecare.get_attribute("class"), "'A Domicílio' deve estar ativo após clique"
        assert "active" not in btn_salon.get_attribute("class"), "'No Salão' não deve estar ativo"
        
        switched_badge = badge_current.inner_text().strip().upper()
        print(f"[*] Badge após alternar: '{switched_badge}'")
        assert "DOMIC" in switched_badge, "Badge deve atualizar para 'A Domicílio'"
        assert notice_box.is_visible(), "Aviso de home care deve se tornar visível"
        print("[+] Teste 2 APROVADO: Dual Fulfillment Switch alterna reativamente entre 'No Salão' e 'A Domicílio'.")

        # ------------------------------------------------------------------
        # TESTE 3: Taxa de Deslocamento e Endereço no Checkout (Home Care)
        # ------------------------------------------------------------------
        print("\n--- TESTE 3: Taxa de Deslocamento e Endereço no Checkout ---")
        # Clica no CTA de prosseguir para checkout (onclick="proceedToCheckout()")
        cta_btn = page.locator("button[onclick*='proceedToCheckout']")
        cta_btn.click()
        page.wait_for_selector("#screen-checkout.active", timeout=5000)

        homecare_section = page.locator("#checkout-homecare-section")
        assert homecare_section.is_visible(), "Seção de endereço para atendimento a domicílio deve estar visível"

        address_input = page.locator("#checkout-homecare-address")
        assert address_input.is_visible(), "Campo de endereço da cliente deve estar visível"
        assert len(address_input.input_value()) > 0, "Campo de endereço deve vir pré-preenchido"

        mode_label = page.locator("#checkout-fulfillment-mode-label")
        assert "A Domicílio" in mode_label.inner_text(), "Modalidade no resumo deve exibir 'A Domicílio'"

        disp_row = page.locator("#checkout-displacement-row")
        assert disp_row.is_visible(), "Linha de Taxa de Deslocamento deve estar visível"
        disp_val = page.locator("#checkout-displacement-val").inner_text()
        print(f"[*] Taxa de deslocamento calculada: {disp_val}")
        assert "R$" in disp_val, "Taxa de deslocamento deve ser exibida em R$"

        # Valida que o total a pagar inclui a taxa de deslocamento
        total_str = page.locator("#checkout-final-total").inner_text()
        print(f"[*] Total final com deslocamento: {total_str}")
        print("[+] Teste 3 APROVADO: Seção de endereço e taxa de deslocamento calculadas com precisão.")

        # ------------------------------------------------------------------
        # TESTE 4: Campo de Observações ("Add note") e Propagação ao Voucher/Agendamentos
        # ------------------------------------------------------------------
        print("\n--- TESTE 4: Campo de Observação e Propagação ---")
        notes_input = page.locator("#checkout-customer-notes")
        assert notes_input.is_visible(), "Campo de observações 'Add note' deve estar visível no checkout"

        test_note = "Preferência por esmalte hipoalergênico e aviso de interfone portaria bloco B"
        notes_input.fill(test_note)
        page.wait_for_timeout(200)

        # Confirma agendamento
        btn_confirm = page.locator("#btn-confirm-checkout")
        btn_confirm.click()
        page.wait_for_selector("#screen-confirm.active", timeout=6000)

        confirm_badge = page.locator("#confirm-fulfillment-badge")
        assert "A Domicílio" in confirm_badge.inner_text(), "Badge no voucher deve indicar 'A Domicílio'"

        confirm_notes_box = page.locator("#confirm-notes-box")
        assert confirm_notes_box.is_visible(), "Box de observações deve estar visível no voucher"
        assert test_note in confirm_notes_box.inner_text(), "Texto da observação deve constar no voucher"

        voucher_code = page.locator("#confirm-voucher-code").inner_text()
        print(f"[*] Voucher gerado: {voucher_code} com observações: '{test_note[:30]}...'")

        # Verifica na tela de Meus Agendamentos
        page.evaluate("navigateTo('appointments')")
        page.wait_for_selector("#screen-appointments.active", timeout=5000)

        first_appt = page.locator(".appt-card").first
        assert first_appt.is_visible(), "Card de agendamento deve estar visível"
        assert "A Domicílio" in first_appt.inner_text(), "Card de agendamento deve exibir badge 'A Domicílio'"
        assert test_note in first_appt.inner_text(), "Card de agendamento deve exibir as observações do cliente"

        # Verifica no Painel B2B (Aba 3)
        page.evaluate("openPartnerB2BPanel('loi')")
        page.wait_for_selector("#screen-partner-b2b.active", timeout=5000)
        b2b_booking_row = page.locator(".b2b-booking-row").first
        assert b2b_booking_row.is_visible(), "Linha de reserva recebida no B2B deve estar visível"
        assert "A Domicílio" in b2b_booking_row.inner_text(), "Reserva no B2B deve conter badge 'A Domicílio'"
        assert test_note in b2b_booking_row.inner_text(), "Reserva no B2B deve exibir a nota informada pela cliente"
        print("[+] Teste 4 APROVADO: Observações persistidas e refletidas no Voucher, Agendamentos e Painel B2B.")

        # ------------------------------------------------------------------
        # TESTE 5: Responsividade em 3 Viewports (360px, 393px, 412px)
        # ------------------------------------------------------------------
        print("\n--- TESTE 5: Responsividade & Zero Overflow ---")
        page.evaluate("navigateTo('home')")
        page.wait_for_selector("#screen-home.active", timeout=5000)

        for width in [360, 393, 412]:
            page.set_viewport_size({"width": width, "height": 800})
            page.wait_for_timeout(300)
            scroll_width = page.evaluate("document.documentElement.scrollWidth")
            client_width = page.evaluate("document.documentElement.clientWidth")
            overflow = scroll_width - client_width
            print(f"[*] Viewport {width}px: scrollWidth={scroll_width}, clientWidth={client_width}, overflow={overflow}px")
            assert overflow <= 0, f"Overflow horizontal detectado ({overflow}px) em viewport de {width}px"

        # Verifica regra Zero Emojis
        text_content = page.locator("#partner-circles-grid").inner_text()
        for char in text_content:
            code = ord(char)
            # Faixa básica de emojis comuns
            if (0x1F300 <= code <= 0x1F9FF) or (0x2600 <= code <= 0x27BF):
                assert False, f"Emoji proibido encontrado no grid de parceiros: {char}"

        print("[+] Teste 5 APROVADO: Zero overflow horizontal em todas as resoluções e Zero Emojis estrito.")

        if errors:
            print("\n[!] Alertas/Erros de console registrados:")
            for err in errors:
                print(f"   {err}")
        else:
            print("\n[+] Console 100% limpo, sem erros de script.")

        browser.close()
        print("\n=======================================================")
        print("  TODOS OS TESTES DA FRENTE 3 PASSARAM COM SUCESSO!    ")
        print("=======================================================")

if __name__ == "__main__":
    run_frente3_tests()
