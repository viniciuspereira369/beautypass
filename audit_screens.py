import asyncio
from playwright.async_api import async_playwright

async def run_audit():
    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        
        viewports = [
            {"width": 320, "height": 568, "name": "320px (iPhone SE 1st Gen)"},
            {"width": 360, "height": 740, "name": "360px (Galaxy A10 / Compact Android)"},
            {"width": 375, "height": 667, "name": "375px (iPhone SE 2nd/3rd Gen)"},
            {"width": 390, "height": 844, "name": "390px (iPhone 14/15/16 Reference)"},
            {"width": 412, "height": 915, "name": "412px (Galaxy S24)"},
            {"width": 768, "height": 1024, "name": "768px (iPad / Foldable)"},
            {"width": 1280, "height": 900, "name": "1280px (Desktop)"}
        ]
        
        for vp in viewports:
            print(f"\n==========================================")
            print(f" AUDIT VIEWPORT: {vp['name']}")
            print(f"==========================================")
            
            context = await browser.new_context(
                viewport={"width": vp["width"], "height": vp["height"]},
                is_mobile=(vp["width"] <= 768)
            )
            page = await context.new_page()
            await page.goto("http://localhost:8089/beautypass_app.html?p=P01", wait_until="networkidle")
            await page.wait_for_timeout(400)
            
            # Check scale value
            scale = await page.evaluate("() => parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--bp-scale'))")
            print(f"  --bp-scale: {scale}")
            
            # Check horizontal overflow on initial screen
            body_scroll_w = await page.evaluate("() => document.body.scrollWidth")
            body_client_w = await page.evaluate("() => document.body.clientWidth")
            if body_scroll_w > body_client_w:
                print(f"  [HORIZONTAL OVERFLOW ON BODY]: scrollWidth={body_scroll_w} > clientWidth={body_client_w}")
            else:
                print(f"  [OK] No horizontal overflow on body ({body_client_w}px)")
                
            vp_scroll_w = await page.evaluate("() => document.querySelector('.app-viewport') ? document.querySelector('.app-viewport').scrollWidth : 0")
            vp_client_w = await page.evaluate("() => document.querySelector('.app-viewport') ? document.querySelector('.app-viewport').clientWidth : 0")
            if vp_scroll_w > vp_client_w:
                print(f"  [HORIZONTAL OVERFLOW IN VIEWPORT]: scrollWidth={vp_scroll_w} > clientWidth={vp_client_w}")
            else:
                print(f"  [OK] No horizontal overflow in viewport ({vp_client_w}px)")
                
            # Complete onboarding
            if await page.locator("#onboarding-submit-btn").is_visible():
                await page.fill("#onboarding-name", "Auditor")
                await page.fill("#onboarding-phone", "11988887777")
                await page.fill("#onboarding-code", "0000")
                await page.check("#onboarding-terms-check")
                await page.dispatch_event("#onboarding-name", "input")
                await page.dispatch_event("#onboarding-phone", "input")
                await page.dispatch_event("#onboarding-code", "input")
                await page.dispatch_event("#onboarding-terms-check", "change")
                await page.click("#onboarding-submit-btn")
                await page.wait_for_selector("#screen-home.active", timeout=5000)
                
            # Home Screen checks
            home_scroll_w = await page.evaluate("() => document.querySelector('#screen-home').scrollWidth")
            home_client_w = await page.evaluate("() => document.querySelector('#screen-home').clientWidth")
            if home_scroll_w > home_client_w:
                print(f"  [HORIZONTAL OVERFLOW ON HOME]: scrollWidth={home_scroll_w} > clientWidth={home_client_w}")
            else:
                print(f"  [OK] Home screen width fit: {home_client_w}px")
                
            # Check bottom nav visibility and bounds
            nav_box = await page.locator("#app-bottom-nav").bounding_box()
            if nav_box:
                print(f"  Bottom nav bounds: y={nav_box['y']:.1f}, h={nav_box['height']:.1f}, w={nav_box['width']:.1f}")
            else:
                print(f"  [ERROR] Bottom nav not visible!")
                
            # Check Map Screen
            await page.click('.nav-item[data-screen="map"]')
            await page.wait_for_timeout(300)
            map_sh = await page.evaluate("() => document.querySelector('.app-viewport').scrollHeight")
            map_ch = await page.evaluate("() => document.querySelector('.app-viewport').clientHeight")
            if map_sh > map_ch:
                print(f"  [MAP VERTICAL OVERFLOW]: scrollHeight={map_sh} > clientHeight={map_ch} (overflow: {map_sh - map_ch}px)")
            else:
                print(f"  [OK] Map fits viewport perfectly: {map_ch}px")
                
            # Check Demand screen
            await page.click('#nav-btn-demand')
            await page.wait_for_timeout(300)
            demand_active = await page.locator('#screen-demand-service.active').is_visible()
            print(f"  Demand Screen visible: {demand_active}")
            
            # Select haircut and advance to uber map
            await page.click('.demand-service-card[data-category="hair"]')
            await page.click('.demand-footer-cta button')
            await page.wait_for_timeout(400)
            uber_active = await page.locator('#screen-uber-demand.active').is_visible()
            print(f"  Uber Demand Map visible: {uber_active}")
            uber_sh = await page.evaluate("() => document.querySelector('.app-viewport').scrollHeight")
            uber_ch = await page.evaluate("() => document.querySelector('.app-viewport').clientHeight")
            if uber_sh > uber_ch:
                print(f"  [UBER MAP VERTICAL OVERFLOW]: scrollHeight={uber_sh} > clientHeight={uber_ch} (overflow: {uber_sh - uber_ch}px)")
            else:
                print(f"  [OK] Uber map fits viewport perfectly: {uber_ch}px")
                
            await context.close()
            
        await browser.close()

if __name__ == "__main__":
    asyncio.run(run_audit())
