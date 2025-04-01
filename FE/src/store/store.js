import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import popoverReducer from './slices/popoverSlice';
import planForHeaderReducer from './slices/planForHeaderSlice';

const store = configureStore({
  reducer: {
    auth: authReducer,
    popover: popoverReducer,
    planForHeader: planForHeaderReducer,
  },
});

export default store;
