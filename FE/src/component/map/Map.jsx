import { useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import MapModal from './MapModal';

export default function Map() {
  const [showModal, setShowModal] = useState(false);

  const mapStyle = 'flex flex-col flex-2 h-full w-full border-r-1 border-gray-300 m-4';
  const mapPlusStyle = 'w-25 h-12 border-2 rounded-xl px-2 py-1 bg-primary text-white me-2';
  const mapButtonStyle =
    'w-25 h-12 border-2 rounded-xl px-2 py-1 text-black hover:bg-white hover:text-primary mx-3';

  const handleCalendarModal = () => {
    setShowModal(() => true);
  };

  const container = useRef(null);

  useEffect(() => {
    const { kakao } = window;

    if (!container.current) return;

    // 지도 생성
    let position = new kakao.maps.LatLng(33.450701, 126.570667);
    const options = {
      center: position,
      level: 3,
    };
    const map = new kakao.maps.Map(container.current, options);

    // map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);

    // const response = await mapApi.maplist(movingPlanId);

    // responses = response.data;

    // setMap(responses)

    // responses.map((response) => {
    //   const { 위도, 경도 } = response;
    // });

    // 마커 생성
    const markerPosition = new kakao.maps.LatLng(33.450701, 126.570667);
    const marker = new kakao.maps.Marker({
      position: markerPosition,
    });

    // 지도에 마커 추가
    marker.setMap(map);

    // position = new kakao.maps.LatLng(33.45091, 126.57199);
    // map.setCenter(position);
  }, []);

  return (
    <>
      {showModal &&
        createPortal(<MapModal modalClose={() => setShowModal(false)} />, document.body)}
      <section className={mapStyle}>
        <div className='flex'>
          <button className={mapPlusStyle} onClick={handleCalendarModal}>
            +
          </button>
          <div className="mb-4 w-155 h-21 overflow-x-auto whitespace-nowrap">
            <button className={mapButtonStyle}>빨간 문</button>
            <button className={mapButtonStyle}>빨간 문</button>
            <button className={mapButtonStyle}>빨간 문</button>
            <button className={mapButtonStyle}>빨간 문</button>
            <button className={mapButtonStyle}>빨간 문</button>
            <button className={mapButtonStyle}>빨간 문</button>
            <button className={mapButtonStyle}>빨간 문</button>
          </div>
        </div>
        <div style={{ width: '730px', height: '620px' }}  ref={container}></div>
      </section>
    </>
  );
}
