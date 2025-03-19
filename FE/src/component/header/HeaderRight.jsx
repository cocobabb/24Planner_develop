import { Link } from 'react-router-dom';

export default function HeaderRight() {
  const headerListStyle = 'flex flex-6 justify-end';
  const headerItemStyle = 'flex items-center p-4';
  const headerDropdownStyle = headerItemStyle + ' relative group';
  const headerDropdownButtonStyle = 'text-secondary cursor-pointer';
  const headerDropdownBodyStyle =
    'absolute text-xl text-center px-6 top-15 space-y-4 left-0 right-0 w-full py-4 bg-gray-100 shadow-sm opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300';

  // 주석 처리 부분은 로그인 후 화면까지 구현한 시점에서 활성화 예정
  // dropdown 구현 참고: https://medium.com/internet-of-technology/in-2-minutes-how-to-make-a-tailwind-css-dropdown-menu-817320bb0678
  return (
    <ul className={headerListStyle}>
      {/*
      <li className={headerDropdownStyle}>
        <span className={headerDropdownButtonStyle}>이사 플랜 1</span>
        <ul className={headerDropdownBodyStyle}>
          <li>
            <Link to="/plans">이사 목록</Link>
          </li>
          <li>
            <Link to="/config">이사 설정</Link>
          </li>
        </ul>
      </li>
      <li className={headerItemStyle}>로그아웃</li>
      */}
    </ul>
  );
}
