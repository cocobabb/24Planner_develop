import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import planApi from '../api/planApi';

export default function PlanSetting() {
  const { movingPlanId } = useParams();

  const navigate = useNavigate();

  // 상태 관리 데이터
  const [title, setTitle] = useState('');
  const [titleInput, setTitleInput] = useState('');
  const [isTitleEditing, setIsTitleEditing] = useState(false);

  // 이사 플랜 조회
  useEffect(() => {
    async function fetchPlan() {
      try {
        const response = await planApi.readPlan(movingPlanId);
        const data = response.data.data;

        setTitle(data.title);
      } catch (error) {
        const errordata = error.response.data;
        if (errordata.code === 'NOT_FOUND') {
          navigate('/not-found');
        }
      }
    }
    fetchPlan();
  }, []);

  // 플랜 제목 수정 상태 설정
  const handleTitleEditing = () => {
    setIsTitleEditing(true);
    setTitleInput(title);
  };

  // 플랜 제목 입력 감지 및 검증
  const handleTitleInput = (e) => {
    const { value } = e.target;

    setTitleInput(value);
  };

  // 이사 플랜 제목 수정 요청
  const updateTitle = async () => {
    if (!titleInput.trim()) {
      return;
    }

    // 기존 제목과 다른 경우에만 api 요청
    if (titleInput !== title) {
      try {
        const response = await planApi.updatePlan(movingPlanId, titleInput);
        const data = response.data.data;

        setTitle(data.title);
      } catch (error) {}
    }

    setIsTitleEditing(false);
  };

  const handleTitleKeyDown = (e) => {
    if (e.key === 'Enter') {
      updateTitle();
    }
  };

  // 이사 플랜 삭제
  const deletePlan = async () => {
    const confirmDelete = window.confirm(
      '이 이사 플랜과 관련된 모든 데이터가 삭제됩니다.\n정말 삭제하시겠습니까?',
    );

    if (confirmDelete) {
      try {
        await planApi.deletePlan(movingPlanId);
        navigate('/plans');
      } catch (error) {}
    }
  };

  // CSS
  const displayStyle = 'w-300 mx-auto my-5';
  const titleHeader = 'h-full mx-60 flex justify-between';
  const titleDiv = 'flex';
  const titleStyle = 'text-2xl mr-5';
  const titleButton =
    'text-xl text-gray-500 text-opacity-70 underline cursor-pointer hover:text-primary';
  const titleInputStyle = 'text-gray-700 text-2xl border-b outline-none';

  return (
    <div className={displayStyle}>
      <div className={titleHeader}>
        <div className={titleDiv}>
          {!isTitleEditing ? (
            <>
              <h2 className={titleStyle}>{title}</h2>
              <button className={titleButton} onClick={handleTitleEditing}>
                수정
              </button>
            </>
          ) : (
            <>
              <input
                type="text"
                value={titleInput}
                placeholder="제목을 입력해주세요."
                className={titleInputStyle}
                onChange={handleTitleInput}
                onKeyDown={handleTitleKeyDown}
                onBlur={updateTitle}
                required
                autoFocus
              />
            </>
          )}
        </div>
        <button className={titleButton} onClick={deletePlan}>
          이사 플랜 삭제
        </button>
      </div>
    </div>
  );
}
