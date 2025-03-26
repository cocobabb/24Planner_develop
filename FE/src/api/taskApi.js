import api from './axios';

const ENDPOINT = '/plans';

const taskApi = {
  // 체크포인트 생성
  createTask: async (movingPlanId, taskGroupId, content) => {
    const response = await api.post(`${ENDPOINT}/${movingPlanId}/taskgroups/${taskGroupId}/tasks`, {
      content,
    });
    return response;
  },

  // 체크포인트 리스트 조회 (+ 그룹 메모)
  getTasks: async (movingPlanId, taskGroupId) => {
    const response = await api.get(`${ENDPOINT}/${movingPlanId}/taskgroups/${taskGroupId}/tasks`);
    return response;
  },

  // 체크포인트 내용 수정
  updateTaskContent: async (movingPlanId, taskGroupId, taskId, content) => {
    const response = await api.patch(
      `${ENDPOINT}/${movingPlanId}/taskgroups/${taskGroupId}/tasks/${taskId}/content`,
      content,
    );
    return response;
  },

  // 체크포인트 완료여부 수정
  updateIsTaskCompleted: async (movingPlanId, taskGroupId, taskId, isCompleted) => {
    const response = await api.patch(
      `${ENDPOINT}/${movingPlanId}/taskgroups/${taskGroupId}/tasks/${taskId}/isCompleted`,
      isCompleted,
    );
    return response;
  },

  // 체크포인트 삭제
  deleteTask: async (movingPlanId, taskGroupId, taskId) => {
    const response = await api.delete(
      `${ENDPOINT}/${movingPlanId}/taskgroups/${taskGroupId}/tasks/${taskId}`,
    );
    return response;
  },
};

export default taskApi;
