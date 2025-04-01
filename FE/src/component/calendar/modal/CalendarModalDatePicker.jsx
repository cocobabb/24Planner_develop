import { forwardRef } from 'react';

import DatePicker, { registerLocale } from 'react-datepicker';
import ko from 'date-fns/locale/ko';

import 'react-datepicker/dist/react-datepicker.css';
import '../style/calendarModalDatePicker.css';

export default function CalendarModalDatePicker({ startDate, setStartDate, endDate, setEndDate }) {
  // DatePicker에 요일이 한국어로 뜨도록 할 때 필요한 설정
  registerLocale('ko', ko);

  const datePickerHeaderStyle = 'flex justify-between items-center text-lg mb-1';
  const datePickerButtonDivStyle = 'flex-1';
  const datePickerButtonStyle = 'text-base';
  const datePickerDateStyle = 'flex flex-2 justify-center';

  const createCustomHeader = ({ date, decreaseMonth, increaseMonth }) => {
    return (
      <div className={datePickerHeaderStyle}>
        <div className={datePickerButtonDivStyle}>
          <button className={datePickerButtonStyle} onClick={decreaseMonth}>
            {'<'}
          </button>
        </div>
        <div className={datePickerDateStyle}>
          {date.getFullYear()}.{String(date.getMonth() + 1).padStart(2, '0')}
        </div>
        <div className={datePickerButtonDivStyle}>
          <button className={datePickerButtonStyle} onClick={increaseMonth}>
            {'>'}
          </button>
        </div>
      </div>
    );
  };

  const StartDateCustomInput = buttonForDatePicker();
  const EndDateCustomInput = buttonForDatePicker();

  const dateSelectWrapperStyle = 'flex w-full h-2/5 justify-between';
  const dateSelectStyle = 'flex flex-1 flex-col justify-evenly items-center';
  const dateSelectTitleStyle = 'text-2xl';
  const dateSelectContentStyle = 'text-lg';

  return (
    <div className={dateSelectWrapperStyle}>
      <div className={dateSelectStyle}>
        <div className={dateSelectTitleStyle}>시작일</div>
        <div className={dateSelectContentStyle}>
          <DatePicker
            selected={startDate}
            onChange={(date) => {
              setStartDate(date);
              if (endDate < date) {
                setEndDate(date);
              }
            }}
            customInput={<StartDateCustomInput />}
            locale="ko"
            renderCustomHeader={createCustomHeader}
          />
        </div>
      </div>
      <div className={dateSelectStyle}>
        <div className={dateSelectTitleStyle}>종료일</div>
        <div className={dateSelectContentStyle}>
          <DatePicker
            selected={endDate}
            onChange={(date) => {
              setEndDate(date);
              if (startDate > date) {
                setStartDate(date);
              }
            }}
            customInput={<EndDateCustomInput />}
            locale="ko"
            renderCustomHeader={createCustomHeader}
          />
        </div>
      </div>
    </div>
  );
}

function buttonForDatePicker() {
  return forwardRef(({ value, onClick, className }, ref) => (
    <button className={className} onClick={onClick} ref={ref}>
      {value.substring(6, 10)}년 {value.substring(0, 2)}월 {value.substring(3, 5)}일
    </button>
  ));
}
