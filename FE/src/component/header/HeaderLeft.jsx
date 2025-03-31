import { Link } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
import logo from '../../logo.png';

export default function HeaderLeft() {
  const location = useLocation();
  const splitedUrlString = location.pathname.split('/');

  const headerLeftList = [];
  if (
    splitedUrlString[1] === 'plans' &&
    splitedUrlString[2] &&
    Number.parseInt(splitedUrlString[2]) > 0
  ) {
    headerLeftList.push({ destinationUrl: '', currentUrl: undefined, text: '체크리스트' });
    headerLeftList.push({ destinationUrl: '/calendar', currentUrl: 'calendar', text: '캘린더' });
    headerLeftList.push({ destinationUrl: '/house', currentUrl: 'house', text: '살 곳 정하기' });
    headerLeftList.push({ destinationUrl: '/chat', currentUrl: 'chat', text: '채팅방' });
  }

  // Linux 환경 Firefox에서 flex에 이미지가 들어가면 무조건 크기를 최대로 차지하는 문제가 있어 flex 크기 수동 지정
  const headerListStyle = 'flex flex-1';
  const headerItemStyle = 'flex items-center p-4';
  const headerLogoStyle = headerItemStyle + ' max-w-40';
  const headerActiveItemStyle = headerItemStyle + ' text-primary';

  // NavLink로는 구분이 어려운 URL이 있어서 Link 상에서 Active 수동 구현
  return (
    <ul className={headerListStyle}>
      <li className={headerLogoStyle}>
        <img src={logo} />
      </li>

      <li className="flex">
        {headerLeftList.length > 0 &&
          headerLeftList.map((element, i) => {
            return (
              <Link
                key={i}
                to={`/${splitedUrlString[1]}/${splitedUrlString[2]}${element.destinationUrl}`}
                className={
                  splitedUrlString[3] === element.currentUrl
                    ? headerActiveItemStyle
                    : headerItemStyle
                }
              >
                {element.text}
              </Link>
            );
          })}
      </li>
    </ul>
  );
}
