importScripts('https://www.gstatic.com/firebasejs/9.23.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.23.0/firebase-messaging-compat.js');

// 새 SW가 설치되면 즉시 활성화하고 모든 클라이언트를 제어
self.addEventListener('install', () => self.skipWaiting());
self.addEventListener('activate', (event) => event.waitUntil(self.clients.claim()));

// 데이터 전용 메시지(data-only) 대비: SDK 초기화 타이밍 전에 도착해도 표시 보장
self.addEventListener('push', (event) => {
  try {
    const payload = event.data ? event.data.json() : {};
    // notification payload가 있으면 브라우저/SDK가 처리하므로 중복 방지
    if (payload && payload.notification) return;

    const title = (payload.data && payload.data.title) || '알림';
    const body = (payload.data && (payload.data.body || payload.data.text)) || '';
    const icon = (payload.data && payload.data.icon) || '📭';
    const link = (payload.data && payload.data.link) || '/';
    const tag = (payload.data && payload.data.tag) || `${title}-${body}`;

    event.waitUntil(
      self.registration.showNotification(title, {
        body,
        icon,
        data: { link },
        tag,
        renotify: false,
        requireInteraction: false,
      }),
    );
  } catch (e) {
    // noop
  }
});

async function initFirebase() {
  const response = await fetch('/firebase-config.json');
  const firebaseConfig = await response.json();

  firebase.initializeApp(firebaseConfig);

  const messaging = firebase.messaging();

  messaging.onBackgroundMessage((payload) => {
    console.log('📩 백그라운드 메시지:', payload);

    // notification 페이로드가 존재하면 브라우저/OS가 이미 처리했을 수 있으므로 중복 방지
    if (payload && payload.notification) {
      return;
    }

    const title =
      (payload.notification && payload.notification.title) ||
      (payload.data && payload.data.title) ||
      '알림';
    const body =
      (payload.notification && payload.notification.body) ||
      (payload.data && (payload.data.body || payload.data.text)) ||
      '';
    const icon = (payload.notification && payload.notification.image) || '📭';
    const link =
      (payload.fcmOptions && payload.fcmOptions.link) || (payload.data && payload.data.link) || '/';

    const tag = (payload.data && payload.data.tag) || `${title}-${body}`;
    self.registration.showNotification(title, {
      body,
      icon,
      data: { link },
      tag,
      renotify: false,
      requireInteraction: false,
    });
  });
}

initFirebase();

// 알림 클릭 시 탭 포커스/열기
self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  const url =
    (event.notification && event.notification.data && event.notification.data.link) || '/';
  event.waitUntil(
    self.clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
      for (const client of clientList) {
        if ('focus' in client) return client.focus();
      }
      if (self.clients.openWindow) return self.clients.openWindow(url);
      return undefined;
    }),
  );
});
