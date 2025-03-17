export default function Header() {
  const headerItemStyle = 'flex items-center p-4';

  return (
    <div className="h-full">
      <div className="flex justify-between h-full p-2 text-2xl">
        {/* Linux 환경 Firefox에서 flex에 이미지가 들어가면 무조건 크기를 최대로 차지하는 문제가 있어 flex 크기 수동 지정*/}
        <div className="flex flex-4 font-bold">
          <div className={`${headerItemStyle} max-w-40`}>
            <img src="/logo.png" />
          </div>

          {/* 로그인 후 화면까지 구현한 시점에서 활성화 예정 */}
          {/*
          <div className={`${headerItemStyle} text-primary`}>체크리스트</div>
          <div className={`${headerItemStyle}`}>캘린더</div>
          */}
        </div>

        <div className="flex flex-6 justify-end">
          {/* 로그인 후 화면까지 구현한 시점에서 활성화 예정 */}
          {/*
          <div className={`${headerItemStyle} text-secondary`}>이사 플랜 1</div>
          <div className={`${headerItemStyle}`}>로그아웃</div>
          */}
        </div>
      </div>
    </div>
  );
}
