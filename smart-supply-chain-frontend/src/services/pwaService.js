/**
 * Progressive Web App Service
 * Handles service worker registration, push notifications, and offline capabilities
 */
class PWAService {
  constructor() {
    this.swRegistration = null;
    this.isOnline = navigator.onLine;
    this.offlineQueue = [];
    this.initializeEventListeners();
  }

  /**
   * Initialize PWA service
   */
  async initialize() {
    try {
      console.log('PWA Service: Initializing...');
      
      if ('serviceWorker' in navigator) {
        await this.registerServiceWorker();
        await this.requestNotificationPermission();
        this.initializeOfflineSupport();
        this.initializePushNotifications();
        
        console.log('PWA Service: Initialization complete');
        return true;
      } else {
        console.warn('PWA Service: Service Worker not supported');
        return false;
      }
    } catch (error) {
      console.error('PWA Service: Initialization failed:', error);
      return false;
    }
  }

  /**
   * Register service worker
   */
  async registerServiceWorker() {
    try {
      const registration = await navigator.serviceWorker.register('/sw.js', {
        scope: '/'
      });

      this.swRegistration = registration;

      console.log('PWA Service: Service Worker registered successfully');

      // Handle service worker updates
      registration.addEventListener('updatefound', () => {
        const newWorker = registration.installing;
        newWorker.addEventListener('statechange', () => {
          if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
            // New content available
            this.showUpdateNotification();
          }
        });
      });

      // Listen for messages from service worker
      navigator.serviceWorker.addEventListener('message', (event) => {
        this.handleServiceWorkerMessage(event.data);
      });

      return registration;
    } catch (error) {
      console.error('PWA Service: Service Worker registration failed:', error);
      throw error;
    }
  }

  /**
   * Request notification permission
   */
  async requestNotificationPermission() {
    if ('Notification' in window) {
      const permission = await Notification.requestPermission();
      console.log('PWA Service: Notification permission:', permission);
      return permission === 'granted';
    }
    return false;
  }

  /**
   * Initialize offline support
   */
  initializeOfflineSupport() {
    // Monitor online/offline status
    window.addEventListener('online', () => {
      console.log('PWA Service: Back online');
      this.isOnline = true;
      this.processPendingActions();
      this.showConnectivityNotification('Online', 'Connection restored');
    });

    window.addEventListener('offline', () => {
      console.log('PWA Service: Gone offline');
      this.isOnline = false;
      this.showConnectivityNotification('Offline', 'Working offline mode');
    });

    // Intercept fetch requests for offline queueing
    this.interceptAPIRequests();
  }

  /**
   * Initialize push notifications
   */
  async initializePushNotifications() {
    if (!this.swRegistration || !('pushManager' in window)) {
      console.warn('PWA Service: Push notifications not supported');
      return;
    }

    try {
      const subscription = await this.swRegistration.pushManager.getSubscription();
      if (subscription) {
        console.log('PWA Service: Push subscription active');
        // Send subscription to server
        await this.sendSubscriptionToServer(subscription);
      } else {
        console.log('PWA Service: No push subscription found');
      }
    } catch (error) {
      console.error('PWA Service: Push notification setup failed:', error);
    }
  }

  /**
   * Subscribe to push notifications
   */
  async subscribeToPush() {
    if (!this.swRegistration) {
      throw new Error('Service Worker not registered');
    }

    try {
      const subscription = await this.swRegistration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: this.urlBase64ToUint8Array(process.env.REACT_APP_VAPID_PUBLIC_KEY)
      });

      console.log('PWA Service: Push subscription created');
      await this.sendSubscriptionToServer(subscription);
      return subscription;
    } catch (error) {
      console.error('PWA Service: Push subscription failed:', error);
      throw error;
    }
  }

  /**
   * Send subscription to server
   */
  async sendSubscriptionToServer(subscription) {
    try {
      const response = await fetch('/api/notifications/push/subscribe', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(subscription)
      });

      if (response.ok) {
        console.log('PWA Service: Subscription sent to server');
      } else {
        console.error('PWA Service: Failed to send subscription to server');
      }
    } catch (error) {
      console.error('PWA Service: Error sending subscription to server:', error);
    }
  }

  /**
   * Show local notification
   */
  showNotification(title, options = {}) {
    if (!('Notification' in window) || Notification.permission !== 'granted') {
      console.warn('PWA Service: Notifications not permitted');
      return;
    }

    const defaultOptions = {
      icon: '/icon-192x192.png',
      badge: '/icon-72x72.png',
      tag: 'supply-chain-notification',
      requireInteraction: false,
      ...options
    };

    if (this.swRegistration) {
      // Use service worker to show notification
      this.swRegistration.showNotification(title, defaultOptions);
    } else {
      // Fallback to browser notification
      new Notification(title, defaultOptions);
    }
  }

  /**
   * Show connectivity notification
   */
  showConnectivityNotification(status, message) {
    this.showNotification(`Supply Chain RI - ${status}`, {
      body: message,
      tag: 'connectivity',
      icon: status === 'Online' ? '/icon-online.png' : '/icon-offline.png',
      requireInteraction: false
    });
  }

  /**
   * Show update notification
   */
  showUpdateNotification() {
    this.showNotification('Update Available', {
      body: 'A new version of Supply Chain RI is available. Refresh to update.',
      tag: 'update',
      requireInteraction: true,
      actions: [
        { action: 'update', title: 'Update Now' },
        { action: 'dismiss', title: 'Later' }
      ]
    });
  }

  /**
   * Initialize event listeners
   */
  initializeEventListeners() {
    // Handle notification clicks
    document.addEventListener('notificationclick', (event) => {
      if (event.action === 'update') {
        window.location.reload();
      }
    });

    // Handle beforeinstallprompt event
    window.addEventListener('beforeinstallprompt', (event) => {
      event.preventDefault();
      this.deferredPrompt = event;
      this.showInstallBanner();
    });

    // Handle app installed event
    window.addEventListener('appinstalled', () => {
      console.log('PWA Service: App installed successfully');
      this.hideInstallBanner();
    });
  }

  /**
   * Show install banner
   */
  showInstallBanner() {
    // Create install banner element
    const banner = document.createElement('div');
    banner.id = 'pwa-install-banner';
    banner.className = 'pwa-install-banner';
    banner.innerHTML = `
      <div class="banner-content">
        <span>Install Supply Chain RI for better experience</span>
        <button id="install-button" class="install-btn">Install</button>
        <button id="dismiss-button" class="dismiss-btn">×</button>
      </div>
    `;

    // Add styles
    const style = document.createElement('style');
    style.textContent = `
      .pwa-install-banner {
        position: fixed;
        bottom: 20px;
        left: 20px;
        right: 20px;
        background: #1976d2;
        color: white;
        padding: 16px;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        z-index: 1000;
        display: flex;
        align-items: center;
        justify-content: space-between;
      }
      .banner-content {
        display: flex;
        align-items: center;
        gap: 12px;
        width: 100%;
      }
      .install-btn {
        background: white;
        color: #1976d2;
        border: none;
        padding: 8px 16px;
        border-radius: 4px;
        cursor: pointer;
        font-weight: bold;
      }
      .dismiss-btn {
        background: none;
        border: none;
        color: white;
        font-size: 20px;
        cursor: pointer;
        margin-left: auto;
      }
    `;

    document.head.appendChild(style);
    document.body.appendChild(banner);

    // Add event listeners
    document.getElementById('install-button').addEventListener('click', () => {
      this.installApp();
    });

    document.getElementById('dismiss-button').addEventListener('click', () => {
      this.hideInstallBanner();
    });
  }

  /**
   * Hide install banner
   */
  hideInstallBanner() {
    const banner = document.getElementById('pwa-install-banner');
    if (banner) {
      banner.remove();
    }
  }

  /**
   * Install app
   */
  async installApp() {
    if (!this.deferredPrompt) {
      console.warn('PWA Service: No install prompt available');
      return;
    }

    try {
      this.deferredPrompt.prompt();
      const choiceResult = await this.deferredPrompt.userChoice;
      
      if (choiceResult.outcome === 'accepted') {
        console.log('PWA Service: User accepted install prompt');
      } else {
        console.log('PWA Service: User dismissed install prompt');
      }
      
      this.deferredPrompt = null;
      this.hideInstallBanner();
    } catch (error) {
      console.error('PWA Service: Install failed:', error);
    }
  }

  /**
   * Intercept API requests for offline queueing
   */
  interceptAPIRequests() {
    const originalFetch = window.fetch;
    
    window.fetch = async (...args) => {
      const [url, options = {}] = args;
      
      try {
        const response = await originalFetch(...args);
        return response;
      } catch (error) {
        // If request fails and we're offline, queue it
        if (!this.isOnline && url.includes('/api/')) {
          console.log('PWA Service: Queueing offline request:', url);
          this.queueOfflineAction(url, options);
          
          // Return cached response if available
          const cachedResponse = await this.getCachedResponse(url);
          if (cachedResponse) {
            return cachedResponse;
          }
        }
        throw error;
      }
    };
  }

  /**
   * Queue offline action
   */
  queueOfflineAction(url, options) {
    const action = {
      id: Date.now() + Math.random(),
      url,
      method: options.method || 'GET',
      headers: options.headers || {},
      body: options.body,
      timestamp: new Date().toISOString()
    };

    this.offlineQueue.push(action);
    console.log('PWA Service: Action queued for sync:', action.id);
  }

  /**
   * Process pending offline actions
   */
  async processPendingActions() {
    if (this.offlineQueue.length === 0) {
      return;
    }

    console.log(`PWA Service: Processing ${this.offlineQueue.length} pending actions`);

    for (const action of [...this.offlineQueue]) {
      try {
        await fetch(action.url, {
          method: action.method,
          headers: action.headers,
          body: action.body
        });

        // Remove successful action from queue
        this.offlineQueue = this.offlineQueue.filter(a => a.id !== action.id);
        console.log('PWA Service: Synced action:', action.id);
      } catch (error) {
        console.error('PWA Service: Failed to sync action:', action.id, error);
      }
    }

    if (this.offlineQueue.length === 0) {
      this.showNotification('Sync Complete', {
        body: 'All offline actions have been synchronized',
        tag: 'sync-complete'
      });
    }
  }

  /**
   * Get cached response
   */
  async getCachedResponse(url) {
    if ('caches' in window) {
      try {
        const cache = await caches.open('smart-supply-chain-v1.0.0');
        const response = await cache.match(url);
        return response;
      } catch (error) {
        console.error('PWA Service: Cache lookup failed:', error);
      }
    }
    return null;
  }

  /**
   * Handle service worker messages
   */
  handleServiceWorkerMessage(data) {
    console.log('PWA Service: Message from service worker:', data);

    switch (data.type) {
      case 'SYNC_COMPLETE':
        this.showNotification('Data Synchronized', {
          body: 'Your data has been synchronized successfully',
          tag: 'sync-complete'
        });
        break;
      default:
        console.log('PWA Service: Unknown message type:', data.type);
    }
  }

  /**
   * Utility: Convert VAPID key
   */
  urlBase64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding)
      .replace(/-/g, '+')
      .replace(/_/g, '/');

    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);

    for (let i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  }

  /**
   * Check if app is installed
   */
  isAppInstalled() {
    return window.matchMedia('(display-mode: standalone)').matches ||
           window.navigator.standalone === true;
  }

  /**
   * Get installation status
   */
  getInstallationStatus() {
    return {
      isInstalled: this.isAppInstalled(),
      canInstall: !!this.deferredPrompt,
      serviceWorkerSupported: 'serviceWorker' in navigator,
      notificationsSupported: 'Notification' in window,
      notificationPermission: Notification.permission,
      isOnline: this.isOnline,
      offlineQueueSize: this.offlineQueue.length
    };
  }
}

// Export singleton instance
export default new PWAService();