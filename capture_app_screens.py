import os
import asyncio
from playwright.async_api import async_playwright

async def capture():
    os.makedirs("screenshots", exist_ok=True)
    url = "http://localhost:8089/beautypass_app.html?p=P01"
    print(f"Loading: {url}")

    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        # Mobile viewport matching modern iPhone (393x852 or 412x915)
        context = await browser.new_context(
            viewport={"width": 412, "height": 915},
            device_scale_factor=2
        )
        page = await context.new_page()
        await page.goto(url)
        await page.wait_for_selector("#onboarding-participant")

        # 1. Onboarding
        await page.screenshot(path="screenshots/app_screen_01_onboarding.png")

        # Fill onboarding
        await page.fill("#onboarding-name", "Camila Alves")
        await page.fill("#onboarding-phone", "11988887777")
        await page.fill("#onboarding-code", "0000")
        await page.check("#onboarding-terms-check")
        await page.dispatch_event("#onboarding-name", "input")
        await page.dispatch_event("#onboarding-phone", "input")
        await page.dispatch_event("#onboarding-code", "input")
        await page.dispatch_event("#onboarding-terms-check", "change")
        await page.click("#onboarding-submit-btn")
        await page.wait_for_selector("#screen-home.active", timeout=5000)
        # Wait for login toast to naturally auto-dismiss (3s duration)
        await page.wait_for_timeout(3200)

        # 2. Home screen
        await page.screenshot(path="screenshots/app_screen_02_home.png")

        # 3. Salon Detail screen
        await page.locator("#social-feed-container .card-action-btn").first.click()
        await page.wait_for_selector("#screen-detail.active", timeout=5000)
        await page.wait_for_timeout(500)
        await page.screenshot(path="screenshots/app_screen_03_detail.png")

        # 4. On-demand / Triple Fusion screen
        await page.evaluate("openOnDemandFlow()")
        await page.wait_for_selector("#screen-demand-service.active", timeout=5000)
        await page.wait_for_timeout(500)
        await page.screenshot(path="screenshots/app_screen_04_demand.png")

        # 5. Profile screen
        await page.evaluate("navigateTo('profile')")
        await page.wait_for_selector("#screen-profile.active", timeout=5000)
        await page.wait_for_timeout(500)
        await page.screenshot(path="screenshots/app_screen_05_profile.png")

        # 6. Appointments screen
        await page.evaluate("navigateTo('appointments')")
        await page.wait_for_selector("#screen-appointments.active", timeout=5000)
        await page.wait_for_timeout(500)
        await page.screenshot(path="screenshots/app_screen_06_appointments.png")

        print("Screenshots captured successfully!")
        await browser.close()

if __name__ == '__main__':
    asyncio.run(capture())
