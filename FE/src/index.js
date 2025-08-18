import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { messaging, onMessage } from './firebase';

// 서비스워커 등록 (백그라운드 알림 표시를 위해 필요)
if ('serviceWorker' in navigator) {
  // 배포 시 VITE_APP_VERSION을 고정값으로 주입하고, 개발 중에는 쿼리를 생략해 불필요한 재설치를 방지
  const version = import.meta.env.VITE_APP_VERSION;
  const base = import.meta.env.BASE_URL || '/';
  const swPath = version
    ? `${base}firebase-messaging-sw.js?v=${version}`
    : `${base}firebase-messaging-sw.js`;
  navigator.serviceWorker
    .register(swPath, { updateViaCache: 'none' })
    .then((registration) => {
      console.log('SW registered:', registration.scope);
      // 등록 직후 최신본 확인
      registration.update();
      // 주기적으로 업데이트 확인 (1시간마다)
      if (typeof window !== 'undefined') {
        setInterval(
          () => {
            registration.update();
          },
          60 * 60 * 1000,
        );
      }
    })
    .catch((err) => console.error('SW registration failed:', err));
}

// HMR/중복 바인딩 방지
if (!window.__fcmOnMessageBound) {
  window.__fcmOnMessageBound = true;
  onMessage(messaging, async (payload) => {
    console.log('📩 Foreground FCM 메시지:', payload);

    // notification 페이로드가 있는 경우(브라우저/OS가 자체 처리 가능) 중복 표시 방지 위해 스킵
    if (payload && payload.notification) {
      console.log('Skip foreground OS notification (notification payload present)');
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
    const tag = (payload.data && payload.data.tag) || `${title}-${body}`;

    try {
      if (Notification.permission === 'granted') {
        new Notification(title, { body, icon, tag, renotify: false });
        return;
      }
    } catch (e) {
      console.warn('Window Notification 실패, SW로 폴백:', e);
    }

    if ('serviceWorker' in navigator) {
      const reg = await navigator.serviceWorker.getRegistration();
      if (reg && Notification.permission === 'granted') {
        reg.showNotification(title, { body, icon, tag, renotify: false });
      } else {
        console.warn('알림 권한이 없거나 SW 등록을 찾지 못했습니다.');
      }
    }
  });
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
