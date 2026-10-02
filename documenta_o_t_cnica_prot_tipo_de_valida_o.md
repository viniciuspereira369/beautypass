# Documentação Técnica — Protótipo de Validação
## Marketplace de Serviços de Beleza (Descoberta + Agendamento + Preço Dinâmico)

| Campo | Valor |
|---|---|
| Versão | 1.0 |
| Tipo | Especificação de protótipo de validação (não-produção) |
| Objetivo | Construir app funcional para testar com usuários reais se "curtem" a proposta de valor |
| Público | IA/engenheiro responsável pela construção |
| Documento relacionado | `beauty_marketplace_planning_and_technical_specification.md` (spec de produção — NÃO usar nesta fase) |

**Como usar:** este é o documento único de verdade para o build. Ele substitui a spec de produção nesta fase. Toda decisão de escopo, copy e arquitetura deve derivar dele; em caso de conflito com qualquer outro material, prevalece este documento.

---

## Índice

1. [Objetivo e contexto](#1-objetivo-e-contexto)
2. [Hipóteses de validação e critérios de go/no-go](#2-hipóteses-de-validação-e-critérios-de-gono-go)
3. [Escopo: matriz real × mocado × não-construído](#3-escopo-matriz-real--mocado--não-construído)
4. [Stack e arquitetura](#4-stack-e-arquitetura)
5. [Personas e protocolo de teste](#5-personas-e-protocolo-de-teste)
6. [Especificação de UX](#6-especificação-de-ux)
7. [Regras de negócio do agendamento](#7-regras-de-negócio-do-agendamento)
8. [Máquina de estados do agendamento](#8-máquina-de-estados-do-agendamento)
9. [Motor de precificação](#9-motor-de-precificação-versão-protótipo)
10. [Modelo de dados](#10-modelo-de-dados-postgresql)
11. [Instrumentação de eventos](#11-instrumentação-de-eventos-catálogo-fechado)
12. [Plano de seed](#12-plano-de-seed-dados-de-demonstração)
13. [Privacidade mínima (LGPD para teste)](#13-privacidade-mínima-lgpd-para-teste)
14. [Critérios de aceite do build](#14-critérios-de-aceite-do-build-checklist-final)
15. [Roadmap pós-validação](#15-roadmap-pós-validação-não-construir-agora)

---

## 0. Instruções para a IA construtora (guardrails — leia primeiro)

1. **Isto é um protótipo de validação, não um produto.** O objetivo é gerar dados de go/no-go, não escalar.
2. **PROIBIDO adicionar** (over-engineering que invalida o cronograma): microserviços, Kafka/RabbitMQ, Redis/Redlock, PostGIS, PSP/gateway de pagamento real, push notifications, chat, portal B2B funcional, backoffice, KYC, split de pagamento, Kubernetes, CI/CD elaborado.
3. **OBRIGATÓRIO desde o primeiro build:** fuso horário correto na exibição de horários, copy exata da seção 6, instrumentação de eventos (seção 11), snapshot de preço no agendamento.
4. Diante de ambiguidade: **escolha a implementação mais simples** que satisfaça a seção correspondente. Não invente infraestrutura.
5. O banco é fonte de verdade. Tudo que o usuário vê de salões, horários e preços vem do seed (seção 12) — nada hardcoded em tela.

---

## 1. Objetivo e contexto

| Item | Definição |
|---|---|
| O que é | Protótipo funcional do app do consumidor, com oferta (salões) e preços dinâmicos reais, pagamentos simulados |
| O que não é | Sistema transacional real; não processa dinheiro; não tem parceiros reais onboardados |
| Objetivo | Testar com usuários reais **se "curtem"** a proposta central: descoberta + agendamento imediato + preço dinâmico em horários de ociosidade |
| Janela de teste | 8–12 participantes, sessões individuais de 45–60 min |
| Duração alvo do build | 3–4 semanas com 1 dev full-stack + 1 designer (ou IA equivalente) |

---

## 2. Hipóteses de validação e critérios de go/no-go

O protótipo existe para responder estas perguntas. Toda decisão de escopo deriva delas.

| ID | Hipótese | Métrica primária | Fonte de dados | Critério GO |
|---|---|---|---|---|
| **H1** | Usuários expostos a slots com desconto escolhem horários de ociosidade | % de agendamentos em slot descontado, entre usuários que visualizaram slots cheios E descontados | Eventos `slot_viewed` / `slot_selected` (flag `has_discount`) | ≥ 35% |
| **H2** | Usuários aceitam pagar antecipado (com pré-autorização) por serviço de beleza | % checkout iniciado → concluído; nº de cartões válidos inseridos; objeções em entrevista | Eventos de funil de checkout + notas de entrevista | ≥ 50% de conclusão; nenhuma objeção fatal recorrente |
| **H3** | Salões aceitam que a plataforma altere preços deles em horários de ociosidade | Adesão declarada após ver painel mock + explicação concierge | Sessões com 5 salões reais (fora do app) | ≥ 3/5 topam pilotar |
| **H4** | O fluxo é melhor que o status quo (agendar via Instagram/WhatsApp) | Tempo e passos para concluir a mesma tarefa vs. relato do fluxo atual; satisfação comparada | Cronometragem das sessões + entrevista | Protótipo mais rápido em ≥ 50% das tarefas comparáveis |

**Métricas globais:** SUS ≥ 70; task success rate ≥ 80%; pergunta final "você usaria este app de novo?" — ≥ 60% "sim".

> Nota: os limiares são arbitráveis pelo time; o importante é defini-los ANTES do teste.

---

## 3. Escopo: matriz real × mocado × não-construído

| Funcionalidade | Status | Detalhe |
|---|---|---|
| Login por telefone | **Real simplificado** | Qualquer número + código fixo `0000` |
| Geolocalização + distância | **Real** | GPS do device + haversine (sem PostGIS) |
| Lista/Busca/Filtros de salões | **Real** | Dados do seed |
| Perfil do salão + portfólio | **Real** | Fotos placeholder rotuladas (seção 12) |
| Grade de horários | **Real** | Calculada de `staff_schedules` − agendamentos existentes |
| Preço dinâmico | **Real** | Motor da seção 9, server-side |
| Agendamento (reserva, conflito) | **Real** | Constraint de exclusão no Postgres |
| Checkout + cartão | **MOCKADO** | Validação Luhn local, processamento simulado de 2s, **nenhum dado transmitido/armazenado** |
| Pré-autorização | **MOCKADA** | Texto exibido, cobrança simulada no cancelamento |
| Pix | **MOCKADO** | QR estático decorativo + opção "pagamento simulado" |
| Notificações push/WhatsApp | **NÃO construir** | Status visível dentro do app; testador acompanha a sessão ao vivo |
| Chat | **NÃO construir** | — |
| Cancelamento | **Real** | Com política simulada e captura de motivo |
| Avaliações (ler) | **Real (leitura)** | Dados do seed. **Escrever review: não construir** |
| Painel B2B | **MOCK ESTÁTICO** | 3 telas em Figma/imagens para H3 — fora do app |
| Backoffice | **NÃO construir** | Seed gerido via script no banco |

---

## 4. Stack e arquitetura

- **Backend:** monolito NestJS (Node.js + TypeScript) + **PostgreSQL 16** (sem extensões geoespaciais). Uma única API REST versionada em `/v1`.
- **Mobile:** Flutter 3, single codebase, Android (APK sideload para testes; iOS fora de escopo).
- **Distância:** cálculo haversine em SQL sobre colunas `lat`/`lng` numéricas. Raio fixo de 10 km + ordenação por distância.
- **Sem cache, sem filas:** jobs agendados (expiração de `PENDING_PAYMENT`) via `node-cron` de 1 min no próprio monolito.
- **Deploy:** 1 instância (VPS/container). Sem multi-AZ, sem observabilidade de produção. Log estruturado em stdout + tabela `analytics_events`.
- **Fuso horário (regra rígida):** persistir **sempre em UTC** (`TIMESTAMPTZ`); exibir sempre no timezone do salão (`merchants.timezone`, default `America/Sao_Paulo`); regras de promoção avaliadas no timezone do salão. *Erro de horário = falha crítica de aceite.*
- **Preços:** sempre computados no servidor; nunca derivados no cliente. O preço visto no slot deve ser idêntico ao do checkout.

---

## 5. Personas e protocolo de teste

### 5.1 Personas (recrutar 4–5 de cada)

| Persona | Perfil | O que observa no teste |
|---|---|---|
| **P1 — Econômica** | Mulher, 22–35, unhas/cabelo, agenda pelo Instagram, sensível a preço | Reação à tag de desconto (H1) |
| **P2 — Fiel ao profissional** | Mulher, 28–45, tem profissional de confiança, paga por confiabilidade | Seleção de profissional específico; reagiria a desconto que muda o profissional? |
| **P3 — Decisor rápido** | Homem, 25–40, corte/barba, quer horário agora | Fluxo de agendamento imediato (H4) |

### 5.2 Roteiro de sessão (moderador externo ao app)

1. Consentimento + coleta de dados demográficos (2 min)
2. **T1:** "Agende um serviço de unhas para esta semana, escolhendo a opção mais barata que encontrar." *(expõe H1)*
3. **T2:** "Agende com um profissional específico de sua escolha." *(fluxo padrão)*
4. **T3:** "Encontre um horário para amanhã o mais cedo possível e conclua." *(expõe descontos de última hora)*
5. **T4:** "Cancele o primeiro agendamento que você fez." *(política + atrito)*
6. Comparativo: "Conte como você faria isso hoje pelo Instagram" + cronometrar o relato (H4)
7. SUS (10 itens) + 3 perguntas abertas: o que mais gostou / o que incomodou / usaria de novo?
8. Encerramento: apagar dados do participante (seção 13)

---

## 6. Especificação de UX

### 6.1 Fluxo de navegação

```
Termo de teste → Login (telefone + código 0000)
  → Permissão de localização
  → HOME: lista de salões por distância [filtros]
      → PERFIL DO SALÃO: portfólio, serviços, profissionais
          → Seleção de serviço(s)
          → Profissional (ou "Qualquer profissional disponível")
          → CALENDÁRIO: grade de slots com tags de preço
              → CHECKOUT: resumo + pagamento simulado
                  → SUCESSO: detalhe do agendamento
  → MEUS AGENDAMENTOS: lista / cancelar
```

### 6.2 Copy exata (obrigatória, não parafrasear)

**Tags de slot:**

| Elemento | Texto | Uso |
|---|---|---|
| Badge | **Horário Econômico** | Slots com desconto programado (ociosidade) |
| Badge | **Última Hora** | Slots nas próximas 3h com desconto de urgência |
| Card do slot | Preço base riscado ~~R$ 120~~ → **R$ 84** + selo "−30%" | Sempre o par completo; nunca só o preço final |
| Subtexto | "preço menor em horário de menor procura" | Sob a badge Horário Econômico |
| Subtexto | "desconto para hoje" | Sob a badge Última Hora |

**Tooltip "Por que o preço varia?" (ícone ? ao lado da grade):**

> "Salões aplicam preços menores em horários de menor movimento para ocupar a agenda. O desconto é definido pelo salão e não muda após a confirmação do seu agendamento."

**Checkout:**

- Aviso fixo no rodapé: *"Pagamento simulado — nenhum valor será cobrado neste teste."*
- Texto de pré-autorização: *"Ao concluir, você autoriza o salão a reter até 30% do valor em caso de não comparecimento."*

**Cancelamento:**

- *"Cancele gratuitamente até 24h antes do horário. Após isso, será retida uma taxa de 30% do valor (simulada neste teste)."*
- Tela de motivo: lista (`Mudei de planos`, `Encontrei opção melhor`, `Emergência`, `Preço ficou alto demais`, `Outro` + campo livre).

### 6.3 Regras de apresentação de preço (testam percepção de discriminação)

1. Desconto é função **do horário**, nunca do usuário. Não existe personalização no protótipo.
2. Todo slot descontado mostra preço original + final + % — sem exceção.
3. O preço exibido no calendário **deve ser idêntico** ao do checkout (validar com teste automatizado).
4. Slots a preço cheio **nunca** exibem badge — a ausência de badge já comunica.

### 6.4 Estados obrigatórios

- Localização negada → fallback para centro de São Paulo com aviso: "mostrando salões em São Paulo (centro) — ative a localização para ver perto de você".
- Sem slots no dia → "Nenhum horário neste dia" + sugestão de dias vizinhos.
- Checkout "falhando" → não simular erros; o mock sempre aprova (o teste é de aceitação, não de resiliência).
- Acessibilidade mínima: contraste AA, alvos de toque ≥ 44px, suporte a fontes escaláveis.

---

## 7. Regras de negócio do agendamento

1. Grade de slots de **15 em 15 minutos**; um slot atende ao serviço quando cabe inteiro dentro do bloco do profissional (`início do slot` + `duração do serviço` + **buffer de 10 min de higienização** ≤ próximo compromisso).
2. Múltiplos serviços: sequência contígua na mesma visita; profissional pode alternar entre eles.
3. "Qualquer profissional disponível" = alocação automática do profissional elegível com menos ocupação no dia.
4. Reserva ao entrar no checkout: status `PENDING_PAYMENT` com expiração em **10 min** (job de 1 min libera o slot).
5. Cancelamento: >24h → gratuito; <24h → exibir política e confirmar (retenção simulada).
6. Conflito impossível por constraint de banco (seção 10) — sem locks de aplicação.

---

## 8. Máquina de estados do agendamento

| Estado | Ator | Transições permitidas | Efeitos |
|---|---|---|---|
| `PENDING_PAYMENT` | sistema/usuário | → `CONFIRMED`, → `EXPIRED` | Slot reservado; expira em 10 min |
| `EXPIRED` | sistema (cron) | terminal | Slot liberado; evento `checkout_abandoned(step=expiry)` |
| `CONFIRMED` | — | → `IN_PROGRESS`, → `CANCELLED_BY_USER`, → `CANCELLED_BY_MERCHANT`, → `NO_SHOW` | Snapshot de preço congelado |
| `IN_PROGRESS` | moderador (via script) | → `COMPLETED` | Usado para simular o dia do teste |
| `COMPLETED` | sistema | terminal | — |
| `CANCELLED_BY_USER` | usuário | terminal | registra `cancellation_reason` |
| `CANCELLED_BY_MERCHANT` | script seed | terminal | usado para simular força maior em 1–2 sessões |
| `NO_SHOW` | script | terminal | — |

Toda transição grava evento `appointment_status_changed` com `from`/`to`.

---

## 9. Motor de precificação (versão protótipo)

**Regra única e transparente** (a sigmoide da spec de produção é adiada para pós-validação — calibração é trabalho de produção):

```
preco_final = max(min_price, round(base_price × (1 − desconto), 2))
```

- **Fonte do desconto:** tabela `promo_rules` — uma regra por salão/serviço opcional/dia/horário, com `discount_pct` (≤ 40%) e `min_price`.
- **Última Hora:** se o slot começa nas próximas 3h E está sob regra ativa, o badge muda para "Última Hora" (mesmo cálculo, apresentação distinta — testa se urgência comunica melhor que economia).
- **Servidor computa** o preço a cada renderização da grade e **recomputa no checkout**; ao confirmar, grava snapshot (`price_base`, `price_final`, `discount_pct`, `promo_rule_id`, `price_explanation`).
- **Edição de regras:** via script de seed apenas. Nenhuma UI.

---

## 10. Modelo de dados (PostgreSQL)

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    locale VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
    accepted_test_terms_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE merchants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trade_name VARCHAR(150) NOT NULL,
    category VARCHAR(30) NOT NULL,  -- 'nails' | 'hair' | 'barber' | 'mixed'
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    address_json JSONB NOT NULL,
    timezone VARCHAR(40) NOT NULL DEFAULT 'America/Sao_Paulo',
    business_hours JSONB NOT NULL,  -- {"mon":["09:00","19:00"], ...}
    cancellation_policy_json JSONB,
    rating_avg NUMERIC(3,2) NOT NULL DEFAULT 0,
    rating_count INT NOT NULL DEFAULT 0,
    price_level SMALLINT CHECK (price_level BETWEEN 1 AND 4),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_merchants_latlng ON merchants (lat, lng);

CREATE TABLE staff_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    rating_avg NUMERIC(3,2) NOT NULL DEFAULT 4.50,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_staff_merchant ON staff_members (merchant_id);

-- Grade semanal de cada profissional (base da disponibilidade)
CREATE TABLE staff_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id UUID NOT NULL REFERENCES staff_members(id) ON DELETE CASCADE,
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CHECK (end_time > start_time)
);
CREATE INDEX idx_schedules_staff ON staff_schedules (staff_id, day_of_week);

CREATE TABLE services (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    base_price NUMERIC(10,2) NOT NULL CHECK (base_price > 0),
    duration_minutes SMALLINT NOT NULL CHECK (duration_minutes BETWEEN 15 AND 240),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_services_merchant ON services (merchant_id);

-- N:N: quais serviços cada profissional executa (com override opcional)
CREATE TABLE staff_services (
    staff_id UUID NOT NULL REFERENCES staff_members(id) ON DELETE CASCADE,
    service_id UUID NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    custom_duration_minutes SMALLINT,
    custom_price NUMERIC(10,2),
    PRIMARY KEY (staff_id, service_id)
);

CREATE TABLE promo_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    service_id UUID REFERENCES services(id) ON DELETE CASCADE, -- NULL = todos
    day_of_week SMALLINT CHECK (day_of_week BETWEEN 0 AND 6),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    discount_pct NUMERIC(5,2) NOT NULL CHECK (discount_pct BETWEEN 5 AND 40),
    min_price NUMERIC(10,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_promo_merchant ON promo_rules (merchant_id, day_of_week);

CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    merchant_id UUID NOT NULL REFERENCES merchants(id),
    staff_id UUID NOT NULL REFERENCES staff_members(id),
    service_id UUID NOT NULL REFERENCES services(id),
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    status VARCHAR(30) NOT NULL CHECK (status IN (
        'PENDING_PAYMENT','CONFIRMED','IN_PROGRESS','COMPLETED',
        'EXPIRED','CANCELLED_BY_USER','CANCELLED_BY_MERCHANT','NO_SHOW')),
    -- Snapshot de preço (auditabilidade: reconstituir o que o usuário viu)
    price_base NUMERIC(10,2) NOT NULL,
    price_final NUMERIC(10,2) NOT NULL,
    discount_pct NUMERIC(5,2) NOT NULL DEFAULT 0,
    promo_rule_id UUID REFERENCES promo_rules(id),
    price_explanation TEXT,
    cancellation_reason VARCHAR(80),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    cancelled_at TIMESTAMPTZ
);
CREATE INDEX idx_appt_user ON appointments (user_id);
CREATE INDEX idx_appt_merchant ON appointments (merchant_id, start_time);
CREATE INDEX idx_appt_status ON appointments (status) WHERE status = 'PENDING_PAYMENT';

-- Defesa de concorrência no banco (substitui Redlock no protótipo)
ALTER TABLE appointments ADD CONSTRAINT no_double_booking
EXCLUDE USING gist (
    staff_id WITH =,
    tstzrange(start_time, end_time) WITH &&
) WHERE (status IN ('PENDING_PAYMENT','CONFIRMED','IN_PROGRESS'));

CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    staff_id UUID REFERENCES staff_members(id),
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_reviews_merchant ON reviews (merchant_id);

CREATE TABLE merchant_photos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    staff_id UUID REFERENCES staff_members(id),
    service_tag VARCHAR(50),
    url TEXT NOT NULL,
    sort_order SMALLINT NOT NULL DEFAULT 0
);

-- Instrumentação: fonte única das métricas de validação
CREATE TABLE analytics_events (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    session_id UUID NOT NULL,
    event_name VARCHAR(60) NOT NULL,
    props JSONB NOT NULL DEFAULT '{}',
    client_ts TIMESTAMPTZ,
    server_ts TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_events_name_ts ON analytics_events (event_name, server_ts);
CREATE INDEX idx_events_user ON analytics_events (user_id);
```

---

## 11. Instrumentação de eventos (catálogo fechado)

Endpoint `POST /v1/events` (fire-and-forget do cliente, lote de até 10). Gravar exatamente estes nomes:

| Evento | Props obrigatórias | Serve a |
|---|---|---|
| `app_opened` | `session_id`, `participant_code` | base |
| `location_permission` | `granted: bool` | base |
| `search_performed` | `query`, `filters[]` | H4 |
| `merchant_viewed` | `merchant_id`, `distance_km`, `has_discount_today: bool` | H1 |
| `slot_viewed` | `merchant_id`, `service_id`, `start_time`, `has_discount`, `discount_pct`, `hours_until` | **H1 (núcleo)** |
| `slot_selected` | idem + `price_final` | **H1 (núcleo)** |
| `price_help_tooltip_opened` | `merchant_id` | percepção de discriminação |
| `checkout_started` | `appointment_id`, `total` | H2 |
| `payment_method_selected` | `method: card\|pix` | H2 |
| `card_validated` | `last4: string` (apenas 4 dígitos) | H2 |
| `checkout_completed` | `appointment_id`, `total`, `discount_applied` | H2 |
| `checkout_abandoned` | `step`, `has_discount` | H2 |
| `cancellation_completed` | `appointment_id`, `reason` | T4 |
| `appointment_status_changed` | `appointment_id`, `from`, `to` | integridade |

**Queries de relatório obrigatórias** (validar contra o seed antes do teste):

- *H1:* taxa de `slot_selected` com `has_discount=true` vs `false` entre sessões com ≥ 1 `slot_viewed` de cada tipo.
- *H2:* funil `checkout_started → checkout_completed` com abandono por step.

---

## 12. Plano de seed (dados de demonstração)

Script idempotente (`seed.ts`) com parâmetros:

1. **24 salões** fictícios em São Paulo (Pinheiros, Vila Madalena, Itaim, Perdizes, Consolação): 8 unhas, 8 cabelo, 4 barbearia, 4 mistos. Nomes inventados; `lat/lng` reais aproximados.
2. **Ratings realistas:** `rating_avg` entre 4.1–4.9 (nenhum 5.0 perfeito), `rating_count` 12–480 com distribuição assimétrica (poucos salões muito populares).
3. **3–6 profissionais por salão** com nomes brasileiros diversos; 1 deles com `rating_avg` destaque (0.3 acima da média da casa) para alimentar T2.
4. **4–10 serviços por salão** com preços realistas de SP (unha R$ 40–90; corte R$ 50–180; química R$ 150–600).
5. **Fotos:** `picsum.photos/seed/<id>` com selo "imagem ilustrativa". **PROIBIDO** baixar/usar fotos reais de salões ou redes sociais (risco legal).
6. **Schedules** com pausas de almoço e folgas semanais variadas.
7. **Promo rules:** descontos de 20–35% nas janelas de ociosidade (ter–qua 13h–17h, seg 9h–11h). **Sábado manhã e quinta noite SEM desconto** — pico cheio, para garantir que todo participante veja os dois mundos (condição do H1).
8. **Escassez artificial:** popular 60–80% dos slots de pico com agendamentos seed (`status='COMPLETED'`) para a grade renderizar ocupação realista; deixar ociosidade visível nos vales.
9. **Reviews:** 8–30 por salão, textos curtos e críveis ("Excelente, pontual e cuidadosa", "Ambiente agradável, mas demorou um pouco").
10. **2 agendamentos seed com `CANCELLED_BY_MERCHANT`** para uso em sessões de força maior.

---

## 13. Privacidade mínima (LGPD para teste)

- Coletar **apenas**: nome, telefone, eventos de uso.
- Tela de termo de teste antes do login, texto modelo: *"Este é um protótipo em teste. Coletamos seu nome, telefone e registros de uso das telas apenas para avaliar o aplicativo, sem qualquer finalidade comercial. Os dados serão apagados até 90 dias após o teste. Você pode solicitar a exclusão imediata a qualquer momento."* Aceitação gravada em `accepted_test_terms_at`.
- **Cartão: validar Luhn localmente, transmitir e armazenar NADA** — apenas evento `card_validated` com `last4`.
- Botão "Apagar minha conta e dados" em configurações (delete user + appointments + events em cascata).
- Ao final da campanha de testes: rodar purga completa.

---

## 14. Critérios de aceite do build (checklist final)

- [ ] Grade de horários renderiza no timezone do salão, correta ao minuto (testar com salão de timezone ≠ do device)
- [ ] Slot com desconto mostra par completo: ~~preço base~~ + final + badge + subtexto exato da seção 6.2
- [ ] Preço do calendário = preço do checkout (teste automatizado)
- [ ] Duas reservas simultâneas no mesmo slot/profissional: uma falha com 409 e a grade atualiza
- [ ] `PENDING_PAYMENT` expira em 10 min e libera o slot
- [ ] Eventos da seção 11 gravando com props corretas (validar as 2 queries de relatório)
- [ ] Termo de teste + apagar conta funcionando
- [ ] Seed re-executável do zero em < 2 min
- [ ] APK instala em device limpo; login com telefone + código `0000`
- [ ] Fallback de localização negada exibindo aviso correto

---

## 15. Roadmap pós-validação (NÃO construir agora — orienta decisões futuras)

**Se GO:** PSP real com split e antecipação (Lei do Salão-Parceiro); motor de yield com calibração dos parâmetros ($k$, $t_0$, $U_{\text{crit}}$) e cold start por heurística; Redis + expiração distribuída; painel B2B para H3 validado; LGPD completa; app B2B; push + WhatsApp Business; observabilidade; PostGIS quando houver escala multi-cidade.

**Se NO-GO parcial (ex.: H1 falha):** o protótipo permite isolar se o problema foi **apresentação** (tags/copy) ou **proposta** — re-testar variação de copy antes de descartar o modelo de negócio.

---

*Fim do documento.*