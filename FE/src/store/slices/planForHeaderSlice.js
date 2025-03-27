import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  title: '',
};

const planForHeaderSlice = createSlice({
  name: 'planForHeader',
  initialState,
  reducers: {
    setCurrentPlanTitle: (state, action) => {
      state.title = action.payload.title;
    },
  },
});

export const { setCurrentPlanTitle } = planForHeaderSlice.actions;
export default planForHeaderSlice.reducer;
