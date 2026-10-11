import asyncio
from playwright.async_api import async_playwright

async def run_story_bubbles_tests():
    print("==================================================")
    print(" INICIANDO TESTES DOS STORY BUBBLES E SOCIAL PROOF")
    print("==================================================")

    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        context = await browser.new_context(viewport={"width": 393, "height": 852})
        page = await context.new_page()

        print("\n--- 1. CARREGANDO HOME E VERIFICANDO STORY BUBBLES ---")
        await page.goto("http://localhost:8089/beautypass_app.html?p=P01", wait_until="networkidle")
        await page.wait_for_timeout(600)

        # Login caso onboarding esteja visível
        if await page.locator("#onboarding-name").is_visible():
            await page.fill("#onboarding-name", "Camila Teste")
            await page.fill("#onboarding-phone", "11988887777")
            await page.fill("#onboarding-code", "0000")
            await page.check("#onboarding-terms-check")
            await page.dispatch_event("#onboarding-name", "input")
            await page.dispatch_event("#onboarding-phone", "input")
            await page.dispatch_event("#onboarding-code", "input")
            await page.dispatch_event("#onboarding-terms-check", "change")
            await page.click("#onboarding-submit-btn")
            await page.wait_for_selector("#screen-home.active", timeout=5000)

        # Verificar presença do container dos Story Bubbles
        carousel = page.locator(".story-bubbles-carousel")
        assert await carousel.is_visible(), "Carrossel de Story Bubbles deve estar visível"
        print("[OK] Carrossel .story-bubbles-carousel visível.")

        bubbles = page.locator(".story-bubble")
        bubble_count = await bubbles.count()
        assert bubble_count == 6, f"Esperado 6 story bubbles, encontrado {bubble_count}"
        print(f"[OK] 6 Story Bubbles encontrados (Todos, Cabelo, Barba, Unhas, Massagem, Estética).")

        # Verificar anéis graduados e imagens
        rings = page.locator(".story-bubble .story-ring")
        assert await rings.count() == 6, "Cada bolha deve conter um .story-ring"
        photos = page.locator(".story-bubble .story-photo-wrap img")
        assert await photos.count() == 6, "Cada bolha deve conter uma imagem fotográfica"
        print("[OK] Estrutura interna (.story-ring + .story-photo-wrap img) validada.")

        # Verificar que a bolha 'Todos' está ativa por padrão
        active_bubble = page.locator(".story-bubble.active")
        active_cat = await active_bubble.get_attribute("data-category")
        assert active_cat == "all", f"Bolha inicial ativa deve ser 'all', obtido: {active_cat}"
        print("[OK] Bolha 'all' ativa inicialmente por padrão.")

        cards_all = await page.locator(".social-salon-card").count()
        print(f"[OK] Total de salões com filtro 'all': {cards_all}")
        assert cards_all == 24, f"Esperado 24 salões para 'all', obtido {cards_all}"

        # ----------------------------------------------------
        # TESTE 2: FILTRAGEM REATIVA POR CATEGORIA
        # ----------------------------------------------------
        categories_to_test = [
            ("hair", "Cabelo", 6),
            ("barber", "Barba", 5),
            ("nails", "Unhas", 8),
            ("massage", "Massagem", 5),
            ("esthetic", "Estética", 10),
        ]

        for cat_key, label, expected_count in categories_to_test:
            print(f"\n--- Testando clique na bolha '{label}' ({cat_key}) ---")
            btn = page.locator(f".story-bubble[data-category='{cat_key}']")
            assert await btn.is_visible(), f"Botão da bolha {cat_key} deve estar visível"
            await btn.click()
            await page.wait_for_timeout(300)

            # Verificar classe active e aria-pressed
            is_active = await btn.evaluate("el => el.classList.contains('active')")
            aria_pressed = await btn.get_attribute("aria-pressed")
            assert is_active, f"Bolha {cat_key} deve ter classe active após clique"
            assert aria_pressed == "true", f"Bolha {cat_key} deve ter aria-pressed='true'"

            # Verificar contagem de cards filtrados
            filtered_cards = await page.locator(".social-salon-card").count()
            print(f"[OK] Cards exibidos para {label}: {filtered_cards} (esperado: {expected_count})")
            assert filtered_cards == expected_count, f"Esperado {expected_count} cards para {cat_key}, obtido {filtered_cards}"

            # Verificar barra de status
            status_bar = page.locator("#feed-status-bar")
            assert await status_bar.is_visible(), "Barra de status do feed deve estar visível com filtro ativo"
            status_text = await status_bar.inner_text()
            assert label in status_text, f"Texto da barra de status deve mencionar '{label}'"

        # Voltar para 'Todos'
        print("\n--- Testando retorno para a bolha 'Todos' ---")
        btn_all = page.locator(".story-bubble[data-category='all']")
        await btn_all.click()
        await page.wait_for_timeout(300)
        cards_reset = await page.locator(".social-salon-card").count()
        assert cards_reset == 24, f"Esperado 24 salões ao resetar para 'all', obtido {cards_reset}"
        print(f"[OK] Retorno para 'all' restaurou todos os 24 salões.")

        # ----------------------------------------------------
        # TESTE 3: PROVAS SOCIAIS E DISTÂNCIA A PÉ NOS CARDS
        # ----------------------------------------------------
        print("\n--- 3. VERIFICAÇÃO DOS ELEMENTOS DE PROVA SOCIAL ---")
        first_card = page.locator(".social-salon-card").first

        # Prova social em tempo real com pulso
        social_proof = first_card.locator(".social-proof-pill")
        assert await social_proof.is_visible(), "social-proof-pill deve estar visível"
        pulse_dot = first_card.locator(".live-pulse-dot")
        assert await pulse_dot.is_visible(), "live-pulse-dot deve estar visível no pill"
        sp_text = await social_proof.inner_text()
        print(f"[OK] Prova social em tempo real: '{sp_text}'")

        # Badge de caminhada a pé
        eta_badge = first_card.locator(".card-eta-badge")
        assert await eta_badge.is_visible(), "card-eta-badge deve estar visível"
        eta_text = await eta_badge.inner_text()
        print(f"[OK] Badge de caminhada a pé: '{eta_text}'")
        assert "min a pé" in eta_text, f"Badge deve conter 'min a pé', obtido: '{eta_text}'"
        assert "km" in eta_text, f"Badge deve conter quilometragem 'km', obtido: '{eta_text}'"

        # Selo de verificado no nome do salão
        verified = first_card.locator(".verified-badge")
        assert await verified.is_visible(), "verified-badge deve estar visível no nome"
        print("[OK] Selo de verificado presente no cabeçalho do salão.")

        # Selo do responsável técnico / fundador
        owner_seal = first_card.locator(".card-owner-seal")
        assert await owner_seal.is_visible(), "card-owner-seal deve estar visível"
        owner_name = await first_card.locator(".owner-seal-name").inner_text()
        owner_role = await first_card.locator(".owner-seal-role").inner_text()
        print(f"[OK] Selo do responsável: '{owner_name}' ({owner_role})")

        # Cluster de amigas em comum
        mutual_friends = first_card.locator(".card-mutual-friends")
        assert await mutual_friends.is_visible(), "card-mutual-friends deve estar visível"
        mutual_avatars = await first_card.locator(".mutual-avatar-item").count()
        assert mutual_avatars >= 2, f"Esperado >= 2 avatares de amigas, obtido {mutual_avatars}"
        mutual_text = await first_card.locator(".mutual-friends-text").inner_text()
        print(f"[OK] Rede de amigas em comum ({mutual_avatars} avatares): '{mutual_text}'")

        # ----------------------------------------------------
        # TESTE 4: ZERO HORIZONTAL OVERFLOW EM TODOS OS PRESETS
        # ----------------------------------------------------
        print("\n--- 4. TESTE DE ZERO OVERFLOW HORIZONTAL NOS PRESETS ---")
        device_viewports = [
            ("iPhone SE", 375, 667),
            ("iPhone 16 Pro", 393, 852),
            ("Galaxy S24", 412, 915),
            ("Narrow Compact", 360, 740),
        ]

        for dev_name, w, h in device_viewports:
            await page.set_viewport_size({"width": w, "height": h})
            await page.wait_for_timeout(300)

            # Verificar se há overflow horizontal no app-viewport
            overflow_info = await page.evaluate("""() => {
                const viewport = document.querySelector('.app-viewport');
                const home = document.querySelector('#screen-home');
                return {
                    viewportScrollWidth: viewport.scrollWidth,
                    viewportClientWidth: viewport.clientWidth,
                    homeScrollWidth: home.scrollWidth,
                    homeClientWidth: home.clientWidth,
                    hasHorizontalOverflow: viewport.scrollWidth > viewport.clientWidth + 2
                };
            }""")
            print(f"[OK] {dev_name} ({w}x{h}): scrollWidth={overflow_info['viewportScrollWidth']}, clientWidth={overflow_info['viewportClientWidth']}, overflow={overflow_info['hasHorizontalOverflow']}")
            assert not overflow_info['hasHorizontalOverflow'], f"Detectado overflow horizontal em {dev_name}!"

        # Restaurar viewport padrão iPhone 16 Pro
        await page.set_viewport_size({"width": 393, "height": 852})
        await page.wait_for_timeout(200)

        # ----------------------------------------------------
        # TESTE 5: BUSCA E FILTRAGEM INSENSÍVEL A ACENTOS
        # ----------------------------------------------------
        print("\n--- 5. TESTE DE BUSCA E FILTRAGEM INSENSÍVEL A ACENTOS ---")
        # Digitar 'atelie' (sem acento circunflexo) na barra de busca
        search_input = page.locator("#home-search-input")
        await search_input.fill("atelie")
        await page.wait_for_timeout(300)

        atelie_cards = await page.locator(".social-salon-card").count()
        print(f"[OK] Busca por 'atelie' (sem acento) retornou {atelie_cards} salão(ões).")
        assert atelie_cards >= 1, "Busca sem acento 'atelie' deve encontrar 'Ateliê Belle Époque'"
        first_name = await page.locator(".social-salon-card .salon-name").first.inner_text()
        assert "Ateliê Belle Époque" in first_name, f"Esperado 'Ateliê Belle Époque', obtido '{first_name}'"

        # Combinar busca com categoria
        await page.locator(".story-bubble[data-category='hair']").click()
        await page.wait_for_timeout(300)
        status_text_comb = await page.locator("#feed-status-bar").inner_text()
        print(f"[OK] Status bar com busca e categoria combinadas: '{status_text_comb}'")
        assert "Cabelo" in status_text_comb and "atelie" in status_text_comb

        # Limpar todos os filtros via botão Limpar do status bar
        await page.locator(".feed-status-clear").click()
        await page.wait_for_timeout(700)
        cards_after_clear = await page.locator(".social-salon-card").count()
        assert cards_after_clear == 24, "Limpar filtros deve restaurar todos os 24 salões"
        carousel_scroll_left = await page.evaluate("() => document.querySelector('.story-bubbles-carousel').scrollLeft")
        print(f"[OK] Limpar filtros restaurou carrossel para scrollLeft={carousel_scroll_left}")
        assert carousel_scroll_left == 0, "Carrossel deve voltar para a esquerda ao limpar filtros"

        # ----------------------------------------------------
        # TESTE 6: ACESSIBILIDADE WCAG (FOCO VISÍVEL NAS BOLHAS)
        # ----------------------------------------------------
        print("\n--- 6. TESTE DE ACESSIBILIDADE WCAG DE FOCO NAS BOLHAS ---")
        has_focus_visible_rule = await page.evaluate("""() => {
            for (let sheet of document.styleSheets) {
                try {
                    for (let rule of sheet.cssRules) {
                        if (rule.selectorText && rule.selectorText.includes('.story-bubble:focus-visible')) {
                            return true;
                        }
                    }
                } catch(e) {}
            }
            return false;
        }""")
        print(f"[OK] Regra .story-bubble:focus-visible definida no CSS: {has_focus_visible_rule}")
        assert has_focus_visible_rule, "Deve haver regra :focus-visible para conformidade com WCAG 2.4.7"

        # ----------------------------------------------------
        # TESTE 7: RESOLUÇÃO DE SINÔNIMOS EM PORTUGUÊS
        # ----------------------------------------------------
        print("\n--- 7. TESTE DE SINÔNIMOS DE CATEGORIA EM PORTUGUÊS ---")
        synonym_cases = [
            ("cabelo", "hair", 6),
            ("barba", "barber", 5),
            ("unhas", "nails", 8),
            ("massagem", "massage", 5),
            ("estetica", "esthetic", 10),
        ]
        for pt_key, canonical_key, expected_count in synonym_cases:
            await page.evaluate(f"k => selectHomeCategory(k)", pt_key)
            await page.wait_for_timeout(200)
            bubble_active = page.locator(f".story-bubble[data-category='{canonical_key}'].active")
            assert await bubble_active.count() == 1, f"Bolha '{canonical_key}' deve estar ativa ao chamar '{pt_key}'"
            cards_count = await page.locator(".social-salon-card").count()
            assert cards_count == expected_count, f"Esperado {expected_count} cards para '{pt_key}', obtido {cards_count}"
            print(f"[OK] Sinônimo '{pt_key}' ativou bolha '{canonical_key}' com {cards_count} estabelecimentos.")

        # Restaurar 'all'
        await page.evaluate("() => selectHomeCategory('all')")
        await page.wait_for_timeout(200)

        # ----------------------------------------------------
        # TESTE 8: CLEARANCE GAP ENTRE SOCIAL PROOF E ETA BADGE EM 360PX
        # ----------------------------------------------------
        print("\n--- 8. TESTE DE CLEARANCE GAP ENTRE BADGES EM 360PX ---")
        await page.set_viewport_size({"width": 360, "height": 740})
        await page.wait_for_timeout(300)
        gap_info = await page.evaluate("""() => {
            const firstCard = document.querySelector('.social-salon-card');
            const spPill = firstCard.querySelector('.social-proof-pill');
            const etaBadge = firstCard.querySelector('.card-eta-badge');
            const spRect = spPill.getBoundingClientRect();
            const etaRect = etaBadge.getBoundingClientRect();
            const gap = etaRect.left - spRect.right;
            return {
                spRight: spRect.right,
                etaLeft: etaRect.left,
                gap: gap,
                isOverlapping: spRect.right > etaRect.left
            };
        }""")
        print(f"[OK] 360px badges clearance gap: {gap_info['gap']:.1f}px (overlap: {gap_info['isOverlapping']})")
        assert not gap_info['isOverlapping'], f"Badges colidiram em 360px! gap={gap_info['gap']}"
        assert gap_info['gap'] >= 4.0, f"Gap mínimo entre badges deve ser >= 4px, obtido: {gap_info['gap']}"

        # Restaurar viewport
        await page.set_viewport_size({"width": 393, "height": 852})

        # ----------------------------------------------------
        # TESTE 9: VALIDAÇÃO DO PERFIL LIMPO E ENXUTO (SEM CARD DE SALDO)
        # ----------------------------------------------------
        print("\n--- 9. TESTE DO PERFIL ENXUTO (SEM SALDO DE CRÉDITOS) ---")
        await page.click(".nav-item[data-screen='profile']")
        await page.wait_for_selector("#screen-profile.active", timeout=3000)
        vip_card_count = await page.locator(".vip-club-glass-card").count()
        assert vip_card_count == 0, "vip-club-glass-card não deve existir no perfil após remoção solicitada"
        print("[OK] Card de saldo em créditos removido com sucesso (count=0).")
        pwa_card = page.locator("#pwa-settings-card")
        assert await pwa_card.is_visible(), "Card de configurações PWA deve estar visível no perfil"
        profile_name = await page.locator("#profile-display-name").inner_text()
        print(f"[OK] Perfil limpo e direto validado com usuária ativa: '{profile_name}'")

        # Voltar para Home
        await page.click(".nav-item[data-screen='home']")
        await page.wait_for_selector("#screen-home.active", timeout=3000)

        print("\n=======================================================")
        print(" TODOS OS TESTES DOS STORY BUBBLES PASSARAM COM 100%! ")
        print("=======================================================")

        await browser.close()

if __name__ == "__main__":
    asyncio.run(run_story_bubbles_tests())
