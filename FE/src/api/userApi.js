import api from './axios';

const ENDPOINT = '/user';

const userApi = {
  // 비밀번호 수정
  patchPassword: async (formData) => {
    const response = await api.patch(`${ENDPOINT}/password`, formData);
    return response.data;
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

  // redis 값 가져오기
  redis: async (key) => {
    const response = await api.get(`${ENDPOINT}/redis/${key}`);
    return response.data;
  },

  // 회원탈퇴
  deleteUser: async () => {
    const response = await api.delete(`${ENDPOINT}/delete`);
    return response.data;
  },
};

export default userApi;
