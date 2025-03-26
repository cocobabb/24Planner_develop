import { useEffect, useState } from 'react';
import taskGroupApi from '../../api/taskGroupApi';
import { useNavigate, useParams } from 'react-router-dom';

export default function TaskGroupInfo({ title, setTaskGroupDetails }) {
  const navigate = useNavigate();

  // 파라미터
  const { movingPlanId } = useParams();
  const { taskGroupId } = useParams();

  // 상태 관리 데이터
  const [isEditing, setIsEditing] = useState(false);
  const [updateTitle, setUpdateTitle] = useState(title);
  const [isError, setIsError] = useState(false);

  // 체크 그룹 제목 수정 시 화면 렌더링
  useEffect(() => {
    setUpdateTitle(title);
  }, [title]);

  // 수정 버튼 클릭
  const handleClickUpdateButton = () => {
    setIsEditing(!isEditing);
  };

  // 새로운 체크 그룹 제목 입력
  const handleInputNewTitle = (e) => {
    setUpdateTitle(e.target.value);
  };

  // 체크 그룹 제목 수정
  const handleUpdateTitle = async (e) => {
    // 체크 그룹 제목이 존재하지 않는 경우
    if (!e.target.value.trim()) {
      setIsError(true);
      return;
    }

    // 기존 제목과 변경된 제목이 동일한 경우
    if (updateTitle === title) {
      setIsEditing(false);
      return;
    }

    // 체크 그룹 제목이 존재하는 경우
    try {
      const response = await taskGroupApi.updateTitle(movingPlanId, taskGroupId, {
        title: updateTitle,
      });
      const newTitle = response.data.data.title;
      setIsEditing(!isEditing);
      setIsError(false);
      setTaskGroupDetails((prev) => ({ ...prev, title: newTitle }));
    } catch (error) {}
  };

  // 엔터키 눌러 체크 그룹 제목 수정
  const handlePressEnter = (e) => {
    if (e.key === 'Enter') {
      handleUpdateTitle(e);
    }
  };

  // 체크 그룹 삭제
  const handleClickDeleteButton = async () => {
    try {
      await taskGroupApi.deleteTaskGroup(movingPlanId, taskGroupId);
      alert('체크 그룹이 삭제되었습니다.');
      navigate(`/plans/${movingPlanId}`);
    } catch (error) {}
  };

  // CSS
  const taskgroupInfoWrapperStyle = 'w-full flex justify-between items-center mb-4';
  const taskgroupInfoStyle = 'flex gap-5 items-center w-full';
  const taskgroupTitleStyle = 'text-xl';
  const buttonStyle =
    'w-1/6 text-gray-500 text-opacity-70 underline cursor-pointer hover:text-primary';
  const inputNewTitleStyle =
    'w-3/5 text-xl focus:outline-none placeholder:text-base placeholder-opacity-70 border-b border-gray-400';

  return (
    <>
      <section className={taskgroupInfoWrapperStyle}>
        <div className={taskgroupInfoStyle}>
          {isEditing ? (
            <input
              type="text"
              name="title"
              id="title"
              value={updateTitle || ''}
              maxLength={18}
              placeholder={isError ? '제목을 입력해주세요.' : ''}
              className={inputNewTitleStyle}
              onChange={handleInputNewTitle}
              onBlur={handleUpdateTitle}
              onKeyDown={handlePressEnter}
              autoFocus
            />
          ) : (
            <>
              <h1 className={taskgroupTitleStyle}>{title}</h1>
              <div className={buttonStyle} onClick={handleClickUpdateButton}>
                수정
              </div>
            </>
          )}
        </div>
        <div className={buttonStyle} onClick={handleClickDeleteButton}>
          체크 그룹 삭제
        </div>
      </section>
    </>
  );
}
