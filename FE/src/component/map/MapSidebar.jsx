import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import mapApi from '../../api/mapApi';

export default function MapSidebar({ houseId, maplists, setMapLists, setAddressData, setNickName }) {
  const { movingPlanId } = useParams();

  const [housedetails, setHouseDetails] = useState('');

  const [mapselect, setSelect] = useState(false);

  const [isEditing, setIsEditing] = useState(false);

  const { nickname, address1, address2, content, id } = housedetails;


  const calendarSidebar = 'w-full h-full flex flex-col justify-center flex-1 m-4';
  const houseDelete = 'text-gray-500 text-opacity-70 underline cursor-pointer hover:text-primary';
  const adressStyle =
    'ml-4 text-gray-500 text-opacity-70 underline cursor-pointer hover:text-primary';
  const textareaStyle = 'w-full h-full border-2 rounded-2xl border-black p-8 resize-none focus:outline-none';
  const nicknameupdateStyle =
    'w-40 text-2xl text-black font-bold border-b-[1px] border-black outline-none px-1 inline-block';

  useEffect(() => {
    async function fetchHouseDetail() {
      if (!houseId) return;

      try {
        const response = await mapApi.housedetail(movingPlanId, houseId);

        setHouseDetails(response.data.data);
        setSelect(true);
      } catch (error) {
        console.log(error);
      }
    }
    fetchHouseDetail();
  }, [houseId, id]);

  const housedelete = async (e) => {
    try {
      const { id } = e.target;

      await mapApi.housedelete(movingPlanId, id);
      setMapLists(maplists.filter((maplist) => maplist.id != id));

      setAddressData((prev) => ({
        ...prev,
        centerlatitude: null,
        centerlongitude: null,
      }));
    } catch (err) {
      console.log(err);
    }
  };

  const contentChange = (e) => {
    setHouseDetails((prev) => ({ ...prev, content: e.target.value }));
  };

  const contentUpdate = async (e) => {
    try {
      const { id } = e.target;

      await mapApi.contentupdate(movingPlanId, id, { content });
    } catch (err) {
      console.log(err);
    }
  };

  // 닉네임 변경 핸들러
  const handleNicknameChange = (e) => {
    setHouseDetails((prev) => ({ ...prev, nickname: e.target.value }));
  };

  // 닉네임 업데이트
  const updateNickname = async (e) => {
    try {
      const { id } = e.target;
      await mapApi.nicknameupdate(movingPlanId, id, { nickname });
      setNickName(nickname);
    } catch (err) {
      console.log(err);
    }
    setIsEditing(false);
  };

  return (
    <section className={calendarSidebar}>
      {!mapselect || maplists.length == 0 ? (
        <div className="flex justify-center text-2xl">
          <div>집을 선택하세요</div>
        </div>
      ) : (
        <>
          <div className="flex justify-end mb-8">
            <div className={houseDelete} onClick={housedelete} id={id}>
              집 삭제
            </div>
          </div>
          <div className="ml-6 mb-10">
            <div className="flex items-center mb-2">
              {isEditing ? (
                <input
                  type="text"
                  className={nicknameupdateStyle}
                  id={id}
                  maxLength="5"
                  value={nickname}
                  onChange={handleNicknameChange}
                  onBlur={updateNickname}
                />
              ) : (
                <>
                  <h2 className="text-2xl text-black font-semibold">{nickname}</h2>
                  <div className={adressStyle} onClick={() => setIsEditing(true)}>
                    수정
                  </div>
                </>
              )}
            </div>
            <div>
              <div className="text-xl mb-2">{address1}</div>
              <div className="flex items-center">
                <div className="text-xl">{address2}</div>
                <div className={adressStyle}>수정</div>
              </div>
            </div>
          </div>
          <textarea
            className={textareaStyle}
            value={content || ""}
            id={id}
            onChange={contentChange}
            onBlur={contentUpdate}
          ></textarea>
        </>
      )}
    </section>
  );
}
