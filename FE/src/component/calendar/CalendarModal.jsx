export default function CalendarModal({ modalClose }) {
  const handleButton = () => {
    alert('확인');
    modalClose();
  };

  const transparentBlackBackgroundStyle =
    'absolute flex top-0 left-0 z-2 w-full h-full min-w-320 min-h-180 bg-black/75';
  const sizeLimiterStyle =
    'flex flex-col justify-center items-center mx-auto my-auto w-full h-full max-w-320 max-h-180 bg-transparent';
  const modalBodyStyle =
    'flex flex-col justify-center items-center mx-auto my-auto w-3/4 h-3/4 bg-white rounded-3xl border-2 border-primary';
  const buttonStyle =
    'w-60 h-20 bg-white border-4 border-primary rounded-3xl text-primary text-4xl font-bold cursor-pointer hover:bg-primary hover:text-white';

  return (
    <div className={transparentBlackBackgroundStyle} onClick={modalClose}>
      <div className={sizeLimiterStyle}>
        <div
          className={modalBodyStyle}
          onClick={(e) => {
            e.stopPropagation();
          }}
        >
          Modal 내용
          <button className={buttonStyle} onClick={handleButton}>
            확인
          </button>
        </div>
      </div>
    </div>
  );
}
