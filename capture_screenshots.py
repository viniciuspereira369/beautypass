import os
import asyncio
from playwright.async_api import async_playwright

async def capture_all():
    output_dir = os.path.join(os.getcwd(), "screenshots")
    brain_dir = r"C:\Users\Usuario(a) Master\.gemini\antigravity-ide\brain\7c96cc1e-c6b9-4457-9fad-359f9b5b0715"
    
    os.makedirs(output_dir, exist_ok=True)
    os.makedirs(brain_dir, exist_ok=True)
    
    html_file = f"file:///{os.path.abspath('index.html').replace('\\', '/')}"
    print(f"Opening: {html_file}")

    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        # 1440x900 viewport for crisp desktop presentations
        context = await browser.new_context(
            viewport={"width": 1440, "height": 960},
            device_scale_factor=2
        )
        page = await context.new_page()
        
        await page.goto(html_file, wait_until="networkidle")
        await page.wait_for_timeout(1500) # wait for GSAP animations to complete

        # -------------------------------------------------------------
        # 1. B2C Hero & Catalog
        # -------------------------------------------------------------
        print("Capturing B2C View (Top & Catalog)...")
        await page.evaluate("switchContext('b2c')")
        await page.wait_for_timeout(600)
        await page.evaluate("window.scrollTo(0, 0)")
        await page.wait_for_timeout(500)
        
        path1 = os.path.join(output_dir, "01_b2c_hero_catalog.png")
        path1_brain = os.path.join(brain_dir, "01_b2c_hero_catalog.png")
        await page.screenshot(path=path1)
        await page.screenshot(path=path1_brain)

        # -------------------------------------------------------------
        # 2. B2C Plans & Pricing
        # -------------------------------------------------------------
        print("Capturing B2C Plans Section...")
        await page.evaluate("document.getElementById('planos').scrollIntoView({behavior: 'instant'})")
        await page.wait_for_timeout(600)
        
        path2 = os.path.join(output_dir, "02_b2c_plans_pricing.png")
        path2_brain = os.path.join(brain_dir, "02_b2c_plans_pricing.png")
        await page.screenshot(path=path2)
        await page.screenshot(path=path2_brain)

        # -------------------------------------------------------------
        # 3. B2B Hero & ROI Calculator
        # -------------------------------------------------------------
        print("Capturing B2B Hero & Calculator...")
        await page.evaluate("switchContext('b2b')")
        await page.wait_for_timeout(800)
        await page.evaluate("window.scrollTo(0, 0)")
        await page.wait_for_timeout(500)
        
        path3 = os.path.join(output_dir, "03_b2b_hero_overview.png")
        path3_brain = os.path.join(brain_dir, "03_b2b_hero_overview.png")
        await page.screenshot(path=path3)
        await page.screenshot(path=path3_brain)

        # Scroll to B2B Calculator & Charts
        print("Capturing B2B ROI Calculator & DataViz Charts...")
        await page.evaluate("document.getElementById('b2b-calculator').scrollIntoView({behavior: 'instant'})")
        await page.wait_for_timeout(800)
        
        path4 = os.path.join(output_dir, "04_b2b_calculator_dataviz.png")
        path4_brain = os.path.join(brain_dir, "04_b2b_calculator_dataviz.png")
        await page.screenshot(path=path4)
        await page.screenshot(path=path4_brain)

        # -------------------------------------------------------------
        # 5. Partner (Estabelecimentos) Hero & POS Validator Simulator
        # -------------------------------------------------------------
        print("Capturing Partner View & POS Simulator...")
        await page.evaluate("switchContext('partner')")
        await page.wait_for_timeout(800)
        await page.evaluate("document.getElementById('simulador-voucher').scrollIntoView({behavior: 'instant'})")
        await page.wait_for_timeout(500)
        
        # Trigger validation simulation to show success state
        await page.evaluate("fillValidatorCode('BP-9042-LUX')")
        await page.wait_for_timeout(1000)
        
        path5 = os.path.join(output_dir, "05_partner_pos_simulator.png")
        path5_brain = os.path.join(brain_dir, "05_partner_pos_simulator.png")
        await page.screenshot(path=path5)
        await page.screenshot(path=path5_brain)

        # -------------------------------------------------------------
        # 6. Digital Voucher Boarding Pass Modal
        # -------------------------------------------------------------
        print("Capturing Digital Voucher Boarding Pass Modal...")
        await page.evaluate("switchContext('b2c')")
        await page.wait_for_timeout(500)
        await page.evaluate("generateVoucher('clinic-1')")
        await page.wait_for_timeout(600)
        
        path6 = os.path.join(output_dir, "06_modal_voucher_boarding_pass.png")
        path6_brain = os.path.join(brain_dir, "06_modal_voucher_boarding_pass.png")
        await page.screenshot(path=path6)
        await page.screenshot(path=path6_brain)
        
        await page.evaluate("closeVoucherModal()")
        await page.wait_for_timeout(400)

        # -------------------------------------------------------------
        # 7. User Digital Wallet Drawer
        # -------------------------------------------------------------
        print("Capturing Digital Wallet Drawer...")
        await page.evaluate("openWalletModal()")
        await page.wait_for_timeout(600)
        
        path7 = os.path.join(output_dir, "07_drawer_digital_wallet.png")
        path7_brain = os.path.join(brain_dir, "07_drawer_digital_wallet.png")
        await page.screenshot(path=path7)
        await page.screenshot(path=path7_brain)
        
        await page.evaluate("closeWalletModal()")
        await page.wait_for_timeout(400)

        # -------------------------------------------------------------
        # 8. Floating 24h AI Concierge Widget
        # -------------------------------------------------------------
        print("Capturing Concierge AI Chat Widget...")
        await page.evaluate("toggleConcierge()")
        await page.wait_for_timeout(500)
        
        path8 = os.path.join(output_dir, "08_widget_concierge_chat.png")
        path8_brain = os.path.join(brain_dir, "08_widget_concierge_chat.png")
        await page.screenshot(path=path8)
        await page.screenshot(path=path8_brain)

        await browser.close()
        print("All screenshots successfully captured!")

if __name__ == "__main__":
    asyncio.run(capture_all())
