/**
 * BEAUTYPASS — INTERACTIVE PROTOTYPE ENGINE
 * Full SPA state management, dynamic catalogs, QR Code generator,
 * B2B DataViz Canvas charts, ROI Calculator & Reception POS Simulator.
 */

// ==========================================================================
// 1. DATA REPOSITORY (CLINICS & PROCEDURES)
// ==========================================================================
const CLINICS_DATA = [
  {
    id: "clinic-1",
    name: "Studio Aurora Hair & Visagismo",
    location: "Jardins, São Paulo • 1.2 km de você",
    category: "hair",
    emotionalTags: ["hair", "corporate"],
    plan: "premium",
    rating: 4.98,
    reviewsCount: 1640,
    procedure: "Corte Visagista, Tratamento Kérastase & Escova Modeladora",
    priceParticular: "R$ 380,00",
    image: "treatment_hair_salon.jpg",
    duration: "60 min",
    voucherCode: "BP-9042-LUX",
    savingsAmount: "R$ 380,00"
  },
  {
    id: "clinic-2",
    name: "Buddha Spa & Wellness Urbano",
    location: "Itaim Bibi, São Paulo • 2.5 km de você",
    category: "massage",
    emotionalTags: ["stress", "corporate"],
    plan: "premium",
    rating: 4.97,
    reviewsCount: 1420,
    procedure: "Massagem Relaxante com Aromaterapia & Pedras Quentes",
    priceParticular: "R$ 290,00",
    image: "treatment_massage_spa.jpg",
    duration: "60 min",
    voucherCode: "BP-8812-SKN",
    savingsAmount: "R$ 290,00"
  },
  {
    id: "clinic-3",
    name: "DermaPrime Medicina Estética & Laser",
    location: "Pinheiros, São Paulo • 0.8 km de você",
    category: "facial",
    emotionalTags: ["facial", "stress"],
    plan: "premium",
    rating: 4.95,
    reviewsCount: 980,
    procedure: "Hydrafacial Profundo + Fototerapia LED Rejuvenescedora",
    priceParticular: "R$ 420,00",
    image: "treatment_facial_spa.jpg",
    duration: "50 min",
    voucherCode: "BP-3341-GLW",
    savingsAmount: "R$ 420,00"
  },
  {
    id: "clinic-4",
    name: "Aura Boutique Dermocosméticos & Skincare",
    location: "Vila Olímpia, São Paulo • 1.9 km de você",
    category: "cosmetics",
    emotionalTags: ["cosmetics", "facial"],
    plan: "basico",
    rating: 4.92,
    reviewsCount: 890,
    procedure: "Voucher de R$ 150 em Produtos Cosméticos & Avaliação Facial",
    priceParticular: "R$ 180,00",
    image: "treatment_skincare_cosmetics.jpg",
    duration: "40 min",
    voucherCode: "BP-5529-DRN",
    savingsAmount: "R$ 180,00"
  },
  {
    id: "clinic-5",
    name: "The Gentleman Barber Lounge",
    location: "Bela Vista, São Paulo • 1.8 km de você",
    category: "barber",
    emotionalTags: ["barber", "express"],
    plan: "basico",
    rating: 4.94,
    reviewsCount: 640,
    procedure: "Barba Terapia com Toalha Quente & Corte Visagista",
    priceParticular: "R$ 160,00",
    image: "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=700&auto=format&fit=crop&q=80",
    duration: "40 min",
    voucherCode: "BP-7731-BAR",
    savingsAmount: "R$ 160,00"
  },
  {
    id: "clinic-6",
    name: "Clínica Revitalize Corporal & Spa",
    location: "Moema, São Paulo • 3.4 km de você",
    category: "massage",
    emotionalTags: ["stress", "corporate"],
    plan: "basico",
    rating: 4.91,
    reviewsCount: 1120,
    procedure: "Drenagem Linfática Corporal Desintoxicante (Método Renata França)",
    priceParticular: "R$ 220,00",
    image: "hero-visual.jpg",
    duration: "50 min",
    voucherCode: "BP-9901-LAV",
    savingsAmount: "R$ 220,00"
  }
];

// Active State Tracker
let currentContext = "b2c";
let currentCategory = "all";
let currentEmotionalFilter = "all";
let isAnnualBilling = true;
let voucherCountdownInterval = null;
let voucherSecondsLeft = 15 * 60; // 15:00 minutes

// ==========================================================================
// 2. INITIALIZATION
// ==========================================================================
document.addEventListener("DOMContentLoaded", () => {
  renderClinics();
  setupContextTabs();
  setupCategoryNav();
  setupEmotionalChips();
  setupBillingToggle();
  setupSearchAutocomplete();
  setupRoiCalculator();
  
  // Premium Animation System
  initHeaderScrollCompaction();
  initHeroMeshCanvas();
  initHeroRevealAnimations();
  initScrollReveals();
  initCounterAnimations();
});

// ==========================================================================
// 3. CONTEXT SWITCHER (PARA VOCÊ | RH | ESTABELECIMENTOS)
// ==========================================================================
function setupContextTabs() {
  const tabs = document.querySelectorAll(".context-tab");
  tabs.forEach(tab => {
    tab.addEventListener("click", () => {
      const targetCtx = tab.dataset.context;
      switchContext(targetCtx);
    });
  });
}

function switchContext(ctx) {
  if (ctx === currentContext) return;
  
  const prevView = document.getElementById(`view-${currentContext}`);
  currentContext = ctx;

  // Update tabs UI
  document.querySelectorAll(".context-tab").forEach(t => {
    const isTarget = t.dataset.context === ctx;
    t.classList.toggle("active", isTarget);
    t.setAttribute("aria-selected", isTarget ? "true" : "false");
  });

  const activeView = document.getElementById(`view-${ctx}`);
  const catNav = document.getElementById("categoryNav");
  const ctaBtnText = document.getElementById("btnCtaHeaderText");

  if (ctx === "b2c") {
    if (catNav) catNav.style.display = "flex";
    if (ctaBtnText) ctaBtnText.textContent = "Conheça os Planos";
  } else if (ctx === "b2b") {
    if (catNav) catNav.style.display = "none";
    if (ctaBtnText) ctaBtnText.textContent = "Solicitar Demonstração";
    setupRoiCalculator();
    setTimeout(initB2bCharts, 200);
  } else if (ctx === "partner") {
    if (catNav) catNav.style.display = "none";
    if (ctaBtnText) ctaBtnText.textContent = "Credenciar Estabelecimento";
  }

  // GSAP animated transition between contexts
  const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  
  if (typeof gsap !== 'undefined' && !prefersReducedMotion && prevView) {
    gsap.to(prevView, {
      opacity: 0,
      y: 12,
      duration: 0.25,
      ease: "power2.in",
      onComplete: () => {
        prevView.classList.remove("active");
        prevView.style.opacity = "";
        prevView.style.transform = "";
        
        if (activeView) {
          activeView.classList.add("active");
          gsap.fromTo(activeView, 
            { opacity: 0, y: 16 },
            { 
              opacity: 1, 
              y: 0, 
              duration: 0.4, 
              ease: "power2.out",
              onComplete: () => {
                initCounterAnimations();
              }
            }
          );
        }
        window.scrollTo({ top: 0, behavior: "smooth" });
      }
    });
  } else {
    // Fallback without GSAP
    document.querySelectorAll(".context-view").forEach(view => view.classList.remove("active"));
    if (activeView) activeView.classList.add("active");
    window.scrollTo({ top: 0, behavior: "smooth" });
    initCounterAnimations();
  }

  showToast(`Contexto alterado para: ${ctx === "b2c" ? "Para Você (B2C)" : ctx === "b2b" ? "Para Empresas (RH)" : "Para Estabelecimentos"}`);
}

function handleHeaderCta() {
  if (currentContext === "b2c") {
    const el = document.getElementById("planos");
    if (el) el.scrollIntoView({ behavior: "smooth" });
  } else if (currentContext === "b2b") {
    const el = document.getElementById("b2b-form");
    if (el) el.scrollIntoView({ behavior: "smooth" });
  } else {
    const el = document.getElementById("simulador-voucher");
    if (el) el.scrollIntoView({ behavior: "smooth" });
  }
}

// ==========================================================================
// 4. CATEGORY & EMOTIONAL FILTERS & SEARCH
// ==========================================================================
function setupCategoryNav() {
  const catLinks = document.querySelectorAll(".cat-link");
  catLinks.forEach(link => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      catLinks.forEach(l => l.classList.remove("active"));
      link.classList.add("active");
      currentCategory = link.dataset.cat;
      renderClinics();
      const el = document.getElementById("catalogo");
      if (el) el.scrollIntoView({ behavior: "smooth" });
    });
  });
}

function setupEmotionalChips() {
  const chips = document.querySelectorAll(".chip-btn");
  chips.forEach(chip => {
    chip.addEventListener("click", () => {
      chips.forEach(c => c.classList.remove("active"));
      chip.classList.add("active");
      currentEmotionalFilter = chip.dataset.filter;
      renderClinics();
    });
  });
}

function setupSearchAutocomplete() {
  const input = document.getElementById("inputProcedure");
  const dropdown = document.getElementById("autocompleteDropdown");
  const btnSubmit = document.getElementById("btnSearchSubmit");
  const geolocBtn = document.getElementById("btnGeoloc");

  if (!input || !dropdown) return;

  input.addEventListener("focus", () => dropdown.classList.add("active"));
  
  // Close on outside click
  document.addEventListener("click", (e) => {
    if (!e.target.closest(".search-procedure")) {
      dropdown.classList.remove("active");
    }
  });

  // Autocomplete items click
  dropdown.querySelectorAll(".ac-item").forEach(item => {
    item.addEventListener("click", () => {
      input.value = item.dataset.value;
      dropdown.classList.remove("active");
      filterClinicsBySearch();
    });
  });

  input.addEventListener("input", filterClinicsBySearch);
  if (btnSubmit) btnSubmit.addEventListener("click", filterClinicsBySearch);

  if (geolocBtn) {
    geolocBtn.addEventListener("click", () => {
      const locInput = document.getElementById("inputLocation");
      if (locInput) {
        locInput.value = "Pinheiros, São Paulo (Sua Localização)";
        showToast("Localização detectada com sucesso!");
        filterClinicsBySearch();
      }
    });
  }

  const selectPlan = document.getElementById("selectPlanFilter");
  if (selectPlan) {
    selectPlan.addEventListener("change", filterClinicsBySearch);
  }
}

function filterClinicsBySearch() {
  const query = (document.getElementById("inputProcedure")?.value || "").toLowerCase();
  const planFilter = document.getElementById("selectPlanFilter")?.value || "all";

  renderClinics(query, planFilter);
}

// ==========================================================================
// 5. RENDER CLINICS CATALOG
// ==========================================================================
function renderClinics(searchQuery = "", planFilter = "all") {
  const grid = document.getElementById("clinicsGrid");
  const counter = document.getElementById("catalogCounter");
  if (!grid) return;

  let filtered = CLINICS_DATA.filter(clinic => {
    // Category match
    if (currentCategory !== "all" && clinic.category !== currentCategory) return false;
    
    // Emotional filter match
    if (currentEmotionalFilter !== "all" && !clinic.emotionalTags.includes(currentEmotionalFilter)) return false;

    // Plan filter match
    if (planFilter !== "all" && clinic.plan !== planFilter) return false;

    // Search query match
    if (searchQuery.trim() !== "") {
      const term = searchQuery.toLowerCase();
      const matchName = clinic.name.toLowerCase().includes(term);
      const matchProc = clinic.procedure.toLowerCase().includes(term);
      const matchLoc = clinic.location.toLowerCase().includes(term);
      if (!matchName && !matchProc && !matchLoc) return false;
    }

    return true;
  });

  if (counter) {
    counter.innerHTML = `Exibindo <strong>${filtered.length}</strong> estabelecimentos auditados`;
  }

  if (filtered.length === 0) {
    grid.innerHTML = `
      <div style="grid-column: 1 / -1; text-align: center; padding: 3rem; background: #FFFFFF; border-radius: 16px; border: 1px dashed #CBD5E1;">
        <p style="font-size: 1.25rem; font-weight: 700; color: #0F172A; margin-bottom: 0.5rem;">Nenhum estabelecimento encontrado</p>
        <p style="color: #64748B; font-size: 0.875rem;">Tente ajustar os filtros de busca ou remover os termos pesquisados.</p>
        <button class="btn-primary" style="margin-top: 1rem;" onclick="resetCatalogFilters()">Limpar Filtros</button>
      </div>
    `;
    return;
  }

  grid.innerHTML = filtered.map(clinic => {
    const isPrem = clinic.plan === "premium";
    return `
      <article class="clinic-card" data-id="${clinic.id}">
        <div class="clinic-image-wrap">
          <img src="${clinic.image}" alt="${clinic.name}" class="clinic-img" loading="lazy">
          <div class="clinic-badges-overlay">
            <span class="audit-pill">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              Auditada & Aprovada
            </span>
            <span class="plan-tag-pill ${isPrem ? 'tag-premium' : 'tag-basic'}">
              <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
              ${isPrem ? 'Plano Premium' : 'Plano Básico'}
            </span>
          </div>
        </div>

        <div class="clinic-card-body">
          <div class="clinic-meta-row">
            <span class="rating-stars">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="#F59E0B" stroke="#F59E0B"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
              ${clinic.rating.toFixed(2)}
            </span>
            <span>(${clinic.reviewsCount} avaliações)</span>
            <span>•</span>
            <span class="meta-duration">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              ${clinic.duration}
            </span>
          </div>

          <h3 class="clinic-title">${clinic.name}</h3>
          <p class="clinic-location">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
            ${clinic.location}
          </p>

          <div class="clinic-procedure-box">
            <span class="proc-label">Procedimento Coberto:</span>
            <div class="proc-name">${clinic.procedure}</div>
          </div>

          <div class="savings-comparator">
            <div>
              <span style="font-size: 0.75rem; color: #94A3B8; display: block;">Valor Particular:</span>
              <span class="price-particular">${clinic.priceParticular}</span>
            </div>
            <div style="text-align: right;">
              <span style="font-size: 0.75rem; color: #0D9488; font-weight: 700; display: block;">No seu BeautyPass:</span>
              <span class="price-pass">Incluso 100%</span>
            </div>
          </div>

          <button class="btn-voucher-action" onclick="generateVoucher('${clinic.id}')">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
            <span>Gerar Voucher Imediato</span>
          </button>
        </div>
      </article>
    `;
  }).join("");
}

function resetCatalogFilters() {
  currentCategory = "all";
  currentEmotionalFilter = "all";
  const input = document.getElementById("inputProcedure");
  if (input) input.value = "";
  document.querySelectorAll(".cat-link").forEach(l => l.classList.toggle("active", l.dataset.cat === "all"));
  document.querySelectorAll(".chip-btn").forEach(c => c.classList.toggle("active", c.dataset.filter === "all"));
  renderClinics();
}

// ==========================================================================
// 6. BILLING TOGGLE (MENSAL VS ANUAL)
// ==========================================================================
function setupBillingToggle() {
  const toggle = document.getElementById("billingToggle");
  const labelM = document.getElementById("labelMonthly");
  const labelA = document.getElementById("labelAnnual");
  const priceB = document.getElementById("priceBasic");
  const priceP = document.getElementById("pricePremium");

  if (!toggle) return;

  toggle.addEventListener("click", () => {
    isAnnualBilling = !isAnnualBilling;
    toggle.classList.toggle("monthly", !isAnnualBilling);
    labelM.classList.toggle("active", !isAnnualBilling);
    labelA.classList.toggle("active", isAnnualBilling);

    if (priceB && priceP) {
      if (isAnnualBilling) {
        priceB.textContent = "89";
        priceP.textContent = "189";
      } else {
        priceB.textContent = "119";
        priceP.textContent = "239";
      }
    }
  });
}

function selectPlan(planName) {
  showToast(`Você selecionou o ${planName}! Redirecionando para ativação segura...`);
  setTimeout(() => {
    openWalletModal();
  }, 1000);
}

// ==========================================================================
// 7. DIGITAL VOUCHER MODAL & QR CODE GENERATOR
// ==========================================================================
function generateVoucher(clinicId) {
  const clinic = CLINICS_DATA.find(c => c.id === clinicId) || CLINICS_DATA[0];

  document.getElementById("vModalProcedure").textContent = clinic.procedure;
  document.getElementById("vModalClinic").textContent = `${clinic.name} (${clinic.location.split('•')[0].trim()})`;
  document.getElementById("vModalPlan").textContent = clinic.plan === "premium" ? "Plano Premium Black" : "Plano Essencial";
  document.getElementById("vModalSavings").textContent = clinic.priceParticular;
  document.getElementById("vModalCode").textContent = clinic.voucherCode;

  // Render Dynamic SVG QR Code
  renderSvgQrCode("vModalQrCanvas", clinic.voucherCode);

  // Start 15-minute countdown
  startVoucherTimer();

  // Open Modal
  const modal = document.getElementById("voucherModal");
  if (modal) modal.classList.add("active");

  showToast(`Voucher ${clinic.voucherCode} gerado com sucesso!`);
}

function renderSvgQrCode(elementId, text) {
  const target = document.getElementById(elementId);
  if (!target) return;

  // Generate crisp deterministic visual QR matrix
  const size = 120;
  const cells = 15;
  const cellSize = size / cells;

  let rects = "";
  for (let r = 0; r < cells; r++) {
    for (let c = 0; c < cells; c++) {
      // Center cutout for brand logo
      if (r >= 6 && r <= 8 && c >= 6 && c <= 8) continue;

      // Create positioning patterns (corners)
      const isCornerTL = r < 4 && c < 4;
      const isCornerTR = r < 4 && c >= cells - 4;
      const isCornerBL = r >= cells - 4 && c < 4;
      
      // Pseudorandom deterministic pattern from code
      const hash = (text.charCodeAt(0) * (r + 1) + text.charCodeAt(text.length - 1) * (c + 1) + r * c) % 3;
      
      if (isCornerTL || isCornerTR || isCornerBL || hash === 0 || (r === 7 && c % 2 === 0)) {
        rects += `<rect x="${c * cellSize}" y="${r * cellSize}" width="${cellSize - 0.6}" height="${cellSize - 0.6}" fill="#0F172A" rx="1.5"/>`;
      }
    }
  }

  // Center logo emblem
  const centerSize = cellSize * 3;
  const centerX = (size - centerSize) / 2;
  const centerY = (size - centerSize) / 2;

  target.innerHTML = `
    <svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}" style="border-radius: 8px; display: block;">
      <rect width="${size}" height="${size}" fill="#FFFFFF" rx="8"/>
      ${rects}
      <!-- Center Monogram -->
      <rect x="${centerX}" y="${centerY}" width="${centerSize}" height="${centerSize}" fill="#0D9488" rx="4"/>
      <path d="M${centerX + 6} ${centerY + 7} L${centerX + 12} ${centerY + 16} L${centerX + 18} ${centerY + 7}" stroke="#FFFFFF" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
    </svg>
  `;
}

function startVoucherTimer() {
  clearInterval(voucherCountdownInterval);
  voucherSecondsLeft = 15 * 60; // 15 minutes

  const timerEl = document.getElementById("vModalTimer");
  
  function update() {
    const mins = Math.floor(voucherSecondsLeft / 60);
    const secs = voucherSecondsLeft % 60;
    if (timerEl) {
      timerEl.textContent = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')} min`;
    }
    if (voucherSecondsLeft <= 0) {
      clearInterval(voucherCountdownInterval);
      if (timerEl) timerEl.textContent = "EXPIRADO";
    }
    voucherSecondsLeft--;
  }

  update();
  voucherCountdownInterval = setInterval(update, 1000);
}

function closeVoucherModal() {
  const modal = document.getElementById("voucherModal");
  if (modal) modal.classList.remove("active");
  clearInterval(voucherCountdownInterval);
}

function copyVoucherCode() {
  const code = document.getElementById("vModalCode")?.textContent || "BP-9042-LUX";
  navigator.clipboard?.writeText(code);
  showToast(`Código ${code} copiado para a área de transferência!`);
}

function saveVoucherOffline() {
  showToast("Voucher salvo na Carteira Digital offline!");
  closeVoucherModal();
}

function openActiveVoucherModal() {
  closeWalletModal();
  generateVoucher("clinic-1");
}

// ==========================================================================
// 8. USER DIGITAL WALLET DRAWER
// ==========================================================================
function openWalletModal() {
  const drawer = document.getElementById("walletDrawer");
  if (drawer) drawer.classList.add("active");
}

function closeWalletModal() {
  const drawer = document.getElementById("walletDrawer");
  if (drawer) drawer.classList.remove("active");
}

function closeWalletDrawer(e) {
  if (e.target.id === "walletDrawer") {
    closeWalletModal();
  }
}

// ==========================================================================
// 9. B2B / RH DATA VIZ CHARTS (CANVAS ENGINE)
// ==========================================================================
function initB2bCharts() {
  renderStressChart();
  renderAdhesionChart();
}

function renderStressChart() {
  const canvas = document.getElementById("chartStress");
  if (!canvas) return;

  const ctx = canvas.getContext("2d");
  const dpr = window.devicePixelRatio || 2;
  const rect = canvas.getBoundingClientRect();
  const w = rect.width || 450;
  const h = 240;

  canvas.width = w * dpr;
  canvas.height = h * dpr;
  ctx.scale(dpr, dpr);

  const paddingX = 40;
  const paddingTop = 25;
  const paddingBottom = 40;

  ctx.clearRect(0, 0, w, h);

  // Months & data
  const labels = ["Mês 1", "Mês 2", "Mês 3", "Mês 4", "Mês 5", "Mês 6"];
  const dataWithout = [88, 90, 86, 92, 94, 91]; // High stress
  const dataWith = [88, 64, 50, 42, 35, 29];    // Drastic stress reduction

  const plotW = w - paddingX * 2;
  const plotH = h - paddingTop - paddingBottom;
  const stepX = plotW / (labels.length - 1);

  // Draw Grid lines & Y values
  ctx.strokeStyle = "rgba(255, 255, 255, 0.05)";
  ctx.lineWidth = 1;
  for (let i = 0; i <= 3; i++) {
    const y = paddingTop + (i * plotH / 3);
    ctx.beginPath();
    ctx.moveTo(paddingX, y);
    ctx.lineTo(w - paddingX, y);
    ctx.stroke();
  }

  // Helper for coordinates
  const getX = (i) => paddingX + i * stepX;
  const getY = (val) => paddingTop + plotH - ((val / 100) * plotH);

  // Helper for drawing smooth spline
  function drawSpline(points) {
    ctx.beginPath();
    ctx.moveTo(points[0].x, points[0].y);
    for (let i = 0; i < points.length - 1; i++) {
      const p0 = points[i];
      const p1 = points[i + 1];
      const cpX = (p0.x + p1.x) / 2;
      ctx.bezierCurveTo(cpX, p0.y, cpX, p1.y, p1.x, p1.y);
    }
  }

  const ptsWithout = dataWithout.map((val, i) => ({ x: getX(i), y: getY(val) }));
  const ptsWith = dataWith.map((val, i) => ({ x: getX(i), y: getY(val) }));

  // Draw Line Without BeautyPass (Dashed Slate)
  ctx.save();
  ctx.strokeStyle = "#64748B";
  ctx.lineWidth = 2;
  ctx.setLineDash([4, 4]);
  drawSpline(ptsWithout);
  ctx.stroke();
  ctx.restore();

  // Draw Gradient Fill for With BeautyPass
  const gradient = ctx.createLinearGradient(0, paddingTop, 0, h - paddingBottom);
  gradient.addColorStop(0, "rgba(6, 182, 212, 0.35)");
  gradient.addColorStop(0.5, "rgba(13, 148, 136, 0.15)");
  gradient.addColorStop(1, "rgba(6, 182, 212, 0.0)");

  ctx.save();
  ctx.fillStyle = gradient;
  drawSpline(ptsWith);
  ctx.lineTo(getX(labels.length - 1), h - paddingBottom);
  ctx.lineTo(getX(0), h - paddingBottom);
  ctx.closePath();
  ctx.fill();
  ctx.restore();

  // Draw Line With BeautyPass (Solid Glowing Cyan)
  ctx.save();
  ctx.strokeStyle = "#06B6D4";
  ctx.lineWidth = 3.5;
  ctx.shadowColor = "rgba(6, 182, 212, 0.6)";
  ctx.shadowBlur = 12;
  drawSpline(ptsWith);
  ctx.stroke();
  ctx.restore();

  // Draw Data Points & Labels
  labels.forEach((label, i) => {
    const x = getX(i);
    const y = ptsWith[i].y;

    // Outer glow circle
    ctx.fillStyle = "rgba(6, 182, 212, 0.25)";
    ctx.beginPath();
    ctx.arc(x, y, 7, 0, Math.PI * 2);
    ctx.fill();

    // Main Point
    ctx.fillStyle = "#06B6D4";
    ctx.beginPath();
    ctx.arc(x, y, 4.5, 0, Math.PI * 2);
    ctx.fill();

    // Inner White Dot
    ctx.fillStyle = "#FFFFFF";
    ctx.beginPath();
    ctx.arc(x, y, 2, 0, Math.PI * 2);
    ctx.fill();

    // Value bubble on last item (-67%)
    if (i === labels.length - 1) {
      ctx.fillStyle = "#10B981";
      ctx.font = "bold 10px Plus Jakarta Sans, sans-serif";
      ctx.textAlign = "center";
      ctx.fillText("-67%", x, y - 12);
    }

    // X Axis text
    ctx.fillStyle = "#94A3B8";
    ctx.font = "600 11px Inter, sans-serif";
    ctx.textAlign = "center";
    ctx.fillText(label, x, h - 14);
  });
}

function renderAdhesionChart() {
  const canvas = document.getElementById("chartAdhesion");
  if (!canvas) return;

  const ctx = canvas.getContext("2d");
  const dpr = window.devicePixelRatio || 2;
  const rect = canvas.getBoundingClientRect();
  const w = rect.width || 450;
  const h = 240;

  canvas.width = w * dpr;
  canvas.height = h * dpr;
  ctx.scale(dpr, dpr);

  const paddingX = 30;
  const paddingTop = 30;

  ctx.clearRect(0, 0, w, h);

  const benefits = [
    { name: "BeautyPass", rate: 84, colorStart: "#0D9488", colorEnd: "#06B6D4", isHero: true },
    { name: "GymPass / Fitness", rate: 32, colorStart: "#475569", colorEnd: "#334155", isHero: false },
    { name: "Terapia Online", rate: 26, colorStart: "#475569", colorEnd: "#334155", isHero: false },
    { name: "App Meditação", rate: 18, colorStart: "#475569", colorEnd: "#334155", isHero: false }
  ];

  const barHeight = 26;
  const gap = 22;
  const labelWidth = 125;

  benefits.forEach((item, i) => {
    const y = paddingTop + i * (barHeight + gap);
    const maxBarW = w - paddingX * 2 - labelWidth - 70;
    const barW = Math.max(12, (item.rate / 100) * maxBarW);
    const startX = paddingX + labelWidth;

    // Label
    ctx.fillStyle = item.isHero ? "#FFFFFF" : "#94A3B8";
    ctx.font = item.isHero ? "bold 12px Plus Jakarta Sans, sans-serif" : "600 11px Inter, sans-serif";
    ctx.textAlign = "left";
    ctx.fillText(item.name, paddingX, y + 17);

    // Bar Background Track
    ctx.fillStyle = "rgba(255, 255, 255, 0.05)";
    ctx.beginPath();
    ctx.roundRect(startX, y, maxBarW, barHeight, 6);
    ctx.fill();

    // Active Bar Fill with Gradient & Shadow
    ctx.save();
    if (item.isHero) {
      const grad = ctx.createLinearGradient(startX, 0, startX + barW, 0);
      grad.addColorStop(0, item.colorStart);
      grad.addColorStop(1, item.colorEnd);
      ctx.fillStyle = grad;
      ctx.shadowColor = "rgba(6, 182, 212, 0.4)";
      ctx.shadowBlur = 10;
    } else {
      ctx.fillStyle = item.colorStart;
    }
    ctx.beginPath();
    ctx.roundRect(startX, y, barW, barHeight, 6);
    ctx.fill();
    ctx.restore();

    // Value percentage
    ctx.fillStyle = item.isHero ? "#38BDF8" : "#94A3B8";
    ctx.font = item.isHero ? "bold 12px Plus Jakarta Sans, sans-serif" : "600 11px Inter, sans-serif";
    ctx.textAlign = "left";
    ctx.fillText(`${item.rate}%`, startX + barW + 10, y + 17);
  });
}

// ==========================================================================
// 10. B2B ROI & IMPACT CALCULATOR
// ==========================================================================
function setupRoiCalculator() {
  const slider = document.getElementById("employeeSlider");
  const display = document.getElementById("employeeCountDisplay");
  const btnCount = document.getElementById("btnColabCount");
  const copayBtns = document.querySelectorAll(".btn-copay");

  let copayPercent = 100;

  copayBtns.forEach(btn => {
    btn.addEventListener("click", () => {
      copayBtns.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      copayPercent = parseInt(btn.dataset.copay, 10);
      updateRoi();
    });
  });

  if (slider) {
    slider.addEventListener("input", () => {
      updateRoi();
    });
  }

  function updateRoi() {
    const count = parseInt(slider.value, 10);
    if (display) display.textContent = `${count.toLocaleString("pt-BR")} colaboradores`;
    if (btnCount) btnCount.textContent = count.toLocaleString("pt-BR");

    // Realistic Math Projections
    // 24 hours of recovered productivity per employee/year
    const hoursSaved = count * 24; 
    // R$ 960 average saved per life in preventive health costs
    const healthSavings = count * 960 * (copayPercent / 100);
    // NPS bump
    const npsBump = count > 500 ? "+42 Pontos" : count > 100 ? "+38 Pontos" : "+35 Pontos";
    // ROI multiplier
    const roiFactor = (3.2 + (count / 2000)).toFixed(1);

    document.getElementById("metricHours").textContent = `${hoursSaved.toLocaleString("pt-BR")}h / ano`;
    document.getElementById("metricHealthSavings").textContent = `R$ ${healthSavings.toLocaleString("pt-BR", { maximumFractionDigits: 0 })}`;
    document.getElementById("metricNps").textContent = npsBump;
    document.getElementById("metricRoi").textContent = `${roiFactor}x`;
  }

  updateRoi();
}

function scrollToB2bForm() {
  const el = document.getElementById("b2b-form");
  if (el) el.scrollIntoView({ behavior: "smooth" });
}

function handleB2bSubmit(e) {
  e.preventDefault();
  const name = document.getElementById("b2bName").value;
  const company = document.getElementById("b2bCompany").value;
  
  showToast(`Solicitação enviada com sucesso para a empresa ${company}! Entraremos em contato via WhatsApp/Email.`);
  document.getElementById("b2bLeadForm").reset();
}

// ==========================================================================
// 11. PARTNER RECEPTION DESK & VOUCHER VALIDATOR SIMULATOR
// ==========================================================================
function fillValidatorCode(code) {
  const input = document.getElementById("posVoucherInput");
  if (input) {
    input.value = code;
    simulateVoucherValidation();
  }
}

function simulateVoucherValidation() {
  const input = document.getElementById("posVoucherInput");
  const resultBox = document.getElementById("posResultBox");
  const btnValidate = document.getElementById("btnPosValidate");
  const laser = document.querySelector(".scanner-laser");
  if (!input || !resultBox) return;

  const rawCode = input.value.trim().toUpperCase();
  if (!rawCode) {
    showToast("Por favor, digite ou selecione um código de voucher.");
    return;
  }

  // Active scanning animation state
  if (btnValidate) {
    btnValidate.disabled = true;
    btnValidate.textContent = "Validando...";
  }
  if (laser) {
    laser.style.animationDuration = "0.8s";
  }

  setTimeout(() => {
    if (btnValidate) {
      btnValidate.disabled = false;
      btnValidate.textContent = "Validar";
    }
    if (laser) {
      laser.style.animationDuration = "2.5s";
    }

    // Known sample codes
    const sampleVouchers = {
      "BP-9042-LUX": {
        clinic: "Buddha Spa & Lounge Estético",
        patient: "Mariana Silva",
        procedure: "Massagem Relaxante com Aromaterapia",
        payout: "R$ 195,00",
        plan: "Plano Premium Black"
      },
      "BP-8812-SKN": {
        clinic: "DermaPrime Medicina Estética",
        patient: "Lucas Andrade",
        procedure: "Peeling de Diamante + Fototerapia LED",
        payout: "R$ 245,00",
        plan: "Plano Premium Black"
      },
      "BP-7731-BAR": {
        clinic: "The Gentleman Barber Lounge",
        patient: "Rodrigo Mendonça",
        procedure: "Barba Terapia & Corte Visagista",
        payout: "R$ 110,00",
        plan: "Plano Básico"
      }
    };

    const found = sampleVouchers[rawCode];
    resultBox.style.display = "block";

    if (found) {
      resultBox.className = "pos-result-box pos-result-success";
      resultBox.innerHTML = `
        <div class="res-header success">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
          <span>VOUCHER AUTORIZADO COM SUCESSO!</span>
        </div>
        <div class="res-details">
          <div><strong>Beneficiário:</strong> ${found.patient} (${found.plan})</div>
          <div><strong>Procedimento:</strong> ${found.procedure}</div>
          <div><strong>Repasse p/ Estabelecimento (D+1):</strong> <span class="text-emerald" style="font-weight: 800;">${found.payout}</span></div>
          <div style="font-size: 0.6875rem; color: #94A3B8; margin-top: 4px;">Data/Hora: ${new Date().toLocaleTimeString("pt-BR")} • Protocolo de Liquidação #${Math.floor(10000 + Math.random() * 90000)}</div>
        </div>
      `;
      showToast(`Voucher ${rawCode} validado! Repasse liberado em D+1.`);
    } else {
      resultBox.className = "pos-result-box pos-result-error";
      resultBox.innerHTML = `
        <div class="res-header error">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
          <span>VOUCHER NÃO ENCONTRADO OU JÁ UTILIZADO</span>
        </div>
        <div class="res-details">
          <div>Verifique se o código foi digitado corretamente ou se o voucher expirou no app do cliente.</div>
        </div>
      `;
      showToast(`Voucher ${rawCode} inválido.`, "error");
    }
  }, 500);
}

// ==========================================================================
// 12. 24H AI CONCIERGE CHAT WIDGET
// ==========================================================================
function toggleConcierge() {
  const panel = document.getElementById("conciergePanel");
  if (panel) {
    panel.classList.toggle("active");
    if (panel.classList.contains("active")) {
      document.getElementById("chatInput")?.focus();
    }
  }
}

function sendQuickPrompt(promptText) {
  const input = document.getElementById("chatInput");
  if (input) {
    input.value = promptText;
    handleChatSubmit(new Event("submit"));
  }
}

function handleChatSubmit(e) {
  e.preventDefault();
  const input = document.getElementById("chatInput");
  const container = document.getElementById("conciergeMessages");
  if (!input || !container) return;

  const text = input.value.trim();
  if (!text) return;

  // Append user message
  const userMsg = document.createElement("div");
  userMsg.className = "chat-msg user-msg";
  userMsg.textContent = text;
  container.appendChild(userMsg);
  input.value = "";
  container.scrollTop = container.scrollHeight;

  // Append typing indicator bubble
  const typingIndicator = document.createElement("div");
  typingIndicator.className = "chat-msg bot-msg bot-typing";
  typingIndicator.innerHTML = `
    <span style="display: inline-flex; gap: 4px; align-items: center;">
      <span class="typing-dot" style="width:6px;height:6px;background:#0D9488;border-radius:50%;animation:typingBounce 1s infinite 0s;"></span>
      <span class="typing-dot" style="width:6px;height:6px;background:#0D9488;border-radius:50%;animation:typingBounce 1s infinite 0.2s;"></span>
      <span class="typing-dot" style="width:6px;height:6px;background:#0D9488;border-radius:50%;animation:typingBounce 1s infinite 0.4s;"></span>
    </span>
  `;
  container.appendChild(typingIndicator);
  container.scrollTop = container.scrollHeight;

  // Bot Smart AI response simulation
  setTimeout(() => {
    typingIndicator.remove();

    const botMsg = document.createElement("div");
    botMsg.className = "chat-msg bot-msg";

    const lower = text.toLowerCase();
    if (lower.includes("voucher") || lower.includes("massagem") || lower.includes("gerar")) {
      botMsg.innerHTML = `Para emitir seu voucher de massagem: vá até o catálogo no início da página, escolha a clínica <strong>Buddha Spa</strong> e clique em <strong>Gerar Voucher Imediato</strong>! Você terá 15 minutos para apresentar o QR Code na recepção.`;
    } else if (lower.includes("cobertura") || lower.includes("plano") || lower.includes("premium")) {
      botMsg.innerHTML = `O seu <strong>Plano Premium Black</strong> inclui 4 procedimentos mensais de alta tecnologia (Laser Lavieen, Peelings químicos, Spas 5 estrelas e Drenagem método Renata França) com 100% de cobertura.`;
    } else if (lower.includes("empresa") || lower.includes("rh") || lower.includes("indicar")) {
      botMsg.innerHTML = `Quer que sua empresa pague o seu plano BeautyPass? Basta navegar até a aba <strong>Para Empresas (RH)</strong> e preencher o formulário para enviarmos uma proposta personalizada ao seu RH com 1 mês grátis!`;
    } else {
      botMsg.innerHTML = `Entendido! Nosso suporte ao vivo 24h está à disposição. Você pode consultar seu histórico e vouchers a qualquer momento clicando no botão <strong>Meus Vouchers</strong> no topo da página.`;
    }

    container.appendChild(botMsg);
    container.scrollTop = container.scrollHeight;
  }, 700);
}

// ==========================================================================
// 13. TOAST NOTIFICATION ENGINE
// ==========================================================================
function showToast(message, type = "success") {
  const container = document.getElementById("toastContainer");
  if (!container) return;

  const toast = document.createElement("div");
  toast.className = "toast-msg";
  toast.innerHTML = `
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="${type === 'error' ? '#F87171' : '#2DD4BF'}" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
    <span>${message}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transform = "translateY(10px)";
    toast.style.transition = "all 250ms ease";
    setTimeout(() => toast.remove(), 250);
  }, 3500);
}

// ==========================================================================
// 14. PREMIUM ANIMATION SYSTEM
// ==========================================================================

const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

// --- Header Scroll Compaction ---
function initHeaderScrollCompaction() {
  const header = document.getElementById('siteHeader');
  if (!header) return;

  let ticking = false;
  window.addEventListener('scroll', () => {
    if (!ticking) {
      requestAnimationFrame(() => {
        header.classList.toggle('header--scrolled', window.scrollY > 80);
        ticking = false;
      });
      ticking = true;
    }
  }, { passive: true });
}

// --- Hero Mesh Gradient Canvas Renderer ---
function initHeroMeshCanvas() {
  const canvas = document.getElementById('heroMeshCanvas');
  if (!canvas || prefersReducedMotion) return;

  const ctx2d = canvas.getContext('2d');
  let animId;
  let time = 0;

  const spheres = [
    { x: 0.3, y: 0.4, r: 280, color: [13, 148, 136], alpha: 0.12, speed: 0.0004, phaseX: 0, phaseY: Math.PI / 3 },
    { x: 0.7, y: 0.3, r: 240, color: [6, 182, 212], alpha: 0.10, speed: 0.0005, phaseX: Math.PI / 2, phaseY: 0 },
    { x: 0.5, y: 0.7, r: 200, color: [16, 185, 129], alpha: 0.08, speed: 0.0003, phaseX: Math.PI, phaseY: Math.PI / 4 },
    { x: 0.2, y: 0.6, r: 160, color: [56, 189, 248], alpha: 0.06, speed: 0.0006, phaseX: Math.PI / 4, phaseY: Math.PI / 2 }
  ];

  function resizeCanvas() {
    const rect = canvas.parentElement.getBoundingClientRect();
    canvas.width = rect.width;
    canvas.height = rect.height;
  }

  function drawSphere(s) {
    const cx = canvas.width * (s.x + 0.08 * Math.sin(time * s.speed * 1000 + s.phaseX));
    const cy = canvas.height * (s.y + 0.06 * Math.cos(time * s.speed * 1000 + s.phaseY));
    const scaledR = s.r * (canvas.width / 1200);

    const gradient = ctx2d.createRadialGradient(cx, cy, 0, cx, cy, scaledR);
    gradient.addColorStop(0, `rgba(${s.color.join(',')}, ${s.alpha})`);
    gradient.addColorStop(0.5, `rgba(${s.color.join(',')}, ${s.alpha * 0.4})`);
    gradient.addColorStop(1, `rgba(${s.color.join(',')}, 0)`);

    ctx2d.fillStyle = gradient;
    ctx2d.fillRect(cx - scaledR, cy - scaledR, scaledR * 2, scaledR * 2);
  }

  function renderMesh(timestamp) {
    time = timestamp;
    ctx2d.clearRect(0, 0, canvas.width, canvas.height);
    spheres.forEach(drawSphere);
    animId = requestAnimationFrame(renderMesh);
  }

  resizeCanvas();
  window.addEventListener('resize', resizeCanvas, { passive: true });
  animId = requestAnimationFrame(renderMesh);

  // Pause when hero is not visible for performance
  const visObserver = new IntersectionObserver(([entry]) => {
    if (entry.isIntersecting) {
      if (!animId) animId = requestAnimationFrame(renderMesh);
    } else {
      if (animId) { cancelAnimationFrame(animId); animId = null; }
    }
  }, { threshold: 0.1 });
  visObserver.observe(canvas.parentElement);
}

// --- GSAP Hero Reveal Timeline ---
function initHeroRevealAnimations() {
  if (typeof gsap === 'undefined' || prefersReducedMotion) {
    // Fallback: just show everything immediately
    document.querySelectorAll('.reveal-up, .reveal-fade').forEach(el => {
      el.style.opacity = '1';
      el.style.transform = 'none';
    });
    return;
  }

  gsap.registerPlugin(ScrollTrigger);

  const heroTextCol = document.querySelector('.hero-text-col');
  const heroVisual = document.querySelector('.hero-visual');
  if (!heroTextCol) return;

  const tl = gsap.timeline({ 
    defaults: { ease: "power3.out" },
    delay: 0.3
  });

  // Trust badge entrance
  const badge = heroTextCol.querySelector('.trust-badge-pill');
  if (badge) {
    tl.fromTo(badge, 
      { opacity: 0, y: 24 }, 
      { opacity: 1, y: 0, duration: 0.6 }
    );
  }

  // Title lines (staggered cinematic reveal)
  const titleLines = heroTextCol.querySelectorAll('.title-line-inner');
  if (titleLines.length > 0) {
    tl.fromTo(titleLines, 
      { opacity: 0, y: 40 }, 
      { opacity: 1, y: 0, duration: 0.6, stagger: 0.12 },
      "-=0.3"
    );
  }

  // Description fade-in
  const desc = heroTextCol.querySelector('.hero-description');
  if (desc) {
    tl.fromTo(desc, 
      { opacity: 0, y: 20 }, 
      { opacity: 1, y: 0, duration: 0.5 },
      "-=0.25"
    );
  }

  // Search bar slide-up with scale
  const searchBar = heroTextCol.querySelector('.smart-search-card');
  if (searchBar) {
    tl.fromTo(searchBar, 
      { opacity: 0, y: 30, scale: 0.97 }, 
      { opacity: 1, y: 0, scale: 1, duration: 0.6 },
      "-=0.2"
    );
  }

  // Trust pills stagger
  const trustPills = heroTextCol.querySelectorAll('.trust-pill');
  if (trustPills.length > 0) {
    tl.fromTo(trustPills, 
      { opacity: 0, y: 16 }, 
      { opacity: 1, y: 0, duration: 0.4, stagger: 0.08 },
      "-=0.2"
    );
  }

  // Hero visual card (from right with scale)
  if (heroVisual) {
    tl.fromTo(heroVisual, 
      { opacity: 0, x: 40, scale: 0.95 }, 
      { opacity: 1, x: 0, scale: 1, duration: 0.8 },
      "-=0.5"
    );
  }

  // Remove CSS reveal classes since GSAP handles animation
  heroTextCol.querySelectorAll('.reveal-up').forEach(el => {
    el.classList.remove('reveal-up');
  });
  if (heroVisual) heroVisual.classList.remove('reveal-up');
}

// --- Scroll Reveal System (Intersection Observer) ---
function initScrollReveals() {
  if (prefersReducedMotion) {
    document.querySelectorAll('.reveal-up, .reveal-fade').forEach(el => {
      el.style.opacity = '1';
      el.style.transform = 'none';
    });
    return;
  }

  const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('revealed');
        revealObserver.unobserve(entry.target);
      }
    });
  }, { 
    threshold: 0.15,
    rootMargin: '0px 0px -40px 0px'
  });

  // Observe elements outside the hero (hero is handled by GSAP timeline)
  document.querySelectorAll('.reveal-up, .reveal-fade').forEach(el => {
    if (!el.closest('.hero-b2c')) {
      revealObserver.observe(el);
    }
  });
}

// --- Smooth Animated Number Counters ---
function initCounterAnimations() {
  const counterElements = document.querySelectorAll('[data-target]');
  if (!counterElements.length) return;

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        animateCounter(entry.target);
        observer.unobserve(entry.target);
      }
    });
  }, { threshold: 0.2 });

  counterElements.forEach(el => observer.observe(el));
}

function animateCounter(el) {
  const target = parseFloat(el.dataset.target);
  const prefix = el.dataset.prefix || "";
  const suffix = el.dataset.suffix || "";
  const format = el.dataset.format || "int";
  const duration = 1800; // ms
  const startTime = performance.now();

  function update(now) {
    const elapsed = now - startTime;
    const progress = Math.min(elapsed / duration, 1);
    // Smooth easeOutExpo
    const ease = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress);
    const current = target * ease;

    let formattedValue;
    if (format === "currency") {
      formattedValue = current.toLocaleString("pt-BR", { minimumFractionDigits: 0, maximumFractionDigits: 0 });
    } else if (format === "decimal") {
      formattedValue = current.toFixed(2);
    } else {
      formattedValue = Math.round(current).toLocaleString("pt-BR");
    }

    el.textContent = `${prefix}${formattedValue}${suffix}`;

    if (progress < 1) {
      requestAnimationFrame(update);
    } else {
      // Final target exact formatting
      let finalFormatted = format === "decimal" ? target.toFixed(2) : target.toLocaleString("pt-BR");
      if (el.dataset.format === "decimal" && el.dataset.suffix === "M") {
        finalFormatted = target.toFixed(1);
      }
      el.textContent = `${prefix}${finalFormatted}${suffix}`;
    }
  }

  requestAnimationFrame(update);
}
