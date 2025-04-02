import React, { useEffect, useState } from 'react';
import Password from '../component/user/Password';
import { useDispatch } from 'react-redux';
import { modifyNickname } from '../store/slices/authSlice';
import userApi from '../api/userApi';

export default function Mypage() {
  const dispatch = useDispatch();
  const [nickname, setNickname] = useState();
  const [formData, setFormData] = useState({
    nickname: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState();

  useEffect(() => {
    const getNickname = async () => {
      try {
        const response = await userApi.getNickname();
        const code = response.code;
        const nickname = response.data.nickname;
        if (code === 'OK') {
          setNickname(nickname);
        }
      } catch (error) {
        const errorData = error.response.data;
        const message = errorData.message;
        setMessage(message);
      }
    };
    getNickname();
  }, []);

  const handleInputValue = (e) => {
    setMessage();
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const patchNickname = async (e) => {
    e.preventDefault();

    if (isSubmitting) return;
    setIsSubmitting(true);

    try {
      const response = await userApi.patchNickname(formData);
      const updatedNickname = response.data.nickname;
      const code = response.code;
      if (code === 'UPDATED') {
        setNickname(updatedNickname);
        setFormData({ nickname: '' });
        dispatch(modifyNickname({ nickname: updatedNickname }));
      }
    } catch (error) {
      const errorData = error.response.data;
      const code = errorData.code;
      const message = errorData.message;
      if (code !== 'INVALID_TOKEN') {
        setMessage(message);
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const container = 'w-full mt-30 grid content-center justify-items-center ';
  const form = 'mb-20 relative';
  const inputStyle = 'w-110 m-3 px-2 focus:outline-none text-xl';
  const lineStyle = 'w-168';
  const buttonStyle =
    'w-35 border-2 rounded-full px-2 py-1 border-primary text-primary hover:bg-primary hover:text-white cursor-pointer absolute right-1';
  const del = 'text-gray-300 relative top-20';
  const messageStyle = 'mb-2 pl-2 font-semibold text-red-400';
  const noticeTextStyle = 'm-2 pl-2 font-semibold text-secondary';

  return (
    <>
      <div className={`${container}`}>
        <form className={`${form}`} onSubmit={patchNickname}>
          <input
            type="text"
            name="nickname"
            className={inputStyle}
            value={formData.nickname}
            placeholder={nickname}
            onChange={handleInputValue}
            required
          />

          <button type="button" className={`${buttonStyle}`} onClick={patchNickname}>
            닉네임 변경
          </button>
          <hr className={`${lineStyle}`}></hr>
          <p className={`${noticeTextStyle}`}>닉네임은 2~17글자까지 작성 가능합니다</p>
          <p className={`${messageStyle}`}>{message || '\u00A0'}</p>
        </form>

        <Password></Password>
        <button className={`${del}`}>탈퇴하기</button>
      </div>
    </>
  );
}
