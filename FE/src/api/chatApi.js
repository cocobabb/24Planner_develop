import api from './axios';

const ENDPOINT = '/chats';

const chatApi = {
  // 채팅 조회
  chatlist: async (movingPlanId, size = 50) => {
    const response = await api.get(`${ENDPOINT}/${movingPlanId}`, {
      params: { size },
    });
    return response;
  },

  // Redis에 사용자가 읽은 마지막 메세지id 저장
  saveLastCursor: async (movingPlanId, messageId) => {
    console.log('saveLastCursor api');
    console.log('saveCursor: ' + messageId);
    const response = await api.post(
      `${ENDPOINT}/${movingPlanId}/lastCursor`,
      {}, // body는 비워두고
      {
        params: { messageId }, // 이게 쿼리 파라미터로 붙음
      },
    );
  },

  previousChatList: async (movingPlanId, messageId) => {
    console.log('previousChatList api');
    console.log('previousChatList: ' + messageId);

    const response = await api.get(`${ENDPOINT}/${movingPlanId}/lastCursor/scroll`, {
      params: { messageId },
    });
    return response;
  },

  // 채팅 삭제
  chatsdelete: async (movingPlanId) => {
    const response = await api.delete(`${ENDPOINT}/${movingPlanId}`);

    return response;
  },
};

export default chatApi;
