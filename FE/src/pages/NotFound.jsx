import { Link } from 'react-router-dom';

export default function NotFound() {
  const flexStyle = 'flex justify-center item-center';
  const flexColStyle = 'flex flex-col justify-center item-center';
  const errorPageMainDivStyle = flexStyle + ' h-screen font-roboto';
  const textDivStyle = 'text-center p-4';
  const textDivMainStyle = textDivStyle + ' text-3xl';
  const textDivSubStyle = textDivStyle + ' text-2xl';
  const errorPageButtonDivStyle = flexStyle + ' py-12';
  const notFoundButtonStyle =
    'w-40 h-20 bg-white border-4 border-primary rounded-3xl text-primary text-xl font-bold cursor-pointer mx-8';

  return (
    <div className={errorPageMainDivStyle}>
      <div className={flexColStyle}>
        <div className={textDivMainStyle}>해당 페이지를 찾을 수 없습니다.</div>
        <div className={textDivSubStyle}>페이지 주소를 다시 확인하시기 바랍니다.</div>
        <div className={errorPageButtonDivStyle}>
          <Link to="/">
            <button className={notFoundButtonStyle}>메인 페이지로</button>
          </Link>
        </div>
      </div>
    </div>
  );
}
