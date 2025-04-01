import api from './axios';

const ENDPOINT = '/plans';

const planApi = {
  // 플랜 생성
  createPlan: async (title) => {
    const response = await api.post(`${ENDPOINT}`, { title });
    return response;
  },

  // 플랜 목록 조회
  readPlans: async () => {
    const response = await api.get(`${ENDPOINT}`);
    return response;
  },

  // 플랜 조회
  readPlan: async (id) => {
    const response = await api.get(`${ENDPOINT}/${id}`);
    return response;
  },

  // 플랜 제목 조회
  readPlanTitle: async (id) => {
    const response = await api.get(`${ENDPOINT}/${id}/title`);
    return response;
  },

  // 플랜 제목 수정
  updatePlanTitle: async (id, title) => {
    const response = await api.patch(`${ENDPOINT}/${id}/title`, { title });
    return response;
  },

  // 플랜 삭제
  deletePlan: async (id) => {
    const response = await api.delete(`${ENDPOINT}/${id}`);
    return response;
  },
};

export default planApi;
