import { useEffect, useState } from 'react';
import taskGroupApi from '../../api/taskGroupApi';
import { useParams } from 'react-router-dom';

export default function TaskGroupMemo({ memo, setTaskGroupDetails }) {
  // 파라미터
  const { movingPlanId } = useParams();
  const { taskGroupId } = useParams();

  // 상태 관리 데이터
  const [updateMemo, setUpdateMemo] = useState(memo);

  // 체크 그룹 메모 수정 시 화면 렌더링
  useEffect(() => {
    setUpdateMemo(memo);
  }, [memo]);

  // 체크 그룹 메모 입력값
  const handleInputNewMemo = (e) => {
    setUpdateMemo(e.target.value);
  };

  // 체크 그룹 메모 수정
  const handleUpdateMemo = async (e) => {
    // 기존 메모와 변경된 메모가 동일한 경우
    if (updateMemo === memo) {
      return;
    }

    // 체크 그룹 메모가 존재하는 경우
    try {
      const response = await taskGroupApi.updateMemo(movingPlanId, taskGroupId, {
        memo: updateMemo,
      });

      const newMemo = response.data.data.memo;
      setTaskGroupDetails((prev) => ({ ...prev, memo: newMemo }));
    } catch (error) {}
  };

  // CSS
  const memoWrapperStyle = 'w-full';
  const memoStyle =
    'w-full min-h-100 border rounded-3xl border-primary border-2 p-18 px-18 py-13 resize-none focus:outline-none';

  return (
    <section className={memoWrapperStyle}>
      <textarea
        name="memo"
        id="memo"
        placeholder="메모를 입력하세요."
        value={updateMemo}
        className={memoStyle}
        onChange={handleInputNewMemo}
        onBlur={handleUpdateMemo}
        autoFocus
      ></textarea>
    </section>
  );
}
