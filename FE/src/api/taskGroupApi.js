import api from './axios';

const ENDPOINT = '/plans';

const taskGroupApi = {
  // 체크 그룹 생성
  postTaskGroup: async (movingPlanId, formData) => {
    const response = await api.post(`${ENDPOINT}/${movingPlanId}/taskgroups`, formData);
    return response.data;
  },

  // 체크 그룹 조회
  getTaskGroups: async (movingPlanId) => {
    const response = await api.get(`${ENDPOINT}/${movingPlanId}/taskgroups`);
    return response.data;
  },
  
  // 체크 그룹 제목 수정
  updateTitle: async (movingPlanId, taskGroupId, title) => {
    const response = await api.patch(
      `${ENDPOINT}/${movingPlanId}/taskgroups/${taskGroupId}/title`,
      title,
    );
    return response;
  },

  // 체크 그룹 메모 수정
  updateMemo: async (movingPlanId, taskGroupId, memo) => {
    const response = await api.patch(
      `${ENDPOINT}/${movingPlanId}/taskgroups/${taskGroupId}/memo`,
      memo,
    );
    return response;
  },

  // 체크 그룹 삭제
  deleteTaskGroup: async (movingPlanId, taskGroupId) => {
    const response = await api.delete(`${ENDPOINT}/${movingPlanId}/taskgroups/${taskGroupId}`);
    return response;
  },
};

export default taskGroupApi;