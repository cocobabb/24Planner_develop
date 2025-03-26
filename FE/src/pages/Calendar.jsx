import { useState } from 'react';
import CalendarContent from '../component/calendar/CalendarContent';
import CalendarSidebar from '../component/calendar/CalendarSidebar';

export default function Calendar() {
  const now = new Date();
  const [selectDate, setSelectDate] = useState(
    parseDate(now.getFullYear(), now.getMonth() + 1, now.getDate()),
  );
  const [eventList, setEventList] = useState([]);

  // TODO: 테스트용 데이터이므로 API 연결 후 삭제 예정
  const scheduleList = [
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-02-27',
      endDate: '2025-02-28',
    },
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-02-27',
      endDate: '2025-03-01',
    },
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-02-27',
      endDate: '2025-03-30',
    },
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-02-27',
      endDate: '2025-03-31',
    },
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-02-27',
      endDate: '2025-04-01',
    },
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-03-01',
      endDate: '2025-03-02',
    },
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-03-28',
      endDate: '2025-03-30',
    },
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-03-28',
      endDate: '2025-03-31',
    },
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-03-31',
      endDate: '2025-04-01',
    },
    {
      color: '#69db7c',
      content: '테스트',
      startDate: '2025-04-01',
      endDate: '2025-04-01',
    },
    {
      color: '#4dabf7',
      content: '테스트',
      startDate: '2025-04-08',
      endDate: '2025-04-08',
    },
    {
      color: '#69db7c',
      content: '일이삼사오육칠팔구십일이삼사오육칠팔구십',
      startDate: '2025-04-08',
      endDate: '2025-04-08',
    },
  ];

  const calendarMainStyle = 'flex justify-center h-full px-2 pb-4';

  return (
    <main className={calendarMainStyle}>
      <CalendarContent
        setSelectDate={setSelectDate}
        scheduleList={scheduleList}
        eventList={eventList}
        setEventList={setEventList}
      />
      <CalendarSidebar selectDate={selectDate} scheduleList={scheduleList} />
    </main>
  );
}

function parseDate(year, month, day) {
  return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
}
