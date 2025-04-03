import { createBrowserRouter } from 'react-router-dom';
import ProtectedLayout from '../layout/ProtectedLayout';
import RootLayout from '../layout/RootLayout';
import Home from '../pages/Home';
import Login from '../pages/Login';
import Signup from '../pages/Signup';
import TaskGroups from '../pages/TaskGroups';
import TaskList from '../pages/TaskList';
import PlanList from '../pages/PlanList';
import Houses from '../pages/Houses';
import Calendar from '../pages/Calendar';
import NotFound from '../pages/NotFound';
import PlanSetting from '../pages/PlanSetting';
import LoginRedirect from '../pages/LoginRedirect';
import SignupAdditionalInfo from '../pages/SignupAdditionalInfo';
import FindPassword from '../pages/FindPassword';
import NewPassword from '../pages/NewPassword';
import Mypage from '../pages/Mypage';
import Chat from '../pages/Chat';
import Invite from '../pages/Invite';

const router = createBrowserRouter([
  {
    path: '/',
    element: <RootLayout />,
    errorElement: <NotFound />,
    children: [
      {
        index: true,
        element: <Home />,
      },
    ],
  },
  {
    path: '/plans',
    element: (
      <ProtectedLayout>
        <RootLayout />
      </ProtectedLayout>
    ),
    errorElement: <NotFound />,
    children: [
      {
        index: true,
        element: <PlanList />,
      },
      {
        path: ':movingPlanId/setting',
        element: <PlanSetting />,
      },
      {
        path: ':movingPlanId',
        element: <TaskGroups />,
      },
      {
        path: ':movingPlanId/task/:taskGroupId',
        element: <TaskList />,
      },
      {
        path: ':movingPlanId/chat',
        element: <Chat />,
      },
      {
        path: ':movingPlanId/house',
        element: <Houses />,
      },
      {
        path: ':movingPlanId/calendar',
        element: <Calendar />,
      },
    ],
  },
  {
    path: '/login',
    element: <Login />,
    errorElement: <NotFound />,
  },
  {
    path: '/signup',
    element: <Signup />,
    errorElement: <NotFound />,
  },
  {
    path: '/login-redirect',
    element: <LoginRedirect />,
    errorElement: <NotFound />,
  },
  {
    path: '/signup/additional-info',
    element: <SignupAdditionalInfo />,
  },
  {
    path: '/password',
    element: <FindPassword />,
  },
  {
    path: '/newpassword',
    element: <NewPassword />,
  },
  {
    path: '/not-found',
    element: <NotFound />,
  },
  {
    path: '/mypage',
    element: (
      <ProtectedLayout>
        <RootLayout />
      </ProtectedLayout>
    ),
    children: [{ index: true, element: <Mypage /> }],
  },
  {
    path: '/invite',
    element: <Invite />,
    errorElement: <NotFound />,
  },
]);

export default router;
