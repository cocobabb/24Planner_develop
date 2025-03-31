const calendarUtil = {
  parseDateStr: (year, month, day) => {
    return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
  },

  parseDateStrFromObject: (date) => {
    return calendarUtil.parseDateStr(date.getFullYear(), date.getMonth() + 1, date.getDate());
  },

  parseIntFromDateStr: (date) => {
    return (
      Number.parseInt(date.substring(0, 4)) * 10000 +
      Number.parseInt(date.substring(5, 7)) * 100 +
      Number.parseInt(date.substring(8, 10))
    );
  },

  hexColorToIntArray: (hexColor) => {
    if (
      typeof hexColor !== 'string' ||
      !new RegExp(/#?([\da-fA-F]{2})([\da-fA-F]{2})([\da-fA-F]{2})/g).test(hexColor)
    ) {
      return null;
    }

    const upperCaseHexColor = hexColor.toUpperCase();
    const result = [];

    for (let i = 1; i < 7; i += 2) {
      result.push(
        calendarUtil.hexDigitToDecimal(upperCaseHexColor[i]) * 16 +
          calendarUtil.hexDigitToDecimal(upperCaseHexColor[i + 1]),
      );
    }

    return result;
  },

  hexDigitToDecimal: (hexDigit) => {
    return hexDigit.charCodeAt(0) - (hexDigit.charCodeAt(0) < 58 ? 48 : 55);
  },

  // 배경 색에 따라 글씨를 흑백으로 구분
  // https://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color
  determineBlackText: (colorIntArray) => {
    return colorIntArray[0] * 0.299 + colorIntArray[1] * 0.587 + colorIntArray[2] * 0.114 > 150;
  },

  scheduleToEvent: (schedule) => {
    return {
      scheduleId: schedule.id,
      title: schedule.content,
      start: schedule.startDate,
      // 달력에 일정을 출력하기 위해서는 종료일을 하루 뒤로 변경해야 함
      // 바로 +를 하면 문자열 연산이 일어나 오작동하므로, -1로 UNIX time으로 변경 뒤 연산 실행
      end: calendarUtil.parseDateStrFromObject(new Date(new Date(schedule.endDate) - 1 + 86400001)),
      // color가 아니라 backgroundColor와 borderColor를 각각 지정해야 일정 간 간격을 띄울 수 있음
      backgroundColor: schedule.color,
      borderColor: '#FFFFFF',
    };
  },
};

export default calendarUtil;
