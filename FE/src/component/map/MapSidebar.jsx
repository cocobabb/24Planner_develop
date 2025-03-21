export default function MapSidebar() {
  const calendarSidebar = 'h-180 flex flex-col flex-1 m-4';
  const houseDelete = 'text-gray-500 text-opacity-70 underline cursor-pointer hover:text-primary';
  const adressStyle =
    'ml-4 text-gray-500 text-opacity-70 underline cursor-pointer hover:text-primary';
  const textareaStyle = "w-100 h-full border-2 rounded-2xl border-black p-8";

  return (
    <section className={calendarSidebar}>
      <div className="flex justify-end mb-8">
        <div className={houseDelete}>집 삭제</div>
      </div>
      <div className="ml-6 mb-10">
        <div className="flex items-center mb-2">
          <h2 className="text-2xl text-black font-bold">빨간 문</h2>
          <div className={adressStyle}>수정</div>
        </div>
        <div>
          <div className="text-xl mb-2">서울시 모라구 알았동</div>
          <div className="flex items-center">
            <div className="text-xl">12동 3456호</div>
            <div className={adressStyle}>수정</div>
          </div>
        </div>
      </div>
      <textarea className={textareaStyle}></textarea>
    </section>
  );
}
