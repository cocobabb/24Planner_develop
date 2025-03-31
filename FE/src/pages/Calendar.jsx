import { useState } from 'react';
import { createPortal } from 'react-dom';

import CalendarMain from '../component/calendar/CalendarMain';
import CalendarSidebar from '../component/calendar/CalendarSidebar';
import CalendarModal from '../component/calendar/CalendarModal';

import calendarUtil from '../component/calendar/util/calendarUtil';

export default function Calendar() {
  // Tailwind CSS에서 사용할 수 있도록 바탕 색상을 미리 정의하는 부분으로, 실제 대입하지는 않음
  const backgroundColorsListForSelect = [
    ['bg-[#FFF5F5]', 'bg-[#FFC9C9]', 'bg-[#FF8787]', 'bg-[#FA5252]', 'bg-[#E03131]'],
    ['bg-[#FFF4E6]', 'bg-[#FFD8A8]', 'bg-[#FFA94D]', 'bg-[#FD7E14]', 'bg-[#E8590C]'],
    ['bg-[#FFF9DB]', 'bg-[#FFEC99]', 'bg-[#FFD43B]', 'bg-[#FAB005]', 'bg-[#F08C00]'],
    ['bg-[#E6FCF5]', 'bg-[#96F2D7]', 'bg-[#38D9A9]', 'bg-[#12B886]', 'bg-[#099268]'],
    ['bg-[#EBFBEE]', 'bg-[#B2F2BB]', 'bg-[#69DB7C]', 'bg-[#40C057]', 'bg-[#2F9E44]'],
    ['bg-[#E7F5FF]', 'bg-[#A5D8FF]', 'bg-[#4DABF7]', 'bg-[#228BE6]', 'bg-[#1971C2]'],
    ['bg-[#F3F0FF]', 'bg-[#D0BFFF]', 'bg-[#9775FA]', 'bg-[#7950F2]', 'bg-[#6741D9]'],
    ['bg-[#F8F0FC]', 'bg-[#EEBEFA]', 'bg-[#DA77F2]', 'bg-[#BE4BDB]', 'bg-[#9C36B5]'],
    ['bg-[#F8F1EE]', 'bg-[#EADDD7]', 'bg-[#D2BAB0]', 'bg-[#A18072]', 'bg-[#846358]'],
    ['bg-[#E9ECEF]', 'bg-[#D0D6DC]', 'bg-[#9CA2A8]', 'bg-[#686E74]', 'bg-[#343A40]'],
  ];

  const borderColorsListForSelect = [
    [
      'border-[#FFF5F5]',
      'border-[#FFC9C9]',
      'border-[#FF8787]',
      'border-[#FA5252]',
      'border-[#E03131]',
    ],
    [
      'border-[#FFF4E6]',
      'border-[#FFD8A8]',
      'border-[#FFA94D]',
      'border-[#FD7E14]',
      'border-[#E8590C]',
    ],
    [
      'border-[#FFF9DB]',
      'border-[#FFEC99]',
      'border-[#FFD43B]',
      'border-[#FAB005]',
      'border-[#F08C00]',
    ],
    [
      'border-[#E6FCF5]',
      'border-[#96F2D7]',
      'border-[#38D9A9]',
      'border-[#12B886]',
      'border-[#099268]',
    ],
    [
      'border-[#EBFBEE]',
      'border-[#B2F2BB]',
      'border-[#69DB7C]',
      'border-[#40C057]',
      'border-[#2F9E44]',
    ],
    [
      'border-[#E7F5FF]',
      'border-[#A5D8FF]',
      'border-[#4DABF7]',
      'border-[#228BE6]',
      'border-[#1971C2]',
    ],
    [
      'border-[#F3F0FF]',
      'border-[#D0BFFF]',
      'border-[#9775FA]',
      'border-[#7950F2]',
      'border-[#6741D9]',
    ],
    [
      'border-[#F8F0FC]',
      'border-[#EEBEFA]',
      'border-[#DA77F2]',
      'border-[#BE4BDB]',
      'border-[#9C36B5]',
    ],
    [
      'border-[#F8F1EE]',
      'border-[#EADDD7]',
      'border-[#D2BAB0]',
      'border-[#A18072]',
      'border-[#846358]',
    ],
    [
      'border-[#E9ECEF]',
      'border-[#D0D6DC]',
      'border-[#9CA2A8]',
      'border-[#686E74]',
      'border-[#343A40]',
    ],
  ];

  const now = new Date();
  const [yearState, setYearState] = useState(now.getFullYear());
  const [monthState, setMonthState] = useState(now.getMonth() + 1);
  const [selectDate, setSelectDate] = useState(
    calendarUtil.parseDateStr(now.getFullYear(), now.getMonth() + 1, now.getDate()),
  );
  /* 
    Schedule은 DB에서 받아오는 형식을 그대로 사용함을 의미
    Event는 DB에서 받은 Schedule을 달력에 적용할 수 있게 변형된 데이터 형식으로 저장된다는 의미
    일정 추가 시 State를 통한 자동 렌더링을 원할하게 구현할 수 있도록 부득이하게 별도 관리
  */
  const [monthlyEventList, setMonthlyEventList] = useState([]);
  const [dailyScheduleList, setDailyScheduleList] = useState([]);

  /*
    일정 생성/수정 modal 출력 여부, 그리고 modal에서 보여줄 schedule 객체 저장
    저장된 게 없으면 modal은 생성, 있으면 수정 역할을 하게 됨
  */
  const [isShowingModal, setIsShowingModal] = useState(false);
  const [showingScheduleToModal, setShowingScheduleToModal] = useState(null);

  const calendarMainStyle = 'flex justify-center h-full px-2 pb-4';

  return (
    <main className={calendarMainStyle}>
      {isShowingModal &&
        createPortal(
          <CalendarModal
            yearState={yearState}
            monthState={monthState}
            selectDate={selectDate}
            dailyScheduleList={dailyScheduleList}
            setDailyScheduleList={setDailyScheduleList}
            monthlyEventList={monthlyEventList}
            setMonthlyEventList={setMonthlyEventList}
            modalClose={() => {
              setShowingScheduleToModal(() => null);
              setIsShowingModal(() => false);
            }}
            showingScheduleToModal={showingScheduleToModal}
          />,
          document.body,
        )}
      <CalendarMain
        yearState={yearState}
        setYearState={setYearState}
        monthState={monthState}
        setMonthState={setMonthState}
        selectDate={selectDate}
        setSelectDate={setSelectDate}
        monthlyEventList={monthlyEventList}
        setMonthlyEventList={setMonthlyEventList}
        setIsShowingModal={setIsShowingModal}
        setShowingScheduleToModal={setShowingScheduleToModal}
      />
      <CalendarSidebar
        yearState={yearState}
        monthState={monthState}
        selectDate={selectDate}
        dailyScheduleList={dailyScheduleList}
        setDailyScheduleList={setDailyScheduleList}
        setMonthlyEventList={setMonthlyEventList}
        setIsShowingModal={setIsShowingModal}
        setShowingScheduleToModal={setShowingScheduleToModal}
      />
    </main>
  );
}
