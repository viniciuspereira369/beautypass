# 🚗 Especificação de UI/UX: Fluxo de Agendamento de Mobilidade Autônoma

## 1. Visão Geral e Arquitetura da Informação (IA)

O fluxo tem como foco a conversão de viagens programadas (*Scheduled Rides*) para serviços de mobilidade autônoma de padrão premium. A estrutura reduz a fricção cognitiva associada a seletores de data/hora tradicionais e reforça a segurança do usuário antes e após a confirmação.

```
┌─────────────────────────────────┐
│  Ecrã 1: Schedule Pickup        │
│  (Configuração de Data e Hora)  │
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│  Ecrã 2: Pick-up & Fleet        │
│  (Visualização de Frota/Preço)  │
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│  Ecrã 3: Trip Confirmed         │
│  (Resumo, Garantias e Políticas)│
└─────────────────────────────────┘
```

---

## 2. Decomposição dos Mainframes (Telas Principais)

### 2.1. Mainframe 01: Schedule Pickup (Agendamento Temporal)

* **Top App Bar:**
  * Botão de retorno (*Back Chevron*) inserido em container quadrado com cantos arredondados (`border-radius: 12px`).
  * Título central em tipografia semi-bold: `"Schedule Pickup"`.
  * Ação secundária à direita: Botão de ajuda e diretrizes com ícone informativo `[!]`.
* **Segmented Control de Recorrência:**
  * Alternador de frequência em cápsula dupla:
    * **Weekly (Ativo):** Fundo verde-esmeralda sólido (`#0E8A73`), texto em alto contraste branco.
    * **Monthly (Inativo):** Fundo transparente neutro com contorno sutil.
* **Seletor de Data (Horizontal Date Strip):**
  * Título tipográfico de destaque: `"September 26"`.
  * Carrossel horizontal exibindo os dias da semana (`Tue`, `Wed`, `Thu`, `Fri`, `Sat`, `Sun`, `Mon`) e dias numéricos (`23` a `29`).
  * Indicador de seleção: Círculo preto sólido contornando o dia ativo (`26`) com linha curva de apoio visual.
* **Controlador Radial de Hora (Analog-Digital Gauge Clock):**
  * Cabeçalho de contexto com seletores de mês (`September v`) e ano (`2026`).
  * Mostrador semicircular graduado estilo velocímetro com ponteiro triangular verde invertido indicando o horário (`8`).
  * Chave de período central: Botões compactos `AM` e `PM` para prevenção de ambiguidade de horário.
* **Footer de Decisão:**
  * CTA Primário: `"Confirm date and time"` (botão full-width preto sólido).
  * CTA Secundário / Alternativo: `"Book ride for now"` (estilo outline/neutro para converter corridas imediatas sem agendamento).

---

### 2.2. Mainframe 02: Pick-up (Seleção de Veículo & Visualização de Frota)

* **Top Bar Flutuante:**
  * Componentes consistentes de navegação com o título de etapa `"Pick-up"`.
* **Viewport de Mapa e Frota Conectada:**
  * Mapa vetorial clean em tons de cinza com vias bem delimitadas.
  * Trajeto dinâmico desenhado em linha verde-esmeralda conectando os pontos de rota.
  * Indicadores de frota ativa (*Live Fleet Pins*): Veículos autônomos verdes distribuídos no mapa, reforçando a prontidão do serviço.
* **Bottom Sheet de Seleção de Categoria:**
  * **Widget de Retenção & Cashback:**
    * Pílula com ícone de veículo: `"Earning 10% credit back — 8 rides left"` com chevron expansível.
  * **Card Imersivo da Categoria:**
    * Render 3D de alta qualidade ilustrando a via autônoma moderna.
    * Badge flutuante de lotação/capacidade de passageiros no canto superior da imagem.
    * Metadados da corrida:
      * Categoria do veículo: `"Comfort"`.
      * Valor garantido: `$ 125.16`.
      * Bonificação: `"Earn 10"`.
* **Área de Checkout:**
  * Botão Primário: `"Schedule ride"` em destaque.
  * Botão de Ação Rápida: Ícone de grupo para reservas com rateio ou múltiplos passageiros (*Carpooling / Ride-share*).

---

### 2.3. Mainframe 03: Trip Confirmed (Confirmação e Políticas do Serviço)

* **Top Bar:**
  * Título de validação de estado: `"Trip Confirmed"`.
* **Mini-Map de Contexto:**
  * Rota confirmada em visualização reduzida mantendo a continuidade visual.
* **Card de Itinerário (Route Stepper Card):**
  * Marcador de partida: Ícone de usuário + `"Harbour Views"` (Dubai - United Arab Emirates).
  * Linha conectora vertical.
  * Marcador de destino: Ícone predial + `"Media One Hotel"` (Al Felak St - Media 1 Tower - Exit 32).
* **Módulo de Políticas e Alívio de Fricção:**
  * **Tempo de Espera:** Card com ícone de relógio verificado informando `"Waiting time — 5 minutes of waiting time included"`.
  * **Cancelamento Gratuito:** Card com ícone de escudo/alerta informando `"Free cancellation — Cancel or edit your reservation free"`.
* **Footer de Finalização:**
  * Botão Primário: `"Back home"` (retorno ao painel principal do app).
  * Botão Secundário: `"Schedule another ride"` (estímulo à recorrência).

---

## 3. Mapeamento de Keyframes & Fluxo de Interação

| Keyframe | Gatilho (Trigger) | Comportamento do Sistema | Feedback Visual / Motion |
| :--- | :--- | :--- | :--- |
| **KF 01: Configuração Temporal** | Rotação do gauge radial ou tap no carrossel | Atualiza os valores de data e hora em tempo real | Vibração tátil sutil (*haptic feedback*) e rotação do ponteiro verde |
| **KF 02: Validação de Horário** | Tap em *"Confirm date and time"* | Transição de tela para consulta da malha de frota | Animação de colapso do relógio e expansão da tela do mapa |
| **KF 03: Seleção de Categoria** | Tap ou swipe lateral nos cards de veículo | Alterna preços, fotos e estimativa de cashback | Atualização instantânea dos valores no footer |
| **KF 04: Confirmação de Reserva** | Tap em *"Schedule ride"* | Processamento do agendamento com backend | Entrada do modal de confirmação com efeito de elevação (*slide up*) |

---

## 4. Sistema de Componentes & Design Tokens (Atomic Design)

### 4.1. Tokens de Cor

* **Primary Accent (Verde Esmeralda):** `#0E8A73` / `#007A5E` (usado em botões ativos, trajetos no mapa e ponteiro de seleção).
* **Primary Dark (Preto Profundo):** `#0D0E10` / `#000000` (utilizado para CTAs primários, pinos principais e destaque de data).
* **Background Neutro:** `#F4F6F8` (cinza muito suave que dá sensação clean e moderna).
* **Card Surface:** `#FFFFFF` com cantos arredondados acentuados (`border-radius: 16px` a `24px`).
* **Bordas e Linhas de Separação:** `#E5E7EB` (divisões sutis sem sobrecarregar o visual).

### 4.2. Tipografia

* **Fonte Sugerida:** *SF Pro Display*, *Plus Jakarta Sans* ou *Inter*.
* **Escala Tipográfica:**
  * *Headline de Data:* ~28-32px / Bold.
  * *Títulos de Ecrã:* ~17-18px / Semi-bold.
  * *Valores Financeiros:* ~20-22px / Bold.
  * *Corpo de Texto & Endereços:* ~13-14px / Regular & Medium.
  * *Labels Auxiliares:* ~11-12px / Regular.

### 4.3. Estrutura de Moléculas e Organismos

```
[ Organismo: Stepper de Rota ]
  ├── [ Ponto de Partida ]: Ícone Círculo + Nome do Ponto + Cidade/País
  ├── [ Linha Conectora ]: Traço vertical contínuo cinza
  └── [ Ponto de Chegada ]: Ícone Prédio + Nome do Ponto + Logradouro detalhado

[ Organismo: Guarantee Tile ]
  ├── [ Container Arredondado Branco ]
  ├── [ Ícone Semântico em Pílula Cinza ]
  ├── [ Título em Negrito ]: "Free cancellation"
  └── [ Subtítulo Explicativo ]: "Cancel or edit your reservation free"
```

---

## 5. Análise de UX e Heurísticas Aplicadas

1. **Prevenção de Erros (Nielsen #5):** O controle com botões fixos `AM` e `PM` minimiza a chance de agendamentos acidentais para horários invertidos (ex.: marcar às 8h da noite acreditando ser manhã).
2. **Saída de Emergência e Flexibilidade (Nielsen #3):** A opção *"Book ride for now"* evita que o usuário se sinta bloqueado caso tenha entrado no agendamento por engano e queira apenas um carro no momento.
3. **Redução da Ansiedade Pós-Compra:** Os módulos de *"Waiting time (5 min included)"* e *"Free cancellation"* logo abaixo da confirmação reduzem o estresse pós-reserva e diminuem chamados de suporte.
4. **Alinhamento com o Mundo Real (Nielsen #2):** O seletor de horas em formato radial imita mostradores analógicos e velocímetros, tornando a regulagem intuitiva pelo gesto de rotação.