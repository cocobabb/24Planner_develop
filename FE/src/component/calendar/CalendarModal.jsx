import { useState } from 'react';
import { useParams } from 'react-router-dom';

import CalendarColorModal from './CalendarColorModal';
import CalendarModalDatePicker from './CalendarModalDatePicker';
import scheduleApi from '../../api/scheduleApi';

import calendarUtil from './util/calendarUtil';
import scheduleUtil from './util/scheduleUtil';
import LoadingCircle from './svg/LoadingCircle';

export default function CalendarModal({
  yearState,
  monthState,
  selectDate,
  dailyScheduleList,
  setDailyScheduleList,
  monthlyEventList,
  setMonthlyEventList,
  modalClose,
  showingScheduleToModal,
}) {
  const now = new Date();
  const selectedMonthDateObject = new Date(yearState, monthState - 1, 1);
  const isSelectedMonthIsNow =
    selectedMonthDateObject.getFullYear() === now.getFullYear() &&
    selectedMonthDateObject.getMonth() === now.getMonth();

  const [content, setContent] = useState(
    showingScheduleToModal ? showingScheduleToModal.content : '',
  );
  const [errorMessage, setErrorMessage] = useState(null);
  const [color, setColor] = useState(
    showingScheduleToModal ? showingScheduleToModal.color : '#69DB7C',
  );
  const [startDate, setStartDate] = useState(
    showingScheduleToModal
      ? new Date(showingScheduleToModal.startDate)
      : isSelectedMonthIsNow
        ? now
        : selectedMonthDateObject,
  );
  const [endDate, setEndDate] = useState(
    showingScheduleToModal
      ? new Date(showingScheduleToModal.endDate)
      : isSelectedMonthIsNow
        ? now
        : selectedMonthDateObject,
  );
  const [showColorDropdown, setShowColorDropdown] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const { movingPlanId } = useParams();

  const handleBackgroundClick = () => {
    if (showColorDropdown) {
      setShowColorDropdown(() => false);
    } else {
      modalClose();
    }
  };

  const handleModalBodyClick = (e) => {
    if (showColorDropdown) {
      setShowColorDropdown(() => false);
    }
    e.stopPropagation();
  };

  const handleFormSubmit = (e) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const handleContentChange = (e) => {
    setContent(() => e.target.value.substring(0, 20));
    setErrorMessage(() => null);
  };

  const handleClickColor = () => {
    setShowColorDropdown((prev) => !prev);
  };

  const handleDropdownClick = (e) => {
    e.stopPropagation();
  };

  const handleButton = async (e) => {
    e.preventDefault();
    e.stopPropagation();

    if (showColorDropdown) {
      setShowColorDropdown(() => false);
    } else {
      if (!content.length) {
        setErrorMessage(() => '내용은 필수 입력 항목입니다.');
      } else {
        setIsLoading(true);

        try {
          const inputSchedule = { ...showingScheduleToModal };
          inputSchedule.content = content;
          inputSchedule.startDate = calendarUtil.parseDateStrFromObject(startDate);
          inputSchedule.endDate = calendarUtil.parseDateStrFromObject(endDate);
          inputSchedule.color = color;
          const scheduleId = showingScheduleToModal ? showingScheduleToModal.id : -1;
          const response = showingScheduleToModal
            ? await scheduleApi.updateSchedule(movingPlanId, scheduleId, inputSchedule)
            : await scheduleApi.createSchedule(movingPlanId, inputSchedule);

          const returnedSchedule = response.data.data;
          const selectDateToInt = calendarUtil.parseIntFromDateStr(selectDate);
          const startDateToInt = calendarUtil.parseIntFromDateStr(returnedSchedule.startDate);
          const endDateToInt = calendarUtil.parseIntFromDateStr(returnedSchedule.endDate);
          const startDateOfSelectedMonthToInt = calendarUtil.parseIntFromDateStr(
            calendarUtil.parseDateStrFromObject(new Date(yearState, monthState - 1, 1)),
          );
          const endDateOfSelectedMonthToInt = calendarUtil.parseIntFromDateStr(
            calendarUtil.parseDateStrFromObject(endDateOfMonthObj(yearState, monthState)),
          );

          let newDailyScheduleList;

          if (showingScheduleToModal) {
            newDailyScheduleList = [];

            dailyScheduleList.forEach((schedule) => {
              if (schedule.id !== scheduleId) {
                newDailyScheduleList.push(schedule);
                return;
              } else if (startDateToInt <= selectDateToInt && endDateToInt >= selectDateToInt) {
                newDailyScheduleList.push(returnedSchedule);
              }
            });
          } else {
            newDailyScheduleList = [...dailyScheduleList];
            if (startDateToInt <= selectDateToInt && endDateToInt >= selectDateToInt) {
              newDailyScheduleList.push(returnedSchedule);
            }
          }

          setDailyScheduleList(() =>
            newDailyScheduleList.sort(scheduleUtil.scheduleCompareFunction),
          );

          if (showingScheduleToModal) {
            const newMonthlyEventList = [];
            monthlyEventList.forEach((event) => {
              if (event.scheduleId !== scheduleId) {
                newMonthlyEventList.push(event);
              } else if (
                startDateToInt <= endDateOfSelectedMonthToInt &&
                endDateToInt >= startDateOfSelectedMonthToInt
              ) {
                newMonthlyEventList.push(calendarUtil.scheduleToEvent(returnedSchedule));
              }
            });

            setMonthlyEventList(() => newMonthlyEventList);
          } else {
            if (
              startDateToInt <= endDateOfSelectedMonthToInt &&
              endDateToInt >= startDateOfSelectedMonthToInt
            ) {
              setMonthlyEventList((prev) => [
                ...prev,
                calendarUtil.scheduleToEvent(returnedSchedule),
              ]);
            }
          }

          modalClose();
        } catch (err) {
          setErrorMessage(() => '등록 도중 오류가 발생했습니다.');
          console.log(err);
        }

        setIsLoading(false);
      }
    }
  };

  const transparentBlackBackgroundStyle =
    'absolute flex top-0 left-0 z-2 size-full min-w-320 min-h-220 bg-black/75';
  const flexColStyle = 'flex flex-col justify-center items-center m-auto';
  const sizeLimiterStyle = flexColStyle + ' size-full max-w-320 max-h-220 bg-transparent';
  const modalBodyStyle = flexColStyle + ' size-2/3 bg-white rounded-3xl border-2 border-primary';
  const formStyle = 'flex flex-col justify-between items-center m-auto h-1/2 w-2/3';
  const inputLineStyle =
    'flex justify-between items-center w-full border-b-1 border-gray-500 text-xl p-1 m-3';
  const inputWrapperStyle = 'flex grow';
  const inputStyle = 'grow focus:outline-hidden';
  const circleStyle = `bg-[${color}] size-10 rounded-4xl`;
  const errorDivStyle = 'text-red-300';
  const buttonStyle = `flex justify-center items-center w-40 h-15 bg-white border-4 border-primary rounded-3xl text-primary text-xl font-bold cursor-pointer ${isLoading ? '' : 'hover:bg-primary hover:text-white'}`;
  const calendarModalDropdownStyle = 'relative group';
  const calendarModalDropdownBodyStyle = `absolute text-xl text-center top-11 space-y-4 -left-57 right-0 w-125 py-4 bg-white border-1 border-primary rounded-2xl shadow-sm z-8 ${showColorDropdown ? 'opacity-100 visible' : 'opacity-0 invisible'}`;

  return (
    <div className={transparentBlackBackgroundStyle} onClick={handleBackgroundClick}>
      <div className={sizeLimiterStyle}>
        <div className={modalBodyStyle} onClick={handleModalBodyClick}>
          <form className={formStyle} onSubmit={handleFormSubmit}>
            <div className={inputLineStyle}>
              <div className={inputWrapperStyle}>
                <input
                  type="text"
                  className={inputStyle}
                  placeholder="할 일 입력"
                  value={content}
                  onChange={handleContentChange}
                />
              </div>
              <div className={calendarModalDropdownStyle}>
                <div className={circleStyle} onClick={handleClickColor}>
                  <div className={calendarModalDropdownBodyStyle} onClick={handleDropdownClick}>
                    <CalendarColorModal color={color} setColor={setColor} />
                  </div>
                </div>
              </div>
            </div>
            <div className={errorDivStyle}>{errorMessage ? errorMessage : '\u00A0'}</div>
            <CalendarModalDatePicker
              startDate={startDate}
              setStartDate={setStartDate}
              endDate={endDate}
              setEndDate={setEndDate}
            />
            <div>
              <button className={buttonStyle} onClick={handleButton}>
                {isLoading ? (
                  <LoadingCircle />
                ) : (
                  `할 일 ${showingScheduleToModal ? '수정' : '추가'}하기`
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

function endDateOfMonthObj(year, month) {
  return new Date(new Date(year + (month === 12), month % 12, 1) - 86400000);
}
