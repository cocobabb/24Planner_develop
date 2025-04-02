import api from './axios';

const ENDPOINT = '/auth';

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

  // 비밀번호 찾기
  findPassword: async (formData) => {
    const response = await api.post(`${ENDPOINT}/verify-password`, formData);
    return response.data;
  },

  // 비밀번호 수정
  patchPassword: async (formData) => {
    const response = await api.patch(`${ENDPOINT}/password`, formData);
    return response.data;
  },

  // 로그인
  login: async (formData) => {
    const response = await api.post(`${ENDPOINT}/login`, formData, { withCredentials: true });
    return response;
  },

  // RefreshToken 검증 및 AccessToken 재발급 요청
  reissue: async () => {
    const response = await api.get(`${ENDPOINT}/reissue`, {}, { withCredentials: true });
    return response;
  },

  // 닉네임 조회
  getNickname: async () => {
    const response = await api.get(`${ENDPOINT}/nickname`);
    return response.data;
  },

  // 닉네임 수정
  patchNickname: async (formData) => {
    const response = await api.patch(`${ENDPOINT}/nickname`, formData);
    return response.data;
  },

  // 로그아웃
  logout: async () => {
    const response = await api.delete(`${ENDPOINT}/logout`, {}, { withCredentials: true });
  },

  // 추가 정보 입력 후 회원가입 (소셜로그인)
  completeSignup: async (nickname, tempToken) => {
    const response = await api.post(
      `${ENDPOINT}/signup/additional-info`,
      { nickname, tempToken },
      { withCredentials: true },
    );
    return response;
  },
  
  // redis 값 가져오기
  redis: async (key) => {
    const response = await api.get(`${ENDPOINT}/redis/${key}`);
    return response.data;
  },

};

export default authApi;
