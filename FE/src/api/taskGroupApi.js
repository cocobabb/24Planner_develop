import api from './axios';

const ENDPOINT = '/plans';

const taskGroupsApi = {
  // taskGroup 생성
  postTaskGroup: async (movingPlanId, formData) => {
    const response = await api.post(`${ENDPOINT}/${movingPlanId}/taskgroups`, formData);
    return response.data;
  },

  // taskGroups 조회
  getTaskGroups: async (movingPlanId) => {
    const response = await api.get(`${ENDPOINT}/${movingPlanId}/taskgroups`);
    return response.data;
  },
};

export default taskGroupsApi;
