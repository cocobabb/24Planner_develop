import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  accessToken: localStorage.getItem('accessToken'),
  nickname: localStorage.getItem('nickname') || '',
  isLoggedIn: !!localStorage.getItem('accessToken'),
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    login: (state, action) => {
      state.accessToken = action.payload.accessToken;
      state.nickname = action.payload.nickname;
      localStorage.setItem('accessToken', action.payload.accessToken);
      localStorage.setItem('nickname', action.payload.nickname);
      state.isLoggedIn = true;
    },
    logout: (state, action) => {
      state.isLoggedIn = false;
      state.accessToken = null;
      state.nickname = '';
      localStorage.removeItem('accessToken');
      localStorage.removeItem('nickname');
    },
    modifyNickname: (state, action) => {
      state.nickname = action.payload.nickname;
      localStorage.setItem('nickname', action.payload.nickname);
    },
  },
});

export const { login, logout, modifyNickname } = authSlice.actions;
export default authSlice.reducer;
