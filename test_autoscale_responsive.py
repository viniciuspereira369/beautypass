import asyncio
import os
from playwright.async_api import async_playwright

async def run_autoscale_tests():
    print("==================================================")
    print(" INICIANDO TESTES DO MOTOR DE ESCALA E RESPONSIVO ")
    print("==================================================")
    
    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        
        # ----------------------------------------------------
        # TESTE 1: DESKTOP COM SELETOR DE DISPOSITIVOS
        # ----------------------------------------------------
        print("\n--- 1. TESTE DO MODO DESKTOP E SELETOR DE DISPOSITIVOS ---")
        context_desktop = await browser.new_context(viewport={"width": 1280, "height": 900})
        page = await context_desktop.new_page()
        await page.goto("http://localhost:8089/beautypass_app.html?p=P01", wait_until="networkidle")
        await page.wait_for_timeout(600)
        
        # Verificar presença dos 4 botões de presets
        btn_se = page.locator("#btn-device-se")
        btn_16pro = page.locator("#btn-device-16pro")
        btn_s24 = page.locator("#btn-device-s24")
        btn_auto = page.locator("#btn-device-auto")
        
        assert await btn_se.is_visible(), "Botão iPhone SE deve estar visível no desktop"
        assert await btn_16pro.is_visible(), "Botão iPhone 16 Pro deve estar visível no desktop"
        assert await btn_s24.is_visible(), "Botão Galaxy S24 deve estar visível no desktop"
        assert await btn_auto.is_visible(), "Botão Fluido / Auto deve estar visível no desktop"
        print("[OK] Todos os 4 botões de dispositivos encontrados no controller.")
        
        # Validar preset padrão: iPhone 16 Pro (393px)
        is_16pro_active = await page.evaluate("() => document.getElementById('btn-device-16pro').classList.contains('active')")
        assert is_16pro_active, "iPhone 16 Pro deve ser o preset padrão ativo"
        scale_16pro = await page.evaluate("() => parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--bp-scale'))")
        print(f"[OK] Preset padrão ativo: iPhone 16 Pro (393px), --bp-scale: {scale_16pro}")
        assert abs(scale_16pro - 1.008) < 0.01, f"Escala esperada para 393px é ~1.008, obteve {scale_16pro}"
        
        # Testar clique no iPhone SE (375px)
        await btn_se.click()
        await page.wait_for_timeout(300)
        scale_se = await page.evaluate("() => parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--bp-scale'))")
        shell_width_se = await page.evaluate("() => document.querySelector('.device-shell').offsetWidth")
        print(f"[OK] Selecionou iPhone SE: largura={shell_width_se}px, --bp-scale: {scale_se}")
        assert shell_width_se == 375, f"Largura esperada da moldura é 375px, obteve {shell_width_se}"
        assert abs(scale_se - 0.962) < 0.01, f"Escala esperada para 375px é ~0.962, obteve {scale_se}"
        
        # Testar clique no Galaxy S24 (412px)
        await btn_s24.click()
        await page.wait_for_timeout(300)
        scale_s24 = await page.evaluate("() => parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--bp-scale'))")
        shell_width_s24 = await page.evaluate("() => document.querySelector('.device-shell').offsetWidth")
        print(f"[OK] Selecionou Galaxy S24: largura={shell_width_s24}px, --bp-scale: {scale_s24}")
        assert shell_width_s24 == 412, f"Largura esperada da moldura é 412px, obteve {shell_width_s24}"
        assert abs(scale_s24 - 1.056) < 0.01, f"Escala esperada para 412px é ~1.056, obteve {scale_s24}"
        
        # Testar clique no Fluido / Auto
        await btn_auto.click()
        await page.wait_for_timeout(300)
        is_auto_active = await page.evaluate("() => document.getElementById('btn-device-auto').classList.contains('active')")
        print(f"[OK] Selecionou Fluido / Auto: ativo={is_auto_active}")
        assert is_auto_active, "Modo Fluido deve estar ativo"
        
        # Voltar para iPhone 16 Pro
        await btn_16pro.click()
        await page.wait_for_timeout(300)
        
        # ----------------------------------------------------
        # TESTE 2: ASPECT-RATIO 16/9 E OBJECT-FIT COVER
        # ----------------------------------------------------
        print("\n--- 2. TESTE DE PROPORÇÃO 16/9 E OBJECT-FIT COVER ---")
        # Completar onboarding para ver Home
        if await page.locator("#onboarding-name").is_visible():
            await page.fill("#onboarding-name", "Teste Responsivo")
            await page.fill("#onboarding-phone", "11988887777")
            await page.fill("#onboarding-code", "0000")
            await page.check("#onboarding-terms-check")
            await page.dispatch_event("#onboarding-name", "input")
            await page.dispatch_event("#onboarding-phone", "input")
            await page.dispatch_event("#onboarding-code", "input")
            await page.dispatch_event("#onboarding-terms-check", "change")
            await page.click("#onboarding-submit-btn")
            await page.wait_for_selector("#screen-home.active", timeout=5000)
        
        # Banner promocional (retention-banner)
        retention_banner_ratio = await page.evaluate("""() => {
            const el = document.querySelector('.retention-banner');
            const style = window.getComputedStyle(el);
            return style.aspectRatio;
        }""")
        print(f"[OK] aspect-ratio .retention-banner: '{retention_banner_ratio}'")
        assert "16" in retention_banner_ratio and "9" in retention_banner_ratio, "Retention banner deve ter aspect-ratio 16 / 9"
        
        # Fotos de salões no feed (.card-media-wrap)
        card_media_info = await page.evaluate("""() => {
            const wrap = document.querySelector('.card-media-wrap');
            const img = wrap.querySelector('img');
            const wrapStyle = window.getComputedStyle(wrap);
            const imgStyle = window.getComputedStyle(img);
            return {
                wrapRatio: wrapStyle.aspectRatio,
                imgRatio: imgStyle.aspectRatio,
                imgFit: imgStyle.objectFit
            };
        }""")
        print(f"[OK] Feed card media: wrapRatio='{card_media_info['wrapRatio']}', objectFit='{card_media_info['imgFit']}'")
        assert "16" in card_media_info['wrapRatio'] and "9" in card_media_info['wrapRatio'], "Card media wrap deve ter aspect-ratio 16 / 9"
        assert card_media_info['imgFit'] == "cover", "Imagem do salão deve ter object-fit cover"
        
        # Navegar para detalhe do salão
        await page.locator("#social-feed-container .card-action-btn").first.click()
        await page.wait_for_selector("#screen-detail.active", timeout=5000)
        
        # Foto de capa do salão no detalhe (.hero-cover & #detail-hero-img)
        hero_cover_info = await page.evaluate("""() => {
            const cover = document.querySelector('.hero-cover');
            const img = document.getElementById('detail-hero-img');
            const coverStyle = window.getComputedStyle(cover);
            const imgStyle = window.getComputedStyle(img);
            return {
                coverRatio: coverStyle.aspectRatio,
                imgFit: imgStyle.objectFit
            };
        }""")
        print(f"[OK] Detail hero cover: coverRatio='{hero_cover_info['coverRatio']}', objectFit='{hero_cover_info['imgFit']}'")
        assert "16" in hero_cover_info['coverRatio'] and "9" in hero_cover_info['coverRatio'], "Hero cover deve ter aspect-ratio 16 / 9"
        assert hero_cover_info['imgFit'] == "cover", "Hero image deve ter object-fit cover"
        
        # Mini-galeria de fotos (.gallery-thumb-item)
        thumb_info = await page.evaluate("""() => {
            const thumb = document.querySelector('.gallery-thumb-item');
            const img = thumb.querySelector('img');
            const thumbStyle = window.getComputedStyle(thumb);
            const imgStyle = window.getComputedStyle(img);
            return {
                thumbRatio: thumbStyle.aspectRatio,
                imgFit: imgStyle.objectFit
            };
        }""")
        print(f"[OK] Gallery thumb: thumbRatio='{thumb_info['thumbRatio']}', objectFit='{thumb_info['imgFit']}'")
        assert "16" in thumb_info['thumbRatio'] and "9" in thumb_info['thumbRatio'], "Gallery thumb deve ter aspect-ratio 16 / 9"
        assert thumb_info['imgFit'] == "cover", "Gallery thumb img deve ter object-fit cover"
        
        await context_desktop.close()
        
        # ----------------------------------------------------
        # TESTE 3: LIMITES RÍGIDOS DA ESCALA (0.85x a 1.15x)
        # ----------------------------------------------------
        print("\n--- 3. TESTE DOS LIMITES RÍGIDOS DO MOTOR DE ESCALA ---")
        # Tela ultra compacta (320px): deve travar em exatamente 0.85x
        context_320 = await browser.new_context(viewport={"width": 320, "height": 640}, is_mobile=True)
        page_320 = await context_320.new_page()
        await page_320.goto("http://localhost:8089/beautypass_app.html?p=P01", wait_until="networkidle")
        await page_320.wait_for_timeout(400)
        scale_320 = await page_320.evaluate("() => parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--bp-scale'))")
        print(f"[OK] Tela compacta (320px): --bp-scale={scale_320} (esperado: 0.850 exatamente)")
        assert scale_320 == 0.85, f"Escala deve travar em 0.850 para tela de 320px, obteve {scale_320}"
        
        # Tela ultra larga / tablet / dobrável aberto (768px): deve travar em exatamente 1.15x
        context_tablet = await browser.new_context(
            viewport={"width": 768, "height": 1024},
            is_mobile=True,
            has_touch=True,
            user_agent="Mozilla/5.0 (iPad; CPU OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1"
        )
        page_tablet = await context_tablet.new_page()
        await page_tablet.goto("http://localhost:8089/beautypass_app.html", wait_until="networkidle")
        await page_tablet.wait_for_timeout(400)
        scale_tablet = await page_tablet.evaluate("() => parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--bp-scale'))")
        print(f"[OK] Tela larga / tablet (768px): --bp-scale={scale_tablet} (esperado: 1.150 exatamente)")
        assert scale_tablet == 1.15, f"Escala deve travar em 1.150 para tablet/phablet, obteve {scale_tablet}"
        
        # ----------------------------------------------------
        # TESTE 4: ÁREA MÍNIMA DE TOQUE (44x44px) SOB ESCALA 0.85x
        # ----------------------------------------------------
        print("\n--- 4. TESTE DE ÁREA MÍNIMA DE TOQUE SOB ESCALA MÍNIMA (0.85x) ---")
        # Concluir onboarding na tela compacta de 320px
        await page_320.fill("#onboarding-name", "Touch Test")
        await page_320.fill("#onboarding-phone", "11988887777")
        await page_320.fill("#onboarding-code", "0000")
        await page_320.check("#onboarding-terms-check")
        await page_320.dispatch_event("#onboarding-name", "input")
        await page_320.dispatch_event("#onboarding-phone", "input")
        await page_320.dispatch_event("#onboarding-code", "input")
        await page_320.dispatch_event("#onboarding-terms-check", "change")
        await page_320.click("#onboarding-submit-btn")
        await page_320.wait_for_selector("#screen-home.active", timeout=5000)
        
        # Avaliar bounding box dos botões da barra inferior de navegação
        nav_items = await page_320.query_selector_all(".nav-item")
        for i, item in enumerate(nav_items):
            box = await item.bounding_box()
            assert box is not None, f"Nav item {i} deve ter bounding box"
            print(f"  Nav item {i}: largura={box['width']:.1f}px, altura={box['height']:.1f}px")
            assert box['height'] >= 44.0, f"Altura da aba {i} deve ser >= 44px (obteve {box['height']:.1f}px)"
            assert box['width'] >= 44.0, f"Largura da aba {i} deve ser >= 44px (obteve {box['width']:.1f}px)"
        print("[OK] Todas as abas da barra inferior atendem área mínima de toque >= 44x44px sob 0.85x.")
        
        # Avaliar botão de ação do card de salão (.card-action-btn)
        await page_320.wait_for_selector("#social-feed-container .card-action-btn", state="visible", timeout=5000)
        card_btn = page_320.locator("#social-feed-container .card-action-btn").first
        card_btn_box = await card_btn.bounding_box()
        assert card_btn_box is not None
        print(f"  Card Action Btn: altura={card_btn_box['height']:.1f}px, largura={card_btn_box['width']:.1f}px")
        assert card_btn_box['height'] >= 44.0, f"Botão de ação do card deve ter altura >= 44px (obteve {card_btn_box['height']:.1f}px)"
        print("[OK] Botão de ação do card atende altura mínima de toque >= 44px.")
        
        # Avaliar botão de favorito do card (.card-fav-btn)
        fav_btn = page_320.locator(".card-fav-btn").first
        fav_box = await fav_btn.bounding_box()
        assert fav_box is not None
        print(f"  Card Fav Btn: largura={fav_box['width']:.1f}px, altura={fav_box['height']:.1f}px")
        assert fav_box['width'] >= 44.0, f"Botão de favorito deve ter largura >= 44px (obteve {fav_box['width']:.1f}px)"
        assert fav_box['height'] >= 44.0, f"Botão de favorito deve ter altura >= 44px (obteve {fav_box['height']:.1f}px)"
        print("[OK] Botão de favorito atende área mínima de toque >= 44x44px sob 0.85x.")
        
        # Entrar no detalhe e avaliar botão de voltar (.back-circle-btn)
        await card_btn.click()
        await page_320.wait_for_selector("#screen-detail.active", timeout=5000)
        back_btn = page_320.locator(".back-circle-btn").first
        back_box = await back_btn.bounding_box()
        assert back_box is not None
        print(f"  Back Circle Btn: largura={back_box['width']:.1f}px, altura={back_box['height']:.1f}px")
        assert back_box['width'] >= 44.0, f"Botão voltar deve ter largura >= 44px (obteve {back_box['width']:.1f}px)"
        assert back_box['height'] >= 44.0, f"Botão voltar deve ter altura >= 44px (obteve {back_box['height']:.1f}px)"
        print("[OK] Botão de voltar atende área mínima de toque >= 44x44px sob 0.85x.")
        
        await context_320.close()
        await context_tablet.close()
        await browser.close()
        
    print("\n=======================================================")
    print(" TODOS OS TESTES DO MOTOR DE ESCALA PASSARAM COM 100%! ")
    print("=======================================================")

if __name__ == "__main__":
    asyncio.run(run_autoscale_tests())
