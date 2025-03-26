import { createPortal } from 'react-dom';
import { useSelector, useDispatch } from 'react-redux';
import { mouseMoveReducer } from '../../../store/slices/popoverSlice';
import PopoverComponent from './PopoverComponent';

export default function PopoverUtilComponent({ children }) {
  const dispatch = useDispatch();

  const handleMainMouseMove = (e) => {
    dispatch(mouseMoveReducer({ x: e.clientX, y: e.clientY }));
  };

  const popoverUtilComponentStyle = 'flex flex-col items-center h-full w-full';

  return (
    <>
      {useSelector((state) => state.popover.popoverTitle) !== '' &&
        createPortal(<PopoverComponent />, document.body)}
      <div className={popoverUtilComponentStyle} onMouseMove={handleMainMouseMove}>
        {children}
      </div>
    </>
  );
}
