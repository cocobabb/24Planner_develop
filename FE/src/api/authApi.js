import api from "./axios";

const ENDPOINT = "/auth";

const authApi = {
  // 이메일 인증 요청
  verifyEmail: async (username) => {
    const response = await api.post(`${ENDPOINT}/verify-email`, { username });
    return response;
  },

  // 이메일 인증번호 확인
  verifyEmailCode: async (username, code) => {
    const response = await api.post(`${ENDPOINT}/verify-email-code`, { username, code });
    return response;
  },

  // 닉네임 중복 확인
  verifyNickname: async (nickname) => {
    const response = await api.get(`${ENDPOINT}/verify-nickname`, { params: { nickname } });
    return response;
  },

  // 회원가입
  signup: async (signupData) => {
    const response = await api.post(`${ENDPOINT}/signup`, signupData);
    return response;
  },
};

export default authApi;