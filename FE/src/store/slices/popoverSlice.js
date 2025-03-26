import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  x: -1,
  y: -1,
  popoverTitle: '',
  popoverStartDate: null,
  popoverEndDate: null,
  popoverColor: null,
};

const popoverSlice = createSlice({
  name: 'popover',
  initialState,
  reducers: {
    mouseMoveReducer: (state, action) => {
      state.x = action.payload.x;
      state.y = action.payload.y;
    },
    eventMouseHoverReducer: (state, action) => {
      state.popoverTitle = action.payload.title;
      state.popoverStartDate = action.payload.start;
      state.popoverEndDate = action.payload.end;
      state.popoverColor = action.payload.color;
    },
    eventMouseLeaveReducer: (state) => {
      state.popoverTitle = '';
      state.popoverStartDate = null;
      state.popoverEndDate = null;
      state.popoverColor = null;
    },
  },
});

export const { mouseMoveReducer, eventMouseHoverReducer, eventMouseLeaveReducer } =
  popoverSlice.actions;

export default popoverSlice.reducer;
