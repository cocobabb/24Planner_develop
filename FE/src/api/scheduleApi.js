import api from './axios';

const ENDPOINT = '/plans';

const scheduleApi = {
  createSchedule: async (movingPlanId, formData) => {
    const response = await api.post(`${ENDPOINT}/${movingPlanId}/schedules`, formData);
    return response;
  },

  getMonthlySchedule: async (movingPlanId, month) => {
    const response = await api.get(`${ENDPOINT}/${movingPlanId}/schedules/month?month=${month}`);
    return response;
  },

  getDailySchedule: async (movingPlanId, date) => {
    const response = await api.get(`${ENDPOINT}/${movingPlanId}/schedules/date?date=${date}`);
    return response;
  },

  updateSchedule: async (movingPlanId, scheduleId, formData) => {
    const response = await api.put(`${ENDPOINT}/${movingPlanId}/schedules/${scheduleId}`, formData);
    return response;
  },

  deleteSchedule: async (movingPlanId, scheduleId) => {
    const response = await api.delete(`${ENDPOINT}/${movingPlanId}/schedules/${scheduleId}`);
    return response;
  },
};

export default scheduleApi;
