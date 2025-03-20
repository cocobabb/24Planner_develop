import CalendarContent from '../component/calendar/CalendarContent';
import CalendarSidebar from '../component/calendar/CalendarSidebar';

export default function Calendar() {
  const calendarMainStyle = 'flex justify-center h-full p-6';

  return (
    <main className={calendarMainStyle}>
      <CalendarContent />
      <CalendarSidebar />
    </main>
  );
}
