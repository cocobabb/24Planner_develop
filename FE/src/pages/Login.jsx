import { useNavigate } from 'react-router-dom';
import logo from '../logo.png';
import { useEffect, useState } from 'react';
import authApi from '../api/authApi';
import { useDispatch, useSelector } from 'react-redux';
import { login } from '../store/slices/authSlice';

export default function Login() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const accessToken = useSelector((state) => state.auth.accessToken);

  // 상태 관리 데이터
  const [errorMessage, setErrorMessage] = useState('');
  const [inputRequestMessage, setInputRequestMessage] = useState({ username: '', password: '' });
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });

  // 로그인 상태로 로그인 페이지 접속 시 메인으로 이동
  useEffect(() => {
    if (accessToken) {
      navigate('/plans');
      return;
    }
  }, []);

  // 로고 클릭 시 메인 페이지로 이동
  const toHome = () => {
    navigate('/');
  };

  // 로그인 폼 제출 시 api 요청
  const handleSubmitLoginForm = async (e) => {
    e.preventDefault();

    const errors = {};
    if (!formData.username) errors.username = '이메일주소를 입력해주세요.';
    if (!formData.password) errors.password = '비밀번호를 입력해주세요.';

    // 입력값이 비어 있으면 입력 요청 메세지 표시 후 중단
    if (Object.keys(errors).length) {
      setInputRequestMessage(errors);
      return;
    }

    try {
      const response = await authApi.login(formData);
      const data = response.data;

      // 로그인 시 accessToken을 localstorage에 저장
      const { accessToken, nickname } = data.data;
      
      dispatch(login({ accessToken, nickname }));

      navigate('/plans');
    } catch (error) {
      const { code } = error.response.data;

      // 로그인 실패 시 메세지
      if (code === 'INVALID_CREDENTIALS') {
        setErrorMessage(error.response.data.message);
      } else if (error.status === 500) {
        setErrorMessage('로그인에 실패했습니다. 새로고침 후 다시 한 번 로그인을 시도해주세요.');
      }
    }
  };

  // 이메일주소, 비밀번호 입력 시 formData 값 변경
  const handleInput = (e) => {
    const { name, value } = e.target;

    // 입력값 존재 시 inputRequestMessage 초기화
    if (value) {
      setInputRequestMessage((prev) => ({ ...prev, [name]: '' }));
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // 회원가입 버튼 클릭 시 회원가입 페이지로 이동
  const handleClickSignupButton = () => {
    navigate('/signup');
  };

  // CSS
  const displayStyle = 'h-screen flex flex-col justify-center items-center';
  const logoStyle = 'w-64 text-center cursor-pointer';
  const loginWrapperStyle = 'w-full flex flex-col justify-center items-center';
  const loginFormStyle =
    'w-140 h-3/4 flex flex-col justify-between items-center box-border pt-15 pb-3';
  const inputWrapperStyle = 'w-full';
  const inputStyle = 'w-full text-xl pl-3 focus:outline-none focus:placeholder-transparent mt-5';
  const lineStyle = 'mt-3';
  const inputRequestMessageStyle = 'text-red-400 mt-1';
  const loginErrorMessageStyle = 'text-red-400 mb-5';
  const buttonStyle =
    'px-12 py-3 text-2xl text-primary cursor-pointer border-3 border-solid rounded-3xl border-primary hover:bg-primary hover:text-white';
  const signupRequestStyle = 'w-1/3 min-w-100 flex justify-center gap-1 mt-10 pt-2';
  const signupButtonStyle =
    'border-b border-primary cursor-pointer hover:text-primary hover:font-bold ';

  return (
    <div className={displayStyle}>
      <img src={logo} alt="이사모음집 로고" className={logoStyle} onClick={toHome} />
      <div className={loginWrapperStyle}>
        <form onSubmit={(e) => handleSubmitLoginForm(e)} className={loginFormStyle}>
          <div className={inputWrapperStyle}>
            <input
              type="text"
              name="username"
              id="username"
              placeholder="이메일주소"
              value={formData.username}
              onChange={(e) => handleInput(e)}
              className={inputStyle}
            />
            <hr className={lineStyle} />
            <div className={inputRequestMessageStyle}>
              {inputRequestMessage.username || '\u00A0'}
            </div>
          </div>
          <div className={inputWrapperStyle}>
            <input
              type="password"
              name="password"
              id="password"
              placeholder="비밀번호"
              value={formData.password}
              onChange={(e) => handleInput(e)}
              className={inputStyle}
            />
            <hr className={lineStyle} />
            <div className={inputRequestMessageStyle}>
              {inputRequestMessage.password || '\u00A0'}
            </div>
          </div>
          <div className={loginErrorMessageStyle}>{errorMessage || '\u00A0'}</div>
          <button className={buttonStyle}>로그인</button>
        </form>
        <div className={signupRequestStyle}>
          <div>계정이 없으신가요?</div>
          <div className={signupButtonStyle} onClick={handleClickSignupButton}>
            회원가입
          </div>
        </div>
      </div>
    </div>
  );
}
