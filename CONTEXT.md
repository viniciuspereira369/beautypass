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

---

## 3. Arquitetura de Arquivos do Projeto

```text
Projeto BeautyPass/
│
├── beautypass_app.html      # APLICAÇÃO PRINCIPAL: Single Page App completa
│                            # (carteira, salões, modal de recarga, reviews e fotos)
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

1. **PWA (Progressive Web App):** Adicionar `manifest.json` e service worker para possibilitar a instalação do app na tela inicial do smartphone como aplicativo nativo.
2. **Backend & Persistência:** Implementar camada de banco de dados (ex: Supabase ou Firebase) para autenticação de usuários, persistência do saldo de créditos e agendamentos reais em tempo real.
3. **Gateway de Pagamento:** Integrar checkout (PIX / Cartão de Crédito via Stripe ou Mercado Pago) no modal de recarga de créditos.
4. **Painel do Salão (B2B):** Desenvolver tela para os estabelecimentos parceiros gerenciarem horários ociosos para o encaixe imediato e validarem vouchers de créditos.
