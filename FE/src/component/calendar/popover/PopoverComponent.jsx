import { useSelector, useDispatch } from 'react-redux';
import { mouseMoveReducer } from '../../../store/slices/popoverSlice';

export default function PopoverComponent() {
  const dispatch = useDispatch();
  const pointerPositionX = useSelector((state) => state.popover.x);
  const pointerPositionY = useSelector((state) => state.popover.y);
  const popoverTitle = useSelector((state) => state.popover.popoverTitle);
  const popoverStartDate = useSelector((state) => state.popover.popoverStartDate);
  const popoverEndDate = useSelector((state) => state.popover.popoverEndDate);
  const popoverBorderColor = useSelector((state) => state.popover.popoverColor);

  const handlePopoverHover = () => {
    dispatch(mouseMoveReducer({ x: -1, y: -1 }));
  };

  const popoverStyle = `w-fit h-fit absolute bg-white rounded-2xl border-1 p-1 z-10`;
  const popoverBorderMarginStyle = `rounded-2xl border-1 border-[${popoverBorderColor}]`;
  const popoverContentStyle = 'm-2';
  const popoverTitleStyle = 'text-center font-semibold mb-2';
  const popoverDateRangeStyle = 'text-sm text-center';

  return (
    <div
      style={{
        left: pointerPositionX + 'px',
        top: pointerPositionY + 20 + 'px',
      }}
      className={popoverStyle}
      onMouseMove={handlePopoverHover}
    >
      <div className={popoverBorderMarginStyle}>
        <div className={popoverContentStyle}>
          <div className={popoverTitleStyle}>{popoverTitle}</div>
          <div className={popoverDateRangeStyle}>
            ({popoverStartDate}~{popoverEndDate})
          </div>
        </div>
      </div>
    </div>
  );
}
