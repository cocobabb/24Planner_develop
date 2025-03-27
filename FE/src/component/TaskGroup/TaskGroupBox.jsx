import { useRef, useState } from 'react';
import taskGroupApi from '../../api/taskGroupApi';
import { useNavigate, useParams } from 'react-router-dom';

export default function TaskGroupBox({ taskGroups, setTaskGroups }) {
  const { movingPlanId } = useParams();

  const [clickAdd, setClickAdd] = useState(false);
  const [message, setMessage] = useState();
  const [formData, setFormData] = useState({
    title: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const navigate = useNavigate();

  const saveinputValues = (e) => {
    setMessage();
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const postTaskGroup = async (e) => {
    e.preventDefault();

    if (isSubmitting) return;
    setIsSubmitting(true);

    try {
      const response = await taskGroupApi.postTaskGroup(movingPlanId, formData);
      const code = response.code;
      const id = response.data.id;
      if (code === 'CREATED') {
        setClickAdd(false);
        setMessage();
        const add = { id, title: formData.title, progress: 0 };
        setTaskGroups((prev) => [...prev, add]);
        setFormData({});
      }
    } catch (error) {
      const errorData = error.response.data;
      const code = errorData.code;
      const message = errorData.message;

      if (code === 'BAD_REQUEST') {
        setMessage(message);
      } else if (code === 'INVALID_TOKEN') {
        setMessage('작성 권한이 없습니다. 로그인 후 다시 이용하세요');
      } else if (code === 'NOT_FOUND') {
        navigate('/not-found');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const section = 'm-10 grid grid-cols-2 gap-14';
  const groupBox =
    'w-100 h-35 border-2  rounded-3xl px-2 py-5 bg-white font-roboto flex flex-col items-center justify-center hover:cursor-pointer';
  const boxText = 'text-lg font-roboto m-3';
  const progress = 'w-90 h-7 border-2 rounded-full border-primary relative';
  const progressPercent =
    'h-full bg-primary border-primary border-1 rounded-full absolute -left-px';
  const addBox =
    'w-100 h-35 border-2 border-gray-300 rounded-3xl px-2 py-5 flex items-center justify-center';
  const addBoxText = 'text-gray-300 text-2xl font-roboto ';
  const changeAddBox =
    'w-100 h-35 border-2 rounded-3xl px-2 py-5 flex flex-col items-center justify-center relative';
  const form = 'mt-8';
  const addBtn =
    'w-20 border-2 rounded-xl m-3 px-2 py-1 border-primary text-primary hover:bg-primary hover:text-white cursor-pointer';
  const inputText =
    'w-50 m-3 border-2 border-b-gray-300 border-x-white border-t-white placeholder:text-gray-400 focus:outline-none';
  const messageStyle = 'font-semibold text-red-400';
  const X = 'text-gray-500 text-opacity-70 cursor-pointer absolute bottom-25 left-90';

  return (
    <section className={`${section}`}>
      {taskGroups.map((task) => (
        <div
          key={task.id}
          className={`${groupBox}`}
          onClick={() => navigate(`/plans/${movingPlanId}/task/${task.id}`)}
        >
          <span className={`${boxText}`}>{task.title}</span>
          <div className={`${progress}`}>
            {task.progress ? (
              <div
                className={`${progressPercent}`}
                style={{
                  width: 2 + 0.985 * task.progress + '%',
                }}
              ></div>
            ) : (
              <div></div>
            )}
          </div>
        </div>
      ))}

      {clickAdd ? (
        <div className={`${changeAddBox}`}>
          <button
            className={`${X}`}
            onClick={() => {
              setMessage();
              setClickAdd(false);
            }}
          >
            ✕
          </button>
          <form className={`${form}`}>
            <input
              name="title"
              className={`${inputText}`}
              onChange={saveinputValues}
              type="text"
              placeholder="체크 그룹 추가"
              required
            />
            <button className={`${addBtn}`} onClick={postTaskGroup}>
              추가
            </button>
          </form>
          {message ? <span className={`${messageStyle}`}>{message}</span> : <div>&nbsp;</div>}
        </div>
      ) : (
        <div
          className={`${addBox}`}
          onClick={(click) => {
            setClickAdd(true);
            setMessage();
          }}
        >
          <span className={`${addBoxText}`}>+</span>
        </div>
      )}
    </section>
  );
}
