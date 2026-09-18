# ✨ BeautyPass — Assinatura & Créditos de Beleza e Bem-Estar

[![Status](https://img.shields.io/badge/status-active-emerald.svg)]()
[![Platform](https://img.shields.io/badge/platform-web%20%7C%20mobile--first-blue.svg)]()
[![Tech Stack](https://img.shields.io/badge/tech-HTML5%20%7C%20CSS3%20%7C%20Vanilla%20JS-pink.svg)]()
[![License](https://img.shields.io/badge/license-MIT-purple.svg)]()

> **BeautyPass** é um ecossistema digital e clube de assinaturas focado no bem-estar, autocuidado e estética. Os assinantes recebem mensalmente créditos flexíveis para agendar procedimentos como lavagem e escova, manicure, pedicure, sobrancelhas, massagens relaxantes e cuidados faciais em salões e clínicas parceiras de alto padrão.

---

## 📱 Visão Geral do Aplicativo

O aplicativo centraliza a jornada completa do cliente:
- **Gestão de Créditos:** Saldo transparente com gráfico de progresso radial, histórico detalhado de extrato e recarga instantânea de pacotes avulsos com bônus VIP.
- **Carrossel de Categorias:** Navegação rápida entre Cabelo & Escova, Nails & Spa, Facial & Pele, Massagem Relax e Sobrancelhas.
- **Geolocalização & Salões Próximos:** Radar com distâncias a pé (ex: Jardins, SP), status de abertura e selos de excelência (*Parceiro Prime*).
- **Agendamento com Encaixe Imediato:** Funcionalidade exclusiva para encontrar horários e vagas de última hora para procedimentos urgentes com desconto em créditos.
- **Comunidade & Avaliações Reais:** Galeria de fotos reais enviadas por clientes, notas verificadas (4.9★), depoimentos e métricas de satisfação (pontualidade, higienização, ambiente).
- **Interface Sofisticada & Touch-First:** Paleta premium em tons de verde menta / Tiffany suave, tipografia moderna (Plus Jakarta Sans) e navegação inferior ergonômica.

---

## 📂 Estrutura do Repositório

```text
├── beautypass_app.html      # Aplicativo Web Completo Interativo (Single Page App)
├── index.html               # Landing page institucional do ecossistema
├── styles.css               # Folha de estilos e design tokens
├── app.js                   # Scripts de interatividade e lógica da landing page
├── DESIGN.md                # Diretrizes de design system, paleta de cores e tipografia
├── pitch_deck_beautypass.html # Apresentação executiva / Pitch Deck de validação
├── treatment_*.jpg          # Acervo de imagens em alta definição dos procedimentos
├── Logo BeautyPass.jfif     # Logotipo oficial BeautyPass
└── assets/                  # Ativos estáticos e mídias visuais
```

---

## 🚀 Demonstração Online & Como Executar

### 🌐 Acessar Diretamente pelo Navegador (GitHub Pages)
O aplicativo já está publicado e pode ser acessado de qualquer celular, tablet ou computador através do GitHub Pages:
- 📱 **[Abrir Aplicativo BeautyPass (App Mobile Interativo)](https://viniciuspereira369.github.io/beautypass/beautypass_app.html)**
- 🏢 **[Abrir Portal Institucional / Landing Page](https://viniciuspereira369.github.io/beautypass/)**
- 📊 **[Abrir Pitch Deck & Proposta de Valor](https://viniciuspereira369.github.io/beautypass/pitch_deck_beautypass.html)**

---

### 💻 Execução Local

Como o projeto é construído em tecnologia web pura (HTML5, CSS3 moderno e Vanilla JavaScript), **não é necessário instalar dependências ou configuradores complexos**.

#### Opção 1: Abrir diretamente no navegador
1. Clone este repositório:
   ```bash
   git clone https://github.com/viniciuspereira369/beautypass.git
   cd beautypass
   ```
2. Abra o arquivo `beautypass_app.html` no seu navegador favorito (Google Chrome, Edge, Safari ou Firefox):
   - No Windows: Dê um duplo clique em `beautypass_app.html` ou execute no terminal:
     ```powershell
     start beautypass_app.html
     ```

### Opção 2: Servidor local (Live Server / Python)
Para uma experiência idêntica à de produção:
```bash
# Usando Python 3
python -m http.server 8000
```
Em seguida, acesse no navegador: `http://localhost:8000/beautypass_app.html`.

---

## 💡 Principais Funcionalidades do Aplicativo

1. **Carteira Digital de Créditos**:
   - Saldo dinâmico (ex: 85 de 120 créditos ativos).
   - Modal de recarga com 3 pacotes (Boost 25 cr, Glow 60 cr, Supreme 120 cr com bônus).
   - Abatimento em tempo real e feedback visual.

2. **Filtro Rápido de Categorias**:
   - Filtre salões e procedimentos por especialidade com um clique.

3. **Perfil Completo do Salão**:
   - Card interativo com galeria de fotos, horários e endereço.
   - Catálogo de procedimentos com custo em créditos e tempo estimado.
   - Aba dedicada a reviews da comunidade com fotos reais de clientes.

4. **Botão de Ação Rápida — Encaixe Imediato**:
   - Destaque central na navegação para procedimentos de última hora para eventos, reuniões ou emergências estéticas.

---

## 📄 Documentação & Pitch Deck

- **Design System**: Consulte [DESIGN.md](DESIGN.md) para detalhes sobre tokens de cor, sombras, espaçamentos e componentes.
- **Pitch Deck**: Abra [pitch_deck_beautypass.html](pitch_deck_beautypass.html) para visualizar a apresentação de negócios e proposta de valor para investidores e parceiros.

---

## 👤 Autor
Desenvolvido por **Vinicius Pereira** ([@viniciuspereira369](https://github.com/viniciuspereira369)).
