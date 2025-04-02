import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { login } from '../store/slices/authSlice';

export default function LoginRedirect() {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const handleLoginRedirect = () => {
      const urlParams = new URLSearchParams(window.location.search);
      const accessToken = urlParams.get('code');
      const nickname = urlParams.get('nickname');

      if (!accessToken && !nickname) {
        alert("기존에 회원가입한 방식으로 로그인 후 이용 가능합니다.");
        navigate('/login', { replace: true });
        return;
      }

      dispatch(login({ nickname: nickname, accessToken: accessToken }));
      setTimeout(() => {
        navigate('/plans', { replace: true });
      }, 0);
    };

    handleLoginRedirect();
  }, [navigate, dispatch]);

  return <></>;
}
