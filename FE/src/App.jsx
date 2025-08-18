import './App.css';
import { RouterProvider } from 'react-router-dom';
import router from './router';
import { Provider } from 'react-redux';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import store from './store/store';
import useFcmToken from './hooks/useFcmToken'; // 커스텀 훅 import

const queryClient = new QueryClient();

export default function App() {
  // 🔹 앱 실행 시점에 토큰 발급 & 서버 등록
  useFcmToken();

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
