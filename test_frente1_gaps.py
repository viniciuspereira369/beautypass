import threading
import http.server
import socketserver
import os
from playwright.sync_api import sync_playwright

PORT = 8094

def start_server():
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    handler = http.server.SimpleHTTPRequestHandler
    with socketserver.TCPServer(("", PORT), handler) as httpd:
        httpd.serve_forever()

def run_frente1_tests():
    server_thread = threading.Thread(target=start_server, daemon=True)
    server_thread.start()

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(permissions=[])  # Deny geolocation to test fallback banner
        page = context.new_page()

        print("\n=== TESTANDO IMPLEMENTAÇÃO COMPLETA DA FRENTE 1 ===")
        page.goto(f"http://localhost:{PORT}/beautypass_app.html?p=P01")
        page.wait_for_selector("#onboarding-participant")

        # 1. Login no Onboarding
        page.fill("#onboarding-name", "Participante Frente 1")
        page.fill("#onboarding-phone", "11988887777")
        page.fill("#onboarding-code", "0000")
        page.check("#onboarding-terms-check")
        page.dispatch_event("#onboarding-name", "input")
        page.dispatch_event("#onboarding-phone", "input")
        page.dispatch_event("#onboarding-code", "input")
        page.dispatch_event("#onboarding-terms-check", "change")
        page.click("#onboarding-submit-btn")
        page.wait_for_selector("#screen-home.active", timeout=5000)

        # 2. Verificar Geolocalização e Fallback na Home (Seção 6.4)
        page.wait_for_timeout(600)
        loc_label = page.inner_text("#home-location-label").strip()
        warning_visible = page.locator("#home-location-warning").is_visible()
        print(f"[OK] Item 3 (GPS Home): Label='{loc_label}', Fallback visível={warning_visible}")
        assert loc_label != "Localizando...", "O label de localização na Home não pode ficar travado em 'Localizando...'"
        assert warning_visible, "O banner #home-location-warning deve aparecer quando o GPS é negado"

        # 3. Verificar Seed Completo s1-s24 (Equipe >= 4, Destaque +0.3 e Avaliações >= 8)
        seed_audit = page.evaluate("""() => {
            return MOCK_SALONS.map(s => ({
                id: s.id,
                staffCount: s.staff ? s.staff.length : 0,
                hasFeatured: Boolean(s.staff && s.staff[0] && s.staff[0].highlightBadge),
                reviewsCount: s.reviews ? s.reviews.length : 0
            }));
        }""")
        all_have_4_staff = all(item["staffCount"] >= 4 for item in seed_audit)
        all_have_featured = all(item["hasFeatured"] for item in seed_audit)
        all_have_8_reviews = all(item["reviewsCount"] >= 8 for item in seed_audit)
        print(f"[OK] Item 1a (Seed s1-s24): {len(seed_audit)} salões verificados | >=4 staff={all_have_4_staff} | destaque={all_have_featured} | >=8 reviews={all_have_8_reviews}")
        assert all_have_4_staff, "Todos os 24 salões devem ter pelo menos 4 profissionais"
        assert all_have_featured, "Todos os 24 salões devem ter 1 profissional com selo de destaque"
        assert all_have_8_reviews, "Todos os 24 salões devem ter pelo menos 8 avaliações"

        # 4. Abrir um salão secundário (ex: s5) e verificar UI de Equipe (+0.3 / Destaque) e Expansão de Reviews
        page.evaluate("openSalonDetail('s5')")
        page.wait_for_selector("#screen-detail.active", timeout=5000)

        featured_badge = page.locator("#staff-carousel-container .staff-highlight-badge").first
        rating_pill = page.locator("#staff-carousel-container .staff-rating-pill").first
        assert featured_badge.is_visible(), "Badge de destaque deve estar visível no card do profissional"
        assert "+0.3" in rating_pill.inner_text(), "Delta +0.3 deve estar visível no card do profissional em destaque"
        print(f"[OK] Item 1b (UI Equipe s5): Badge='{featured_badge.inner_text()}', Nota='{rating_pill.inner_text()}'")

        # Verificar expansão de avaliações
        initial_reviews = page.locator("#detail-reviews-container .review-card").count()
        toggle_btn = page.locator("#btn-toggle-reviews")
        assert initial_reviews == 3, f"Esperado 3 avaliações iniciais, obtido {initial_reviews}"
        assert toggle_btn.is_visible(), "Botão de expandir avaliações deve estar visível"
        toggle_btn.click()
        expanded_reviews = page.locator("#detail-reviews-container .review-card").count()
        print(f"[OK] Item 1c (Expansão Reviews): Inicial={initial_reviews} -> Expandido={expanded_reviews}")
        assert expanded_reviews >= 8, f"Esperado >= 8 avaliações após expandir, obtido {expanded_reviews}"

        # 5. Testar Seleção de Múltiplos Serviços na Mesma Visita (Seção 6.1 e 7.2)
        page.evaluate("openSalonDetail('s1')")
        page.wait_for_selector("#screen-detail.active", timeout=5000)
        empty_banner = page.locator("#detail-empty-slots-banner")
        if empty_banner.is_visible():
            page.click("#detail-empty-slots-banner .empty-slots-action-btn")

        single_price = page.evaluate("() => AppState.selectedService.basePrice")
        single_duration = page.evaluate("() => AppState.selectedService.durationMinutes")
        # Clicar no 2º serviço para combinar
        page.locator("#detail-services-list .service-item-card").nth(1).click()
        multi_count = page.evaluate("() => AppState.selectedServices.length")
        combo_price = page.evaluate("() => AppState.selectedService.basePrice")
        combo_duration = page.evaluate("() => AppState.selectedService.durationMinutes")
        badge_visible = page.locator("#detail-service-package-badge").is_visible()
        print(f"[OK] Item 2 (Múltiplos Serviços): {multi_count} serviços | Preço: R$ {single_price} -> R$ {combo_price} | Duração: {single_duration}m -> {combo_duration}m")
        assert multi_count == 2, "Deve haver 2 serviços selecionados no pacote"
        assert combo_price > single_price, "Preço base composto deve somar os serviços"
        assert combo_duration > single_duration, "Duração composta deve somar os minutos dos serviços"
        assert badge_visible, "Badge de pacote de múltiplos serviços deve estar visível no Hero Card"

        # 6. Testar Regra Dinâmica de 3 Horas para "Última Hora" (Seção 6.2 e 9)
        urgent_check = page.evaluate("""() => {
            const now = new Date();
            const futureH = now.getHours() + 1;
            const fakeTime = String(futureH).padStart(2, '0') + ':00';
            const todayStr = getTodayDateStr();
            const isWithin3h = isSlotWithinNext3Hours(fakeTime, todayStr);
            return { fakeTime, todayStr, isWithin3h };
        }""")
        print(f"[OK] Item 4 (Regra 3h Última Hora): {urgent_check}")
        if 0 <= int(urgent_check["fakeTime"].split(":")[0]) <= 23:
            assert urgent_check["isWithin3h"], "Horário 1h à frente hoje deve ser detectado dentro da janela de 3h"

        # 7. Testar Política Dinâmica de Cancelamento (<24h = 30% vs >24h = Grátis — Seção 7.5)
        policy_test = page.evaluate("""() => {
            openCancelModalForAppt('BP-108420'); // Hoje -> <24h (30% de R$ 84 = R$ 25.20)
            const under24Text = document.getElementById('cancel-fee-breakdown-box').innerText;
            closeCancelModal();

            // Testar agendamento > 24h (daqui a 3 dias)
            const futureDate = new Date(Date.now() + 86400000 * 3);
            const yyyy = futureDate.getFullYear();
            const mm = String(futureDate.getMonth() + 1).padStart(2, '0');
            const dd = String(futureDate.getDate()).padStart(2, '0');
            const policyFuture = calculateCancellationPolicy({
                rawDate: `${yyyy}-${mm}-${dd}`,
                time: '15:00',
                pricing: { finalPrice: 120.00 }
            });
            return { under24Text, policyFuture };
        }""")
        print(f"[OK] Item 5 (Cancelamento <24h vs >24h):\n{policy_test['under24Text']}\nFuture (>24h): {policy_test['policyFuture']}")
        assert "30%" in policy_test["under24Text"] and "25.20" in policy_test["under24Text"], "Deve calcular 30% (R$ 25.20) para agendamento < 24h"
        assert policy_test["policyFuture"]["isUnder24h"] is False and policy_test["policyFuture"]["feeAmount"] == 0, "Agendamento > 24h deve ser gratuito (R$ 0)"

        print("\n=== TODOS OS TESTES DA FRENTE 1 PASSARAM COM 100% DE SUCESSO! ===\n")
        browser.close()

if __name__ == "__main__":
    run_frente1_tests()
