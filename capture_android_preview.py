import asyncio
import os
import subprocess
import time
from playwright.async_api import async_playwright

ARTIFACT_DIR = r"C:\Users\Usuario(a) Master\.gemini\antigravity\brain\481b6e1b-3839-42a1-bbb3-54d21a03a81b"
SCREENSHOTS_DIR = os.path.join(os.getcwd(), "screenshots")
os.makedirs(ARTIFACT_DIR, exist_ok=True)
os.makedirs(SCREENSHOTS_DIR, exist_ok=True)

async def main():
    server_process = subprocess.Popen(["python", "-m", "http.server", "8095"])
    time.sleep(1.5)

    try:
        async with async_playwright() as p:
            browser = await p.chromium.launch(headless=True)
            context = await browser.new_context(
                viewport={"width": 412, "height": 915},
                device_scale_factor=2.0,
                is_mobile=True,
                has_touch=True,
                user_agent="Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
            )
            page = await context.new_page()

            # 1. Onboarding
            await page.goto("http://localhost:8095/beautypass_app.html?p=P01", wait_until="networkidle")
            await page.wait_for_timeout(600)
            await page.screenshot(path=os.path.join(ARTIFACT_DIR, "preview_android_1_onboarding.png"))
            await page.screenshot(path=os.path.join(SCREENSHOTS_DIR, "preview_android_1_onboarding.png"))
            print("[OK] Screenshot 1: Onboarding")

            # Preenche Onboarding e vai para Home
            await page.fill("#onboarding-name", "Camila Silva")
            await page.fill("#onboarding-phone", "11999887766")
            await page.fill("#onboarding-code", "0000")
            await page.check("#onboarding-terms-check")
            await page.dispatch_event("#onboarding-name", "input")
            await page.dispatch_event("#onboarding-phone", "input")
            await page.dispatch_event("#onboarding-code", "input")
            await page.dispatch_event("#onboarding-terms-check", "change")
            await page.click("#onboarding-submit-btn")
            await page.wait_for_selector("#screen-home.active", timeout=6000)
            await page.wait_for_timeout(800)

            # 2. Home Screen
            await page.screenshot(path=os.path.join(ARTIFACT_DIR, "preview_android_2_home.png"))
            await page.screenshot(path=os.path.join(SCREENSHOTS_DIR, "preview_android_2_home.png"))
            print("[OK] Screenshot 2: Home Screen")

            # 3. Detalhes do Salão
            await page.locator("#social-feed-container .card-action-btn").first.click()
            await page.wait_for_selector("#screen-detail.active", timeout=6000)
            await page.wait_for_timeout(800)
            await page.screenshot(path=os.path.join(ARTIFACT_DIR, "preview_android_3_detail.png"))
            await page.screenshot(path=os.path.join(SCREENSHOTS_DIR, "preview_android_3_detail.png"))
            print("[OK] Screenshot 3: Detalhes do Salão")

            # 4. Checkout
            empty_banner = page.locator("#detail-empty-slots-banner")
            if await empty_banner.is_visible():
                await page.click("#detail-empty-slots-banner .empty-slots-action-btn")
                await page.wait_for_timeout(400)

            await page.locator("#screen-detail .card-action-btn").first.click()
            await page.wait_for_selector("#screen-checkout.active", timeout=6000)
            await page.wait_for_timeout(800)
            await page.screenshot(path=os.path.join(ARTIFACT_DIR, "preview_android_4_checkout.png"))
            await page.screenshot(path=os.path.join(SCREENSHOTS_DIR, "preview_android_4_checkout.png"))
            print("[OK] Screenshot 4: Checkout")

            # 5. Voucher & QR Code
            await page.click("#btn-confirm-checkout")
            await page.wait_for_selector("#screen-confirm.active", timeout=12000)
            await page.wait_for_timeout(800)
            await page.screenshot(path=os.path.join(ARTIFACT_DIR, "preview_android_5_voucher.png"))
            await page.screenshot(path=os.path.join(SCREENSHOTS_DIR, "preview_android_5_voucher.png"))
            print("[OK] Screenshot 5: Voucher & QR Code")

            # 6. Radar On-Demand (Pedir Agora)
            await page.evaluate("openOnDemandFlow()")
            await page.wait_for_selector("#screen-demand-service.active", timeout=6000)
            await page.wait_for_timeout(800)
            await page.screenshot(path=os.path.join(ARTIFACT_DIR, "preview_android_6_radar.png"))
            await page.screenshot(path=os.path.join(SCREENSHOTS_DIR, "preview_android_6_radar.png"))
            print("[OK] Screenshot 6: Radar On-Demand")

            # 7. Painel B2B Parceiro & H3
            await page.evaluate("openPartnerB2BPanel()")
            await page.wait_for_selector("#screen-partner-b2b.active", timeout=6000)
            await page.wait_for_timeout(800)
            await page.screenshot(path=os.path.join(ARTIFACT_DIR, "preview_android_7_b2b.png"))
            await page.screenshot(path=os.path.join(SCREENSHOTS_DIR, "preview_android_7_b2b.png"))
            print("[OK] Screenshot 7: Painel B2B Parceiro")

            await browser.close()
    finally:
        server_process.terminate()

if __name__ == "__main__":
    asyncio.run(main())
