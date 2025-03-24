import { useState } from 'react';
import Map from '../component/map/Map';
import MapSidebar from '../component/map/MapSidebar';

export default function Houses() {
  const mapMainStyle = 'flex justify-center h-full p-6';

  const [houseId, setHouseId] = useState('');

  const [maplists, setMapLists] = useState([]);

  const [addressData, setAddressData] = useState({
    centerlatitude: null,
    centerlongitude: null,
  });

  const [nickname, setNickName] = useState("");

  return (
    <main className={mapMainStyle}>
      <Map setHouseId={setHouseId} maplists={maplists} setMapLists={setMapLists} addressData={addressData} setAddressData={setAddressData} nickname={nickname} />
      <MapSidebar houseId={houseId} maplists={maplists} setMapLists={setMapLists} setAddressData={setAddressData} setNickName={setNickName}/>
    </main>
  );
}
