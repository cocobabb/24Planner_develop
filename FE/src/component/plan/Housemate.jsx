import { useParams } from 'react-router-dom';
import housemateApi from '../../api/housemateApi';

export default function Housemate({ myHousemateId, housemate, canManage, onHousemateDelete }) {
  const { movingPlanId } = useParams();
  const { id, username, nickname, isOwner } = housemate;

  // 동거인 삭제
  const deleteHousemate = async () => {
    const confirmDelete = window.confirm(
      `${nickname}님이 더 이상 이 이사 플랜에 접근할 수 없게 됩니다.\n정말 삭제하시겠습니까?`,
    );

    if (confirmDelete) {
      try {
        await housemateApi.deleteHousemate(movingPlanId, id);

        onHousemateDelete(id);
      } catch (error) {}
    }
  };

  // CSS
  const housemateLi = 'flex items-center mt-10';
  const ownerCheck = 'w-5';
  const textStyle = 'ml-5 text-xl';
  const usernameStyle = 'text-primary';
  const whoAmIStyle = 'ml-3 text-gray-500';
  const deleteButtonStyle = 'ml-5 text-gray-500 text-opacity-70 cursor-pointer';

  return (
    <li className={housemateLi}>
      <p className={ownerCheck}>{isOwner ? '👑' : '👤'}</p>
      <p className={textStyle}>{nickname}</p>
      <p className={`${textStyle} ${usernameStyle}`}>({username})</p>
      {myHousemateId == id && <p className={whoAmIStyle}>( 나 )</p>}
      {canManage && !isOwner && (
        <button className={deleteButtonStyle} onClick={deleteHousemate}>
          ✕
        </button>
      )}
    </li>
  );
}
