# 🧠 BeautyPass — Contexto e Memória de Projeto

> **Data de Atualização:** 17 de Setembro de 2026  
> **Status do Projeto:** Ativo & Publicado  
> **Repositório GitHub:** [https://github.com/viniciuspereira369/beautypass](https://github.com/viniciuspereira369/beautypass)  
> **App Online (GitHub Pages):** [https://viniciuspereira369.github.io/beautypass/beautypass_app.html](https://viniciuspereira369.github.io/beautypass/beautypass_app.html)  
> **Portal Institucional:** [https://viniciuspereira369.github.io/beautypass/](https://viniciuspereira369.github.io/beautypass/)  
> **Pitch Deck:** [https://viniciuspereira369.github.io/beautypass/pitch_deck_beautypass.html](https://viniciuspereira369.github.io/beautypass/pitch_deck_beautypass.html)  

---

## 1. Visão Geral e Proposta de Valor

O **BeautyPass** é uma plataforma e clube de assinatura de estética, beleza e bem-estar.
- **Modelo de Negócio:** Os consumidores pagam uma assinatura mensal e recebem um saldo de créditos flexíveis.
- **Uso dos Créditos:** Realização de procedimentos diversos como lavagem de cabelo, escova modeladora, cortes, manicure express, pedicure spa, cuidados com a pele, massagens relaxantes e design/laminação de sobrancelhas.
- **Diferenciais Competitivos:**
  1. Localização georreferenciada de estabelecimentos próximos (foco inicial: Jardins, São Paulo).
  2. Avaliação de qualidade e selos de excelência (*Parceiro Prime*).
  3. Aba da comunidade com comentários verificados e galeria de fotos reais de clientes pós-procedimento.
  4. Agendamento com **Encaixe Imediato** para emergências e horários de última hora com economia em créditos.

---

## 2. Linha do Tempo e Decisões de Design (Changelog Crítico)

| Data / Iteração | Solicitação do Usuário | Decisão Técnica / Implementação |
|---|---|---|
| **Fase 1** | Criação da nova proposta de app focado em assinatura de créditos, busca de salões, reviews e agendamento de urgência. | Criação do design system no Stitch e geração do app mobile-first em HTML5, CSS3 moderno e JS interativo (`beautypass_app.html`). |
| **Fase 2** | *"Retirar emojis dos quais ficaram muito feios"* | Remoção de todos os emojis amadores. Substituição por ícones vetoriais SVG minimalistas de alta definição (estilo Lucide/Heroicons) e tipografia elegante. |
| **Fase 3** | *"Ícone do raio inadequado para urgência estética; menu superior redundante com o inferior"* | Substituição do ícone de raio por ícone de relógio estético de alta precisão (`clock-urgent`). Eliminação da redundância do menu superior, consolidando a barra de navegação inferior flutuante com destaque central no botão de Encaixe Imediato. |
| **Fase 4** | *"Mudar o logo do aplicativo para um semelhante à imagem de referência"* | Criação e vetorização do logotipo BeautyPass com silhueta feminina minimalista em traços suaves e paleta Tiffany/Menta (`#0D9488` / `#14B8A6`). Aplicação no cabeçalho, modais e tela de splash. |
| **Fase 5** | *"Foto das sobrancelhas não está aparecendo no aplicativo"* | Identificado link quebrado (404) do Unsplash. Gerada imagem hiper-realista local (`treatment_eyebrows_beauty.jpg`), inserida localmente no projeto e atualizada no carrossel de categorias e na galeria de fotos dos salões. |
| **Fase 6** | *"Criar repositório no GitHub para compartilhar com outras pessoas"* | Inicialização do Git local, configuração de `.gitignore` com proteção de dados, criação do repositório remoto público `viniciuspereira369/beautypass` via GitHub API, ativação do GitHub Pages e primeiro commit/push completo da branch `main`. |
| **Fase 7** | *"Fusão dos designs de inspiração (Corridas Autônomas, Mobilidade e Social Food Delivery) com base no DESIGN.md e spec de validação"* | Alinhamento via `/grill-me` e `/plan`. Construção da **Versão C ("The Triple Fusion")** em SPA interativa: Home com prova social em tempo real, Seletor Radial de Horários analógico-digital com arco dinâmico em SVG e precificação dinâmica (Horário Econômico / Última Hora), checkout simulado com Algoritmo de Luhn e retenção de vaga de 10 min. |
| **Fase 8** | *"Correção do mapa e simplificação dos marcadores para localidades em geral sem emojis"* | Ajuste estrutural da viewport do mapa, criação de marcadores concêntricos elegantes em verde-esmeralda com etiquetas de bairros de SP (*Pinheiros*, *Jardins*, *Itaim Bibi*), traçado dinâmico de rota conectando o usuário ao destino e barra de busca livre. |
| **Fase 9** | *"Corte Inteligente sob Demanda com algoritmo de Tarifa Justa"* | Criação de fluxo Uber-style onde o usuário informa sua disponibilidade de tempo e o algoritmo calcula o preço justo equilibrando piso de custos, ociosidade e deslocamento. Radar visual de ociosidade com 1.8s de scan e apresentação em 3 opções de match (Ideal, Mais Próximo, Mais Econômico). |
| **Fase 10** | *"Fechamento de Gaps da Spec Técnica de Validação"* | Expansão para 24 salões seed em 6 bairros de SP, criação da tela e aba 'Meus Agendamentos' com histórico e cancelamento auditável, tratamento de dias sem vagas e cálculo de `hours_until` nos eventos analíticos. |
| **Fase 11** | *"Fluxo sob demanda imersivo com rota de caminhada estilo Uber"* | Criação do botão central elevado "Pedir Agora" na barra de navegação inferior, tela de seleção com cards táteis e tela de mapa dinâmico com traçado Bézier e cálculo de percurso a pé. |
| **Fase 12** | *"Remoção do card 'Corte com Preço Justo sob Demanda' da Home"* | Desacoplamento do card volumoso da tela inicial, mantendo a experiência sob demanda centralizada no botão "Pedir Agora" e tornando o feed inicial mais limpo e direto. |
| **Fase 13** | *"Ajuste das categorias populares, filtros explícitos de proximidade e economia, fotos autênticas, selo do responsável e conexões da rede social"* | Correção da filtragem por especialidade com adaptação contextual do card (serviço, foto e precificação dinâmica vinculados à categoria selecionada), ordenação real de proximidade e desconto com badges prioritários e barra de feedback, inclusão de fotos hiper-realistas locais, selo do(a) responsável técnico e cluster social de amigas em comum, com adesão estrita à regra de zero emojis (100% ícones vetoriais SVG). |
| **Fase 14** | *"Aumentar a diversidade de imagens e lugares de maneira a maximizar a autenticidade"* | Alinhamento via `/grill-me` e `/plan`. Curadoria de 24 fotos reais exclusivas de ambientes e estabelecimentos via Unsplash, 24 fundadores/responsáveis técnicos únicos com nomes e retratos diversificados, pool amplo de 19 amigas em comum com recomendações autênticas, atualização do carrossel de categorias e ajuste de `getContextualSalonImage()` para manter a identidade fotográfica autêntica de cada salão ao filtrar. Auditoria com 0 duplicatas e validação visual E2E via browser subagent. |
| **Fase 15** | *"Apresentação minimalista das imagens e conexões de amigos no feed"* | Alinhamento via `/grill-me` e `/plan`. Redução das fotos de amigos para micro-avatares de 16px com sobreposição delicada (-5px) e borda de 1px, remoção da caixa/fundo retangular pastel pesada por uma linha horizontal limpa e fluida, introdução de micro-ícone SVG de conexões de 12px e tipografia neutra em 10.5px. |
| **Fase 16** | *"Foco específico na foto no centro da tela: sugestão mais simples e minimalista"* | Alinhamento via `/grill-me` e `/plan`. Correção do seletor CSS `.card-media-wrap > img` e remoção da foto de pessoa sobre a foto do salão, eliminando o avatar gigante obstrutivo. Introdução de tag flutuante minimalista translúcida (`rgba(15, 23, 42, 0.68)`) com ponto pulsante verde (`.live-pulse-dot`) e texto limpo. Restauração do bloco estilizado de amigas em comum (`.card-mutual-friends`) com fundo menta suave, avatares em stack de 22px e frases de recomendação autênticas conforme preferência do usuário. Validação visual 100% via browser subagent. |
| **Fase 17** | *"Ajuste dos ícones dos serviços na guia Pedir Agora para ficarem condizentes com os serviços oferecidos"* | Alinhamento via `/grill-me` e `/plan`. Substituição de ícones genéricos/desconexos (microfone em Unhas, papel em Barbearia, avatar em Massagem, carinha sorridente em Estética) por 5 ícones vetoriais SVG de alta precisão (Line-art premium com traço 2.2px): Tesoura com mechas modeladoras (Cabelo), Vidro de esmalte com pincel e brilho (Unhas), Navalhete clássico articulado de lâmina aberta (Barbearia), Flor de lótus zen com pétalas fluidas (Massagem) e Perfil facial feminino sereno com estrelas de brilho/glow (Estética Facial). Sincronização em `beautypass_app.html`, `app.js` e `styles.css`. Validação visual 100% via browser subagent. |
| **Fase 18** | *"Alinhamento com a Documentação Técnica de Validação (Frente 1: Integridade de Dados, Eventos e Métricas H1/H2)"* | Reanálise da spec técnica `documenta_o_t_cnica_prot_tipo_de_valida_o.md` e alinhamento via `/grill-me`. Reformulação do Onboarding com campos em branco, identificador do participante (`#onboarding-participant`, ex.: `P01`), termos LGPD desmarcados e validação reativa do botão com código fixo `0000`. Remoção de permissão falsa de GPS e adição de banner de fallback na Home (`#home-location-warning`). Padronização rigorosa do catálogo de eventos (Seção 11): injeção automática de `session_id` e `participant_code`, `last4` em `card_validated`, `from`/`to` em `appointment_status_changed` e `has_discount` em `checkout_abandoned`. Calibração dos limiares GO para H1 &ge; 35% e H2 &ge; 50% com atualização do painel de métricas. |
| **Fase 19** | *"Sprint Final de Blindagem e Validação Científica do Protótipo (14 Tickets)"* | Implementação do Web App Google Apps Script sem servidor (`google_apps_script.js`), trava de double-booking concorrente com erro 409 simulado, buffer de latência de 1.4s, cronômetro regressivo com expiração de slot, bateria completa de 10 testes SUS na finalização de sessão, testes E2E Playwright (`test_validation_sprint.py`) aprovados com 100% de sucesso. |
| **Fase 20** | *"Implementação Integral dos Requisitos de UI/UX e Alinhamento /grill-me (Sessão 14)"* | Fechamento integral de todas as pendências das documentações: QR Code vetorial SVG Serene Mint/Teal no Voucher Digital (`.itinerary-card`) e botão "Ver QR Code & Voucher" nos agendamentos ativos; sistema interativo de Favoritos no Feed e Detalhe com persistência em `localStorage` e chip de filtro rápido; mini-galeria de 4 fotos em alta definição por categoria com troca interativa na foto Hero principal; estimativa de caminhada a pé nos 24 salões do Bottom Sheet do mapa; copy exata no checkout ("Pagamento simulado — nenhum valor será cobrado neste teste."); isenção de selo de disclaimer fotográfico e preservação da barra superior conforme alinhamento do usuário; suite E2E Playwright dedicada (`test_validation_session14.py`) com 8/8 testes aprovados e 0 regressões. |
| **Fase 21** | *"Elaboração do Aplicativo Real Android (Jetpack Compose & Material 3) com Emulador Pixel 8 Pro"* | Alinhamento via `/grill-me`. Construção da estrutura nativa oficial Google Android em `android/` (Kotlin, Jetpack Compose 1.6, Material 3, Navigation Compose, Coil e StateFlow MVI), cobrindo todo o fluxo ponta a ponta (Home com favoritos, Detalhes com mini-galeria, Checkout com Luhn/timer, Voucher com QR Code Canvas e Mapa com tempo a pé). Criação do Emulador Android Interativo de alta fidelidade do Google Pixel 8 Pro (`android_emulator.html`) com hardware controls, Logcat em tempo real e inspetor de código Compose. |
| **Fase 22** | *"Decisão Estratégica de Distribuição: Adoção do PWA (Progressive Web App Instalável)"* | Decisão formal do usuário de seguir com a distribuição do aplicativo real via PWA instalável diretamente pelo navegador móvel sem dependência de lojas de aplicativos (Play Store), viabilizando instalação com 1 toque, WebAPK na gaveta de apps, execução em tela cheia (standalone), suporte offline (Service Worker) e atualizações instantâneas. |
| **Fase 24** | *"Extração de Design e Engenharia Reversa (/designer-extracter)"* | Desconstrução pericial dos 5 layouts de referência. Criação da sandbox interativa `design_showcase.html` e tokens em `DESIGN_EXTRACT_SPEC.md` com moldura do iPhone 16 Pro, navegação dock e barra de Tweaks em tempo real. |
| **Fase 25** | *"Autenticidade Tipográfica e Ícones Especializados de Beleza (/boost)"* | Padronização em Plus Jakarta Sans com tracking contraído e numerais tabulares (`tabular-nums`). Ícones vetoriais 2px especializados (navalhete de barbeiro, pedras zen de massagem, rejuvenescimento facial, Pix oficial do BACEN e QR Code matriz real). Toast flutuante no topo em Dynamic Island (`top: 52px`). |
| **Fase 26** | *"GPS Factível, Ícone Central com Silhueta Feminina e Riqueza na Agenda (/boost)"* | Rotas de caminhada factíveis por ruas reais de SP (`AUTHENTIC_SP_WALKING_ROUTES`) com itinerário passo a passo para 24 salões. Ícone central e marcador GPS atualizados para a silhueta da mulher do BeautyPass sem texto. Agenda com 4 estados reais de horários (Disponível, Tarifa Demanda, Alta Procura e Ocupado) com sincronização em tempo real do relógio radial. |
| **Fase 27** | *"Motor de Escala Proporcional Dinâmica Universal (Auto-Scale) (/grill-me)"* | Alinhamento interativo via `/grill-me`. Implementação do motor matemático de auto-escala baseado em 390px com limites de segurança (0.85x a 1.15x), aspect-ratio 16:9 travado com `object-fit: cover` nas fotos/banners, seletor de modelos no desktop (iPhone SE 375px, iPhone 16 Pro 393px, Galaxy S24 412px, Fluido/Auto) e adaptação 100% nativa em tela cheia no smartphone/PWA. Validação com 100% em `test_autoscale_responsive.py`. |



---

## 3. Arquitetura de Arquivos do Projeto

```text
Projeto BeautyPass/
│
├── android/                 # NOVO: Projeto Nativo Android (Kotlin + Jetpack Compose)
│   ├── build.gradle.kts     # Build do Gradle raiz
│   ├── settings.gradle.kts  # Módulos do projeto (:app)
│   ├── app/
│   │   ├── build.gradle.kts # Dependências Compose Material 3, Navigation e Coil
│   │   └── src/main/java/com/beautypass/app/
│   │       ├── MainActivity.kt
│   │       ├── theme/       # Design System (Color.kt, Theme.kt, Type.kt)
│   │       ├── model/       # Data classes (Salon, Service, Appointment)
│   │       ├── data/        # Repositório reativo (SalonRepository.kt)
│   │       ├── navigation/  # NavHost & Rotas (BeautyPassNavigation.kt)
│   │       └── ui/
│   │           ├── components/ # SalonCard.kt, VoucherQrCodeView.kt
│   │           └── screens/    # HomeScreen, SalonDetailScreen, CheckoutScreen...
│   └── README.md            # Guia de execução no Android Studio
│
├── android_emulator.html    # NOVO: Emulador Android Oficial (Google Pixel 8 Pro)
├── beautypass_app.html      # APLICAÇÃO PRINCIPAL: Single Page App completa
├── index.html               # Portal Institucional / Landing page com atalhos para o app
├── styles.css               # Folha de estilos corporativa com tokens de design
├── app.js                   # Scripts de interatividade da landing page
├── pitch_deck_beautypass.html # Apresentação executiva para investidores
├── DESIGN.md                # Diretrizes de design system, paleta e componentes
├── README.md                # Apresentação do repositório para o público externo
├── CONTEXT.md               # Este arquivo: memória de decisões, arquitetura e estado
├── .gitignore               # Exclusões de arquivos temporários e certificados pessoais
│
├── treatment_*.jpg          # Fotos hiper-realistas locais dos procedimentos:
│   ├── treatment_eyebrows_beauty.jpg   # Sobrancelhas / Laminação
│   ├── treatment_hair_salon.jpg       # Cabelo & Escova
│   ├── treatment_facial_spa.jpg       # Estética Facial & Skincare
│   ├── treatment_massage_spa.jpg      # Massagem relaxante
│   └── treatment_skincare_cosmetics.jpg # Produtos e cosméticos
│
├── Logo BeautyPass.jfif     # Logotipo mestre
└── assets/                  # Ícones, gráficos e ativos estáticos
```

---

## 4. Ambiente Técnico e Credenciais

- **Ambiente Operacional:** Windows 11 / PowerShell
- **Controle de Versão:** Git 2.48.1
- **Conta GitHub:** `viniciuspereira369` (Vinicius Pereira)
- **E-mail Git:** `vinicius.pereira.54@usp.br`
- **Autenticação:** Integrada via Windows Credential Manager (`git:https://viniciuspereira369@github.com`), com escopos `repo, workflow, gist`.
- **Hospedagem Ativa:** GitHub Pages (`https://viniciuspereira369.github.io/beautypass/`)

---

## 5. Próximos Passos e Oportunidades Futuras

1. **PWA Instalável (Prioridade Imediata Aprovada na Fase 22):** Implementação do `manifest.json`, Service Worker (`sw.js`) para suporte offline e botão nativo de instalação no navegador móvel, viabilizando o uso como aplicativo baixável sem Play Store.
2. **Backend & Persistência:** Implementar camada de banco de dados (ex: Supabase ou Firebase) para autenticação de usuários, persistência do saldo de créditos e agendamentos reais em tempo real.
3. **Gateway de Pagamento:** Integrar checkout (PIX / Cartão de Crédito via Stripe ou Mercado Pago) no modal de recarga de créditos.
4. **Painel do Salão (B2B):** Desenvolver tela para os estabelecimentos parceiros gerenciarem horários ociosos para o encaixe imediato e validarem vouchers de créditos.
