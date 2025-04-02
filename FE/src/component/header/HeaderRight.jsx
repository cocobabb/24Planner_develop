import { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../../store/slices/authSlice';
import { setCurrentPlanTitle } from '../../store/slices/planForHeaderSlice';
import authApi from '../../api/authApi';
import planApi from '../../api/planApi';

export default function HeaderRight() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const [storedPlanId, setStoredPlanId] = useState(0);

  const currentPlanTitle = useSelector((state) => state.planForHeader.title);
  const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);

  const isPlanTitleLong = currentPlanTitle.length > 16;

  const splitedUrlString = location.pathname.split('/');
  let currentPlanId = splitedUrlString[2];
  currentPlanId = currentPlanId === undefined ? 0 : currentPlanId;

  if (currentPlanId != storedPlanId) {
    setStoredPlanId(currentPlanId);
  }

  useEffect(() => {
    async function setPlanTitle() {
      try {
        const response = await planApi.readPlanTitle(storedPlanId);
        dispatch(setCurrentPlanTitle({ title: response.data.data.title }));
      } catch (error) {
        console.log(error);
        setStoredPlanId(() => 0);
        dispatch(setCurrentPlanTitle({ title: '' }));
      }
    }

    if (storedPlanId) {
      setPlanTitle();
    } else {
      dispatch(setCurrentPlanTitle({ title: '' }));
    }
  }, [storedPlanId]);

  const handleLogoutClick = async () => {
    dispatch(logout());
    await authApi.logout();
    navigate('/');
  };

  const headerListStyle = 'flex flex-1 justify-end items-center';
  const headerItemStyle = 'flex items-center p-4';
  const headerDropdownStyle = headerItemStyle + ' relative group max-h-8 min-w-30';
  const headerDropdownButtonStyle = `w-full text-center text-secondary cursor-pointer ${isPlanTitleLong ? 'text-lg' : ''}`;
  const headerDropdownBodyStyle =
    'absolute text-center top-8 space-y-4 left-0 right-0 w-full py-4 bg-gray-100 shadow-sm opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300';
  const headerDropdownItemStyle = 'w-full';
  const headerDropdownLinkStyle = 'block w-full';

  // dropdown 구현 참고: https://medium.com/internet-of-technology/in-2-minutes-how-to-make-a-tailwind-css-dropdown-menu-817320bb0678
  return (
    <ul className={headerListStyle}>
      {splitedUrlString[1] === 'plans' &&
      splitedUrlString[2] &&
      Number.parseInt(splitedUrlString[2]) > 0 ? (
        <li className={headerDropdownStyle}>
          <div className={headerDropdownButtonStyle}>{currentPlanTitle}</div>
          <ul className={headerDropdownBodyStyle}>
            <li className={headerDropdownItemStyle}>
              <Link to="/plans" className={headerDropdownLinkStyle}>
                이사 목록
              </Link>
            </li>
            <li className={headerDropdownItemStyle}>
              <Link
                to={`/plans/${splitedUrlString[2]}/setting`}
                className={headerDropdownLinkStyle}
              >
                이사 설정
              </Link>
            </li>
          </ul>
        </li>
      ) : (
        <></>
      )}

      {isLoggedIn && (
        <>
          <li className={headerItemStyle}>
            <Link to="/mypage" className="cursor-pointer">
              마이 페이지
            </Link>
          </li>
          <li className={headerItemStyle}>
            <div className="cursor-pointer" onClick={handleLogoutClick}>
              로그아웃
            </div>
          </li>
        </>
      )}
    </ul>
  );
}
