import { useParams } from 'react-router-dom';
import housemateApi from '../../api/housemateApi';
import Housemate from './Housemate';
import { useState } from 'react';

export default function HousemateListSection({
  housemates,
  myHousemateId,
  canManage,
  onHousemateDelete,
}) {
  const { movingPlanId } = useParams();

  // 상태 관리 데이터
  const [invitationLink, setInvitationLink] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showPopup, setShowPopup] = useState(false);
  const [isCopied, setIsCopied] = useState(false);

  // 동거인 초대 링크 생성
  const createInvitationLink = async () => {
    if (isLoading) return;

    try {
      setIsLoading(true);

      const response = await housemateApi.createInvitationLink(movingPlanId);
      const data = response.data.data;

      setInvitationLink(data.invitationLink);
      setShowPopup(true);
    } catch (error) {
      const errordata = error.response.data;
      if (errordata.code === 'NOT_FOUND') {
        navigate('/not-found');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const copyToClipboard = () => {
    navigator.clipboard.writeText(invitationLink);
    setIsCopied(true);
    setTimeout(() => setIsCopied(false), 1000);
  };

  const closePopup = () => {
    setShowPopup(false);
  };

  // CSS
  const displayStyle = 'mx-60 mt-15';
  const titleHeader = 'mx-5 flex justify-between';
  const titleStyle = 'text-primary text-xl';
  const titleButtonContainer = 'relative';
  const titleButton = 'text-secondary underline cursor-pointer';
  const listContainer = 'w-180 mt-10 px-20 pt-5 pb-15 border-2 border-primary rounded-3xl';

  // 팝업 CSS
  const popupStyle =
    'absolute right-0 top-6 w-100 p-8 bg-white border-2 border-secondary rounded-2xl z-10';
  const inputStyle = 'w-full px-2 py-1 mr-2 border border-gray-300 rounded-xl';
  const explainstyle = 'ml-2 mt-1 text-sm text-gray-500';
  const copyButtonStyle =
    'w-20 py-2 bg-primary text-sm text-white rounded-xl hover:bg-secondary cursor-pointer';
  const copyDoneStyle = 'w-20 py-2 bg-gray-400 text-sm text-white rounded-xl';
  const closeButtonStyle =
    'absolute top-3 right-4 text-gray-500 hover:text-gray-700 cursor-pointer';

  return (
    <div className={displayStyle}>
      <div className={titleHeader}>
        <h2 className={titleStyle}>이사에 함께 하는 Zipper</h2>
        {canManage && (
          <div className={titleButtonContainer}>
            <button className={titleButton} onClick={createInvitationLink} disabled={showPopup}>
              {isLoading ? '초대 링크 생성 중' : '이사 플랜 초대하기'}
            </button>

            {showPopup && (
              <div className={popupStyle}>
                <p className="mb-2">초대 링크가 생성되었습니다.</p>
                <p className="mb-2">링크를 공유해 함께 할 Zipper을 초대하세요!</p>
                <div className="flex justify-center items-center">
                  <input type="text" value={invitationLink} className={inputStyle} readOnly />
                  <button
                    className={isCopied ? copyDoneStyle : copyButtonStyle}
                    onClick={copyToClipboard}
                  >
                    {isCopied ? '복사됨' : '복사'}
                  </button>
                </div>
                <p className={explainstyle}>✓ 링크는 24시간 동안 유효합니다.</p>
                <button className={closeButtonStyle} onClick={closePopup}>
                  ✕
                </button>
              </div>
            )}
          </div>
        )}
      </div>
      <ul className={listContainer}>
        {housemates.map((housemate) => (
          <Housemate
            key={housemate.id}
            myHousemateId={myHousemateId}
            housemate={housemate}
            canManage={canManage}
            onHousemateDelete={onHousemateDelete}
          />
        ))}
      </ul>
    </div>
  );
}
