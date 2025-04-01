import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import popoverReducer from './slices/popoverSlice';
import planForHeaderReducer from './slices/planForHeaderSlice';
import authPwdReducer from './slices/authPwdSlice';
const store = configureStore({
  reducer: {
    auth: authReducer,
    popover: popoverReducer,
    planForHeader: planForHeaderReducer,
    authPwd: authPwdReducer,
  },
});

export default store;
