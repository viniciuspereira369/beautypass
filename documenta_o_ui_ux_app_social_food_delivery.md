# 🍔 Especificação de UI/UX: Aplicativo de Social Food Delivery & Descoberta Colaborativa

## 1. Visão Geral e Arquitetura da Informação (AI)

O produto posiciona-se como uma plataforma de **Social Food Delivery**, fundindo conveniência logística com mecânicas de **Comércio Social (*Social Commerce*)**. O diferencial central da experiência reside na eliminação da fadiga de decisão através da prova social imediata (exibições de pedidos recentes feitos por amigos e recomendações colaborativas).

```
                      ┌─────────────────────────┐
                      │      Home (Início)      │
                      └────────────┬────────────┘
         ┌───────────────────┬─────┴─────┬───────────────────┐
         ▼                   ▼           ▼                   ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ Search / Mapa   │ │ Friends (Rede)  │ │My Order (Sacola)│ │ Profile (Conta) │
└─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘
```

### Barra Global de Navegação (Bottom Navigation Bar)
Ancorada na base da tela com suporte à *Safe Area* do iOS e Android:
* **Home (Início):** Feed principal com categorias visuais, atalhos rápidos e o módulo social *"Friends are eating"*.
* **Search (Busca & Mapa):** Exploração aberta por texto associada à camada de geolocalização e raio de entrega.
* **Friends (Amigos):** Hub de atividade social, listas compartilhadas, pratos favoritos de contatos e avaliações entre pares.
* **My Order (Pedidos):** Rastreamento de entregas em andamento, pedidos em grupo e histórico de faturas.
* **Profile (Perfil):** Gerenciamento de endereços, preferências dietéticas, cartões e configurações.

---

## 2. Decomposição dos Mainframes (Telas Principais)

### 2.1. Mainframe 01: Home / Feed Social de Descoberta

* **Header Superior Contextual:**
  * Seletor de endereço: *"Deliver to: 1226 University Dr"* acompanhado de chevron expansível.
  * Ações rápidas à direita: Ícone de notificações (*Bell*) e ícone de sacola/carrinho (*Cart*).
* **Barra de Pesquisa de Alta Empatia:**
  * Input arredondado em estilo pílula com o placeholder: *"What are you craving?"*.
  * Ícone de lupa à esquerda e atalho de acesso direto ao mapa vetorial à direita.
* **Carrossel Horizontal de Categorias (Food Categories):**
  * Cards quadrados suaves com ilustrações 3D representativas:
    * **Deals** (tag vermelha com `%`).
    * **Grocery** (laticínios e frutas).
    * **Pizza** (fatia estilizada).
    * **Breakfast** (ovos com bacon).
    * **Burger** (hambúrguer clássico).
* **Trilha de Filtros Rápidos (Filter Chips Bar):**
  * Chips horizontais clicáveis:
    * `🚶 Pickup` (modalidade para retirada no balcão).
    * `Delivery fee ▾` (seletor de taxa de entrega máxima).
    * `Under 30 min` (filtro de urgência/tempo de entrega).
* **Módulo de Prova Social ("Friends are eating"):**
  * Título de seção com link de navegação rápida `->`.
  * Cards de pratos com destaque para a sobreposição de tags sociais verdes no topo da foto:
    * Card 1: *"Creamy Shrimp Soup"* com tag `#Sarah just ordered this` | *$3.69 Delivery fee* | *Ordered 2h ago* | *★ 4.6 (123) · 25 min*.
    * Card 2: *"Sandwich with..."* com tag `#Affikri just ordered this` | *$8.52 Delivery fee* | *★ 4.4 (236) · 30 min*.
* **Seção de Proximidade ("Popular near you"):**
  * Vitrine visual de alta densidade gastronômica priorizando pratos bem avaliados num raio curto.

---

### 2.2. Mainframe 02: Geo-Discovery & Map View (Busca Cartográfica)

* **Top Bar & Alternador de Cumprimento:**
  * Botão de voltar circular (`<-`).
  * Toggle encapsulado central: **Delivery (Ativo - Verde)** vs. **Pickup (Inativo - Branco)**.
* **Barra de Busca e Filtros Avançados:**
  * Input flutuante com texto: *"Search for food, store, and restaurants"*.
  * Lista de filtros com scroll horizontal: *"Open now"*, *"★ 4 Stars and up"*, *"🏷 Offers"* e *"Cuisines"*.
* **Camada Cartográfica Interativa (Map Viewport):**
  * Mapa vetorial clean em tons claros destacando regiões e cidades (ex.: Sinaloa, Durango, Mazatlán).
  * **Sistema de Marcadores (Pins):**
    * *Pins Cinzas/Neutros:* Restaurantes cadastrados fora do foco principal da busca.
    * *Pins Verdes com Anel Concêntrico:* Estabelecimentos com ofertas ativas, alta demanda ou pedidos frequentes pela rede de amigos.
* **Bottom Sheet Flutuante (Card de Pré-visualização):**
  * Card flutuante com cantos arredondados contendo fotografia do produto (*"Amber Iced Tea"*), ícone de favoritos (coração), taxa de entrega (`$2.87`), tempo estimado (`30 min`), avaliação (`★ 4.4`) e distância (`0.8 mi`).

---

### 2.3. Mainframe 03: Storefront & Menu ("Gourmet Burger")

* **Header Hero Imersivo:**
  * Fotografia do prato principal em tela cheia na porção superior.
  * Botões flutuantes em vidro (*Glassmorphism Circular Buttons*): Voltar, Buscar no cardápio, Adicionar aos favoritos e Menu de contexto (`...`).
* **Informações e Reputação do Restaurante:**
  * Indicador de disponibilidade: *"Open now"* em verde vivo.
  * Título H1 em destaque: *"Gourmet Burger"*.
  * Metadados: *"★ 4.6 (236) · 219 Westren Ave · 2.5 mi >"*.
* **Ações Operacionais Rápidas (Pill Buttons):**
  * Três botões utilitários:
    * `🕒 Schedule` (agendamento para horário programado).
    * `👥 Group order` (pedido compartilhado entre múltiplos dispositivos/amigos).
    * `📄 Add note` (instruções específicas para a cozinha/embalagem).
* **Segmented Control de Fulfillment:**
  * Alternador de três modos: **Delivery (Selecionado)** | **Pickup** | **In-Store**.
  * Link de transparência: *"Fees info"*.
* **Menu de Navegação de Itens:**
  * Filtro lateral + Categorias: **All (Ativo verde)**, **Popular items**, **Build for you**, **Dessert**.
* **Vitrine Promocional ("Special offers"):**
  * Cards horizontais com tag de destaque: *"🏆 Top offer · Buy 1, Get 1 Free"*.
* **Recomendações Sociais ("Friend also like"):**
  * Vitrine inferior dedicada a itens recomendados especificamente por amigos que já frequentaram a casa.

---

## 3. Mapeamento de Keyframes & Fluxo de Interação (UX Motion)

| Estágio | Gatilho (Trigger) | Comportamento da Interface | Transição / Microinteração |
| :--- | :--- | :--- | :--- |
| **KF 01: Inspiração Social** | Scroll no Feed Inicial | O módulo *"Friends are eating"* exibe pratos consumidos recentemente por contatos | Aparição com *fade-in* suave e destaque na tag social verde |
| **KF 02: Transição para Mapa** | Tap no ícone de mapa na barra de busca | A Home se recolhe e o mapa surge com zoom suave nos estabelecimentos próximos | Animação em cascata dos pins verdes concêntricos (*pin drop*) |
| **KF 03: Seleção no Mapa** | Tap em um pin verde ou deslize no card flutuante | Centraliza o estabelecimento no mapa e atualiza o card flutuante inferior | Transição horizontal suave do Bottom Sheet |
| **KF 04: Entrada no Restaurante** | Tap no card do estabelecimento | Expande a foto do prato para o topo da tela do cardápio (*Shared Element Transition*) | Efeito de elevação da folha de itens com efeito de vidro nos botões |
| **KF 05: Pedido Colaborativo** | Tap em *"Group order"* | Abre modal para compartilhar link de carrinho com amigos | Geração de link copiável e exibição de avatares dos membros conectados |

---

## 4. Sistema de Componentes & Design Tokens (Atomic Design)

### 4.1. Tokens de Cores

* **Primary Accent Green:** `#15803D` / `#16A34A` (Verde para tags sociais `#User just ordered`, selos de abertura *"Open now"* e estado ativo).
* **Promo Accent Red:** `#DC2626` (Usado para badges de descontos, ofertas e selo de Deals).
* **Canvas Background:** `#F9FAFB` (Branco/Cinza suave para evitar fadiga ocular).
* **Dark Typography:** `#111827` (Preto neutro profundo garantindo acessibilidade e contraste AAA).
* **Muted Grey:** `#6B7280` (Textos de apoio, distâncias e separadores).
* **Glass Surface:** `rgba(0, 0, 0, 0.45)` com `backdrop-filter: blur(8px)` (utilizado nos botões sobrepostos à imagem hero).

### 4.2. Tipografia

* **Fonte Base:** *Inter*, *Plus Jakarta Sans* ou *SF Pro*.
* **Escala Tipográfica:**
  * *H1 (Nome da Loja):* 22px / Semi-bold.
  * *H2 (Títulos de Seção):* 18px / Bold.
  * *Item Names:* 15px / Semi-bold.
  * *Body & Metadados:* 13px - 14px / Regular.
  * *Social Badges & Timestamps:* 11px / Medium.

### 4.3. Anatomia de Componentes

```
[ Componente: Social Card ]
┌──────────────────────────────────────────────┐
│ [ #Sarah just ordered this ]    [ ♡ Favorito]│  <- Imagem do Prato
└──────────────────────────────────────────────┘
  Creamy Shrimp Soup                              <- Título (15px Bold)
  $3.69 Delivery fee  •  Ordered 2h ago           <- Taxa + Prova Social
  ★ 4.6 (123)  •  25 min                         <- Rating + Tempo
```

```
[ Componente: Fulfillment Switcher ]
┌──────────────────────────────────────────────┐
│  (•) Delivery   │    Pickup    │   In-Store  │
└──────────────────────────────────────────────┘
```

---

## 5. Diretrizes de UX e Psicologia de Produto

1. **Gatilho de Prova Social (*Social Proof & Bandwagon Effect*):**
   * Ver que amigos reais (*Sarah*, *Affikri*) acabaram de pedir um prato específico elimina a desconfiança sobre qualidade ou sabor, reduzindo o tempo médio de navegação até o fechamento do carrinho.
2. **Redução de Fricção em Grupos (*Collaborative Ordering*):**
   * O botão *"Group order"* diretamente visível no cabeçalho do restaurante simplifica o processo de pedidos em equipe ou entre amigos, centralizando cobranças e reduzindo a perda de conversão.
3. **Prevenção de Surpresas Financeiras:**
   * A taxa de entrega (*"$3.69 Delivery fee"*) é exibida explicitamente no próprio card do feed, evitando o abandono no checkout por taxas ocultas.
4. **Ergonomia e Zonas de Toque (*Thumb Zone*):**
   * A divisão entre navegação horizontal de categorias e scroll vertical no cardápio respeita os padrões naturais de exploração com apenas uma mão.