import { Link, useNavigate } from 'react-router-dom';

export default function NotFound() {
  const navigate = useNavigate();

  const flexStyle = 'flex justify-center item-center';
  const flexColStyle = 'flex flex-col justify-center item-center';
  const notFoundButtonStyle =
    'w-40 h-20 bg-white border-4 border-primary rounded-3xl text-primary text-xl font-bold cursor-pointer mx-8';

  return (
    <div className={`${flexStyle} h-screen font-roboto`}>
      <div className={flexColStyle}>
        <div className="text-center text-3xl p-4">해당 페이지를 찾을 수 없습니다.</div>
        <div className="text-center text-2xl p-4">페이지 주소를 다시 확인하시기 바랍니다.</div>
        <div className={`${flexStyle} py-12 px-8`}>
          <button className={notFoundButtonStyle} onClick={() => navigate(-1)}>
            뒤로 가기
          </button>
          <Link to="/">
            <button className={notFoundButtonStyle}>메인 페이지로</button>
          </Link>
        </div>
      </div>
    </div>
  );
}
