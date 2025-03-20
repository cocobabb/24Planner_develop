import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { logout } from '../../store/slices/authSlice';

export default function HeaderRight() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const splitedUrlString = location.pathname.split('/');

  const handleLogoutClick = () => {
    dispatch(logout());
    navigate('/');
  };

  const headerListStyle = 'flex flex-6 justify-end';
  const headerItemStyle = 'flex items-center p-4';
  const headerDropdownStyle = headerItemStyle + ' relative group';
  const headerDropdownButtonStyle = 'text-secondary cursor-pointer';
  const headerDropdownBodyStyle =
    'absolute text-xl text-center top-15 space-y-4 left-0 right-0 w-full py-4 bg-gray-100 shadow-sm opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300';
  const headerDropdownItemStyle = 'w-full';
  const headerDropdownLinkStyle = 'block w-full';

  // dropdown 구현 참고: https://medium.com/internet-of-technology/in-2-minutes-how-to-make-a-tailwind-css-dropdown-menu-817320bb0678
  return (
    <ul className={headerListStyle}>
      {splitedUrlString[1] === 'plans' &&
      splitedUrlString[2] &&
      Number.parseInt(splitedUrlString[2]) > 0 ? (
        <li className={headerDropdownStyle}>
          <span className={headerDropdownButtonStyle}>이사 플랜 이름</span>
          <ul className={headerDropdownBodyStyle}>
            <li className={headerDropdownItemStyle}>
              <Link to="/plans" className={headerDropdownLinkStyle}>
                이사 목록
              </Link>
            </li>
            <li className={headerDropdownItemStyle}>
              <Link to="/config" className={headerDropdownLinkStyle}>
                이사 설정
              </Link>
            </li>
          </ul>
        </li>
      ) : (
        <></>
      )}

      {splitedUrlString[1] === 'plans' ? (
        <li className={headerItemStyle}>
          <div className="cursor-pointer" onClick={handleLogoutClick}>
            로그아웃
          </div>
        </li>
      ) : (
        <></>
      )}
    </ul>
  );
}
