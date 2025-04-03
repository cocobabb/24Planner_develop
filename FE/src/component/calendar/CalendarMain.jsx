import { useRef } from 'react';

import CalendarContent from './content/CalendarContent';
import CalendarHeader from './calendarHeader/CalendarHeader';

export default function CalendarMain({
  yearState,
  setYearState,
  monthState,
  setMonthState,
  selectDate,
  setSelectDate,
  monthlyEventList,
  setMonthlyEventList,
  setIsShowingModal,
  setShowingScheduleToModal,
}) {
  const calendarRef = useRef(null);

  const calendarMainStyle = 'flex flex-col flex-2 size-full border-r-1 border-gray-300 my-4';
  const calendarPaddingStyle = 'px-8';

  return (
    <section className={calendarMainStyle}>
      <div className={calendarPaddingStyle}>
        <CalendarHeader
          calendarRef={calendarRef}
          yearState={yearState}
          setYearState={setYearState}
          monthState={monthState}
          setMonthState={setMonthState}
          setSelectDate={setSelectDate}
          setIsShowingModal={setIsShowingModal}
          setShowingScheduleToModal={setShowingScheduleToModal}
        />
        <CalendarContent
          calendarRef={calendarRef}
          yearState={yearState}
          setYearState={setYearState}
          monthState={monthState}
          setMonthState={setMonthState}
          monthlyEventList={monthlyEventList}
          setMonthlyEventList={setMonthlyEventList}
          selectDate={selectDate}
          setSelectDate={setSelectDate}
        />
      </div>
    </section>
  );
}
