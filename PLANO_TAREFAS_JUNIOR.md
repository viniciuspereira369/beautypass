# 📘 Plano de Engenharia e Tarefas Técnicas — BeautyPass
## Guia do Engenheiro Sênior para o Desenvolvedor Júnior

> **Status do Projeto:** Protótipo de Validação Pré-Testes  
> **Objetivo da Sprint:** Preparar o protótipo para execução impecável dos testes com 8 a 12 participantes reais (Personas P1, P2 e P3), eliminando vieses de dados e garantindo integridade das métricas H1, H2, H4 e SUS.  
> **Documento Base de Verdade:** `documenta_o_t_cnica_prot_tipo_de_valida_o.md` (Spec de Validação)  
> **Stack:** HTML5, CSS3 Moderno, JavaScript Vanilla (ES6+), Leaflet/OSM, Google Apps Script + Google Sheets.  
> **Regra de Ouro:** Não implementar código de produção antecipado (sem microsserviços, sem NestJS/Postgres nesta fase). Aderência estrita à especificação de teste.

---

## 🧭 Mensagem do Engenheiro Sênior

> *"Olá! Seja muito bem-vindo a este ciclo de trabalho no BeautyPass.
> 
> Antes de você abrir o editor, preciso que você entenda algo crucial: **nós não estamos construindo um software final de prateleira, estamos construindo um instrumento de medição científica**. 
> 
> O objetivo deste aplicativo é responder se o modelo de negócios é viável antes que a empresa gaste centenas de milhares de reais desenvolvendo backend complexo e contratando gateways. Se os dados colhidos durante os testes forem enviesados — por exemplo, se um formulário vier pré-marcado ou um evento registrar propriedades erradas —, a liderança tomará decisões estratégicas baseadas em ilusões.
> 
> Eu fiz uma auditoria completa no código atual (`app.js`, `beautypass_app.html`, `styles.css`) e estruturei este plano em **14 tickets ordenados por dependência lógica**. 
> Cada ticket contém:
> 1. O **Problema e o Impacto no Negócio** (o porquê).
> 2. A **Localização Exata no Código** (arquivo e linhas).
> 3. O código **Antes** e o código **Depois**.
> 4. As **Armadilhas Comuns** onde desenvolvedores costumam errar.
> 5. O **Roteiro de Teste** com Critério de Pronto (*Definition of Done*).
> 
> Trabalhe um ticket por vez, valide localmente e não hesite em consultar a especificação. Bom trabalho!"*

---

## 🗺️ Mapa de Dependência dos Tickets

```
┌────────────────────────────────────────────────────────────────────────┐
│ FASE 1: Correção Crítica dos Vieses de Dados (H1, H2, SUS e Datas)     │
│ [TK-01: SUS & Tarefas] ──► [TK-02: Debounce H1] ──► [TK-03: Funil H2] │
│                                   │                                    │
│ [TK-04: Datas Dinâmicas] ◄────────┴────────► [TK-05: Persistência Appt]│
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│ FASE 2: Backend Leve (Google Sheets + Apps Script) & Conflito 409      │
│ [TK-06: Script Apps Script] ──► [TK-07: Dispatcher Eventos Front]      │
│                                           │                            │
│                                 [TK-08: Conflito 409]                  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│ FASE 3: Realismo de Catálogo e Disponibilidade Determinística          │
│ [TK-09: Motor Promo/Ocupação] ──► [TK-10: Seed 3-6 Staff + Reviews]   │
│                                           │                            │
│                                 [TK-11: Tag on_demand]                 │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│ FASE 4: Conformidade Legal, Parâmetros e Higiene                       │
│ [TK-12: Copy Seção 6.2] ──► [TK-13: Query Param ?p=P01] ──► [TK-14]   │
└────────────────────────────────────────────────────────────────────────┘
```

---

# FASE 1: Correção Crítica dos Vieses de Dados

---

## 🎫 TICKET 01: Correção de Vício e Obrigatoriedade no Questionário SUS
- **Prioridade:** Blocker (Crítica)
- **Componente:** `app.js`, `beautypass_app.html`, `styles.css`
- **Linhas de Referência:** `app.js:5949–6052`, `beautypass_app.html:1210–1295`

### 1. Contexto e Motivação
Na auditoria, descobri que a variável `currentSUSAnswers` inicia com respostas pré-selecionadas (`[4, 2, 4, 1, 4, 2, 5, 1, 4, 2]`), o rádio *"Você usaria este app de novo?"* já vem marcado como `"Sim, com certeza"` e as tarefas T1–T4 vêm marcadas como cumpridas.
Se o participante ou o moderador apenas clicar em "Salvar", o sistema calcula um SUS artificial de ~85 pontos (Status GO fraudulento). 
Além disso, as tarefas T1–T4 são avaliações de **desempenho técnico que o moderador avalia externamente**, e não o usuário no seu próprio celular.

### 2. O que deve ser feito
1. O array `currentSUSAnswers` deve iniciar com 10 posições `null`.
2. Os botões de escala Likert (1 a 5) devem renderizar sem nenhuma opção ativa.
3. As 10 perguntas devem ser de resposta **obrigatória**. O botão de submissão deve ficar desabilitado (`disabled`) até que todas as 10 notas estejam preenchidas.
4. O rádio de retenção (*"Usaria de novo?"*) deve iniciar sem nenhuma seleção.
5. Os checkboxes de tarefas T1–T4 devem ser **removidos do modal do participante**, pois o moderador fará essa anotação na planilha de controle.

### 3. Implementação Detalhada

#### A) Alteração em `app.js` (Estado inicial e validação)
```javascript
// ANTES (app.js:5950):
let currentSUSAnswers = [4, 2, 4, 1, 4, 2, 5, 1, 4, 2];

// DEPOIS:
// Inicializa com 10 posições vazias (null). Nenhuma resposta pré-marcada.
let currentSUSAnswers = new Array(10).fill(null);
```

#### B) Atualização do validador do modal em `app.js`:
```javascript
// Substituir a função renderSUSQuestions e adicionar checagem de completude:
function renderSUSQuestions() {
  const container = document.getElementById('sus-questions-container');
  if (!container) return;

  container.innerHTML = SUS_QUESTIONS.map((qText, idx) => {
    const currentVal = currentSUSAnswers[idx]; // Pode ser null
    return `
      <div class="sus-question-item ${currentVal === null ? 'unanswered' : 'answered'}">
        <div class="sus-question-text">${qText}</div>
        <div class="sus-likert-scale">
          ${[1, 2, 3, 4, 5].map(num => `
            <button type="button" class="sus-likert-btn ${currentVal === num ? 'selected' : ''}" 
              onclick="selectSUSRating(${idx}, ${num})" 
              title="Nota ${num} para a pergunta ${idx + 1}">
              ${num}
            </button>
          `).join('')}
        </div>
        <div class="sus-extremes-labels">
          <span>Discordo Totalmente (1)</span>
          <span>Concordo Totalmente (5)</span>
        </div>
      </div>
    `;
  }).join('');

  validateSUSFormCompleteness();
}

function selectSUSRating(questionIdx, val) {
  currentSUSAnswers[questionIdx] = val;
  renderSUSQuestions();
}

function validateSUSFormCompleteness() {
  const allAnswered = currentSUSAnswers.every(ans => ans !== null && ans >= 1 && ans <= 5);
  const retentionSelected = document.querySelector('input[name="finish-retention"]:checked') !== null;
  const submitBtn = document.getElementById('btn-submit-sus-evaluation');
  
  if (submitBtn) {
    submitBtn.disabled = !(allAnswered && retentionSelected);
    submitBtn.style.opacity = (allAnswered && retentionSelected) ? '1' : '0.5';
    submitBtn.style.cursor = (allAnswered && retentionSelected) ? 'pointer' : 'not-allowed';
  }
}
```

#### C) Ajuste em `beautypass_app.html` (`#session-finish-modal-overlay`):
- Remover o bloco `<div class="finish-section-box">` referente às tarefas T1–T4.
- No rádio de retenção, remover o atributo `checked` do input `"yes"`.
- Adicionar `id="btn-submit-sus-evaluation"` no botão com `disabled="true"`.

### 4. Armadilhas Comuns (Gotchas)
- **Atenção:** A função de cálculo `calculateSUSScore(answers)` assume índices de 0 a 9. Se houver algum `null`, `(val - 1)` resultará em `-1` e gerará pontuação corrompida. A trava no botão impede esse envio.
- Lembre-se de resetar `currentSUSAnswers = new Array(10).fill(null)` quando o modal for aberto para um novo participante.

### 5. Como Testar & Critério de Aceite
1. Abra o modal via Perfil > "Nova Avaliação".
2. Verifique se **nenhum** botão de 1 a 5 está selecionado.
3. Verifique se o botão "Salvar Avaliação" está desabilitado e cinza.
4. Preencha apenas 9 das 10 perguntas; o botão deve continuar travado.
5. Preencha a 10ª pergunta e selecione "Sim" no rádio; o botão deve ficar ativo e clicável.

---

## 🎫 TICKET 02: Correção Metodológica de H1 (`slot_viewed` com Debounce e `slot_selected` Enriquecido)
- **Prioridade:** Blocker (Crítica)
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:4813–4848`, `app.js:6145`

### 1. Contexto e Motivação
A Hipótese H1 avalia se o usuário, quando exposto a horários cheios e com desconto, escolhe o desconto.
Porém, no código atual:
1. O evento `slot_viewed` dispara no evento `input` a cada milissegundo de arraste do slider. Arrastar de 09:00 a 19:00 cospe mais de 40 eventos no log e faz com que quase todas as sessões sejam marcadas artificialmente como "elegíveis".
2. A função `proceedToCheckout()` dispara o evento `slot_selected`, mas **não envia a propriedade `has_discount`**, nem o `discount_pct`, nem o `price_base`. Quando a query de H1 tenta calcular conversão de slots com desconto, ela não encontra a flag e calcula 0%!

### 2. O que deve ser feito
1. Implementar um **Debounce de 800ms** no slider do relógio radial: a hora só é considerada "visualizada" se o participante parar o ponteiro nela por pelo menos 800ms, ou se clicar diretamente numa pílula da fita de horários rápidos.
2. Adicionar uma estrutura em `AppState.viewedSlotsInSession` do tipo `Set` para registrar `slot_viewed` **apenas uma vez** por combinação `session_id + salon_id + date + time`.
3. Atualizar `proceedToCheckout()` para enviar as propriedades completas no evento `slot_selected`.

### 3. Implementação Detalhada

#### A) Em `app.js` (Adicionar controle no estado global):
```javascript
// No objeto AppState (após a linha 2904):
viewedSlotsHistory: new Set(), // Chave: `${salonId}_${date}_${time}`
sliderDebounceTimer: null,
```

#### B) Refatorar o disparo de `slot_viewed` em `app.js`:
```javascript
// Substituir linhas 4813–4832 em app.js por:
function triggerSlotViewedEvent(salon, service, time, dateStr, pricing) {
  const slotKey = `${salon.id}_${dateStr}_${time}`;
  
  // Deduplicação: só dispara se ainda não foi visualizado nesta sessão
  if (AppState.viewedSlotsHistory.has(slotKey)) {
    return;
  }
  AppState.viewedSlotsHistory.add(slotKey);

  const [slotH, slotM] = time.split(':').map(Number);
  const now = new Date();
  const slotDate = new Date();
  slotDate.setHours(slotH, slotM, 0, 0);
  let hoursUntil = (slotDate - now) / 3600000;
  if (hoursUntil < 0) hoursUntil += 24;
  hoursUntil = Math.round(hoursUntil * 10) / 10;

  trackEvent('slot_viewed', {
    merchant_id: salon.id,
    service_id: service.id,
    start_time: time,
    date: dateStr,
    has_discount: Boolean(pricing.hasDiscount),
    discount_pct: pricing.hasDiscount ? pricing.discountPct : 0,
    hours_until: hoursUntil
  });
}
```

#### C) Implementar Debounce em `onSliderTimeChange`:
```javascript
// Substituir linhas 4834–4837 em app.js por:
function onSliderTimeChange(val) {
  AppState.selectedTimeSlotIndex = parseInt(val, 10);
  // Atualiza visualmente o mostrador imediatamente (zero lag na UI)
  updateRadialClock(false); 

  // Debounce de 800ms para disparar a telemetria analítica
  clearTimeout(AppState.sliderDebounceTimer);
  AppState.sliderDebounceTimer = setTimeout(() => {
    const salon = AppState.selectedSalon;
    const time = TIME_SLOTS[AppState.selectedTimeSlotIndex];
    const service = AppState.selectedService || salon.services[0];
    const pricing = calculateSlotPrice(salon, time, service, AppState.selectedDate);
    triggerSlotViewedEvent(salon, service, time, AppState.selectedDate, pricing);
  }, 800);
}
```

#### D) Enriquecer o `slot_selected` em `proceedToCheckout`:
```javascript
// Substituir linhas 4839–4848 em app.js por:
function proceedToCheckout() {
  const salon = AppState.selectedSalon;
  const currentService = AppState.selectedService || (salon.services && salon.services[0]) || salon.service;
  const pricing = AppState.currentPricing;

  trackEvent('slot_selected', {
    merchant_id: salon.id,
    service_id: currentService.id,
    start_time: AppState.selectedTime,
    date: AppState.selectedDate,
    price_base: pricing.basePrice,
    price_final: pricing.finalPrice,
    has_discount: Boolean(pricing.hasDiscount),
    discount_pct: pricing.hasDiscount ? pricing.discountPct : 0,
    promo_type: pricing.type // 'economy' | 'urgent' | 'normal'
  });
  
  navigateTo('checkout');
}
```

### 4. Armadilhas Comuns (Gotchas)
- Não coloque o debounce no desenho do ponteiro do SVG! O mostrador e o texto digital devem responder no milissegundo do toque do usuário. O debounce deve envolver **apenas** a chamada da telemetria `triggerSlotViewedEvent`.
- Na fita de slots rápidos (`renderHorizontalTimeStrip`), o clique do usuário é uma intenção explícita; chame `triggerSlotViewedEvent` diretamente sem esperar 800ms.

### 5. Como Testar & Critério de Aceite
1. Abra o console do navegador (`F12`).
2. Entre num salão e arraste o slider de ponta a ponta rapidamente.
3. Observe que **nenhum** evento `slot_viewed` deve ser logado durante o arraste rápido contínuo.
4. Pare o slider num horário (ex: 14:00) e aguarde 1 segundo. Exatamente **um** evento `slot_viewed` deve aparecer no console.
5. Clique em "Avançar para Pagamento" e confira se o evento `slot_selected` contém `has_discount: true/false` e `discount_pct`.

---

## 🎫 TICKET 03: Rastreabilidade e Coerência no Funil H2 (Unificação do `appointment_id`)
- **Prioridade:** Blocker (Crítica)
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:3320–3330`, `app.js:4997–5022`

### 1. Contexto e Motivação
A métrica H2 calcula a taxa de conclusão do checkout (`checkout_started` $\to$ `checkout_completed`).
Hoje o código faz o seguinte:
- Em `navigateTo('checkout')`, ele gera `tempAppointmentId = 'BP-' + Math.floor(...)` e emite `checkout_started`.
- No clique em confirmar pagamento em `confirmBooking()`, ele gera **um novo** `id: 'BP-' + Math.floor(...)` e emite `checkout_completed`.
Resultado: na análise de dados, nenhum agendamento concluído corresponde ao agendamento que foi iniciado! O funil fica completamente desconectado por ID de transação.

### 2. O que deve ser feito
1. Criar o `appointment_id` único no momento em que o checkout é montado e guardá-lo em `AppState._pendingCheckoutApptId`.
2. Usar **esse mesmo ID** em:
   - `checkout_started`
   - No cronômetro de expiração de 10 min
   - No cancelamento/abandono voluntário `checkout_abandoned`
   - Na confirmação final `confirmBooking()` e no voucher emitido.

### 3. Implementação Detalhada

#### A) Em `app.js` (Na entrada do checkout - linhas 3320–3330):
```javascript
// DEPOIS:
} else if (screenId === 'checkout') {
  renderCheckoutScreen();
  startReservationTimer();
  const finalPrice = AppState.currentPricing ? AppState.currentPricing.finalPrice : 84.00;
  
  // Cria o ID canônico único da tentativa de reserva
  const pendingId = 'BP-' + Math.floor(100000 + Math.random() * 900000);
  AppState._pendingCheckoutApptId = pendingId;

  // Transição formal para PENDING_PAYMENT (Seção 8 da spec)
  emitAppointmentStatusChanged(pendingId, 'NONE', 'PENDING_PAYMENT');

  trackEvent('checkout_started', { 
    appointment_id: pendingId,
    total: finalPrice,
    has_discount: Boolean(AppState.currentPricing?.hasDiscount),
    merchant_id: AppState.selectedSalon.id,
    service_id: (AppState.selectedService || AppState.selectedSalon.service).id
  });
}
```

#### B) Em `app.js` (Na finalização em `confirmBooking` - linha 4995):
```javascript
// Substituir linha 4997 por:
const canonicalAppointmentId = AppState._pendingCheckoutApptId || ('BP-' + Math.floor(100000 + Math.random() * 900000));

const newAppointment = {
  id: canonicalAppointmentId, // Mesmo ID gerado no checkout_started!
  salon: AppState.selectedSalon,
  service: currentService,
  staff: currentStaff,
  time: AppState.selectedTime,
  date: formatAppointmentDisplayDate(AppState.selectedDate),
  pricing: AppState.currentPricing,
  paymentMethod: AppState.selectedPaymentMethod,
  status: 'CONFIRMED',
  bookedAt: new Date().toISOString(),
  walkingTimeMin: AppState.lastWalkTimeMin || (AppState.selectedSalon.distanceKm ? Math.round(AppState.selectedSalon.distanceKm * 12) : null),
  walkingDistanceM: AppState.lastWalkDistanceM || (AppState.selectedSalon.distanceKm ? Math.round(AppState.selectedSalon.distanceKm * 1000) : null)
};
```

### 4. Como Testar & Critério de Aceite
1. Entre no checkout e observe no console o payload de `checkout_started`. Anote o `appointment_id` (ex: `BP-492104`).
2. Digite um cartão de teste (ex: `4111 1111 1111 1111`) e clique em confirmar.
3. Observe o payload de `checkout_completed`. O `appointment_id` deve ser **estritamente idêntico** (`BP-492104`).
4. O voucher emitido na tela final deve exibir exatamente esse mesmo código.

---

## 🎫 TICKET 04: Dinamismo Real de Datas e Fuso Horário de São Paulo
- **Prioridade:** Alta
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:2892`, `app.js:4610–4616`, `app.js:4859`, `app.js:5002`

### 1. Contexto e Motivação
A spec diz na Seção 4: *"Fuso horário (regra rígida): exibir sempre no timezone do salão (default America/Sao_Paulo). Erro de horário = falha crítica de aceite"*.
Hoje, se o usuário seleciona Terça-feira (30/09) na régua de datas, ao chegar no checkout e no voucher o texto exibe hardcoded: `"Hoje, 26 de Setembro às 14:00"`. Isso confunde participantes e causa desconfiança durante a sessão.

### 2. O que deve ser feito
1. Criar uma função utilitária `formatAppointmentDisplayDate(dateStr)` que use `Intl.DateTimeFormat` configurado explicitamente com `timeZone: 'America/Sao_Paulo'`.
2. Fazer com que a régua de datas inicialize com o dia atual do teste e os 4 dias seguintes, calculados dinamicamente via `new Date()`.
3. Substituir todo texto hardcoded `"Hoje, 26 de Setembro"` pelo valor dinâmico de `AppState.selectedDate`.

### 3. Implementação Detalhada

#### A) Adicionar helper de data em `app.js`:
```javascript
function formatAppointmentDisplayDate(dateStr) {
  if (!dateStr) return 'Data a confirmar';
  const parts = dateStr.split('-');
  if (parts.length !== 3) return dateStr;

  const d = new Date(Number(parts[0]), Number(parts[1]) - 1, Number(parts[2]), 12, 0, 0);
  
  const formatter = new Intl.DateTimeFormat('pt-BR', {
    timeZone: 'America/Sao_Paulo',
    weekday: 'short',
    day: '2-digit',
    month: 'short'
  });
  
  return formatter.format(d); // Ex.: "ter., 29 de set."
}
```

#### B) Atualizar `renderCheckoutScreen` e `renderConfirmScreen`:
```javascript
// Em renderCheckoutScreen (app.js:4859):
const formattedDate = formatAppointmentDisplayDate(AppState.selectedDate);
document.getElementById('checkout-datetime').textContent = `${formattedDate} às ${AppState.selectedTime}`;

// Em renderConfirmScreen (app.js:5049):
document.getElementById('confirm-time').textContent = `${formattedDate} às ${appt.time}`;
```

### 4. Como Testar & Critério de Aceite
1. Selecione um salão e mude a data na régua para "Seg 29" ou "Ter 30".
2. Escolha o horário das 11:30 e avance para o checkout.
3. Valide se o card de resumo do checkout exibe `"seg., 29 de set. às 11:30"` e **não** mais o texto estático "26 de Setembro".

---

## 🎫 TICKET 05: Persistência Local dos Agendamentos no `localStorage`
- **Prioridade:** Alta
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:2924–2960`, `app.js:5013`

### 1. Contexto e Motivação
Quando o participante conclui um agendamento na tarefa T1 ou T2 e por acaso recarrega a página no celular (ou o navegador fecha a aba para economizar memória), a lista `AppState.appointmentsList` volta para o array de seed original. Quando chega na tarefa T4 (*"Cancele o agendamento que você acabou de fazer"*), o agendamento sumiu!

### 2. O que deve ser feito
1. Salvar a lista de agendamentos em `localStorage.getItem('bp_user_appointments')` sempre que um agendamento for criado ou alterado de status.
2. Na inicialização do app (`initSessionState`), carregar os agendamentos persistidos, unindo-os aos seeds de demonstração.

### 3. Implementação Detalhada
```javascript
function persistAppointments() {
  try {
    localStorage.setItem('bp_user_appointments', JSON.stringify(AppState.appointmentsList));
  } catch (e) {
    console.warn('Erro ao salvar agendamentos:', e);
  }
}

function loadAppointments() {
  try {
    const saved = localStorage.getItem('bp_user_appointments');
    if (saved) {
      AppState.appointmentsList = JSON.parse(saved);
      return;
    }
  } catch (e) {
    console.warn('Erro ao restaurar agendamentos:', e);
  }
}
```
Chamar `persistAppointments()` em:
- `confirmBooking()` (após `unshift`)
- `executeCancellation()` (após mudar o status)
- `simulateMerchantCancellation()`

Chamar `loadAppointments()` dentro de `initSessionState()`.

---

# FASE 2: Backend Leve (Google Sheets) & Conflito Concorrente

---

## 🎫 TICKET 06: Criação do Google Apps Script para Coleta Remota e Controle de Conflitos
- **Prioridade:** Blocker (Arquitetura)
- **Componente:** Novo arquivo `google_apps_script.js` (código de referência para colar no editor do Apps Script)

### 1. Contexto e Motivação
Conforme alinhado, os participantes testarão o app nos seus **próprios celulares**. O `localStorage` de cada aparelho fica isolado.
Para centralizar os dados sem custos de servidor e sem violar a restrição de "não criar microsserviços", usaremos uma planilha do Google Sheets alimentada por uma Web App simples do Apps Script.

A planilha terá 5 abas:
1. `Eventos`: Log bruto de telemetria (`session_id`, `participant_code`, `event_name`, `client_ts`, `props_json`).
2. `Reservas`: Tabela de controle de slots ativos com expiração de 60 minutos para validação de concorrência real.
3. `SUS_Avaliacoes`: Questionário SUS, perguntas abertas e intenção de retenção.
4. `Moderador`: Roteiro de campo para o moderador anotar sucesso de T1–T4 e tempos em segundos (H4).
5. `Relatorio_Metricas`: Dashboard com fórmulas prontas calculando H1, H2, SUS global e taxa de retenção.

### 2. O que deve ser feito
Criar o arquivo `google_apps_script.js` com o código completo do Google Apps Script contendo `doPost(e)` e `doGet(e)`.

### 3. Código Completo de Referência (`google_apps_script.js`)

```javascript
/**
 * BEAUTYPASS — GOOGLE APPS SCRIPT BACKEND
 * Endpoint REST para coleta de eventos, reservas e questionários SUS.
 * 
 * Instruções de Implantação:
 * 1. Abra a planilha do Google Sheets criada para o teste.
 * 2. Acesse Extensões > Apps Script.
 * 3. Cole este código no arquivo Código.gs.
 * 4. Execute a função setupSpreadsheetSheets() uma vez para criar as abas e cabeçalhos.
 * 5. Clique em Implantar > Nova Implantação > Tipo: App da Web.
 * 6. Executar como: 'Eu' (sua conta) | Quem pode acessar: 'Qualquer pessoa'.
 * 7. Copie a URL gerada e configure em app.js (CONFIG.APPS_SCRIPT_URL).
 */

function setupSpreadsheetSheets() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  
  // Aba 1: Eventos
  let sheetEvt = ss.getSheetByName('Eventos') || ss.insertSheet('Eventos');
  if (sheetEvt.getLastRow() === 0) {
    sheetEvt.appendRow(['id', 'server_ts', 'client_ts', 'session_id', 'participant_code', 'event_name', 'props_json']);
    sheetEvt.getRange(1, 1, 1, 7).setFontWeight('bold').setBackground('#E6FFFA');
  }

  // Aba 2: Reservas (Slots Ativos)
  let sheetRes = ss.getSheetByName('Reservas') || ss.insertSheet('Reservas');
  if (sheetRes.getLastRow() === 0) {
    sheetRes.appendRow(['booking_id', 'created_at', 'expires_at', 'salon_id', 'date', 'time', 'staff_id', 'participant_code', 'status']);
    sheetRes.getRange(1, 1, 1, 9).setFontWeight('bold').setBackground('#FEF3C7');
  }

  // Aba 3: SUS_Avaliacoes
  let sheetSUS = ss.getSheetByName('SUS_Avaliacoes') || ss.insertSheet('SUS_Avaliacoes');
  if (sheetSUS.getLastRow() === 0) {
    sheetSUS.appendRow([
      'eval_id', 'timestamp', 'participant_code', 'session_id', 
      'q1', 'q2', 'q3', 'q4', 'q5', 'q6', 'q7', 'q8', 'q9', 'q10', 
      'sus_score', 'liked_feedback', 'disliked_feedback', 'would_use_again'
    ]);
    sheetSUS.getRange(1, 1, 1, 18).setFontWeight('bold').setBackground('#E0E7FF');
  }

  // Aba 4: Moderador (Controle de Tarefas e Tempos)
  let sheetMod = ss.getSheetByName('Moderador') || ss.insertSheet('Moderador');
  if (sheetMod.getLastRow() === 0) {
    sheetMod.appendRow([
      'participant_code', 'persona', 'date', 't1_success', 't1_time_sec', 
      't2_success', 't2_time_sec', 't3_success', 't3_time_sec', 
      't4_success', 't4_time_sec', 'instagram_time_sec', 'h4_faster', 'notes'
    ]);
    sheetMod.getRange(1, 1, 1, 14).setFontWeight('bold').setBackground('#FCE7F3');
  }

  // Aba 5: Relatorio_Metricas (Fórmulas Automáticas)
  let sheetRel = ss.getSheetByName('Relatorio_Metricas') || ss.insertSheet('Relatorio_Metricas');
  if (sheetRel.getLastRow() === 0) {
    sheetRel.appendRow(['Métrica de Validação', 'Meta GO', 'Fórmula / Valor Atual', 'Status']);
    sheetRel.appendRow(['H1: % Agendamentos com Desconto', '>= 35%', '=IFERROR(COUNTIFS(Eventos!F:F, "checkout_completed", Eventos!G:G, "*\"discount_applied\":[1-9]*") / COUNTIF(Eventos!F:F, "checkout_completed"), 0)', 'Verificar']);
    sheetRel.appendRow(['H2: Conclusão Checkout', '>= 50%', '=IFERROR(COUNTIF(Eventos!F:F, "checkout_completed") / COUNTIF(Eventos!F:F, "checkout_started"), 0)', 'Verificar']);
    sheetRel.appendRow(['SUS Global Médio', '>= 70 pts', '=IFERROR(AVERAGE(SUS_Avaliacoes!O:O), 0)', 'Verificar']);
    sheetRel.appendRow(['Intenção de Retenção', '>= 60%', '=IFERROR(COUNTIF(SUS_Avaliacoes!R:R, TRUE) / COUNT(SUS_Avaliacoes!O:O), 0)', 'Verificar']);
    sheetRel.getRange(1, 1, 1, 4).setFontWeight('bold').setBackground('#DCFCE7');
  }
}

function doPost(e) {
  try {
    const data = JSON.parse(e.postData.contents);
    const ss = SpreadsheetApp.getActiveSpreadsheet();

    // Rota 1: Ingestão de Lote de Eventos Analíticos
    if (data.action === 'log_events' && Array.isArray(data.events)) {
      const sheet = ss.getSheetByName('Eventos');
      const now = new Date().toISOString();
      const rows = data.events.map(ev => [
        ev.id || ('evt_' + Math.random().toString(36).substr(2, 7)),
        now,
        ev.clientTs || now,
        ev.sessionId || 'sess_unknown',
        ev.props?.participant_code || 'P00',
        ev.eventName,
        JSON.stringify(ev.props || {})
      ]);
      if (rows.length > 0) {
        sheet.getRange(sheet.getLastRow() + 1, 1, rows.length, rows[0].length).setValues(rows);
      }
      return ContentService.createTextOutput(JSON.stringify({ status: 'ok', count: rows.length }))
        .setMimeType(ContentService.MimeType.JSON);
    }

    // Rota 2: Tentativa de Reserva com Checagem de Conflito (60 min)
    if (data.action === 'attempt_booking') {
      const sheet = ss.getSheetByName('Reservas');
      const now = new Date();
      const salonId = String(data.salon_id);
      const dateStr = String(data.date);
      const timeStr = String(data.time);
      const staffId = String(data.staff_id || 'any');

      // Limpa ou ignora reservas com mais de 60 minutos
      const values = sheet.getDataRange().getValues();
      let hasConflict = false;

      for (let i = 1; i < values.length; i++) {
        const row = values[i];
        const rExpires = new Date(row[2]);
        const rSalon = String(row[3]);
        const rDate = String(row[4]);
        const rTime = String(row[5]);
        const rStaff = String(row[6]);
        const rStatus = String(row[8]);

        // Se ainda não expirou e é o mesmo salão, data e horário
        if (rExpires > now && rStatus === 'ACTIVE' && rSalon === salonId && rDate === dateStr && rTime === timeStr) {
          // Se for o mesmo profissional ou um dos dois for 'any'
          if (rStaff === staffId || staffId === 'any' || rStaff === 'any') {
            hasConflict = true;
            break;
          }
        }
      }

      if (hasConflict) {
        return ContentService.createTextOutput(JSON.stringify({ 
          status: 'CONFLICT', 
          message: 'Outro participante acabou de reservar este horário.' 
        })).setMimeType(ContentService.MimeType.JSON);
      }

      // Reserva liberada: registrar com validade de 60 minutos
      const expiresAt = new Date(now.getTime() + 60 * 60 * 1000).toISOString();
      sheet.appendRow([
        data.booking_id || ('BP-' + Math.floor(100000 + Math.random() * 900000)),
        now.toISOString(),
        expiresAt,
        salonId,
        dateStr,
        timeStr,
        staffId,
        data.participant_code || 'P00',
        'ACTIVE'
      ]);

      return ContentService.createTextOutput(JSON.stringify({ status: 'RESERVED', expiresAt }))
        .setMimeType(ContentService.MimeType.JSON);
    }

    // Rota 3: Envio de Questionário SUS
    if (data.action === 'submit_sus') {
      const sheet = ss.getSheetByName('SUS_Avaliacoes');
      const now = new Date().toISOString();
      const q = data.susAnswers || [];
      sheet.appendRow([
        data.id || ('eval_' + Math.random().toString(36).substr(2, 7)),
        now,
        data.participantCode || 'P00',
        data.sessionId || 'sess_unknown',
        q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7], q[8], q[9],
        data.susScore || 0,
        data.feedbackLiked || '',
        data.feedbackDisliked || '',
        Boolean(data.wouldUseAgain)
      ]);
      return ContentService.createTextOutput(JSON.stringify({ status: 'ok' }))
        .setMimeType(ContentService.MimeType.JSON);
    }

    return ContentService.createTextOutput(JSON.stringify({ status: 'unknown_action' }))
      .setMimeType(ContentService.MimeType.JSON);

  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({ status: 'error', error: error.toString() }))
      .setMimeType(ContentService.MimeType.JSON);
  }
}
```

---

## 🎫 TICKET 07: Dispatcher Assíncrono de Eventos e Reservas no Frontend (`app.js`)
- **Prioridade:** Blocker
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:2965–3006`

### 1. Contexto e Motivação
Com o endpoint do Apps Script implantado, o método `trackEvent` em `app.js` precisa enviar os dados para a planilha em background (fire-and-forget), sem nunca travar a navegação do usuário no celular se a internet oscilar.

### 2. O que deve ser feito
1. Declarar o objeto de configuração `CONFIG` no topo de `app.js` com a URL do Apps Script.
2. Criar um buffer de envio em lote (lotes de até 5 eventos ou a cada 4 segundos via `sendBeacon` ou `fetch(..., { keepalive: true })`).
3. Manter o espelho no `localStorage` como fallback de segurança offline.

### 3. Implementação Detalhada em `app.js`
```javascript
// No topo do app.js (após o comentário de abertura):
const CONFIG = {
  APPS_SCRIPT_URL: 'https://script.google.com/macros/s/SEU_SCRIPT_ID_AQUI/exec', // Configurar após deploy do Ticket 06
  ENABLE_REMOTE_SYNC: true
};

let eventQueue = [];
let syncTimer = null;

function flushEventsQueue() {
  if (eventQueue.length === 0 || !CONFIG.ENABLE_REMOTE_SYNC || !CONFIG.APPS_SCRIPT_URL.includes('/exec')) {
    return;
  }

  const batch = [...eventQueue];
  eventQueue = [];

  fetch(CONFIG.APPS_SCRIPT_URL, {
    method: 'POST',
    mode: 'no-cors', // Google Apps Script Web App exige no-cors para POST cross-origin simples
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      action: 'log_events',
      events: batch
    }),
    keepalive: true
  }).catch(err => {
    console.warn('Falha no envio remoto de eventos (retidos localmente):', err);
  });
}

function queueEventForSync(eventPayload) {
  eventQueue.push(eventPayload);
  clearTimeout(syncTimer);
  if (eventQueue.length >= 5) {
    flushEventsQueue();
  } else {
    syncTimer = setTimeout(flushEventsQueue, 4000);
  }
}
```
Invocar `queueEventForSync(eventPayload)` ao final de `trackEvent()`.

---

## 🎫 TICKET 08: Tratamento de Conflito Concorrente no Checkout (Critério de Aceite 4)
- **Prioridade:** Alta
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:4969–5028`

### 1. Contexto e Motivação
A Seção 14 da documentação técnica exige o seguinte critério de aceite: *"Duas reservas simultâneas no mesmo slot/profissional: uma falha e a grade atualiza"*.
Com a rota `attempt_booking` criada no Apps Script (Ticket 06), o front-end pode consultar a planilha no exato instante em que o usuário toca em "Confirmar Pagamento". Se outro participante reservou o mesmo horário nos últimos 60 min, o front deve barrar a transação com elegância.

### 2. O que deve ser feito
1. Antes de emitir o voucher e transicionar para a tela `confirm`, fazer uma chamada rápida para `attempt_booking`.
2. Se retornar `status: 'CONFLICT'`:
   - Parar o processamento.
   - Disparar `trackEvent('checkout_abandoned', { step: 'conflict', reason: 'slot_already_taken' })`.
   - Marcar o slot como ocupado localmente na memória.
   - Exibir alerta/modal explicativo: *"Este horário acabou de ser preenchido por outro cliente. Por favor, selecione outro horário."*
   - Redirecionar o usuário de volta para a tela de detalhes com a grade atualizada.

---

# FASE 3: Realismo de Catálogo e Disponibilidade Determinística

---

## 🎫 TICKET 09: Motor Determinístico de Promoção e Ocupação por Salão
- **Prioridade:** Média-Alta
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:3118–3177`

### 1. Contexto e Motivação
Na auditoria, notamos que a função `getSalonPromoRules` aplica **exatamente a mesma tabela de horários** para todos os 24 estabelecimentos de São Paulo. Isso reduz a credibilidade: se um participante navega entre o *Ateliê Belle Époque* e a *Barbearia Maestro*, ambos têm descontos exatamente nos mesmos minutos (10:30, 13:30, 14:00).
A solução aprovada no alinhamento é um **gerador determinístico**: o salão `s1` tem horários ociosos diferentes do salão `s2`, mas esses horários serão sempre consistentes entre diferentes celulares e dias da semana.

### 2. O que deve ser feito
Implementar uma função de hash determinística simples que combine o ID do salão (`salon.id`) e o dia da semana (`dow`) para selecionar uma das 4 matrizes de promoção realistas da Seção 12.7 da spec (Seg–Qua horários ociosos com 25% a 35% de desconto; Sábado sem descontos pela manhã).

### 3. Implementação Detalhada
```javascript
const PROMO_PRESETS = [
  [
    { time: '10:00', discountPct: 25, type: 'economy' },
    { time: '13:30', discountPct: 35, type: 'economy' },
    { time: '14:30', discountPct: 30, type: 'economy' },
    { time: '15:00', discountPct: 20, type: 'economy' }
  ],
  [
    { time: '10:30', discountPct: 20, type: 'economy' },
    { time: '11:00', discountPct: 25, type: 'economy' },
    { time: '14:00', discountPct: 30, type: 'economy' },
    { time: '15:30', discountPct: 25, type: 'economy' }
  ],
  [
    { time: '09:30', discountPct: 30, type: 'economy' },
    { time: '13:00', discountPct: 35, type: 'economy' },
    { time: '14:00', discountPct: 25, type: 'economy' },
    { time: '16:00', discountPct: 20, type: 'economy' }
  ]
];

function getSalonPromoRules(salon, dateStr) {
  const dow = getDayOfWeek(dateStr);
  if (dow === 0) return []; // Domingo fechado

  if (dow === 6) { // Sábado: sem descontos pela manhã; 1 slot no fim de tarde
    return [{ time: '17:00', discountPct: 20, type: 'urgent' }];
  }

  // Hash determinístico simples baseado no ID do salão
  const idNum = parseInt(salon.id.replace(/\D/g, ''), 10) || 1;
  const presetIndex = (idNum + dow) % PROMO_PRESETS.length;
  return PROMO_PRESETS[presetIndex];
}
```

---

## 🎫 TICKET 10: Enriquecimento do Seed: 3 a 6 Profissionais e 8 a 12 Avaliações por Salão
- **Prioridade:** Média
- **Componente:** `app.js` (Array `MOCK_SALONS`)
- **Linhas de Referência:** `app.js:11–2870`

### 1. Contexto e Motivação
A Seção 12.3 da spec exige:
- **3 a 6 profissionais por salão** com nomes brasileiros diversos, sendo **1 profissional em destaque com rating 0.3 acima da média da casa** (necessário para alimentar a tarefa T2: *"Agende com um profissional específico de sua escolha"*).
- **8 a 30 avaliações por salão** (atualmente há apenas 3 reviews por salão).

### 2. O que deve ser feito
1. Adicionar para os principais salões (especialmente os 8 salões principais dos bairros Pinheiros, Jardins e Itaim) um 3º e 4º profissional, garantindo que um deles tenha o selo e nota de destaque (ex: `rating: 4.98` enquanto a casa é `4.70`).
2. Complementar o array de `reviews` com textos críveis conforme exemplos da Seção 12.9 (*"Excelente, pontual e cuidadosa"*, *"Ambiente muito agradável"*, *"Atendimento impecável da Juliana"*).

---

## 🎫 TICKET 11: Rastreabilidade no Fluxo "Pedir Agora" (`source: 'on_demand'`)
- **Prioridade:** Média-Alta
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:4318–4327`, `app.js:5017`

### 1. Contexto e Motivação
O fluxo "Pedir Agora" (estilo Uber) calcula a tarifa justa baseada tanto na ociosidade quanto na distância. Como toda reserva feita por essa via gera desconto, precisamos identificar a origem da reserva para que o analista consiga separar na planilha:
- H1 via navegação de calendário tradicional.
- H1 via encaixe sob demanda ("Pedir Agora").

### 2. O que deve ser feito
Incluir a propriedade `booking_channel: 'on_demand'` (ou `'calendar'`) nos eventos `checkout_started`, `checkout_completed` e no payload do agendamento.

---

# FASE 4: Conformidade Legal, Parâmetros e Higiene

---

## 🎫 TICKET 12: Ajuste da Copy Rígida da Seção 6.2 (Subtexto e Regra das 3 Horas)
- **Prioridade:** Alta (Requisito da Spec)
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:3216–3218`

### 1. Contexto e Motivação
A Seção 6.2 da documentação técnica estabelece as seguintes cópias obrigatórias (proibido parafrasear):
- Badge **Horário Econômico** $\to$ Subtexto obrigatório: `"preço menor em horário de menor procura"`
- Badge **Última Hora** $\to$ Subtexto obrigatório: `"desconto para hoje"` (apenas se o slot começa nas próximas 3 horas).

No código atual, o subtexto do Horário Econômico está registrado como `"menor preço em horário ocioso"`.

### 2. O que deve ser feito
Substituir a string em `app.js` (linha 3217) para a copy exata da tabela da Seção 6.2:
```javascript
// ANTES (app.js:3217):
subtext: promoRule.type === 'urgent' ? 'desconto de última hora' : 'menor preço em horário ocioso'

// DEPOIS:
subtext: promoRule.type === 'urgent' ? 'desconto para hoje' : 'preço menor em horário de menor procura'
```

---

## 🎫 TICKET 13: Captura de Código de Participante via URL Query Param (`?p=P01`)
- **Prioridade:** Média
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:3011–3025`

### 1. Contexto e Motivação
Quando o moderador enviar o link do GitHub Pages para o participante no WhatsApp (ex: `https://viniciuspereira369.github.io/beautypass/beautypass_app.html?p=P03`), o aplicativo deve ler o parâmetro `p` da URL e preencher automaticamente o campo `#onboarding-participant`, economizando tempo e evitando erros de digitação do código da sessão.

### 2. O que deve ser feito
Em `initSessionState()`, adicionar:
```javascript
const urlParams = new URLSearchParams(window.location.search);
const participantParam = urlParams.get('p') || urlParams.get('participant');
if (participantParam) {
  const inputEl = document.getElementById('onboarding-participant');
  if (inputEl) {
    inputEl.value = participantParam.toUpperCase();
    inputEl.readOnly = true; // Trava para evitar alteração acidental
  }
}
```

---

## 🎫 TICKET 14: Expiração do Cronômetro com Estado `EXPIRED` e Purga Completa LGPD
- **Prioridade:** Média
- **Componente:** `app.js`
- **Linhas de Referência:** `app.js:4903–4928`, `app.js:5157–5166`

### 1. Contexto e Motivação
1. A máquina de estados da Seção 8 exige que, quando o timer de 10 min zerar no checkout, o sistema realize a transição de estado para `EXPIRED` com emissão do evento `appointment_status_changed`. Hoje o código apenas dá um `alert()` e chama `checkout_abandoned`.
2. O botão *"Apagar minha conta e dados"* no Perfil (Seção 13) apaga o `bp_user_session` e `bp_analytics_events`, mas esquece de remover `bp_sus_evaluations` e os agendamentos locais.

### 2. O que deve ser feito
1. Ao zerar o timer em `startReservationTimer()`, disparar:
   ```javascript
   emitAppointmentStatusChanged(AppState._pendingCheckoutApptId, 'PENDING_PAYMENT', 'EXPIRED');
   ```
2. Em `executeDeleteAccount()`, incluir:
   ```javascript
   localStorage.removeItem('bp_sus_evaluations');
   localStorage.removeItem('bp_user_appointments');
   AppState.appointmentsList = [];
   ```

---

## 📋 Checklist de Validação Final do Júnior (Definition of Done)

Antes de considerar a sprint concluída, o desenvolvedor deve verificar cada um dos seguintes itens:

- [ ] `node -c app.js` executa com **0 erros de sintaxe**.
- [ ] O questionário SUS abre com 10 perguntas **vazias** (sem botões ativos) e botão "Salvar" travado.
- [ ] Não há respostas pré-marcadas em nenhuma pergunta qualitativa ou de retenção.
- [ ] O evento `slot_viewed` só é disparado após 800ms de parada no slider e não se repete para o mesmo slot.
- [ ] O evento `slot_selected` envia `has_discount`, `discount_pct` e `price_base`.
- [ ] O ID do agendamento em `checkout_started` é idêntico ao de `checkout_completed`.
- [ ] O checkout e o voucher mostram a data selecionada dinamicamente, formatada em horário de São Paulo.
- [ ] O arquivo `google_apps_script.js` foi criado e testado com simulação de conflito concorrente.
- [ ] As cópias da Seção 6.2 estão com as palavras exatas da documentação técnica.
- [ ] O parâmetro `?p=P01` preenche e trava o código do participante no onboarding.
