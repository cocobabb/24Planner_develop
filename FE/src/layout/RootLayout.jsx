import { Outlet } from 'react-router-dom';
import Header from '../component/Header';
import Footer from '../component/Footer';

export default function RootLayout() {
  return (
    <>
      <div className="flex flex-col items-center h-screen w-screen min-h-180 min-w-320 font-roboto">
        <div className="h-1/10 min-w-320">
          <Header></Header>
        </div>
        <div className="h-4/5 min-w-320">
          <Outlet></Outlet>
        </div>
        <div className="h-1/10 min-w-320">
          <Footer></Footer>
        </div>
      </div>
    </>
  );
}
