import api from './axios';

const ENDPOINT = '/plans';

const mapApi = {
  // 집 생성
  mapCreate: async (movingPlanId, formData) => {
    const response = await api.post(`${ENDPOINT}/${movingPlanId}/houses`, formData);
    return response;
  },
  // 집 조회
  maplist: async (movingPlanId) => {
    const response = await api.get(`${ENDPOINT}/${movingPlanId}/houses`);
    return response;
  },
};

export default mapApi;