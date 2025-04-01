import React, { useEffect, useState } from 'react';
import Password from '../component/user/Password';
import { useNavigate, useSearchParams } from 'react-router-dom';
import authApi from '../api/authApi';
import { jwtDecode } from 'jwt-decode';

export default function NewPassword() {
  const navigate = useNavigate();

  const [value, setValue] = useState();

  const [search] = useSearchParams();
  const query = search.get('query');

  const accessTokenData = localStorage.getItem('accessToken');

  useEffect(() => {
    if (!query) {
      navigate('/notfound');
      return;
    }
    const getRedisValue = async () => {
      try {
        const decoded = jwtDecode(query);
        const username = decoded.sub;
        const key = username + '_tempToken';

        const response = await authApi.redis(key);
        console.log(response);
        const code = response.code;
        const value = response.data.value;
        setValue(value);
        if (!value) {
          navigate('/notfound');
          return;
        } else if (query !== value) {
          navigate('/notfound');
          return;
        }
      } catch (error) {
        console.error();
      }
    };

    getRedisValue();
  }, []);

  const container = 'w-full grid content-center justify-items-center relative top-50';
  const image = 'w-64 mb-20 text-center cursor-pointer';

  return (
    <div className={`${container}`}>
      <img alt="이사모음집 로고" className={`${image}`} src="/src/logo.png"></img>

      <Password value={value}></Password>
    </div>
  );
}
