import './App.css';
import { RouterProvider } from 'react-router-dom';
import router from './router';
import { Provider } from 'react-redux';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import store from './store/store';
import useFcmToken from './hooks/useFcmToken'; // 커스텀 훅 import
import { useEffect } from 'react';

const queryClient = new QueryClient();

export default function App() {
  // 앱 실행 시점에 토큰 발급 & 서버 등록
  useFcmToken();

  // 서비스 워커 등록
  useEffect(() => {
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker
        .register('/firebase-messaging-sw.js', { scope: '/' })
        .then((registration) => console.log('✅ SW 등록 성공:', registration))
        .catch((error) => console.error('❌ SW 등록 실패:', error));
    }
  }, []);

  

  return (
    <>
      <Provider store={store}>
        <QueryClientProvider client={queryClient}>
          <RouterProvider router={router} />
        </QueryClientProvider>
      </Provider>
    </>
  );
}
