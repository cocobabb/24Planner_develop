import { useDispatch } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';

import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';

import scheduleApi from '../../../api/scheduleApi';

import { eventMouseHoverReducer, eventMouseLeaveReducer } from '../../../store/slices/popoverSlice';

import calendarUtil from '../util/calendarUtil';

import '../style/calendarContent.css';

export default function CalendarContent({
  calendarRef,
  setYearState,
  setMonthState,
  monthlyEventList,
  setMonthlyEventList,
  selectDate,
  setSelectDate,
}) {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { movingPlanId } = useParams();

  const handleEventMouseEnter = (e) => {
    dispatch(
      eventMouseHoverReducer({
        title: e.event.title,
        start: e.event.startStr,
        end: calendarUtil.parseDateStrFromObject(new Date(e.event.end - 86400000)),
        color: e.event.backgroundColor,
      }),
    );
  };

  const handleEventMouseLeave = () => {
    dispatch(eventMouseLeaveReducer());
  };

  const handleDateCellClick = (e) => {
    setSelectDate(() => e.dateStr);
  };

  const loadList = async (yearMonth) => {
    setMonthlyEventList(() => []);

    try {
      // 월 단위로 스케줄 가져오기
      const response = await scheduleApi.getMonthlySchedule(movingPlanId, yearMonth);
      const scheduleList = response.data.data.schedules;

      // 달력에 맞게 형식 변경
      setMonthlyEventList(() =>
        scheduleList.map((schedule) => calendarUtil.scheduleToEvent(schedule)),
      );
    } catch (err) {
      console.log(err);
      const errordata = err.response.data;
      if (errordata.code === 'NOT_FOUND') {
        navigate('/not-found');
      }
    }
  };

  const selectDateObject = new Date(selectDate);

  const eventLineStyle = 'h-3 px-2 text-tiny truncate';

  return (
    <FullCalendar
      ref={calendarRef}
      plugins={[dayGridPlugin, interactionPlugin]}
      initialView="dayGridMonth"
      showNonCurrentDates={false}
      dayMaxEventRows={3}
      events={monthlyEventList}
      eventMouseEnter={handleEventMouseEnter}
      eventMouseLeave={handleEventMouseLeave}
      dateClick={handleDateCellClick}
      dayHeaderContent={(arg) => ({
        html: `${arg.text.toUpperCase()}`,
      })}
      dayCellContent={(arg) => ({
        html: `<div class="text-center ${isEqualDay(selectDateObject, arg.date) ? 'animate-selected-date' : ''}">${arg.date.getDate()}</div>`,
      })}
      // 더 보기 버튼에, 기본 제공되는 +3 개라는 텍스트 대신 +3만 사용
      moreLinkContent={(arg) => ({ html: arg.shortText })}
      eventContent={(arg) => {
        return {
          html: `<div class="${eventLineStyle} ${calendarUtil.determineBlackText(calendarUtil.hexColorToIntArray(arg.event.backgroundColor)) ? 'text-black' : 'text-white'}">${arg.event.title}</div>`,
        };
      }}
      dayPopoverFormat={(arg) => {
        return `${arg.date.year}년 ${arg.date.month + 1}월 ${arg.date.day}일`;
      }}
      datesSet={async (dateInfo) => {
        const selectedYear = dateInfo.start.getFullYear();
        const selectedMonth = dateInfo.start.getMonth() + 1;

        // 스타일 수정을 위해 헤더를 외부에서 정의했으므로, 월을 바꿀 때마다 해당 년도와 월을 state에 지정해야 표시됨
        setYearState(() => selectedYear);
        setMonthState(() => selectedMonth);

        loadList(parseMonth(selectedYear, selectedMonth));
      }}
      // 달력 헤더 스타일 수정을 위해, 달력 기본 헤더를 비활성
      headerToolbar={{
        left: '',
        center: '',
        right: '',
      }}
    />
  );
}

function parseMonth(year, month) {
  return `${year}-${month.toString().padStart(2, '0')}`;
}

function isEqualDay(date1, date2) {
  return (
    date1.getFullYear() === date2.getFullYear() &&
    date1.getMonth() === date2.getMonth() &&
    date1.getDate() === date2.getDate()
  );
}
