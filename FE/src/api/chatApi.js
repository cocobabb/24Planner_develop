import api from './axios';

const ENDPOINT = '/chats';

const chatApi = {

  // 채팅 조회
  chatlist: async (movingPlanId) => {
    const response = await api.get(
      `${ENDPOINT}/${movingPlanId}`
    );

    return response;
  },

   // 채팅 삭제
   chatsdelete: async (movingPlanId) => {
    const response = await api.delete(
      `${ENDPOINT}/${movingPlanId}`
    );

    return response;
  },
};


export default chatApi;