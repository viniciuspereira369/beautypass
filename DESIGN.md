# Design System & Telas: BeautyPass — Light Clean Wellness
**Stitch Project:** `BeautyPass App` (ID: `1775592881919060389`)  
**Design System Asset ID:** `assets/6532485547744953053`  
**Primary Anchor Color:** `#5ce2e7` (Electric Aqua Glow)  
**Estética:** Light Clean Wellness, Minimalista, Visual-First & Movimento Fluido  

---

## 1. Conceito do Aplicativo & Proposta de Valor

O **BeautyPass** é uma plataforma por assinatura de créditos mensais para serviços de beleza, autocuidado e estética avançada (cabelo, manicure/pedicure, sobrancelhas, massagem, skincare e tratamentos clínicos).

### Funcionalidades Centrais Mapeadas:
1. **Carteira de Créditos Unificada**:
   - Assinatura mensal com créditos recarregáveis (ex: 120 créditos/mês).
   - Anel visual de uso em degradê aqua (`#5ce2e7`) inspirado em medidores de consumo modernos.
   - Cada procedimento tem um valor transparente em créditos (ex: Manicure: 15 cr, Escova: 25 cr, Limpeza de Pele: 35 cr).
2. **Geolocalização & Mapa de Salões Próximos**:
   - Mapa dinâmico com indicador de posição do usuário e pins customizados em `#5ce2e7` com valores de créditos.
   - Bottom sheet arrastável com distância em tempo real (ex: 450m), selo de parceiro verificado e status de abertura.
3. **Perfil do Estabelecimento com Avaliações & Depoimentos Reais**:
   - Galeria de fotos de alta fidelidade das instalações e dos resultados dos procedimentos.
   - Aba dedicada de avaliações comunitárias com nota geral (4.9 ★), tags de diferenciais (*Ambiente Impecável*, *Pontualidade 100%*) e fotos enviadas pelos próprios clientes.
4. **SOS Beleza — Agendamento de Urgência (Broadcast Express)**:
   - Modo de encaixe imediato (em até 30-45 minutos).
   - Transmissão em tempo real tipo broadcast com radar concêntrico animado em `#5ce2e7`.
   - Feed de propostas imediatas de salões parceiros próximos com profissionais disponíveis na hora.

---

## 2. Paleta de Cores e Tokens de Design

| Token | Hex / Valor | Papel Funcional |
| :--- | :--- | :--- |
| **Electric Aqua Glow** | `#5ce2e7` | **Âncora Primária:** Ações principais (CTAs), anel de créditos, botão SOS, pins do mapa e abas ativas. |
| **Vivid Aqua Highlight** | `#6cf0f5` | Hover de botões, gradientes e reflexos de luz. |
| **Deep Clinical Teal** | `#006669` / `#0D9488` | Tom de apoio de alto contraste para textos em botões e ícones clínicos. |
| **Pure White** | `#FFFFFF` | Superfície de cards flutuantes, campo de busca e modais. |
| **Porcelain Mist Canvas** | `#F8FAFC` | Fundo principal da aplicação (clean, arejado e anti-fadiga). |
| **Mint Tint Wash** | `#F0FDFA` | Fundo suave para badges de status e recipientes de destaque. |
| **Imperial Amber Gold** | `#F8A010` / `#F59E0B` | Estrelas de avaliação (ex: 4.9 ★) e selo VIP BeautyPass. |
| **Emerald Active** | `#10B981` | Status "Aberto agora" e confirmação de encaixe imediato. |
| **Coral Alert** | `#B31B25` / `#FF6B6B` | Indicador de cancelamento e alertas de expiração. |
| **Charcoal Ink** | `#2C2F31` / `#0F172A` | Tipografia de títulos e dados de leitura imediata. |

---

## 3. Telas Geradas no Google Stitch

### 📱 Tela 1: Home / Dashboard do Assinante
* **Screen ID:** `f1669520c0a24b5eaa8d5eac89c9d357`
* **Título no Stitch:** `BeautyPass - Início`
* **Elementos Chave:**
  - Saudação personalizada (*"Olá, Camila ✨"*), localização dinâmica (*Jardins, SP*) e sino com notificação em aqua.
  - **Medidor Circular de Créditos**: Gráfico circular em anel gradiente `#5ce2e7` exibindo **85 / 120 créditos**, contador de renovação (*14 dias*) e botão pílula *+ Recarregar*.
  - **Banner SOS Beleza**: Acesso de 1 toque ao agendamento emergencial com botão pulsante.
  - **Carrossel de Categorias Rápidas**: Chips visuais circulares com fotografia (Cabelo & Escova, Nails & Spa, Facial & Pele, Massagem, Sobrancelhas).
  - **Cards Editoriais de Salões Próximos**: Fotografia imersiva, notas (★ 4.9), distâncias e preço em créditos.
  - Barra de navegação inferior flutuante com botão central SOS elevado.

---

### 🗺️ Tela 2: Explorar & Mapa de Salões Próximos
* **Screen ID:** `59b6cb9c717649179cdd8b1df12e79aa`
* **Título no Stitch:** `BeautyPass - Explorar & Mapa`
* **Elementos Chave:**
  - Barra de pesquisa flutuante com filtros rápidos em pílulas (*Próximos < 2km*, *Aberto Agora*, *Manicure*, *Escova*, etc.).
  - **Mapa Vetorial Interativo**: Posição do usuário com anel de pulso aqua e pins interativos em `#5ce2e7` com os valores em créditos dos estabelecimentos.
  - **Bottom Sheet de Estabelecimentos**: Lista deslizante com fotos em alta resolução, distância em metros, notas e procedimentos com seus custos em créditos (*L'Atelier Jardins*, *Maison Glow*, *Zenith Spa*).

---

### ⭐ Tela 3: Perfil do Salão — Avaliações & Procedimentos
* **Screen ID:** `f9cf2b6fd5e84bba976c233d855a82a1`
* **Título no Stitch:** `Perfil do Salão — Avaliações & Procedimentos` (L'Atelier Jardins)
* **Elementos Chave:**
  - Galeria hero em carrossel com fotos do interior e estações de atendimento.
  - Informações de endereço, selo de parceiro verificado e status de atendimento.
  - **Abas Segmentadas**: Alternância entre *Procedimentos* e a aba ativa de *Avaliações & Fotos*.
  - **Central de Avaliações Comunitárias**: Nota `4.9` (128 avaliações), pílulas de diferenciais (*Ambiente Impecável*, *Pontualidade 100%*, *Massagem no Lavatório*).
  - **Galeria de Fotos da Comunidade & Depoimentos**: Fotos de clientes reais (cabelos finalizados, nail art) com reviews detalhadas de assinantes verificados com créditos debitados.
  - Barra inferior fixa exibindo o saldo do usuário (85 créditos) e botão de agendamento em `#5ce2e7`.

---

### ⚡ Tela 4: SOS Beleza — Broadcast Express (Agendamento Urgente)
* **Screen ID:** `3def71dfc7c74d0495b6ba84f33a69cd`
* **Título no Stitch:** `BeautyPass - SOS Beleza Broadcast Express`
* **Elementos Chave:**
  - Card de resumo do procedimento urgente selecionado (*Lavagem & Escova Modeladora*, 25 créditos, raio 3km).
  - **Radar Concêntrico Animado**: Animação de pulso aqua com ondas circulares irradiando a partir da localização do usuário, transmitindo a solicitação para salões em um raio de 3km.
  - Cronômetro regressivo de resposta rápida (02:30).
  - **Feed de Propostas em Tempo Real**: Cards de encaixes imediatos que aceitaram a chamada (*L'Atelier Jardins com Stylist Clara pronta em 15 min*, *Studio Bella Donna pronto em 25 min*) com botão de confirmação instantânea.
