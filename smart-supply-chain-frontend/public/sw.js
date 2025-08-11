const CACHE_NAME = 'smart-supply-chain-v1.0.0';
const urlsToCache = [
  '/',
  '/static/js/bundle.js',
  '/static/css/main.css',
  '/manifest.json',
  // Add other static assets
];

// Install event - cache resources
self.addEventListener('install', (event) => {
  console.log('Service Worker: Installing...');
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => {
        console.log('Service Worker: Caching files');
        return cache.addAll(urlsToCache);
      })
      .then(() => {
        console.log('Service Worker: Installation complete');
        return self.skipWaiting();
      })
  );
});

// Activate event - clean up old caches
self.addEventListener('activate', (event) => {
  console.log('Service Worker: Activating...');
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (cacheName !== CACHE_NAME) {
            console.log('Service Worker: Deleting old cache:', cacheName);
            return caches.delete(cacheName);
          }
        })
      );
    }).then(() => {
      console.log('Service Worker: Activation complete');
      return self.clients.claim();
    })
  );
});

// Fetch event - serve cached content when offline
self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request)
      .then((response) => {
        // Return cached version or fetch from network
        if (response) {
          console.log('Service Worker: Serving from cache:', event.request.url);
          return response;
        }

        return fetch(event.request).then((response) => {
          // Check if we received a valid response
          if (!response || response.status !== 200 || response.type !== 'basic') {
            return response;
          }

          // Clone the response
          const responseToCache = response.clone();

          caches.open(CACHE_NAME)
            .then((cache) => {
              cache.put(event.request, responseToCache);
            });

          return response;
        }).catch(() => {
          // Return offline page for navigation requests
          if (event.request.destination === 'document') {
            return caches.match('/offline.html');
          }
        });
      })
  );
});

// Push notification event
self.addEventListener('push', (event) => {
  console.log('Service Worker: Push notification received');
  
  let data = {};
  if (event.data) {
    data = event.data.json();
  }

  const options = {
    body: data.body || 'Supply Chain Alert',
    icon: '/icon-192x192.png',
    badge: '/icon-72x72.png',
    tag: data.tag || 'supply-chain-notification',
    renotify: true,
    requireInteraction: data.requireInteraction || false,
    actions: [
      {
        action: 'view',
        title: 'View Details',
        icon: '/icon-view.png'
      },
      {
        action: 'dismiss',
        title: 'Dismiss',
        icon: '/icon-close.png'
      }
    ],
    data: data
  };

  event.waitUntil(
    self.registration.showNotification(data.title || 'Supply Chain Alert', options)
  );
});

// Notification click event
self.addEventListener('notificationclick', (event) => {
  console.log('Service Worker: Notification clicked');
  event.notification.close();

  if (event.action === 'view') {
    // Open the app to specific page
    event.waitUntil(
      clients.openWindow(event.notification.data.url || '/')
    );
  } else if (event.action === 'dismiss') {
    // Just close the notification
    return;
  } else {
    // Default action - open the app
    event.waitUntil(
      clients.openWindow('/')
    );
  }
});

// Background sync event
self.addEventListener('sync', (event) => {
  console.log('Service Worker: Background sync triggered');
  
  if (event.tag === 'sync-shipments') {
    event.waitUntil(syncShipmentData());
  }
});

// Sync shipment data when back online
async function syncShipmentData() {
  try {
    console.log('Service Worker: Syncing shipment data...');
    
    // Get stored offline actions from IndexedDB
    const offlineActions = await getOfflineActions();
    
    for (const action of offlineActions) {
      try {
        await fetch(action.url, {
          method: action.method,
          headers: action.headers,
          body: action.body
        });
        
        // Remove successful action from offline storage
        await removeOfflineAction(action.id);
        
        console.log('Service Worker: Synced action:', action.id);
      } catch (error) {
        console.error('Service Worker: Failed to sync action:', action.id, error);
      }
    }
    
    // Notify clients about sync completion
    const clients = await self.clients.matchAll();
    clients.forEach(client => {
      client.postMessage({
        type: 'SYNC_COMPLETE',
        timestamp: new Date().toISOString()
      });
    });
    
  } catch (error) {
    console.error('Service Worker: Sync failed:', error);
  }
}

// Helper functions for offline storage
async function getOfflineActions() {
  // This would typically use IndexedDB
  // For now, return empty array
  return [];
}

async function removeOfflineAction(actionId) {
  // This would typically remove from IndexedDB
  console.log('Service Worker: Removing offline action:', actionId);
}

// Message event - handle messages from main thread
self.addEventListener('message', (event) => {
  console.log('Service Worker: Message received:', event.data);
  
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});

// Periodic background sync (if supported)
self.addEventListener('periodicsync', (event) => {
  console.log('Service Worker: Periodic sync triggered');
  
  if (event.tag === 'refresh-data') {
    event.waitUntil(refreshCriticalData());
  }
});

async function refreshCriticalData() {
  try {
    console.log('Service Worker: Refreshing critical data...');
    
    // Refresh critical shipment data
    const response = await fetch('/api/shipments/critical');
    if (response.ok) {
      const data = await response.json();
      
      // Store in cache for offline access
      const cache = await caches.open(CACHE_NAME);
      await cache.put('/api/shipments/critical', new Response(JSON.stringify(data)));
      
      console.log('Service Worker: Critical data refreshed');
    }
  } catch (error) {
    console.error('Service Worker: Failed to refresh critical data:', error);
  }
}