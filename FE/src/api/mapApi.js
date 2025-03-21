import api from './axios';

const ENDPOINT = '/plans';

const mapApi = {

    // 집 생성
    mapCreate: async (movingPlanId, formData) => {
      const response = await api.post(`${ENDPOINT}/${movingPlanId}/house`, formData);
      return response;
    },
};

export default mapApi;