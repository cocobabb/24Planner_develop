import { useState } from 'react';
import { createPortal } from 'react-dom';
import CalendarModal from './CalendarModal';

export default function CalendarContent() {
  const [showModal, setShowModal] = useState(false);

  const handleCalendarModal = () => {
    setShowModal(() => true);
  };

  const calendarContentStyle = 'flex flex-col flex-2 h-full w-full border-r-1 border-gray-300 m-4';

  return (
    <>
      {showModal &&
        createPortal(<CalendarModal modalClose={() => setShowModal(false)} />, document.body)}
      <section className={calendarContentStyle}>
        <div>CalendarContent</div>
        <div className="text-primary" onClick={handleCalendarModal}>
          Modal 버튼
        </div>
      </section>
    </>
  );
}
