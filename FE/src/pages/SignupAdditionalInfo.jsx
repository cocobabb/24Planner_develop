import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import authApi from '../api/authApi';
import { useDispatch, useSelector } from 'react-redux';
import { login } from '../store/slices/authSlice';
import logo from '../logo.png';

export default function SignupAdditionalInfo() {
  const location = useLocation();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);

  // 상태 관리 데이터
  const [tempToken, setTempToken] = useState('');
  const [nickname, setNickname] = useState('');
  const [isSignupAvailable, setIsSignupAvailable] = useState(false);
  const [message, setMessage] = useState({
    color: '',
    content: '',
  });

  // 로그인 상태인 경우 이사 플랜 목록으로 이동
  useEffect(() => {
    if (isLoggedIn) {
      navigate('/plans');
      return;
    }
  }, [isLoggedIn]);

  // 로고 클릭 시 메인 페이지로 이동
  const toHome = () => {
    navigate('/');
  };

  // 입력값 검증
  const checkNickname = (value) => /^[가-힣a-zA-Z0-9]{2,17}$/.test(value);

  // 쿼리 파라미터에 있는 임시 토큰 가져오기
  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const token = urlParams.get('code');
    setTempToken(token);
  }, [location]);

  // 회원가입
  const handleSubmit = async (e) => {
    try {
      const response = await authApi.completeSignup(nickname, tempToken);
      const data = response.data.data;

      dispatch(login({ nickname: data.nickname, accessToken: data.accessToken }));
      alert(response.data.message);
      navigate('/plans', { replace: true });
    } catch (e) {
      alert('회원가입에 실패했습니다. 다시 로그인해주세요.');
      navigate('/login', { replace: true });
    }
  };

  // 닉네임 입력
  const handleInputNickname = (e) => {
    const value = e.target.value;

    if (!value) {
      setIsSignupAvailable(false);
      setMessage({ color: '', content: '' });
      setNickname('');
      return;
    }

    setNickname(e.target.value);
  };

  // 닉네임 유효성 검증
  const verifyNickname = async () => {
    if (!checkNickname(nickname)) {
      setMessage({ color: 'red-400', content: '특수문자 제외, 2자 이상 17자 이하여야 합니다.' });
      return;
    }

    try {
      const response = await authApi.verifyNickname(nickname);
      const data = response.data;

      setMessage({ color: 'primary', content: data.message });
      setIsSignupAvailable(true);
    } catch (error) {
      const errordata = error.response.data;

      if (errordata.code === 'EXIST_NICKNAME') {
        setMessage({ color: 'red-400', content: errordata.message });
      } else {
        setMessage({ color: 'red-400', content: '닉네임 중복 확인에 실패했습니다.' });
      }

      setIsSignupAvailable(false);
    }
  };

  // 엔터키 눌러 닉네임 유효성 검증
  const handlePressEnter = (e) => {
    if (e.key === 'Enter') {
      verifyNickname();
    }
  };

  // CSS
  const signupWrapperStyle = 'h-screen flex flex-col justify-center items-center gap-3';
  const logoStyle = 'w-55 text-center cursor-pointer mb-10';
  const inputButtonDiv = 'w-full max-w-90 flex justify-center';
  const inputStyle = 'w-2/3 mx-1 px-2 focus:outline-none text-xl border-b border-gray-400';
  const able = 'border-primary text-primary hover:bg-primary hover:text-white cursor-pointer';
  const disable = 'border-gray-300 text-gray-300 hover:none';
  const buttonStyle = 'w-1/4 h-10 border-2 rounded-full px-2 py-1' + able;
  const signupButton = 'block mx-auto border-2 rounded-2xl px-8 py-2 text-xl';
  const messageStyle = 'font-semibold';

  return (
    <div className={signupWrapperStyle}>
      <img src={logo} alt="로고" className={logoStyle} onClick={toHome} />
      <div className={inputButtonDiv}>
        <input
          type="text"
          id="nickname"
          name="nickname"
          value={nickname}
          placeholder="닉네임"
          className={inputStyle}
          onChange={handleInputNickname}
          onKeyDown={handlePressEnter}
          required
        />
        <button
          className={buttonStyle}
          onMouseDown={(e) => e.preventDefault()}
          onClick={verifyNickname}
        >
          중복확인
        </button>
      </div>
      <div className={`${messageStyle} text-${message.color}`}>{message.content || '\u00A0'}</div>
      <button
        className={`${signupButton} ${isSignupAvailable ? able : disable}`}
        disabled={!isSignupAvailable}
        onClick={handleSubmit}
      >
        회원가입
      </button>
    </div>
  );
}
