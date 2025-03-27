import { useEffect, useState } from 'react';
import Task from './Task';
import taskApi from '../../api/taskApi';
import { useParams } from 'react-router-dom';

export default function TaskListSection({ taskGroupDetails, setTaskGroupDetails }) {
  const { totalCount, completeCount, tasks } = taskGroupDetails;

  // 파라미터
  const { movingPlanId } = useParams();
  const { taskGroupId } = useParams();

  // 상태 관리 데이터
  const [newContent, setNewContent] = useState('');
  const [isEditing, setIsEditing] = useState(false);

  // 체크리스트 변경 시 화면 렌더링
  useEffect(() => {
    setTaskGroupDetails((prev) => ({ ...prev, tasks: tasks }));
  }, [tasks, totalCount, completeCount]);

  // 체크포인트 추가하기 클릭
  const handleClickCreateButton = (e) => {
    setIsEditing(true);
  };

  // 체크포인트 내용 입력
  const handleInputNewContent = (e) => {
    setNewContent(e.target.value);
  };

  // 체크포인트 생성
  const handleCreateTask = async (e) => {
    // 체크포인트 내용이 존재하지 않는 경우
    if (!e.target.value.trim()) {
      setIsEditing(false);
      return;                                                                  
    }

    try {
      const response = await taskApi.createTask(movingPlanId, taskGroupId, newContent);
      const task = response.data.data;

      setNewContent('');
      setTaskGroupDetails((prev) => ({
        ...prev,
        tasks: [...prev.tasks, task],
        totalCount: totalCount + 1,
      }));
      setIsEditing(false);
    } catch (error) {}
  };

  // 엔터키 눌러 체크포인트 생성
  const handlePressEnter = (e) => {
    if (e.key === 'Enter') {
      handleCreateTask(e);
    }
  };

  // CSS
  const taskWrapperStyle = 'w-full';
  const taskCountingStyle = 'text-right text-xl text-primary mb-2 pr-7';
  const taskListStyle =
    'border rounded-3xl border-primary border-2 mb-10 px-18 py-12 h-120 overflow-y-auto';
  const taskStyle = 'flex items-center gap-5';
  const checkBoxStyle = 'hidden peer';
  const checkBoxLabelStyle =
    'min-w-6 min-h-6 w-6 h-6 flex items-center justify-center rounded-md border-2 border-gray-400 cursor-pointer';
  const taskContentStyle = 'text-gray-400';
  const inputNewContentStyle =
    'focus:outline-none placeholder:text-base placeholder-opacity-70 border-b border-gray-400 w-full break-all';

  return (
    <section className={taskWrapperStyle}>
      <div className={taskCountingStyle}>
        {completeCount} / {totalCount}
      </div>
      <ul className={taskListStyle}>
        {tasks?.map((task) => {
          return <Task key={task.id} task={task} setTaskGroupDetails={setTaskGroupDetails}></Task>;
        })}
        <li className={taskStyle}>
          <input type="checkbox" id="createButton" className={checkBoxStyle} />
          <label htmlFor="createButton" className={checkBoxLabelStyle}></label>
          {isEditing ? (
            <input
              type="text"
              name="content"
              id="content"
              value={newContent || ''}
              maxLength={1000}
              placeholder="내용을 입력해주세요."
              className={inputNewContentStyle}
              onChange={handleInputNewContent}
              onBlur={handleCreateTask}
              onKeyDown={handlePressEnter}
              autoFocus
            />
          ) : (
            <>
              <div className={taskContentStyle} onClick={handleClickCreateButton}>
                체크포인트 추가하기
              </div>
            </>
          )}
        </li>
      </ul>
    </section>
  );
}
