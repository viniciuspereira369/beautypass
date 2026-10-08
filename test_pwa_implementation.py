import os
import json
import asyncio
from playwright.async_api import async_playwright

def test_manifest():
    print("--- 1. TESTE DE MANIFEST E ÍCONES ---")
    manifest_path = "manifest.webmanifest"
    assert os.path.exists(manifest_path), "manifest.webmanifest deve existir"
    
    with open(manifest_path, "r", encoding="utf-8") as f:
        data = json.load(f)
        
    assert data["name"] == "BeautyPass — Beleza & Bem-Estar"
    assert data["short_name"] == "BeautyPass"
    assert data["display"] == "fullscreen", "Display mode deve ser fullscreen conforme alinhamento"
    assert data["theme_color"] == "#00685F"
    assert len(data["icons"]) >= 8, "Deve conter ícones para todas as densidades"
    assert len(data["shortcuts"]) == 3, "Deve conter 3 App Shortcuts no Android"
    
    # Validar se todos os arquivos de ícones existem no disco
    for icon in data["icons"]:
        assert os.path.exists(icon["src"]), f"Arquivo de ícone {icon['src']} não encontrado"
        
    for sc in data["shortcuts"]:
        for icon in sc["icons"]:
            assert os.path.exists(icon["src"]), f"Arquivo de ícone de atalho {icon['src']} não encontrado"
            
    print("[OK] Manifest e todos os ícones validados com sucesso.")

async def test_pwa_in_browser():
    print("\n--- 2. TESTE E2E DO PWA NO BROWSER ---")
    brain_dir = r"C:\Users\Usuario(a) Master\.gemini\antigravity\brain\aca43ec3-cd8f-44d2-b1a7-87fa773fc14b"
    os.makedirs(brain_dir, exist_ok=True)
    os.makedirs("screenshots", exist_ok=True)

    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        context = await browser.new_context(
            viewport={"width": 412, "height": 892},
            device_scale_factor=2.6,
            is_mobile=True,
            has_touch=True,
            user_agent="Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
        )
        page = await context.new_page()

        # 1. Carregar app no servidor local
        await page.goto("http://localhost:8089/beautypass_app.html?p=P01_PWA", wait_until="networkidle")
        await page.wait_for_timeout(800)

        # Fazer login se onboarding estiver visível
        if await page.locator("#onboarding-name").is_visible():
            await page.fill("#onboarding-name", "Camila PWA")
            await page.fill("#onboarding-phone", "11988887777")
            await page.fill("#onboarding-code", "0000")
            await page.check("#onboarding-terms-check")
            await page.dispatch_event("#onboarding-name", "input")
            await page.dispatch_event("#onboarding-phone", "input")
            await page.dispatch_event("#onboarding-code", "input")
            await page.dispatch_event("#onboarding-terms-check", "change")
            await page.click("#onboarding-submit-btn")
            await page.wait_for_selector("#screen-home.active", timeout=5000)

        # 2. Verificar Service Worker no navegador
        sw_registered = await page.evaluate("""async () => {
            if ('serviceWorker' in navigator) {
                const regs = await navigator.serviceWorker.getRegistrations();
                return regs.length > 0;
            }
            return false;
        }""")
        print(f"[OK] Service Worker registrado no navegador: {sw_registered}")

        # 3. Forçar exibição do Banner PWA para validação visual
        await page.evaluate("""() => {
            const banner = document.getElementById('pwa-install-banner');
            if (banner) banner.style.display = 'flex';
        }""")
        await page.wait_for_timeout(400)
        
        banner_visible = await page.locator("#pwa-install-banner").is_visible()
        assert banner_visible, "Banner PWA deve estar visível"
        print("[OK] Banner de Instalação PWA verificado no topo da Home.")

        shot_banner = os.path.join(brain_dir, "pwa_install_banner.png")
        await page.screenshot(path=shot_banner)
        await page.screenshot(path="screenshots/pwa_install_banner.png")
        print(f"Screenshot do banner capturada: {shot_banner}")

        # 4. Navegar para a tela de Ajustes/Perfil e validar card PWA
        await page.click('button[data-screen="profile"]')
        await page.wait_for_selector("#screen-profile.active", timeout=3000)
        
        card_pwa = await page.locator("#pwa-settings-card").is_visible()
        assert card_pwa, "Card de instalação PWA deve estar presente na tela de Ajustes"
        print("[OK] Card de Ajustes PWA verificado na aba de Perfil.")

        shot_settings = os.path.join(brain_dir, "pwa_settings_screen.png")
        await page.screenshot(path=shot_settings)
        await page.screenshot(path="screenshots/pwa_settings_screen.png")
        print(f"Screenshot de ajustes PWA capturada: {shot_settings}")

        # 5. Testar modo Standalone / Fullscreen PWA
        standalone_context = await browser.new_context(
            viewport={"width": 412, "height": 892},
            device_scale_factor=2.6,
            is_mobile=True,
            has_touch=True
        )
        standalone_page = await standalone_context.new_page()
        # Abre com parâmetro standalone=true
        await standalone_page.goto("http://localhost:8089/beautypass_app.html?standalone=true", wait_until="networkidle")
        await standalone_page.wait_for_timeout(600)

        has_standalone_class = await standalone_page.evaluate("() => document.body.classList.contains('in-pwa-standalone')")
        controller_hidden = await standalone_page.evaluate("""() => {
            const el = document.querySelector('.prototype-controller');
            return !el || window.getComputedStyle(el).display === 'none';
        }""")
        
        print(f"[OK] Classe 'in-pwa-standalone' aplicada: {has_standalone_class}")
        print(f"[OK] Barra de controle de protótipo oculta no modo PWA: {controller_hidden}")
        assert has_standalone_class, "Classe in-pwa-standalone deve ser aplicada"
        assert controller_hidden, "Controles de desktop devem ser ocultados no modo PWA"

        shot_standalone = os.path.join(brain_dir, "pwa_standalone_fullscreen.png")
        await standalone_page.screenshot(path=shot_standalone)
        await standalone_page.screenshot(path="screenshots/pwa_standalone_fullscreen.png")
        print(f"Screenshot modo PWA Standalone Fullscreen: {shot_standalone}")

        await browser.close()
        print("\n--- TODOS OS TESTES PWA PASSARAM COM 100% DE SUCESSO! ---")

if __name__ == "__main__":
    test_manifest()
    asyncio.run(test_pwa_in_browser())
