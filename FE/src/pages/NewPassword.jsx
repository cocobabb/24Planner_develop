import React, { useEffect, useState } from 'react';
import logo from '../logo.png';
import Password from '../component/user/Password';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import userApi from '../api/userApi';
import { useSelector } from 'react-redux';

export default function NewPassword() {
  const navigate = useNavigate();

  const [token, setToken] = useState();
  const [email, setEmail] = useState();

  const [search] = useSearchParams();
  const query = search.get('query');

  const accessTokenData = useSelector((state) => state.auth.accessToken);

  const getRedisValue = async () => {
    try {
      const decoded = jwtDecode(query);
      const username = decoded.sub;
      const key = username + '_tempToken';
      setEmail(username);

      const response = await userApi.redis(key);
      const code = response.code;
      const value = response.data.value;
      setToken(value);
      if (!value) {
        navigate('/not-found');
        return;
      } else if (query !== value) {
        navigate('/not-found');
        return;
      }
    } catch (error) {
      console.error();
    }
  };
  useEffect(() => {
    if (!query) {
      navigate('/not-found');
      return;
    } else if (accessTokenData) {
      navigate('/');
      return;
    }

    getRedisValue();
  }, []);

  const container = 'w-full grid content-center justify-items-center relative top-50';
  const image = 'w-64 mb-20 text-center cursor-pointer';

  return (
    <div className={`${container}`}>
      <img alt="이사모음집 로고" className={`${image}`} src={logo}></img>

      <Password token={token} email={email}></Password>
    </div>
  );
}
