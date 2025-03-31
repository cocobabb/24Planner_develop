import calendarUtil from './util/calendarUtil';

import ChevronLeftSvg from './svg/ChevronLeftSvg';
import ChevronRightSvg from './svg/ChevronRightSvg';

export default function CalendarHeader({
  calendarRef,
  yearState,
  setYearState,
  monthState,
  setMonthState,
  setShowingScheduleToModal,
  setIsShowingModal,
}) {
  const moveToCurrentMonth = () => {
    const now = new Date();
    calendarRef.current.getApi().gotoDate(now);
    setSelectDate(calendarUtil.parseDateStrFromObject(now));
  };

  const moveToPrevMonth = () => {
    const targetYear = yearState - (monthState === 1);
    const targetMonth = ((monthState + 10) % 12) + 1;
    setYearState(targetYear);
    setMonthState(targetMonth);
    calendarRef.current.getApi().gotoDate(calendarUtil.parseDateStr(targetYear, targetMonth, 1));
  };

  const moveToNextMonth = () => {
    const targetYear = yearState + (monthState === 12);
    const targetMonth = (monthState % 12) + 1;
    setYearState(targetYear);
    setMonthState(targetMonth);
    calendarRef.current.getApi().gotoDate(calendarUtil.parseDateStr(targetYear, targetMonth, 1));
  };

  const handleCalendarModal = () => {
    setShowingScheduleToModal(() => null);
    setIsShowingModal(() => true);
  };

  const monthList = [
    'January',
    'Febraury',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December',
  ];

  const calendarHeaderStyle = 'flex justify-between w-full pt-4';
  const calendarHeaderLeftStyle = 'flex flex-1 justify-start';
  const calendarHeaderLeftContentStyle = 'flex items-center mx-2';
  const calendarHeaderLeftMonthNumberStyle =
    'bg-secondary rounded-[50%] text-center text-white text-xl font-black p-2 px-4';
  const calendarHeaderLeftMonthStyle = 'mx-2 text-secondary text-lg font-bold font-lexend';
  const calendarHeaderCenterStyle = 'flex flex-2 justify-center items-center font-lexend';
  const calendarHeaderRightStyle = 'flex flex-1 justify-end items-center';
  const calendarHeaderCenterPrevDivStyle = 'flex flex-1 justify-end';
  const calendarHeaderCenterDateDivStyle = 'flex flex-3 justify-center mx-5';
  const calendarHeaderCenterDateTextStyle = 'text-xl';
  const calendarHeaderCenterDivClickableStyle = 'cursor-pointer';
  const chevronStyle = 'size-8 fill-secondary';
  const calendarHeaderCenterNextDivStyle = 'flex flex-1 justify-start';
  const buttonStyle =
    'w-20 h-10 bg-white border-2 border-primary rounded-xl text-primary font-semibold cursor-pointer hover:bg-primary hover:text-white ml-2';

  return (
    <div className={calendarHeaderStyle}>
      <nav className={calendarHeaderLeftStyle}>
        <div className={calendarHeaderLeftContentStyle}>
          <div className={calendarHeaderLeftMonthNumberStyle}>
            {monthState.toString().padStart(2, '0')}
          </div>
          <div className={calendarHeaderLeftMonthStyle}>{monthList[monthState - 1]}</div>
        </div>
      </nav>
      <nav className={calendarHeaderCenterStyle}>
        <div className={calendarHeaderCenterPrevDivStyle}>
          <div className={calendarHeaderCenterDivClickableStyle} onClick={moveToPrevMonth}>
            <ChevronLeftSvg className={chevronStyle} />
          </div>
        </div>
        <div className={calendarHeaderCenterDateDivStyle}>
          <h2 className={calendarHeaderCenterDateTextStyle}>
            {yearState}.{monthState.toString().padStart(2, '0')}
          </h2>
        </div>
        <div className={calendarHeaderCenterNextDivStyle} onClick={moveToNextMonth}>
          <div className={calendarHeaderCenterDivClickableStyle} onClick={moveToNextMonth}>
            <ChevronRightSvg className={chevronStyle} />
          </div>
        </div>
      </nav>
      <nav className={calendarHeaderRightStyle}>
        <button className={buttonStyle} onClick={moveToCurrentMonth}>
          오늘
        </button>
        <button className={buttonStyle} onClick={handleCalendarModal}>
          추가
        </button>
      </nav>
    </div>
  );
}
