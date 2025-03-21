import { useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import MapModal from './MapModal';
import mapApi from '../../api/mapApi';
import { useParams } from 'react-router-dom';

export default function Map() {
  const { movingPlanId } = useParams();

  const [showModal, setShowModal] = useState(false);

  const [addressData, setAddressData] = useState({
    centerlatitude: null,
    centerlongitude: null,
  });

  const [maplists, setMapLists] = useState([]);

  const [selectedButton, setSelectedButton] = useState(null);

  const mapStyle = 'flex flex-col flex-2 h-full w-full border-r-1 border-gray-300 m-4';
  const mapPlusStyle = 'w-22 h-12 border-2 rounded-xl px-2 py-1 bg-primary text-2xl text-white me-2';
  const mapButtonStyle =
    'w-25 h-12 border-2 rounded-xl px-2 py-1 text-black hover:bg-white hover:text-primary mx-3';

  const handleCalendarModal = () => {
    setShowModal(() => true);
  };

  const container = useRef(null);

  const mapButton = (e) => {
    const { latitude, longitude } = e.target.dataset;

    setAddressData((prev) => ({
      ...prev,
      centerlatitude: latitude,
      centerlongitude: longitude,
    }));

    setSelectedButton(`${latitude},${longitude}`);
  };

  useEffect(() => {
    const { kakao } = window;

    const { centerlatitude, centerlongitude } = addressData;

    // 지도 생성
    let position = new kakao.maps.LatLng(33.450701, 126.570667);
    const options = {
      center: position,
      level: 3,
    };
    const map = new kakao.maps.Map(container.current, options);

    // 지도 중심지 설정
    if (centerlatitude == null || centerlongitude == null) {
      position = new kakao.maps.LatLng(33.450701, 126.570667);
      map.setCenter(position);
    } else {
      position = new kakao.maps.LatLng(centerlatitude, centerlongitude);
      map.setCenter(position);
    }

    async function fetchMapMarker() {
      let responses = await mapApi.maplist(movingPlanId);

      responses = responses.data.data.houses;
      setMapLists(responses);

      responses.map((response) => {
        const { latitude, longitude } = response;

        // 마커 생성
        const markerPosition = new kakao.maps.LatLng(latitude, longitude);
        const marker = new kakao.maps.Marker({
          position: markerPosition,
        });

        // 지도에 마커 추가
        marker.setMap(map);

        // 마커 클릭 이벤트 추가
        kakao.maps.event.addListener(marker, 'click', function () {
          alert("버튼을 클릭했습니다!");
          setAddressData({
            centerlatitude: latitude,
            centerlongitude: longitude,
          });
          setSelectedButton(`${latitude},${longitude}`);
        });
      });
    }
    fetchMapMarker();
  }, [addressData]);

  return (
    <>
      {showModal &&
        createPortal(
          <MapModal modalClose={() => setShowModal(false)} setAddressData={setAddressData} />,
          document.body,
        )}
      <section className={mapStyle}>
        <div className="flex">
          <button className={mapPlusStyle} onClick={handleCalendarModal}>
            +
          </button>
          <div className="mb-4 w-155 h-21 overflow-x-auto whitespace-nowrap">
            {maplists.map((maplist) => {
              const { latitude, longitude, nickname } = maplist;

              const isSelected = selectedButton === `${latitude},${longitude}`;

              return (
                <button
                  className={`${mapButtonStyle} ${isSelected ? 'bg-white text-primary' : ''}`}
                  id=""
                  data-latitude={latitude}
                  data-longitude={longitude}
                  onClick={mapButton}
                >
                  {nickname}
                </button>
              );
            })}
          </div>
        </div>
        <div style={{ width: '730px', height: '620px' }} ref={container}></div>
      </section>
    </>
  );
}
