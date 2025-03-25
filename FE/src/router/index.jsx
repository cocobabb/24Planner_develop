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

const router = createBrowserRouter([
  {
    path: '/',
    element: <RootLayout />,
    // errorElement: <NotFound />,
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
    // errorElement: <NotFound />,
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
    // errorElement: <NotFound />,
  },
  {
    path: '/signup',
    element: <Signup />,
    // errorElement: <NotFound />,
  },
]);

export default router;
