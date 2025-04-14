import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import scheduleApi from '../../../api/scheduleApi';

import calendarUtil from '../util/calendarUtil';
import scheduleUtil from '../util/scheduleUtil';
import LoadingCircle from '../svg/LoadingCircle';

export default function CalendarSidebar({
  yearState,
  monthState,
  selectDate,
  dailyScheduleList,
  setDailyScheduleList,
  setIsShowingModal,
  setShowingScheduleToModal,
}) {
  const [content, setContent] = useState('');
  const [errorMessage, setErrorMessage] = useState(null);

  const { movingPlanId } = useParams();

  const queryClient = useQueryClient();

  const {
    data: readData,
    isLoading,
    isError: isReadError,
    error: readError,
  } = useQuery({
    queryKey: [movingPlanId, selectDate],
    queryFn: async () => {
      // 일 단위로 스케줄 가져오기
      const response = await scheduleApi.getDailySchedule(movingPlanId, selectDate);
      return await response.data.data.schedules;
    },
    staleTime: 1000 * 3,
    retry: false,
  });

  if (isReadError) {
    setDailyScheduleList(() => []);
    console.log(readError);
  }

  useEffect(() => {
    if (!isLoading && readData) {
      setDailyScheduleList(() => readData.sort(scheduleUtil.scheduleCompareFunction));
    }
  }, [isLoading, readData]);

  useEffect(() => {
    setErrorMessage(() => null);
    setContent(() => '');
  }, [selectDate]);

  const {
    mutate: createMutate,
    error: createError,
    isError: isCreateError,
    isPending: isPendingForCreate,
  } = useMutation({
    mutationFn: async (newSchedule) => {
      const response = await scheduleApi.createSchedule(movingPlanId, newSchedule);
      return response.data.data;
    },
    onMutate: async (newSchedule) => {
      await queryClient.cancelQueries({ queryKey: [movingPlanId, selectDate] });
      const previousDailyScheduleList = queryClient.getQueryData([movingPlanId, selectDate]);
      const newDailyScheduleList = [...previousDailyScheduleList];

      newDailyScheduleList.push(newSchedule);
      newDailyScheduleList.sort(scheduleUtil.scheduleCompareFunction);

      queryClient.setQueryData([movingPlanId, selectDate], newDailyScheduleList);

      const selectedYear = Number.parseInt(selectDate.substring(0, 4));
      const selectedMonth = Number.parseInt(selectDate.substring(5, 7));

      await queryClient.cancelQueries({ queryKey: [movingPlanId, selectedYear, selectedMonth] });
      const previousMonthlyEventList = queryClient.getQueryData([
        movingPlanId,
        selectedYear,
        selectedMonth,
      ]);

      if (selectedYear === yearState && selectedMonth === monthState) {
        const newMonthlyEventList = [
          ...previousMonthlyEventList,
          calendarUtil.scheduleToEvent(newSchedule),
        ];
        queryClient.setQueryData([movingPlanId, selectedYear, selectedMonth], newMonthlyEventList);
      }

      return { previousDailyScheduleList, previousMonthlyEventList };
    },
    onSuccess: (data, newSchedule, context) => {
      queryClient.invalidateQueries({ queryKey: [movingPlanId, selectDate] });

      const selectedYear = Number.parseInt(selectDate.substring(0, 4));
      const selectedMonth = Number.parseInt(selectDate.substring(5, 7));
      if (selectedYear === yearState && selectedMonth === monthState) {
        queryClient.invalidateQueries({ queryKey: [movingPlanId, selectedYear, selectedMonth] });
      }
    },
    onError: (error, newSchedule, context) => {
      if (context) {
        queryClient.setQueryData([movingPlanId, selectDate], context.previousDailyScheduleList);

        const selectedYear = Number.parseInt(selectDate.substring(0, 4));
        const selectedMonth = Number.parseInt(selectDate.substring(5, 7));
        if (selectedYear === yearState && selectedMonth === monthState) {
          queryClient.setQueryData(
            [movingPlanId, selectedYear, selectedMonth],
            context.previousMonthlyEventList,
          );
        }
      }
    },
    retry: false,
  });

  if (isCreateError) {
    setErrorMessage(() => '등록 도중 오류가 발생했습니다.');
    console.log(createError);
  }

  const handleAddButton = (e) => {
    e.preventDefault();
    e.stopPropagation();
    addSchedule();
  };

  const handleEnterKeyDown = (e) => {
    if (e.key === 'Enter') {
      addSchedule();
    }
  };

  const addSchedule = async () => {
    if (!content.length) {
      setErrorMessage(() => '내용은 필수로 입력해야 합니다.');
    } else {
      createMutate({
        content: content,
        startDate: selectDate,
        endDate: selectDate,
        color: '#69DB7C',
      });
      setContent(() => '');
    }
  };

  const handleContentChange = (e) => {
    setContent(() => e.target.value.substring(0, 20));
    setErrorMessage(() => null);
  };

  const modalForUpdateSchedule = async (e, schedule) => {
    setShowingScheduleToModal(() => schedule);
    setIsShowingModal(() => true);
  };

  const {
    mutate: deleteMutate,
    error: deleteError,
    isError: isDeleteError,
  } = useMutation({
    mutationFn: async (scheduleId) => {
      const response = await scheduleApi.deleteSchedule(movingPlanId, scheduleId);
      return scheduleId;
    },
    onMutate: async (scheduleId) => {
      await queryClient.cancelQueries({ queryKey: [movingPlanId, selectDate] });
      const previousDailyScheduleList = queryClient.getQueryData([movingPlanId, selectDate]);
      const newDailyScheduleList = previousDailyScheduleList.filter(
        (schedule) => schedule.id !== scheduleId,
      );
      queryClient.setQueryData([movingPlanId, selectDate], newDailyScheduleList);

      const selectedYear = Number.parseInt(selectDate.substring(0, 4));
      const selectedMonth = Number.parseInt(selectDate.substring(5, 7));

      await queryClient.cancelQueries({ queryKey: [movingPlanId, selectedYear, selectedMonth] });
      const previousMonthlyEventList = queryClient.getQueryData([
        movingPlanId,
        selectedYear,
        selectedMonth,
      ]);
      const newMonthlyEventList = previousMonthlyEventList.filter(
        (event) => event.scheduleId !== scheduleId,
      );
      queryClient.setQueryData([movingPlanId, selectedYear, selectedMonth], newMonthlyEventList);

      return { previousDailyScheduleList, previousMonthlyEventList };
    },
    onSuccess: (data, scheduleId, context) => {
      queryClient.invalidateQueries({ queryKey: [movingPlanId, selectDate] });

      const selectedYear = Number.parseInt(selectDate.substring(0, 4));
      const selectedMonth = Number.parseInt(selectDate.substring(5, 7));
      queryClient.invalidateQueries({ queryKey: [movingPlanId, selectedYear, selectedMonth] });
    },
    onError: (error, scheduleId, context) => {
      if (context) {
        queryClient.setQueryData([movingPlanId, selectDate], context.previousDailyScheduleList);

        const selectedYear = Number.parseInt(selectDate.substring(0, 4));
        const selectedMonth = Number.parseInt(selectDate.substring(5, 7));
        queryClient.setQueryData(
          [movingPlanId, selectedYear, selectedMonth],
          context.previousMonthlyEventList,
        );
      }
    },
    retry: false,
  });

  if (isDeleteError) {
    console.log(deleteError);
  }

  const deleteSchedule = async (e, scheduleId) => {
    if (!confirm(`'${e.target.previousSibling.innerText}' 일정을 삭제합니다.\n계속하시겠습니까?`)) {
      return;
    }

    deleteMutate(scheduleId);
  };

  const scheduleElementDivStyle = 'flex items-center';
  const scheduleElementContentStyle =
    'flex justify-center items-center rounded-3xl w-5/6 p-2 m-2 cursor-pointer';
  const deleteButtonDivStyle = 'text-gray-500 text-opacity-70 cursor-pointer ml-5';

  const dailyScheduleListDiv = dailyScheduleList.map((schedule, i) => {
    return (
      <div key={i} className={scheduleElementDivStyle}>
        <div
          className={`${scheduleElementContentStyle} bg-[${schedule.color}] ${calendarUtil.determineBlackText(calendarUtil.hexColorToIntArray(schedule.color)) ? 'text-black' : 'text-white'}`}
          onClick={(e) => {
            modalForUpdateSchedule(e, schedule);
          }}
        >
          {schedule.content}
        </div>
        <div
          val={schedule.content}
          className={deleteButtonDivStyle}
          onClick={(e) => {
            deleteSchedule(e, schedule.id);
          }}
        >
          ✕
        </div>
      </div>
    );
  });

  const calendarSidebarStyle = 'flex flex-1 flex-col items-center m-4';
  const scheduleDateStyle = 'text-xl mt-12';
  const scheduleListStyle = 'flex flex-col w-full mt-8';
  const inputDivStyle =
    'flex justify-center items-center border-1 border-gray-300 rounded-3xl w-full py-2 pr-5 m-2 h-10';
  const inputStyle = 'focus:outline-none w-full p-4';
  const addButtonStyle = `flex justify-center items-center border-2 border-primary rounded-3xl w-15 h-10 ${isPendingForCreate ? 'cursor-progress' : 'bg-primary cursor-pointer'}`;
  const errorMessageStyle = 'px-4 mx-2 text-red-300';

  return (
    <section className={calendarSidebarStyle}>
      {selectDate ? (
        <>
          <div className={scheduleDateStyle}>
            {Number.parseInt(selectDate.substring(0, 4))}년{' '}
            {Number.parseInt(selectDate.substring(5, 7))}월{' '}
            {Number.parseInt(selectDate.substring(8, 10))}일
          </div>
          <div className={scheduleListStyle}>
            {dailyScheduleListDiv}
            <div className={scheduleElementDivStyle}>
              <div className={inputDivStyle}>
                <input
                  type="text"
                  className={inputStyle}
                  placeholder="할 일 입력"
                  value={content}
                  onChange={handleContentChange}
                  onKeyDown={handleEnterKeyDown}
                />
              </div>
              <div
                className={addButtonStyle}
                onClick={handleAddButton}
                disabled={isPendingForCreate}
              >
                {isPendingForCreate ? <LoadingCircle /> : '+'}
              </div>
            </div>
            <div className={errorMessageStyle}>{errorMessage ? errorMessage : '\u00A0'}</div>
          </div>
        </>
      ) : (
        <></>
      )}
    </section>
  );
}
