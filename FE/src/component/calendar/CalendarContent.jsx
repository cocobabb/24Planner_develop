import { useRef } from 'react';
import { useParams } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import koLocale from '@fullcalendar/core/locales/ko';

import scheduleApi from '../../api/scheduleApi';

import ChevronLeftSvg from './svg/ChevronLeftSvg';
import ChevronRightSvg from './svg/ChevronRightSvg';
import calendarUtil from './util/calendarUtil';

import { eventMouseHoverReducer, eventMouseLeaveReducer } from '../../store/slices/popoverSlice';

export default function CalendarContent({
  yearState,
  setYearState,
  monthState,
  setMonthState,
  setSelectDate,
  monthlyEventList,
  setMonthlyEventList,
  setIsShowingModal,
  setShowingScheduleToModal,
}) {
  const dispatch = useDispatch();

  const calendarRef = useRef(null);

  const { movingPlanId } = useParams();

  const moveToCurrentMonth = () => {
    const now = new Date();
    calendarRef.current.getApi().gotoDate(now);
    setSelectDate(calendarUtil.parseDateFromObject(now));
  };

  const moveToPrevMonth = () => {
    const targetYear = yearState - (monthState === 1);
    const targetMonth = ((monthState + 10) % 12) + 1;
    setYearState(targetYear);
    setMonthState(targetMonth);
    calendarRef.current.getApi().gotoDate(calendarUtil.parseDate(targetYear, targetMonth, 1));
  };

  const moveToNextMonth = () => {
    const targetYear = yearState + (monthState === 12);
    const targetMonth = (monthState % 12) + 1;
    setYearState(targetYear);
    setMonthState(targetMonth);
    calendarRef.current.getApi().gotoDate(calendarUtil.parseDate(targetYear, targetMonth, 1));
  };

  const handleDateCellClick = (e) => {
    setSelectDate(() => e.dateStr);
  };

  const handleEventMouseEnter = (e) => {
    dispatch(
      eventMouseHoverReducer({
        title: e.event.title,
        start: e.event.startStr,
        end: calendarUtil.parseDateFromObject(new Date(e.event.end - 86400000)),
        color: e.event.backgroundColor,
      }),
    );
  };

  const handleEventMouseLeave = () => {
    dispatch(eventMouseLeaveReducer());
  };

  const handleCalendarModal = () => {
    setShowingScheduleToModal(() => null);
    setIsShowingModal(() => true);
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
    }
  };

  const calendarContentStyle = 'flex flex-col flex-2 h-full w-full border-r-1 border-gray-300 my-4';
  const calendarPaddingStyle = 'px-8';
  const calendarHeaderStyle = 'flex justify-between w-full pt-4';
  const calendarHeaderLeftStyle = 'flex flex-1 justify-start';
  const calendarHeaderCenterStyle = 'flex flex-2 justify-center items-center';
  const calendarHeaderRightStyle = 'flex flex-1 justify-end';
  const calendarHeaderCenterPrevDivStyle = 'flex flex-1 justify-end';
  const calendarHeaderCenterDateDivStyle = 'flex flex-3 justify-center mx-5';
  const calendarHeaderCenterDateTextStyle = 'text-xl';
  const calendarHeaderCenterDivClickableStyle = 'cursor-pointer';
  const calendarHeaderCenterNextDivStyle = 'flex flex-1 justify-start';
  const buttonStyle =
    'w-20 h-10 bg-white border-2 border-primary rounded-xl text-primary font-semibold cursor-pointer hover:bg-primary hover:text-white ml-2';
  const eventLineStyle = 'h-3 px-1 text-tiny truncate';

  return (
    <>
      <section className={calendarContentStyle}>
        <div className={calendarPaddingStyle}>
          <div className={calendarHeaderStyle}>
            {/* 헤더 중앙 정렬용 태그 */}
            <nav className={calendarHeaderLeftStyle} />
            <nav className={calendarHeaderCenterStyle}>
              <div className={calendarHeaderCenterPrevDivStyle}>
                <div className={calendarHeaderCenterDivClickableStyle} onClick={moveToPrevMonth}>
                  <ChevronLeftSvg />
                </div>
              </div>
              <div className={calendarHeaderCenterDateDivStyle}>
                <h2 className={calendarHeaderCenterDateTextStyle}>
                  {yearState}년 {monthState}월
                </h2>
              </div>
              <div className={calendarHeaderCenterNextDivStyle} onClick={moveToNextMonth}>
                <div className={calendarHeaderCenterDivClickableStyle} onClick={moveToNextMonth}>
                  <ChevronRightSvg />
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
          <FullCalendar
            ref={calendarRef}
            plugins={[dayGridPlugin, interactionPlugin]}
            initialView="dayGridMonth"
            showNonCurrentDates={false}
            locale={koLocale}
            dayMaxEventRows={3}
            events={monthlyEventList}
            eventMouseEnter={handleEventMouseEnter}
            eventMouseLeave={handleEventMouseLeave}
            dateClick={handleDateCellClick}
            dayCellContent={(arg) => ({
              // 한국어 사용 시, 일수가 기본적으로 "n일" 형태로 표현되므로 직접 숫자만 출력
              html: `${arg.date.getDate()}`,
            })}
            // 더 보기 버튼에, 기본 제공되는 +3 개라는 텍스트 대신 +3만 사용
            moreLinkContent={(arg) => ({ html: arg.shortText })}
            eventContent={(arg) => {
              const color = arg.event.backgroundColor;
              return {
                html: `<div class="${eventLineStyle} bg-[${color}] ${calendarUtil.determineBlackText(calendarUtil.hexColorToIntArray(color)) ? 'text-black' : 'text-white'}">${arg.event.title}</div>`,
              };
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
        </div>
      </section>
    </>
  );
}

function parseMonth(year, month) {
  return `${year}-${month.toString().padStart(2, '0')}`;
}
