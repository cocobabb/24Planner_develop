import { Link } from 'react-router-dom';
import logo from '../../logo.png';

export default function HeaderLeft() {
  const headerListStyle = 'flex flex-4 font-bold';
  const headerItemStyle = 'flex items-center p-4';
  const headerLogoStyle = headerItemStyle + ' max-w-40';
  const headerActiveItemStyle = headerItemStyle + ' text-primary';

  // Linux 환경 Firefox에서 flex에 이미지가 들어가면 무조건 크기를 최대로 차지하는 문제가 있어 flex 크기 수동 지정
  // 주석 처리 부분은 로그인 후 화면까지 구현한 시점에서 활성화 예정
  return (
    <ul className={headerListStyle}>
      <li className={headerLogoStyle}>
        <Link to="/">
          <img src={logo} />
        </Link>
      </li>

      {/*
      <li className={headerActiveItemStyle}>체크리스트</li>
      <li className={headerItemStyle}>캘린더</li>
      */}
    </ul>
  );
}
