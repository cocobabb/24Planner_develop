// api/fcmApi.ts
import axios from './axios';

// 서버에 FCM 토큰 저장
export async function registerFcmToken(deviceToken) {
  return axios.post('/fcm/createToken', { deviceToken });
}
