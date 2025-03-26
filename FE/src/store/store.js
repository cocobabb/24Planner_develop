import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./slices/authSlice";
import popoverReducer from './slices/popoverSlice';

const store = configureStore({
  reducer: {
    auth: authReducer,
    popover: popoverReducer,
  },
});

export default store;
