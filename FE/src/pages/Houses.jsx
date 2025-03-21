import Map from '../component/map/Map';
import MapSidebar from '../component/map/MapSidebar';


export default function Houses() {
 const mapMainStyle = 'flex justify-center h-full p-6';

  return (
    <main className={mapMainStyle}>
      <Map />
      <MapSidebar />
    </main>
  );
}
