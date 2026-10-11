// BeautyPass Service Worker — Versão 1.0.0
// Estratégia: Stale-While-Revalidate com Fallback Offline Robusto

const CACHE_NAME = 'beautypass-cache-v1.0.0';

const PRECACHE_ASSETS = [
  './',
  './beautypass_app.html',
  './styles.css',
  './app.js',
  './manifest.webmanifest',
  './Logo BeautyPass.jfif',
  './assets/logo_beautypass_emblem.png',
  './icons/icon-72.png',
  './icons/icon-96.png',
  './icons/icon-128.png',
  './icons/icon-144.png',
  './icons/icon-152.png',
  './icons/icon-192.png',
  './icons/icon-maskable-192.png',
  './icons/icon-384.png',
  './icons/icon-512.png',
  './icons/icon-maskable-512.png',
  './icons/shortcut-ondemand.png',
  './icons/shortcut-reservations.png',
  './icons/shortcut-map.png',
  './treatment_eyebrows_beauty.jpg',
  './treatment_hair_salon.jpg',
  './treatment_facial_spa.jpg',
  './treatment_massage_spa.jpg',
  './treatment_skincare_cosmetics.jpg',
  './salon_barber_modern.jpg',
  './salon_clinic_aesthetic.jpg',
  './salon_hair_boutique.jpg',
  './salon_nail_lounge.jpg',
  './salon_spa_oasis.jpg',
  './friend_beatriz.jpg',
  './friend_camila.jpg',
  './friend_larissa.jpg',
  './profile_owner_juliana.jpg',
  './profile_owner_marcos.jpg',
  './profile_owner_renata.jpg'
];

// Instalação do Service Worker e pré-cache
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      console.log('[Service Worker] Pré-cache de recursos críticos do BeautyPass');
      return cache.addAll(PRECACHE_ASSETS).catch((err) => {
        console.warn('[Service Worker] Falha ao pré-cachear alguns itens:', err);
      });
    }).then(() => self.skipWaiting())
  );
});

// Ativação e limpeza de versões antigas do cache
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((name) => {
          if (name !== CACHE_NAME) {
            console.log('[Service Worker] Removendo cache obsoleto:', name);
            return caches.delete(name);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// Interceptação de requisições: Stale-While-Revalidate
self.addEventListener('fetch', (event) => {
  const { request } = event;
  const url = new URL(request.url);

  // Ignorar chamadas não-GET ou extensões do navegador
  if (request.method !== 'GET' || url.protocol.startsWith('chrome-extension')) {
    return;
  }

  // Requisições de navegação (HTML principal)
  if (request.mode === 'navigate') {
    event.respondWith(
      fetch(request)
        .then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const clone = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => cache.put(request, clone));
          }
          return networkResponse;
        })
        .catch(() => {
          // Fallback offline: serve o beautypass_app.html em cache
          return caches.match('./beautypass_app.html') || caches.match(request);
        })
    );
    return;
  }

  // Recursos estáticos locais e CDNs públicas (Leaflet, Google Fonts)
  event.respondWith(
    caches.match(request).then((cachedResponse) => {
      const fetchPromise = fetch(request)
        .then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const clone = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => cache.put(request, clone));
          }
          return networkResponse;
        })
        .catch(() => cachedResponse);

      // Retorna imediatamente do cache (se houver), enquanto atualiza em background
      return cachedResponse || fetchPromise;
    })
  );
});
