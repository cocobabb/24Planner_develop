import { useEffect } from 'react';
import { getToken } from 'firebase/messaging';
import { messaging } from '../firebase'; // firebase 초기화 파일
import { registerFcmToken } from '../api/fcmApi'; // 서버 등록 API

export default function useFcmToken() {
  useEffect(() => {
    let currentToken = null;

    async function initToken() {
      try {
        // 알림 권한 요청
        const permission = await Notification.requestPermission();
        if (permission !== 'granted') {
          console.warn('알림 권한 거부됨');
          return;
        }

        // 토큰 발급 (서비스워커 준비 후)
        const registration = await navigator.serviceWorker.ready;
        const vapidKey = import.meta.env.VITE_FIREBASE_VAPID_KEY;

        const token = await getToken(messaging, {
          vapidKey,
          serviceWorkerRegistration: registration,
        });

        if (token) {
          currentToken = token;
          console.log('FCM Device Token:', token);
          await registerFcmToken(token); // 서버에 등록
        }
      } catch (err) {
        console.error('FCM 토큰 발급 실패:', err);
      }
    }

    initToken();

    // 토큰 갱신 주기적 확인 (1시간마다)
    const interval = setInterval(async () => {
      if (!currentToken) return;
      try {
        const registration = await navigator.serviceWorker.ready;
        const vapidKey = import.meta.env.VITE_FIREBASE_VAPID_KEY;

        const newToken = await getToken(messaging, {
          vapidKey,
          serviceWorkerRegistration: registration,
        });

        if (newToken && newToken !== currentToken) {
          currentToken = newToken;
          console.log('FCM Token 갱신됨:', newToken);
          await registerFcmToken(newToken);
        }
      } catch (err) {
        console.error('토큰 갱신 확인 실패:', err);
      }
    }, 60 * 60 * 1000); // 1시간마다 체크

    return () => clearInterval(interval);
  }, []);
}
