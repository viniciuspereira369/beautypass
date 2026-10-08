# 📱 BeautyPass — Aplicativo Android Nativo (Jetpack Compose)

Aplicativo oficial do **BeautyPass** desenvolvido com arquitetura nativa moderna do Google Android: **Kotlin**, **Jetpack Compose (Material 3)**, **MVVM** e **Navigation Compose**.

---

## 🏗️ Arquitetura do Projeto

```text
android/
├── build.gradle.kts                # Configurações globais do Gradle
├── settings.gradle.kts             # Módulos incluídos (:app)
├── gradle.properties               # JVM args e flags do AndroidX
└── app/
    ├── build.gradle.kts            # Dependências Compose, Coil, Material 3
    └── src/main/
        ├── AndroidManifest.xml     # Permissões de Internet e Localização
        ├── res/                    # Cores, strings e temas XML
        └── java/com/beautypass/app/
            ├── MainActivity.kt     # Ponto de entrada Android (ComponentActivity)
            ├── theme/              # Design System Material 3 (Color, Type, Theme)
            ├── model/              # Modelos de dados (Salon, Service, Appointment, SlotPricing)
            ├── data/               # Repositório de dados e regras de negócio (SalonRepository)
            ├── navigation/         # Rotas e NavHost (BeautyPassNavigation)
            └── ui/
                ├── components/     # Componentes reutilizáveis (SalonCard, VoucherQrCodeView)
                └── screens/        # Telas do fluxo completo:
                    ├── home/       # Feed com categorias e favoritos
                    ├── detail/     # Detalhe com mini-galeria de 4 fotos e seletor dinâmico
                    ├── checkout/   # Checkout com timer de 10 min e aviso de teste
                    ├── confirm/    # Voucher com QR Code vetorial e trajeto a pé
                    ├── appointments/ # Meus Agendamentos (Ativos/Histórico) e QR Code
                    └── map/        # Mapa com marcadores e bottom sheet com tempo a pé
```

---

## 🚀 Como Executar no Android Studio

1. Abra o **Android Studio** (versão Iguana, Jellyfish ou Koala recomendada).
2. Selecione **File > Open...** e aponte para a pasta `android/` deste projeto.
3. Aguarde o Android Studio sincronizar o projeto via Gradle (**Sync Project with Gradle Files**).
4. Selecione um dispositivo físico conectado via USB (com depuração ativada) ou crie um emulador AVD (ex.: *Pixel 8 Pro com API 34*).
5. Clique no botão **Run 'app'** (`Shift + F10`) ou execute no terminal:
   ```bash
   ./gradlew installDebug
   ```

---

## 🎨 Destaques de UI/UX Implementados

- **Design System Serene Mint/Teal:** Material Design 3 com tokens consistentes.
- **Botão de Favoritos Ergonômico:** Posicionado no canto inferior direito (`bottom: 12dp; right: 12dp;`) no `SalonCard`, desobstruindo a métrica de quilômetros no topo direito.
- **Mini-Galeria de 4 Fotos por Salão:** Alternância reativa da foto principal Hero com transição fluida.
- **Voucher Digital com QR Code Vetorial:** Renderizado via Canvas Compose nativo com padrão Serene Mint e identificador único de check-in.
- **Estimativas de Caminhada a Pé:** Cálculo proporcional à distância em todos os estabelecimentos do Bottom Sheet do mapa.
