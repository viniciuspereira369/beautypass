# 📱 BeautyPass — Aplicativo Android Nativo (Jetpack Compose)

Aplicativo oficial do **BeautyPass** desenvolvido com arquitetura nativa moderna do Google Android: **Kotlin**, **Jetpack Compose (Material 3)**, **MVVM**, **Room Database** e **Navigation Compose**.

---

## 🏗️ Arquitetura do Projeto

```text
android/
├── build.gradle.kts                # Configurações globais do Gradle
├── settings.gradle.kts             # Módulos incluídos (:app)
├── gradle.properties               # JVM args e flags do AndroidX
└── app/
    ├── build.gradle.kts            # Dependências Compose, Coil, Room, Material 3
    └── src/
        ├── test/java/com/beautypass/app/
        │   ├── BookingRulesTest.kt      # Testes de slots de 15 min e buffer de 10 min
        │   ├── LuhnValidatorTest.kt     # Validação de cartões em tempo real
        │   ├── NotificationHelperTest.kt # Payloads e canais de notificação
        │   ├── PricingEngineTest.kt     # Precificação dinâmica, taxa de deslocamento e cancelamento
        │   └── RoomDatabaseTest.kt      # Mapeamento Room v2, B2B rules e H3 LOI
        └── main/
            ├── AndroidManifest.xml # Permissões de Internet, Localização e Notificações
            ├── res/                # Cores, strings e temas XML
            └── java/com/beautypass/app/
                ├── MainActivity.kt # Ponto de entrada Android (ComponentActivity)
                ├── theme/          # Design System Serene Mint/Teal (Color, Type, Theme)
                ├── model/          # Modelos de dados (Salon, Service, Appointment, B2BDiscountRule, H3Report)
                ├── data/           # Repositórios, motores de cálculo e banco local:
                │   ├── PricingEngine.kt   # Cálculo dinâmico, taxas de deslocamento e cancelamento
                │   ├── BookingEngine.kt   # Regras de agendamento e sanitização
                │   ├── SalonRepository.kt # Estado reativo StateFlow, regras B2B e validação H3
                │   └── local/             # Room Database v2 (BeautyPassDatabase, DAOs, Entidades)
                ├── notification/   # BeautyPassNotificationHelper (canais e notificações locais)
                ├── navigation/     # Rotas e NavHost (BeautyPassNavigation)
                └── ui/
                    ├── components/ # Componentes (SalonCard, RadialGaugeTimeSelector, VoucherQrCodeView)
                    └── screens/    # Telas do fluxo completo:
                        ├── home/         # Feed com Grade 4x2 Circular de Parceiros e categorias
                        ├── detail/       # Detalhe com mini-galeria de 4 fotos e Dual Fulfillment Switch
                        ├── checkout/     # Checkout seguro com timer de 10 min, endereço a domicílio e notas
                        ├── confirm/      # Voucher com QR Code vetorial, badge de modalidade e notas
                        ├── appointments/ # Meus Agendamentos com badge, notas e cancelamento dinâmico
                        ├── partner/      # Painel B2B (Agenda -25%, Regras 15%-35% com Repasse, Check-in & H3)
                        ├── profile/      # Perfil, métricas H1/H2, Escala SUS e status da Hipótese H3
                        ├── ondemand/     # Pedir Agora com Match Ideal, Mais Próximo e Mais Econômico
                        ├── map/          # Mapa com marcadores e bottom sheet com tempo a pé
                        └── onboarding/   # Identificação e consentimento LGPD
```

---

## 🚀 Como Executar no Android Studio

1. Abra o **Android Studio** (versão Iguana, Jellyfish, Koala ou superior).
2. Selecione **File > Open...** e aponte para a pasta `android/` deste projeto.
3. Aguarde o Android Studio sincronizar o projeto via Gradle (**Sync Project with Gradle Files**).
4. Selecione um dispositivo físico conectado via USB (com depuração ativada) ou crie um emulador AVD (ex.: *Pixel 8 Pro com API 34*).
5. Clique no botão **Run 'app'** (`Shift + F10`) ou execute no terminal:
   ```bash
   ./gradlew installDebug
   ```

---

## 🎨 Destaques de UI/UX e Funcionalidades (Paridade 100% com Web/PWA)

- **Design System Serene Mint/Teal:** Material Design 3 com tokens consistentes, sem emojis em UI (100% vetores SVG e Material Icons).
- **Grade Circular 4x2 de Parceiros em Destaque:** 8 salões com anel Serene Mint, selos de verificação e notas visagistas.
- **Dual Fulfillment Mode:** Alternância instantânea entre **No Salão** e **A Domicílio** no detalhe do salão.
- **Taxa de Deslocamento Dinâmica para Home Care:** Base de R$ 10,00 nos primeiros 2 km + R$ 5,00 por km adicional calculada no checkout.
- **Notas do Cliente ("Add note"):** Campo de observações especiais integrado ao checkout, voucher digital, lista de agendamentos e painel do parceiro.
- **Painel do Salão Parceiro B2B (`PartnerB2BScreen`):**
  - *Aba 1 (Agenda):* Gestão de capacidade com ativação de promoções de última hora (-25% OFF).
  - *Aba 2 (Regras & Repasse):* Configurador de descontos (15% a 35%) com simulador de repasse líquido (15% take rate).
  - *Aba 3 (Check-in & H3):* Validação de voucher QR Code e formulário de assinatura de Carta de Intenção (LOI - Hipótese H3).
- **Room Database v2:** Entidade `AppointmentEntity` atualizada com suporte completo a modalidades, notas e taxas com `fallbackToDestructiveMigration()`.
- **Política de Cancelamento Dinâmica:** Retenção de 30% em cancelamentos com menos de 24 horas e estorno integral de 100% para cancelamentos prévios.
