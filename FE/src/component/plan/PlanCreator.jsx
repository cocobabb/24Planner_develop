import { useEffect, useRef, useState } from 'react';
import planApi from '../../api/planApi';

export default function PlanCreator({ onPlanCreated }) {
  const liRef = useRef(null);

  // 상태 관리 데이터
  const [isCreating, setIsCreating] = useState(false);
  const [newPlan, setNewPlan] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 이사 플랜 생성 상태 토글
  const toggleCreating = () => {
    setIsCreating((prev) => !prev);
    setNewPlan('');
  };

  // 플랜 제목 입력 감지 및 검증
  const handleInput = (e) => {
    const { value } = e.target;

    setNewPlan(value);
  };

  // 이사 플랜 생성 요청
  const createPlan = async () => {
    if (isSubmitting || !newPlan.trim()) return;

    try {
      setIsSubmitting(true);

      const response = await planApi.createPlan(newPlan);
      const data = response.data.data;

      onPlanCreated(data);

      toggleCreating();
      setNewPlan('');
    } catch (error) {
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      createPlan();
    }
  };

  // 외부 클릭 감지
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (isCreating && liRef.current && !liRef.current.contains(e.target)) {
        setIsSubmitting(false);
        toggleCreating();
      }
    };

    document.addEventListener('mousedown', handleClickOutside);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isCreating]);

  // CSS
  const planLi =
    'w-180 h-20 flex items-center justify-center mt-5 border-2 rounded-3xl cursor-pointer';
  const defaultLi = 'border-gray-300';
  const creatingLi = 'border-primary';
  const planText = 'w-full text-center text-3xl text-gray-300 text-opacity-70';
  const createDiv = 'w-full flex items-center justify-center relative';
  const inputStyle = 'w-105 px-2 focus:outline-none text-center text-xl';
  const createButton =
    'absolute right-5 w-15 border-2 border-primary rounded-xl py-1 text-primary cursor-pointer hover:bg-primary hover:text-white';

  return (
    <li
      ref={liRef}
      className={`${planLi} ${isCreating ? creatingLi : defaultLi}`}
      onClick={!isCreating ? toggleCreating : undefined}
    >
      {!isCreating ? (
        <p className={planText}>+</p>
      ) : (
        <div className={createDiv}>
          <div>
            <input
              type="text"
              value={newPlan}
              placeholder="이사 플랜 추가"
              maxLength="20"
              className={inputStyle}
              onChange={handleInput}
              onKeyDown={handleKeyDown}
              required
              autoFocus
            />
            <hr />
          </div>
          <button className={createButton} onClick={createPlan}>
            추가
          </button>
        </div>
      )}
    </li>
  );
}
