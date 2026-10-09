# Project: BeautyPass Android Nativo (Jetpack Compose & Kotlin)

## Architecture
- **Paradigma**: Arquitetura Unidirectional Data Flow (MVI / UDF) no Jetpack Compose com `StateFlow` e `ViewModel`.
- **Camada de Apresentação**: Jetpack Compose com Material 3, Navigation Compose, `Theme.kt` com tokens Serene Mint & Teal (#00685F, #0D9488, #5EEAD4, #FAFCFC, #134E4A), componentes visuais especializados (`RadialGaugeTimeSelector`, `RadarScanView`, `SalonCard`, `VoucherQrCodeView`).
- **Camada de Domínio / Regras de Negócio**: Motores determinísticos para Algoritmo de Luhn, Precificação Dinâmica com piso operacional (`min_price`), Regras de Agendamento (blocos 15 min, buffer 10 min de higienização, prevenção double-booking), cálculo SUS (0 a 100).
- **Camada de Dados**: `SalonRepository.kt` com catálogo determinístico dos 24 salões de São Paulo (coordenadas, fotos HD, ratings 4.60-4.92, serviços, equipes, promoções de ociosidade) e persistência de agendamentos e favoritos em memória reativa (`MutableStateFlow`).
- **Camada de Testes e Validação**: Testes unitários JUnit 4 (`LuhnValidatorTest`, `PricingEngineTest`, `BookingRulesTest`), script de validação automatizada `verify_android_build.py` e relatório pericial de design `DESIGN_AUDIT_ANDROID.md`.

## Code Layout
- `android/gradle/wrapper/`: Gradle wrapper (`gradlew`, `gradlew.bat`, `gradle-wrapper.properties`).
- `android/app/src/main/res/drawable/`: Ícones vetoriais de aplicação (`ic_launcher.xml`, `ic_launcher_round.xml`) e recursos XML.
- `android/app/src/main/java/com/beautypass/app/`:
  - `MainActivity.kt`: Ponto de entrada Android inicializando `BeautyPassApp()`.
  - `theme/`:
    - `Color.kt`: Tokens oficiais Serene Mint & Teal.
    - `Theme.kt`: `BeautyPassTheme` com esquemas M3.
    - `Type.kt`: Tipografia Plus Jakarta Sans / Roboto M3.
    - `Shape.kt`: Sistema de formas e cantos arredondados (8dp a 24dp, cápsulas 9999dp).
  - `model/`:
    - `SalonModels.kt`: Data classes do domínio (Salon, Service, Staff, Appointment, DiscountSlot, etc.).
  - `data/`:
    - `SalonRepository.kt`: Repositório com os 24 salões de SP, favoritos e agendamentos.
    - `LuhnValidator.kt`: Algoritmo de validação de cartões de crédito.
    - `PricingEngine.kt`: Motor de cálculo de descontos e piso operacional.
    - `BookingEngine.kt`: Motor de validação de slots, buffers e conflitos de agenda.
  - `navigation/`:
    - `BeautyPassNavigation.kt`: NavHost com as rotas completas das 6 telas principais.
  - `ui/components/`:
    - `SalonCard.kt`: Card rico com prova social, km desobstruído, selo de fundador e amigas em comum.
    - `RadialGaugeTimeSelector.kt`: Seletor radial analógico-digital de 180° com alternador AM/PM.
    - `RadarScanView.kt`: Scanner de radar visual animado de 1.8s e blips de ociosidade.
    - `VoucherQrCodeView.kt`: QR Code vetorial nativo via Canvas Compose.
  - `ui/screens/`:
    - `onboarding/OnboardingScreen.kt`: Tela 1 (Onboarding e Auth reativo com LGPD).
    - `home/HomeScreen.kt`: Tela 2 (Feed social, carrossel e filtros contextuais).
    - `detail/SalonDetailScreen.kt`: Tela 3 (Mini-galeria 4 fotos HD, catálogo, radial gauge).
    - `ondemand/OnDemandScreen.kt`: Tela 4 (Pedir Agora, radar de ociosidade, match 3 opções, estimativa a pé).
    - `checkout/CheckoutScreen.kt`: Tela 5 (Resumo, snapshot congelado, Luhn, timer 10 min).
    - `confirm/ConfirmScreen.kt`: Voucher digital com QR Code e detalhes.
    - `appointments/AppointmentsScreen.kt`: Tela 6A (Histórico, retenção 30% para cancelamento <24h).
    - `profile/ProfileScreen.kt`: Tela 6B (Métricas do participante, questionário SUS de 10 itens).
    - `map/MapScreen.kt`: Mapa vetorial e bottom sheet com distâncias a pé.
- `android/app/src/test/java/com/beautypass/app/`:
  - `LuhnValidatorTest.kt`: Testes de números válidos e inválidos de cartão.
  - `PricingEngineTest.kt`: Testes de descontos 5%-40%, respeito a min_price e congelamento de snapshot.
  - `BookingRulesTest.kt`: Testes de blocos 15 min, buffer 10 min e prevenção de double-booking.
- `verify_android_build.py`: Script de verificação automatizada completa (sintaxe, compilação, testes unitários).
- `DESIGN_AUDIT_ANDROID.md`: Relatório pericial do especialista de design em 8 dimensões.

## Feature Inventory
| # | Feature | Description | Milestone | Source |
|---|---------|-------------|-----------|--------|
| 1 | Gradle Wrapper & Assets | Configuração de gradlew, gradlew.bat, wrapper properties e drawables de ícones | M1 | Survey E1 |
| 2 | M3 Serene Mint & Teal Theme | Tokens Color.kt (#00685F, #0D9488, #5EEAD4, etc.), Theme.kt, Type.kt, Shape.kt | M2 | Survey E3, DESIGN.md |
| 3 | Catálogo Determinístico 24 Salões | SalonRepository.kt com os 24 salões de SP com fotos, GPS, serviços, profissionais | M2 | Survey E2, app.js |
| 4 | Motores de Regras de Negócio | LuhnValidator.kt, PricingEngine.kt, BookingEngine.kt | M2 | Survey E2, Doc Técnica |
| 5 | Tela 1: Onboarding & Auth | Formulário reativo (nome, telefone, P01, 0000, LGPD) e persistência de sessão | M3 | R1.1, Survey E2 |
| 6 | Tela 2: Home Feed & Hub | Feed social, categorias, filtros (Todos, Economia, Proximidade, Favoritos), 24 cards | M3 | R1.2, Survey E2/E3 |
| 7 | Tela 3: Detalhe & Seletor Radial | Galeria 4 fotos HD, catálogo, RadialGaugeTimeSelector (180°, AM/PM, par de preços) | M3 | R1.3, Inspiração 1 |
| 8 | Tela 4: Pedir Agora (On-Demand) | RadarScanView animado, match em 3 opções (Ideal, Próximo, Econômico), estimativa a pé | M3 | R1.4, Inspiração 2 |
| 9 | Tela 5: Checkout & Voucher | Resumo com snapshot, validação Luhn, timer regressivo 10 min, Canvas VoucherQrCodeView | M3 | R1.5, Survey E2/E3 |
| 10 | Tela 6: Agendamentos & Perfil/SUS | Histórico, cancelamento com retenção 30% (<24h), métricas H1/H2, questionário SUS 10 itens | M3 | R1.6, Survey E2 |
| 11 | Grafo de Rotas Navigation Compose | BeautyPassNavigation.kt conectando as 6 telas e rotas de suporte | M3 | R1, Survey E1 |
| 12 | Testes Unitários Kotlin | LuhnValidatorTest, PricingEngineTest, BookingRulesTest em src/test | M4 | Critérios Aceite |
| 13 | Script de Automação de Verificação | verify_android_build.py com validação 100% de testes e estrutura | M4 | Critérios Aceite |
| 14 | Auditoria Especializada de Design | DESIGN_AUDIT_ANDROID.md com avaliação de 8 dimensões e conformidade visual | M5 | R4, Requisito Obrigatório |
| 15 | Compilação e Verificação Final E2E | Execução de verify_android_build.py, checagem de build limpo e fechamento | M6 | Critérios Aceite |

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | M1: Gradle Wrapper & Build Assets | Wrapper gradlew/bat, wrapper properties, launcher drawables XML | none | DONE |
| 2 | M2: M3 Design Tokens, 24 Salons & Business Engines | Color.kt, Theme.kt, Type.kt, Shape.kt, SalonRepository.kt (24 salões), LuhnValidator.kt, PricingEngine.kt, BookingEngine.kt | none | DONE |
| 3 | M3: 6 Jetpack Compose Screens, Custom Components & Nav | RadialGauge, RadarScan, Onboarding, Home, Detail, OnDemand, Checkout, Confirm, Appointments, Profile, Navigation | M2 | DONE |
| 4 | M4: Kotlin Unit Tests & verify_android_build.py | LuhnValidatorTest, PricingEngineTest, BookingRulesTest, verify_android_build.py | M2, M3 | DONE |
| 5 | M5: UI/UX Design Specialist Audit | Auditoria rigorosa de código Compose e geração de DESIGN_AUDIT_ANDROID.md | M2, M3 | DONE |
| 6 | M6: E2E Verification & Forensic Closure | Execução do script automatizado, verificação final independente e aprovação | M1, M2, M3, M4, M5 | DONE |

## Interface Contracts
### LuhnValidator ↔ CheckoutScreen / Tests
- `fun isValid(cardNumber: String): Boolean`
- Entrada: string numérica de 13 a 19 dígitos (ignora espaços).
- Saída: true se soma de verificação mod 10 == 0.

### PricingEngine ↔ DetailScreen / OnDemandScreen / Repository / Tests
- `fun calculateSlotPricing(basePrice: Double, discountPct: Int, isUrgent: Boolean): SlotPricing`
- `minPrice = 25.00`
- `finalPrice = max(minPrice, round(basePrice * (1.0 - discountPct / 100.0) * 100) / 100)`
- Badges: "Horário Econômico" (desconto >= 15% e !isUrgent) com subtexto "preço menor em horário de menor procura"; "Última Hora" (isUrgent) com subtexto "encaixe promocional para hoje".

### BookingEngine ↔ AppointmentsScreen / Repository / Tests
- `fun validateBookingSlot(slotTime: String, serviceDurationMinutes: Int, existingAppointments: List<Appointment>): BookingValidationResult`
- Blocos de 15 min; buffer de higienização de 10 min entre atendimentos consecutivos do mesmo profissional.
- Prevenção rigorosa de double-booking.

### SUS Score Calculator ↔ ProfileScreen
- `fun calculateSusScore(answers: Map<Int, Int>): Double`
- Entrada: 10 perguntas na escala Likert 1 a 5.
- Fórmula: `((soma(ímpares - 1)) + (soma(5 - pares))) * 2.5`
- Escala: 0 a 100 (Meta > 68 = Boa Usabilidade, Meta > 80.3 = Excelente).
