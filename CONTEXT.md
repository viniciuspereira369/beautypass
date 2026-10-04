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
