import { useEffect, useState } from 'react';
import logo from '../../public/logo.png';
import { useNavigate } from 'react-router-dom';
import authApi from '../api/authApi';

export default function Signup() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: '',
    code: '',
    nickname: '',
    password: '',
  });
  const [verifyPassword, setVerifyPassword] = useState('');
  const [validation, setValidation] = useState({
    username: { isValid: false },
    nickname: { isValid: false },
    password: { isValid: false, isEqual: false },
  });
  const [expiredAt, SetExpiredAt] = useState(null);
  const [remainingTime, setRemainingTime] = useState('3:00');
  const [usernameState, setUsernameState] = useState({
    isVerifying: false,
    username: '',
  });
  const [usernameMessage, setUsernameMessage] = useState({
    color: '',
    content: '',
  });
  const [codeMessage, setCodeMessage] = useState({
    color: '',
    content: '',
  });
  const [nicknameMessage, setNicknameMessage] = useState({
    color: '',
    content: '',
  });
  const [passwordMessage, setPasswordMessage] = useState('');

  // 이메일, 코드, 닉네임, 비밀번호 입력값 검증
  const checkUsername = (value) => /\S+@\S+\.\S+/.test(value);
  const checkFourDigit = (value) => /^\d{4}$/.test(value);
  const checkNickname = (value) => /^[가-힣a-zA-Z0-9]{2,17}$/.test(value);
  const checkPassword = (value) => /^(?=.*[A-Za-z])(?=.*\d)(?=.*[#?!]).{8,}$/.test(value);
  const checkMinLength = (value) => value.length >= 8;
  const checkLetter = (value) => /[a-zA-Z]/.test(value);
  const checkNumber = (value) => /[0-9]/.test(value);
  const checkSpecialChar = (value) => /[#?!]/.test(value);
  const checkInvalidChar = (value) => /[^A-Za-z0-9#?!]/.test(value);

  // 회원가입 필수값 검증 확인
  const isSignupAvailable =
    validation.username.isValid &&
    validation.nickname.isValid &&
    validation.password.isValid &&
    validation.password.isEqual;

  // 로고 클릭 시 메인페이지로 이동
  const toHome = () => {
    navigate('/');
  };

  // 회원가입 폼 입력 감지 및 검증
  const handleFormInput = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    if (name === 'username') {
      setUsernameMessage({ color: '', content: '' });
    } else if (name === 'nickname') {
      setValidation((prev) => ({
        ...prev,
        nickname: { isValid: false },
      }));

      if (!value || checkNickname(value)) {
        setNicknameMessage({ color: '', content: '' });
      } else {
        setNicknameMessage({
          color: 'red-400',
          content: '특수문자 제외, 2자 이상 17자 이하여야 합니다.',
        });
      }
    } else if (name === 'password') {
      setValidation((prev) => ({
        ...prev,
        password: { ...prev.password, isValid: checkPassword(value) },
      }));
    }
  };

  // 비밀번호 확인 입력 감지 및 검증
  const handleVerifyPassword = (e) => {
    const { value } = e.target;

    setVerifyPassword(value);

    if (!value || value === formData.password) {
      setPasswordMessage('');
    } else if (value != formData.password) {
      setPasswordMessage('비밀번호가 일치하지 않습니다.');
    }

    setValidation((prev) => ({
      ...prev,
      password: { ...prev.password, isEqual: value === formData.password },
    }));
  };

  // 이메일 발송 요청
  const verifyEmail = async () => {
    setCodeMessage({ color: '', content: '' });

    if (!formData.username) return;

    if (!checkUsername(formData.username)) {
      setUsernameMessage({ color: 'red-400', content: '유효한 이메일 주소를 입력해주세요.' });
      return;
    }

    if (usernameState.isVerifying) return;

    try {
      const response = await authApi.verifyEmail(formData.username);
      const expiredAt = response.data.data.expiredAt;

      setUsernameState({
        isVerifying: true,
        username: formData.username,
      });
      SetExpiredAt(expiredAt);
      setUsernameMessage({
        color: 'primary',
        content: '인증번호가 전송되었습니다. 메일함을 확인해주세요.',
      });
    } catch (error) {
      const errordata = error.response.data;

      if (errordata.code === 'EXIST_EMAIL') {
        setUsernameMessage({ color: 'red-400', content: errordata.message });
      } else {
        setUsernameMessage({ color: 'red-400', content: '인증 코드 발송에 실패했습니다.' });
      }
    }
  };

  // 인증번호 만료까지 남은 시간 계산
  const calculateRemainingTime = (expiredAt) => {
    const now = new Date();
    const expireTime = new Date(expiredAt);

    const remainingMs = expireTime - now;

    if (remainingMs <= 0) return '0:00';

    const minutes = Math.floor(remainingMs / (1000 * 60));
    const seconds = Math.floor((remainingMs % (1000 * 60)) / 1000);

    const formattedSeconds = String(seconds).padStart(2, '0');

    return `${minutes}:${formattedSeconds}`;
  };

  // 인증번호 만료까지 남은 시간 표시
  useEffect(() => {
    if (!expiredAt) return;

    let interval;

    const updateRemainingTime = () => {
      const remainingTime = calculateRemainingTime(expiredAt);
      setRemainingTime(remainingTime);

      if (validation.username.isValid) {
        clearInterval(interval);
        setRemainingTime('');
        return;
      }

      if (remainingTime === '0:00') {
        setUsernameState((prev) => ({
          ...prev,
          isVerifying: false,
        }));
        setCodeMessage({
          color: 'red-400',
          content: '인증 시간이 만료되었습니다. 다시 인증해주세요.',
        });
        clearInterval(interval);
      }
    };

    updateRemainingTime();

    interval = setInterval(updateRemainingTime, 1000);

    return () => clearInterval(interval);
  }, [expiredAt, validation.username.isValid]);

  // 인증번호 발송 후 이메일 입력값 변경 감지
  useEffect(() => {
    if (usernameState.isVerifying && formData.username !== usernameState.username) {
      setUsernameState((prev) => ({
        ...prev,
        isVerifying: false,
      }));
      setFormData((prev) => ({
        ...prev,
        code: '',
      }));
      SetExpiredAt(null);
      setRemainingTime('3:00');
      setCodeMessage({
        color: 'red-400',
        content: '이메일이 변경되었습니다. 인증을 다시 진행해주세요.',
      });
      setUsernameMessage({ color: '', content: '' });
    }
  }, [formData.username, usernameState]);

  // 인증번호 확인
  const verifyEmailCode = async () => {
    if (!formData.code) return;

    if (!checkFourDigit(formData.code)) {
      setCodeMessage({ color: 'red-400', content: '인증번호를 확인해주세요.' });
      return;
    }

    try {
      await authApi.verifyEmailCode(formData.username, formData.code);

      setValidation((prev) => ({
        ...prev,
        username: { isValid: true },
      }));
      setUsernameMessage({ color: '', content: '' });
      setCodeMessage({ color: 'primary', content: '인증되었습니다.' });
    } catch (error) {
      const errordata = error.response.data;

      if (errordata.code === 'BAD_REQUEST' || errordata.code === 'TIME_OUT') {
        setCodeMessage({ color: 'red-400', content: errordata.message });
      } else {
        setCodeMessage({ color: 'red-400', content: '이메일 인증에 실패했습니다.' });
      }
    }
  };

  // 닉네임 중복 확인
  const verifyNickname = async () => {
    if (!formData.nickname || !checkNickname(formData.nickname)) return;

    try {
      const response = await authApi.verifyNickname(formData.nickname);
      const data = response.data;

      setValidation((prev) => ({
        ...prev,
        nickname: { isValid: true },
      }));
      setNicknameMessage({ color: 'primary', content: data.message });
    } catch (error) {
      const errordata = error.response.data;

      if (errordata.code === 'EXIST_NICKNAME') {
        setNicknameMessage({ color: 'red-400', content: errordata.message });
      } else {
        setNicknameMessage({ color: 'red-400', content: '닉네임 중복 확인에 실패했습니다.' });
      }
    }
  };

  // 회원가입
  const handleSubmit = async (e) => {
    e.preventDefault();

    const { code, ...signupData } = formData;

    try {
      await authApi.signup(signupData);
      navigate('/login');
    } catch (error) {
      const errordata = error.response.data;
      if (errordata.code === 'EXIST_EMAIL' || error.code === 'EXIST_NICKNAME') {
        alert(`회원가입에 실패했습니다.\n${errordata.message}`);
      } else {
        alert('회원가입에 실패했습니다.');
      }

      // 회원가입 실패 시 폼 상태 초기화
      setFormData({ username: '', code: '', nickname: '', password: '' });
      setVerifyPassword('');
      setValidation({
        username: { isValid: false },
        nickname: { isValid: false },
        password: { isValid: false, isEqual: false },
      });
      SetExpiredAt(null);
      setRemainingTime('3:00');
      setUsernameState({ isVerifying: false, username: '' });
      setUsernameMessage({ color: '', content: '' });
      setCodeMessage({ color: '', content: '' });
      setNicknameMessage({ color: '', content: '' });
      setPasswordMessage('');
    }
  };

  // 테일윈드 class
  const displayStyle = 'h-screen flex flex-col justify-center items-center gap-10';
  const logoStyle = 'w-48 cursor-pointer';
  const explainTextStyle = 'mb-5 text-2xl ';
  const inputButtonDiv = 'flex justify-between';
  const inputStyle = 'w-100 mx-1 px-2 focus:outline-none text-xl';
  const timeStyle = 'w-10 pr-3 self-center text-primary';
  const buttonStyle = 'w-25 border-2 rounded-full px-2 py-1';
  const able = 'border-primary text-primary hover:bg-primary hover:text-white cursor-pointer';
  const disable = 'border-gray-300 text-gray-300 hover:none';
  const lineStyle = 'mt-2';
  const messageStyle = 'mb-2 pl-2 font-semibold';
  const invalid = 'text-gray-300 font-semibold';
  const valid = 'text-primary font-semibold';
  const wrong = 'text-red-400 font-semibold';
  const signupButton = 'block mt-10 mx-auto border-2 rounded-2xl px-8 py-2 text-2xl';

  return (
    <div className={displayStyle}>
      <img src={logo} alt="이사모음집 로고" className={logoStyle} onClick={toHome} />
      <h2 className={explainTextStyle}>
        이사모음.zip의 새로운 <b className="text-secondary">Zipper</b>가 되어주세요
      </h2>
      <div>
        <form onSubmit={handleSubmit}>
          <div className={inputButtonDiv}>
            <input
              type="email"
              id="username"
              name="username"
              value={formData.username}
              placeholder="이메일 주소"
              className={inputStyle}
              onChange={handleFormInput}
              disabled={validation.username.isValid}
              required
            />
            <p className={timeStyle}>{remainingTime}</p>
            <button
              type="button"
              className={`${buttonStyle} ${validation.username.isValid ? disable : able}`}
              disabled={validation.username.isValid}
              onMouseDown={(e) => e.preventDefault()}
              onClick={verifyEmail}
            >
              이메일인증
            </button>
          </div>
          <hr className={lineStyle} />
          <p className={`${messageStyle} text-${usernameMessage.color}`}>
            {usernameMessage.content || '\u00A0'}
          </p>

          <div className={inputButtonDiv}>
            <input
              type="number"
              id="code"
              name="code"
              value={formData.code}
              placeholder="인증번호 입력"
              className={inputStyle}
              onChange={handleFormInput}
              disabled={validation.username.isValid || !usernameState.isVerifying}
              required
            />
            <button
              type="button"
              className={`${buttonStyle} ${validation.username.isValid || !usernameState.isVerifying ? disable : able}`}
              disabled={validation.username.isValid || !usernameState.isVerifying}
              onMouseDown={(e) => e.preventDefault()}
              onClick={verifyEmailCode}
            >
              확인
            </button>
          </div>
          <hr className={lineStyle} />
          <p className={`${messageStyle} text-${codeMessage.color}`}>
            {codeMessage.content || '\u00A0'}
          </p>

          <div className={inputButtonDiv}>
            <input
              type="text"
              id="nickname"
              name="nickname"
              value={formData.nickname}
              placeholder="닉네임"
              className={inputStyle}
              onChange={handleFormInput}
              required
            />
            <button
              type="button"
              className={`${buttonStyle} ${validation.nickname.isValid ? disable : able}`}
              disabled={validation.nickname.isValid}
              onMouseDown={(e) => e.preventDefault()}
              onClick={verifyNickname}
            >
              중복확인
            </button>
          </div>
          <hr className={lineStyle} />
          <p className={`${messageStyle} text-${nicknameMessage.color}`}>
            {nicknameMessage.content || '\u00A0'}
          </p>

          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            placeholder="비밀번호"
            className={`${inputStyle} mt-2`}
            onChange={handleFormInput}
            required
          />
          <hr className={lineStyle} />
          <ul className="flex gap-4 ml-2 mt-1 mb-3">
            <li className={checkMinLength(formData.password) ? valid : invalid}>✓ 8자 이상</li>
            <li className={checkLetter(formData.password) ? valid : invalid}>✓ 영문</li>
            <li className={checkNumber(formData.password) ? valid : invalid}>✓ 숫자</li>
            <li className={checkSpecialChar(formData.password) ? valid : invalid}>
              ✓ 특수문자(#?!)
            </li>
            <li className={checkInvalidChar(formData.password) ? wrong : invalid}>
              ✗ 허용되지 않는 문자
            </li>
          </ul>

          <input
            type="password"
            id="verifyPassword"
            name="verifyPassword"
            value={verifyPassword}
            placeholder="비밀번호 확인"
            className={`${inputStyle} mt-2`}
            onChange={handleVerifyPassword}
            required
          />
          <hr className={lineStyle} />
          <p className={`${messageStyle} text-red-400`}>{passwordMessage || '\u00A0'}</p>

          <button
            className={`${signupButton} ${isSignupAvailable ? able : disable}`}
            disabled={!isSignupAvailable}
          >
            회원가입
          </button>
        </form>
      </div>
    </div>
  );
}
