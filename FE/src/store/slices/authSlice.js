import { createSlice } from "@reduxjs/toolkit";

const initialState = { 
  accessToken: localStorage.getItem('accessToken'),
  isLoggedIn: !!localStorage.getItem('accessToken'),
}; 

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    login: (state, action) => {
      state.accessToken = action.payload.accessToken;
      state.isLoggedIn = true;
      localStorage.setItem('accessToken', action.payload.accessToken);
    },
    logout: (state, action) => {
      state.accessToken = null;
      state.isLoggedIn = false;
      localStorage.removeItem('accessToken');
    },
  },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;
