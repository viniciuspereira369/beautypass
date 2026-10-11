import asyncio
from playwright.async_api import async_playwright

async def test_map_heights():
    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        page = await browser.new_page(viewport={"width": 1280, "height": 900})
        await page.goto("http://localhost:8089/beautypass_app.html?p=P01")
        await page.wait_for_timeout(400)
        
        # Complete onboarding
        if await page.locator("#onboarding-submit-btn").is_visible():
            await page.fill("#onboarding-name", "Test")
            await page.fill("#onboarding-phone", "11999998888")
            await page.fill("#onboarding-code", "0000")
            await page.check("#onboarding-terms-check")
            await page.dispatch_event("#onboarding-name", "input")
            await page.dispatch_event("#onboarding-phone", "input")
            await page.dispatch_event("#onboarding-code", "input")
            await page.dispatch_event("#onboarding-terms-check", "change")
            await page.click("#onboarding-submit-btn")
            await page.wait_for_selector("#screen-home.active", timeout=5000)
            
        await page.click('.nav-item[data-screen="map"]')
        await page.wait_for_timeout(300)
        
        # Validação direta dos presets sem injeção artificial de CSS
        
        presets = ['se', '16pro', 's24', 'auto']
        for pr in presets:
            await page.click(f"#btn-device-{pr}")
            await page.wait_for_timeout(200)
            
            # trigger resize or leaflet invalidate
            await page.evaluate("() => { if (window.bpMap) window.bpMap.invalidateSize(); }")
            
            sh = await page.evaluate("() => document.querySelector('.app-viewport').scrollHeight")
            ch = await page.evaluate("() => document.querySelector('.app-viewport').clientHeight")
            map_h = await page.evaluate("() => document.querySelector('.map-screen-wrap').offsetHeight")
            print(f"Preset {pr:6s} -> scrollHeight: {sh}, clientHeight: {ch}, map_height: {map_h}, overflow: {sh - ch}")
            assert sh == ch, f"Preset {pr} should have NO overflow: {sh} vs {ch}"
            
        print("ALL PRESETS FIT MAP WITH ZERO OVERFLOW!")
        await browser.close()

if __name__ == "__main__":
    asyncio.run(test_map_heights())
