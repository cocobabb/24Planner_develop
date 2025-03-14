import { createBrowserRouter } from "react-router-dom";
import RootLayout from "../layout/RootLayout";
import Home from "../pages/Home";
import Login from "../pages/Login";
import Signup from "../pages/Signup";
import TaskGroups from "../pages/TaskGroups";
import TaskList from "../pages/TaskList";
import PlanList from "../pages/PlanList";
import Houses from "../pages/Houses";
import Calendar from "../pages/Calendar";
import NotFound from "../pages/NotFound";

const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    errorElement: <NotFound />,
    children: [
      {
        index: true,
        element: <Home />,
      },
      {
        path: "/plans",
        element: <PlanList />,
      },
      {
        path: "/plans/:movingPlanId",
        element: <TaskGroups />,
      },
      {
        path: "/plans/:movingPlanId/task/:taskGroupId",
        element: <TaskList />,
      },
      {
        path: "/plans/:movingPlanId/house",
        element: <Houses />,
      },
      {
        path: "/plans/:movingPlanId/calendar",
        element: <Calendar />,
      },
    ],
  },
  {
    path: "/login",
    element: <Login />,
    errorElement: <NotFound />,
  },
  {
    path: "/signup",
    element: <Signup />,
    errorElement: <NotFound />,
  },
]);

export default router;
