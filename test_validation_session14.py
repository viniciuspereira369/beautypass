import sys
from playwright.sync_api import sync_playwright

def run_session14_tests():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        print("\n--- INICIANDO VALIDAÇÃO COMPLETA DA SESSÃO 14 ---")

        # 1. Login e acesso à Home
        page.goto("http://localhost:8089/beautypass_app.html?p=P14_TEST")
        page.wait_for_selector("#onboarding-participant")
        page.fill("#onboarding-name", "Usuário Teste Sessão 14")
        page.fill("#onboarding-phone", "11999998888")
        page.fill("#onboarding-code", "0000")
        page.check("#onboarding-terms-check")
        page.dispatch_event("#onboarding-name", "input")
        page.dispatch_event("#onboarding-phone", "input")
        page.dispatch_event("#onboarding-code", "input")
        page.dispatch_event("#onboarding-terms-check", "change")
        page.click("#onboarding-submit-btn")
        page.wait_for_selector("#screen-home.active", timeout=5000)
        print("[OK] Teste 1: Autenticação Onboarding e chegada na Home.")

        # 2. Testar Favoritar no Feed
        first_fav_btn = page.locator("#social-feed-container .card-fav-btn").first
        first_salon_name = page.locator("#social-feed-container .salon-name").first.inner_text().strip()
        first_fav_btn.click()
        page.wait_for_timeout(400)
        
        # Verificar classe active
        has_active = "active" in (first_fav_btn.get_attribute("class") or "")
        print(f"[OK] Teste 2a: Salão '{first_salon_name}' favoritado. Botão ativo: {has_active}")
        assert has_active, "Botão de favorito deve ter a classe .active"

        # Verificar localStorage
        stored_favs = page.evaluate("() => JSON.parse(localStorage.getItem('bp_favorite_salons') || '[]')")
        print(f"[OK] Teste 2b: localStorage 'bp_favorite_salons': {stored_favs}")
        assert len(stored_favs) >= 1, "Salão deve estar salvo no localStorage"

        # Filtrar por Favoritos
        page.click('.filter-chip[data-filter="favorites"]')
        page.wait_for_timeout(400)
        visible_cards = page.query_selector_all("#social-feed-container .social-salon-card")
        status_bar_text = page.inner_text("#feed-status-bar")
        print(f"[OK] Teste 2c: Filtro 'Favoritos' aplicado. Cards visíveis: {len(visible_cards)}. Status: '{status_bar_text}'")
        assert len(visible_cards) == len(stored_favs), "Feed deve exibir apenas os salões favoritados"
        assert "favoritos" in status_bar_text.lower(), "Status bar deve indicar filtro de Favoritos"

        # Voltar para Todos
        page.click('.filter-chip[data-filter="all"]')
        page.wait_for_timeout(300)

        # 3. Testar Detalhe do Salão e Mini-Galeria
        page.locator("#social-feed-container .card-action-btn").first.click()
        page.wait_for_selector("#screen-detail.active", timeout=5000)
        
        # Verificar botão de favorito no detalhe
        detail_fav_active = page.evaluate("() => document.getElementById('detail-fav-btn').classList.contains('active')")
        print(f"[OK] Teste 3a: Botão de favorito no cabeçalho do detalhe ativo: {detail_fav_active}")
        assert detail_fav_active, "Botão de favorito no detalhe deve refletir o estado favoritado"

        # Verificar Mini-Galeria
        gallery_container = page.locator("#detail-photo-gallery")
        assert gallery_container.is_visible(), "Mini-galeria deve estar visível no detalhe"
        thumbs = page.query_selector_all(".gallery-thumb-item")
        print(f"[OK] Teste 3b: Mini-galeria renderizada com {len(thumbs)} miniaturas.")
        assert len(thumbs) == 4, f"Esperado 4 miniaturas na galeria, obtido {len(thumbs)}"

        # Testar troca de foto principal ao clicar na 2ª miniatura
        initial_hero_src = page.get_attribute("#detail-hero-img", "src")
        page.locator(".gallery-thumb-item").nth(1).click()
        page.wait_for_timeout(400)
        new_hero_src = page.get_attribute("#detail-hero-img", "src")
        thumb1_active = page.evaluate("() => document.querySelectorAll('.gallery-thumb-item')[1].classList.contains('active')")
        print(f"[OK] Teste 3c: Troca de foto na galeria. Hero inicial: {initial_hero_src[:40]}... -> Novo: {new_hero_src[:40]}... (Thumb 2 ativo: {thumb1_active})")
        assert initial_hero_src != new_hero_src, "Foto principal deve mudar após clique na miniatura"
        assert thumb1_active, "Segunda miniatura deve estar ativa"

        # 4. Agendamento e Seleção de Horário
        empty_banner = page.locator("#detail-empty-slots-banner")
        if empty_banner.is_visible():
            print("Dia sem slots (ex: Domingo). Clicando no próximo dia disponível...")
            page.click("#detail-empty-slots-banner .empty-slots-action-btn")
            page.wait_for_selector(".radial-clock-container", state="visible", timeout=3000)

        # Clicar no botão 'Continuar com este Horário'
        page.locator("#screen-detail .card-action-btn").first.click()
        page.wait_for_selector("#screen-checkout.active", timeout=5000)
        print("[OK] Teste 4: Navegou para a tela de Checkout.")

        # 5. Validação da Copy Exata do Checkout (Seção 6.2)
        notice_text = page.inner_text("#checkout-notice-text").strip()
        print(f"[OK] Teste 5: Copy do Checkout: '{notice_text}'")
        expected_copy = "Pagamento simulado — nenhum valor será cobrado neste teste."
        assert expected_copy in notice_text, f"Copy esperada '{expected_copy}' não encontrada em '{notice_text}'"

        # 6. Concluir Agendamento e Validar QR Code no Voucher Digital
        page.click("#btn-confirm-checkout")
        # Espera processamento simulado de 1.4s + rede
        page.wait_for_selector("#screen-confirm.active", timeout=10000)
        print("[OK] Teste 6a: Agendamento confirmado com sucesso!")

        # Validar SVG do QR Code
        qr_svg = page.locator("#confirm-qrcode-frame svg")
        assert qr_svg.is_visible(), "SVG do QR Code deve estar visível no voucher"
        qr_code_text = page.inner_text("#confirm-qrcode-voucher-code").strip()
        voucher_code_displayed = page.inner_text("#confirm-voucher-code").strip()
        print(f"[OK] Teste 6b: QR Code SVG renderizado. Código no QR: '{qr_code_text}', Voucher: '{voucher_code_displayed}'")
        assert voucher_code_displayed in qr_code_text, "Código no QR Code deve coincidir com o voucher gerado"

        # 7. Validar Tela 'Meus Agendamentos' com Botão 'Ver QR Code & Voucher'
        page.click(".nav-item[data-screen='appointments']")
        page.wait_for_selector("#screen-appointments.active", timeout=5000)
        appt_card = page.locator("#appointments-list-container .appt-card").first
        appt_action_btn = appt_card.locator(".appt-btn-outline").first
        btn_text = appt_action_btn.inner_text().strip()
        print(f"[OK] Teste 7a: Card de agendamento ativo. Texto do botão: '{btn_text}'")
        assert "Ver QR Code & Voucher" in btn_text, f"Botão deve conter 'Ver QR Code & Voucher', obtido '{btn_text}'"

        # Clicar no botão para voltar ao voucher com QR Code
        appt_action_btn.click()
        page.wait_for_selector("#screen-confirm.active", timeout=5000)
        assert page.locator("#confirm-qrcode-frame svg").is_visible(), "QR Code deve estar visível ao abrir o voucher por Meus Agendamentos"
        print("[OK] Teste 7b: Retorno ao Voucher e QR Code a partir de Meus Agendamentos validado com sucesso!")

        # 8. Validar Tempo a Pé no Mapa Geral (Bottom Sheet)
        page.click(".nav-item[data-screen='map']")
        page.wait_for_selector("#screen-map.active", timeout=5000)
        page.wait_for_timeout(500)
        walk_badges = page.query_selector_all("#sheet-salons-list .mini-card-walk")
        print(f"[OK] Teste 8: Badges de caminhada a pé no Mapa Geral: {len(walk_badges)} encontrados.")
        assert len(walk_badges) > 0, "Deverá haver badges de tempo a pé no Bottom Sheet do mapa"
        walk_text = walk_badges[0].inner_text().strip()
        print(f"       Exemplo de badge: '{walk_text}'")
        assert "min a pé" in walk_text, f"Badge deve conter 'min a pé', obtido '{walk_text}'"

        print("\n=======================================================")
        print(" TODAS AS 8 ESPECIFICAÇÕES DA SESSÃO 14 FORAM APROVADAS!")
        print("=======================================================\n")
        browser.close()

if __name__ == "__main__":
    run_session14_tests()
