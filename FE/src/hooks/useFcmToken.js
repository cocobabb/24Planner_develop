import { useEffect } from 'react';
import { getToken } from 'firebase/messaging';
import { messaging } from '../firebase'; // firebase 초기화 파일
import { registerFcmToken } from '../api/fcmApi'; // 서버 API 모듈

export default function useFcmToken() {
  useEffect(() => {
    async function fetchToken() {
      try {
        // 1️⃣ 알림 권한 요청
        const permission = await Notification.requestPermission();

        if (permission !== 'granted') {
          console.warn('❌ 알림 권한 거부됨');
          return;
        }

        // 2️⃣ 토큰 발급 (환경 변수에서 안전하게 관리)
        const vapidKey = import.meta.env.VITE_FIREBASE_VAPID_KEY;

        // 서비스워커가 준비되면 해당 registration으로 토큰 발급 (안정성)
        const registration = await navigator.serviceWorker.ready;
        let token = await getToken(messaging, {
          vapidKey,
          serviceWorkerRegistration: registration,
        });
        if (token) {
          console.log('✅ FCM Device Token:', token);
          await registerFcmToken(token);
        }

        // 토큰 갱신 감지
        navigator.serviceWorker.ready.then(async (registration) => {
          const newToken = await getToken(messaging, {
            vapidKey,
            serviceWorkerRegistration: registration,
          });
          if (newToken && newToken !== token) {
            console.log('🔄 FCM Token 갱신 감지:', newToken);
            await registerFcmToken(newToken);
          }
        });
      } catch (err) {
        console.error('❌ FCM 토큰 가져오기 실패:', err);
      }
    }

    fetchToken();
  }, []);
}
