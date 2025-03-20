import { Link } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
import logo from '../../logo.png';

export default function HeaderLeft() {
  const location = useLocation();
  const splitedUrlString = location.pathname.split('/');

  // Linux 환경 Firefox에서 flex에 이미지가 들어가면 무조건 크기를 최대로 차지하는 문제가 있어 flex 크기 수동 지정
  const headerListStyle = 'flex flex-4 font-bold';
  const headerItemStyle = 'flex items-center p-4';
  const headerLogoStyle = headerItemStyle + ' max-w-40';
  const headerActiveItemStyle = headerItemStyle + ' text-primary';

  // NavLink로는 구분이 어려운 URL이 있어서 Link 상에서 Active 수동 구현
  return (
    <ul className={headerListStyle}>
      <li className={headerLogoStyle}>
        <Link to="/">
          <img src={logo} />
        </Link>
      </li>

      {splitedUrlString[1] === 'plans' &&
      splitedUrlString[2] &&
      Number.parseInt(splitedUrlString[2]) > 0 ? (
        <>
          <Link
            to={`/${splitedUrlString[1]}/${splitedUrlString[2]}`}
            className={splitedUrlString[3] === 'calendar' ? headerItemStyle : headerActiveItemStyle}
          >
            체크리스트
          </Link>

          <Link
            to={`/${splitedUrlString[1]}/${splitedUrlString[2]}/calendar`}
            className={splitedUrlString[3] === 'calendar' ? headerActiveItemStyle : headerItemStyle}
          >
            캘린더
          </Link>
        </>
      ) : (
        <></>
      )}
    </ul>
  );
}
