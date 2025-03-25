import { useNavigate } from 'react-router-dom';

export default function Plan({ plan }) {
  const navigate = useNavigate();

  const { id, title } = plan;

  // CSS
  const planLi =
    'w-full h-20 flex items-center justify-center mb-10 py-5 border-2 border-primary rounded-3xl relative cursor-pointer';
  const planText = 'w-full text-center text-2xl ';
  const settingLink = 'absolute right-5 text-xl cursor-pointer';

  return (
    <li className={planLi} onClick={() => navigate(`/plans/${id}`)}>
      <p className={planText}>{title}</p>
      <button
        className={settingLink}
        onClick={(e) => {
          e.stopPropagation();
          navigate(`/plans/${id}/setting`);
        }}
      >
        ⚙️
      </button>
    </li>
  );
}
