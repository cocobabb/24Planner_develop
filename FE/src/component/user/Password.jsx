import React, { useState } from 'react';
import authApi from '../../api/authApi';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { login, logout } from '../../store/slices/authSlice';

export default function Password({ value }) {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const accessTokenData = localStorage.getItem('accessToken');

  const [message, setMessage] = useState();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [passwordMessage, setPasswordMessage] = useState('');
  const [verifyPassword, setVerifyPassword] = useState('');
  const [formData, setFormData] = useState({
    password: '',
  });
  const [validation, setValidation] = useState({
    password: { isValid: false, isEqual: false },
  });

  const handleInputValue = (e) => {
    setMessage();
    const { name, value } = e.target;
    if (name === 'verifyPassword') {
      setVerifyPassword(value);
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }

    if (name === 'password') {
      setValidation((prev) => ({
        ...prev,
        password: { isValid: checkPassword(value), isEqual: value === verifyPassword },
      }));

      if (!verifyPassword) {
        setPasswordMessage('');
      } else {
        setPasswordMessage('비밀번호가 일치하지 않습니다.');
      }
    } else if (name === 'verifyPassword') {
      setValidation((prev) => ({
        ...prev,
        password: { ...prev.password, isEqual: value === formData.password },
      }));

      if (!value || value === formData.password) {
        setPasswordMessage('');
      } else if (value != formData.password) {
        setPasswordMessage('비밀번호가 일치하지 않습니다.');
      }
    }
  };

  // 비밀번호 입력값 검증
  const checkPassword = (value) => /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&]).{8,}$/.test(value);
  const checkMinLength = (value) => value.length >= 8;
  const checkLetter = (value) => /[a-zA-Z]/.test(value);
  const checkNumber = (value) => /[0-9]/.test(value);
  const checkSpecialChar = (value) => /[@$!%*#?&]/.test(value);
  const checkInvalidChar = (value) => /[^A-Za-z0-9@$!%*#?&]/.test(value);

  const patchPassword = async (e) => {
    e.preventDefault();

    if (isSubmitting) return;
    setIsSubmitting(true);

    if (!verifyPassword) {
      setMessage('필수값이 누락되거나 형식이 올바르지 않습니다.');
      setIsSubmitting(false);
      return;
    } else if (!value && !accessTokenData) {
      alert('페이지 사용시간이 만료되어 비밀번호 변경에 실패하였습니다.');
      navigate('/login');
      return;
    }

    try {
      if (value) {
        dispatch(login({ accessToken: value }));
      }
      const response = await authApi.patchPassword(formData);
      const code = response.code;
      const message = response.message;
      console.log(value);
      if (!value && code === 'UPDATED') {
        alert('비밀번호가 수정되었습니다.');
        return;
      } else {
        dispatch(logout());
        navigate('/login');
        return;
      }
    } catch (error) {
      const errorData = error.response.data;
      const code = errorData.code;
      const message = errorData.message;

      if (code !== 'TOOMANY_REQUEST') {
        setMessage(message);
      }
    } finally {
      setIsSubmitting(false);
      setFormData({
        password: '',
      });
      setVerifyPassword('');
    }
  };

  const container = 'w-full grid content-center justify-items-center';
  const form = 'w-full grid content-center justify-items-center relative ';
  const inputStyle = 'w-110 m-3 px-2 focus:outline-none text-xl';
  const invalid = 'text-gray-300 font-semibold';
  const valid = 'text-primary font-semibold';
  const wrong = 'text-red-400 font-semibold';
  const lineStyle = 'w-168';
  const buttonStyle =
    'w-45 border-2 rounded-full m-3 px-2 py-1 border-primary text-primary hover:bg-primary hover:text-white cursor-pointer';
  const messageStyle = 'mb-2 pl-2 font-semibold text-red-400';

  return (
    <form className={`${form}`} onSubmit={patchPassword}>
      <div>
        <input
          type="password"
          name="password"
          value={formData.password}
          placeholder="비밀번호"
          className={inputStyle}
          onChange={handleInputValue}
          required
        />
        <hr className={`${lineStyle}`}></hr>
        <ul class="flex justify-between ml-2 mt-1 mb-3">
          <li
            className={
              checkMinLength(formData.password) && !checkInvalidChar(formData.password)
                ? valid
                : invalid
            }
          >
            ✓ 8자 이상
          </li>
          <li
            className={
              checkLetter(formData.password) && !checkInvalidChar(formData.password)
                ? valid
                : invalid
            }
          >
            ✓ 영문
          </li>
          <li
            className={
              checkNumber(formData.password) && !checkInvalidChar(formData.password)
                ? valid
                : invalid
            }
          >
            ✓ 숫자
          </li>
          <li
            className={
              checkSpecialChar(formData.password) && !checkInvalidChar(formData.password)
                ? valid
                : invalid
            }
          >
            ✓ 특수문자(@$!%*#?&)
          </li>
          <li className={checkInvalidChar(formData.password) ? wrong : invalid}>
            ✗ 허용되지 않는 문자&nbsp;
          </li>
        </ul>
        <input
          type="password"
          name="verifyPassword"
          value={verifyPassword}
          placeholder="비밀번호 확인"
          className={inputStyle}
          onChange={handleInputValue}
          required
        />
        <hr className={lineStyle} />
        <p className={`${messageStyle}`}>{passwordMessage || '\u00A0'}</p>
      </div>
      <p className={`${messageStyle}`}>{message || '\u00A0'}</p>
      <button type="button" className={`${buttonStyle}`} onClick={patchPassword}>
        비밀번호 변경
      </button>
    </form>
  );
}
