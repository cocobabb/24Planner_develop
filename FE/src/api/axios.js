import axios from "axios";
import authApi from './authApi';
import store from '../store/store';
import { logout } from '../store/slices/authSlice';

const api = axios.create({
  baseURL: '/api',
});

// 재실행한 요청 여부를 판단하기 위한 flag
let flag = false;

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      // accessToken을 Authorization 헤더에 포함시킴
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  // 에러를 거절 상태로 반환하여, 이후 처리를 catch 등의 방식으로 할 수 있게 함
  (error) => Promise.reject(error),
);

// 응답 인터셉터
api.interceptors.response.use(
  // 정상적인 응답을 받은 경우, 그대로 진행
  (response) => response,

  async (error) => {
    // 요청에 문제 발생 시 (에러, Axios 요청의 설정 정보, 응답 중 하나라도 없으면)
    // 토큰 재발급을 요청할 필요가 없음 (네트워크 오류, CORS 차단, Axios 내부 오류 등)
    if (!error.config || !error.response) return Promise.reject(error);

    // 기존 요청 저장
    const originalRequest = error.config;
    // 서버에서 반환한 에러 코드 확인
    const errorCode = error.response?.data?.code;

    // 401 (INVALID_TOKEN) 에러 발생 시 && 재실행한 요청이 아닌 경우
    if (errorCode === 'INVALID_TOKEN' && !flag) {
      // 401 무한 루프 방지를 위해 실행한 요청임을 표시
      flag = true;

      try {
        // accessToken 재발급 요청
        const response = await authApi.reissue();
        const data = response.data;
        const newAccessToken = data.data.accessToken;
 
        // 새로 발급받은 accessToken을 로컬스토리지에 저장
        localStorage.setItem('accessToken', newAccessToken);

        // 실패한 요청 재시도
        return api(originalRequest);

      } catch (error) {

        // 서버에 로그아웃 요청
        await authApi.logout();

        // redux와 localStorage에서 accessToken 삭제
        store.dispatch(logout());

        return Promise.reject(error);

      } finally {
        flag = false;
      }
    }

    return Promise.reject(error);
  },
);

export default api;
