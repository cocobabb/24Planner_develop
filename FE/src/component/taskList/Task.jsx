import { useParams } from 'react-router-dom';
import taskApi from '../../api/taskApi';
import { useState } from 'react';

export default function Task({ task, setTaskGroupDetails }) {
  const { id, content, isCompleted } = task;

  // 파라미터
  const { movingPlanId } = useParams();
  const { taskGroupId } = useParams();

  // 상태 관리 데이터
  const [isEditing, setIsEditing] = useState(false);
  const [updateContent, setUpdateContent] = useState(content);
  const [updateIsCompleted, setUpdateIsCompleted] = useState(isCompleted);
  const [isError, setIsError] = useState(false);

  // 체크포인트 내용 클릭
  const handleClickContent = () => {
    setIsEditing(!isEditing);
  };

  // 새로운 체크포인트 내용 입력
  const handleInputNewContent = (e) => {
    setUpdateContent(e.target.value);
  };

  // 체크포인트 내용 수정
  const handleUpdateContent = async (e) => {
    // 체크포인트 내용이 존재하지 않는 경우
    if (!e.target.value.trim()) {
      setIsError(true);
      return;
    }

    // 기존 내용과 변경된 내용이 동일한 경우
    if (updateContent === content) {
      setIsEditing(false);
      return;
    }

    // 체크포인트 내용이 존재하는 경우
    try {
      const response = await taskApi.updateTaskContent(movingPlanId, taskGroupId, id, {
        content: updateContent,
      });
      const newContent = response.data.data.content;
      setIsEditing(!isEditing);
      setIsError(false);
      setTaskGroupDetails((prev) => ({
        ...prev,
        tasks: prev.tasks.map((prevTask) => {
          if (prevTask.id === id) {
            return { ...prevTask, content: newContent };
          }
          return prevTask;
        }),
      }));
    } catch (error) {
      console.log(error);
    }
  };

  // 엔터키 눌러 체크포인트 내용 수정
  const handlePressEnter = (e) => {
    if (e.key === 'Enter') {
      handleUpdateContent(e);
    }
  };

  // 체크포인트 완료여부 수정
  const handleUpdateIsCompleted = async () => {
    try {
      const response = await taskApi.updateIsTaskCompleted(movingPlanId, taskGroupId, id, {
        isCompleted: !isCompleted,
      });
      const newIsCompleted = response.data.data.isCompleted;
      setUpdateIsCompleted(newIsCompleted);
      setTaskGroupDetails((prev) => ({
        ...prev,
        tasks: prev.tasks.map((prevTask) => {
          if (prevTask.id === id) {
            return { ...prevTask, isCompleted: newIsCompleted };
          }
          return prevTask;
        }),
        completeCount: isCompleted ? prev.completeCount - 1 : prev.completeCount + 1,
      }));
    } catch (error) {}
  };

  // 체크포인트 삭제
  const handleClickDeleteButton = async () => {
    try {
      await taskApi.deleteTask(movingPlanId, taskGroupId, id);
      setTaskGroupDetails((prev) => ({
        ...prev,
        tasks: prev.tasks.filter((prevTask) => {
          const prevTaskId = prevTask.id;
          return prevTaskId !== id;
        }),
        totalCount: prev.totalCount - 1,
        completeCount: isCompleted ? prev.completeCount - 1 : prev.completeCount,
      }));
    } catch (error) {}
  };

  // CSS
  const taskInfoStyle = 'flex justify-between box-border mb-10';
  const taskStyle = 'flex gap-5 w-full';
  const checkBoxStyle = 'hidden peer';
  const checkBoxLabelStyle =
    'min-w-6 min-h-6 w-6 h-6 flex justify-center items-center rounded-md border-2 border-primary cursor-pointer peer-checked:bg-primary peer-checked:border-primary peer-checked:before:content-["✔"] peer-checked:before:text-white';
  const inputNewContentStyle =
    'w-full focus:outline-none placeholder:text-base placeholder-opacity-70 border-b border-gray-400 border-b break-all border-box mr-5';
  const taskContentStyle = 'break-all mr-5';
  const deleteTaskStyle = 'text-gray-500 text-opacity-70 cursor-pointer';

  return (
    <li className={taskInfoStyle}>
      <div className={taskStyle}>
        {updateIsCompleted ? (
          <input
            type="checkbox"
            id={id}
            className={checkBoxStyle}
            defaultChecked
            value={updateIsCompleted}
            onChange={handleUpdateIsCompleted}
          />
        ) : (
          <input
            type="checkbox"
            id={id}
            className={checkBoxStyle}
            value={updateIsCompleted}
            onChange={handleUpdateIsCompleted}
          />
        )}
        <label htmlFor={id} className={checkBoxLabelStyle}></label>
        {isEditing ? (
          <input
            type="text"
            name="content"
            id="content"
            value={updateContent || ''}
            maxLength={100}
            placeholder={isError ? '내용을 입력해주세요.' : ''}
            className={inputNewContentStyle}
            onChange={handleInputNewContent}
            onBlur={handleUpdateContent}
            onKeyDown={handlePressEnter}
            autoFocus
          />
        ) : (
          <div className={taskContentStyle} onClick={handleClickContent}>
            {content}
          </div>
        )}
      </div>
      <div className={deleteTaskStyle} onClick={handleClickDeleteButton}>
        ✕
      </div>
    </li>
  );
}
