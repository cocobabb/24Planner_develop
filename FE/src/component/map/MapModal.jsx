import { useState } from 'react';
import { useParams } from 'react-router-dom';
import mapApi from '../../api/mapApi';

export default function MapModal({ modalClose, setAddressData, setSelectedButton }) {
  const { movingPlanId } = useParams();

  const [address, setAddress] = useState('');

  const [formData, setFormData] = useState({
    nickname: '',
    address1: '',
    address2: '',
  });

  const [inputRequestMessage, setInputRequestMessage] = useState({
    nickname: '',
    address1: '',
    address2: '',
  });

  const [isOpen, setIsOpen] = useState(false);

  const loadPostcodeScript = () => {
    return new Promise((resolve, reject) => {
      // 이미 스크립트가 로드된 경우 바로 resolve
      if (window.daum && window.daum.Postcode) {
        resolve();
        return;
      }

      const script = document.createElement('script');
      script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
      script.onload = () => resolve(); // 스크립트가 로드되면 resolve
      script.onerror = () => reject(new Error('Failed to load the postcode script.'));
      document.head.appendChild(script);
    });
  };

  const transparentBlackBackgroundStyle =
    'absolute flex top-0 left-0 z-2 w-full h-full min-w-320 min-h-180 bg-black/75';
  const sizeLimiterStyle =
    'flex flex-col justify-center items-center mx-auto my-auto w-full h-full max-w-320 max-h-180 bg-transparent';
  const modalBodyStyle =
    'flex flex-col justify-center items-center mx-auto my-auto w-200 h-3/4 bg-white rounded-3xl border-2 border-primary';

  const inputWrapperStyle = 'w-130 mt-4';
  const adressWrapperStyle = 'flex h-11';
  const inputStyle = 'w-full text-xl pl-3 focus:outline-none focus:placeholder-transparent mt-5';
  const lineStyle = 'mt-2';
  const buttonStyle =
    'block mt-10 mx-auto border-2 rounded-2xl px-9 py-2 text-2xl text-primary hover:bg-primary hover:text-white';
  const adressButtonStyle =
    'w-30 border-2 rounded-full px-2 py-1 text-primary hover:bg-primary hover:text-white';
  const inputRequestMessageStyle = 'text-red-400 mt-1';

  const handleSubmit = (e) => {
    e.preventDefault();
  };

  const handleChange = async (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const toggleHandler = async () => {
    await loadPostcodeScript();

    if (isOpen) return;
    setIsOpen(true);

    new window.daum.Postcode({
      oncomplete: (data) => {
        setAddress(data.address);
        setFormData((prev) => ({
          ...prev,
          address1: data.address,
        }));
      },
      onclose: () => {
        setIsOpen(false); // 사용자가 닫으면 상태 변경
      },
    }).open();
  };

  const createhouse = async () => {
    const errors = {};
    if (!formData.nickname) errors.nickname = '별명을 지어주세요.';
    if (!formData.address1) errors.address1 = '주소를 넣어주세요.';

    if (Object.keys(errors).length) {
      setInputRequestMessage(errors);
      return;
    }

    try {
      let response = await mapApi.mapCreate(movingPlanId, formData);
      response = response.data.data;

      const { latitude, longitude } = response;

      setAddressData((prev) => ({
        ...prev,
        centerlatitude: latitude,
        centerlongitude: longitude,
      }));

      setSelectedButton(`${latitude},${longitude}`);

      modalClose();
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className={transparentBlackBackgroundStyle} onClick={modalClose}>
      <div className={sizeLimiterStyle}>
        <div
          className={modalBodyStyle}
          onClick={(e) => {
            e.stopPropagation();
          }}
        >
          <form className="flex flex-col items-center" onSubmit={handleSubmit}>
            <div className={inputWrapperStyle}>
              <input
                type="text"
                name="nickname"
                id="nickname"
                placeholder="새 집 별칭"
                maxLength="5"
                onChange={handleChange}
                className={inputStyle}
              />
              <hr className={lineStyle} />
              <div className={inputRequestMessageStyle}>
                {inputRequestMessage.nickname || '\u00A0'}
              </div>
            </div>

            <div className={inputWrapperStyle}>
              <div className={adressWrapperStyle} onClick={toggleHandler}>
                <input
                  type="text"
                  name="address1"
                  id="address1"
                  value={address}
                  placeholder="주소"
                  disabled
                  className={inputStyle}
                />
                <button className={adressButtonStyle}>주소 검색</button>
              </div>
              <hr className={lineStyle} />
              <div className={inputRequestMessageStyle}>
                {inputRequestMessage.address1 || '\u00A0'}
              </div>
            </div>

            <div className={inputWrapperStyle}>
              <input
                type="text"
                name="address2"
                id="address2"
                placeholder="상세 주소"
                onChange={handleChange}
                className={inputStyle}
              />
              <hr className={lineStyle} />
            </div>
            <button className={buttonStyle} onClick={createhouse}>
              새 집 추가
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
