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

  // 집 상세내용 조회
  housedetail: async (movingPlanId, houseId) => {
    const response = await api.get(`${ENDPOINT}/${movingPlanId}/houses/${houseId}`);
    return response;
  },

  // 집 삭제
  housedelete: async (movingPlanId, houseId) => {
    const response = await api.delete(`${ENDPOINT}/${movingPlanId}/houses/${houseId}`);
    return response;
  },

  // 집 상세 내용 수정
  contentupdate: async (movingPlanId, houseId, detailcontent) => {
    const response = await api.patch(
      `${ENDPOINT}/${movingPlanId}/houses/${houseId}/content`,
      detailcontent,
    );
    return response;
  },

   // 집 별칭 수정
  nicknameupdate: async (movingPlanId, houseId, nicknamecontent) => {

    const response = await api.patch(
      `${ENDPOINT}/${movingPlanId}/houses/${houseId}/nickname`,
      nicknamecontent,
    );
    return response;
  },
};


export default mapApi;