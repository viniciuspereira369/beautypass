import sys
from playwright.sync_api import sync_playwright

def run_tests():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        
        # 1. Test URL Param ?p=P01
        print("Navigating to http://localhost:8089/beautypass_app.html?p=P01...")
        page.goto("http://localhost:8089/beautypass_app.html?p=P01")
        page.wait_for_selector("#onboarding-participant")
        participant_val = page.input_value("#onboarding-participant")
        is_readonly = page.get_attribute("#onboarding-participant", "readonly") is not None
        print(f"Test 1 - Participant Code: {participant_val} (ReadOnly: {is_readonly})")
        assert participant_val == "P01", "Participant code should be P01"
        assert is_readonly, "Participant code input should be read-only"

        # 2. Fill onboarding and submit
        page.fill("#onboarding-name", "Teste Automatizado")
        page.fill("#onboarding-phone", "11988887777")
        page.fill("#onboarding-code", "0000")
        page.check("#onboarding-terms-check")
        
        # Trigger validation input
        page.dispatch_event("#onboarding-name", "input")
        page.dispatch_event("#onboarding-phone", "input")
        page.dispatch_event("#onboarding-code", "input")
        page.dispatch_event("#onboarding-terms-check", "change")
        
        page.click("#onboarding-submit-btn")
        page.wait_for_selector("#screen-home.active", timeout=5000)
        print("Test 2 - Successfully landed on Home screen!")

        # 3. Enter salon detail
        page.locator("#social-feed-container .card-action-btn").first.click()
        page.wait_for_selector("#screen-detail.active", timeout=5000)
        print("Test 3 - Entered Salon Detail screen!")

        # 4. Check dynamic date pills
        pills = page.query_selector_all("#detail-date-strip-row .date-day-pill")
        print(f"Test 4 - Date pills rendered: {len(pills)} days")
        assert len(pills) == 5, f"Expected 5 dynamic date pills, got {len(pills)}"

        # If Sunday, select Monday via next available date button or pill[1]
        empty_banner = page.locator("#detail-empty-slots-banner")
        if empty_banner.is_visible():
            print("Today is Sunday - Salons closed. Clicking next available day (Monday)...")
            page.click("#detail-empty-slots-banner .empty-slots-action-btn")
            page.wait_for_selector(".radial-clock-container", state="visible", timeout=3000)

        # 5. Check subtext compliance (Section 6.2)
        slot_card_text = page.inner_text("#slot-pricing-summary")
        print(f"Test 5 - Slot card text:\n{slot_card_text}")
        has_correct_subtext = ("preço menor em horário de menor procura" in slot_card_text or 
                               "desconto para hoje" in slot_card_text or
                               "horário de alta procura" in slot_card_text or
                               "slot já preenchido" in slot_card_text)
        assert has_correct_subtext, "Subtext does not follow Section 6.2 compliance"

        # 6. Go to Profile and open SUS evaluation modal
        page.click(".nav-item[data-screen='profile']")
        page.wait_for_selector("#screen-profile.active", timeout=5000)
        page.click("button:has-text('Avaliar Sessão (SUS)')")
        page.wait_for_selector("#session-finish-modal-overlay.active", timeout=5000)
        print("Test 6 - Opened SUS Evaluation modal!")

        # 7. Verify all SUS Likert buttons are unselected
        selected_buttons = page.query_selector_all(".sus-likert-btn.selected")
        print(f"Test 7 - Selected SUS Likert buttons: {len(selected_buttons)} (Expected: 0)")
        assert len(selected_buttons) == 0, "No Likert button should be pre-selected!"

        # 8. Verify Submit button is disabled
        is_disabled = page.get_attribute("#btn-submit-sus-evaluation", "disabled") is not None
        print(f"Test 8 - Submit button disabled: {is_disabled}")
        assert is_disabled, "Submit button must be disabled until all questions are answered!"

        # 9. Answer all 10 Likert questions and select retention radio
        for q_idx in range(10):
            page.locator(f"#sus-questions-container .sus-question-item:nth-child({q_idx + 1}) .sus-likert-btn").nth(3).click()
        
        page.check("input[name='finish-retention'][value='yes']")
        page.dispatch_event("input[name='finish-retention'][value='yes']", "change")

        is_disabled_after = page.get_attribute("#btn-submit-sus-evaluation", "disabled") is not None
        print(f"Test 9a - Submit button enabled after answering: {not is_disabled_after}")
        assert not is_disabled_after, "Submit button should be enabled after completing all questions"

        # Submit evaluation
        page.click("#btn-submit-sus-evaluation")
        page.wait_for_timeout(500)

        # 10. Check localStorage persistence
        saved_evals = page.evaluate("() => JSON.parse(localStorage.getItem('bp_sus_evaluations') || '[]')")
        print(f"Test 10 - Saved evaluations count: {len(saved_evals)}, Score: {saved_evals[-1]['susScore']}")
        assert len(saved_evals) >= 1, "Evaluation should be persisted in localStorage"
        assert saved_evals[-1]["participantCode"] == "P01", "Evaluation participant code should be P01"

        print("\nALL SPRINT ACCEPTANCE TESTS PASSED WITH 100% SUCCESS!")
        browser.close()

if __name__ == "__main__":
    run_tests()
