import { Outlet } from 'react-router-dom';
import Header from '../component/Header';
import Footer from '../component/Footer';
import PopoverUtilComponent from '../component/calendar/popover/PopoverUtilComponent';

export default function RootLayout() {
  return (
    <>
      <div className="flex flex-col items-center h-screen min-h-210 min-w-320 font-roboto">
        <PopoverUtilComponent>
          <div className="min-h-28 min-w-320">
            <Header />
          </div>
          <div className="grow min-w-320">
            <Outlet />
          </div>
          <div className="min-h-18 min-w-320">
            <Footer />
          </div>
        </PopoverUtilComponent>
      </div>
    </>
  );
}
