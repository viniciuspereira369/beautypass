# 📱 Especificação de UI/UX: Aplicativo de Mobilidade Urbana & Microtransit

---

## 1. Visão Geral e Arquitetura da Informação (AI)

O aplicativo foi projetado com foco em viagens frequentes, redução do atrito no agendamento e transparência financeira imediata. A hierarquia prioriza o que o usuário precisa nos primeiros 3 segundos de uso: saldo disponível, rota mais provável e barra de busca de destino.

```
                    ┌─────────────────────────┐
                    │      Home (Início)      │
                    └────────────┬────────────┘
         ┌───────────────────────┼───────────────────────┐
         ▼                       ▼                       ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Orders/Viagens│     │  Stops/Paradas  │     │  Account/Conta  │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

### Barra Global de Navegação (Bottom Navigation Bar)
* **Home (Início):** Dashboard consolidado com saldo, rotas frequentes, cupons e histórico recente.
* **Orders (Viagens):** Acompanhamento de viagens passadas e ativas.
* **Stops (Paradas):** Catálogo de pontos de embarque fixos e linhas de microônibus/vans.
* **Account (Conta):** Configurações de perfil, segurança, carteira e preferências.

---

## 2. Decomposição dos Mainframes (Telas Principais)

### 2.1. Mainframe 01: Home Dashboard (Hub Central)
* **Status Bar & Header Contextual:**
  * Indicador de sistema padrão (horário `9:41`, bateria, sinal e Wi-Fi).
  * Saudação personalizada: `"Hi, Steve!"` com microcopy de suporte: `"Going somewhere?"`.
  * Ícone de notificações (*Bell*) com *badge dot* vermelho de alertas ativos.
* **Wallet Card (Widget de Saldo):**
  * Card escuro em contraste com o fundo claro.
  * Informações: Ícone de cartão, valor monetário (`$150,45`) e label `"Account balance"`.
  * CTA secundário em formato pílula clara: `"+ Top up"`.
* **Primary Search Bar:**
  * Input estilizado com ícone de pin à esquerda e placeholder `"Where to go?"`.
  * Botão de alternância para visão de mapa vetorial à direita.
* **Módulo de Rotas Sugeridas ("Your routes"):**
  * Card horizontal com thumbnail de mapa da rota.
  * Título: `"Central Park Station"` | Subtítulo: `"739 Main Street, Springfield"`.
  * Tag de tempo real verde (ETA): `"🚌 In 5 min"`.
* **Banner de Gamificação & Retenção:**
  * Superfície verde-petróleo escura com ícone de cupom.
  * Texto de benefício: `"Receive a 15% coupon on your first third orders!"`.
  * Barra de progresso linear com microcopy: `"Total orders: 1 of 3"`.
* **Histórico Recente ("Your last trip"):**
  * Lista vertical de destinos anteriores com ícones de edifícios (`Central Park Station`, `Greenwood Mall`).

---

### 2.2. Mainframe 02: Busca & Seleção de Paradas (Search / Stop Selection)
* **Header de Proximidade:**
  * Card superior destacando a parada mais próxima: `"Halte 14 - 314 Street Fighter, Springh..."` com badge de distância e tempo a pé (`0.5 km · 4 min`).
* **Campos Sequenciais de Entrada:**
  * Campo 1: `"request pick up point?"` / Endereço de partida.
  * Campo 2: `"Where to go? / Enter your destination address..."` / Endereço de destino.
* **Histórico com Datas:**
  * Lista de paradas frequentes com ícones de trajeto (*path pin*), logradouro e data da última corrida (ex.: `"Aug 21"`).

---

### 2.3. Mainframe 03: Rastreamento em Tempo Real (Live Map Tracking)
* **Viewport Cartográfico:**
  * Mapa em escala com malha viária, nomes de ruas e representação vetorial do veículo em trânsito (marcador retangular laranja sobre o traçado da rota).
* **Floating Route Card (Card Flutuante de Embarque):**
  * Componente elevado contendo linha vertical contínua conectando dois nós:
    * **Origem (Ciano):** `"Pickup point - 456 Elm Street, Springfield"`.
    * **Destino (Escuro):** `"Where to go? - Office – 739 Main Street, Springfield"`.

---

### 2.4. Mainframe 04: Modal de Pagamento & Checkout (Bottom Sheet)
* **Lista de Métodos de Pagamento:**
  * **Credit Card:** Card escuro expandido com saldo e chevron para troca rápida.
  * **Venmo:** Card claro com tag promocional verde `"Get 2% discount"` e ícone de tooltip informativo `(i)`.
  * **Revolut:** Integração de fintech parceira com tag `"Save 5%"`.
  * **Cash Payment:** Opção para pagamento em dinheiro em espécie.
  * **Ação Secundária:** Botão `"+ add payment method"`.
* **CTA Principal de Conversão:**
  * Botão de largura total (*full-width*) com cantos arredondados: `"Book ride"`, estrategicamente posicionado na zona de alcance do polegar (*thumb zone*).

---

### 2.5. Mainframe 05: Interface Dark de Atalhos Rápidos
* **Quick Access Bar:**
  * Input de destino acompanhado de três atalhos em botões de ação rápida: `"Take me home"`, `"Office"` e `"Favorite"`.
* **Drawer Inferior Expansível:**
  * Seções divididas em `"Your last trip"` e `"Popular destination"`, permitindo solicitação com um toque diretamente a partir dos favoritos da comunidade ou do usuário.

---

## 3. Mapeamento de Keyframes & Fluxo de Interação

| Estágio | Gatilho (Trigger) | Comportamento da Interface | Feedback / Transição |
| :--- | :--- | :--- | :--- |
| **KF 01: Idle State** | Abertura do app | Exibe Home Hub com saldo, rota próxima e cupom ativo | Carregamento suave (*fade-in*) dos cards |
| **KF 02: Iniciar Busca** | Tap no campo *"Where to go?"* | O input expande para o topo e abre o Mainframe 02 | Transição compartilhada de elementos (*Hero animation*) |
| **KF 03: Definir Rota** | Seleção da parada de destino | Abre o mapa com traçado da rota e cálculo de valor | O traçado da rota desenha dinamicamente no mapa |
| **KF 04: Seleção de Pagamento** | Tap em avançar para pagamento | Sobe o Bottom Sheet modal cobrindo 60% da tela | Animação de subida com *backdrop blur* no mapa |
| **KF 05: Confirmação & Tracking** | Tap em *"Book ride"* | Fecha o modal de pagamento e ativa o modo rastreamento | Zoom no veículo em movimento com card de rota no topo |

---

## 4. Sistema de Componentes (Design System)

### 4.1. Cores e Tokens Visuais
* **Primary / Dark Surface:** `#0B2B26` / `#133E3B` (Verde-petróleo escuro sofisticado para cards principais e botões primários).
* **Background Neutro:** `#F8F9FA` / `#FFFFFF` (Garante alto contraste e legibilidade).
* **Semantic Accent (Sucesso / Desconto):** `#E8F5E9` (Fundo de tag) com texto em `#2E7D32` (Verde esmeralda).
* **Alert Indicator:** `#E53935` (Ponto de alerta de notificações).
* **Textos Secundários:** `#8E8E93` (Subtítulos, datas e endereços complementares).

### 4.2. Tipografia
* **Estilo:** Sem serifa geométrica moderna (Inter, SF Pro ou similar).
* **Escala:**
  * *H1 (Saudação):* 22px / Bold
  * *H2 (Títulos de Seção):* 16px - 18px / Semi-bold
  * *Body (Endereços e nomes de locais):* 14px / Regular & Medium
  * *Badges & Timestamps:* 11px - 12px / Medium

### 4.3. Anatomia dos Principais Componentes
* **Address Card Component:**
  ```
  [ Ícone de Categoria ] [ Título do Local ]       [ Data / Ação ]
                         [ Endereço Completo ]
  ```
* **Discount Tag Component:**
  * Pílula com fundo verde translúcido, bordas arredondadas e ícone de etiqueta ou porcentagem em escala reduzida.
* **Progress Gamification Widget:**
  * Contêiner com cantos arredondados de 16px, texto descritivo e barra de carregamento com transição linear entre os passos concluídos e totais.

---

## 5. Diretrizes de UX & Heurísticas Aplicadas

1. **Eficiência de Uso (Lei de Fitts):** O botão `"Book ride"` e os atalhos rápidos (*"Take me home"*, *"Office"*) estão localizados na porção inferior da tela, facilitando a navegação com uma só mão.
2. **Previsibilidade e Confiança:** A presença constante da tag de tempo real (`"In 5 min"`) reduz a ansiedade de espera antes mesmo do usuário confirmar a corrida.
3. **Reconhecimento em vez de Memória:** O sistema prioriza destinos recentes e paradas próximas com endereços visíveis para eliminar a necessidade de digitação repetitiva.